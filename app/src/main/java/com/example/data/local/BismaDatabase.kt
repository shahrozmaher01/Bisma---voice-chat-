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
        MomentComment::class,
        NotificationItem::class,
        VisitorRecord::class,
        StoreItem::class,
        Agency::class,
        AgencyJoinRequest::class,
        AgencyInvitation::class,
        CpRelationship::class,
        Family::class,
        WalletTransaction::class,
        Friendship::class,
        Follow::class,
        UserRoleAssignment::class,
        AppConfigEntity::class,
        ReportEntity::class,
        AuditLogEntity::class,
        AdminLinkUser::class,
        OfficialFrameAssignment::class,
        FeedbackItem::class,
        RechargePackage::class,
        WithdrawalRequest::class,
        CurrencyConfig::class
    ],
    version = 8,
    exportSchema = false
)
abstract class BismaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun roomDao(): RoomDao
    abstract fun seatDao(): SeatDao
    abstract fun chatDao(): ChatDao
    abstract fun momentDao(): MomentDao
    abstract fun momentCommentDao(): MomentCommentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun visitorDao(): VisitorDao
    abstract fun storeDao(): StoreDao
    abstract fun agencyFamilyDao(): AgencyFamilyDao
    abstract fun agencyInteractionDao(): AgencyInteractionDao
    abstract fun cpDao(): CpDao
    abstract fun walletTransactionDao(): WalletTransactionDao
    abstract fun socialDao(): SocialDao
    abstract fun userRoleDao(): UserRoleDao
    abstract fun appConfigDao(): AppConfigDao
    abstract fun reportDao(): ReportDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun adminLinkUserDao(): AdminLinkUserDao
    abstract fun officialFrameDao(): OfficialFrameDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun rechargePackageDao(): RechargePackageDao
    abstract fun withdrawalRequestDao(): WithdrawalRequestDao
    abstract fun currencyConfigDao(): CurrencyConfigDao


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
