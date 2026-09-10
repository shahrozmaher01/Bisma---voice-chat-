package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerSettingsView(
    appConfigs: List<AppConfigEntity>,
    auditLogs: List<AuditLogEntity>,
    onUpdateConfig: (key: String, value: String, desc: String) -> Unit,
    onBroadcastAnnouncement: (title: String, message: String, audience: String) -> Unit
) {
    // 7 Settings Sections:
    // 0: General Settings
    // 1: User Settings
    // 2: Room Settings
    // 3: Coin Settings
    // 4: Gift Settings
    // 5: Role & Permissions (12 roles)
    // 6: Security & Audit
    var selectedSectionIndex by remember { mutableStateOf(0) }
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    // General Settings State
    var appName by remember { mutableStateOf("AURA Live") }
    var maintenanceMode by remember { mutableStateOf(false) }
    var registrationEnabled by remember { mutableStateOf(true) }

    // User Settings State
    var maxLevel by remember { mutableStateOf("100") }
    var xpMultiplier by remember { mutableStateOf("1.5x") }
    var vipDiscountEnabled by remember { mutableStateOf(true) }
    var autoBanReportThreshold by remember { mutableStateOf("5") }

    // Room Settings State
    var defaultSeatCount by remember { mutableStateOf("8 Seats") }
    var maxConcurrentRooms by remember { mutableStateOf("500") }
    var allowRoomLocking by remember { mutableStateOf(true) }
    var allowLuckyBag by remember { mutableStateOf(true) }

    // Coin Settings State
    var coinExchangeRate by remember { mutableStateOf("10,000 Coins / $1.00 USD") }
    var minCashoutDiamonds by remember { mutableStateOf("100,000 💎") }
    var platformCommissionPercent by remember { mutableStateOf("15%") }

    // Gift Settings State
    val giftsList = remember {
        mutableStateListOf(
            Triple("Rose 🌹", "10 Coins", true),
            Triple("Microphone 🎤", "100 Coins", true),
            Triple("Sports Car 🏎️", "2,500 Coins", true),
            Triple("Private Jet ✈️", "10,000 Coins", true),
            Triple("Imperial Castle 🏰", "50,000 Coins", true)
        )
    }

    // 12 Official Roles for Role & Permissions Settings:
    val twelveRoles = remember {
        listOf(
            "Owner",
            "Super Admin",
            "Admin",
            "Admin Leader",
            "Manager",
            "BD",
            "BD Leader",
            "Agency",
            "Agency Leader",
            "Host",
            "Coin Reseller",
            "Super Coin Reseller"
        )
    }
    var selectedRoleForPerms by remember { mutableStateOf("Owner") }
    val rolePermissionsState = remember {
        mutableStateMapOf(
            "Full System Master Access" to true,
            "Financial Mint & Adjust" to true,
            "Ban & Delete Accounts" to true,
            "Moderate Voice Rooms" to true,
            "Approve Cashout Requests" to true,
            "Manage Agency Contracts" to true
        )
    }

    // Broadcast Dialog
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMsg by remember { mutableStateOf("") }
    var broadcastAudience by remember { mutableStateOf("All Users") }

    val sectionTabs = listOf(
        "General",
        "Users",
        "Rooms",
        "Coins",
        "Gifts",
        "12 Roles",
        "Security & Audit"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Owner Settings",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Master Configurations & Security Policy",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { showBroadcastDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A85)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Section Navigation Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(sectionTabs.size) { idx ->
                val isSel = selectedSectionIndex == idx
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF00E5FF) else Color(0xFF131D31))
                        .border(1.dp, if (isSel) Color(0xFF00E5FF) else Color(0xFF223250), RoundedCornerShape(8.dp))
                        .clickable { selectedSectionIndex = idx }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sectionTabs[idx],
                        color = if (isSel) Color.Black else Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Settings Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            when (selectedSectionIndex) {
                // 0: GENERAL SETTINGS
                0 -> {
                    item {
                        SettingsCard(title = "General System Settings") {
                            OutlinedTextField(
                                value = appName,
                                onValueChange = { appName = it },
                                label = { Text("Application Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            SettingsToggleRow(
                                title = "Maintenance Mode",
                                subtitle = "Temporarily suspend user logins for server upgrades",
                                checked = maintenanceMode,
                                onCheckedChange = {
                                    maintenanceMode = it
                                    onUpdateConfig("MAINTENANCE_MODE", it.toString(), "Toggled maintenance mode")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SettingsToggleRow(
                                title = "User Registration",
                                subtitle = "Allow new accounts to register via Phone/OAuth",
                                checked = registrationEnabled,
                                onCheckedChange = {
                                    registrationEnabled = it
                                    onUpdateConfig("REGISTRATION_ENABLED", it.toString(), "Toggled registration")
                                }
                            )
                        }
                    }
                }

                // 1: USER SETTINGS
                1 -> {
                    item {
                        SettingsCard(title = "User & Account Settings") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = maxLevel,
                                    onValueChange = { maxLevel = it },
                                    label = { Text("Max User Level") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = xpMultiplier,
                                    onValueChange = { xpMultiplier = it },
                                    label = { Text("XP Rate Multiplier") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            SettingsToggleRow(
                                title = "VIP Store Discounts",
                                subtitle = "Provide tiered coin discounts for VIP badge holders",
                                checked = vipDiscountEnabled,
                                onCheckedChange = { vipDiscountEnabled = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = autoBanReportThreshold,
                                onValueChange = { autoBanReportThreshold = it },
                                label = { Text("Auto-Ban Violation Report Threshold") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // 2: ROOM SETTINGS
                2 -> {
                    item {
                        SettingsCard(title = "Voice Room Settings") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = defaultSeatCount,
                                    onValueChange = { defaultSeatCount = it },
                                    label = { Text("Default Seats (8, 12, 16)") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = maxConcurrentRooms,
                                    onValueChange = { maxConcurrentRooms = it },
                                    label = { Text("Max Concurrent Rooms") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            SettingsToggleRow(
                                title = "Allow Room Locking",
                                subtitle = "Room hosts can set 4-digit PIN passwords",
                                checked = allowRoomLocking,
                                onCheckedChange = { allowRoomLocking = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SettingsToggleRow(
                                title = "Lucky Bag Feature",
                                subtitle = "Allow users to drop coin lucky bags on active seats",
                                checked = allowLuckyBag,
                                onCheckedChange = { allowLuckyBag = it }
                            )
                        }
                    }
                }

                // 3: COIN SETTINGS
                3 -> {
                    item {
                        SettingsCard(title = "Coin & Economic Settings") {
                            OutlinedTextField(
                                value = coinExchangeRate,
                                onValueChange = { coinExchangeRate = it },
                                label = { Text("Coin Exchange Rate") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = minCashoutDiamonds,
                                    onValueChange = { minCashoutDiamonds = it },
                                    label = { Text("Min Withdrawal") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = platformCommissionPercent,
                                    onValueChange = { platformCommissionPercent = it },
                                    label = { Text("Platform Fee") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Transaction Rules: Daily peer transfer limit is capped at 500,000 coins per normal user account.", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                    }
                }

                // 4: GIFT SETTINGS
                4 -> {
                    item {
                        SettingsCard(title = "Gift Management & Catalog") {
                            Text("Manage prices and in-room availability for official virtual gifts:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                giftsList.forEachIndexed { idx, gift ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0A0F1D),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(gift.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                Text("Price: ${gift.second}", color = Color(0xFFFFD700), fontSize = 11.sp)
                                            }
                                            Switch(
                                                checked = gift.third,
                                                onCheckedChange = { checked ->
                                                    giftsList[idx] = Triple(gift.first, gift.second, checked)
                                                },
                                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E676))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5: ROLE & PERMISSION SETTINGS (12 OFFICIAL ROLES)
                5 -> {
                    item {
                        SettingsCard(title = "Manage Permissions for 12 Official Roles") {
                            Text("Select an official role to inspect and configure authorized privileges:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            // 12 Roles Grid / List
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(twelveRoles) { role ->
                                    val isSel = selectedRoleForPerms == role
                                    Surface(
                                        onClick = { selectedRoleForPerms = role },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSel) Color(0xFFFFD700) else Color(0xFF0A0F1D),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFFFFD700) else Color(0xFF223250))
                                    ) {
                                        Text(
                                            text = role,
                                            color = if (isSel) Color.Black else Color.White.copy(alpha = 0.8f),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Privileges for '$selectedRoleForPerms'", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                rolePermissionsState.keys.forEach { perm ->
                                    val isChecked = rolePermissionsState[perm] ?: false
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(perm, color = Color.White, fontSize = 12.sp)
                                        Switch(
                                            checked = if (selectedRoleForPerms == "Owner") true else isChecked,
                                            enabled = selectedRoleForPerms != "Owner", // Owner is un-revocable
                                            onCheckedChange = { rolePermissionsState[perm] = it },
                                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6: SECURITY SETTINGS & AUDIT LOGS
                6 -> {
                    item {
                        SettingsCard(title = "Security & Immutable Audit Trail") {
                            Text("Security Overview & Active Sessions", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0A0F1D),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("• Root Owner Device: Pixel Fold (Android 14) • Authenticated", color = Color.White, fontSize = 11.sp)
                                    Text("• Login History: 192.168.1.1 (Secure TLS 1.3 • AES-256)", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                    Text("• Active Sessions: 1 Desktop Console, 1 Mobile Emulator", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Real-Time Audit Logs (${auditLogs.size} Events)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))

                            if (auditLogs.isEmpty()) {
                                Text("No audit log events recorded yet.", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    auditLogs.take(10).forEach { log ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF0A0F1D),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(log.action, color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    Text(dateFormatter.format(Date(log.timestamp)), color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                                                }
                                                Text("By: ${log.adminName} (${log.adminRole}) • Target: ${log.targetName ?: log.targetId}", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                                                log.newValue?.let {
                                                    Text("Detail: $it", color = Color(0xFFFFD700), fontSize = 10.sp)
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
    }

    // BROADCAST MODAL
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank() && broadcastMsg.isNotBlank()) {
                            onBroadcastAnnouncement(broadcastTitle, broadcastMsg, broadcastAudience)
                            showBroadcastDialog = false
                            broadcastTitle = ""
                            broadcastMsg = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A85))
                ) {
                    Text("Broadcast Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showBroadcastDialog = false }) { Text("Cancel") } },
            title = { Text("Official Platform Broadcast", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Announcement Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = broadcastMsg,
                        onValueChange = { broadcastMsg = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Target Audience:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("All Users", "Managers & Admins", "Agencies & Hosts").forEach { aud ->
                            val isSel = broadcastAudience == aud
                            Surface(
                                onClick = { broadcastAudience = aud },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFFFF2A85) else Color(0xFF0A0F1D),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    aud,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF131D31),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
fun SettingsToggleRow(
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
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF))
        )
    }
}
