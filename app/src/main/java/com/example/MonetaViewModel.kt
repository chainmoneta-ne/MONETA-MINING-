package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.ActivityNotFoundException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Transaction(
    val id: Long,
    val timestamp: Long,
    val type: String, // "mining", "deposit", "withdraw", "referral", "subscription", "purchase"
    val amount: Double,
    val description: String,
    val network: String? = null,
    val txHash: String? = null
)

data class PaymentIntent(
    val intentId: String,
    val productId: String,
    val productName: String,
    val expectedNetwork: String,
    val expectedChainId: Int,
    val expectedAsset: String,
    val usdPrice: Double,
    val expectedAmountCrypto: Double,
    val receivingAddress: String,
    val status: String, // CREATED, PENDING, CONFIRMED, EXPIRED
    val createdAt: Long,
    val expiresAt: Long
)

data class FriendReferral(
    val id: Long,
    val name: String,
    val earned: Double
)

data class MonetaUiState(
    // Global Network & Sync State
    val isOffline: Boolean = false,
    val isSyncing: Boolean = false,
    val transientNotification: String? = null,
    
    // Theme
    val isDarkMode: Boolean = true,
    
    // Identity
    val username: String = "@moneta_miner",
    val accountId: String = "MNT-A94B2C",
    
    // Central Authoritative MINT Balance
    val walletBalance: Double = 50.0,
    val totalEarned: Double = 0.0,
    
    // Mining Session State
    val isMining: Boolean = false,
    val minerLevel: Int = 1,
    val idleMultiplier: Double = 1.0,
    val sessionProgress: Float = 0f,
    val sessionRemainingSeconds: Int = 0,
    val sessionCompleted: Boolean = false,
    
    // Subscriptions & Boosters
    val autoMiningActive: Boolean = false,
    val autoMiningExpires: Long = 0,
    val activeBoosters: List<String> = emptyList(),
    
    // Form & Screen Messages (Local Errors)
    val boostError: String? = null,
    val minerUpgradeError: String? = null,
    val withdrawError: String? = null,
    val addressError: String? = null,
    
    // Referral Data
    val activeFriendsCount: Int = 0,
    val totalEarnedFromFriends: Double = 0.0,
    val recentReferrals: List<FriendReferral> = emptyList(),
    
    // Trust Wallet & Payments
    val isWalletConnected: Boolean = false,
    val walletAddress: String = "",
    val connectedNetwork: String? = null, // "BNB Smart Chain" or "Ethereum"
    val connectedChainId: Int? = null,    // 56 or 1
    val nativeAssetBalance: Double = 0.0,
    
    // Payment Intent System
    val activePaymentIntent: PaymentIntent? = null,
    val paymentIntentLoading: Boolean = false,
    
    val transactions: List<Transaction> = emptyList()
)

class MonetaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MonetaUiState())
    val uiState: StateFlow<MonetaUiState> = _uiState.asStateFlow()

    private var miningJob: Job? = null
    
    // System Simulator: randomly go offline to demonstrate real-time UX
    init {
        viewModelScope.launch {
            while (true) {
                delay(60000) // Every minute
                if (Math.random() > 0.8) {
                    _uiState.update { it.copy(isOffline = true) }
                    delay(5000)
                    _uiState.update { it.copy(isOffline = false) }
                    triggerSync()
                }
            }
        }
    }

    private fun triggerSync() {
        if (_uiState.value.isOffline) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            delay(1200)
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    private fun simulateBackendAction(
        actionName: String, 
        action: suspend () -> Unit, 
        onSuccessMessage: String? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (_uiState.value.isOffline) {
            onError?.invoke("Cannot complete $actionName while offline.")
            notify("You are offline. Reconnecting...")
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            delay(1500)
            action()
            _uiState.update { it.copy(isSyncing = false) }
            if (onSuccessMessage != null) {
                notify(onSuccessMessage)
            }
        }
    }

    fun notify(message: String) {
        _uiState.update { it.copy(transientNotification = message) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(transientNotification = null) }
    }

    private fun addTransactionInternal(type: String, amount: Double, description: String, network: String? = null, txHash: String? = null) {
        val newTx = Transaction(
            id = System.currentTimeMillis(),
            timestamp = System.currentTimeMillis(),
            type = type,
            amount = amount,
            description = description,
            network = network,
            txHash = txHash
        )
        _uiState.update { currentState ->
            val newBalance = if (type == "mining" || type == "deposit" || type == "withdraw" || type == "referral" || type == "purchase") {
                currentState.walletBalance + amount
            } else currentState.walletBalance
            
            val updatedList = (listOf(newTx) + currentState.transactions).take(100)
            currentState.copy(
                walletBalance = newBalance,
                transactions = updatedList
            )
        }
    }

    // --- PAYMENT INTENT SYSTEM (BACKEND AUTHORITATIVE) ---
    
    fun createPaymentIntent(productId: String, network: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(paymentIntentLoading = true, boostError = null) }
            delay(1500) // Simulate backend intent generation latency

            val isBnb = network == "BNB Smart Chain"
            val priceUsd = if (productId == "AUTO_MINING_30D") 9.0 else 19.0
            val name = if (productId == "AUTO_MINING_30D") "AUTO-MINING" else "PRO BOOST"
            val asset = if (isBnb) "BNB" else "ETH"
            val chainId = if (isBnb) 56 else 1
            
            // Backend decides exact required amount
            val cryptoAmount = if (isBnb) (priceUsd / 550.0) else (priceUsd / 3200.0) 
            
            val intent = PaymentIntent(
                intentId = "PI_${System.currentTimeMillis()}",
                productId = productId,
                productName = name,
                expectedNetwork = network,
                expectedChainId = chainId,
                expectedAsset = asset,
                usdPrice = priceUsd,
                expectedAmountCrypto = cryptoAmount,
                receivingAddress = "0xb6fe369fbb317eff1c8c8add493196b8e8708342",
                status = "CREATED",
                createdAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + (10 * 60 * 1000L) // 10 minutes
            )

            _uiState.update { 
                it.copy(
                    paymentIntentLoading = false,
                    activePaymentIntent = intent
                ) 
            }
        }
    }

    fun submitPaymentTransaction(txHash: String) {
        val intent = _uiState.value.activePaymentIntent ?: return
        if (intent.status != "CREATED") return

        viewModelScope.launch {
            // Update intent to PENDING - waiting for backend confirmation
            _uiState.update { 
                it.copy(
                    activePaymentIntent = intent.copy(status = "PENDING")
                ) 
            }
            
            // Simulate backend monitoring the blockchain & waiting for confirmations
            delay(4000) 
            
            // Backend verified the transaction logic
            val confirmedIntent = intent.copy(status = "CONFIRMED")
            _uiState.update { 
                it.copy(activePaymentIntent = confirmedIntent) 
            }
            
            // Backend explicitly applies entitlements to the account
            if (intent.productId == "AUTO_MINING_30D") {
                val expiry = System.currentTimeMillis() + (30 * 24L * 3600L * 1000L)
                _uiState.update { 
                    it.copy(
                        autoMiningActive = true,
                        autoMiningExpires = expiry
                    ) 
                }
            } else if (intent.productId == "PRO_BOOST_30D") {
                _uiState.update { 
                    val newBoosters = it.activeBoosters.toMutableList()
                    if (!newBoosters.contains("PRO BOOST")) {
                        newBoosters.add("PRO BOOST")
                    }
                    it.copy(activeBoosters = newBoosters) 
                }
            }
            
            // Store robust transaction record locally
            addTransactionInternal(
                type = "subscription",
                amount = -intent.expectedAmountCrypto,
                description = "${intent.productName} 30 Days (Verified on ${intent.expectedNetwork})",
                network = intent.expectedNetwork,
                txHash = txHash
            )
            
            notify("Payment verified! ${intent.productName} activated.")
        }
    }

    fun clearPaymentIntent() {
        _uiState.update { it.copy(activePaymentIntent = null, paymentIntentLoading = false) }
    }

    // --- END PAYMENT INTENT SYSTEM ---

    fun withdrawMNT(amount: Double) {
        val minWithdrawal = 1000.0
        val withdrawalFee = 10.0
        val currentState = _uiState.value
        
        _uiState.update { it.copy(withdrawError = null) }
        
        if (amount < minWithdrawal) {
            _uiState.update { it.copy(withdrawError = "Minimum withdrawal is $minWithdrawal MNT") }
            return
        }
        if (!currentState.isWalletConnected || currentState.walletAddress.isBlank()) {
            _uiState.update { it.copy(withdrawError = "Please connect Trust Wallet first") }
            return
        }
        val totalDeduct = amount + withdrawalFee
        if (currentState.walletBalance < totalDeduct) {
            _uiState.update { it.copy(withdrawError = "Insufficient authoritative balance (including fee)") }
            return
        }
        
        simulateBackendAction("Withdraw MNT", {
            addTransactionInternal("withdraw", -totalDeduct, "Withdrawal to ${currentState.walletAddress.take(6)}... (fee $withdrawalFee MNT)", network = currentState.connectedNetwork)
        }, "Withdrawal of $amount MNT initiated successfully.")
    }

    fun loadPreferences(context: Context) {
        val prefs = context.getSharedPreferences("moneta_prefs", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("isDarkMode", true)
        val autoActive = prefs.getBoolean("autoMiningActive", false)
        val autoExpires = prefs.getLong("autoMiningExpires", 0L)
        val savedLevel = prefs.getInt("minerLevel", 1)
        val savedMultiplier = prefs.getFloat("idleMultiplier", 1.0f).toDouble()
        val totalEarned = prefs.getFloat("totalEarned", 0.0f).toDouble()
        val walletBalance = prefs.getFloat("walletBalance", 0.0f).toDouble()
        
        _uiState.update {
            it.copy(
                isDarkMode = isDark,
                autoMiningActive = autoActive && System.currentTimeMillis() < autoExpires,
                autoMiningExpires = autoExpires,
                minerLevel = savedLevel,
                idleMultiplier = savedMultiplier,
                totalEarned = totalEarned,
                walletBalance = walletBalance
            )
        }
    }

    fun startMiningSession() {
        simulateBackendAction("Start Mining", {
            _uiState.update { 
                it.copy(
                    isMining = true, 
                    sessionCompleted = false,
                    sessionProgress = 0f,
                    sessionRemainingSeconds = 4 * 3600
                ) 
            }
            if (miningJob?.isActive == true) miningJob?.cancel()
            miningJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    _uiState.update { currentState ->
                        if (!currentState.isMining) return@update currentState
                        
                        val newRemaining = currentState.sessionRemainingSeconds - 1
                        if (newRemaining <= 0) {
                            val reward = 100.0 * currentState.idleMultiplier
                            addTransactionInternal("mining", reward, "Completed 4-hour mining session")
                            
                            val newTotalEarned = currentState.totalEarned + reward
                            currentState.copy(
                                totalEarned = newTotalEarned,
                                sessionProgress = 1f,
                                sessionRemainingSeconds = 0,
                                sessionCompleted = true,
                                isMining = false
                            )
                        } else {
                            val newProgress = 1f - (newRemaining.toFloat() / (4 * 3600))
                            currentState.copy(
                                sessionProgress = newProgress,
                                sessionRemainingSeconds = newRemaining
                            )
                        }
                    }
                    if (_uiState.value.sessionCompleted) {
                        notify("Mining session completed! Reward credited.")
                        break
                    }
                }
            }
        }, "Mining session initialized on protocol.")
    }

    fun acknowledgeSessionCompletion(context: Context) {
        _uiState.update { it.copy(sessionCompleted = false, sessionProgress = 0f) }
        val prefs = context.getSharedPreferences("moneta_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putFloat("totalEarned", _uiState.value.totalEarned.toFloat())
            .putFloat("walletBalance", _uiState.value.walletBalance.toFloat())
            .apply()
    }

    fun purchaseWithMint(cost: Double, itemName: String) {
        val currentState = _uiState.value
        if (currentState.walletBalance >= cost) {
            simulateBackendAction("Purchase $itemName", {
                addTransactionInternal("purchase", -cost, itemName)
                _uiState.update { 
                    it.copy(
                        boostError = null,
                        activeBoosters = it.activeBoosters + itemName
                    ) 
                }
            }, "$itemName activated. ${cost} MINT deducted.")
        } else {
            _uiState.update {
                it.copy(boostError = "Insufficient authoritative MINT balance.")
            }
        }
    }

    fun clearBoostMessages() {
        _uiState.update { it.copy(boostError = null) }
    }

    fun simulateNewFriend() {
        simulateBackendAction("Add Referral", {
            val earnedFromFriend = 500.0
            val friendName = listOf("CryptoKing", "MoonMiner", "TapQueen", "HustleBro", "DiamondHands").random()
            val newReferral = FriendReferral(
                id = System.currentTimeMillis(),
                name = friendName,
                earned = earnedFromFriend
            )
            addTransactionInternal("referral", earnedFromFriend, "Referral commission from $friendName")
            
            _uiState.update { currentState ->
                currentState.copy(
                    activeFriendsCount = currentState.activeFriendsCount + 1,
                    totalEarnedFromFriends = currentState.totalEarnedFromFriends + earnedFromFriend,
                    recentReferrals = listOf(newReferral) + currentState.recentReferrals
                )
            }
        }, "New referral verified! Reward added to balance.")
    }

    fun toggleDarkMode(context: Context) {
        _uiState.update { currentState ->
            val newValue = !currentState.isDarkMode
            val prefs = context.getSharedPreferences("moneta_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("isDarkMode", newValue).apply()
            currentState.copy(isDarkMode = newValue)
        }
    }
    
    fun connectTrustWallet(context: Context, network: String) {
        val chainId = if (network == "BNB Smart Chain") 56 else 1
        
        // REAL WORLD INTEGRATION: Attempt to deep-link to the actual Trust Wallet app
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("trust://"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // In the AI Studio Emulator, Trust Wallet is not installed.
            // When installed on a real device, this exception will not be thrown.
            _uiState.update { it.copy(addressError = "Trust Wallet is not installed on this device.") }
            
            // For preview/emulator purposes, we will continue with the backend simulation
            // so you can see how the UI reacts when connected.
        }

        simulateBackendAction("Connect Trust Wallet", {
            val mockAddress = "0xb6FE" + (1..32).map { "0123456789abcdef".random() }.joinToString("") + "8342"
            val mockBalance = if (chainId == 56) 0.85 else 0.12
            
            _uiState.update { 
                it.copy(
                    isWalletConnected = true,
                    walletAddress = mockAddress,
                    connectedNetwork = network,
                    connectedChainId = chainId,
                    nativeAssetBalance = mockBalance,
                    addressError = null
                ) 
            }
        }, "Trust Wallet connection synchronized via $network.") { error ->
            _uiState.update { it.copy(addressError = error) }
        }
    }
    
    fun disconnectWallet() {
        _uiState.update { 
            it.copy(
                isWalletConnected = false,
                walletAddress = "",
                connectedNetwork = null,
                connectedChainId = null,
                nativeAssetBalance = 0.0
            ) 
        }
        notify("Trust Wallet disconnected.")
    }

    fun clearMessages() {
        _uiState.update { 
            it.copy(withdrawError = null, addressError = null, minerUpgradeError = null) 
        }
    }
}
