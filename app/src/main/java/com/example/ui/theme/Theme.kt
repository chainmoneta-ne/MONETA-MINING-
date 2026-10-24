package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MonetaElectricBlue, // Main buttons
    onPrimary = MonetaWhite,
    secondary = MonetaEmeraldGreen,
    onSecondary = MonetaCharcoalBlack,
    tertiary = MonetaGoldenYellow,
    background = MonetaCharcoalBlack, // Main Background
    surface = MonetaGraphiteGray,
    surfaceVariant = MonetaGraphiteGray,
    onBackground = MonetaWhite,
    onSurface = MonetaWhite,
    error = MonetaCrimsonRed,
    onError = MonetaWhite
)

private val LightColorScheme = lightColorScheme(
    primary = MonetaElectricBlue, // Main buttons
    onPrimary = MonetaWhite,
    secondary = MonetaEmeraldGreen,
    onSecondary = MonetaCharcoalBlack,
    tertiary = MonetaGoldenYellow,
    background = MonetaCharcoalBlack, // Force Dark Mode Default
    surface = MonetaGraphiteGray,
    surfaceVariant = MonetaGraphiteGray,
    onBackground = MonetaWhite,
    onSurface = MonetaWhite,
    error = MonetaCrimsonRed,
    onError = MonetaWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
