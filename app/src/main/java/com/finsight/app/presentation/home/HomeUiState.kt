package com.finsight.app.presentation.home

import com.finsight.app.data.local.entity.TransactionEntity

data class HomeUiState(
    val isLoading: Boolean = false,
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val budgetProgress: Float = 0f,
    val error: String? = null
)