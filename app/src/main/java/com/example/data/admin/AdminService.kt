package com.example.data.admin

import com.example.data.local.BismaDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AdminService(
    private val db: BismaDatabase,
    val securityManager: AdminSecurityManager
) {

    init {
        // Initialize default app configs if not present
    }

    suspend fun initializeDefaultConfigs() = withContext(Dispatchers.IO) {
        val existing = db.appConfigDao().getAllConfigs()
        if (existing.isEmpty()) {
            val defaults = listOf(
                AppConfigEntity("app_name", "Bisma Live", "branding"),
                AppConfigEntity("app_logo", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300", "branding"),
                AppConfigEntity("app_icon", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300", "branding"),
                AppConfigEntity("splash_title", "Bisma Voice Live", "branding"),
                AppConfigEntity("splash_subtitle", "Connect, Voice Chat & Stream Across Communities", "branding"),
                AppConfigEntity("theme_mode", "Dark Neon", "branding"),
                AppConfigEntity("primary_accent_color", "#FF2A85", "branding"),
                AppConfigEntity("secondary_accent_color", "#FFD700", "branding"),
                AppConfigEntity("background_style", "Cyber Glow Gradient", "branding"),
                AppConfigEntity("navigation_style", "Floating Pill Bar", "branding"),
                AppConfigEntity("card_border_radius", "16px", "branding"),
                AppConfigEntity("welcome_message", "Welcome to Bisma Voice Chat! ✨ Enjoy crystal-clear audio rooms.", "general"),
                AppConfigEntity("announcement_banner", "📢 Official Notice: Welcome to Bisma Live Community! Be respectful and have fun.", "general"),
                AppConfigEntity("support_email", "support@bismalive.com", "general"),
                AppConfigEntity("support_whatsapp", "+92 300 1234567", "general"),
                AppConfigEntity("feature_voice_rooms", "true", "features"),
                AppConfigEntity("feature_live_chat", "true", "features"),
                AppConfigEntity("feature_minigames", "true", "features"),
                AppConfigEntity("feature_moments", "true", "features"),
                AppConfigEntity("feature_coin_reseller", "true", "features"),
                AppConfigEntity("feature_gift_store", "true", "features"),
                AppConfigEntity("feature_google_login", "true", "features"),
                AppConfigEntity("feature_maintenance_mode", "false", "features"),
                AppConfigEntity("maintenance_message", "Scheduled platform optimization in progress. Voice rooms will resume shortly.", "features"),
                AppConfigEntity("room_default_seat_count", "8", "rooms"),
                AppConfigEntity("room_max_seat_count", "20", "rooms"),
                AppConfigEntity("room_audio_bitrate", "64kbps", "rooms"),
                AppConfigEntity("room_noise_suppression", "true", "rooms"),
                AppConfigEntity("room_echo_cancellation", "true", "rooms"),
                AppConfigEntity("allow_new_registration", "true", "security")
            )
            db.appConfigDao().insertConfigs(defaults)
        }
    }

    suspend fun getDashboardStats(session: AdminSession): JSONObject = withContext(Dispatchers.IO) {
        val totalUsers = db.userDao().countTotalUsers()
        val activeUsers = db.userDao().countActiveUsers()
        val activeRooms = db.roomDao().searchRooms("").size
        val superAdminsCount = db.userRoleDao().countByRole(AdminRole.SUPER_ADMIN.roleName)
        val adminsCount = db.userRoleDao().countByRole(AdminRole.ADMIN.roleName)
        val managersCount = db.userRoleDao().countByRole(AdminRole.MANAGER.roleName)
        val bdsCount = db.userRoleDao().countByRole(AdminRole.BD.roleName)
        val agenciesCount = db.userRoleDao().countByRole(AdminRole.AGENCY.roleName)
        val resellersCount = db.userRoleDao().countByRole(AdminRole.COIN_RESELLER.roleName)
        val pendingReports = db.reportDao().countPendingReports()
        val recentLogs = db.auditLogDao().getRecentAuditLogs(15)

        val json = JSONObject()
        json.put("totalUsers", totalUsers)
        json.put("activeUsers", activeUsers)
        json.put("activeRooms", activeRooms)
        json.put("superAdminsCount", superAdminsCount)
        json.put("adminsCount", adminsCount)
        json.put("managersCount", managersCount)
        json.put("bdsCount", bdsCount)
        json.put("agenciesCount", agenciesCount)
        json.put("resellersCount", resellersCount)
        json.put("pendingReports", pendingReports)

        val logsArray = JSONArray()
        recentLogs.forEach { log ->
            val obj = JSONObject()
            obj.put("id", log.id)
            obj.put("adminId", log.adminId)
            obj.put("adminName", log.adminName)
            obj.put("adminRole", log.adminRole)
            obj.put("action", log.action)
            obj.put("targetType", log.targetType)
            obj.put("targetId", log.targetId)
            obj.put("targetName", log.targetName)
            obj.put("previousValue", log.previousValue ?: "")
            obj.put("newValue", log.newValue ?: "")
            obj.put("timestamp", log.timestamp)
            obj.put("isSuccess", log.isSuccess)
            logsArray.put(obj)
        }
        json.put("recentLogs", logsArray)
        json
    }

    suspend fun getAllUsers(session: AdminSession, query: String = "", roleFilter: String = ""): List<JSONObject> = withContext(Dispatchers.IO) {
        val users = if (query.isBlank()) db.userDao().getAllUsers() else db.userDao().searchUsers(query)
        val rolesMap = db.userRoleDao().getAllRoles().associateBy { it.userId }

        users.mapNotNull { u ->
            val assignedRole = rolesMap[u.id]?.role ?: "User"
            if (roleFilter.isNotEmpty() && !assignedRole.equals(roleFilter, ignoreCase = true)) {
                return@mapNotNull null
            }
            val obj = JSONObject()
            obj.put("id", u.id)
            obj.put("username", u.username)
            obj.put("avatarUrl", u.avatarUrl)
            obj.put("email", u.email ?: "")
            obj.put("gender", u.gender)
            obj.put("country", u.country)
            obj.put("coins", u.coins)
            obj.put("diamonds", u.diamonds)
            obj.put("userLevel", u.userLevel)
            obj.put("vipLevel", u.vipLevel)
            obj.put("richLevel", u.richLevel)
            obj.put("charmLevel", u.charmLevel)
            obj.put("followersCount", u.followersCount)
            obj.put("followingCount", u.followingCount)
            obj.put("friendsCount", u.friendsCount)
            obj.put("isBanned", u.isBanned)
            obj.put("role", assignedRole)
            obj.put("permissions", rolesMap[u.id]?.permissions ?: "")
            obj.put("assignedArea", rolesMap[u.id]?.assignedArea ?: "")
            obj
        }
    }

    suspend fun assignUserRole(
        session: AdminSession,
        targetUserId: String,
        newRoleStr: String,
        customPermissions: String,
        assignedArea: String?,
        notes: String?,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        val targetUser = db.userDao().getUserById(targetUserId)
            ?: return@withContext Result.failure(Exception("Target user with ID '$targetUserId' not found."))

        val currentAssignment = db.userRoleDao().getRoleForUser(targetUserId)
        val currentTargetRole = AdminRole.fromString(currentAssignment?.role)
        val newRole = AdminRole.fromString(newRoleStr)

        // Server-side hierarchy enforcement
        if (!securityManager.canModifyTargetRole(session.role, currentTargetRole, newRole)) {
            securityManager.logAction(
                session.userId, session.username, session.role.roleName,
                "ASSIGN_ROLE_FAILED_PERM", "Role", targetUserId, targetUser.username,
                currentTargetRole.roleName, newRole.roleName, false, clientIp
            )
            return@withContext Result.failure(Exception("Permission Denied: Your role (${session.role.roleName}) cannot grant, alter or revoke '${newRole.roleName}' privileges."))
        }

        // Prevent self-role modification if attempting privilege escalation
        if (session.userId == targetUserId && newRole.level > session.role.level) {
            return@withContext Result.failure(Exception("Permission Denied: You cannot escalate your own role."))
        }

        if (newRole == AdminRole.USER) {
            db.userRoleDao().removeRoleForUser(targetUserId)
        } else {
            val assignment = UserRoleAssignment(
                userId = targetUserId,
                username = targetUser.username,
                role = newRole.roleName,
                assignedBy = session.userId,
                assignedByName = session.username,
                assignedAt = System.currentTimeMillis(),
                permissions = customPermissions.ifBlank {
                    AdminPermissions.DEFAULT_ROLE_PERMISSIONS[newRole]?.joinToString(",") ?: ""
                },
                assignedArea = assignedArea,
                notes = notes
            )
            db.userRoleDao().insertOrUpdateRole(assignment)
        }

        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "CHANGE_USER_ROLE", "Role", targetUserId, targetUser.username,
            currentTargetRole.roleName, newRole.roleName, true, clientIp
        )

        Result.success(true)
    }

    suspend fun setUserBanStatus(
        session: AdminSession,
        targetUserId: String,
        isBanned: Boolean,
        reason: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.MANAGE_USERS)) {
            return@withContext Result.failure(Exception("Permission Denied: You do not have 'MANAGE_USERS' permission."))
        }

        val targetUser = db.userDao().getUserById(targetUserId)
            ?: return@withContext Result.failure(Exception("Target user not found."))

        val targetRole = AdminRole.fromString(db.userRoleDao().getRoleForUser(targetUserId)?.role)
        if (session.role != AdminRole.SUPER_ADMIN && targetRole.level >= session.role.level) {
            return@withContext Result.failure(Exception("Permission Denied: Cannot suspend an account with equal or higher administrative rank."))
        }

        db.userDao().updateBanStatus(targetUserId, isBanned)

        val action = if (isBanned) "BAN_USER" else "UNBAN_USER"
        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            action, "User", targetUserId, targetUser.username,
            "isBanned=${targetUser.isBanned}", "isBanned=$isBanned, Reason: $reason", true, clientIp
        )

        Result.success(true)
    }

    suspend fun adjustUserBalance(
        session: AdminSession,
        targetUserId: String,
        deltaCoins: Long,
        deltaDiamonds: Long,
        reason: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.MANAGE_COINS)) {
            return@withContext Result.failure(Exception("Permission Denied: You do not have 'MANAGE_COINS' permission."))
        }

        val targetUser = db.userDao().getUserById(targetUserId)
            ?: return@withContext Result.failure(Exception("Target user not found."))

        if (reason.isBlank()) {
            return@withContext Result.failure(Exception("A valid administrative reason is mandatory for all balance adjustments."))
        }

        db.userDao().updateBalance(targetUserId, deltaCoins, deltaDiamonds)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = targetUserId,
                type = if (deltaCoins >= 0) "Admin Credit" else "Admin Debit",
                amountCoins = deltaCoins,
                amountDiamonds = deltaDiamonds,
                description = "Admin (${session.username} - ${session.role.roleName}): $reason"
            )
        )

        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "ADJUST_BALANCE", "Wallet", targetUserId, targetUser.username,
            "Coins=${targetUser.coins}, Diamonds=${targetUser.diamonds}",
            "+$deltaCoins Coins, +$deltaDiamonds Diamonds. Reason: $reason", true, clientIp
        )

        Result.success(true)
    }

    suspend fun getAllVoiceRooms(session: AdminSession): List<JSONObject> = withContext(Dispatchers.IO) {
        val rooms = db.roomDao().searchRooms("")
        rooms.map { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("title", r.title)
            obj.put("description", r.description)
            obj.put("coverUrl", r.coverUrl)
            obj.put("ownerId", r.ownerId)
            obj.put("ownerName", r.ownerName)
            obj.put("seatCount", r.seatCount)
            obj.put("onlineCount", r.onlineCount)
            obj.put("isLocked", r.isLocked)
            obj.put("category", r.category)
            obj.put("announcement", r.announcement)
            obj.put("isActive", r.isActive)
            obj.put("createdAt", r.createdAt)
            obj
        }
    }

    suspend fun manageRoom(
        session: AdminSession,
        roomId: String,
        action: String, // "LOCK", "UNLOCK", "CLOSE", "UPDATE_ANNOUNCEMENT"
        payload: String?,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.MANAGE_ROOMS)) {
            return@withContext Result.failure(Exception("Permission Denied: You do not have 'MANAGE_ROOMS' permission."))
        }

        val room = db.roomDao().getRoomById(roomId)
            ?: return@withContext Result.failure(Exception("Room not found."))

        when (action) {
            "LOCK" -> {
                db.roomDao().insertOrUpdate(room.copy(isLocked = true))
            }
            "UNLOCK" -> {
                db.roomDao().insertOrUpdate(room.copy(isLocked = false))
            }
            "CLOSE" -> {
                db.roomDao().closeRoom(roomId)
                db.seatDao().clearSeatsForRoom(roomId)
            }
            "UPDATE_ANNOUNCEMENT" -> {
                val newAnnounce = payload ?: ""
                db.roomDao().insertOrUpdate(room.copy(announcement = newAnnounce))
            }
            else -> return@withContext Result.failure(Exception("Unknown room action '$action'"))
        }

        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "ROOM_MODERATION_$action", "Room", roomId, room.title,
            "isActive=${room.isActive}, isLocked=${room.isLocked}", action, true, clientIp
        )

        Result.success(true)
    }

    suspend fun getAppConfigs(session: AdminSession): List<JSONObject> = withContext(Dispatchers.IO) {
        val configs = db.appConfigDao().getAllConfigs()
        configs.map { c ->
            val obj = JSONObject()
            obj.put("key", c.key)
            obj.put("value", c.value)
            obj.put("category", c.category)
            obj.put("updatedAt", c.updatedAt)
            obj.put("updatedBy", c.updatedBy)
            obj
        }
    }

    suspend fun updateAppConfig(
        session: AdminSession,
        key: String,
        value: String,
        category: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.MANAGE_APP_CONFIG)) {
            return@withContext Result.failure(Exception("Permission Denied: You do not have 'MANAGE_APP_CONFIG' permission."))
        }

        val prevConfig = db.appConfigDao().getConfigByKey(key)
        val entity = AppConfigEntity(
            key = key,
            value = value,
            category = category,
            updatedAt = System.currentTimeMillis(),
            updatedBy = "${session.username} (${session.role.roleName})"
        )
        db.appConfigDao().insertOrUpdateConfig(entity)

        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "UPDATE_APP_CONFIG", "Config", key, key,
            prevConfig?.value, value, true, clientIp
        )

        Result.success(true)
    }

    suspend fun getReports(session: AdminSession, statusFilter: String = ""): List<JSONObject> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.VIEW_REPORTS)) {
            return@withContext emptyList()
        }

        val reports = if (statusFilter.isBlank()) db.reportDao().getAllReports() else db.reportDao().getReportsByStatus(statusFilter)
        reports.map { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("reporterId", r.reporterId)
            obj.put("reporterName", r.reporterName)
            obj.put("targetType", r.targetType)
            obj.put("targetId", r.targetId)
            obj.put("targetTitleOrName", r.targetTitleOrName)
            obj.put("reason", r.reason)
            obj.put("details", r.details)
            obj.put("status", r.status)
            obj.put("createdAt", r.createdAt)
            obj.put("resolvedAt", r.resolvedAt ?: 0)
            obj.put("resolvedBy", r.resolvedBy ?: "")
            obj.put("resolutionNotes", r.resolutionNotes ?: "")
            obj
        }
    }

    suspend fun resolveReport(
        session: AdminSession,
        reportId: String,
        status: String, // "Resolved", "Dismissed"
        notes: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.RESOLVE_REPORTS)) {
            return@withContext Result.failure(Exception("Permission Denied: You do not have 'RESOLVE_REPORTS' permission."))
        }

        db.reportDao().resolveReport(
            reportId = reportId,
            status = status,
            resolvedAt = System.currentTimeMillis(),
            resolvedBy = "${session.username} (${session.role.roleName})",
            notes = notes
        )

        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "RESOLVE_REPORT", "Report", reportId, reportId,
            "Pending", "$status: $notes", true, clientIp
        )

        Result.success(true)
    }

    suspend fun getAuditLogs(session: AdminSession, limit: Int = 100): List<JSONObject> = withContext(Dispatchers.IO) {
        if (!securityManager.hasPermission(session, AdminPermissions.VIEW_AUDIT_LOGS)) {
            return@withContext emptyList()
        }

        val logs = db.auditLogDao().getRecentAuditLogs(limit)
        logs.map { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("adminId", l.adminId)
            obj.put("adminName", l.adminName)
            obj.put("adminRole", l.adminRole)
            obj.put("action", l.action)
            obj.put("targetType", l.targetType)
            obj.put("targetId", l.targetId)
            obj.put("targetName", l.targetName)
            obj.put("previousValue", l.previousValue ?: "")
            obj.put("newValue", l.newValue ?: "")
            obj.put("timestamp", l.timestamp)
            obj.put("isSuccess", l.isSuccess)
            obj.put("ipAddress", l.ipAddress ?: "")
            obj
        }
    }
}
