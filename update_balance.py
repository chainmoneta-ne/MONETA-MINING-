import re

with open('app/src/main/java/com/example/MonetaViewModel.kt', 'r') as f:
    content = f.read()

# Replace the initial walletBalance in MonetaUiState from 0.0 to 50.0
content = content.replace('val walletBalance: Double = 0.0,', 'val walletBalance: Double = 50.0,')

with open('app/src/main/java/com/example/MonetaViewModel.kt', 'w') as f:
    f.write(content)
