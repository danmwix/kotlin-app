package com.example.maternitymanagement.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import com.example.maternitymanagement.data.remote.SupabaseClient

// ✅ FIXED IMPORTS
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

import kotlinx.coroutines.launch

@Composable
fun MotherRegisterScreen(navController: NavHostController) {

    var fullName by remember { mutableStateOf("") }
    var regNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(regNumber, { regNumber = it }, label = { Text("Registration Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(password, { password = it }, label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(confirmPassword, { confirmPassword = it }, label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                if (password != confirmPassword) {
                    message = "Passwords do not match!"
                    return@Button
                }

                isLoading = true

                scope.launch {
                    try {
                        // ✅ SIGN UP
                        SupabaseClient.client.auth.signUpWith(Email) {
                            this.email = email
                            this.password = password
                        }

// ✅ Get logged-in user from session
                        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                            ?: throw Exception("User not logged in")

                        // ✅ INSERT PROFILE
                        SupabaseClient.client.postgrest["profiles"].insert(
                            mapOf(
                                "id" to userId,
                                "full_name" to fullName,
                                "registration_number" to regNumber,
                                "email" to email,
                                "role" to "expectant_mother"
                            )
                        )

                        message = "Account created successfully!"
                        navController.navigate("mother_login")

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
            Text(
                text = message,
                color = if (message.contains("success", true))
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("mother_login") }) {
            Text("Already have an account? Login here")
        }
    }
}