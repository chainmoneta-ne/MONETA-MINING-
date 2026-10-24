package com.example.data

import androidx.room.*

@Dao
interface MonetaDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Insert
    suspend fun insertProfile(profile: ProfileEntity)

    @Insert
    suspend fun insertBalance(balance: BalanceEntity)

    @Insert
    suspend fun insertSettings(settings: SettingsEntity)

    @Insert
    suspend fun insertSession(session: SecuritySessionEntity)

    @Insert
    suspend fun insertAuditEvent(event: AuditEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferralStat(stat: ReferralStatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferralRecord(record: ReferralRecordEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferralStats(stats: List<ReferralStatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOtp(otp: OtpRecordEntity)

    @Query("SELECT * FROM otp_records WHERE phoneNumber = :phoneNumber AND expiresAt > :currentTime LIMIT 1")
    suspend fun getValidOtp(phoneNumber: String, currentTime: Long): OtpRecordEntity?

    @Query("DELETE FROM otp_records WHERE phoneNumber = :phoneNumber")
    suspend fun deleteOtp(phoneNumber: String)

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhone(phoneNumber: String): UserEntity?

    @Query("SELECT * FROM users WHERE monetaId = :monetaId LIMIT 1")
    suspend fun getUserByMonetaId(monetaId: String): UserEntity?

    @Query("SELECT * FROM security_sessions WHERE sessionToken = :token AND revoked = 0 AND expiresAt > :currentTime LIMIT 1")
    suspend fun getValidSession(token: String, currentTime: Long): SecuritySessionEntity?

    @Query("UPDATE security_sessions SET revoked = 1 WHERE sessionToken = :token")
    suspend fun revokeSession(token: String)
    
    @Query("SELECT * FROM users WHERE internalUuid = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: String): ProfileEntity?

    @Query("SELECT * FROM balances WHERE userId = :userId LIMIT 1")
    suspend fun getBalance(userId: String): BalanceEntity?

    @Query("SELECT * FROM settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettings(userId: String): SettingsEntity?

    @Query("SELECT * FROM referral_stats WHERE userId = :userId LIMIT 1")
    suspend fun getReferralStats(userId: String): ReferralStatEntity?

    @Query("SELECT * FROM referral_records WHERE referrerId = :userId ORDER BY createdAt DESC")
    suspend fun getReferralRecords(userId: String): List<ReferralRecordEntity>

    // Join users and stats to get the leaderboard.
    // In production, we'd rank by qualifiedCount DESC, then by earliest achieved.
    // For this prototype, ordering by qualifiedCount DESC is sufficient.
    data class LeaderboardEntry(
        val monetaId: String,
        val qualifiedCount: Int
    )

    @Query("SELECT u.monetaId, s.qualifiedCount FROM users u INNER JOIN referral_stats s ON u.internalUuid = s.userId ORDER BY s.qualifiedCount DESC LIMIT :limit")
    suspend fun getGlobalLeaderboard(limit: Int): List<LeaderboardEntry>
    
    // Calculates a dynamic rank by counting how many users have strictly more qualified referrals.
    @Query("SELECT COUNT(*) + 1 FROM referral_stats WHERE qualifiedCount > (SELECT qualifiedCount FROM referral_stats WHERE userId = :userId)")
    suspend fun calculateRank(userId: String): Int

    @Transaction
    suspend fun registerNewUserAtomically(
        user: UserEntity,
        profile: ProfileEntity,
        balance: BalanceEntity,
        settings: SettingsEntity,
        auditEvent: AuditEventEntity,
        referralStat: ReferralStatEntity
    ) {
        insertUser(user)
        insertProfile(profile)
        insertBalance(balance)
        insertSettings(settings)
        insertAuditEvent(auditEvent)
        insertReferralStat(referralStat)
    }
}
