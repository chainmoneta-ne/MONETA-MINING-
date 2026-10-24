package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun AnnouncementsScreen() {
    val announcements = listOf(
        "Mining Updates" to "We've increased the base mining rate for this week!",
        "Maintenance" to "Scheduled maintenance on Sunday 2AM UTC.",
        "New Features" to "Digital Cards are now available.",
        "Security Notice" to "Please enable 2FA to protect your account.",
        "MONETA Chain" to "Testnet phase 2 is launching next month."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "MONETA MINING ANNOUNCEMENTS",
            style = MaterialTheme.typography.titleLarge.copy(
                brush = MainGradient
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(announcements) { (title, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MonetaGraphiteGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MonetaElectricBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MonetaSilver)
                    }
                }
            }
        }
    }
}
