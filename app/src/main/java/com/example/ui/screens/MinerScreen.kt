package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.components.CircularMiningProgressIndicator
import com.example.ui.components.HashRateTelemetryCard
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun MinerScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        
        // News & Announcements
        Card(
            modifier = Modifier.fillMaxWidth().border(1.dp, MonetaElectricBlue.copy(alpha=0.3f), MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Notifications, contentDescription = "News", tint = MonetaElectricBlue)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("PROTOCOL NEWS", style = MaterialTheme.typography.labelSmall, color = MonetaElectricBlue, fontWeight = FontWeight.Bold)
                    Text("System unified to v2 realtime architecture.", style = MaterialTheme.typography.bodySmall, color = MonetaWhite)
                }
            }
        }

        // Balance Card
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
                Text("TOTAL MONETA COIN", style = MaterialTheme.typography.labelMedium, color = MonetaSilver, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(java.util.Locale.US, "%.2f MNT", uiState.walletBalance),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MonetaEmeraldGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // 3. Central Circular Mining Progress & Hash Rate Progression Component
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, MaterialTheme.shapes.medium)
                .border(
                    1.dp,
                    if (uiState.isMining || uiState.autoMiningActive) MonetaEmeraldGreen.copy(alpha = 0.6f) else MonetaGraphiteGray,
                    MaterialTheme.shapes.medium
                ),
            colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val baseHashRate = 28.5 * uiState.minerLevel
                val boostMultiplier = (if (uiState.activeBoosters.contains("PRO BOOST")) 5.0 else if (uiState.activeBoosters.contains("2X MULTIPLIER")) 2.0 else 1.0) * (if (uiState.autoMiningActive) 1.5 else 1.0)
                val minedSoFar = uiState.sessionProgress * (100.0 * uiState.idleMultiplier * (if (uiState.activeBoosters.contains("PRO BOOST")) 5.0 else 1.0))

                CircularMiningProgressIndicator(
                    isMining = uiState.isMining,
                    autoMiningActive = uiState.autoMiningActive,
                    sessionCompleted = uiState.sessionCompleted,
                    progress = uiState.sessionProgress,
                    remainingSeconds = uiState.sessionRemainingSeconds,
                    baseHashRate = baseHashRate,
                    multiplier = boostMultiplier,
                    minedSoFar = minedSoFar,
                    onStartMiningClick = {
                        if (!uiState.isMining && !uiState.autoMiningActive && !uiState.sessionCompleted) {
                            viewModel.startMiningSession()
                        }
                    }
                )

                if (uiState.sessionCompleted) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.acknowledgeSessionCompletion(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MonetaEmeraldGreen,
                            contentColor = MonetaCharcoalBlack
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp)
                            .testTag("claim_mining_reward_button")
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Claim")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CLAIM REWARD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Live Hash Rate Telemetry Strip
        val activeHashRate = if (uiState.isMining || uiState.autoMiningActive) {
            val boostMult = (if (uiState.activeBoosters.contains("PRO BOOST")) 5.0 else if (uiState.activeBoosters.contains("2X MULTIPLIER")) 2.0 else 1.0) * (if (uiState.autoMiningActive) 1.5 else 1.0)
            28.5 * uiState.minerLevel * boostMult
        } else {
            0.0
        }
        
        HashRateTelemetryCard(
            isMining = uiState.isMining,
            autoMiningActive = uiState.autoMiningActive,
            hashRate = activeHashRate,
            activeBoosters = uiState.activeBoosters
        )

        // 5. Mining Status Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, MaterialTheme.shapes.medium)
                .border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.medium),
            colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("MINING STATUS", color = MonetaSilver)
                    Text(
                        if (uiState.isMining || uiState.autoMiningActive) "● ACTIVE" else "● INACTIVE", 
                        color = if (uiState.isMining || uiState.autoMiningActive) MonetaEmeraldGreen else MonetaSilver,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (uiState.autoMiningActive) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("AUTO-MINING", color = MonetaSilver)
                        Text(
                            "⚡ ACTIVE ✓", 
                            color = MonetaElectricBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("MINING RATE", color = MonetaSilver)
                    val baseRate = (100.0 * uiState.idleMultiplier) / 3600.0
                    val currentRate = if (uiState.autoMiningActive) baseRate * 2.0 else baseRate
                    Text("${String.format(Locale.US, "%.4f", currentRate)} MNT/sec", color = MonetaWhite)
                }
                
                if (uiState.activeBoosters.isNotEmpty()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ACTIVE BOOSTERS", color = MonetaSilver)
                        Text("${uiState.activeBoosters.size} Active", color = MonetaGoldenYellow, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!uiState.isMining && !uiState.sessionCompleted && !uiState.autoMiningActive) {
            Button(
                onClick = { viewModel.startMiningSession() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("start_mining_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonetaEmeraldGreen,
                    contentColor = MonetaCharcoalBlack
                )
            ) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start Mining")
                Text(
                    text = "START MINING",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
