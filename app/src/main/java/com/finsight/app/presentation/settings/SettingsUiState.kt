package com.finsight.app.presentation.settings

data class SettingsUiState(
    val name: String = "",
    val salary: Double = 0.0,
    val currency: String = "₹",
    val isLoading: Boolean = false
)
