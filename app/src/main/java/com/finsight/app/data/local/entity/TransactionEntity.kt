package com.finsight.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val title: String,
    val amount: Double,
    val type: String,
    val date: Long,
    val category: String,
    val note: String
)
