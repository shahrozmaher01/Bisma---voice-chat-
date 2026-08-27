package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BismaRepository
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    repository: BismaRepository,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var privacyRoomIncognito by remember { mutableStateOf(false) }
    var pushNotifications by remember { mutableStateOf(true) }
    var voiceDenoiseEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "System Settings & Privacy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                item {
                    Text("Audio & Room Preferences", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SettingToggleItem(
                                title = "AI Noise Cancellation",
                                subtitle = "Eliminate background noise & microphone echo",
                                checked = voiceDenoiseEnabled,
                                onCheckedChange = { voiceDenoiseEnabled = it }
                            )
                            Divider(color = SurfaceCardBorder)
                            SettingToggleItem(
                                title = "Push Notifications",
                                subtitle = "Room invitations, friend messages & VIP rewards",
                                checked = pushNotifications,
                                onCheckedChange = { pushNotifications = it }
                            )
                            Divider(color = SurfaceCardBorder)
                            SettingToggleItem(
                                title = "Incognito Room Entry",
                                subtitle = "Hide entrance banner in public rooms",
                                checked = privacyRoomIncognito,
                                onCheckedChange = { privacyRoomIncognito = it }
                            )
                        }
                    }
                }

                item {
                    Text("Support & About", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            SettingLinkItem(icon = Icons.Default.Language, title = "App Language", value = "English (US)") {
                                Toast.makeText(context, "English, Urdu, Hindi, Arabic supported", Toast.LENGTH_SHORT).show()
                            }
                            Divider(color = SurfaceCardBorder)
                            SettingLinkItem(icon = Icons.Default.Security, title = "Safety & Community Guidelines", value = "Verified") {
                                Toast.makeText(context, "Safe community moderation active 24/7", Toast.LENGTH_SHORT).show()
                            }
                            Divider(color = SurfaceCardBorder)
                            SettingLinkItem(icon = Icons.Default.Feedback, title = "Feedback & Customer Support", value = "24/7 Live") {
                                Toast.makeText(context, "Support ticket created: support@bisma.app", Toast.LENGTH_SHORT).show()
                            }
                            Divider(color = SurfaceCardBorder)
                            SettingLinkItem(icon = Icons.Default.Info, title = "About Bisma Voice Chat", value = "v1.0.0 (Build 2026)") { }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF1744)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp)
                    ) {
                        Text("Log Out of Account", color = DarkRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Confirm Log Out", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out of Bisma Voice Chat?", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Log Out", color = DarkRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = subtitle, color = TextSecondary, fontSize = 10.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NeonPink, checkedTrackColor = NeonPink.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun SettingLinkItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, color = TextSecondary, fontSize = 11.sp)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}
