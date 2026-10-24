import re

with open('app/src/main/java/com/example/ui/screens/WalletScreen.kt', 'r') as f:
    content = f.read()

if 'LocalContext' not in content:
    content = content.replace('import androidx.compose.ui.platform.LocalClipboardManager', 'import androidx.compose.ui.platform.LocalClipboardManager\nimport androidx.compose.ui.platform.LocalContext')

content = content.replace('val clipboardManager: ClipboardManager = LocalClipboardManager.current', 'val clipboardManager: ClipboardManager = LocalClipboardManager.current\n    val context = LocalContext.current')
content = content.replace('viewModel.connectTrustWallet("BNB Smart Chain")', 'viewModel.connectTrustWallet(context, "BNB Smart Chain")')
content = content.replace('viewModel.connectTrustWallet("Ethereum")', 'viewModel.connectTrustWallet(context, "Ethereum")')

with open('app/src/main/java/com/example/ui/screens/WalletScreen.kt', 'w') as f:
    f.write(content)
