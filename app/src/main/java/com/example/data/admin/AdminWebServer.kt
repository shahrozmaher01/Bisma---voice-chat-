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
            Log.i(TAG, "Admin Web Server started at http://localhost:$activePort/admin")

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
            socket.soTimeout = 10000
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

            // Route handling
            when {
                path == "/" || path == "/admin" || path == "/index.html" -> {
                    val html = AdminWebPageTemplate.getHtml()
                    sendResponse(outputStream, 200, "OK", "text/html; charset=UTF-8", html)
                }

                path == "/api/admin/login" && method == "POST" -> {
                    handleLogin(outputStream, body, clientIp)
                }

                path.startsWith("/api/admin/") -> {
                    val authHeader = headers["authorization"] ?: ""
                    val token = authHeader.removePrefix("Bearer ").trim()
                    val session = adminService.securityManager.validateSession(token)

                    if (session == null) {
                        val err = JSONObject().apply {
                            put("success", false)
                            put("message", "Unauthorized. Please login again.")
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

        } catch (e: Exception) {
            Log.e(TAG, "Exception handling client request", e)
        } finally {
            try {
                socket.close()
            } catch (_: Exception) {}
        }
    }

    private suspend fun handleLogin(outputStream: OutputStream, body: String, clientIp: String) {
        val json = try { JSONObject(body) } catch (_: Exception) { JSONObject() }
        val idOrEmail = json.optString("idOrEmail", "")
        val password = json.optString("password", "")

        val res = adminService.securityManager.authenticateAdmin(idOrEmail, password, clientIp)
        if (res.isSuccess) {
            val session = res.getOrThrow()
            val resp = JSONObject().apply {
                put("success", true)
                put("session", JSONObject().apply {
                    put("token", session.token)
                    put("userId", session.userId)
                    put("username", session.username)
                    put("role", session.role.roleName)
                    put("permissions", JSONArray(session.permissions))
                    put("expiresAt", session.expiresAt)
                })
            }
            sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
        } else {
            val err = JSONObject().apply {
                put("success", false)
                put("message", res.exceptionOrNull()?.message ?: "Authentication failed")
            }
            sendResponse(outputStream, 400, "Bad Request", "application/json", err.toString())
        }
    }

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
                val resp = JSONObject().apply { put("success", true) }
                sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
            }

            path == "/api/admin/session" && method == "GET" -> {
                val resp = JSONObject().apply {
                    put("success", true)
                    put("session", JSONObject().apply {
                        put("token", session.token)
                        put("userId", session.userId)
                        put("username", session.username)
                        put("role", session.role.roleName)
                        put("permissions", JSONArray(session.permissions))
                    })
                }
                sendResponse(outputStream, 200, "OK", "application/json", resp.toString())
            }

            path == "/api/admin/dashboard" && method == "GET" -> {
                val stats = adminService.getDashboardStats(session)
                sendResponse(outputStream, 200, "OK", "application/json", stats.toString())
            }

            path == "/api/admin/users" && method == "GET" -> {
                val query = params["query"] ?: ""
                val role = params["role"] ?: ""
                val users = adminService.getAllUsers(session, query, role)
                val arr = JSONArray(users)
                sendResponse(outputStream, 200, "OK", "application/json", arr.toString())
            }

            path == "/api/admin/users/role" && method == "POST" -> {
                val json = JSONObject(body)
                val targetUserId = json.optString("targetUserId")
                val role = json.optString("role")
                val permissions = json.optString("permissions", "")
                val assignedArea = json.optString("assignedArea", null)
                val notes = json.optString("notes", null)

                val result = adminService.assignUserRole(session, targetUserId, role, permissions, assignedArea, notes, clientIp)
                if (result.isSuccess) {
                    sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply { put("success", true) }.toString())
                } else {
                    sendResponse(outputStream, 400, "Bad Request", "application/json", JSONObject().apply {
                        put("success", false)
                        put("message", result.exceptionOrNull()?.message ?: "Role change failed")
                    }.toString())
                }
            }

            path == "/api/admin/users/ban" && method == "POST" -> {
                val json = JSONObject(body)
                val targetUserId = json.optString("targetUserId")
                val isBanned = json.optBoolean("isBanned", false)
                val reason = json.optString("reason", "Administrative action")

                val result = adminService.setUserBanStatus(session, targetUserId, isBanned, reason, clientIp)
                if (result.isSuccess) {
                    sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply { put("success", true) }.toString())
                } else {
                    sendResponse(outputStream, 400, "Bad Request", "application/json", JSONObject().apply {
                        put("success", false)
                        put("message", result.exceptionOrNull()?.message ?: "Ban action failed")
                    }.toString())
                }
            }

            path == "/api/admin/users/balance" && method == "POST" -> {
                val json = JSONObject(body)
                val targetUserId = json.optString("targetUserId")
                val deltaCoins = json.optLong("deltaCoins", 0)
                val deltaDiamonds = json.optLong("deltaDiamonds", 0)
                val reason = json.optString("reason", "")

                val result = adminService.adjustUserBalance(session, targetUserId, deltaCoins, deltaDiamonds, reason, clientIp)
                if (result.isSuccess) {
                    sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply { put("success", true) }.toString())
                } else {
                    sendResponse(outputStream, 400, "Bad Request", "application/json", JSONObject().apply {
                        put("success", false)
                        put("message", result.exceptionOrNull()?.message ?: "Balance adjustment failed")
                    }.toString())
                }
            }

            path == "/api/admin/rooms" && method == "GET" -> {
                val rooms = adminService.getAllVoiceRooms(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(rooms).toString())
            }

            path == "/api/admin/rooms/action" && method == "POST" -> {
                val json = JSONObject(body)
                val roomId = json.optString("roomId")
                val action = json.optString("action")
                val payload = json.optString("payload", null)

                val result = adminService.manageRoom(session, roomId, action, payload, clientIp)
                if (result.isSuccess) {
                    sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply { put("success", true) }.toString())
                } else {
                    sendResponse(outputStream, 400, "Bad Request", "application/json", JSONObject().apply {
                        put("success", false)
                        put("message", result.exceptionOrNull()?.message ?: "Room action failed")
                    }.toString())
                }
            }

            path == "/api/admin/config" && method == "GET" -> {
                val configs = adminService.getAppConfigs(session)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(configs).toString())
            }

            path == "/api/admin/config" && method == "POST" -> {
                val json = JSONObject(body)
                val key = json.optString("key")
                val value = json.optString("value")
                val category = json.optString("category", "general")

                val result = adminService.updateAppConfig(session, key, value, category, clientIp)
                if (result.isSuccess) {
                    sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply { put("success", true) }.toString())
                } else {
                    sendResponse(outputStream, 400, "Bad Request", "application/json", JSONObject().apply {
                        put("success", false)
                        put("message", result.exceptionOrNull()?.message ?: "Config update failed")
                    }.toString())
                }
            }

            path == "/api/admin/reports" && method == "GET" -> {
                val status = params["status"] ?: ""
                val reports = adminService.getReports(session, status)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(reports).toString())
            }

            path == "/api/admin/reports/resolve" && method == "POST" -> {
                val json = JSONObject(body)
                val reportId = json.optString("reportId")
                val status = json.optString("status", "Resolved")
                val notes = json.optString("notes", "")

                val result = adminService.resolveReport(session, reportId, status, notes, clientIp)
                if (result.isSuccess) {
                    sendResponse(outputStream, 200, "OK", "application/json", JSONObject().apply { put("success", true) }.toString())
                } else {
                    sendResponse(outputStream, 400, "Bad Request", "application/json", JSONObject().apply {
                        put("success", false)
                        put("message", result.exceptionOrNull()?.message ?: "Report resolution failed")
                    }.toString())
                }
            }

            path == "/api/admin/audit-logs" && method == "GET" -> {
                val limit = params["limit"]?.toIntOrNull() ?: 100
                val logs = adminService.getAuditLogs(session, limit)
                sendResponse(outputStream, 200, "OK", "application/json", JSONArray(logs).toString())
            }

            else -> {
                sendResponse(outputStream, 404, "Not Found", "application/json", JSONObject().apply {
                    put("error", "Endpoint not found")
                }.toString())
            }
        }
    }

    private fun parseQueryParams(queryString: String): Map<String, String> {
        if (queryString.isBlank()) return emptyMap()
        val result = mutableMapOf<String, String>()
        val pairs = queryString.split("&")
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            if (idx > 0) {
                val key = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                val value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                result[key] = value
            }
        }
        return result
    }

    private fun sendResponse(
        outputStream: OutputStream,
        statusCode: Int,
        statusText: String,
        contentType: String,
        body: String
    ) {
        val bodyBytes = body.toByteArray(StandardCharsets.UTF_8)
        val header = StringBuilder()
            .append("HTTP/1.1 $statusCode $statusText\r\n")
            .append("Content-Type: $contentType\r\n")
            .append("Content-Length: ${bodyBytes.size}\r\n")
            .append("Access-Control-Allow-Origin: *\r\n")
            .append("Access-Control-Allow-Methods: GET, POST, OPTIONS\r\n")
            .append("Access-Control-Allow-Headers: Content-Type, Authorization\r\n")
            .append("Connection: close\r\n\r\n")

        outputStream.write(header.toString().toByteArray(StandardCharsets.UTF_8))
        outputStream.write(bodyBytes)
        outputStream.flush()
    }
}
