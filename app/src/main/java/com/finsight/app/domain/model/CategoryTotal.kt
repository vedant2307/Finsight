package com.finsight.app.domain.model

data class CategoryTotal(
    val categoryName: String,
    val emoji: String,
    val totalAmount: Double,
    val percentage: Float
)
