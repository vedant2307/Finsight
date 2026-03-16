package com.finsight.app.presentation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object Budget : Screen("budget")
    object History : Screen("history")
    object Settings : Screen("settings")
}