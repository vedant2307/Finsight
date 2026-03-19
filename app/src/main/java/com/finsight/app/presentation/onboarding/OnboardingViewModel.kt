package com.finsight.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finsight.app.di.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val currentStep: Int = 0,           // 0 = Welcome, 1 = Setup, 2 = Currency
    val name: String = "",
    val salary: String = "",
    val selectedCurrency: String = "₹",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())

    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // ── STEP NAVIGATION ───────────────────────────────────

    fun nextStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep < 2 && validateCurrentStep()) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep + 1)
        }
    }

    fun previousStep() {
        val currentStep = _uiState.value.currentStep
        if (currentStep > 0) {
            _uiState.value = _uiState.value.copy(currentStep = currentStep - 1)
        }
    }

    // ── USER INPUTS ───────────────────────────────────────

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name, errorMessage = null)
        }
    }

    fun onSalaryChange(salary: String) {
        val filtered = salary.filter { it.isDigit() || it == '.' }
        _uiState.update {
            it.copy(salary = filtered, errorMessage = null)
        }
    }

    fun onCurrencySelect(currency: String) {
        _uiState.update { it.copy(selectedCurrency = currency) }
    }

    // ── VALIDATION ────────────────────────────────────────

    private fun validateCurrentStep(): Boolean {
        val state = _uiState.value
        return when (state.currentStep) {
            0 -> true // Welcome screen — no validation needed
            1 -> {
                when {
                    state.name.isBlank() -> {
                        _uiState.update { it.copy(errorMessage = "Please enter your name") }
                        false
                    }

                    state.salary.isBlank() || state.salary.toDoubleOrNull() == null -> {
                        _uiState.update { it.copy(errorMessage = "Please enter a valid salary") }
                        false
                    }

                    state.salary.toDouble() <= 0 -> {
                        _uiState.update { it.copy(errorMessage = "Salary must be greater than 0") }
                        false
                    }

                    else -> true
                }
            }

            else -> true
        }
    }

    // ── COMPLETE ONBOARDING ───────────────────────────────

    fun completeOnBoarding() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                userPreferences.saveOnBoardingData(
                    userName = state.name,
                    monthlySalary = state.salary.toDouble(),
                    selectedCurrency = state.selectedCurrency
                )
                _uiState.update {
                    it.copy(
                        isComplete = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Something went wrong. Please try again.",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}