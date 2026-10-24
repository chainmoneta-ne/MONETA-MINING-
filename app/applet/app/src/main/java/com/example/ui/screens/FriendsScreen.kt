package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.components.OrigamiLoadingSpinner
import com.example.ui.theme.*

@Composable
fun FriendsScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchReferralDashboard()
    }

    if (uiState.leaderboardLoading && uiState.referralDashboard == null) {
        Box(modifier = Modifier.fillMaxSize().background(MonetaCharcoalBlack), contentAlignment = Alignment.Center) {
            OrigamiLoadingSpinner(size = 64.dp, text = "Syncing Leaderboard & Referrals...")
        }
        return
    }

    val dashboard = uiState.referralDashboard

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MonetaCharcoalBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // My Referral Network
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, MonetaElectricBlue.copy(alpha=0.5f), MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
            ) {
                Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                    Text("MY REFERRAL NETWORK", color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(uiState.accountId, color = MonetaElectricBlue, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.notify("MONETA ID copied to clipboard") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MonetaCharcoalBlack, contentColor = MonetaWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MonetaSilver)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("COPY ID")
                        }
                        Button(
                            onClick = { viewModel.notify("Opening share sheet...") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MonetaElectricBlue, contentColor = MonetaCharcoalBlack)
                        ) {
                            Icon(Icons.Filled.GroupAdd, contentDescription = "Invite", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("INVITE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (dashboard != null) {
            // Stats Grid
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(title = "INVITED", value = dashboard.myStats.invitedCount.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "REGISTERED", value = dashboard.myStats.registeredCount.toString(), modifier = Modifier.weight(1f))
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(title = "QUALIFIED", value = dashboard.myStats.qualifiedCount.toString(), modifier = Modifier.weight(1f))
                    StatCard(title = "MINT EARNED", value = "+${dashboard.myStats.earnedMint}", modifier = Modifier.weight(1f), valueColor = MonetaGoldenYellow)
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MonetaGraphiteGray)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆 REFERRAL LEADERBOARD", style = MaterialTheme.typography.titleMedium, color = MonetaWhite, fontWeight = FontWeight.Bold)
                    Text("GLOBAL", style = MaterialTheme.typography.labelSmall, color = MonetaElectricBlue, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Top Leaderboard
            itemsIndexed(dashboard.leaderboard.take(5)) { index, entry ->
                val rank = index + 1
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (rank) {
                                    1 -> "👑"
                                    2 -> "🥈"
                                    3 -> "🥉"
                                    else -> "#$rank"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = if (rank <= 3) MonetaWhite else MonetaSilver,
                                modifier = Modifier.width(36.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(entry.monetaId, color = MonetaWhite, fontWeight = if (rank <= 3) FontWeight.Bold else FontWeight.Normal)
                        }
                        Text("${entry.qualifiedCount} Qualified", color = MonetaEmeraldGreen, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // User's Own Rank (if not in top 5 or just pinned at bottom of top section)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).border(1.dp, MonetaGoldenYellow.copy(alpha=0.5f), MaterialTheme.shapes.small),
                    colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("YOUR RANK", color = MonetaGoldenYellow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("#${dashboard.myRank}", color = MonetaWhite, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(uiState.accountId, color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
                            Text("${dashboard.myStats.qualifiedCount} Qualified Referrals", color = MonetaEmeraldGreen, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("MY REFERRALS", style = MaterialTheme.typography.titleSmall, color = MonetaSilver, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (dashboard.recentReferrals.isEmpty()) {
                item {
                    Text("You haven't referred anyone yet.", color = MonetaSilver, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            } else {
                itemsIndexed(dashboard.recentReferrals) { _, ref ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MonetaGraphiteGray)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(ref.referredMonetaId, color = MonetaWhite)
                            val (statusColor, statusText) = when (ref.status) {
                                "QUALIFIED" -> MonetaEmeraldGreen to "✓ QUALIFIED"
                                "REGISTERED" -> MonetaElectricBlue to "✓ REGISTERED"
                                "FRAUD_REVIEW" -> MonetaCrimsonRed to "⚠ REVIEW"
                                else -> MonetaSilver to ref.status
                            }
                            Text(statusText, color = statusColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, valueColor: androidx.compose.ui.graphics.Color = MonetaWhite) {
    Card(
        modifier = modifier.border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.small),
        colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = MonetaSilver, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = valueColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
