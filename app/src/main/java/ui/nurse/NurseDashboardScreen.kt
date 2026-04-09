package com.example.maternitymanagement.ui.nurse

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun NurseDashboardScreen(navController: NavHostController) {
    var nurseName by remember { mutableStateOf("Loading...") }
    var patientRegNumber by remember { mutableStateOf("") }
    var patientData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Load nurse profile
    LaunchedEffect(Unit) {
        scope.launch {
            val userId = FirebaseManager.auth.currentUser?.uid
            if (userId != null) {
                val snapshot = FirebaseManager.firestore.collection("profiles").document(userId).get().await()
                nurseName = snapshot.getString("full_name") ?: "Nurse"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // ✅ Top bar with logout
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Welcome $nurseName", style = MaterialTheme.typography.headlineMedium)
            Button(
                onClick = {
                    FirebaseManager.auth.signOut()
                    navController.navigate("role_selection") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = patientRegNumber,
            onValueChange = { patientRegNumber = it },
            label = { Text("Enter Patient Registration Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val snapshot = FirebaseManager.firestore.collection("profiles")
                            .whereEqualTo("registration_number", patientRegNumber.trim()) // ✅ trim input
                            .get().await()

                        if (!snapshot.isEmpty) {
                            patientData = snapshot.documents.first().data
                            message = ""
                        } else {
                            message = "Patient not found"
                            patientData = null
                        }
                    } catch (e: Exception) {
                        message = e.message ?: "Error fetching patient"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Patient")
        }

        Spacer(modifier = Modifier.height(24.dp))

        patientData?.let { data ->
            Text("Patient: ${data["full_name"]}")
            Text("Reg No: ${data["registration_number"]}")
            Text("Email: ${data["email"]}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("maternity_record/${patientRegNumber.trim()}") }, modifier = Modifier.fillMaxWidth()) {
                Text("View Maternity Records")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("appointment/${patientRegNumber.trim()}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Schedule Appointment")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("pregnancy_tracking/${patientRegNumber.trim()}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Pregnancy Tracking")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
