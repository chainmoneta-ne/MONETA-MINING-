package com.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonetaApp(viewModel: MonetaViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val screens = listOf(
        "miner" to Icons.Filled.Home,
        "boost" to Icons.Filled.Bolt,
        "friends" to Icons.Filled.Group,
        "wallet" to Icons.Filled.AccountBalanceWallet
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Listen to unified app notifications
    LaunchedEffect(uiState.transientNotification) {
        uiState.transientNotification?.let { notif ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = notif,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearNotification()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentRoute != "splash" && currentRoute != "auth") {
                Column {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "MONETA CHAIN",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MonetaSilver,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%,.2f", uiState.walletBalance)} MINT",
                                    style = MaterialTheme.typography.titleLarge.copy(brush = MiningGradient),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { navController.navigate("announcements") }) {
                                Icon(Icons.Filled.Notifications, contentDescription = "Announcements", tint = MonetaElectricBlue)
                            }
                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MonetaElectricBlue)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MonetaCharcoalBlack
                        )
                    )
                    
                    // Offline / Syncing Banner
                    AnimatedVisibility(visible = uiState.isOffline || uiState.isSyncing) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (uiState.isOffline) MonetaCrimsonRed else MonetaGoldenYellow)
                                .padding(vertical = 4.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (uiState.isOffline) Icons.Filled.Warning else Icons.Filled.Sync,
                                contentDescription = "Status",
                                tint = MonetaCharcoalBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isOffline) "OFFLINE - Waiting for connection" else "SYNCING WITH PROTOCOL...",
                                color = MonetaCharcoalBlack,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute != "splash" && currentRoute != "auth") {
                NavigationBar(containerColor = MonetaCharcoalBlack) {
                    val currentDestination = navBackStackEntry?.destination
                    screens.forEach { (route, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = route.replaceFirstChar { it.uppercase() }) },
                            label = { Text(route.replaceFirstChar { it.uppercase() }) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MonetaElectricBlue,
                                selectedTextColor = MonetaElectricBlue,
                                unselectedIconColor = MonetaSilver,
                                unselectedTextColor = MonetaSilver,
                                indicatorColor = MonetaGraphiteGray
                            ),
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo("miner") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = if (currentRoute == "splash" || currentRoute == "auth") Modifier else Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onSplashComplete = {
                    navController.navigate("auth") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
            }
            composable("auth") {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthComplete = {
                        navController.navigate("miner") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }
            composable("miner") { MinerScreen(viewModel) }
            composable("boost") { BoostScreen(viewModel) }
            composable("friends") { FriendsScreen(viewModel) }
            composable("wallet") { WalletScreen(viewModel) }
            composable("announcements") { AnnouncementsScreen() }
            composable("settings") { SettingsScreen(viewModel) }
        }
    }
}
