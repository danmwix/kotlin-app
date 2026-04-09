package com.example.maternitymanagement.ui.nurse

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
fun AppointmentScreen(patientRegNumber: String, navEntry: NavBackStackEntry) {
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("Loading...") }
    var userId by remember { mutableStateOf<String?>(null) }

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
            } else {
                patientName = "Unknown"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Schedule Appointment for $patientName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time (HH:MM)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        // Save appointment under UID
                        FirebaseManager.firestore.collection("appointments").document(it).set(
                            mapOf(
                                "appointment_date" to date,
                                "appointment_time" to time,
                                "reason" to reason
                            )
                        ).await()

                        // Update return date in patient profile
                        FirebaseManager.firestore.collection("profiles").document(it).update(
                            mapOf("return_date" to date)
                        ).await()

                        message = "Appointment scheduled successfully!"
                    } ?: run {
                        message = "Patient not found"
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error scheduling appointment"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Appointment")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
