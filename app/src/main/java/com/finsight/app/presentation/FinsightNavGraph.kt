package com.finsight.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.finsight.app.presentation.addTransaction.AddTransactionScreen
import com.finsight.app.presentation.budget.BudgetScreen
import com.finsight.app.presentation.history.HistoryScreen
import com.finsight.app.presentation.home.HomeScreen
import com.finsight.app.presentation.onboarding.OnboardingScreen
import com.finsight.app.presentation.settings.SettingsScreen

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
                    navController.navigate(Screen.AddTransaction.add())
                },
                onEditTransaction = { transactionId ->
                    navController.navigate(Screen.AddTransaction.edit(transactionId))
                }
            )
        }

        composable(
            Screen.AddTransaction.route,
            arguments = listOf(
                // This tells Nav Compose "expect a Long here called transactionId, default to -1 if it's missing."
                // That argument now lands automatically in the ViewModel's SavedStateHandle
                navArgument("transactionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onEditTransaction = { transactionId ->
                    navController.navigate(Screen.AddTransaction.edit(transactionId))
                }
            )
        }

        composable(Screen.Budget.route) {
            BudgetScreen()
        }

        composable(Screen.Settings.route) {
            // SettingsScreen will go here
            SettingsScreen()
        }
    }
}