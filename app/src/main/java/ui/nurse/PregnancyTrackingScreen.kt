package com.example.maternitymanagement.ui.nurse

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun PregnancyTrackingScreen(patientRegNumber: String) {
    var lmp by remember { mutableStateOf("") }
    var weeksPregnant by remember { mutableStateOf("") }
    var trimester by remember { mutableStateOf("") }
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
        Text("Pregnancy Tracking for $patientName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = lmp, onValueChange = { lmp = it }, label = { Text("Last Menstrual Period (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            try {
                val lmpDate = LocalDate.parse(lmp)
                val weeks = ChronoUnit.WEEKS.between(lmpDate, LocalDate.now())
                weeksPregnant = "$weeks Weeks"

                trimester = when {
                    weeks < 13 -> "First Trimester"
                    weeks in 13..27 -> "Second Trimester"
                    else -> "Third Trimester"
                }
            } catch (e: Exception) {
                message = "Invalid date format"
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (weeksPregnant.isNotEmpty()) {
            Text("Weeks Pregnant: $weeksPregnant")
            Text("Trimester: $trimester")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        userId?.let {
                            FirebaseManager.firestore.collection("profiles").document(it).update(
                                mapOf(
                                    "pregnancy_weeks" to weeksPregnant,
                                    "trimester" to trimester
                                )
                            ).await()
                            message = "Pregnancy status updated!"
                        } ?: run {
                            message = "Patient not found"
                        }
                    } catch (e: Exception) {
                        message = e.message ?: "Error updating pregnancy status"
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.primary)
        }
    }
}
