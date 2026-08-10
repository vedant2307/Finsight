package com.finsight.app.presentation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction?transaction_id={transactionId}") {
        fun add() = "add_transaction?transaction_id=-1"
        fun edit(transactionId: Long) = "add_transaction?transaction_id=$transactionId"
    }
    object Budget : Screen("budget")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Insights : Screen("insights")
}