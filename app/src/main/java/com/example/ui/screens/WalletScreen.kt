package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun WalletScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showNetworkDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- MONETA RECEIVING ADDRESS CARD ---
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, MonetaEmeraldGreen, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MONETA RECEIVING WALLET", color = MonetaSilver, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("0xb6FE••••••8342", color = MonetaWhite, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { 
                        clipboardManager.setText(AnnotatedString("0xb6fe369fbb317eff1c8c8add493196b8e8708342")) 
                        viewModel.notify("Receiving address copied to clipboard")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MonetaCharcoalBlack, contentColor = MonetaEmeraldGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("COPY ADDRESS")
                }
            }
        }

        if (uiState.isWalletConnected) {
            // --- CONNECTED WALLET CARD ---
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MonetaElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TRUST WALLET", style = MaterialTheme.typography.titleMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("● CONNECTED", color = MonetaEmeraldGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    
                    Text(
                        text = if (uiState.walletAddress.length > 10) 
                            "${uiState.walletAddress.take(6)}••••••${uiState.walletAddress.takeLast(4)}" 
                        else uiState.walletAddress,
                        color = MonetaSilver, style = MaterialTheme.typography.bodyLarge
                    )

                    HorizontalDivider(color = MonetaCharcoalBlack)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("NETWORK", color = MonetaSilver)
                        Text(uiState.connectedNetwork ?: "Unknown", color = MonetaWhite, fontWeight = FontWeight.Bold)
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("BALANCE", color = MonetaSilver)
                        val asset = if (uiState.connectedChainId == 56) "BNB" else "ETH"
                        Text("${uiState.nativeAssetBalance} $asset", color = MonetaWhite, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showNetworkDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MonetaCharcoalBlack, contentColor = MonetaElectricBlue)
                        ) {
                            Text("SWITCH NETWORK")
                        }
                    }
                    
                    Button(
                        onClick = { viewModel.disconnectWallet() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaCharcoalBlack, contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.LinkOff, contentDescription = "Disconnect")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DISCONNECT TRUST WALLET")
                    }
                }
            }
            
            // withdraw card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("WITHDRAW MNT", style = MaterialTheme.typography.titleMedium, color = MonetaWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Available to withdraw: ${String.format(Locale.US, "%.2f", uiState.walletBalance)} MNT", color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (uiState.withdrawError != null) {
                        Text(uiState.withdrawError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Button(
                        onClick = { viewModel.withdrawMNT(1000.0) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue)
                    ) {
                        Text("WITHDRAW 1000 MNT")
                    }
                }
            }
        } else {
            // --- DISCONNECTED STATE ---
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MonetaCharcoalBlack, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(Icons.Filled.AccountBalanceWallet, contentDescription = "Wallet", tint = MonetaElectricBlue, modifier = Modifier.size(48.dp))
                    Text("WALLET", style = MaterialTheme.typography.titleMedium, color = MonetaWhite)
                    Text("Connect your Trust Wallet to manage your earnings and purchase boosters securely.", color = MonetaSilver, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    
                    Button(
                        onClick = { showNetworkDialog = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue)
                    ) {
                        Icon(Icons.Filled.Link, contentDescription = "Connect")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CONNECT TRUST WALLET", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Network Selection Dialog
        if (showNetworkDialog) {
            AlertDialog(
                onDismissRequest = { showNetworkDialog = false },
                containerColor = MonetaGraphiteGray,
                title = { Text("SELECT NETWORK", color = MonetaWhite) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Please select your preferred network for Trust Wallet connection:", color = MonetaSilver)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.connectTrustWallet(context, "BNB Smart Chain")
                                showNetworkDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MonetaCharcoalBlack, contentColor = MonetaGoldenYellow)
                        ) {
                            Text("BNB SMART CHAIN (BNB)")
                        }
                        Button(
                            onClick = {
                                viewModel.connectTrustWallet(context, "Ethereum")
                                showNetworkDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MonetaCharcoalBlack, contentColor = MonetaElectricBlue)
                        ) {
                            Text("ETHEREUM MAINNET (ETH)")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showNetworkDialog = false }) { Text("CANCEL", color = MonetaSilver) }
                }
            )
        }

        // --- TRANSACTION HISTORY ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("TRANSACTION HISTORY", style = MaterialTheme.typography.titleMedium, color = MonetaSilver)
                Spacer(modifier = Modifier.height(16.dp))
                if (uiState.transactions.isEmpty()) {
                    Text("No transactions yet.", color = MonetaSilver)
                } else {
                    uiState.transactions.take(10).forEach { tx ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tx.description, color = MonetaWhite, style = MaterialTheme.typography.bodyMedium)
                                if (tx.network != null) {
                                    Text("via ${tx.network}", color = MonetaElectricBlue, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Text(
                                text = if (tx.amount > 0) "+${tx.amount}" else "${tx.amount}",
                                color = if (tx.amount > 0) MonetaEmeraldGreen else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(color = MonetaCharcoalBlack)
                    }
                }
            }
        }
    }
}
