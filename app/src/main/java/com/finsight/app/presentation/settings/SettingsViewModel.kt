package com.finsight.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            combine(
                userPreferences.userName,
                userPreferences.monthlySalary,
                userPreferences.selectedCurrency
            ) { name, salary, currency ->
                SettingsUiState(
                    name = name,
                    salary = salary,
                    currency = currency,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { state }
            }

        }
    }

    fun updateName(name: String) {
        viewModelScope.launch {
            userPreferences.saveName(name)
            _uiState.update {
                it.copy(name = name)
            }
        }
    }

    fun updateSalary(salary: Double) {
        viewModelScope.launch {
            userPreferences.saveSalary(salary)
            _uiState.update {
                it.copy(salary = salary)
            }
        }
    }

}