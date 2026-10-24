import re

with open('app/src/main/java/com/example/ui/screens/BoostScreen.kt', 'r') as f:
    content = f.read()

# Add a warning in the payment dialog if needed, or implement the real intent
old_btn = """val mockTxHash = "0x" + (1..40).map { "0123456789abcdef".random() }.joinToString("")
                            viewModel.submitPaymentTransaction(mockTxHash)"""

new_btn = """try {
                                val intentUri = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("trust://"))
                                intentUri.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intentUri)
                            } catch (e: android.content.ActivityNotFoundException) {
                                viewModel.notify("Trust Wallet is not installed. Running simulation for preview.")
                            }
                            val mockTxHash = "0x" + (1..40).map { "0123456789abcdef".random() }.joinToString("")
                            viewModel.submitPaymentTransaction(mockTxHash)"""

content = content.replace(old_btn, new_btn)

with open('app/src/main/java/com/example/ui/screens/BoostScreen.kt', 'w') as f:
    f.write(content)
