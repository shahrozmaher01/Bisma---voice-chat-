package com.example.data.admin

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class AdminWebServer(
    private val adminService: AdminService,
    private val preferredPort: Int = 8080
) {
    private val TAG = "AdminWebServer"
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _serverUrl = MutableStateFlow("http://localhost:8080/admin")
    val serverUrl = _serverUrl.asStateFlow()

    private var activePort = preferredPort

    fun start() {
        if (_isRunning.value) return

        serverJob = coroutineScope.launch {
            try {
                adminService.initializeDefaultConfigs()
                adminService.initializeDefaultFrameAssignments()
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing default configs", e)
            }

            var bound = false
            var portToTry = preferredPort
            while (!bound && portToTry <= preferredPort + 5) {
                try {
                    val socket = ServerSocket()
                    socket.reuseAddress = true
                    socket.bind(InetSocketAddress("0.0.0.0", portToTry))
                    serverSocket = socket
                    activePort = portToTry
                    bound = true
                } catch (e: Exception) {
                    Log.w(TAG, "Port $portToTry in use, trying next...")
                    portToTry++
                }
            }

            if (!bound || serverSocket == null) {
                Log.e(TAG, "Failed to bind Admin Web Server on ports $preferredPort..$portToTry")
                return@launch
            }

            _serverUrl.value = "http://localhost:$activePort/admin"
            _isRunning.value = true
            Log.i(TAG, "Official 1 Admin Web Server running at http://localhost:$activePort/admin")

            while (isActive && serverSocket?.isClosed == false) {
                try {
                    val clientSocket = serverSocket?.accept() ?: break
                    launch(Dispatchers.IO) {
                        handleClient(clientSocket)
                    }
                } catch (e: Exception) {
                    if (!isActive || serverSocket?.isClosed == true) break
                    Log.e(TAG, "Error accepting client connection", e)
                }
            }
        }
    }

    fun stop() {
        _isRunning.value = false
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing server socket", e)
        }
        serverJob?.cancel()
    }

    private suspend fun handleClient(socket: Socket) {
        val clientIp = socket.inetAddress.hostAddress ?: "127.0.0.1"
        try {
            socket.soTimeout = 12000
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
            val outputStream = socket.getOutputStream()

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            if (parts.size < 2) return
            val method = parts[0].uppercase()
            val rawPath = parts[1]

            val headers = mutableMapOf<String, String>()
            var line: String?
            var contentLength = 0
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                val colonIdx = line!!.indexOf(":")
                if (colonIdx > 0) {
                    val key = line!!.substring(0, colonIdx).trim().lowercase()
                    val value = line!!.substring(colonIdx + 1).trim()
                    headers[key] = value
                    if (key == "content-length") {
                        contentLength = value.toIntOrNull() ?: 0
                    }
                }
            }

            val body = if (contentLength > 0) {
                val chars = CharArray(contentLength)
                var read = 0
                while (read < contentLength) {
                    val r = reader.read(chars, read, contentLength - read)
                    if (r == -1) break
                    read += r
                }
                String(chars, 0, read)
            } else {
                ""
            }

            val urlParts = rawPath.split("?", limit = 2)
            val path = urlParts[0]
            val queryParamString = if (urlParts.size > 1) urlParts[1] else ""
            val queryParams = parseQueryParams(queryParamString)

            if (method == "OPTIONS") {
                sendResponse(outputStream, 204, "No Content", "text/plain", "")
                return
            }

            // Public & Auth Routes
            when {
                path == "/" || path == "/admin" || path == "/index.html" -> {
                    val html = AdminWebPageTemplate.getHtml()
                    sendResponse(outputStream, 200, "OK", "text/html; charset=UTF-8", html)
                }

                path == "/api/admin/info" && method == "GET" -> {
                    val profile = adminService.securityManager.getAdminProfile()
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("panelName", "Official Admin Panel")
                        put("adminName", profile.adminName)
                        put("adminId", profile.adminId)
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                }

                path == "/api/admin/setup" && method == "POST" -> {
                    handleInitialSetup(outputStream, body, clientIp)
                }

                path == "/api/admin/login" && method == "POST" -> {
                    handleLogin(outputStream, body, clientIp)
                }

                path == "/api/admin/send-whatsapp-otp" && method == "POST" -> {
                    handleSendWhatsAppOtp(outputStream, body, clientIp)
                }

                path == "/api/admin/verify-whatsapp-otp" && method == "POST" -> {
                    handleVerifyWhatsAppOtp(outputStream, body, clientIp)
                }

                // Protected Routes (Require Bearer Token)
                path.startsWith("/api/admin/") -> {
                    val authHeader = headers["authorization"] ?: ""
                    val token = authHeader.removePrefix("Bearer ").trim()
                    val session = adminService.securityManager.validateSession(token)

                    if (session == null) {
                        val err = JSONObject().apply {
                            put("success", false)
                            put("message", "Session expired or unauthorized. Please re-login.")
                        }
                        sendResponse(outputStream, 401, "Unauthorized", "application/json", err.toString())
                    } else {
                        handleProtectedApi(path, method, queryParams, body, session, outputStream, clientIp)
                    }
                }

                else -> {
                    sendResponse(outputStream, 404, "Not Found", "text/plain", "404 Not Found")
                }
            }

        } catch (e: java.net.SocketTimeoutException) {
            Log.d(TAG, "Client socket read timed out from $clientIp")
        } catch (e: java.net.SocketException) {
            Log.d(TAG, "Client socket closed: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Exception handling client request", e)
        } finally {
            try {
                socket.close()
            } catch (_: Exception) {}
        }
    }

    /**
     * Initial Admin Profile Setup Endpoint
     */
    private suspend fun handleInitialSetup(outputStream: OutputStream, body: String, clientIp: String) {
        val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
        val panelName = json.optString("panelName", "Official 1").trim().ifBlank { "Official 1" }
        val adminName = json.optString("adminName", "Sherry").trim().ifBlank { "Sherry" }
        val adminId = json.optString("adminId", "565656565666555").trim().ifBlank { "565656565666555" }
        val password = json.optString("password", "").trim()
        val mobileNumber = json.optString("mobileNumber", "+923254256177").trim().ifBlank { "+923254256177" }

        val res = adminService.securityManager.saveAdminProfile(
            panelName = panelName,
            adminName = adminName,
            adminId = adminId,
            newPasswordRaw = password,
            mobileNumber = mobileNumber,
            actorId = "SETUP_WIZARD",
            actorName = "Initial Setup",
            clientIp = clientIp
        )

        if (res.isSuccess) {
            val updated = res.getOrThrow()
            val resp = JSONObject().apply {
                put("success", true)
                put("message", "Official 1 Admin Profile initialized successfully!")
                put("panelName", updated.panelName)
                put("adminName", updated.adminName)
                put("adminId", updated.adminId)
                put("mobileMasked", AdminSecurityManager.maskPhoneNumber(updated.mobileNumber))
            }
            sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
        } else {
            val err = JSONObject().apply {
                put("success", false)
                put("message", res.exceptionOrNull()?.message ?: "Setup failed")
            }
            sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
        }
    }

    /**
     * Direct Admin Login (No phone number, OTP, SMS code, or WhatsApp verification):
     * Validates Username or Admin ID and Password entered manually by the admin.
     * If valid, opens the Admin Panel immediately.
     * If invalid, returns "Invalid Username or Password".
     */
    private suspend fun handleLogin(outputStream: OutputStream, body: String, clientIp: String) {
        val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
        val usernameOrId = json.optString("usernameOrId", "").ifBlank {
            json.optString("usernameOrAdminId", "").ifBlank {
                json.optString("userName", "").ifBlank {
                    json.optString("username", "").ifBlank {
                        json.optString("adminId", "").ifBlank {
                            json.optString("idOrUsername", "")
                        }
                    }
                }
            }
        }
        val password = json.optString("password", "")

        val res = adminService.securityManager.authenticateDirect(
            usernameOrAdminId = usernameOrId,
            passwordRaw = password,
            clientIp = clientIp
        )
        if (res.isSuccess) {
            val session = res.getOrThrow()
            val sessionJson = JSONObject().apply {
                put("token", session.token)
                put("userId", session.userId)
                put("username", session.username)
                put("role", session.role.roleName)
                put("panelName", "Official Admin Panel")
                put("permissions", org.json.JSONArray(session.permissions))
            }
            val resp = JSONObject().apply {
                put("success", true)
                put("session", sessionJson)
                put("panelName", "Official Admin Panel")
                put("adminName", session.username)
                put("adminId", session.userId)
                put("message", "Credentials verified! Welcome to Official Admin Panel.")
            }
            sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
        } else {
            val err = JSONObject().apply {
                put("success", false)
                put("message", res.exceptionOrNull()?.message ?: "Invalid Username or Password")
            }
            sendResponse(outputStream, 401, "Unauthorized", "application/json", err.toString())
        }
    }

    /**
     * Step 2: Send WhatsApp OTP code.
     * Code is strictly server-side and never returned to client!
     */
    private suspend fun handleSendWhatsAppOtp(outputStream: OutputStream, body: String, clientIp: String) {
        val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
        val preAuthToken = json.optString("preAuthToken", "")
        val mobile = json.optString("mobileNumber", "").ifBlank { json.optString("phone", "") }

        val res = adminService.securityManager.sendWhatsAppOtp(preAuthToken, mobile, clientIp)
        if (res.isSuccess) {
            val msg = res.getOrThrow()
            val resp = JSONObject().apply {
                put("success", true)
                put("message", msg)
                put("cooldownSeconds", 60)
            }
            sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
        } else {
            val err = JSONObject().apply {
                put("success", false)
                put("message", res.exceptionOrNull()?.message ?: "Unable to send verification code. Please check the number or try again later.")
            }
            sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
        }
    }

    /**
     * Step 2: Verify WhatsApp OTP Code and Unlock Control Panel.
     */
    private suspend fun handleVerifyWhatsAppOtp(outputStream: OutputStream, body: String, clientIp: String) {
        val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
        val preAuthToken = json.optString("preAuthToken", "")
        val code = json.optString("code", "").trim()

        val res = adminService.securityManager.verifyWhatsAppOtpAndLogin(preAuthToken, code, clientIp)
        if (res.isSuccess) {
            val session = res.getOrThrow()
            val resp = JSONObject().apply {
                put("success", true)
                put("message", "Authentication successful! Welcome to Official 1.")
                put("session", JSONObject().apply {
                    put("token", session.token)
                    put("userId", session.userId)
                    put("username", session.username)
                    put("panelName", session.panelName)
                    put("role", session.role.roleName)
                    put("mobileNumber", session.mobileNumber)
                    put("permissions", JSONArray(session.permissions))
                    put("expiresAt", session.expiresAt)
                })
            }
            sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
        } else {
            val err = JSONObject().apply {
                put("success", false)
                put("message", res.exceptionOrNull()?.message ?: "Verification failed. Incorrect OTP code.")
            }
            sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
        }
    }

    /**
     * Protected Administrative Endpoints
     */
    private suspend fun handleProtectedApi(
        path: String,
        method: String,
        params: Map<String, String>,
        body: String,
        session: AdminSession,
        outputStream: OutputStream,
        clientIp: String
    ) {
        when {
            path == "/api/admin/logout" && method == "POST" -> {
                adminService.securityManager.invalidateSession(session.token)
                adminService.securityManager.logAction(
                    session.userId, session.username, session.role.roleName,
                    "ADMIN_LOGOUT", "AdminSession", session.token.take(8), session.username,
                    null, "Logged out of Official 1 Control Panel", true, clientIp
                )
                val resp = JSONObject().apply { put("success", true); put("message", "Logged out successfully") }
                sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
            }

            path == "/api/admin/session" && method == "GET" -> {
                val resp = JSONObject().apply {
                    put("success", true)
                    put("session", JSONObject().apply {
                        put("token", session.token)
                        put("userId", session.userId)
                        put("username", session.username)
                        put("panelName", session.panelName)
                        put("role", session.role.roleName)
                        put("mobileNumber", session.mobileNumber)
                        put("permissions", JSONArray(session.permissions))
                    })
                }
                sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
            }

            // Admin Profile Tab
            path == "/api/admin/profile" && method == "GET" -> {
                val profile = adminService.getAdminProfile(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply {
                    put("success", true)
                    put("profile", profile)
                }.toString())
            }

            path == "/api/admin/profile" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val panelName = json.optString("panelName", session.panelName)
                val adminName = json.optString("adminName", session.username)
                val adminId = json.optString("adminId", session.userId)
                val newPassword = json.optString("password", "").ifBlank { null }
                val mobileNumber = json.optString("mobileNumber", session.mobileNumber)
                val waUrl = json.optString("whatsappApiUrl", "").ifBlank { null }
                val waKey = json.optString("whatsappApiKey", "").ifBlank { null }
                val is2fa = json.optBoolean("is2FaEnforced", true)

                val res = adminService.updateAdminProfile(
                    session = session,
                    panelName = panelName,
                    adminName = adminName,
                    adminId = adminId,
                    newPasswordRaw = newPassword,
                    mobileNumber = mobileNumber,
                    whatsappApiUrl = waUrl,
                    whatsappApiKey = waKey,
                    is2FaEnforced = is2fa,
                    clientIp = clientIp
                )

                if (res.isSuccess) {
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("message", "Official 1 Admin Profile updated successfully!")
                        put("profile", res.getOrThrow())
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply {
                        put("success", false)
                        put("message", res.exceptionOrNull()?.message ?: "Failed to update profile")
                    }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Dashboard Stats
            path == "/api/admin/dashboard" && method == "GET" -> {
                val stats = adminService.getDashboardStats(session)
                sendResponse(outputStream, 200, "OK", "application/json", stats.toString())
            }

            // Link Section - Scoped Users & Assigned Work
            path == "/api/admin/link/users" && method == "GET" -> {
                val query = params["query"] ?: ""
                val users = adminService.getLinkUsers(session, query)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(users).toString())
            }

            (path == "/api/admin/link/users" || path == "/api/admin/link/users/add") && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val userId = json.optString("userId", "")
                val userName = json.optString("userName", "")
                val status = json.optString("status", "Active")
                val assignedWork = json.optString("assignedWork", "Live Audio Host")
                val workStatus = json.optString("workStatus", "In Progress")
                val workCategory = json.optString("workCategory", "Voice Hosting")
                val targetHours = json.optDouble("targetHours", 40.0)
                val completedHours = json.optDouble("completedHours", 0.0)
                val targetDiamonds = json.optLong("targetDiamonds", 50000L)
                val earnedDiamonds = json.optLong("earnedDiamonds", 0L)
                val activityInfo = json.optString("activityInfo", "")
                val notes = json.optString("notes", "")

                val res = adminService.addLinkUser(
                    session = session,
                    userId = userId,
                    userName = userName,
                    status = status,
                    assignedWork = assignedWork,
                    workStatus = workStatus,
                    workCategory = workCategory,
                    targetHours = targetHours,
                    completedHours = completedHours,
                    targetDiamonds = targetDiamonds,
                    earnedDiamonds = earnedDiamonds,
                    activityInfo = activityInfo,
                    notes = notes,
                    clientIp = clientIp
                )
                if (res.isSuccess) {
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("message", "User registered under link successfully")
                        put("user", res.getOrThrow())
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply {
                        put("success", false)
                        put("message", res.exceptionOrNull()?.message ?: "Failed to add user to link")
                    }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            (path == "/api/admin/link/users/update" || (path == "/api/admin/link/users" && method == "PUT")) -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val id = json.optString("id", "")
                val status = json.optString("status", "Active")
                val assignedWork = json.optString("assignedWork", "")
                val workStatus = json.optString("workStatus", "In Progress")
                val workCategory = json.optString("workCategory", "Voice Hosting")
                val targetHours = json.optDouble("targetHours", 40.0)
                val completedHours = json.optDouble("completedHours", 0.0)
                val targetDiamonds = json.optLong("targetDiamonds", 50000L)
                val earnedDiamonds = json.optLong("earnedDiamonds", 0L)
                val activityInfo = json.optString("activityInfo", "")
                val notes = json.optString("notes", "")

                val res = adminService.updateLinkUserWork(
                    session = session,
                    id = id,
                    status = status,
                    assignedWork = assignedWork,
                    workStatus = workStatus,
                    workCategory = workCategory,
                    targetHours = targetHours,
                    completedHours = completedHours,
                    targetDiamonds = targetDiamonds,
                    earnedDiamonds = earnedDiamonds,
                    activityInfo = activityInfo,
                    notes = notes,
                    clientIp = clientIp
                )
                if (res.isSuccess) {
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("message", "Work assignment updated successfully")
                        put("user", res.getOrThrow())
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply {
                        put("success", false)
                        put("message", res.exceptionOrNull()?.message ?: "Failed to update work assignment")
                    }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            (path == "/api/admin/link/users/delete" || (path == "/api/admin/link/users" && method == "DELETE")) -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val id = json.optString("id", "").ifBlank { params["id"] ?: "" }

                val res = adminService.deleteLinkUser(session, id, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("message", "User removed from link")
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply {
                        put("success", false)
                        put("message", res.exceptionOrNull()?.message ?: "Failed to remove user")
                    }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // User Management
            path == "/api/admin/users" && method == "GET" -> {
                val query = params["query"] ?: ""
                val role = params["role"] ?: ""
                val users = adminService.getAllUsers(session, query, role)
                val arr = JSONArray(users)
                sendResponse(outputStream, 200, "OK", "application/json", arr.toString())
            }

            path == "/api/admin/users/role" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val targetUserId = json.optString("userId", "")
                val newRole = json.optString("role", "User")
                val permissions = json.optString("permissions", "")
                val assignedArea = json.optString("assignedArea", "")
                val notes = json.optString("notes", "")

                val res = adminService.assignUserRole(session, targetUserId, newRole, permissions, assignedArea, notes, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Role updated successfully") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Role assignment failed") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            path == "/api/admin/users/ban" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val targetUserId = json.optString("userId", "")
                val shouldBan = json.optBoolean("isBanned", true)
                val reason = json.optString("reason", "Administrative action")

                val res = adminService.setUserBanStatus(session, targetUserId, shouldBan, reason, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", if (shouldBan) "User banned" else "User unbanned") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed to update ban status") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            path == "/api/admin/users/balance" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val targetUserId = json.optString("userId", "")
                val deltaCoins = json.optLong("coins", 0L)
                val deltaDiamonds = json.optLong("diamonds", 0L)
                val reason = json.optString("reason", "Official 1 balance adjustment")

                val res = adminService.adjustUserBalance(session, targetUserId, deltaCoins, deltaDiamonds, reason, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Balance adjusted successfully") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed to adjust balance") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Room Management
            path == "/api/admin/rooms" && method == "GET" -> {
                val rooms = adminService.getAllVoiceRooms(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(rooms).toString())
            }

            path == "/api/admin/rooms/action" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val roomId = json.optString("roomId", "")
                val action = json.optString("action", "")
                val payload = json.optString("payload", "")

                val res = adminService.manageRoom(session, roomId, action, payload, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Room action executed") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed room action") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Agency Management
            path == "/api/admin/agencies" && method == "GET" -> {
                val agencies = adminService.getAllAgencies(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(agencies).toString())
            }

            // Withdrawal Management
            path == "/api/admin/withdrawals" && method == "GET" -> {
                val withdrawals = adminService.getAllWithdrawals(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(withdrawals).toString())
            }

            path == "/api/admin/withdrawals/action" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val txId = json.optString("txId", "")
                val action = json.optString("action", "APPROVE")
                val notes = json.optString("notes", "Processed by Official 1")

                val res = adminService.handleWithdrawalAction(session, txId, action, notes, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Withdrawal $action processed successfully") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed to process withdrawal") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Official 1 Frame Management System Endpoints
            path == "/api/admin/frames/definitions" && method == "GET" -> {
                val defs = adminService.getOfficialFrameDefinitions()
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(defs).toString())
            }

            path == "/api/admin/frames/history" && method == "GET" -> {
                val query = params["query"] ?: ""
                val history = adminService.getFrameAssignments(session, query)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(history).toString())
            }

            path == "/api/admin/frames/check-conflict" && method == "GET" -> {
                val targetUserId = params["userId"]?.trim() ?: ""
                adminService.checkAndCleanExpiredFrames()
                val active = if (targetUserId.isNotBlank()) {
                    adminService.getFrameAssignments(session).find {
                        it.optString("userId") == targetUserId && it.optString("status") == "Active"
                    }
                } else null

                val resp = JSONObject().apply {
                    put("hasConflict", active != null)
                    if (active != null) {
                        put("conflictingFrame", active)
                        put("message", "User already has active frame '${active.optString("frameName")}' expiring on ${active.optString("expiryDateFormatted")}")
                    }
                }
                sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
            }

            path == "/api/admin/frames/send" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val frameId = json.optString("frameId", "")
                val targetUserId = json.optString("userId", "")
                val days = json.optInt("days", 0)

                val res = adminService.sendFrame(
                    session = session,
                    frameId = frameId,
                    userId = targetUserId,
                    days = days,
                    clientIp = clientIp
                )

                if (res.isSuccess) {
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("message", "Official frame assigned successfully!")
                        put("assignment", res.getOrThrow())
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply {
                        put("success", false)
                        put("message", res.exceptionOrNull()?.message ?: "Failed to assign frame")
                    }
                    val statusCode = if (err.optString("message").startsWith("Conflict")) 409 else 400
                    sendResponse(outputStream, statusCode, "Error", "application/json", err.toString())
                }
            }

            path == "/api/admin/frames/revoke" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val assignmentId = json.optString("assignmentId", "")

                val res = adminService.revokeFrame(session, assignmentId, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply {
                        put("success", true)
                        put("message", "Official frame revoked successfully")
                    }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply {
                        put("success", false)
                        put("message", res.exceptionOrNull()?.message ?: "Failed to revoke frame")
                    }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Gift & Frame Management
            path == "/api/admin/gifts" && method == "GET" -> {
                val items = adminService.getAllStoreItems(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(items).toString())
            }

            // Moderation Reports
            path == "/api/admin/reports" && method == "GET" -> {
                val status = params["status"] ?: ""
                val reports = adminService.getReports(session, status)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(reports).toString())
            }

            path == "/api/admin/reports/resolve" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val reportId = json.optString("reportId", "")
                val status = json.optString("status", "Resolved")
                val notes = json.optString("notes", "Handled by admin")

                val res = adminService.resolveReport(session, reportId, status, notes, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Report marked as $status") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed to resolve report") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Notifications Broadcast
            path == "/api/admin/notifications" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val targetUserId = json.optString("targetUserId", "").ifBlank { null }
                val title = json.optString("title", "Official Notice")
                val message = json.optString("message", "")

                val res = adminService.sendSystemNotification(session, targetUserId, title, message, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Notification broadcasted successfully") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed to broadcast notification") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // App Settings
            path == "/api/admin/configs" && method == "GET" -> {
                val configs = adminService.getAppConfigs(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(configs).toString())
            }

            path == "/api/admin/configs" && method == "POST" -> {
                val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
                val key = json.optString("key", "")
                val value = json.optString("value", "")
                val category = json.optString("category", "general")

                val res = adminService.updateAppConfig(session, key, value, category, clientIp)
                if (res.isSuccess) {
                    val resp = JSONObject().apply { put("success", true); put("message", "Configuration '$key' updated") }
                    sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
                } else {
                    val err = JSONObject().apply { put("success", false); put("message", res.exceptionOrNull()?.message ?: "Failed to update configuration") }
                    sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
                }
            }

            // Security Audit Trail
            path == "/api/admin/audit-logs" && method == "GET" -> {
                val limit = params["limit"]?.toIntOrNull() ?: 100
                val logs = adminService.getAuditLogs(session, limit)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(logs).toString())
            }

            else -> {
                sendResponse(outputStream, 404, "Not Found", "text/plain", "404 Not Found")
            }
        }
    }

    private fun parseQueryParams(queryString: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        if (queryString.isBlank()) return params
        queryString.split("&").forEach { pair ->
            val idx = pair.indexOf("=")
            if (idx > 0) {
                val key = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                val value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                params[key] = value
            }
        }
        return params
    }

    private fun sendResponse(
        outputStream: OutputStream,
        statusCode: Int,
        statusText: String,
        contentType: String,
        body: String
    ) {
        val bodyBytes = body.toByteArray(StandardCharsets.UTF_8)
        val headers = StringBuilder()
        headers.append("HTTP/1.1 $statusCode $statusText\r\n")
        headers.append("Content-Type: $contentType\r\n")
        headers.append("Content-Length: ${bodyBytes.size}\r\n")
        headers.append("Access-Control-Allow-Origin: *\r\n")
        headers.append("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n")
        headers.append("Access-Control-Allow-Headers: Content-Type, Authorization\r\n")
        headers.append("Connection: close\r\n")
        headers.append("\r\n")

        outputStream.write(headers.toString().toByteArray(StandardCharsets.UTF_8))
        if (bodyBytes.isNotEmpty()) {
            outputStream.write(bodyBytes)
        }
        outputStream.flush()
    }
}
