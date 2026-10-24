package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["monetaId"], unique = true), Index(value = ["phoneNumber"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val internalUuid: String,
    val monetaId: String,
    val phoneNumber: String,
    val passwordHash: String,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "profiles",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["internalUuid"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId", unique = true)]
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val username: String,
    val minerLevel: Int = 1,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "balances",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["internalUuid"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId", unique = true)]
)
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val asset: String = "MNT",
    val availableBalance: Double = 0.0,
    val lockedBalance: Double = 0.0,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "settings",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["internalUuid"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId", unique = true)]
)
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val isDarkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "security_sessions",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["internalUuid"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("sessionToken", unique = true), Index("userId")]
)
data class SecuritySessionEntity(
    @PrimaryKey val sessionToken: String,
    val userId: String,
    val expiresAt: Long,
    val revoked: Boolean = false,
    val createdAt: Long
)

@Entity(
    tableName = "otp_records",
    indices = [Index("phoneNumber", unique = true)]
)
data class OtpRecordEntity(
    @PrimaryKey val phoneNumber: String,
    val otpCode: String,
    val expiresAt: Long
)

data class AuditEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val eventType: String,
    val details: String,
    val timestamp: Long
)

@Entity(
    tableName = "referral_records",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["internalUuid"], childColumns = ["referrerId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("referrerId"), Index("referredMonetaId")]
)
data class ReferralRecordEntity(
    @PrimaryKey val id: String, // e.g. REF_UUID
    val referrerId: String,
    val referredMonetaId: String, // Storing MonetaID to avoid exposing internal UUIDs of referred users
    val status: String, // INVITED, REGISTERED, VERIFIED, QUALIFIED, FRAUD_REVIEW, REJECTED
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    tableName = "referral_stats",
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["internalUuid"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId", unique = true), Index("qualifiedCount")]
)
data class ReferralStatEntity(
    @PrimaryKey val userId: String,
    val invitedCount: Int = 0,
    val registeredCount: Int = 0,
    val qualifiedCount: Int = 0,
    val earnedMint: Double = 0.0,
    val currentRank: Int = 0,
    val updatedAt: Long
)
