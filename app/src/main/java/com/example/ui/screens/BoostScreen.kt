package com.example.ui.screens

import com.example.ui.components.OrigamiLoadingSpinner

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MonetaViewModel
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun BoostScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var showPaymentFlow by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.boostError != null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    text = uiState.boostError!!,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        Text(
            text = "PREMIUM SUBSCRIPTIONS",
            style = MaterialTheme.typography.labelMedium,
            color = MonetaElectricBlue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Payments are processed securely via Trust Wallet on your chosen network.",
            style = MaterialTheme.typography.bodySmall,
            color = MonetaSilver,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        BoostCard(
            title = "AUTO-MINING",
            icon = Icons.Filled.ElectricBolt,
            subtitle = "Mines 24/7 automatically",
            description = "Never miss a session. Protocol automatically credits your account every hour. Earn 2x base speed.",
            buttonText = if (uiState.autoMiningActive) "EXTEND SUBSCRIPTION" else "PAY WITH TRUST WALLET ($9)",
            isActive = uiState.autoMiningActive,
            isPremium = true,
            onClick = {
                if (!uiState.isWalletConnected || uiState.connectedNetwork == null) {
                    viewModel.notify("Please connect Trust Wallet on the Wallet screen first.")
                    return@BoostCard
                }
                showPaymentFlow = true
                viewModel.createPaymentIntent("AUTO_MINING_30D", uiState.connectedNetwork!!)
            }
        )
        
        BoostCard(
            title = "PRO BOOST",
            icon = Icons.Filled.Star,
            subtitle = "Maximum extraction power",
            description = "The ultimate upgrade. Increases your base mining rate by 5x and lowers the withdrawal threshold.",
            buttonText = if (uiState.activeBoosters.contains("PRO BOOST")) "EXTEND SUBSCRIPTION" else "PAY WITH TRUST WALLET ($19)",
            isActive = uiState.activeBoosters.contains("PRO BOOST"),
            isPremium = true,
            onClick = {
                if (!uiState.isWalletConnected || uiState.connectedNetwork == null) {
                    viewModel.notify("Please connect Trust Wallet on the Wallet screen first.")
                    return@BoostCard
                }
                showPaymentFlow = true
                viewModel.createPaymentIntent("PRO_BOOST_30D", uiState.connectedNetwork!!)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "IN-APP BOOSTERS",
            style = MaterialTheme.typography.labelMedium,
            color = MonetaSilver,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        BoostCard(
            title = "2X MULTIPLIER",
            icon = Icons.Filled.TrendingUp,
            subtitle = "Double your hash rate",
            description = "Applies a permanent 2x multiplier to your base mining rate. Payable with MINT.",
            buttonText = if (uiState.activeBoosters.contains("2X MULTIPLIER")) "ACTIVE" else "BUY FOR 50 MINT",
            isActive = uiState.activeBoosters.contains("2X MULTIPLIER"),
            onClick = {
                if (!uiState.activeBoosters.contains("2X MULTIPLIER")) {
                    viewModel.purchaseWithMint(50.0, "2X MULTIPLIER")
                }
            }
        )
    }

    if (showPaymentFlow) {
        val intent = uiState.activePaymentIntent
        
        AlertDialog(
            onDismissRequest = { 
                if (intent?.status == "CREATED") {
                    showPaymentFlow = false
                    viewModel.clearPaymentIntent()
                }
            },
            containerColor = MonetaGraphiteGray,
            title = {
                Text(
                    text = when {
                        uiState.paymentIntentLoading -> "CREATING PAYMENT INTENT..."
                        intent?.status == "CREATED" -> "CONFIRM PAYMENT"
                        intent?.status == "PENDING" -> "PAYMENT PENDING"
                        intent?.status == "CONFIRMED" -> "PAYMENT CONFIRMED ✓"
                        else -> ""
                    },
                    fontWeight = FontWeight.Bold,
                    color = if (intent?.status == "CONFIRMED") MonetaEmeraldGreen else MonetaElectricBlue
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (uiState.paymentIntentLoading) {
                        OrigamiLoadingSpinner(text = null, color = MonetaElectricBlue, modifier = Modifier.align(Alignment.CenterHorizontally))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Securely generating transaction payload on backend...", color = MonetaSilver, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    } else if (intent != null) {
                        when (intent.status) {
                            "CREATED" -> {
                                Text("PRODUCT", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(intent.productName, color = MonetaWhite, fontWeight = FontWeight.Bold)
                                
                                Text("PRICE", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(String.format(Locale.US, "$%.2f", intent.usdPrice), color = MonetaWhite)
                                
                                Text("PAYMENT NETWORK", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(intent.expectedNetwork, color = MonetaWhite)
                                
                                Text("PAYMENT ASSET", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(intent.expectedAsset, color = MonetaWhite)
                                
                                Text("AMOUNT REQUIRED", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(String.format(Locale.US, "%.5f %s", intent.expectedAmountCrypto, intent.expectedAsset), color = MonetaGoldenYellow, fontWeight = FontWeight.Bold)
                                
                                Text("RECEIVING ADDRESS", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("0xb6FE••••••8342", color = MonetaElectricBlue)
                                
                                Text("NETWORK FEE", color = MonetaSilver, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Calculated by Trust Wallet", color = MonetaWhite)
                            }
                            "PENDING" -> {
                                OrigamiLoadingSpinner(text = null, color = MonetaGoldenYellow, modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Your transaction has been submitted and is waiting for blockchain confirmation.", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = MonetaSilver)
                            }
                            "CONFIRMED" -> {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Success", tint = MonetaEmeraldGreen, modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally))
                                Text("${intent.productName} ACTIVE", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = MonetaWhite, fontWeight = FontWeight.Bold)
                                Text("30 DAYS", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), color = MonetaSilver)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (intent?.status == "CREATED") {
                    Button(
                        onClick = {
                            try {
                                val intentUri = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("trust://"))
                                intentUri.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intentUri)
                            } catch (e: android.content.ActivityNotFoundException) {
                                viewModel.notify("Trust Wallet is not installed. Running simulation for preview.")
                            }
                            val mockTxHash = "0x" + (1..40).map { "0123456789abcdef".random() }.joinToString("")
                            viewModel.submitPaymentTransaction(mockTxHash)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("PAY WITH TRUST WALLET", fontWeight = FontWeight.Bold) }
                } else if (intent?.status == "CONFIRMED") {
                    Button(
                        onClick = {
                            showPaymentFlow = false
                            viewModel.clearPaymentIntent()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaEmeraldGreen, contentColor = MonetaCharcoalBlack)
                    ) { Text("DONE", fontWeight = FontWeight.Bold) }
                }
            },
            dismissButton = {
                if (intent?.status == "CREATED") {
                    TextButton(onClick = { 
                        showPaymentFlow = false
                        viewModel.clearPaymentIntent()
                    }) {
                        Text("CANCEL", color = MonetaSilver)
                    }
                }
            }
        )
    }
}

@Composable
fun BoostCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    subtitle: String,
    description: String,
    buttonText: String,
    isActive: Boolean = false,
    isPremium: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isPremium) 12.dp else 8.dp, MaterialTheme.shapes.medium)
            .border(
                1.dp, 
                if (isActive) MonetaEmeraldGreen else if (isPremium) MonetaElectricBlue else MonetaGraphiteGray, 
                MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = title, tint = if (isPremium) MonetaElectricBlue else MonetaWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, color = if (isPremium) MonetaElectricBlue else MonetaWhite, fontWeight = FontWeight.Bold)
                }
                if (isActive) {
                    Text("ACTIVE ✓", style = MaterialTheme.typography.labelSmall, color = MonetaEmeraldGreen, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(subtitle, style = MaterialTheme.typography.titleMedium, color = MonetaGoldenYellow, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, color = MonetaSilver, style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) MonetaCharcoalBlack else if (isPremium) MonetaElectricBlue else MonetaWhite,
                    contentColor = if (isActive) MonetaEmeraldGreen else if (isPremium) MonetaWhite else MonetaCharcoalBlack
                )
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
