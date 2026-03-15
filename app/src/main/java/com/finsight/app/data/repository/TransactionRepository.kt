package com.finsight.app.data.repository

import com.finsight.app.data.local.dao.TransactionDao
import com.finsight.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    // Read operations

    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransaction()
    }

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactionByType(type)
    }

    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactionByCategory(category)
    }

    fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactionByDateRange(startDate, endDate)
    }

    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getTotalIncome(startDate, endDate)
    }

    fun getTotalExpense(startDate: Long, endDate: Long): Flow<Double> {
        return transactionDao.getTotalExpense(startDate, endDate)
    }

    fun getTotalExpenseByCategory(
        category: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        return transactionDao.getTotalExpenseByCategory(category, startDate, endDate)
    }

    fun searchTransactions(query: String): Flow<List<TransactionEntity>> {
        return transactionDao.searchTransactions(query)
    }

    // Write operations

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
}
