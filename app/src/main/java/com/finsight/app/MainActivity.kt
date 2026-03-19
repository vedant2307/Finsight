package com.finsight.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finsight.app.di.UserPreferences
import com.finsight.app.presentation.BottomNavBar
import com.finsight.app.presentation.FinsightNavGraph
import com.finsight.app.presentation.Screen
import com.finsight.app.ui.theme.FinsightTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinsightTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isOnboardingComplete by userPreferences.isOnBoardingComplete.collectAsState(initial = false)

                val startDestination = if (isOnboardingComplete) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }

                val hideBottomNav = currentRoute == Screen.Onboarding.route ||
                        currentRoute == Screen.AddTransaction.route

                Scaffold(
                    bottomBar = {
                        if (!hideBottomNav) {
                            BottomNavBar(navController)
                        }
                    }
                ) { paddingValues ->
                    FinsightNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
