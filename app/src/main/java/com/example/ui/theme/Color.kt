package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Colors (Main Identity)
val MonetaElectricBlue = Color(0xFF00E5FF)
val MonetaRoyalPurple = Color(0xFF7B1FA2)
val MonetaEmeraldGreen = Color(0xFF00C853)

// Secondary Colors (Highlights)
val MonetaGoldenYellow = Color(0xFFFFD700)
val MonetaOrange = Color(0xFFFF9800)
val MonetaCrimsonRed = Color(0xFFDC143C)

// Neutral Colors (Application Interface)
val MonetaCharcoalBlack = Color(0xFF121212)
val MonetaGraphiteGray = Color(0xFF2C2C2C)
val MonetaWhite = Color(0xFFFFFFFF)
val MonetaSilver = Color(0xFFC0C0C0)

// Gradients
val MainGradient = Brush.linearGradient(
    colors = listOf(MonetaElectricBlue, MonetaRoyalPurple)
)

val MiningGradient = Brush.linearGradient(
    colors = listOf(MonetaEmeraldGreen, MonetaElectricBlue)
)

val PremiumGradient = Brush.linearGradient(
    colors = listOf(MonetaGoldenYellow, MonetaOrange)
)

// Aliases for compatibility
val MonetaBlue = MonetaElectricBlue
val MonetaPurple = MonetaRoyalPurple
val MonetaGreen = MonetaEmeraldGreen
val MonetaYellow = MonetaGoldenYellow
val MonetaRed = MonetaCrimsonRed
val MonetaBlack = MonetaCharcoalBlack
val MonetaDarkCard = MonetaGraphiteGray
val MonetaGray = MonetaSilver

val MonetaLightBg = Color(0xFFF2F2F7)
val MonetaLightCard = Color(0xFFFFFFFF)
val MonetaLightGray = Color(0xFFE5E5EA)

val RainbowGradient = Brush.linearGradient(
    colors = listOf(MonetaRed, MonetaOrange, MonetaYellow, MonetaGreen, MonetaBlue, MonetaPurple)
)
