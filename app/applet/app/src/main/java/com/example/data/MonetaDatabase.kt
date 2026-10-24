package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ProfileEntity::class,
        BalanceEntity::class,
        SettingsEntity::class,
        SecuritySessionEntity::class,
        AuditEventEntity::class,
        ReferralRecordEntity::class,
        ReferralStatEntity::class,
        OtpRecordEntity::class
    ],
    version = 4, // increment version since schema changed to phone number
    exportSchema = false
)
abstract class MonetaDatabase : RoomDatabase() {
    abstract fun monetaDao(): MonetaDao

    companion object {
        @Volatile
        private var Instance: MonetaDatabase? = null

        fun getDatabase(context: Context): MonetaDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, MonetaDatabase::class.java, "moneta_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
