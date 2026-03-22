package com.finsight.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.data.local.entity.TransactionEntity
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
class HistoryViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            transactionRepository.getAllTransactions().catch { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            }.collect { transactions ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transactions = transactions,
                        filteredTransactions = applyFilter(
                            transactions,
                            it.selectedFilter,
                            it.searchQuery
                        )
                    )
                }
            }
        }
    }

    fun onFilterChange(filter: TransactionFilter) {
        _uiState.update {
            it.copy(
                selectedFilter = filter,
                filteredTransactions = applyFilter(
                    it.transactions,
                    filter,
                    it.searchQuery
                )
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredTransactions = applyFilter(
                    it.transactions,
                    it.selectedFilter,
                    query
                )
            )
        }
    }

    fun deleteTransaction(transactionEntity: TransactionEntity) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionEntity)
        }
    }

    private fun applyFilter(
        transactions: List<TransactionEntity>,
        filter: TransactionFilter,
        query: String
    ): List<TransactionEntity> {
        return transactions
            .filter { transaction ->
                when (filter) {
                    TransactionFilter.ALL -> true
                    TransactionFilter.INCOME -> transaction.type == "INCOME"
                    TransactionFilter.EXPENSE -> transaction.type == "EXPENSE"
                }
            }
            .filter { transaction ->
                if (query.isBlank()) true
                else transaction.title.contains(query, ignoreCase = true)
                        || transaction.category.contains(query, ignoreCase = true)
                        || transaction.note.contains(query, ignoreCase = true)
            }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}