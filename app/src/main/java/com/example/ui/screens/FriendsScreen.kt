package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.MonetaViewModel
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun FriendsScreen(viewModel: MonetaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var showCopyToast by remember { mutableStateOf(false) }

    LaunchedEffect(showCopyToast) {
        if (showCopyToast) {
            kotlinx.coroutines.delay(2000)
            showCopyToast = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Friends & Referrals",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("YOUR REFERRAL CODE", style = MaterialTheme.typography.labelLarge, color = MonetaSilver)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MonetaCharcoalBlack)
                        .border(1.dp, MonetaElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(uiState.accountId))
                            showCopyToast = true
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = uiState.accountId.ifEmpty { "LOADING..." },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MonetaElectricBlue
                        )
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = MonetaSilver, modifier = Modifier.size(20.dp))
                    }
                }
                
                if (showCopyToast) {
                    Text("Code copied to clipboard!", color = MonetaEmeraldGreen, style = MaterialTheme.typography.bodySmall)
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("Join MONETA with my code ${uiState.accountId} and get 100 free MNT!"))
                            showCopyToast = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaEmeraldGreen, contentColor = MonetaCharcoalBlack)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                        Text(" SHARE", modifier = Modifier.padding(start = 4.dp))
                    }
                    
                    Button(
                        onClick = { viewModel.simulateNewFriend() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MonetaGoldenYellow, contentColor = MonetaCharcoalBlack)
                    ) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Simulate", modifier = Modifier.size(18.dp))
                        Text(" DEMO FRIEND", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Referral rewards are subject to MONETA eligibility and protocol rules.",
            style = MaterialTheme.typography.bodySmall,
            color = MonetaSilver,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Commission", color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${String.format(Locale.US, "%.0f", uiState.totalEarnedFromFriends)} MNT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MonetaGoldenYellow
                    )
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Active Friends", color = MonetaSilver, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "${uiState.activeFriendsCount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MonetaElectricBlue
                    )
                }
            }
        }

        Text(
            text = "Recent Referrals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start).padding(top = 8.dp)
        )

        if (uiState.recentReferrals.isEmpty()) {
            Text("No friends referred yet.", color = MonetaSilver, modifier = Modifier.padding(32.dp))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.recentReferrals) { referral ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, MaterialTheme.shapes.medium)
                            .border(1.dp, MonetaElectricBlue.copy(alpha = 0.2f), MaterialTheme.shapes.medium),
                        colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Person, contentDescription = "Friend", tint = MonetaElectricBlue)
                                Text(
                                    text = referral.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MonetaWhite,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            Text(
                                "+${String.format(Locale.US, "%.0f", referral.earned)} MNT",
                                color = MonetaEmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

