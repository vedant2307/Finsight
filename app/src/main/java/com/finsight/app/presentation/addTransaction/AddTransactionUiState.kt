package com.finsight.app.presentation.addTransaction

import com.finsight.app.data.local.entity.CategoryEntity

data class AddTransactionUiState(
    val isLoading: Boolean = false,
    val isSave: Boolean = false,
    val errorMessage: String? = null,
    val amount: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: CategoryEntity? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val title: String = "",
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
