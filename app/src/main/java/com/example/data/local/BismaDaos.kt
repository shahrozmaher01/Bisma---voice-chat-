package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users ORDER BY richLevel DESC, coins DESC LIMIT 20")
    fun getTopWealthUsers(): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY charmLevel DESC LIMIT 20")
    fun getTopCharmUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR id = :query")
    suspend fun searchUsers(query: String): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    @Query("UPDATE users SET coins = coins + :deltaCoins, diamonds = diamonds + :deltaDiamonds WHERE id = :userId")
    suspend fun updateBalance(userId: String, deltaCoins: Long, deltaDiamonds: Long)

    @Query("UPDATE users SET richLevel = :rich, charmLevel = :charm WHERE id = :userId")
    suspend fun updateLevels(userId: String, rich: Int, charm: Int)

    @Query("UPDATE users SET vipLevel = :vip WHERE id = :userId")
    suspend fun updateVip(userId: String, vip: Int)

    @Query("UPDATE users SET equippedFrameId = :frameId WHERE id = :userId")
    suspend fun updateEquippedFrame(userId: String, frameId: String?)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE id = :id")
    suspend fun countUserById(id: String): Int

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updatePassword(userId: String, passwordHash: String)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
}

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms WHERE isActive = 1 ORDER BY onlineCount DESC, createdAt DESC")
    fun getAllActiveRoomsFlow(): Flow<List<VoiceRoom>>

    @Query("SELECT * FROM rooms WHERE id = :roomId LIMIT 1")
    fun getRoomByIdFlow(roomId: String): Flow<VoiceRoom?>

    @Query("SELECT * FROM rooms WHERE id = :roomId LIMIT 1")
    suspend fun getRoomById(roomId: String): VoiceRoom?

    @Query("SELECT * FROM rooms WHERE ownerId = :ownerId AND isActive = 1 LIMIT 1")
    suspend fun getRoomByOwnerId(ownerId: String): VoiceRoom?

    @Query("SELECT * FROM rooms WHERE isActive = 1 AND (title LIKE '%' || :query || '%' OR id = :query)")
    suspend fun searchRooms(query: String): List<VoiceRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(room: VoiceRoom)

    @Query("UPDATE rooms SET onlineCount = :count WHERE id = :roomId")
    suspend fun updateOnlineCount(roomId: String, count: Int)

    @Query("UPDATE rooms SET isActive = 0 WHERE id = :roomId")
    suspend fun closeRoom(roomId: String)

    @Query("DELETE FROM rooms WHERE id = :roomId")
    suspend fun deleteRoom(roomId: String)

    @Query("DELETE FROM rooms WHERE id = :roomId")
    suspend fun deleteRoomById(roomId: String)
}

@Dao
interface SeatDao {
    @Query("SELECT * FROM room_seats WHERE roomId = :roomId ORDER BY seatIndex ASC")
    fun getSeatsForRoomFlow(roomId: String): Flow<List<RoomSeat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<RoomSeat>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSeat(seat: RoomSeat)

    @Query("DELETE FROM room_seats WHERE roomId = :roomId")
    suspend fun clearSeatsForRoom(roomId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE targetId = :targetId ORDER BY timestamp ASC")
    fun getMessagesFlow(targetId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE targetId = :targetId")
    suspend fun clearMessages(targetId: String)
}

@Dao
interface MomentDao {
    @Query("SELECT * FROM moments ORDER BY timestamp DESC")
    fun getAllMomentsFlow(): Flow<List<MomentPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoment(moment: MomentPost)

    @Query("UPDATE moments SET likesCount = likesCount + :delta, isLiked = :isLiked WHERE id = :momentId")
    suspend fun toggleLike(momentId: String, delta: Int, isLiked: Boolean)

    @Query("DELETE FROM moments WHERE id = :momentId")
    suspend fun deleteMoment(momentId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsFlow(userId: String): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(item: NotificationItem)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllRead(userId: String)
}

@Dao
interface VisitorDao {
    @Query("SELECT * FROM visitors WHERE targetUserId = :targetUserId ORDER BY visitedAt DESC")
    fun getVisitorsFlow(targetUserId: String): Flow<List<VisitorRecord>>

    @Query("SELECT COUNT(*) FROM visitors WHERE targetUserId = :targetUserId")
    fun getVisitorsCountFlow(targetUserId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitor(visitor: VisitorRecord)

    @Query("DELETE FROM visitors WHERE targetUserId = :targetUserId")
    suspend fun clearVisitors(targetUserId: String)
}

@Dao
interface StoreDao {
    @Query("SELECT * FROM store_items")
    fun getAllStoreItemsFlow(): Flow<List<StoreItem>>

    @Query("SELECT * FROM store_items WHERE isOwned = 1")
    fun getBackpackItemsFlow(): Flow<List<StoreItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<StoreItem>)

    @Query("UPDATE store_items SET isOwned = 1 WHERE id = :itemId")
    suspend fun markOwned(itemId: String)

    @Query("UPDATE store_items SET isEquipped = :isEquipped WHERE id = :itemId")
    suspend fun setEquipped(itemId: String, isEquipped: Boolean)

    @Query("UPDATE store_items SET isEquipped = 0 WHERE category = :category")
    suspend fun unequipCategory(category: String)
}

@Dao
interface AgencyFamilyDao {
    @Query("SELECT * FROM agencies ORDER BY ranking ASC")
    fun getAllAgenciesFlow(): Flow<List<Agency>>

    @Query("SELECT * FROM agencies WHERE id = :id LIMIT 1")
    suspend fun getAgencyById(id: String): Agency?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgency(agency: Agency)

    @Query("DELETE FROM agencies WHERE id = :id")
    suspend fun deleteAgencyById(id: String)

    @Query("SELECT * FROM families ORDER BY ranking ASC")
    fun getAllFamiliesFlow(): Flow<List<Family>>

    @Query("SELECT * FROM families WHERE id = :id LIMIT 1")
    suspend fun getFamilyById(id: String): Family?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: Family)

    @Query("DELETE FROM families WHERE id = :id")
    suspend fun deleteFamilyById(id: String)
}

@Dao
interface WalletTransactionDao {
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsFlow(userId: String): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction)
}

@Dao
interface SocialDao {
    @Query("SELECT * FROM friendships WHERE (userId = :userId OR friendId = :userId) AND status = 'accepted'")
    fun getFriendsFlow(userId: String): Flow<List<Friendship>>

    @Query("SELECT COUNT(*) FROM friendships WHERE (userId = :userId OR friendId = :userId) AND status = 'accepted'")
    fun getFriendsCountFlow(userId: String): Flow<Int>

    @Query("SELECT * FROM friendships WHERE friendId = :userId AND status = 'pending'")
    fun getPendingFriendRequestsFlow(userId: String): Flow<List<Friendship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: Friendship)

    @Query("DELETE FROM friendships WHERE (userId = :u1 AND friendId = :u2) OR (userId = :u2 AND friendId = :u1)")
    suspend fun deleteFriendship(u1: String, u2: String)

    @Query("SELECT COUNT(*) > 0 FROM friendships WHERE ((userId = :u1 AND friendId = :u2) OR (userId = :u2 AND friendId = :u1)) AND status = 'accepted'")
    suspend fun areFriends(u1: String, u2: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFollow(follow: Follow)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun deleteFollow(followerId: String, followingId: String)

    @Query("SELECT COUNT(*) > 0 FROM follows WHERE followerId = :followerId AND followingId = :followingId")
    suspend fun isFollowing(followerId: String, followingId: String): Boolean

    @Query("SELECT * FROM follows WHERE followerId = :userId")
    fun getFollowingFlow(userId: String): Flow<List<Follow>>

    @Query("SELECT COUNT(*) FROM follows WHERE followerId = :userId")
    fun getFollowingCountFlow(userId: String): Flow<Int>

    @Query("SELECT * FROM follows WHERE followingId = :userId")
    fun getFollowersFlow(userId: String): Flow<List<Follow>>

    @Query("SELECT COUNT(*) FROM follows WHERE followingId = :userId")
    fun getFollowersCountFlow(userId: String): Flow<Int>
}
