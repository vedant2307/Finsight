package com.finsight.app.data.repository

import com.finsight.app.data.local.dao.CategoryDao
import com.finsight.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    // Read operations

    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getCategoryByName(name: String): CategoryEntity? {
        return categoryDao.getCategoryByName(name)
    }

    // Write operations

    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    /**
     * When the app is installed for the first time, the categories table is empty.
     * This function pre-populates it with all default categories — Food, Transport, Shopping etc.
     * We'll call this once during onboarding or app first launch.
     */
    suspend fun insertDefaultCategories() {
        val defaultCategories = listOf(
            CategoryEntity(name = "Food",          emoji = "\uD83C\uDF54", color = "#FEF3C7"),
            CategoryEntity(name = "Transport",     emoji = "\uD83D\uDE97", color = "#DBEAFE"),
            CategoryEntity(name = "Shopping",      emoji = "\uD83D\uDECD\uFE0F", color = "#FCE7F3"),
            CategoryEntity(name = "Groceries",     emoji = "\uD83D\uDED2", color = "#D1FAE5"),
            CategoryEntity(name = "Bills",         emoji = "\uD83D\uDCA1", color = "#FFF7ED"),
            CategoryEntity(name = "Health",        emoji = "\uD83D\uDC8A", color = "#FFE4E6"),
            CategoryEntity(name = "Rent",          emoji = "\uD83C\uDFE0", color = "#EDE9FE"),
            CategoryEntity(name = "Entertainment", emoji = "\uD83C\uDFAC", color = "#F0F9FF"),
            CategoryEntity(name = "Travel",        emoji = "\u2708\uFE0F",  color = "#ECFDF5"),
            CategoryEntity(name = "Education",     emoji = "\uD83D\uDCDA", color = "#FEF9C3"),
            CategoryEntity(name = "Salary",        emoji = "\uD83D\uDCB0", color = "#D1FAE5"),
            CategoryEntity(name = "Freelance",     emoji = "\uD83D\uDCBB", color = "#D1FAE5"),
            CategoryEntity(name = "Investment",    emoji = "\uD83D\uDCC8", color = "#D1FAE5"),
            CategoryEntity(name = "Other",         emoji = "\uD83D\uDCE6", color = "#F1F5F9"),
        )
        categoryDao.insertCategories(defaultCategories)
    }
}