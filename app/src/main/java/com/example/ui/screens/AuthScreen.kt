package com.example.ui.screens

import com.example.ui.components.OrigamiLoadingSpinner

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.MonetaViewModel
import com.example.ui.theme.*
import com.example.ui.components.OrigamiCubeLoader

@Composable
fun AuthScreen(
    viewModel: MonetaViewModel,
    onAuthComplete: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    
    // Form fields
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    var showWalletModal by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }

    LaunchedEffect(isConnecting) {
        if (isConnecting) {
            delay(2000)
            isConnecting = false
            onAuthComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MONETA MINING",
            style = MaterialTheme.typography.headlineLarge.copy(
                brush = MainGradient
            ),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isSignUp) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = if (isSignUp) email else username,
            onValueChange = { if (isSignUp) email = it else username = it },
            label = { Text(if (isSignUp) "Email" else "Username") },
            leadingIcon = { 
                Icon(if (isSignUp) Icons.Filled.Email else Icons.Filled.Person, contentDescription = null) 
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (isSignUp) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        if (isSignUp) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Show password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = referralCode,
                onValueChange = { referralCode = it },
                label = { Text("Referral Code (Optional)") },
                leadingIcon = { Icon(Icons.Filled.Group, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Verification Code") },
                leadingIcon = { Icon(Icons.Filled.VerifiedUser, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { /* Forgot password */ }) {
                    Text("Forgot Password?")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                isConnecting = true
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue)
        ) {
            Text(if (isSignUp) "Create Account" else "Login", fontWeight = FontWeight.Bold)
        }
        
        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(if (isSignUp) "Already have an account? Login" else "Don't have an account? Create Account")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(" OR ", color = MonetaGray, modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { showWalletModal = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MonetaOrange)
        ) {
            Icon(Icons.Filled.Wallet, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Connect Crypto Wallet")
        }
    }

    if (showWalletModal) {
        AlertDialog(
            onDismissRequest = { showWalletModal = false },
            title = { Text("Connect Wallet") },
            text = { Text("Please sign the message in your Web3 wallet (e.g., MetaMask, Trust Wallet) to authenticate securely via the blockchain.") },
            confirmButton = {
                Button(
                    onClick = {
                        showWalletModal = false
                        isConnecting = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MonetaEmeraldGreen)
                ) {
                    Text("Simulate Wallet Signature", color = MonetaCharcoalBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWalletModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (isConnecting) {
        OrigamiCubeLoader(
            size = 80.dp,
            text = "Connecting to MONETA CHAIN...",
            fullScreen = true
        )
    }
}
