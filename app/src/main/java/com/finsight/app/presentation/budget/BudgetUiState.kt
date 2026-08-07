package com.finsight.app.presentation.budget

import com.finsight.app.data.local.entity.BudgetEntity
import com.finsight.app.domain.model.CategoryTotal

data class BudgetUiState(
    val isLoading: Boolean = false,
    val isBudgetSave: Boolean = false,
    val budgets: List<BudgetEntity> = emptyList(),
    val budgetProgressList: List<BudgetProgress> = emptyList(),
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val errorMessage: String? = null,
    val categoryTotals: List<CategoryTotal> = emptyList()
)

data class BudgetProgress(
    val spent: Double,
    val budget: BudgetEntity,
    val progress: Float,
    val isOverBudget: Boolean = false
)
