package com.finsight.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun FinsightNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            // OnboardingScreen will go here
        }

        composable(Screen.Home.route) {
            // HomeScreen will go here
        }

        composable(Screen.AddTransaction.route) {
            // AddTransactionScreen will go here
        }

        composable(Screen.History.route) {
            // HistoryScreen will go here
        }

        composable(Screen.Budget.route) {
            // BudgetScreen will go here
        }

        composable(Screen.Settings.route) {
            // SettingsScreen will go here
        }
    }
}