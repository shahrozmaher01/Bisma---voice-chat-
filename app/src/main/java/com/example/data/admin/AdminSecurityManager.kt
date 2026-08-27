package com.example.data.admin

import com.example.data.local.BismaDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

enum class AdminRole(val roleName: String, val level: Int, val description: String) {
    SUPER_ADMIN("Super Admin", 100, "Full root administrative control over all aspects of the platform"),
    ADMIN("Admin", 80, "Senior administrative access for user, room, and content moderation"),
    MANAGER("Manager", 60, "Room and regional content moderation and user support"),
    BD("BD", 50, "Business Development management for agencies and partnerships"),
    AGENCY("Agency", 40, "Agency management, host recruitment, and team performance"),
    COIN_RESELLER("Coin Reseller", 30, "Authorized coin distribution and balance transfers"),
    USER("User", 10, "Standard consumer voice chat participant");

    companion object {
        fun fromString(role: String?): AdminRole {
            return entries.find { it.roleName.equals(role, ignoreCase = true) } ?: USER
        }
    }
}

object AdminPermissions {
    const val MANAGE_SUPER_ADMIN = "MANAGE_SUPER_ADMIN"
    const val MANAGE_ADMINS = "MANAGE_ADMINS"
    const val MANAGE_MANAGERS = "MANAGE_MANAGERS"
    const val MANAGE_BD = "MANAGE_BD"
    const val MANAGE_AGENCY = "MANAGE_AGENCY"
    const val MANAGE_RESELLERS = "MANAGE_RESELLERS"
    const val MANAGE_USERS = "MANAGE_USERS"
    const val MANAGE_ROLES = "MANAGE_ROLES"
    const val MANAGE_APP_CONFIG = "MANAGE_APP_CONFIG"
    const val MANAGE_BRANDING = "MANAGE_BRANDING"
    const val MANAGE_ROOMS = "MANAGE_ROOMS"
    const val MANAGE_COINS = "MANAGE_COINS"
    const val VIEW_REPORTS = "VIEW_REPORTS"
    const val RESOLVE_REPORTS = "RESOLVE_REPORTS"
    const val VIEW_AUDIT_LOGS = "VIEW_AUDIT_LOGS"
    const val MAINTENANCE_MODE = "MAINTENANCE_MODE"

    val DEFAULT_ROLE_PERMISSIONS = mapOf(
        AdminRole.SUPER_ADMIN to listOf(
            MANAGE_SUPER_ADMIN, MANAGE_ADMINS, MANAGE_MANAGERS, MANAGE_BD,
            MANAGE_AGENCY, MANAGE_RESELLERS, MANAGE_USERS, MANAGE_ROLES,
            MANAGE_APP_CONFIG, MANAGE_BRANDING, MANAGE_ROOMS, MANAGE_COINS,
            VIEW_REPORTS, RESOLVE_REPORTS, VIEW_AUDIT_LOGS, MAINTENANCE_MODE
        ),
        AdminRole.ADMIN to listOf(
            MANAGE_MANAGERS, MANAGE_BD, MANAGE_AGENCY, MANAGE_RESELLERS,
            MANAGE_USERS, MANAGE_ROOMS, VIEW_REPORTS, RESOLVE_REPORTS,
            VIEW_AUDIT_LOGS, MANAGE_COINS
        ),
        AdminRole.MANAGER to listOf(
            MANAGE_USERS, MANAGE_ROOMS, VIEW_REPORTS, RESOLVE_REPORTS
        ),
        AdminRole.BD to listOf(
            MANAGE_AGENCY, MANAGE_RESELLERS, VIEW_REPORTS
        ),
        AdminRole.AGENCY to listOf(
            "MANAGE_AGENCY_MEMBERS", "VIEW_AGENCY_STATS"
        ),
        AdminRole.COIN_RESELLER to listOf(
            "COIN_TRANSFER", "VIEW_RESELLER_TRANSACTIONS"
        ),
        AdminRole.USER to emptyList()
    )
}

data class AdminSession(
    val token: String,
    val userId: String,
    val username: String,
    val role: AdminRole,
    val permissions: List<String>,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
)

class AdminSecurityManager(private val db: BismaDatabase) {
    private val activeSessions = ConcurrentHashMap<String, AdminSession>()
    private val secureRandom = SecureRandom()

    suspend fun authenticateAdmin(idOrEmail: String, passwordHash: String, clientIp: String = "127.0.0.1"): Result<AdminSession> = withContext(Dispatchers.IO) {
        val trimmed = idOrEmail.trim()
        val user = db.userDao().getUserById(trimmed) ?: db.userDao().getUserByEmail(trimmed)
            ?: return@withContext Result.failure(Exception("Account not found for '$trimmed'"))

        if (user.isBanned) {
            logAction("SYSTEM", "System", "System", "LOGIN_FAILED_BANNED", "User", user.id, user.username, null, "Account is banned", false, clientIp)
            return@withContext Result.failure(Exception("This account is currently suspended/banned."))
        }

        // Verify password hash
        if (user.passwordHash.isNotEmpty() && user.passwordHash != passwordHash) {
            logAction("SYSTEM", "System", "System", "LOGIN_FAILED_PASSWORD", "User", user.id, user.username, null, "Invalid password attempt", false, clientIp)
            return@withContext Result.failure(Exception("Invalid password credentials."))
        }

        // Check assigned role
        var roleAssignment = db.userRoleDao().getRoleForUser(user.id)

        // Bootstrap: If no Super Admin exists in the database, promote this user or seed Super Admin
        val totalSuperAdmins = db.userRoleDao().countByRole(AdminRole.SUPER_ADMIN.roleName)
        if (totalSuperAdmins == 0) {
            val superAdminRole = UserRoleAssignment(
                userId = user.id,
                username = user.username,
                role = AdminRole.SUPER_ADMIN.roleName,
                assignedBy = "SYSTEM_INITIALIZER",
                assignedByName = "Root Initializer",
                permissions = AdminPermissions.DEFAULT_ROLE_PERMISSIONS[AdminRole.SUPER_ADMIN]?.joinToString(",") ?: "",
                notes = "Initial Root Super Admin designated upon system deployment"
            )
            db.userRoleDao().insertOrUpdateRole(superAdminRole)
            roleAssignment = superAdminRole
            logAction(user.id, user.username, AdminRole.SUPER_ADMIN.roleName, "INIT_SUPER_ADMIN", "User", user.id, user.username, null, "Designated as Root Super Admin", true, clientIp)
        }

        val role = AdminRole.fromString(roleAssignment?.role)
        if (role == AdminRole.USER) {
            logAction(user.id, user.username, "User", "ACCESS_DENIED_ROLE", "AdminPanel", "login", "User", null, "Access denied: insufficient privileges", false, clientIp)
            return@withContext Result.failure(Exception("Access Denied: Standard user accounts are not authorized to access the Admin Control Panel."))
        }

        val customPerms = roleAssignment?.permissions?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
        val defaultPerms = AdminPermissions.DEFAULT_ROLE_PERMISSIONS[role] ?: emptyList()
        val combinedPerms = (defaultPerms + customPerms).distinct()

        val tokenBytes = ByteArray(32)
        secureRandom.nextBytes(tokenBytes)
        val token = tokenBytes.joinToString("") { "%02x".format(it) }

        val session = AdminSession(
            token = token,
            userId = user.id,
            username = user.username,
            role = role,
            permissions = combinedPerms
        )

        activeSessions[token] = session

        logAction(user.id, user.username, role.roleName, "ADMIN_LOGIN_SUCCESS", "AdminSession", session.token.take(8), user.username, null, "Logged into Web Admin Panel", true, clientIp)

        Result.success(session)
    }

    fun validateSession(token: String?): AdminSession? {
        if (token.isNullOrBlank()) return null
        val session = activeSessions[token] ?: return null
        if (System.currentTimeMillis() > session.expiresAt) {
            activeSessions.remove(token)
            return null
        }
        return session
    }

    fun invalidateSession(token: String) {
        activeSessions.remove(token)
    }

    fun hasPermission(session: AdminSession, permission: String): Boolean {
        if (session.role == AdminRole.SUPER_ADMIN) return true
        return session.permissions.contains(permission)
    }

    /**
     * Server-side Hierarchy validation rule:
     * - An actor can only assign or modify roles that have a strictly LOWER level than their own role.
     * - Super Admin (100) can assign any role.
     * - Admin (80) can assign Manager (60), BD (50), Agency (40), Reseller (30), User (10).
     * - Manager, BD, Agency, Reseller, User CANNOT assign or elevate roles.
     * - Prevents privilege escalation completely.
     */
    fun canModifyTargetRole(actorRole: AdminRole, targetCurrentRole: AdminRole, newRole: AdminRole): Boolean {
        if (actorRole == AdminRole.SUPER_ADMIN) return true

        // Lower-level roles cannot modify roles at or above their level
        if (actorRole.level <= targetCurrentRole.level) return false
        if (actorRole.level <= newRole.level) return false

        return true
    }

    suspend fun logAction(
        adminId: String,
        adminName: String,
        adminRole: String,
        action: String,
        targetType: String,
        targetId: String,
        targetName: String,
        previousValue: String?,
        newValue: String?,
        isSuccess: Boolean = true,
        ipAddress: String? = "127.0.0.1"
    ) = withContext(Dispatchers.IO) {
        val log = AuditLogEntity(
            id = UUID.randomUUID().toString(),
            adminId = adminId,
            adminName = adminName,
            adminRole = adminRole,
            action = action,
            targetType = targetType,
            targetId = targetId,
            targetName = targetName,
            previousValue = previousValue,
            newValue = newValue,
            timestamp = System.currentTimeMillis(),
            isSuccess = isSuccess,
            ipAddress = ipAddress
        )
        db.auditLogDao().insertAuditLog(log)
    }
}
