package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val avatarUrl: String,
    val bio: String = "Hello, I am using Bisma Voice Chat! ✨",
    val country: String = "🇵🇰 Pakistan",
    val language: String = "English",
    val gender: String = "Female", // "Male" or "Female"
    val dateOfBirth: String = "2002-05-14",
    val passwordHash: String = "",
    val email: String? = null,
    val userLevel: Int = 1,
    val richLevel: Int = 0,
    val charmLevel: Int = 0,
    val vipLevel: Int = 0, // 0 = Not Active, 1..7 VIP tiers
    val coins: Long = 1000,
    val diamonds: Long = 50,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val friendsCount: Int = 0,
    val isOnline: Boolean = true,
    val cpPartnerId: String? = null,
    val cpPartnerName: String? = null,
    val cpPartnerAvatar: String? = null,
    val cpLevel: Int = 0,
    val agencyId: String? = null,
    val agencyName: String? = null,
    val familyId: String? = null,
    val familyName: String? = null,
    val equippedFrameId: String? = "frame_neon_circle",
    val equippedHeadwearId: String? = null,
    val equippedBubbleId: String? = null,
    val equippedEntryEffectId: String? = null,
    val equippedSoundWaveId: String? = null,
    val isBanned: Boolean = false,
    val privacyFollowersOnly: Boolean = false,
    val privacyFriendsOnlyMsg: Boolean = true,
    val privacyVisitorsHidden: Boolean = false
)

@Entity(tableName = "rooms")
data class VoiceRoom(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val ownerId: String,
    val ownerName: String,
    val ownerAvatar: String,
    val ownerVip: Int = 0,
    val country: String = "🇵🇰 Pakistan",
    val seatCount: Int = 8, // 8, 10, 15, 20
    val isLocked: Boolean = false,
    val password: String = "",
    val announcement: String = "Welcome to our Bisma Voice Chat Room! Please be respectful and enjoy the music & voice interactions.",
    val onlineCount: Int = 1,
    val backgroundRes: String = "bg_neon_purple",
    val category: String = "Singing & Chill",
    val isFeatured: Boolean = false,
    val allowPublicChat: Boolean = true,
    val heartbeatValueDisplay: Boolean = true,
    val soundMuted: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val musicTrackName: String = "Neon Lo-Fi Chill Beats 🎧",
    val blockedUserIds: String = "",
    val adminUserIds: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

@Entity(tableName = "room_seats", primaryKeys = ["roomId", "seatIndex"])
data class RoomSeat(
    val roomId: String,
    val seatIndex: Int,
    val userId: String? = null,
    val username: String? = null,
    val avatarUrl: String? = null,
    val vipLevel: Int = 0,
    val userLevel: Int = 1,
    val frameId: String? = null,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isSpeaking: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey val id: String,
    val targetId: String, // roomId or peer userId
    val isRoomChat: Boolean,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val senderVip: Int = 0,
    val content: String,
    val giftName: String? = null,
    val giftIcon: String? = null,
    val giftCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "moments")
data class MomentPost(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val authorVip: Int = 0,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // "friend_request", "follow", "visitor", "gift", "system", "cp_request", "invite"
    val title: String,
    val message: String,
    val senderId: String? = null,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "visitors")
data class VisitorRecord(
    @PrimaryKey val id: String,
    val targetUserId: String,
    val visitorId: String,
    val visitorName: String,
    val visitorAvatar: String,
    val visitorVip: Int = 0,
    val visitedAt: Long = System.currentTimeMillis(),
    val isFollowing: Boolean = false,
    val isFriend: Boolean = false
)

@Entity(tableName = "store_items")
data class StoreItem(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // Frames, Headwear, Entry Effects, Chat Bubbles, Sound Waves, Room Backgrounds
    val price: Long,
    val previewIcon: String,
    val frameColorHex: Long = 0xFFFF2A85,
    val isPermanent: Boolean = true,
    val durationDays: Int = 30,
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false,
    val description: String = "Exclusive luxury cosmetic for your profile and chat seats."
)

@Entity(tableName = "agencies")
data class Agency(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String,
    val ownerId: String,
    val ownerName: String,
    val agencyCode: String = "",
    val bdId: String = "BD_OFFICIAL",
    val memberCount: Int = 1,
    val level: Int = 1,
    val announcement: String = "Welcome to our premier Bisma Voice Agency!",
    val totalIncome: Long = 50000,
    val ranking: Int = 1
) {
    val description: String get() = announcement
}

@Entity(tableName = "agency_join_requests")
data class AgencyJoinRequest(
    @PrimaryKey val id: String,
    val agencyId: String,
    val agencyName: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val status: String = "pending", // "pending", "accepted", "rejected"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "agency_invitations")
data class AgencyInvitation(
    @PrimaryKey val id: String,
    val agencyId: String,
    val agencyName: String,
    val agencyLogo: String,
    val inviterId: String,
    val inviterName: String,
    val inviteeId: String,
    val status: String = "pending", // "pending", "accepted", "rejected"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "moment_comments")
data class MomentComment(
    @PrimaryKey val id: String,
    val momentId: String,
    val authorId: String,
    val authorName: String,
    val authorAvatar: String,
    val authorVip: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cp_relationships", primaryKeys = ["user1Id", "user2Id"])
data class CpRelationship(
    val user1Id: String,
    val user1Name: String,
    val user1Avatar: String,
    val user2Id: String,
    val user2Name: String,
    val user2Avatar: String,
    val intimacyScore: Long = 1250,
    val cpLevel: Int = 1,
    val ringName: String = "Eternal Diamond Band 💍",
    val establishedAt: Long = System.currentTimeMillis()
)

data class LuckyBagEvent(
    val id: String,
    val roomId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val totalCoins: Long,
    val remainingCoins: Long,
    val claimedCount: Int = 0,
    val maxClaims: Int = 10,
    val timestamp: Long = System.currentTimeMillis()
)

data class RoomEmojiEvent(
    val id: String,
    val roomId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val seatIndex: Int,
    val emoji: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "families")
data class Family(
    @PrimaryKey val id: String,
    val name: String,
    val logoUrl: String,
    val leaderId: String,
    val leaderName: String,
    val memberCount: Int = 1,
    val level: Int = 1,
    val announcement: String = "One Family, One Voice! Welcome to Bisma Family community.",
    val score: Long = 25000,
    val ranking: Int = 1
)

@Entity(tableName = "wallet_transactions")
data class WalletTransaction(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // "Recharge", "Gift Sent", "Gift Received", "Diamond Exchange", "Store Purchase", "Game Win"
    val amountCoins: Long = 0,
    val amountDiamonds: Long = 0,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "friendships", primaryKeys = ["userId", "friendId"])
data class Friendship(
    val userId: String,
    val friendId: String,
    val status: String = "accepted", // "pending", "accepted"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "follows", primaryKeys = ["followerId", "followingId"])
data class Follow(
    val followerId: String,
    val followingId: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class VirtualGift(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val costCoins: Long,
    val charmPoints: Int,
    val animationEffect: String
)

data class PromoBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val iconEmoji: String,
    val actionRoute: String
)

@Entity(tableName = "user_roles")
data class UserRoleAssignment(
    @PrimaryKey val userId: String,
    val username: String = "",
    val role: String = "User", // "Super Admin", "Admin", "Manager", "BD", "Agency", "Coin Reseller", "User"
    val assignedBy: String = "System",
    val assignedByName: String = "System",
    val assignedAt: Long = System.currentTimeMillis(),
    val permissions: String = "", // Comma-separated list of granular permissions
    val assignedArea: String? = null, // e.g. Room ID or Agency ID
    val notes: String? = null
)

@Entity(tableName = "app_configs")
data class AppConfigEntity(
    @PrimaryKey val key: String,
    val value: String,
    val category: String = "general", // "general", "branding", "features", "rooms", "chat", "security"
    val updatedAt: Long = System.currentTimeMillis(),
    val updatedBy: String = "Super Admin"
)

@Entity(tableName = "moderation_reports")
data class ReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val reporterName: String,
    val targetType: String, // "User", "Room", "Chat", "Moment"
    val targetId: String,
    val targetTitleOrName: String,
    val reason: String,
    val details: String = "",
    val status: String = "Pending", // "Pending", "Resolved", "Dismissed"
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolvedBy: String? = null,
    val resolutionNotes: String? = null
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val adminId: String,
    val adminName: String,
    val adminRole: String,
    val action: String, // e.g. "ROLE_CHANGE", "BAN_USER", "UNBAN_USER", "COIN_ADJUSTMENT", "APP_CONFIG_UPDATE", "BRANDING_UPDATE", "ROOM_MODERATION", "REPORT_RESOLVE"
    val targetType: String, // "User", "Room", "Config", "Role", "Report"
    val targetId: String,
    val targetName: String,
    val previousValue: String? = null,
    val newValue: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true,
    val ipAddress: String? = "127.0.0.1"
)
