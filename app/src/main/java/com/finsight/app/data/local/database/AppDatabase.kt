package com.finsight.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finsight.app.data.local.dao.BudgetDao
import com.finsight.app.data.local.dao.CategoryDao
import com.finsight.app.data.local.dao.TransactionDao
import com.finsight.app.data.local.entity.BudgetEntity
import com.finsight.app.data.local.entity.CategoryEntity
import com.finsight.app.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
}
