package com.finsight.app.presentation.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.data.local.entity.BudgetEntity
import com.finsight.app.data.repository.BudgetRepository
import com.finsight.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())

    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    // Current month and year
    private val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    // Start and end of current month as timestamps
    private val startOfMonth: Long
    private val endOfMonth: Long

    init {
        // Calculate month timestamps once
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        startOfMonth = calendar.timeInMillis

        calendar.set(
            Calendar.DAY_OF_MONTH,
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        endOfMonth = calendar.timeInMillis

        loadBudgets()
    }

    private fun loadBudgets() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            budgetRepository.getAllBudgetsByMonthAndYear(currentMonth, currentYear)
                .catch { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }.collect { budgets ->
                    // For each budget — calculate how much is spent
                    calculateBudgetProgress(budgets)
                }
        }
    }

    private fun calculateBudgetProgress(budgets: List<BudgetEntity>) {
        viewModelScope.launch {
            val budgetProgressList = mutableListOf<BudgetProgress>()
            budgets.forEach { budget ->
                transactionRepository.getTotalExpenseByCategory(
                    budget.category,
                    startOfMonth,
                    endOfMonth
                ).catch {

                }.collect { spent ->
                    val progress = if (budget.amount > 0) {
                        (spent / budget.amount).toFloat()
                    } else {
                        0f
                    }

                    budgetProgressList.add(
                        BudgetProgress(
                            spent = spent,
                            budget = budget,
                            progress = progress.coerceIn(0f, 1f),
                            isOverBudget = spent > budget.amount
                        )
                    )
                }
            }

            val totalBudget = budgets.sumOf { it.amount }
            val totalSpent = budgetProgressList.sumOf { it.spent }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    budgets = budgets,
                    budgetProgressList = budgetProgressList,
                    totalBudget = totalBudget,
                    totalSpent = totalSpent
                )
            }
        }
    }

    fun addBudget(category: String, amount: Double) {
        if (category.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please select a category") }
            return
        }
        if (amount <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount") }
            return
        }

        viewModelScope.launch {
            try {
                budgetRepository.insertBudget(
                    BudgetEntity(
                        category = category,
                        amount = amount,
                        month = currentMonth,
                        year = currentYear
                    )
                )
                _uiState.update { it.copy(isBudgetSave = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to add budget. Please try again.")
                }
            }

        }
    }

    fun deleteBudget(budgetEntity: BudgetEntity) {
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudget(budgetEntity)
            } catch(e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete budget. Please try again.")
                }
            }
        }
    }

    fun resetBudgetSave() {
        _uiState.update { it.copy(isBudgetSave = false) }
    }

    fun onClearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }


}