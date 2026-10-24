package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.components.OrigamiLoadingSpinner
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: MonetaViewModel, onAuthComplete: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    var isRegistering by remember { mutableStateOf(false) }
    
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MonetaCharcoalBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MonetaGraphiteGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "M",
                    style = MaterialTheme.typography.displayLarge,
                    color = MonetaElectricBlue,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "MONETA CHAIN",
                style = MaterialTheme.typography.headlineMedium,
                color = MonetaSilver,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Secure Blockchain Mining",
                style = MaterialTheme.typography.bodyMedium,
                color = MonetaElectricBlue
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            if (uiState.isAuthenticating) {
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, MonetaElectricBlue, MaterialTheme.shapes.medium),
                    colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OrigamiLoadingSpinner(
                            size = 64.dp,
                            text = if (isRegistering) "Atomic Account Creation...\nGenerating SECURE MONETA ID" else "Authenticating & Syncing Ledger..."
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (uiState.authError != null) {
                            Text(
                                text = uiState.authError!!,
                                color = MonetaCrimsonRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        if (uiState.isOtpRequested) {
                            var otpCode by remember { mutableStateOf("") }
                            Text("Verify Phone Number", color = MonetaWhite, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Enter the 6-digit code sent to ${uiState.registrationPhonePending}", color = MonetaSilver, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { otpCode = it; viewModel.clearAuthError() },
                                label = { Text("6-Digit OTP", color = MonetaSilver) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonetaWhite,
                                    unfocusedTextColor = MonetaWhite,
                                    focusedBorderColor = MonetaElectricBlue,
                                    unfocusedBorderColor = MonetaSilver
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = { viewModel.registerAccount(otpCode) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("auth_submit_otp_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MonetaElectricBlue,
                                    contentColor = MonetaCharcoalBlack
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = "VERIFY & REGISTER",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(onClick = { viewModel.cancelOtpFlow() }) {
                                Text("Cancel & Go Back", color = MonetaSilver)
                            }
                        } else {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it; viewModel.clearAuthError() },
                                label = { Text("Phone Number", color = MonetaSilver) },
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = "Phone Number", tint = MonetaElectricBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonetaWhite,
                                    unfocusedTextColor = MonetaWhite,
                                    focusedBorderColor = MonetaElectricBlue,
                                    unfocusedBorderColor = MonetaSilver
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it; viewModel.clearAuthError() },
                                label = { Text("Password", color = MonetaSilver) },
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password", tint = MonetaElectricBlue) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MonetaWhite,
                                    unfocusedTextColor = MonetaWhite,
                                    focusedBorderColor = MonetaElectricBlue,
                                    unfocusedBorderColor = MonetaSilver
                                ),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = { 
                                    if (isRegistering) viewModel.requestRegistrationOtp(phoneNumber, password) 
                                    else viewModel.loginAccount(phoneNumber, password) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("auth_submit_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MonetaElectricBlue,
                                    contentColor = MonetaCharcoalBlack
                                ),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = if (isRegistering) "CREATE ACCOUNT" else "SECURE LOGIN",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            TextButton(
                                onClick = { 
                                    isRegistering = !isRegistering
                                    viewModel.clearAuthError() 
                                }
                            ) {
                                Text(
                                    text = if (isRegistering) "Already have an account? Login" else "Don't have an account? Register",
                                    color = MonetaSilver
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
