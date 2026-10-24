import re

with open('app/src/main/java/com/example/ui/screens/MinerScreen.kt', 'r') as f:
    content = f.read()

# Add a balance card right after News & Announcements

balance_card = """        // Balance Card
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
        """

# find where News & Announcements Card ends
# The structure is:
#         // News & Announcements
#         Card( ... ) { ... }
#         // Status Card

content = content.replace('        // Status Card', balance_card + '\n        // Status Card')

with open('app/src/main/java/com/example/ui/screens/MinerScreen.kt', 'w') as f:
    f.write(content)

