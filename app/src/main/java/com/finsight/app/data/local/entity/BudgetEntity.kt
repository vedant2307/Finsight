package com.finsight.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Year

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val category: String,
    val amount: Double,
    val month: Int,
    val year: Int
)
