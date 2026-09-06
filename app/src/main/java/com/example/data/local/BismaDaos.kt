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

    @Query("SELECT * FROM users WHERE agencyId = :agencyId")
    fun getAgencyMembersFlow(agencyId: String): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY richLevel DESC, coins DESC LIMIT 20")
    fun getTopWealthUsers(): Flow<List<User>>

    @Query("SELECT * FROM users ORDER BY charmLevel DESC LIMIT 20")
    fun getTopCharmUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR id = :query")
    suspend fun searchUsers(query: String): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

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

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE id = :id")
    suspend fun countUserById(id: String): Int

    @Query("UPDATE users SET passwordHash = :passwordHash WHERE id = :userId")
    suspend fun updatePassword(userId: String, passwordHash: String)

    @Query("UPDATE users SET isBanned = :isBanned WHERE id = :userId")
    suspend fun updateBanStatus(userId: String, isBanned: Boolean)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users ORDER BY userLevel DESC, coins DESC")
    fun getAllUsersFlow(): Flow<List<User>>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun countTotalUsers(): Int

    @Query("SELECT COUNT(*) FROM users WHERE isBanned = 0")
    suspend fun countActiveUsers(): Int

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

    @Query("SELECT * FROM store_items")
    suspend fun getAllItems(): List<StoreItem>

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

    @Query("SELECT * FROM agencies ORDER BY ranking ASC")
    suspend fun getAllAgencies(): List<Agency>

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

    @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<WalletTransaction>

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

@Dao
interface UserRoleDao {
    @Query("SELECT * FROM user_roles WHERE userId = :userId LIMIT 1")
    fun getRoleForUserFlow(userId: String): Flow<UserRoleAssignment?>

    @Query("SELECT * FROM user_roles WHERE userId = :userId LIMIT 1")
    suspend fun getRoleForUser(userId: String): UserRoleAssignment?

    @Query("SELECT * FROM user_roles ORDER BY assignedAt DESC")
    fun getAllRolesFlow(): Flow<List<UserRoleAssignment>>

    @Query("SELECT * FROM user_roles ORDER BY assignedAt DESC")
    suspend fun getAllRoles(): List<UserRoleAssignment>

    @Query("SELECT * FROM user_roles WHERE role = :role")
    suspend fun getUsersByRole(role: String): List<UserRoleAssignment>

    @Query("SELECT COUNT(*) FROM user_roles WHERE role = :role")
    suspend fun countByRole(role: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRole(roleAssignment: UserRoleAssignment)

    @Query("DELETE FROM user_roles WHERE userId = :userId")
    suspend fun removeRoleForUser(userId: String)
}

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_configs")
    fun getAllConfigsFlow(): Flow<List<AppConfigEntity>>

    @Query("SELECT * FROM app_configs")
    suspend fun getAllConfigs(): List<AppConfigEntity>

    @Query("SELECT * FROM app_configs WHERE key = :key LIMIT 1")
    suspend fun getConfigByKey(key: String): AppConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateConfig(config: AppConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<AppConfigEntity>)
}

@Dao
interface ReportDao {
    @Query("SELECT * FROM moderation_reports ORDER BY createdAt DESC")
    fun getAllReportsFlow(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM moderation_reports ORDER BY createdAt DESC")
    suspend fun getAllReports(): List<ReportEntity>

    @Query("SELECT * FROM moderation_reports WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getReportsByStatus(status: String): List<ReportEntity>

    @Query("SELECT COUNT(*) FROM moderation_reports WHERE status = 'Pending'")
    suspend fun countPendingReports(): Int

    @Query("SELECT COUNT(*) FROM moderation_reports WHERE status = 'Pending'")
    fun countPendingReportsFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Query("UPDATE moderation_reports SET status = :status, resolvedAt = :resolvedAt, resolvedBy = :resolvedBy, resolutionNotes = :notes WHERE id = :reportId")
    suspend fun resolveReport(reportId: String, status: String, resolvedAt: Long, resolvedBy: String, notes: String)

    @Query("DELETE FROM moderation_reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentAuditLogs(limit: Int = 100): List<AuditLogEntity>

    @Query("SELECT * FROM audit_logs WHERE adminId = :adminId ORDER BY timestamp DESC LIMIT 50")
    suspend fun getLogsByAdmin(adminId: String): List<AuditLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}

@Dao
interface MomentCommentDao {
    @Query("SELECT * FROM moment_comments WHERE momentId = :momentId ORDER BY timestamp ASC")
    fun getCommentsForMomentFlow(momentId: String): Flow<List<MomentComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: MomentComment)

    @Query("DELETE FROM moment_comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)
}

@Dao
interface AgencyInteractionDao {
    @Query("SELECT * FROM agency_join_requests WHERE agencyId = :agencyId AND status = 'pending' ORDER BY timestamp DESC")
    fun getPendingRequestsForAgencyFlow(agencyId: String): Flow<List<AgencyJoinRequest>>

    @Query("SELECT * FROM agency_join_requests WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRequestsByUserFlow(userId: String): Flow<List<AgencyJoinRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJoinRequest(request: AgencyJoinRequest)

    @Query("UPDATE agency_join_requests SET status = :status WHERE id = :requestId")
    suspend fun updateRequestStatus(requestId: String, status: String)

    @Query("SELECT * FROM agency_invitations WHERE inviteeId = :userId AND status = 'pending' ORDER BY timestamp DESC")
    fun getPendingInvitationsForUserFlow(userId: String): Flow<List<AgencyInvitation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvitation(invitation: AgencyInvitation)

    @Query("UPDATE agency_invitations SET status = :status WHERE id = :invitationId")
    suspend fun updateInvitationStatus(invitationId: String, status: String)
}

@Dao
interface CpDao {
    @Query("SELECT * FROM cp_relationships WHERE user1Id = :userId OR user2Id = :userId LIMIT 1")
    fun getCpForUserFlow(userId: String): Flow<CpRelationship?>

    @Query("SELECT * FROM cp_relationships ORDER BY intimacyScore DESC LIMIT 20")
    fun getTopCpListFlow(): Flow<List<CpRelationship>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCp(cp: CpRelationship)

    @Query("DELETE FROM cp_relationships WHERE user1Id = :userId OR user2Id = :userId")
    suspend fun dissolveCp(userId: String)
}

@Dao
interface AdminLinkUserDao {
    @Query("SELECT * FROM admin_link_users WHERE adminId = :adminId ORDER BY updatedAt DESC")
    fun getLinkUsersFlow(adminId: String): Flow<List<AdminLinkUser>>

    @Query("SELECT * FROM admin_link_users WHERE adminId = :adminId ORDER BY updatedAt DESC")
    suspend fun getLinkUsers(adminId: String): List<AdminLinkUser>

    @Query("SELECT * FROM admin_link_users WHERE adminId = :adminId AND (userName LIKE '%' || :query || '%' OR userId LIKE '%' || :query || '%' OR assignedWork LIKE '%' || :query || '%') ORDER BY updatedAt DESC")
    suspend fun searchLinkUsers(adminId: String, query: String): List<AdminLinkUser>

    @Query("SELECT * FROM admin_link_users WHERE id = :id LIMIT 1")
    suspend fun getLinkUserById(id: String): AdminLinkUser?

    @Query("SELECT * FROM admin_link_users WHERE adminId = :adminId AND userId = :userId LIMIT 1")
    suspend fun getLinkUserByAdminAndUser(adminId: String, userId: String): AdminLinkUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(linkUser: AdminLinkUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(linkUsers: List<AdminLinkUser>)

    @Query("DELETE FROM admin_link_users WHERE id = :id AND adminId = :adminId")
    suspend fun deleteLinkUser(id: String, adminId: String)

    @Query("SELECT COUNT(*) FROM admin_link_users WHERE adminId = :adminId")
    suspend fun countLinkUsers(adminId: String): Int

    @Query("SELECT COUNT(*) FROM admin_link_users WHERE adminId = :adminId AND (status = 'Active' OR status = 'Online')")
    suspend fun countActiveLinkUsers(adminId: String): Int

    @Query("SELECT COUNT(*) FROM admin_link_users WHERE adminId = :adminId AND workStatus = 'Completed'")
    suspend fun countCompletedWork(adminId: String): Int
}

@Dao
interface OfficialFrameDao {
    @Query("SELECT * FROM official_frame_assignments ORDER BY sendDate DESC")
    fun getAllAssignmentsFlow(): kotlinx.coroutines.flow.Flow<List<OfficialFrameAssignment>>

    @Query("SELECT * FROM official_frame_assignments ORDER BY sendDate DESC")
    suspend fun getAllAssignments(): List<OfficialFrameAssignment>

    @Query("SELECT * FROM official_frame_assignments WHERE id = :id LIMIT 1")
    suspend fun getAssignmentById(id: String): OfficialFrameAssignment?

    @Query("SELECT * FROM official_frame_assignments WHERE userId = :userId ORDER BY sendDate DESC")
    suspend fun getAssignmentsForUser(userId: String): List<OfficialFrameAssignment>

    @Query("SELECT * FROM official_frame_assignments WHERE userId = :userId AND status = 'Active' LIMIT 1")
    suspend fun getActiveAssignmentForUser(userId: String): OfficialFrameAssignment?

    @Query("SELECT * FROM official_frame_assignments WHERE status = 'Active'")
    suspend fun getAllActiveAssignments(): List<OfficialFrameAssignment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: OfficialFrameAssignment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assignments: List<OfficialFrameAssignment>)

    @Query("UPDATE official_frame_assignments SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE official_frame_assignments SET status = 'Expired' WHERE id = :id")
    suspend fun markExpired(id: String)
}

