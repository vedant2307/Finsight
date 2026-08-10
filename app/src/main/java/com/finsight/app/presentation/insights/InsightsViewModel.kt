package com.finsight.app.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.data.repository.CategoryRepository
import com.finsight.app.data.repository.TransactionRepository
import com.finsight.app.domain.model.CategoryTotal
import com.finsight.app.presentation.Utils.getCategoryEmoji
import com.finsight.app.presentation.budget.chartColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                val expenseTransactions = transactions.filter { it.type == "EXPENSE" }
                val totalExpense = expenseTransactions.sumOf { it.amount }

                val categoryTotalList: List<CategoryTotal> = if (totalExpense == 0.0) {
                    emptyList()
                } else {
                    expenseTransactions.groupBy {
                        it.category
                    }.toList().mapIndexed { index, (categoryName, transactionsInCategory) ->
                        val categoryTotal = transactionsInCategory.sumOf { it.amount }
                        CategoryTotal(
                            categoryName = categoryName,
                            emoji = getCategoryEmoji(categoryName),
                            totalAmount = categoryTotal,
                            percentage = ((categoryTotal / totalExpense) * 100).toFloat(),
                            color = chartColors[index % chartColors.size]
                        )
                    }.sortedByDescending { it.totalAmount }
                }

                _uiState.update {
                    it.copy(
                        categoryTotals = categoryTotalList,
                        totalExpense = totalExpense
                    )
                }
            }
        }
    }
}

data class InsightsUiState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalExpense: Double = 0.0
)