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
fun PhysicalExamScreen(patientRegNumber: String) {
    var abdominalExam by remember { mutableStateOf("") }
    var urinalysis by remember { mutableStateOf("") }
    var bloodTest by remember { mutableStateOf("") }
    var bloodPressure by remember { mutableStateOf("") }
    var ultrasound by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf<String?>(null) }
    var patientName by remember { mutableStateOf("Loading...") }
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
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Physical Exam for $patientName", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = abdominalExam, onValueChange = { abdominalExam = it }, label = { Text("Abdominal Exam") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = urinalysis, onValueChange = { urinalysis = it }, label = { Text("Urinalysis") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = bloodTest, onValueChange = { bloodTest = it }, label = { Text("Blood Test") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = bloodPressure, onValueChange = { bloodPressure = it }, label = { Text("Blood Pressure") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = ultrasound, onValueChange = { ultrasound = it }, label = { Text("Ultrasound Findings") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                try {
                    userId?.let {
                        FirebaseManager.firestore.collection("physical_exams").document(it).set(
                            mapOf(
                                "abdominal_exam" to abdominalExam,
                                "urinalysis" to urinalysis,
                                "blood_test" to bloodTest,
                                "blood_pressure" to bloodPressure,
                                "ultrasound" to ultrasound
                            )
                        ).await()
                        message = "Exam findings saved!"
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error saving exam"
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
                        val doc = FirebaseManager.firestore.collection("physical_exams").document(it).get().await()
                        if (doc.exists()) {
                            savedData = doc.data
                            message = ""
                        } else {
                            message = "No exam records found"
                        }
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Error fetching exam"
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("View Records")
        }

        savedData?.let { data ->
            Spacer(modifier = Modifier.height(12.dp))
            Text("Abdominal Exam: ${data["abdominal_exam"]}")
            Text("Urinalysis: ${data["urinalysis"]}")
            Text("Blood Test: ${data["blood_test"]}")
            Text("Blood Pressure: ${data["blood_pressure"]}")
            Text("Ultrasound: ${data["ultrasound"]}")
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.error)
        }
    }
}
