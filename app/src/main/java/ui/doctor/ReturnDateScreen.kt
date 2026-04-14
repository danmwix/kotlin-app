package com.example.maternitymanagement.ui.doctor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ReturnDateScreen(patientRegNumber: String) {
    var returnDate by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf<String?>(null) }
    var patientName by remember { mutableStateOf("Loading...") }

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
        Text("Schedule Return Date for $patientName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = returnDate,
            onValueChange = { returnDate = it },
            label = { Text("Return Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        // Save return date in a dedicated collection
                        FirebaseManager.firestore.collection("return_dates").document(it).set(
                            mapOf("return_date" to returnDate)
                        ).await()

                        // Update patient profile so mother sees it
                        FirebaseManager.firestore.collection("profiles").document(it).update(
                            mapOf("return_date" to returnDate)
                        ).await()

                        message = "Return date scheduled successfully!"
                    } ?: run {
                        message = "Patient not found"
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error scheduling return date"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Return Date")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
