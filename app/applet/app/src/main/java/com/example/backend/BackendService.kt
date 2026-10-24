package com.example.backend

import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

// This class simulates the strict backend API server.
class BackendService(private val dao: MonetaDao) {

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun generateUniqueMonetaId(): String {
        var monetaId: String
        do {
            val randomString = (1..8).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString("")
            monetaId = "MNT-$randomString"
        } while (dao.getUserByMonetaId(monetaId) != null)
        return monetaId
    }

    // POST /auth/register/otp
    suspend fun requestRegistrationOtp(phoneNumber: String): Result<String> = withContext(Dispatchers.IO) {
        val existing = dao.getUserByPhone(phoneNumber)
        if (existing != null) return@withContext Result.failure(Exception("Phone number already registered"))
        
        val code = (100000..999999).random().toString()
        val expiresAt = System.currentTimeMillis() + (10 * 60 * 1000L) // 10 minutes
        
        dao.insertOtp(OtpRecordEntity(phoneNumber, code, expiresAt))
        
        Result.success(code)
    }

    // POST /auth/register
    suspend fun registerUser(phoneNumber: String, plaintextPass: String, otpCode: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val validOtp = dao.getValidOtp(phoneNumber, System.currentTimeMillis())
            if (validOtp == null || validOtp.otpCode != otpCode) {
                return@withContext Result.failure(Exception("Invalid or expired OTP code"))
            }

            val existing = dao.getUserByPhone(phoneNumber)
            if (existing != null) return@withContext Result.failure(Exception("Phone number already registered"))

            val uuid = UUID.randomUUID().toString()
            val monetaId = generateUniqueMonetaId()

            val now = System.currentTimeMillis()
            val user = UserEntity(uuid, monetaId, phoneNumber, hashPassword(plaintextPass), "ACTIVE", now, now)
            val profile = ProfileEntity(userId = uuid, username = "User-${phoneNumber.takeLast(4)}", createdAt = now, updatedAt = now)
            val balance = BalanceEntity(userId = uuid, availableBalance = 0.0, lockedBalance = 0.0, createdAt = now, updatedAt = now)
            val settings = SettingsEntity(userId = uuid, createdAt = now, updatedAt = now)
            val audit = AuditEventEntity(userId = uuid, eventType = "ACCOUNT_CREATED", details = "User registered successfully", timestamp = now)
            val referralStat = ReferralStatEntity(userId = uuid, updatedAt = now)

            dao.registerNewUserAtomically(user, profile, balance, settings, audit, referralStat)
            
            // Clean up OTP
            dao.deleteOtp(phoneNumber)

            // Seed mock users so the leaderboard isn't empty
            seedMockLeaderboard()

            // Auto-login logic
            val sessionToken = UUID.randomUUID().toString()
            val session = SecuritySessionEntity(sessionToken, uuid, now + (24 * 3600 * 1000L), false, now)
            dao.insertSession(session)
            dao.insertAuditEvent(AuditEventEntity(userId = uuid, eventType = "LOGIN_SUCCESS", details = "Auto-login after registration", timestamp = now))

            Result.success(sessionToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // POST /auth/login
    suspend fun loginUser(phoneNumber: String, plaintextPass: String): Result<String> = withContext(Dispatchers.IO) {
        val user = dao.getUserByPhone(phoneNumber) ?: return@withContext Result.failure(Exception("Invalid credentials"))
        val now = System.currentTimeMillis()
        if (user.passwordHash != hashPassword(plaintextPass)) {
            dao.insertAuditEvent(AuditEventEntity(userId = user.internalUuid, eventType = "LOGIN_FAILURE", details = "Invalid password", timestamp = now))
            return@withContext Result.failure(Exception("Invalid credentials"))
        }

        if (user.status != "ACTIVE") {
            return@withContext Result.failure(Exception("Account is ${user.status}"))
        }

        val sessionToken = UUID.randomUUID().toString()
        val session = SecuritySessionEntity(sessionToken, user.internalUuid, now + (24 * 3600 * 1000L), false, now)
        dao.insertSession(session)
        dao.insertAuditEvent(AuditEventEntity(userId = user.internalUuid, eventType = "LOGIN_SUCCESS", details = "User logged in", timestamp = now))

        Result.success(sessionToken)
    }

    // POST /auth/logout
    suspend fun logout(sessionToken: String) = withContext(Dispatchers.IO) {
        val session = dao.getValidSession(sessionToken, System.currentTimeMillis())
        if (session != null) {
            dao.revokeSession(sessionToken)
            dao.insertAuditEvent(AuditEventEntity(userId = session.userId, eventType = "LOGOUT", details = "User logged out securely", timestamp = System.currentTimeMillis()))
        }
    }

    // GET /me
    suspend fun getMe(sessionToken: String): Result<MeResponse> = withContext(Dispatchers.IO) {
        val session = dao.getValidSession(sessionToken, System.currentTimeMillis()) 
            ?: return@withContext Result.failure(Exception("Unauthorized or expired session"))

        val user = dao.getUserById(session.userId) ?: return@withContext Result.failure(Exception("User not found"))
        val profile = dao.getProfile(session.userId) ?: return@withContext Result.failure(Exception("Profile not found"))
        val balance = dao.getBalance(session.userId) ?: return@withContext Result.failure(Exception("Balance not found"))
        val settings = dao.getSettings(session.userId) ?: return@withContext Result.failure(Exception("Settings not found"))

        Result.success(
            MeResponse(
                monetaId = user.monetaId,
                phoneNumber = user.phoneNumber,
                status = user.status,
                username = profile.username,
                minerLevel = profile.minerLevel,
                availableBalance = balance.availableBalance,
                isDarkMode = settings.isDarkMode
            )
        )
    }

    data class MeResponse(
        val monetaId: String,
        val phoneNumber: String,
        val status: String,
        val username: String,
        val minerLevel: Int,
        val availableBalance: Double,
        val isDarkMode: Boolean
    )

    // Leaderboard Models
    data class LeaderboardDashboard(
        val myRank: Int,
        val myStats: ReferralStatEntity,
        val leaderboard: List<MonetaDao.LeaderboardEntry>,
        val recentReferrals: List<ReferralRecordEntity>
    )

    // GET /referrals/dashboard
    suspend fun getReferralDashboard(sessionToken: String): Result<LeaderboardDashboard> = withContext(Dispatchers.IO) {
        val session = dao.getValidSession(sessionToken, System.currentTimeMillis()) 
            ?: return@withContext Result.failure(Exception("Unauthorized or expired session"))

        val stats = dao.getReferralStats(session.userId) ?: return@withContext Result.failure(Exception("Stats not found"))
        val rank = dao.calculateRank(session.userId)
        val globalLeaderboard = dao.getGlobalLeaderboard(100)
        val recentReferrals = dao.getReferralRecords(session.userId)

        Result.success(LeaderboardDashboard(rank, stats, globalLeaderboard, recentReferrals))
    }

    private suspend fun seedMockLeaderboard() {
        // Seeds a few top players so the leaderboard isn't empty on a fresh install.
        if (dao.getGlobalLeaderboard(1).isNotEmpty()) return

        val mockScores = listOf(124, 97, 84, 72, 68, 45, 30, 22, 19, 11)
        for (i in mockScores.indices) {
            val uuid = UUID.randomUUID().toString()
            val monetaId = "MNT-TOP${i}X${mockScores[i]}"
            val user = UserEntity(uuid, monetaId, "mock${i}@example.com", "hash", "ACTIVE", 0L, 0L)
            dao.insertUser(user)
            dao.insertReferralStat(ReferralStatEntity(userId = uuid, qualifiedCount = mockScores[i], updatedAt = System.currentTimeMillis()))
        }
    }
}
