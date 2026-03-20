package com.finsight.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.finsight.app.presentation.addTransaction.AddTransactionScreen
import com.finsight.app.presentation.home.HomeScreen
import com.finsight.app.presentation.onboarding.OnboardingScreen

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
            OnboardingScreen(
                onBoardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                }
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
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