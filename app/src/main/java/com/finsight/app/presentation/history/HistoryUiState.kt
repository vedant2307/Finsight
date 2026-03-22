package com.finsight.app.presentation.history

import com.finsight.app.data.local.entity.TransactionEntity

data class HistoryUiState(
    val isLoading: Boolean = false,
    val transactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val searchQuery: String = "",
    val errorMessage: String? = null
)

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}