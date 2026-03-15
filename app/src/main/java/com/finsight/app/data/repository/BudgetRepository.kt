package com.finsight.app.data.repository

import com.finsight.app.data.local.dao.BudgetDao
import com.finsight.app.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    // Read operations
    fun getAllBudgetsByMonthAndYear(month: Int, year: Int): Flow<List<BudgetEntity>> {
        return budgetDao.getAllBudgetsByMonthAndYear(month, year)
    }

    fun getBudgetByCategory(
        category: String,
        month: Int,
        year: Int
    ): Flow<BudgetEntity?> {
        return budgetDao.getBudgetByCategory(category, month, year)
    }

    // Write operations

    suspend fun insertBudget(budget: BudgetEntity) {
        budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }
}