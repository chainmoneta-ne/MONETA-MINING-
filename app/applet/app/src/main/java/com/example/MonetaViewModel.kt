package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.ActivityNotFoundException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

import com.example.data.MonetaDatabase
import com.example.backend.BackendService

data class Transaction(
    val id: Long,
    val timestamp: Long,
    val type: String,
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
    val status: String,
    val createdAt: Long,
    val expiresAt: Long
)

data class FriendReferral(
    val id: Long,
    val name: String,
    val earned: Double
)

data class MiningSession(
    val id: String,
    val durationHours: Int,
    val startedAt: Long,
    val expectedEndAt: Long,
    val ratePerHour: Double,
    val estimatedReward: Double,
    val finalReward: Double? = null,
    val status: String // ACTIVE, VERIFYING, CREDITED, FAILED
)

data class MonetaUiState(
    // Global Network & Sync State
    val isOffline: Boolean = false,
    val isSyncing: Boolean = false,
    val transientNotification: String? = null,
    
    // Theme
    val isDarkMode: Boolean = true,
    
    // Auth & Identity (Module 1)
    val isAuthenticated: Boolean = false,
    val isAuthenticating: Boolean = false,
    val authError: String? = null,
    val isOtpRequested: Boolean = false,
    val registrationPhonePending: String = "",
    val registrationPassPending: String = "",
    val username: String = "",
    val accountId: String = "", // This is the public MONETA ID e.g. MNT-XXXXXXXX
    
    // Central Authoritative MINT Balance
    val walletBalance: Double = 0.0,
    val totalEarned: Double = 0.0,
    
    // Mining Session State
    val selectedSessionHours: Int? = null,
    val activeMiningSession: MiningSession? = null,
    val miningHistory: List<MiningSession> = listOf(),
    val showHistoryModal: Boolean = false,
    val minerLevel: Int = 1,
    val idleMultiplier: Double = 1.0,
    
    // Subscriptions & Boosters
    val autoMiningActive: Boolean = false,
    val autoMiningExpires: Long = 0,
    val activeBoosters: List<String> = emptyList(),
    
    // Form & Screen Messages (Local Errors)
    val boostError: String? = null,
    val minerUpgradeError: String? = null,
    val withdrawError: String? = null,
    val addressError: String? = null,
    
    // Referral Data (Module 3)
    val referralDashboard: BackendService.LeaderboardDashboard? = null,
    val leaderboardLoading: Boolean = false,
    
    // Trust Wallet & Payments (Module 2)
    val isWalletConnected: Boolean = false,
    val walletAddress: String = "",
    val connectedNetwork: String? = null,
    val connectedChainId: Int? = null,
    val nativeAssetBalance: Double = 0.0,
    
    val activePaymentIntent: PaymentIntent? = null,
    val paymentIntentLoading: Boolean = false,
    
    val transactions: List<Transaction> = emptyList()
)

class MonetaViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MonetaUiState())
    val uiState: StateFlow<MonetaUiState> = _uiState.asStateFlow()

    private val db = MonetaDatabase.getDatabase(application)
    private val backend = BackendService(db.monetaDao())
    private val prefs = application.getSharedPreferences("moneta_prefs", Context.MODE_PRIVATE)

    private var miningJob: Job? = null
    
    private var secureSessionToken: String?
        get() = prefs.getString("session_token", null)
        set(value) {
            prefs.edit().putString("session_token", value).apply()
        }

    init {
        viewModelScope.launch {
            // Check for existing valid session on startup
            val token = secureSessionToken
            if (token != null) {
                val result = backend.getMe(token)
                if (result.isSuccess) {
                    val me = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            isAuthenticated = true,
                            accountId = me.monetaId,
                            username = me.username,
                            walletBalance = me.availableBalance,
                            isDarkMode = me.isDarkMode
                        )
                    }
                } else {
                    secureSessionToken = null // Token revoked or expired
                }
            }

            // Simulate periodic network drops
            while (true) {
                delay(60000)
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
            
            // Re-fetch backend state if authenticated
            val token = secureSessionToken
            if (token != null) {
                val result = backend.getMe(token)
                if (result.isSuccess) {
                    val me = result.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            accountId = me.monetaId,
                            username = me.username,
                            walletBalance = me.availableBalance
                        )
                    }
                }
            }
            
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
            onError?.invoke("Cannot complete \$actionName while offline.")
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

    // --- MODULE 1: AUTHENTICATION & SECURE IDENTITY ---
    
    fun requestRegistrationOtp(phoneNumber: String, plaintextPass: String) {
        if (phoneNumber.isBlank() || phoneNumber.length < 7) {
            _uiState.update { it.copy(authError = "Please enter a valid phone number.") }
            return
        }
        if (plaintextPass.length < 6) {
            _uiState.update { it.copy(authError = "Password must be at least 6 characters.") }
            return
        }
        
        _uiState.update { it.copy(isAuthenticating = true, authError = null) }
        viewModelScope.launch {
            val result = backend.requestRegistrationOtp(phoneNumber)
            if (result.isSuccess) {
                val code = result.getOrNull()!!
                _uiState.update { 
                    it.copy(
                        isAuthenticating = false,
                        isOtpRequested = true,
                        registrationPhonePending = phoneNumber,
                        registrationPassPending = plaintextPass
                    ) 
                }
                notify("SIMULATION: Sent OTP code $code to $phoneNumber")
            } else {
                _uiState.update { 
                    it.copy(
                        isAuthenticating = false,
                        authError = result.exceptionOrNull()?.message ?: "Failed to send OTP."
                    ) 
                }
            }
        }
    }

    fun cancelOtpFlow() {
        _uiState.update { 
            it.copy(
                isOtpRequested = false,
                registrationPhonePending = "",
                registrationPassPending = "",
                authError = null
            ) 
        }
    }

    fun registerAccount(otpCode: String) {
        val phoneNumber = _uiState.value.registrationPhonePending
        val plaintextPass = _uiState.value.registrationPassPending
        
        if (otpCode.isBlank() || otpCode.length != 6) {
            _uiState.update { it.copy(authError = "Please enter a valid 6-digit OTP.") }
            return
        }
        
        _uiState.update { it.copy(isAuthenticating = true, authError = null) }
        viewModelScope.launch {
            // Actual interaction with the Backend Simulator
            val result = backend.registerUser(phoneNumber, plaintextPass, otpCode)
            if (result.isSuccess) {
                val token = result.getOrNull()!!
                secureSessionToken = token
                val meResult = backend.getMe(token)
                if (meResult.isSuccess) {
                    val me = meResult.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            isAuthenticating = false,
                            isAuthenticated = true,
                            isOtpRequested = false,
                            accountId = me.monetaId,
                            username = me.username,
                            walletBalance = me.availableBalance
                        ) 
                    }
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isAuthenticating = false,
                        authError = result.exceptionOrNull()?.message ?: "Registration failed."
                    ) 
                }
            }
        }
    }
    
    fun loginAccount(phoneNumber: String, plaintextPass: String) {
        if (phoneNumber.isBlank() || phoneNumber.length < 7) {
            _uiState.update { it.copy(authError = "Please enter a valid phone number.") }
            return
        }
        _uiState.update { it.copy(isAuthenticating = true, authError = null) }
        viewModelScope.launch {
            // Actual interaction with Backend Simulator
            val result = backend.loginUser(phoneNumber, plaintextPass)
            if (result.isSuccess) {
                val token = result.getOrNull()!!
                secureSessionToken = token
                val meResult = backend.getMe(token)
                if (meResult.isSuccess) {
                    val me = meResult.getOrNull()!!
                    _uiState.update { 
                        it.copy(
                            isAuthenticating = false,
                            isAuthenticated = true,
                            accountId = me.monetaId,
                            username = me.username,
                            walletBalance = me.availableBalance
                        ) 
                    }
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isAuthenticating = false,
                        authError = result.exceptionOrNull()?.message ?: "Invalid credentials."
                    ) 
                }
            }
        }
    }

    fun logout() {
        val token = secureSessionToken
        if (token != null) {
            viewModelScope.launch { backend.logout(token) }
            secureSessionToken = null
        }
        _uiState.update { MonetaUiState() } // Reset entirely
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
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

    // --- NEW MINING SESSION SYSTEM ---

    fun selectSessionDuration(hours: Int) {
        _uiState.update { it.copy(selectedSessionHours = hours) }
    }

    fun cancelSessionSelection() {
        _uiState.update { it.copy(selectedSessionHours = null) }
    }

    fun startSelectedSession() {
        val hours = _uiState.value.selectedSessionHours ?: return
        if (_uiState.value.activeMiningSession != null) {
            notify("You already have an active mining session.")
            return
        }
        
        simulateBackendAction("Start Mining", {
            val now = System.currentTimeMillis()
            val durationMs = hours * 3600 * 1000L
            
            val baseRate = 12.5 // Base MINT per hour
            val multiplier = _uiState.value.idleMultiplier * (if (_uiState.value.activeBoosters.contains("2X MULTIPLIER")) 2.0 else 1.0) * (if (_uiState.value.activeBoosters.contains("PRO BOOST")) 5.0 else 1.0)
            val ratePerHour = baseRate * multiplier
            val estReward = ratePerHour * hours
            
            val newSession = MiningSession(
                id = "MS_\${now}",
                durationHours = hours,
                startedAt = now,
                expectedEndAt = now + durationMs,
                ratePerHour = ratePerHour,
                estimatedReward = estReward,
                status = "ACTIVE"
            )
            
            _uiState.update { it.copy(
                activeMiningSession = newSession,
                selectedSessionHours = null
            )}
            
            startMiningLoop()
        }, "Mining session created and confirmed by backend.")
    }

    private fun startMiningLoop() {
        miningJob?.cancel()
        miningJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                val session = state.activeMiningSession
                if (session != null && session.status == "ACTIVE") {
                    val now = System.currentTimeMillis()
                    if (now >= session.expectedEndAt) {
                        // Move to VERIFYING status immediately when timer completes
                        _uiState.update { it.copy(activeMiningSession = session.copy(status = "VERIFYING")) }
                        verifyReward(session)
                    }
                }
            }
        }
    }

    private fun verifyReward(session: MiningSession) {
        viewModelScope.launch {
            // Simulate backend calculation & validation
            delay(4000) 
            
            val finalReward = session.estimatedReward
            val creditedSession = session.copy(status = "CREDITED", finalReward = finalReward)
            
            addTransactionInternal("mining", finalReward, "\${session.durationHours}-Hour Mining Session")
            
            _uiState.update { state ->
                val newHistory = listOf(creditedSession) + state.miningHistory
                state.copy(
                    activeMiningSession = null,
                    miningHistory = newHistory,
                    totalEarned = state.totalEarned + finalReward
                )
            }
            notify("⛏ MINING COMPLETE. Reward verified: +\${String.format(java.util.Locale.US, "%.2f", finalReward)} MINT")
        }
    }

    fun toggleHistoryModal(show: Boolean) {
        _uiState.update { it.copy(showHistoryModal = show) }
    }

    // --- MODULE 2: PAYMENT INTENT SYSTEM ---
    fun createPaymentIntent(productId: String, network: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(paymentIntentLoading = true, boostError = null) }
            delay(1500) // Simulate backend validation and generation
            val isBnb = network == "BNB Smart Chain"
            val priceUsd = if (productId == "AUTO_MINING_30D") 9.0 else 19.0
            val name = if (productId == "AUTO_MINING_30D") "AUTO-MINING" else "PRO BOOST"
            val asset = if (isBnb) "BNB" else "ETH"
            val chainId = if (isBnb) 56 else 1
            val cryptoAmount = if (isBnb) (priceUsd / 550.0) else (priceUsd / 3200.0) 
            
            val intent = PaymentIntent(
                intentId = "PI_\${System.currentTimeMillis()}",
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
                expiresAt = System.currentTimeMillis() + (10 * 60 * 1000L)
            )

            _uiState.update { 
                it.copy(paymentIntentLoading = false, activePaymentIntent = intent) 
            }
        }
    }

    fun submitPaymentTransaction(txHash: String) {
        val intent = _uiState.value.activePaymentIntent ?: return
        if (intent.status != "CREATED") return

        viewModelScope.launch {
            // Step 1: Detect
            _uiState.update { it.copy(activePaymentIntent = intent.copy(status = "PAYMENT_DETECTED")) }
            delay(2000)
            
            // Step 2: Verify Blockchain
            _uiState.update { it.copy(activePaymentIntent = intent.copy(status = "VERIFYING_BLOCKCHAIN")) }
            delay(4000) 
            
            // Step 3: Confirmed
            val confirmedIntent = intent.copy(status = "CONFIRMED")
            _uiState.update { it.copy(activePaymentIntent = confirmedIntent) }
            
            // Step 4: Issue Entitlement
            if (intent.productId == "AUTO_MINING_30D") {
                val expiry = System.currentTimeMillis() + (30 * 24L * 3600L * 1000L)
                _uiState.update { it.copy(autoMiningActive = true, autoMiningExpires = expiry) }
            } else if (intent.productId == "PRO_BOOST_30D") {
                _uiState.update { 
                    val newBoosters = it.activeBoosters.toMutableList()
                    if (!newBoosters.contains("PRO BOOST")) newBoosters.add("PRO BOOST")
                    it.copy(activeBoosters = newBoosters) 
                }
            }
            
            addTransactionInternal(
                type = "subscription",
                amount = -intent.expectedAmountCrypto,
                description = "\${intent.productName} 30 Days (Verified on \${intent.expectedNetwork})",
                network = intent.expectedNetwork,
                txHash = txHash
            )
            notify("Payment verified! \${intent.productName} activated.")
        }
    }

    fun clearPaymentIntent() {
        _uiState.update { it.copy(activePaymentIntent = null, paymentIntentLoading = false) }
    }

    fun withdrawMNT(amount: Double) {
        val minWithdrawal = 1000.0
        val withdrawalFee = 10.0
        val currentState = _uiState.value
        _uiState.update { it.copy(withdrawError = null) }
        
        if (amount < minWithdrawal) {
            _uiState.update { it.copy(withdrawError = "Minimum withdrawal is \$minWithdrawal MNT") }
            return
        }
        if (!currentState.isWalletConnected || currentState.walletAddress.isBlank()) {
            _uiState.update { it.copy(withdrawError = "Please connect Trust Wallet first") }
            return
        }
        val totalDeduct = amount + withdrawalFee
        if (currentState.walletBalance < totalDeduct) {
            _uiState.update { it.copy(withdrawError = "Insufficient authoritative balance") }
            return
        }
        
        simulateBackendAction("Withdraw MNT", {
            addTransactionInternal("withdraw", -totalDeduct, "Withdrawal to \${currentState.walletAddress.take(6)}... (fee \$withdrawalFee MNT)", network = currentState.connectedNetwork)
        }, "Withdrawal of \$amount MNT initiated successfully.")
    }

    fun loadPreferences(context: Context) {
        // Obsolete local loading mechanism; now uses backend token
    }

    fun purchaseWithMint(cost: Double, itemName: String) {
        val currentState = _uiState.value
        if (currentState.walletBalance >= cost) {
            simulateBackendAction("Purchase \$itemName", {
                addTransactionInternal("purchase", -cost, itemName)
                _uiState.update { 
                    it.copy(
                        boostError = null,
                        activeBoosters = it.activeBoosters + itemName
                    ) 
                }
            }, "\$itemName activated. \${cost} MINT deducted.")
        } else {
            _uiState.update {
                it.copy(boostError = "Insufficient authoritative MINT balance.")
            }
        }
    }

    fun clearBoostMessages() {
        _uiState.update { it.copy(boostError = null) }
    }

    // --- MODULE 3: REFERRALS AND FRIENDS ---
    fun fetchReferralDashboard() {
        val token = secureSessionToken ?: return
        
        _uiState.update { it.copy(leaderboardLoading = true) }
        viewModelScope.launch {
            val result = backend.getReferralDashboard(token)
            if (result.isSuccess) {
                val data = result.getOrNull()!!
                _uiState.update { 
                    it.copy(
                        referralDashboard = data,
                        leaderboardLoading = false
                    ) 
                }
            } else {
                _uiState.update { it.copy(leaderboardLoading = false) }
            }
        }
    }
    
    fun toggleDarkMode(context: Context) {
        // Toggle locally for now
        _uiState.update { currentState ->
            val newValue = !currentState.isDarkMode
            currentState.copy(isDarkMode = newValue)
        }
    }
    
    fun connectTrustWallet(context: Context, network: String) {
        val chainId = if (network == "BNB Smart Chain") 56 else 1
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("trust://"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            _uiState.update { it.copy(addressError = "Trust Wallet is not installed on this device.") }
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
        }, "Trust Wallet connection synchronized via \$network.") { error ->
            _uiState.update { it.copy(addressError = error) }
        }
    }
    
    fun disconnectWallet() {
        _uiState.update { 
            it.copy(isWalletConnected = false, walletAddress = "", connectedNetwork = null, connectedChainId = null, nativeAssetBalance = 0.0) 
        }
        notify("Trust Wallet disconnected.")
    }

    fun clearMessages() {
        _uiState.update { it.copy(withdrawError = null, addressError = null, minerUpgradeError = null) }
    }
}
