package com.example.maternitymanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maternitymanagement.ui.auth.MotherLoginScreen
import com.example.maternitymanagement.ui.auth.MotherRegisterScreen
import com.example.maternitymanagement.ui.auth.RoleSelectionScreen
import com.example.maternitymanagement.ui.auth.NurseLoginScreen
import com.example.maternitymanagement.ui.auth.NurseRegisterScreen
import com.example.maternitymanagement.ui.mother.MotherDashboardScreen
import com.example.maternitymanagement.ui.nurse.NurseDashboardScreen
import com.example.maternitymanagement.ui.nurse.MaternityRecordScreen
import com.example.maternitymanagement.ui.nurse.AppointmentScreen
import com.example.maternitymanagement.ui.nurse.PregnancyTrackingScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "role_selection") {

        // ==================== MOTHER FLOW ====================
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

        // ==================== NURSE FLOW ====================
        composable("nurse_register") {
            NurseRegisterScreen(navController = navController)
        }
        composable("nurse_login") {
            NurseLoginScreen(navController = navController)
        }
        composable("nurse_dashboard") {
            NurseDashboardScreen(navController = navController)
        }

        // Pass patient registration number as argument
        composable("maternity_record/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            MaternityRecordScreen(patientRegNumber, backStackEntry)
        }
        composable("appointment/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            AppointmentScreen(patientRegNumber, backStackEntry)
        }
        composable("pregnancy_tracking/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            PregnancyTrackingScreen(patientRegNumber, backStackEntry)
        }
    }
}
