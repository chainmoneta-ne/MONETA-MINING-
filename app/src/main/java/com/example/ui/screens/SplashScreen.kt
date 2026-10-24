package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import com.example.ui.components.OrigamiCubeLoader
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2500) // Minimum 2.5s splash
        onSplashComplete()
    }

    OrigamiCubeLoader(
        size = 100.dp,
        text = "Initializing MONETA CHAIN...",
        fullScreen = true
    )
}

