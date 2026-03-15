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
): ViewModel() {

    // Private mutable state — only ViewModel can change this
    private val _uiState = MutableStateFlow(HomeUiState())

    // Public read-only state — UI can only observe this
    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startOfMonth = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        val endOfMonth = calendar.timeInMillis

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                transactionRepository.getTransactionsByDateRange(startOfMonth, endOfMonth),
                transactionRepository.getTotalIncome(startOfMonth, endOfMonth),
                transactionRepository.getTotalExpense(startOfMonth, endOfMonth),
            ) { transactions, totalIncome, totalExpense ->
                HomeUiState(
                    isLoading = false,
                    recentTransactions = transactions.take(5),
                    totalBalance = totalIncome - totalExpense,
                    totalIncome = totalIncome,
                    totalExpense = totalExpense
                )
            }.catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }.collect { state ->
                _uiState.update { state }
            }
        }
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