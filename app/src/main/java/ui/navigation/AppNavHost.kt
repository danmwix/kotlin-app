package com.example.maternitymanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maternitymanagement.ui.auth.MotherLoginScreen
import com.example.maternitymanagement.ui.auth.MotherRegisterScreen
import com.example.maternitymanagement.ui.auth.RoleSelectionScreen
import com.example.maternitymanagement.ui.mother.MotherDashboardScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "role_selection"
    ) {
        composable("role_selection") {
            RoleSelectionScreen(navController = navController)
        }
        composable("mother_register") {
            MotherRegisterScreen(navController = navController)
        }
        composable("mother_login") {
            MotherLoginScreen(navController = navController)
        }
        composable("mother_dashboard") {
            MotherDashboardScreen(navController = navController)
        }
    }
}