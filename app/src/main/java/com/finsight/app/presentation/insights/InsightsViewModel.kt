package com.finsight.app.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.data.local.entity.TransactionEntity
import com.finsight.app.data.repository.CategoryRepository
import com.finsight.app.data.repository.TransactionRepository
import com.finsight.app.domain.model.CategoryTotal
import com.finsight.app.domain.model.MonthlySpend
import com.finsight.app.presentation.Utils.getCategoryEmoji
import com.finsight.app.presentation.budget.chartColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
        loadMonthlyTrend()
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

    private fun loadMonthlyTrend() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                val monthlySpend = buildMonthlySpend(transactions)
                _uiState.update {
                    it.copy(monthlySpends = monthlySpend)
                }
            }
        }
    }

    private fun buildMonthlySpend(transactions: List<TransactionEntity>): List<MonthlySpend> {
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

        return (5 downTo 0).map{ monthsAgo ->
            calendar.time = Date()
            calendar.add(Calendar.MONTH, -monthsAgo)

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val label = monthFormat.format(calendar.time)

            val total = transactions
                .filter { transaction ->
                    val txCal = Calendar.getInstance()
                    txCal.timeInMillis = transaction.date
                    transaction.type == "EXPENSE" &&
                            txCal.get(Calendar.YEAR) == year &&
                            txCal.get(Calendar.MONTH) == month
                }
                .sumOf { it.amount }

            MonthlySpend(month = label, amount = total)
        }
    }
}

data class InsightsUiState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalExpense: Double = 0.0,
    val monthlySpends: List<MonthlySpend> = emptyList()
)