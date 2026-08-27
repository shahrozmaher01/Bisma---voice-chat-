package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        User::class,
        VoiceRoom::class,
        RoomSeat::class,
        ChatMessage::class,
        MomentPost::class,
        NotificationItem::class,
        VisitorRecord::class,
        StoreItem::class,
        Agency::class,
        Family::class,
        WalletTransaction::class,
        Friendship::class,
        Follow::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BismaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun roomDao(): RoomDao
    abstract fun seatDao(): SeatDao
    abstract fun chatDao(): ChatDao
    abstract fun momentDao(): MomentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun visitorDao(): VisitorDao
    abstract fun storeDao(): StoreDao
    abstract fun agencyFamilyDao(): AgencyFamilyDao
    abstract fun walletTransactionDao(): WalletTransactionDao
    abstract fun socialDao(): SocialDao

    companion object {
        @Volatile
        private var INSTANCE: BismaDatabase? = null

        fun getDatabase(context: Context): BismaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BismaDatabase::class.java,
                    "bisma_voice_chat.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
