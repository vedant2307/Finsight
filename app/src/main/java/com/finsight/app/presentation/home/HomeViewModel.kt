package com.finsight.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.data.local.entity.TransactionEntity
import com.finsight.app.data.repository.BudgetRepository
import com.finsight.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    // Private mutable state — only ViewModel can change this
    private val _uiState = MutableStateFlow(HomeUiState())

    // Public read-only state — UI can only observe this
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                transactionRepository.getAllTransactions(),
                transactionRepository.getTotalIncome(
                    startDate = getStartOfMonth(),
                    endDate = getEndOfMonth()
                ),
                transactionRepository.getTotalExpense(
                    startDate = getStartOfMonth(),
                    endDate = getEndOfMonth()
                )
            ) { transactions, income, expense ->
                HomeUiState(
                    isLoading = false,
                    recentTransactions = transactions.take(5),
                    totalIncome = income,
                    totalExpense = expense,
                    totalBalance = income - expense
                )
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { state ->
                    _uiState.update { state }
                }
        }
    }

    private fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(
            Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    fun deleteTransaction(transactionEntity: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionEntity)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}