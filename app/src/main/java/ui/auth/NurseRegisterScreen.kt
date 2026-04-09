package com.example.maternitymanagement.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment   // ✅ Import Alignment here
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.maternitymanagement.data.remote.FirebaseManager
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun NurseRegisterScreen(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,   // ✅ Now works
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nurse Registration", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (password != confirmPassword) {
                    message = "Passwords do not match!"
                    return@Button
                }
                isLoading = true
                scope.launch {
                    try {
                        val result = FirebaseManager.auth.createUserWithEmailAndPassword(email, password).await()
                        val userId = result.user?.uid ?: throw Exception("Failed to create account")

                        // Save profile in Firestore
                        FirebaseManager.firestore.collection("profiles").document(userId).set(
                            mapOf(
                                "id" to userId,
                                "full_name" to fullName,
                                "email" to email,
                                "role" to "nurse"
                            )
                        ).await()

                        navController.navigate("nurse_login")
                    } catch (e: FirebaseAuthUserCollisionException) {
                        message = "Email already in use"
                    } catch (e: Exception) {
                        message = e.message ?: "Registration failed"
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Creating Account..." else "Register")
        }

        if (message.isNotEmpty()) {
            Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("nurse_login") }) {
            Text("Already have an account? Login here")
        }
    }
}
