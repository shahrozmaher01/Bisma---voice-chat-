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

    suspend fun initializeDefaultLinkUsers(adminId: String) = withContext(Dispatchers.IO) {
        val count = db.adminLinkUserDao().countLinkUsers(adminId)
        if (count == 0) {
            val defaults = listOf(
                AdminLinkUser(
                    id = "link_${adminId}_usr1",
                    adminId = adminId,
                    userId = "usr_78912",
                    userName = "Ali Raza",
                    userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                    status = "Active",
                    assignedWork = "Prime-Time Audio Host (Target 40h/month)",
                    workStatus = "In Progress",
                    workCategory = "Voice Hosting",
                    targetHours = 40.0,
                    completedHours = 26.5,
                    targetDiamonds = 50000,
                    earnedDiamonds = 34200,
                    activityInfo = "Live 26.5h • 34.2k Diamonds • 98% punctuality",
                    lastActive = "Today, 16:45",
                    joinedDate = "2026-08-10",
                    notes = "Hosting evening music and discussion rooms successfully"
                ),
                AdminLinkUser(
                    id = "link_${adminId}_usr2",
                    adminId = adminId,
                    userId = "usr_45623",
                    userName = "Bisma Noor",
                    userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
                    status = "Active",
                    assignedWork = "Singing & Chill Room Lead",
                    workStatus = "Active Live",
                    workCategory = "Voice Hosting",
                    targetHours = 35.0,
                    completedHours = 38.0,
                    targetDiamonds = 60000,
                    earnedDiamonds = 65800,
                    activityInfo = "Live 38.0h • 65.8k Diamonds • Target Achieved 🎉",
                    lastActive = "Currently Online 🟢",
                    joinedDate = "2026-08-05",
                    notes = "High talent host with top room retention"
                ),
                AdminLinkUser(
                    id = "link_${adminId}_usr3",
                    adminId = adminId,
                    userId = "usr_33219",
                    userName = "Hamza Khan",
                    userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150",
                    status = "Active",
                    assignedWork = "Night Shift Room Moderation (10 PM - 4 AM)",
                    workStatus = "In Progress",
                    workCategory = "Moderation",
                    targetHours = 60.0,
                    completedHours = 44.0,
                    targetDiamonds = 20000,
                    earnedDiamonds = 16800,
                    activityInfo = "18 night shifts completed • 0 rule violations",
                    lastActive = "Today, 04:15",
                    joinedDate = "2026-08-12",
                    notes = "Maintains room discipline and resolves seat disputes"
                ),
                AdminLinkUser(
                    id = "link_${adminId}_usr4",
                    adminId = adminId,
                    userId = "usr_88241",
                    userName = "Ayesha Malik",
                    userAvatar = "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150",
                    status = "Active",
                    assignedWork = "Host Recruitment & Community Management",
                    workStatus = "Pending Review",
                    workCategory = "Agency BD",
                    targetHours = 30.0,
                    completedHours = 22.0,
                    targetDiamonds = 40000,
                    earnedDiamonds = 29500,
                    activityInfo = "14 new verified hosts onboarded • Review pending",
                    lastActive = "Yesterday, 21:30",
                    joinedDate = "2026-08-18",
                    notes = "Assisting new creators with setup and audio equipment"
                )
            )
            db.adminLinkUserDao().insertAll(defaults)
        }
    }

    suspend fun getLinkUsers(session: AdminSession, query: String = ""): List<JSONObject> = withContext(Dispatchers.IO) {
        initializeDefaultLinkUsers(session.userId)

        val linkUsers = if (query.isBlank()) {
            db.adminLinkUserDao().getLinkUsers(session.userId)
        } else {
            db.adminLinkUserDao().searchLinkUsers(session.userId, query.trim())
        }

        linkUsers.map { u ->
            JSONObject().apply {
                put("id", u.id)
                put("adminId", u.adminId)
                put("userId", u.userId)
                put("userName", u.userName)
                put("userAvatar", u.userAvatar)
                put("status", u.status)
                put("assignedWork", u.assignedWork)
                put("workStatus", u.workStatus)
                put("workCategory", u.workCategory)
                put("targetHours", u.targetHours)
                put("completedHours", u.completedHours)
                put("targetDiamonds", u.targetDiamonds)
                put("earnedDiamonds", u.earnedDiamonds)
                put("activityInfo", u.activityInfo)
                put("lastActive", u.lastActive)
                put("joinedDate", u.joinedDate)
                put("notes", u.notes)
                put("updatedAt", u.updatedAt)
            }
        }
    }

    suspend fun addLinkUser(
        session: AdminSession,
        userId: String,
        userName: String,
        status: String,
        assignedWork: String,
        workStatus: String,
        workCategory: String,
        targetHours: Double,
        completedHours: Double,
        targetDiamonds: Long,
        earnedDiamonds: Long,
        activityInfo: String,
        notes: String,
        clientIp: String
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val cleanUserId = userId.trim()
            val cleanUserName = userName.trim()
            if (cleanUserId.isBlank() || cleanUserName.isBlank()) {
                return@withContext Result.failure(Exception("User ID and User Name are required."))
            }

            val id = "link_${session.userId}_${cleanUserId}"
            val existing = db.adminLinkUserDao().getLinkUserById(id)
            val record = AdminLinkUser(
                id = id,
                adminId = session.userId,
                userId = cleanUserId,
                userName = cleanUserName,
                userAvatar = existing?.userAvatar ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150",
                status = status.ifBlank { "Active" },
                assignedWork = assignedWork.ifBlank { "Live Audio Host" },
                workStatus = workStatus.ifBlank { "In Progress" },
                workCategory = workCategory.ifBlank { "Voice Hosting" },
                targetHours = targetHours.coerceAtLeast(0.0),
                completedHours = completedHours.coerceAtLeast(0.0),
                targetDiamonds = targetDiamonds.coerceAtLeast(0),
                earnedDiamonds = earnedDiamonds.coerceAtLeast(0),
                activityInfo = activityInfo.ifBlank { "Joined via Admin Link • Ready for assignment" },
                lastActive = "Just added",
                joinedDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )

            db.adminLinkUserDao().insertOrUpdate(record)

            securityManager.logAction(
                session.userId, session.username, session.role.roleName,
                "ADD_LINK_USER", "AdminLink", cleanUserId, cleanUserName,
                null, "Assigned: $assignedWork ($workStatus)", true, clientIp
            )

            val json = JSONObject().apply {
                put("id", record.id)
                put("userId", record.userId)
                put("userName", record.userName)
                put("status", record.status)
                put("assignedWork", record.assignedWork)
                put("workStatus", record.workStatus)
                put("activityInfo", record.activityInfo)
            }
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLinkUserWork(
        session: AdminSession,
        id: String,
        status: String,
        assignedWork: String,
        workStatus: String,
        workCategory: String,
        targetHours: Double,
        completedHours: Double,
        targetDiamonds: Long,
        earnedDiamonds: Long,
        activityInfo: String,
        notes: String,
        clientIp: String
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val record = db.adminLinkUserDao().getLinkUserById(id)
                ?: return@withContext Result.failure(Exception("User not found under your link."))

            if (record.adminId != session.userId && session.role != AdminRole.SUPER_ADMIN) {
                return@withContext Result.failure(Exception("Access Denied: You cannot modify users registered under another admin's link."))
            }

            val updated = record.copy(
                status = status.ifBlank { record.status },
                assignedWork = assignedWork.ifBlank { record.assignedWork },
                workStatus = workStatus.ifBlank { record.workStatus },
                workCategory = workCategory.ifBlank { record.workCategory },
                targetHours = targetHours,
                completedHours = completedHours,
                targetDiamonds = targetDiamonds,
                earnedDiamonds = earnedDiamonds,
                activityInfo = activityInfo.ifBlank { record.activityInfo },
                notes = notes,
                updatedAt = System.currentTimeMillis()
            )

            db.adminLinkUserDao().insertOrUpdate(updated)

            securityManager.logAction(
                session.userId, session.username, session.role.roleName,
                "UPDATE_LINK_USER_WORK", "AdminLink", record.userId, record.userName,
                "${record.assignedWork} (${record.workStatus})",
                "${updated.assignedWork} (${updated.workStatus})",
                true, clientIp
            )

            val json = JSONObject().apply {
                put("id", updated.id)
                put("userId", updated.userId)
                put("userName", updated.userName)
                put("status", updated.status)
                put("assignedWork", updated.assignedWork)
                put("workStatus", updated.workStatus)
                put("activityInfo", updated.activityInfo)
                put("notes", updated.notes)
            }
            Result.success(json)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLinkUser(
        session: AdminSession,
        id: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val record = db.adminLinkUserDao().getLinkUserById(id)
                ?: return@withContext Result.failure(Exception("User not found under your link."))

            if (record.adminId != session.userId && session.role != AdminRole.SUPER_ADMIN) {
                return@withContext Result.failure(Exception("Access Denied: You cannot remove users registered under another admin's link."))
            }

            db.adminLinkUserDao().deleteLinkUser(id, record.adminId)

            securityManager.logAction(
                session.userId, session.username, session.role.roleName,
                "REMOVE_LINK_USER", "AdminLink", record.userId, record.userName,
                record.assignedWork, "Removed from admin link", true, clientIp
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboardStats(session: AdminSession): JSONObject = withContext(Dispatchers.IO) {
        initializeDefaultLinkUsers(session.userId)

        val totalLinkUsers = db.adminLinkUserDao().countLinkUsers(session.userId)
        val activeLinkUsers = db.adminLinkUserDao().countActiveLinkUsers(session.userId)
        val completedWork = db.adminLinkUserDao().countCompletedWork(session.userId)
        val recentLogs = db.auditLogDao().getLogsByAdmin(session.userId)

        val json = JSONObject()
        json.put("panelName", session.panelName)
        json.put("adminName", session.username)
        json.put("adminId", session.userId)
        json.put("adminRole", session.role.roleName)
        json.put("mobileNumber", session.mobileNumber)
        json.put("totalLinkUsers", totalLinkUsers)
        json.put("activeLinkUsers", activeLinkUsers)
        json.put("completedWork", completedWork)
        json.put("inProgressWork", (totalLinkUsers - completedWork).coerceAtLeast(0))
        json.put("linkStatus", "Active & Verified 🛡️")
        json.put("adminLinkUrl", "https://official1.live/link?admin=${session.userId}")

        val logsArray = JSONArray()
        recentLogs.take(10).forEach { log ->
            val obj = JSONObject()
            obj.put("id", log.id)
            obj.put("action", log.action)
            obj.put("targetType", log.targetType)
            obj.put("targetName", log.targetName)
            obj.put("details", log.newValue ?: log.action)
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

    suspend fun getAllAgencies(session: AdminSession): List<JSONObject> = withContext(Dispatchers.IO) {
        val agencies = db.agencyFamilyDao().getAllAgencies()
        agencies.map { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("name", a.name)
            obj.put("logoUrl", a.logoUrl)
            obj.put("ownerId", a.ownerId)
            obj.put("ownerName", a.ownerName)
            obj.put("agencyCode", a.agencyCode)
            obj.put("bdId", a.bdId)
            obj.put("memberCount", a.memberCount)
            obj.put("level", a.level)
            obj.put("totalIncome", a.totalIncome)
            obj.put("ranking", a.ranking)
            obj
        }
    }

    suspend fun getAllWithdrawals(session: AdminSession): List<JSONObject> = withContext(Dispatchers.IO) {
        val txs = db.walletTransactionDao().getAllTransactions()
            .filter { it.type.contains("Withdraw", ignoreCase = true) || it.type.contains("Exchange", ignoreCase = true) || it.description.contains("Cash", ignoreCase = true) }
        txs.map { tx ->
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("userId", tx.userId)
            obj.put("type", tx.type)
            obj.put("amountCoins", tx.amountCoins)
            obj.put("amountDiamonds", tx.amountDiamonds)
            obj.put("description", tx.description)
            obj.put("timestamp", tx.timestamp)
            obj.put("status", if (tx.description.contains("Approved", ignoreCase = true)) "Approved" else if (tx.description.contains("Rejected", ignoreCase = true)) "Rejected" else "Pending Review")
            obj
        }
    }

    suspend fun handleWithdrawalAction(
        session: AdminSession,
        txId: String,
        action: String, // "APPROVE", "REJECT"
        notes: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "WITHDRAWAL_$action", "Withdrawal", txId, txId,
            "Pending", "$action: $notes", true, clientIp
        )
        Result.success(true)
    }

    suspend fun getAllStoreItems(session: AdminSession): List<JSONObject> = withContext(Dispatchers.IO) {
        val items = db.storeDao().getAllItems()
        items.map { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("category", item.category)
            obj.put("price", item.price)
            obj.put("previewIcon", item.previewIcon)
            obj.put("durationDays", item.durationDays)
            obj.put("isOwned", item.isOwned)
            obj.put("isEquipped", item.isEquipped)
            obj.put("description", item.description)
            obj
        }
    }

    suspend fun sendSystemNotification(
        session: AdminSession,
        targetUserId: String?,
        title: String,
        message: String,
        clientIp: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        val users = if (targetUserId.isNullOrBlank()) {
            db.userDao().getAllUsers()
        } else {
            listOfNotNull(db.userDao().getUserById(targetUserId))
        }

        users.forEach { u ->
            db.notificationDao().insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    userId = u.id,
                    type = "system",
                    title = title,
                    message = message,
                    senderName = "Official 1 Support 🛡️",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        securityManager.logAction(
            session.userId, session.username, session.role.roleName,
            "BROADCAST_NOTIFICATION", "Notification", targetUserId ?: "ALL_USERS", title,
            null, message, true, clientIp
        )

        Result.success(true)
    }

    suspend fun getAdminProfile(session: AdminSession): JSONObject = withContext(Dispatchers.IO) {
        val profile = securityManager.getAdminProfile()
        JSONObject().apply {
            put("panelName", profile.panelName)
            put("adminName", profile.adminName)
            put("adminId", profile.adminId)
            put("mobileNumber", profile.mobileNumber)
            put("whatsappApiUrl", profile.whatsappApiUrl)
            put("whatsappApiKey", profile.whatsappApiKey)
            put("is2FaEnforced", profile.is2FaEnforced)
            put("isSetupComplete", profile.isSetupComplete)
        }
    }

    suspend fun updateAdminProfile(
        session: AdminSession,
        panelName: String,
        adminName: String,
        adminId: String,
        newPasswordRaw: String?,
        mobileNumber: String,
        whatsappApiUrl: String?,
        whatsappApiKey: String?,
        is2FaEnforced: Boolean,
        clientIp: String
    ): Result<JSONObject> = withContext(Dispatchers.IO) {
        if (session.role != AdminRole.SUPER_ADMIN && !securityManager.hasPermission(session, AdminPermissions.MANAGE_PROFILE)) {
            return@withContext Result.failure(Exception("Permission Denied: Only Super Admin can update the Official 1 Admin Profile."))
        }

        val res = securityManager.saveAdminProfile(
            panelName = panelName,
            adminName = adminName,
            adminId = adminId,
            newPasswordRaw = newPasswordRaw,
            mobileNumber = mobileNumber,
            whatsappApiUrl = whatsappApiUrl,
            whatsappApiKey = whatsappApiKey,
            is2FaEnforced = is2FaEnforced,
            actorId = session.userId,
            actorName = session.username,
            clientIp = clientIp
        )

        if (res.isSuccess) {
            val updated = res.getOrThrow()
            val obj = JSONObject().apply {
                put("panelName", updated.panelName)
                put("adminName", updated.adminName)
                put("adminId", updated.adminId)
                put("mobileNumber", updated.mobileNumber)
                put("whatsappApiUrl", updated.whatsappApiUrl)
                put("is2FaEnforced", updated.is2FaEnforced)
            }
            Result.success(obj)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Failed to update profile"))
        }
    }
}
