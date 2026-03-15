package com.finsight.app.presentation.addTransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.data.local.entity.CategoryEntity
import com.finsight.app.data.local.entity.TransactionEntity
import com.finsight.app.data.repository.CategoryRepository
import com.finsight.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())

    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepository.getAllCategories()
                .catch { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
                .collect { categories ->
                    if (categories.isEmpty()) {
                        categoryRepository.insertDefaultCategories()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                categories = categories
                            )
                        }
                    }
                }
        }
    }

    fun onAmountChanged(amount: String) {
        val filteredAmount = amount.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(
                amount = filteredAmount
            )
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update {
            it.copy(
                title = title
            )
        }
    }

    fun onTypeChanged(type: TransactionType) {
        _uiState.update {
            it.copy(
                selectedType = type
            )
        }
    }

    fun onCategorySelect(category: CategoryEntity) {
        _uiState.update {
            it.copy(
                selectedCategory = category
            )
        }
    }

    fun onNoteChanged(note: String) {
        _uiState.update {
            it.copy(
                note = note
            )
        }
    }

    fun onDateChanged(date: Long) {
        _uiState.update {
            it.copy(
                date = date
            )
        }
    }

    fun onSaveTransaction() {
        val state = _uiState.value

        if (!validateInput(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val transaction = TransactionEntity(
                    title = state.title,
                    amount = state.amount.toDouble(),
                    type = state.selectedType.name,
                    date = state.date,
                    category = state.selectedCategory!!.name,
                    note = state.note
                )

                transactionRepository.insertTransaction(transaction)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to save transaction. Please try again."
                    )
                }
            }
        }
    }

    private fun validateInput(state: AddTransactionUiState): Boolean {
        return when {
            state.amount.isBlank() || state.amount.toDoubleOrNull() == null -> {
                _uiState.update { it.copy(errorMessage = "Please enter a valid amount") }
                false
            }

            state.amount.toDouble() <= 0 -> {
                _uiState.update { it.copy(errorMessage = "Amount must be greater than 0") }
                false
            }

            state.title.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please enter a title") }
                false
            }

            state.selectedCategory == null -> {
                _uiState.update { it.copy(errorMessage = "Please select a category") }
                false
            }

            else -> true
        }
    }

    fun resetSaved() {
        _uiState.update { it.copy(isSaved = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}