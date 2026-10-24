import re

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'r') as f:
    content = f.read()

pattern = r'SettingsSection\("CONNECTED WALLETS"\) \{.*?\n        \}'

replacement = '''SettingsSection("CONNECTED WALLET") {
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
            }'''

new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/SettingsScreen.kt', 'w') as f:
    f.write(new_content)
