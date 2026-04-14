package com.example.maternitymanagement.ui.pharmacist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Alignment

@Composable
fun PharmacistDashboardScreen(navController: NavHostController) {
    var pharmacistName by remember { mutableStateOf("Loading...") }
    var patientRegNumber by remember { mutableStateOf("") }
    var patientData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var message by remember { mutableStateOf("") }
    var prescription by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var savedPrescription by remember { mutableStateOf<Map<String, Any>?>(null) }
    var treatmentPlan by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val uid = FirebaseManager.auth.currentUser?.uid
            if (uid != null) {
                val snapshot = FirebaseManager.firestore.collection("profiles").document(uid).get().await()
                pharmacistName = snapshot.getString("full_name") ?: "Pharmacist"
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
                text = "Welcome Pharmacist $pharmacistName",
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
                    .height(42.dp)
                    .padding(start = 12.dp, end = 8.dp)
            ) {
                Text(
                    "Logout",
                    fontSize = 14.sp,
                    color = Color.White
                )
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

            OutlinedTextField(value = prescription, onValueChange = { prescription = it }, label = { Text("Medication Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("Dosage Instructions") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        val uid = data["id"] as? String
                        uid?.let {
                            FirebaseManager.firestore.collection("prescriptions").document(it).set(
                                mapOf("medication" to prescription, "dosage" to dosage)
                            ).await()
                            FirebaseManager.firestore.collection("profiles").document(it).update(
                                mapOf("prescription" to "$prescription - $dosage")
                            ).await()
                            message = "Prescription saved successfully!"
                        } ?: run { message = "Patient ID missing" }
                    } catch (e: Exception) {
                        message = e.message ?: "Error saving prescription"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Prescription")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        val uid = data["id"] as? String
                        uid?.let {
                            val doc = FirebaseManager.firestore.collection("prescriptions").document(it).get().await()
                            if (doc.exists()) {
                                savedPrescription = doc.data
                                message = ""
                            } else {
                                message = "No prescription found"
                            }
                        }
                    } catch (e: Exception) {
                        message = e.message ?: "Error fetching prescription"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("View Prescription")
            }

            savedPrescription?.let { pres ->
                Spacer(modifier = Modifier.height(12.dp))
                Text("Medication: ${pres["medication"]}")
                Text("Dosage: ${pres["dosage"]}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        val uid = data["id"] as? String
                        uid?.let {
                            val doc = FirebaseManager.firestore.collection("treatment_plans").document(it).get().await()
                            if (doc.exists()) {
                                // ✅ Correct field name
                                treatmentPlan = doc.getString("treatment_plan") ?: "No treatment plan found"
                                message = ""
                            } else {
                                message = "No treatment plan found"
                            }
                        }
                    } catch (e: Exception) {
                        message = e.message ?: "Error fetching treatment plan"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("View Treatment Plan")
            }

            treatmentPlan?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Treatment Plan: $it")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
