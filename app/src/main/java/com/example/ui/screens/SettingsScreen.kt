package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showComingSoonDialog by remember { mutableStateOf(false) }
    var activeModalTitle by remember { mutableStateOf("") }

    val openModal = { title: String -> 
        activeModalTitle = title
        showComingSoonDialog = true 
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ACCOUNT SETTINGS
        item {
            SettingsSection("ACCOUNT") {
                SettingsRow(
                    title = "User Profile", 
                    subtitle = "${uiState.username} • ${uiState.accountId}", 
                    onClick = { openModal("User Profile") }
                )
                SettingsRow(title = "Email Address", subtitle = "miningmoneta@gmail.com", trailingText = "SECURE", onClick = { openModal("Email Address") })
                SettingsRow(title = "Security & Two-Factor (2FA)", onClick = { openModal("Security") })
                SettingsRow(title = "Delete Account", textColor = MonetaCrimsonRed, onClick = { showDeleteDialog = true })
            }
        }

        // CONNECTED WALLET
        item {
            SettingsSection("CONNECTED WALLET") {
                if (uiState.isWalletConnected) {
                    SettingsRow(
                        title = "Trust Wallet",
                        subtitle = "${uiState.walletAddress.take(6)}••••${uiState.walletAddress.takeLast(4)}\nNetwork: ${uiState.connectedNetwork}",
                        trailingText = "● Connected",
                        onClick = { }
                    )
                    SettingsRow(
                        title = "Disconnect",
                        onClick = { viewModel.disconnectWallet() },
                        textColor = MonetaCrimsonRed
                    )
                } else {
                    SettingsRow(
                        title = "Wallet",
                        trailingText = "Not Connected",
                        onClick = { }
                    )
                }
            }
        }

        // DISPLAY & PREFERENCES
        item {
            SettingsSection("DISPLAY & PREFERENCES") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark Theme", color = MonetaWhite)
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(context) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MonetaCharcoalBlack,
                            checkedTrackColor = MonetaElectricBlue
                        )
                    )
                }
                SettingsRow(title = "Animations", trailingText = "Full", onClick = { openModal("Animations") })
                SettingsRow(title = "Haptic Feedback", trailingText = "ON", onClick = { openModal("Haptic Feedback") })
            }
        }

        // APP INFORMATION & UPDATES
        item {
            SettingsSection("APP INFORMATION & UPDATES") {
                SettingsRow(title = "MONETA MINING APP", trailingText = "v1.0.0 (100)", onClick = { openModal("App Information") })
                SettingsRow(title = "Check for Updates", onClick = { openModal("Check for Updates") }, textColor = MonetaElectricBlue)
                SettingsRow(title = "Release Notes", onClick = { openModal("Release Notes") })
            }
        }

        // SUPPORT
        item {
            SettingsSection("HELP & SUPPORT") {
                SettingsRow(title = "Help Center & FAQs", onClick = { openModal("Help Center") })
                SettingsRow(title = "Contact Support", onClick = { openModal("Contact Support") })
                SettingsRow(title = "Report a Problem", onClick = { openModal("Report a Problem") })
                SettingsRow(title = "Report Security Issue", onClick = { openModal("Report Security Issue") }, textColor = MonetaOrange)
            }
        }

        // DATA & PRIVACY
        item {
            SettingsSection("DATA & PRIVACY") {
                SettingsRow(title = "Privacy Controls", onClick = { openModal("Privacy Controls") })
                SettingsRow(title = "Analytics & Data Collection", onClick = { openModal("Analytics") })
                SettingsRow(title = "Export Account Data", onClick = { openModal("Export Data") })
            }
        }

        // LEGAL & TRANSPARENCY
        item {
            SettingsSection("LEGAL & TRANSPARENCY") {
                SettingsRow(title = "Terms of Service", onClick = { openModal("Terms of Service") })
                SettingsRow(title = "Privacy Policy", onClick = { openModal("Privacy Policy") })
                SettingsRow(title = "MINT Reward & Participation Rules", onClick = { openModal("MINT Rules") })
                SettingsRow(title = "Wallet & Payment Disclosures", onClick = { openModal("Payment Disclosures") })
                SettingsRow(title = "Risk Disclosure", onClick = { openModal("Risk Disclosure") })
            }
        }

        // STORAGE
        item {
            SettingsSection("STORAGE") {
                SettingsRow(title = "App Storage Usage", trailingText = "45 MB", onClick = { openModal("Storage Usage") })
                SettingsRow(title = "Clear Cache", onClick = { showClearCacheDialog = true }, textColor = MonetaOrange)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    // Modals
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("DELETE ACCOUNT", color = MonetaCrimsonRed, fontWeight = FontWeight.Bold) },
            text = { 
                Text(
                    "This permanently removes your MONETA account and associated account data, subject to applicable retention requirements.\n\nAre you sure you want to proceed?",
                    color = MonetaSilver
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MonetaCrimsonRed)
                ) {
                    Text("PROCEED TO AUTHENTICATE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL", color = MonetaSilver)
                }
            }
        )
    }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("CLEAR CACHE", color = MonetaWhite, fontWeight = FontWeight.Bold) },
            text = { 
                Text(
                    "This will clear temporary files to free up space. It will NOT delete your MINT balance, blockchain transactions, subscriptions, or wallet ownership.",
                    color = MonetaSilver
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { showClearCacheDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue)
                ) {
                    Text("CLEAR CACHE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("CANCEL", color = MonetaSilver)
                }
            }
        )
    }

    if (showComingSoonDialog) {
        AlertDialog(
            onDismissRequest = { showComingSoonDialog = false },
            title = { Text(activeModalTitle, color = MonetaWhite, fontWeight = FontWeight.Bold) },
            text = { 
                Text(
                    "This feature requires secure authentication and backend confirmation. Proceeding would initialize secure API communication.",
                    color = MonetaSilver
                ) 
            },
            confirmButton = {
                Button(
                    onClick = { showComingSoonDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue)
                ) {
                    Text("UNDERSTOOD")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MonetaSilver,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    trailingText: String? = null,
    textColor: androidx.compose.ui.graphics.Color = MonetaWhite,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = textColor, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(trailingText, color = if (trailingText.contains("ACTIVE") || trailingText.contains("SECURE")) MonetaEmeraldGreen else MonetaSilver)
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = "Forward", tint = MonetaSilver, modifier = Modifier.size(20.dp))
        }
    }
    HorizontalDivider(color = MonetaCharcoalBlack, modifier = Modifier.padding(horizontal = 16.dp))
}
