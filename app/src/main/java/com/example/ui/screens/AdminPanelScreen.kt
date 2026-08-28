package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.admin.AdminRole
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val currentUserRole by repository.currentUserRole.collectAsState(initial = null)
    val isServerRunning by repository.adminWebServer.isRunning.collectAsState()
    val serverUrl by repository.adminWebServer.serverUrl.collectAsState()

    val allRoles by repository.allUserRoles.collectAsState(initial = emptyList())
    val recentLogs by repository.recentAuditLogs.collectAsState(initial = emptyList())
    val pendingReports by repository.allReports.collectAsState(initial = emptyList())

    var showEmbeddedWebView by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        if (showEmbeddedWebView) {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showEmbeddedWebView = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Close Web View", tint = Color.White)
                        }
                        Text(
                            "Web Admin Console",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(serverUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Open in Chrome", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            webViewClient = WebViewClient()
                            webChromeClient = WebChromeClient()
                            loadUrl(serverUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "Super Admin Control Center 🛡️",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldYellow
                            )
                            Text(
                                text = "Web-based responsive dashboard & RBAC engine",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 40.dp)
                ) {
                    // Web Admin Server Hero Card
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Brush.horizontalGradient(listOf(NeonPink, GoldYellow)), RoundedCornerShape(16.dp))
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(if (isServerRunning) EmeraldGreen else Color.Red)
                                        )
                                        Text(
                                            if (isServerRunning) "WEB CONTROL PANEL ONLINE" else "INITIALIZING...",
                                            color = if (isServerRunning) EmeraldGreen else Color.Red,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Text(
                                        "PORT: 8080",
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    "Dedicated Web Dashboard for Chrome & Desktop",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )

                                Text(
                                    "Access the full-featured responsive admin web application in Google Chrome on mobile or desktop with real-time analytics, user role assignments, moderation queue, branding engine, and audit logs.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Surface(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            serverUrl,
                                            color = ElectricCyan,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Copy 📋",
                                            color = GoldYellow,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.clickable {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                clipboard.setPrimaryClip(ClipData.newPlainText("Admin URL", serverUrl))
                                                Toast.makeText(context, "Copied URL to clipboard!", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(serverUrl))
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Opening in browser: $serverUrl", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("🌐 Launch in Chrome", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { showEmbeddedWebView = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("📱 Quick Web View", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Authenticated Admin Status Card
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Current Session Identity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("${currentUser?.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("User ID: ${currentUser?.id}", color = TextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                    }

                                    Surface(
                                        color = GoldYellow.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(99.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldYellow.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            (currentUserRole?.role ?: "Super Admin").uppercase(),
                                            color = GoldYellow,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Platform Telemetry Overview Grid
                    item {
                        Text("Live Platform Metrics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassCard(modifier = Modifier.weight(1f)) {
                                Column {
                                    Text("Staff Members", color = TextSecondary, fontSize = 11.sp)
                                    Text("${allRoles.size}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text("Across 7 Tiers", color = ElectricCyan, fontSize = 10.sp)
                                }
                            }

                            GlassCard(modifier = Modifier.weight(1f)) {
                                Column {
                                    Text("Pending Flags", color = TextSecondary, fontSize = 11.sp)
                                    Text("${pendingReports.filter { it.status == "Pending" }.size}", color = if (pendingReports.any { it.status == "Pending" }) Color.Red else EmeraldGreen, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text("Moderation Queue", color = TextMuted, fontSize = 10.sp)
                                }
                            }

                            GlassCard(modifier = Modifier.weight(1f)) {
                                Column {
                                    Text("Audit Trail", color = TextSecondary, fontSize = 11.sp)
                                    Text("${recentLogs.size}", color = GoldYellow, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text("Immutable Logs", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    // Role Hierarchy Matrix Section
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🛡️ RBAC Role Hierarchy Levels", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("• Level 100: Super Admin (Root administrative authority)", color = GoldYellow, fontSize = 12.sp)
                                Text("• Level 80: Admin (Senior ops & user/room moderation)", color = NeonPink, fontSize = 12.sp)
                                Text("• Level 60: Manager (Room & community moderators)", color = ElectricCyan, fontSize = 12.sp)
                                Text("• Level 50: BD (Business Development & Agency liaisons)", color = EmeraldGreen, fontSize = 12.sp)
                                Text("• Level 40: Agency (Host recruitment & broadcaster events)", color = Color(0xFFC084FC), fontSize = 12.sp)
                                Text("• Level 30: Coin Reseller (Authorized merchant quotas)", color = Color(0xFFFF9100), fontSize = 12.sp)
                                Text("• Level 10: User (Standard consumer participant)", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }

                    // Recent Audit Logs preview
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("📜 Recent Audit Trail", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Latest Actions", color = TextMuted, fontSize = 11.sp)
                                }

                                if (recentLogs.isEmpty()) {
                                    Text("No administrative actions recorded yet.", color = TextMuted, fontSize = 12.sp)
                                } else {
                                    recentLogs.take(5).forEach { log ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("${log.action} on ${log.targetName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                                Text("By ${log.adminName} (${log.adminRole})", color = TextSecondary, fontSize = 10.sp)
                                            }
                                            Text(
                                                if (log.isSuccess) "SUCCESS" else "FAILED",
                                                color = if (log.isSuccess) EmeraldGreen else Color.Red,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
