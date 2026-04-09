package com.example.maternitymanagement.ui.mother

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.maternitymanagement.data.remote.FirebaseManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotherDashboardScreen(navController: NavHostController) {
    var fullName by remember { mutableStateOf("Loading...") }
    var appointmentDate by remember { mutableStateOf("N/A") }
    var appointmentTime by remember { mutableStateOf("N/A") }
    var appointmentReason by remember { mutableStateOf("N/A") }
    var prescription by remember { mutableStateOf("N/A") }
    var pregnancyWeeks by remember { mutableStateOf("N/A") }
    var trimester by remember { mutableStateOf("N/A") }
    var dueDate by remember { mutableStateOf("N/A") }
    var returnDate by remember { mutableStateOf("N/A") }

    val scope = rememberCoroutineScope()

    // Load real data from Firestore
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val userId = FirebaseManager.auth.currentUser?.uid
                if (userId != null) {
                    val profile = FirebaseManager.firestore.collection("profiles").document(userId).get().await()
                    fullName = profile.getString("full_name") ?: "Unknown"
                    prescription = profile.getString("prescription") ?: "N/A"
                    pregnancyWeeks = profile.getString("pregnancy_weeks") ?: "N/A"
                    trimester = profile.getString("trimester") ?: "N/A"
                    dueDate = profile.getString("due_date") ?: "N/A"
                    returnDate = profile.getString("return_date") ?: "N/A"

                    val appointment = FirebaseManager.firestore.collection("appointments").document(userId).get().await()
                    appointmentDate = appointment.getString("appointment_date") ?: "N/A"
                    appointmentTime = appointment.getString("appointment_time") ?: "N/A"
                    appointmentReason = appointment.getString("reason") ?: "N/A"
                }
            } catch (e: Exception) {
                fullName = "Error loading data"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Top bar with logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Welcome $fullName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    FirebaseManager.auth.signOut()
                    navController.navigate("role_selection") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Cards row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                title = "Upcoming Appointment",
                value = "$appointmentDate • $appointmentTime\nReason: $appointmentReason",
                icon = "📅",
                modifier = Modifier.weight(1f)
            )

            DashboardCard(
                title = "Prescriptions",
                value = prescription,
                icon = "💊",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Cards row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DashboardCard(
                title = "Pregnancy Status",
                value = "$pregnancyWeeks\n$trimester\nDue: $dueDate",
                icon = "🤰",
                modifier = Modifier.weight(1f)
            )

            DashboardCard(
                title = "Scheduled Return Date",
                value = returnDate,
                icon = "📆",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Emergency Message Card
        var showEmergencyForm by remember { mutableStateOf(false) }
        var patientName by remember { mutableStateOf("") }
        var regNumber by remember { mutableStateOf("") }
        var selectedSymptom by remember { mutableStateOf("") }
        val symptoms = listOf(
            "Severe abdominal pain",
            "Heavy bleeding",
            "Severe headache",
            "Blurred vision",
            "Swelling of hands/face",
            "Difficulty breathing",
            "Fever/chills"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "🚨 Send Emergency Message",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Red
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showEmergencyForm = !showEmergencyForm },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Emergency Message")
                }

                if (showEmergencyForm) {
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = regNumber,
                        onValueChange = { regNumber = it },
                        label = { Text("Registration Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSymptom,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Symptom") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            symptoms.forEach { symptom ->
                                DropdownMenuItem(
                                    text = { Text(symptom) },
                                    onClick = {
                                        selectedSymptom = symptom
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { /* TODO: Implement emergency send */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm & Send")
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = icon, fontSize = 32.sp)

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
