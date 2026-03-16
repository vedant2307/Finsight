package com.finsight.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finsight.app.presentation.BottomNavBar
import com.finsight.app.presentation.FinsightNavGraph
import com.finsight.app.presentation.Screen
import com.finsight.app.ui.theme.FinsightTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinsightTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

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
                        startDestination = Screen.Onboarding.route,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}
