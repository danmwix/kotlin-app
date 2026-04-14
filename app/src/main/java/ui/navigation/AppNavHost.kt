package com.example.maternitymanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maternitymanagement.ui.auth.*
import com.example.maternitymanagement.ui.mother.MotherDashboardScreen
import com.example.maternitymanagement.ui.nurse.NurseDashboardScreen
import com.example.maternitymanagement.ui.nurse.MaternityRecordScreen
import com.example.maternitymanagement.ui.nurse.AppointmentScreen
import com.example.maternitymanagement.ui.nurse.PregnancyTrackingScreen
import com.example.maternitymanagement.ui.doctor.DoctorDashboardScreen
import com.example.maternitymanagement.ui.doctor.PhysicalExamScreen
import com.example.maternitymanagement.ui.doctor.ReturnDateScreen
import com.example.maternitymanagement.ui.doctor.TreatmentPlanScreen
import com.example.maternitymanagement.ui.pharmacist.PharmacistDashboardScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "role_selection") {

        // ==================== ROLE SELECTION ====================
        composable("role_selection") {
            RoleSelectionScreen(navController = navController)
        }

        // ==================== MOTHER FLOW ====================
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

        composable("maternity_record/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            MaternityRecordScreen(patientRegNumber)
        }
        composable("appointment/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            AppointmentScreen(patientRegNumber)
        }
        composable("pregnancy_tracking/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            PregnancyTrackingScreen(patientRegNumber)
        }

        // ==================== DOCTOR FLOW ====================
        composable("doctor_register") {
            DoctorRegisterScreen(navController = navController)
        }
        composable("doctor_login") {
            DoctorLoginScreen(navController = navController)
        }
        composable("doctor_dashboard") {
            DoctorDashboardScreen(navController = navController)
        }

        composable("physical_exam/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            PhysicalExamScreen(patientRegNumber)
        }
        composable("return_date/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            ReturnDateScreen(patientRegNumber)
        }
        composable("treatment_plan/{patientRegNumber}") { backStackEntry ->
            val patientRegNumber = backStackEntry.arguments?.getString("patientRegNumber") ?: ""
            TreatmentPlanScreen(patientRegNumber)
        }

        // ==================== PHARMACIST FLOW ====================
        composable("pharmacist_register") {
            PharmacistRegisterScreen(navController = navController)
        }
        composable("pharmacist_login") {
            PharmacistLoginScreen(navController = navController)
        }
        composable("pharmacist_dashboard") {
            PharmacistDashboardScreen(navController = navController)
        }
    }
}
