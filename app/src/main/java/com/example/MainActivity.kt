package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.MyApplicationTheme

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Firebase with placeholder options to avoid crash without google-services.json
    try {
      if (FirebaseApp.getApps(this).isEmpty()) {
        val options = FirebaseOptions.Builder()
            .setApplicationId("1:1234567890:android:abcdef")
            .setApiKey("placeholder_api_key")
            .setProjectId("moneta-mining-placeholder")
            .build()
        FirebaseApp.initializeApp(this, options)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    
    enableEdgeToEdge()
    setContent {
      val viewModel: MonetaViewModel = viewModel()
      viewModel.loadPreferences(this)
      val uiState by viewModel.uiState.collectAsState()
      
      MyApplicationTheme(darkTheme = uiState.isDarkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
          MonetaApp(viewModel = viewModel)
        }
      }
    }
  }
}
