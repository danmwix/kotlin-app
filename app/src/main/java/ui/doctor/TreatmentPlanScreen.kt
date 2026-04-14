package com.example.maternitymanagement.ui.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TreatmentPlanScreen(patientRegNumber: String) {
    var treatmentPlan by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf<String?>(null) }
    var patientName by remember { mutableStateOf("Loading...") }
    var savedData by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Load patient info
    LaunchedEffect(Unit) {
        scope.launch {
            val snapshot = FirebaseManager.firestore.collection("profiles")
                .whereEqualTo("registration_number", patientRegNumber.trim())
                .get().await()
            if (!snapshot.isEmpty) {
                val doc = snapshot.documents.first()
                userId = doc.getString("id")
                patientName = doc.getString("full_name") ?: patientRegNumber
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Treatment Plan for $patientName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = treatmentPlan, onValueChange = { treatmentPlan = it }, label = { Text("Enter Treatment Plan") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        FirebaseManager.firestore.collection("treatment_plans").document(it).set(
                            mapOf("treatment_plan" to treatmentPlan)
                        ).await()
                        message = "Treatment plan saved!"
                    } ?: run {
                        message = "Patient not found"
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error saving treatment plan"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Treatment Plan")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        val doc = FirebaseManager.firestore.collection("treatment_plans").document(it).get().await()
                        if (doc.exists()) {
                            savedData = doc.getString("treatment_plan")
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

        savedData?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Treatment Plan: $it")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
