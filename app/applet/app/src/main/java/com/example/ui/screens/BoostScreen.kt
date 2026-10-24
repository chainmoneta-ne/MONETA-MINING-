package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.components.OrigamiLoadingSpinner
import com.example.ui.theme.*

@Composable
fun BoostScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.activePaymentIntent != null) {
        val intent = uiState.activePaymentIntent!!
        Column(
            modifier = Modifier.fillMaxSize().background(MonetaCharcoalBlack).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (intent.status) {
                "CREATED" -> {
                    Text("AWAITING PAYMENT", style = MaterialTheme.typography.titleLarge, color = MonetaWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Please complete the transaction in Trust Wallet.", color = MonetaSilver)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(String.format(java.util.Locale.US, "Amount: %.4f ${intent.expectedAsset}", intent.expectedAmountCrypto), color = MonetaGoldenYellow, style = MaterialTheme.typography.headlineMedium)
                    Text("Network: ${intent.expectedNetwork}", color = MonetaElectricBlue)
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { viewModel.submitPaymentTransaction("0xMOCKTXHASH123456789") },
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue, contentColor = MonetaCharcoalBlack)
                    ) { Text("I HAVE SENT THE PAYMENT") }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { viewModel.clearPaymentIntent() }) { Text("CANCEL", color = MonetaSilver) }
                }
                "PAYMENT_DETECTED" -> {
                    OrigamiLoadingSpinner(size = 80.dp, text = "Payment Hash Detected...")
                }
                "VERIFYING_BLOCKCHAIN" -> {
                    OrigamiLoadingSpinner(size = 80.dp, text = "Verifying Blockchain Confirmations...\n${intent.expectedNetwork}")
                }
                "CONFIRMED" -> {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Success", tint = MonetaEmeraldGreen, modifier = Modifier.size(80.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("PAYMENT CONFIRMED", style = MaterialTheme.typography.titleLarge, color = MonetaEmeraldGreen, fontWeight = FontWeight.Bold)
                    Text("Entitlement ${intent.productName} activated.", color = MonetaSilver)
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.clearPaymentIntent() },
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaEmeraldGreen, contentColor = MonetaCharcoalBlack)
                    ) { Text("CONTINUE TO DASHBOARD", fontWeight = FontWeight.Bold) }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MonetaCharcoalBlack).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MonetaGoldenYellow, MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
            ) {
                Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PROTOCOL BOOSTERS", color = MonetaGoldenYellow, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("Upgrade your mining rate on the Moneta network", color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        
        if (uiState.boostError != null) {
            item {
                Card(modifier = Modifier.fillMaxWidth().border(1.dp, MonetaCrimsonRed, MaterialTheme.shapes.small), colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(uiState.boostError!!, color = MonetaCrimsonRed, style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { viewModel.clearBoostMessages() }) { Text("Dismiss", color = MonetaSilver) }
                    }
                }
            }
        }

        // Crypto Payment Products
        item {
            Text("PREMIUM ENTITLEMENTS (TRUST WALLET)", color = MonetaWhite, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        }

        item {
            BoostProductCard(
                title = "AUTO-MINING ENTITLEMENT",
                description = "Automate your session renewals for 30 days. Earn MINT continuously while the app is closed.",
                price = "$9.00 USD",
                currency = "BNB Smart Chain",
                isActive = uiState.autoMiningActive,
                actionText = if (uiState.autoMiningActive) "ACTIVE (30D)" else "PAY WITH TRUST WALLET",
                onAction = { viewModel.createPaymentIntent("AUTO_MINING_30D", "BNB Smart Chain") }
            )
        }

        item {
            BoostProductCard(
                title = "PRO MINING BOOST (5X)",
                description = "Multiply your base mining rate by 5X. Verified via smart contract entitlement for 30 days.",
                price = "$19.00 USD",
                currency = "Ethereum Mainnet (ETH)",
                isActive = uiState.activeBoosters.contains("PRO BOOST"),
                actionText = if (uiState.activeBoosters.contains("PRO BOOST")) "ACTIVE (30D)" else "PAY WITH TRUST WALLET",
                onAction = { viewModel.createPaymentIntent("PRO_BOOST_30D", "Ethereum Mainnet") }
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { Divider(color = MonetaGraphiteGray, thickness = 1.dp) }
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // MINT Payment Products
        item {
            Text("PROTOCOL UPGRADES (MINT BALANCE)", color = MonetaWhite, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        }
        
        item {
            BoostProductCard(
                title = "2X MINING MULTIPLIER",
                description = "Double your mining speed permanently.",
                price = "50 MINT",
                currency = "Deducted from balance",
                isActive = uiState.activeBoosters.contains("2X MULTIPLIER"),
                actionText = if (uiState.activeBoosters.contains("2X MULTIPLIER")) "ACTIVE" else "BUY FOR 50 MINT",
                onAction = { viewModel.purchaseWithMint(50.0, "2X MULTIPLIER") }
            )
        }
    }
}

@Composable
fun BoostProductCard(title: String, description: String, price: String, currency: String, isActive: Boolean, actionText: String, onAction: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp),
        colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, color = MonetaWhite, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                if (isActive) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Active", tint = MonetaEmeraldGreen)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(price, color = MonetaGoldenYellow, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(currency, color = MonetaElectricBlue, style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = onAction,
                    enabled = !isActive,
                    colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue, contentColor = MonetaCharcoalBlack, disabledContainerColor = MonetaCharcoalBlack, disabledContentColor = MonetaEmeraldGreen)
                ) { Text(actionText, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
