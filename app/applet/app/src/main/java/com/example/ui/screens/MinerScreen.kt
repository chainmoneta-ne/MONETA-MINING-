package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.MonetaViewModel
import com.example.ui.components.OrigamiLoadingSpinner
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinerScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Timer state for forcing UI recomposition without spamming ViewModel updates
    var currentUiTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    
    LaunchedEffect(uiState.activeMiningSession) {
        while (uiState.activeMiningSession?.status == "ACTIVE") {
            delay(1000)
            currentUiTime = System.currentTimeMillis()
        }
    }

    // Modal overlay for Mining History
    if (uiState.showHistoryModal) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleHistoryModal(false) },
            containerColor = MonetaGraphiteGray,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.History, contentDescription = "History", tint = MonetaElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MINING HISTORY", color = MonetaElectricBlue, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.miningHistory) { session ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack),
                            modifier = Modifier.fillMaxWidth().border(1.dp, MonetaEmeraldGreen.copy(alpha=0.3f), MaterialTheme.shapes.small)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("+${String.format(Locale.US, "%.2f", session.finalReward ?: session.estimatedReward)} MNT", color = MonetaEmeraldGreen, fontWeight = FontWeight.Bold)
                                    Text("✓ CREDITED", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${session.durationHours} HOURS", color = MonetaWhite, style = MaterialTheme.typography.bodySmall)
                                val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                                Text("Completed: ${sdf.format(Date(session.expectedEndAt))}", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.toggleHistoryModal(false) }) {
                    Text("CLOSE", color = MonetaSilver)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Identity Card
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("MONETA ID", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                    Text(uiState.accountId, color = MonetaElectricBlue, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(uiState.username, color = MonetaWhite, style = MaterialTheme.typography.bodySmall)
                }
                TextButton(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MonetaCrimsonRed)
                ) {
                    Text("LOGOUT", fontWeight = FontWeight.Bold)
                }
            }
        }
        
        // 1. Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, MaterialTheme.shapes.medium)
                .border(1.dp, MonetaEmeraldGreen.copy(alpha=0.5f), MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("MINT BALANCE", style = MaterialTheme.typography.labelMedium, color = MonetaSilver, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(Locale.US, "%,.2f MNT", uiState.walletBalance),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MonetaEmeraldGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Active Boosts & Auto-Mining Status Flags
        if (uiState.autoMiningActive || uiState.activeBoosters.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (uiState.autoMiningActive) {
                    Card(
                        modifier = Modifier.weight(1f).border(1.dp, MonetaElectricBlue.copy(alpha=0.5f), MaterialTheme.shapes.small),
                        colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ElectricBolt, contentDescription = "Auto", tint = MonetaElectricBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AUTO-MINING ACTIVE", color = MonetaElectricBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (uiState.activeBoosters.isNotEmpty()) {
                    Card(
                        modifier = Modifier.weight(1f).border(1.dp, MonetaGoldenYellow.copy(alpha=0.5f), MaterialTheme.shapes.small),
                        colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ElectricBolt, contentDescription = "Boost", tint = MonetaGoldenYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            val boostText = if (uiState.activeBoosters.contains("PRO BOOST")) "5× MINING BOOST" else "2× MINING BOOST"
                            Text("ACTIVE BOOST: $boostText", color = MonetaGoldenYellow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Divider(color = MonetaGraphiteGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

        // 2 & 3 & 4. Mining Session Area
        val activeSession = uiState.activeMiningSession
        
        if (activeSession != null) {
            // Active or Verifying Mining State
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, MaterialTheme.shapes.medium)
                    .border(1.dp, if (activeSession.status == "VERIFYING") MonetaElectricBlue else MonetaEmeraldGreen, MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (activeSession.status == "VERIFYING") {
                        Text("SESSION COMPLETE", style = MaterialTheme.typography.titleMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        OrigamiLoadingSpinner(text = "Verifying your mining reward...", size = 48.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                    } else {
                        // ACTIVE
                        Text("MINING ACTIVE ●", style = MaterialTheme.typography.titleMedium, color = MonetaEmeraldGreen, fontWeight = FontWeight.Bold)
                        Text("${activeSession.durationHours} HOURS", color = MonetaSilver, style = MaterialTheme.typography.labelMedium)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        val remainingMs = maxOf(0L, activeSession.expectedEndAt - currentUiTime)
                        val totalMs = activeSession.durationHours * 3600 * 1000L
                        val elapsed = totalMs - remainingMs
                        val progress = (elapsed.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
                        val currentMint = progress * activeSession.estimatedReward
                        
                        val hours = (remainingMs / (1000 * 60 * 60)) % 24
                        val minutes = (remainingMs / (1000 * 60)) % 60
                        val seconds = (remainingMs / 1000) % 60
                        
                        Text(String.format(Locale.US, "%02d:%02d:%02d REMAINING", hours, minutes, seconds), style = MaterialTheme.typography.headlineMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(MaterialTheme.shapes.small),
                            color = MonetaEmeraldGreen,
                            trackColor = MonetaGraphiteGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("CURRENT SESSION MINT", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                        Text(String.format(Locale.US, "+%.2f MNT", currentMint), color = MonetaGoldenYellow, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(String.format(Locale.US, "Mining Rate: %.2f MNT/hr", activeSession.ratePerHour), color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        } else {
            // No Active Session - Show Selector or Confirmation
            if (uiState.selectedSessionHours != null) {
                // Confirmation Panel
                val hours = uiState.selectedSessionHours!!
                val baseRate = 12.5
                val multiplier = uiState.idleMultiplier * (if (uiState.activeBoosters.contains("2X MULTIPLIER")) 2.0 else 1.0) * (if (uiState.activeBoosters.contains("PRO BOOST")) 5.0 else 1.0)
                val ratePerHour = baseRate * multiplier
                val estReward = ratePerHour * hours
                
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(8.dp).border(1.dp, MonetaElectricBlue, MaterialTheme.shapes.medium),
                    colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("$hours-HOUR MINING SESSION", style = MaterialTheme.typography.titleMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Duration:", color = MonetaSilver)
                            Text("$hours hours", color = MonetaWhite)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Mining rate:", color = MonetaSilver)
                            Text(String.format(Locale.US, "%.2f MNT/hr", ratePerHour), color = MonetaWhite)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimated MINT:", color = MonetaSilver)
                            Text(String.format(Locale.US, "%.2f MNT", estReward), color = MonetaEmeraldGreen, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.cancelSessionSelection() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MonetaSilver)
                            ) { Text("CANCEL") }
                            
                            Button(
                                onClick = { viewModel.startSelectedSession() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MonetaEmeraldGreen, contentColor = MonetaCharcoalBlack)
                            ) { Text("START MINING", fontWeight = FontWeight.Bold) }
                        }
                    }
                }
            } else {
                // Selector
                Text("START MINING", style = MaterialTheme.typography.titleMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                Text("READY TO MINE. Choose a session duration.", color = MonetaSilver, style = MaterialTheme.typography.bodyMedium)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // 6 Hours
                    Card(
                        onClick = { viewModel.selectSessionDuration(6) },
                        modifier = Modifier.weight(1f).border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.medium),
                        colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("6 HOURS", style = MaterialTheme.typography.titleMedium, color = MonetaElectricBlue, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Short Session", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    
                    // 12 Hours
                    Card(
                        onClick = { viewModel.selectSessionDuration(12) },
                        modifier = Modifier.weight(1f).border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.medium),
                        colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("12 HOURS", style = MaterialTheme.typography.titleMedium, color = MonetaGoldenYellow, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Extended", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Divider(color = MonetaGraphiteGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

        // 5 & 6 & 7. Latest Mined MINT & History
        val latestSession = uiState.miningHistory.firstOrNull()
        
        if (latestSession != null) {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⛏ LATEST MINED MINT", style = MaterialTheme.typography.labelMedium, color = MonetaSilver, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(String.format(Locale.US, "+%.2f MNT", latestSession.finalReward ?: latestSession.estimatedReward), style = MaterialTheme.typography.headlineMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${latestSession.durationHours}-HOUR SESSION", color = MonetaSilver, style = MaterialTheme.typography.bodyMedium)
                    
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    Text("Completed: ${sdf.format(Date(latestSession.expectedEndAt))}", color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("✓ CREDITED", color = MonetaEmeraldGreen, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        
                        TextButton(onClick = { viewModel.toggleHistoryModal(true) }) {
                            Text("VIEW HISTORY", color = MonetaElectricBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Divider(color = MonetaGraphiteGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())

        // 8. News & Announcements
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, MonetaElectricBlue.copy(alpha=0.3f), MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Notifications, contentDescription = "News", tint = MonetaElectricBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("NEWS & ANNOUNCEMENTS", style = MaterialTheme.typography.labelSmall, color = MonetaElectricBlue, fontWeight = FontWeight.Bold)
                    Text("Latest MONETA CHAIN updates applied. Auto-mining unified to v2 architecture.", style = MaterialTheme.typography.bodySmall, color = MonetaWhite)
                }
            }
        }
    }
}
