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
fun MaternityRecordScreen(patientRegNumber: String, navEntry: NavBackStackEntry) {
    var weight by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var respiratoryRate by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("Loading...") }
    var userId by remember { mutableStateOf<String?>(null) }
    var savedData by remember { mutableStateOf<Map<String, Any>?>(null) }

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
        Text("Maternity Records for $patientName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = bloodPressure, onValueChange = { bloodPressure = it }, label = { Text("Blood Pressure") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = temperature, onValueChange = { temperature = it }, label = { Text("Temperature (°C)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = respiratoryRate, onValueChange = { respiratoryRate = it }, label = { Text("Respiratory Rate") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        FirebaseManager.firestore.collection("maternity_records").document(it).set(
                            mapOf(
                                "weight" to weight,
                                "blood_pressure" to bloodPressure,
                                "temperature" to temperature,
                                "height" to height,
                                "respiratory_rate" to respiratoryRate
                            )
                        ).await()
                        message = "Record saved successfully!"
                    } ?: run {
                        message = "Patient not found"
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error saving record"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        val doc = FirebaseManager.firestore.collection("maternity_records").document(it).get().await()
                        if (doc.exists()) {
                            savedData = doc.data
                            message = ""
                        } else {
                            message = "No records found"
                        }
                    } ?: run {
                        message = "Patient not found"
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error fetching record"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("View Records")
        }

        savedData?.let { data ->
            Spacer(modifier = Modifier.height(12.dp))
            Text("Weight: ${data["weight"]}")
            Text("Blood Pressure: ${data["blood_pressure"]}")
            Text("Temperature: ${data["temperature"]}")
            Text("Height: ${data["height"]}")
            Text("Respiratory Rate: ${data["respiratory_rate"]}")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
