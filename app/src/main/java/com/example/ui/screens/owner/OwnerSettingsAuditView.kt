package com.example.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppConfigEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.LoginLogItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerSettingsAuditView(
    auditLogs: List<AuditLogEntity>,
    configs: List<AppConfigEntity>,
    onBroadcast: (title: String, message: String, audience: String) -> Unit,
    onUpdateConfig: (key: String, value: String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Announcements, 1: App Settings & Maintenance, 2: Security & Login Logs, 3: Audit Trail

    // Broadcast State
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastAudience by remember { mutableStateOf("All Users") }

    // Settings State
    var maintenanceEnabled by remember {
        mutableStateOf(configs.find { it.key == "maintenance_mode" }?.value?.toBoolean() ?: false)
    }
    var registrationsOpen by remember {
        mutableStateOf(configs.find { it.key == "registrations_enabled" }?.value?.toBoolean() ?: true)
    }
    var voiceRoomMaxSeats by remember {
        mutableStateOf(configs.find { it.key == "voice_room_max_seats" }?.value ?: "12")
    }

    // Mock Login Logs
    val mockLoginLogs = remember {
        listOf(
            LoginLogItem("1", "565656565666555", "Owner", "Owner (Root)", "182.185.12.98", "Samsung Galaxy S24 Ultra", "Success", System.currentTimeMillis() - 120000),
            LoginLogItem("2", "adm_991", "Kashif Admin", "Admin Leader", "110.38.45.19", "Pixel 8 Pro", "Success", System.currentTimeMillis() - 3600000),
            LoginLogItem("3", "mgr_303", "Sara Manager", "Room Manager", "39.42.112.5", "iPhone 15 Pro", "Success", System.currentTimeMillis() - 7200000),
            LoginLogItem("4", "usr_intruder", "Unknown", "Unverified", "203.11.89.4", "Unknown Linux Host", "Failed (Invalid Credentials)", System.currentTimeMillis() - 14400000)
        )
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Sub tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF131D2F))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("📢 Broadcast", "⚙️ System Config", "🔐 Login Logs", "📜 Audit Trail").forEachIndexed { index, label ->
                val isSelected = activeSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
                        .clickable { activeSubTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        when (activeSubTab) {
            // TAB 0: BROADCAST ANNOUNCEMENT
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Push Official Broadcast Notification", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text("Instant delivery to user notifications with Owner badge", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = broadcastTitle,
                                    onValueChange = { broadcastTitle = it },
                                    label = { Text("Announcement Title") },
                                    placeholder = { Text("e.g. Ramadan Super Star Event Now Live!", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth().testTag("broadcast_title_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = broadcastMessage,
                                    onValueChange = { broadcastMessage = it },
                                    label = { Text("Announcement Content") },
                                    placeholder = { Text("Write full message body...", color = Color.Gray) },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth().testTag("broadcast_msg_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Select Target Audience:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("All Users", "Managers & Admins", "Agencies & Hosts").forEach { aud ->
                                        FilterChip(
                                            selected = broadcastAudience == aud,
                                            onClick = { broadcastAudience = aud },
                                            label = { Text(aud, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF00E5FF),
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        onBroadcast(broadcastTitle, broadcastMessage, broadcastAudience)
                                        broadcastTitle = ""
                                        broadcastMessage = ""
                                    },
                                    enabled = broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("send_broadcast_btn")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Broadcast to $broadcastAudience 📢", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: SYSTEM CONFIG & MAINTENANCE
            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Platform Switches & Security Modes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(14.dp))

                                // Maintenance Mode Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Emergency Maintenance Mode", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("When active, non-staff users cannot open live rooms", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = maintenanceEnabled,
                                        onCheckedChange = {
                                            maintenanceEnabled = it
                                            onUpdateConfig("maintenance_mode", it.toString())
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF5252), checkedTrackColor = Color(0xFFFF5252).copy(alpha = 0.4f))
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF223250))

                                // Registrations Switch
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Public Registration", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Allow new guest signups and phone bindings", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    }
                                    Switch(
                                        checked = registrationsOpen,
                                        onCheckedChange = {
                                            registrationsOpen = it
                                            onUpdateConfig("registrations_enabled", it.toString())
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676), checkedTrackColor = Color(0xFF00E676).copy(alpha = 0.4f))
                                    )
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF223250))

                                // Max Seats Selector
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Voice Room Max Mic Capacity", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Default maximum seats in new voice rooms", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("8", "12", "16").forEach { seats ->
                                            FilterChip(
                                                selected = voiceRoomMaxSeats == seats,
                                                onClick = {
                                                    voiceRoomMaxSeats = seats
                                                    onUpdateConfig("voice_room_max_seats", seats)
                                                },
                                                label = { Text(seats) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: SECURITY & LOGIN LOGS
            2 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text("Administrative Login History & Security Alerts", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    items(mockLoginLogs) { log ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (log.status.startsWith("Success")) Color(0xFF00E676).copy(alpha = 0.3f) else Color(0xFFFF5252).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (log.status.startsWith("Success")) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF5252).copy(alpha = 0.15f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (log.status.startsWith("Success")) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (log.status.startsWith("Success")) Color(0xFF00E676) else Color(0xFFFF5252),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(log.userName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF2979FF)) {
                                            Text(log.role, color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("IP: ${log.ipAddress} • Device: ${log.deviceModel}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text(dateFormatter.format(Date(log.timestamp)), color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                                Surface(shape = RoundedCornerShape(4.dp), color = if (log.status.startsWith("Success")) Color(0xFF00E676) else Color(0xFFFF5252)) {
                                    Text(if (log.status.startsWith("Success")) "OK" else "BLOCKED", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: AUDIT TRAIL
            3 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text("Immutable Administrative Audit Trail (${auditLogs.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (auditLogs.isEmpty()) {
                        item {
                            Text("No actions logged yet in this session.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                        }
                    } else {
                        items(auditLogs, key = { it.id }) { log ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131C2E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(log.action, color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(dateFormatter.format(Date(log.timestamp)), color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Performer: ${log.adminName} (${log.adminRole})", color = Color.White, fontSize = 12.sp)
                                    Text("Target ${log.targetType}: ${log.targetName ?: log.targetId}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                    if (!log.newValue.isNullOrBlank()) {
                                        Text("Details: ${log.newValue}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
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
