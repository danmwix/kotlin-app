package com.example.maternitymanagement.ui.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun DoctorDashboardScreen(navController: NavHostController) {
    var doctorName by remember { mutableStateOf("Loading...") }
    var patientRegNumber by remember { mutableStateOf("") }
    var patientData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val userId = FirebaseManager.auth.currentUser?.uid
            if (userId != null) {
                val snapshot = FirebaseManager.firestore.collection("profiles").document(userId).get().await()
                doctorName = snapshot.getString("full_name") ?: "Doctor"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        // Top bar with logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome Doctor $doctorName",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 22.sp
            )

            Button(
                onClick = {
                    FirebaseManager.auth.signOut()
                    navController.navigate("role_selection") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .height(40.dp)
                    .padding(start = 8.dp)
            ) {
                Text(
                    "Logout",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }

        OutlinedTextField(
            value = patientRegNumber,
            onValueChange = { patientRegNumber = it },
            label = { Text("Enter Patient Registration Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    val snapshot = FirebaseManager.firestore.collection("profiles")
                        .whereEqualTo("registration_number", patientRegNumber.trim())
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
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Search Patient")
        }

        Spacer(modifier = Modifier.height(24.dp))

        patientData?.let { data ->
            Text("Patient: ${data["full_name"]}")
            Text("Reg No: ${data["registration_number"]}")
            Text("Email: ${data["email"]}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("physical_exam/${patientRegNumber}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Enter Physical Exam Findings")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("return_date/${patientRegNumber}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Schedule Return Date")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { navController.navigate("treatment_plan/${patientRegNumber}") }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Treatment Plan")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
