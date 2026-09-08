package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.repository.BismaRepository
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldYellow

enum class AdminPanelTab {
    OFFICIAL_1,
    OFFICIAL_2
}

@Composable
fun AdminPanelScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val official1Url by repository.adminWebServer.official1Url.collectAsState()
    val official2Url by repository.adminWebServer.official2Url.collectAsState()

    var activeTab by remember { mutableStateOf(AdminPanelTab.OFFICIAL_1) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadProgress by remember { mutableIntStateOf(0) }

    val currentUrl = when (activeTab) {
        AdminPanelTab.OFFICIAL_1 -> official1Url
        AdminPanelTab.OFFICIAL_2 -> official2Url
    }

    val panelName = when (activeTab) {
        AdminPanelTab.OFFICIAL_1 -> "Official Panel 1"
        AdminPanelTab.OFFICIAL_2 -> "Official Panel 2"
    }

    val credentialsHint = when (activeTab) {
        AdminPanelTab.OFFICIAL_1 -> "User: Sherry | Pass: bismajan56b@$56"
        AdminPanelTab.OFFICIAL_2 -> "User: Maz | ID: 41387 | Pass: 30484"
    }

    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // TOP APP BAR
        Surface(
            color = Color(0xFF101726),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("admin_panel_back_btn")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Tab Switcher Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                // Tab 1: Official Panel 1
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (activeTab == AdminPanelTab.OFFICIAL_1) ElectricCyan else Color.Transparent,
                                    modifier = Modifier
                                        .clickable {
                                            if (activeTab != AdminPanelTab.OFFICIAL_1) {
                                                activeTab = AdminPanelTab.OFFICIAL_1
                                                webViewInstance?.loadUrl(official1Url)
                                            }
                                        }
                                        .testTag("tab_official_panel_1")
                                ) {
                                    Text(
                                        text = "🛡️ Panel 1",
                                        color = if (activeTab == AdminPanelTab.OFFICIAL_1) Color(0xFF0B0F19) else Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }

                                // Tab 2: Official Panel 2
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (activeTab == AdminPanelTab.OFFICIAL_2) Color(0xFF8B5CF6) else Color.Transparent,
                                    modifier = Modifier
                                        .clickable {
                                            if (activeTab != AdminPanelTab.OFFICIAL_2) {
                                                activeTab = AdminPanelTab.OFFICIAL_2
                                                webViewInstance?.loadUrl(official2Url)
                                            }
                                        }
                                        .testTag("tab_official_panel_2")
                                ) {
                                    Text(
                                        text = "👑 Panel 2",
                                        color = if (activeTab == AdminPanelTab.OFFICIAL_2) Color.White else Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Action Icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // 1-Click Auto Verify Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x3300E5FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .clickable {
                                    val autoUrl = if (currentUrl.contains("?")) "$currentUrl&autologin=1" else "$currentUrl?autologin=1"
                                    webViewInstance?.loadUrl(autoUrl)
                                    Toast.makeText(context, "Verifying & Unlocking $panelName...", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("admin_auto_verify_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = "Auto Verify",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Auto Verify",
                                    color = ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Reload Button
                        IconButton(
                            onClick = {
                                webViewInstance?.reload()
                                Toast.makeText(context, "Reloading...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Open in External Browser
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl)).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not launch external browser", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.OpenInBrowser,
                                contentDescription = "Open in External Browser",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Copy Link
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Admin Link", currentUrl))
                                Toast.makeText(context, "Link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy Link",
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Sub-Bar: URL & Credentials info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = currentUrl,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = credentialsHint,
                        color = GoldYellow,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }

                // Loading progress bar
                AnimatedVisibility(visible = isLoading) {
                    LinearProgressIndicator(
                        progress = { loadProgress / 100f },
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = ElectricCyan,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        // EMBEDDED WEBVIEW
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            allowContentAccess = true
                            allowFileAccess = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                loadProgress = newProgress
                                if (newProgress >= 100) {
                                    isLoading = false
                                }
                            }
                        }
                        webViewInstance = this
                        loadUrl(currentUrl)
                    }
                },
                update = { webView ->
                    // Keep instance reference updated
                    webViewInstance = webView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("admin_panel_embedded_webview")
            )
        }
    }
}
