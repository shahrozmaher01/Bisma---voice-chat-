package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.UserRoleAssignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerUserManagerView(
    allUsers: List<User>,
    allRoles: List<UserRoleAssignment>,
    onSendManager: (userId: String, role: String, permissions: List<String>, status: String, notes: String) -> Unit,
    onRemoveManager: (userId: String, reason: String) -> Unit,
    onAddAdmin: (userId: String, role: String, permissions: List<String>, notes: String) -> Unit,
    onRemoveAdmin: (userId: String, reason: String) -> Unit,
    onToggleBanUser: (userId: String, isBanned: Boolean, reason: String) -> Unit,
    onAdjustCoins: (userId: String, deltaCoins: Long, reason: String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf(1) } // 0: User Management, 1: Send Manager System, 2: Admin Management
    var searchQuery by remember { mutableStateOf("") }

    // Send Manager State
    var managerUserIdInput by remember { mutableStateOf("") }
    var verifiedUserForManager by remember { mutableStateOf<User?>(null) }
    var selectedManagerRole by remember { mutableStateOf("Room Manager") }
    var managerStatus by remember { mutableStateOf("Active") }
    var managerNotes by remember { mutableStateOf("") }
    val managerPermissions = remember {
        mutableStateMapOf(
            "MANAGE_USERS" to true,
            "MANAGE_ROOMS" to true,
            "VIEW_REPORTS" to true,
            "RESOLVE_REPORTS" to false,
            "MUTE_USERS" to true,
            "KICK_USERS" to true,
            "SEND_NOTIFICATIONS" to false
        )
    }
    var showConfirmManagerDialog by remember { mutableStateOf(false) }

    // Admin Management State
    var adminUserIdInput by remember { mutableStateOf("") }
    var verifiedUserForAdmin by remember { mutableStateOf<User?>(null) }
    var selectedAdminRole by remember { mutableStateOf("Admin") }
    var adminNotes by remember { mutableStateOf("") }
    val adminPermissions = remember {
        mutableStateMapOf(
            "MANAGE_MANAGERS" to true,
            "MANAGE_ROOMS" to true,
            "MANAGE_USERS" to true,
            "VIEW_REPORTS" to true,
            "RESOLVE_REPORTS" to true,
            "VIEW_AUDIT_LOGS" to true,
            "MANAGE_COINS" to false
        )
    }

    // User Profile Sheet state
    var inspectingUser by remember { mutableStateOf<User?>(null) }
    var banReasonInput by remember { mutableStateOf("") }
    var showBanDialog by remember { mutableStateOf(false) }

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
            listOf("Users & Moderation", "👑 Send Manager System", "🛡️ Admin Roster").forEachIndexed { index, label ->
                val isSelected = activeSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) Color(if (index == 1) 0xFF7C4DFF else 0xFF00E5FF) else Color.Transparent
                        )
                        .clickable { activeSubTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) (if (index == 1) Color.White else Color.Black) else Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        when (activeSubTab) {
            // TAB 0: USER MANAGEMENT
            0 -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by User ID or Name...", color = Color.Gray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00E5FF)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF223250),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("user_search_input")
                )

                val filteredUsers = remember(allUsers, searchQuery) {
                    if (searchQuery.isBlank()) allUsers else allUsers.filter {
                        it.id.contains(searchQuery.trim(), ignoreCase = true) ||
                                it.username.contains(searchQuery.trim(), ignoreCase = true)
                    }
                }

                Text(
                    text = "Total Accounts: ${filteredUsers.size}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredUsers, key = { it.id }) { user ->
                        val roleInfo = allRoles.find { it.userId == user.id }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (user.isBanned) Color(0xFFFF5252).copy(alpha = 0.5f) else Color(0xFF223250)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" },
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.username,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (user.vipLevel > 0) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFFFD700)
                                            ) {
                                                Text(
                                                    text = "VIP ${user.vipLevel}",
                                                    color = Color.Black,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        if (roleInfo != null && roleInfo.role != "User") {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF7C4DFF)
                                            ) {
                                                Text(
                                                    text = roleInfo.role,
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ID: ${user.id} • Lv.${user.userLevel} • ${user.coins} 🪙 • ${user.diamonds} 💎",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                    if (user.isBanned) {
                                        Text(
                                            text = "⛔ ACCOUNT BANNED",
                                            color = Color(0xFFFF5252),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { inspectingUser = user },
                                        modifier = Modifier.size(34.dp).testTag("inspect_user_${user.id}")
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = "Details", tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            if (user.isBanned) {
                                                onToggleBanUser(user.id, false, "Owner Unban")
                                            } else {
                                                inspectingUser = user
                                                showBanDialog = true
                                            }
                                        },
                                        modifier = Modifier.size(34.dp).testTag("ban_user_btn_${user.id}")
                                    ) {
                                        Icon(
                                            imageVector = if (user.isBanned) Icons.Default.LockOpen else Icons.Default.Block,
                                            contentDescription = "Ban/Unban",
                                            tint = if (user.isBanned) Color(0xFF00E676) else Color(0xFFFF5252),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: SEND MANAGER SYSTEM (DEDICATED)
            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Color(0xFF7C4DFF), modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Dedicated Send Manager System",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Search ID, verify profile & assign authorized Manager roles",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // User ID Search Input + Verify Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = managerUserIdInput,
                                        onValueChange = {
                                            managerUserIdInput = it
                                            verifiedUserForManager = null
                                        },
                                        placeholder = { Text("Enter target User ID...", color = Color.Gray, fontSize = 13.sp) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF7C4DFF),
                                            unfocusedBorderColor = Color(0xFF2E3D5C),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f).testTag("manager_user_id_input")
                                    )

                                    Button(
                                        onClick = {
                                            verifiedUserForManager = allUsers.find { it.id == managerUserIdInput.trim() }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                                        modifier = Modifier.testTag("verify_manager_user_btn")
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verify", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                // Verified User Preview Card & Duplicate Detection
                                if (verifiedUserForManager != null) {
                                    val target = verifiedUserForManager!!
                                    val currentRoleAssignment = allRoles.find { it.userId == target.id }
                                    val isAlreadyManager = currentRoleAssignment?.role?.contains("Manager", ignoreCase = true) == true

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isAlreadyManager) Color(0xFF261D12) else Color(0xFF16253B),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isAlreadyManager) Color(0xFFFFB300) else Color(0xFF00E676)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AsyncImage(
                                                model = target.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" },
                                                contentDescription = "Avatar",
                                                modifier = Modifier.size(48.dp).clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = target.username,
                                                        color = Color.White,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "(ID: ${target.id})",
                                                        color = Color(0xFF00E5FF),
                                                        fontSize = 12.sp
                                                    )
                                                }
                                                Text(
                                                    text = "Level ${target.userLevel} • VIP ${target.vipLevel} • ${target.coins} Coins",
                                                    color = Color.White.copy(alpha = 0.7f),
                                                    fontSize = 11.sp
                                                )
                                                if (isAlreadyManager) {
                                                    Text(
                                                        text = "⚠️ ALREADY ASSIGNED: ${currentRoleAssignment?.role}",
                                                        color = Color(0xFFFFB300),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                } else {
                                                    Text(
                                                        text = "✅ Verified: Ready for Manager Assignment",
                                                        color = Color(0xFF00E676),
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Select Manager Role & Department:",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Role chips
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("Room Manager", "Operations Manager", "Content Moderator", "Senior Manager").forEach { r ->
                                            val isSelected = selectedManagerRole == r
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { selectedManagerRole = r },
                                                label = { Text(r, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF7C4DFF),
                                                    selectedLabelColor = Color.White,
                                                    containerColor = Color(0xFF1A2744),
                                                    labelColor = Color.White.copy(alpha = 0.7f)
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Assign Manager Permissions:",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    // Permissions Checkboxes
                                    Column(modifier = Modifier.padding(top = 4.dp)) {
                                        managerPermissions.keys.forEach { perm ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { managerPermissions[perm] = !(managerPermissions[perm] ?: false) }
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = managerPermissions[perm] ?: false,
                                                    onCheckedChange = { managerPermissions[perm] = it },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = Color(0xFF7C4DFF),
                                                        checkmarkColor = Color.White
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = perm.replace("_", " "),
                                                    color = Color.White.copy(alpha = 0.85f),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Manager Status:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            listOf("Active", "Inactive").forEach { s ->
                                                val isSelected = managerStatus == s
                                                Button(
                                                    onClick = { managerStatus = s },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isSelected) (if (s == "Active") Color(0xFF00E676) else Color(0xFFFF5252)) else Color(0xFF1E293B)
                                                    ),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                                ) {
                                                    Text(s, color = if (isSelected) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { showConfirmManagerDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("send_manager_role_btn")
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isAlreadyManager) "Update Manager Role 👑" else "Send Manager Role 👑",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Assigned Managers List
                    item {
                        val managers = allRoles.filter { it.role.contains("Manager", ignoreCase = true) }
                        Text(
                            text = "Assigned Managers Roster (${managers.size})",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    val managers = allRoles.filter { it.role.contains("Manager", ignoreCase = true) }
                    if (managers.isEmpty()) {
                        item {
                            Text(
                                text = "No managers assigned yet. Use the tool above to send a Manager role.",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    } else {
                        items(managers, key = { it.userId }) { mgr ->
                            val userMatch = allUsers.find { it.id == mgr.userId }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF141D2D),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF7C4DFF).copy(alpha = 0.2f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = mgr.username.take(1).uppercase(),
                                                color = Color(0xFF7C4DFF),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = mgr.username.ifBlank { "User ${mgr.userId}" },
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF7C4DFF)
                                            ) {
                                                Text(
                                                    text = mgr.role,
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "ID: ${mgr.userId} • Assigned by ${mgr.assignedByName}",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Permissions: ${mgr.permissions.split(",").filter { it.isNotBlank() }.size} granted",
                                            color = Color(0xFF00E5FF),
                                            fontSize = 10.sp
                                        )
                                    }

                                    Button(
                                        onClick = { onRemoveManager(mgr.userId, "Owner Directive") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.testTag("remove_manager_${mgr.userId}")
                                    ) {
                                        Text("Remove", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: ADMIN MANAGEMENT
            2 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2979FF).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF2979FF), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Admin Staff Management",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Appoint Super Admins, Admin Leaders & Regional Admins",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = adminUserIdInput,
                                        onValueChange = {
                                            adminUserIdInput = it
                                            verifiedUserForAdmin = null
                                        },
                                        placeholder = { Text("Target User ID...", color = Color.Gray, fontSize = 13.sp) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF2979FF),
                                            unfocusedBorderColor = Color(0xFF2E3D5C),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        modifier = Modifier.weight(1f).testTag("admin_user_id_input")
                                    )

                                    Button(
                                        onClick = {
                                            verifiedUserForAdmin = allUsers.find { it.id == adminUserIdInput.trim() }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
                                        modifier = Modifier.testTag("verify_admin_user_btn")
                                    ) {
                                        Text("Verify", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (verifiedUserForAdmin != null) {
                                    val target = verifiedUserForAdmin!!
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Target: ${target.username} (ID: ${target.id})",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Select Admin Role:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("Super Admin", "Admin Leader", "Admin").forEach { role ->
                                            FilterChip(
                                                selected = selectedAdminRole == role,
                                                onClick = { selectedAdminRole = role },
                                                label = { Text(role, fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF2979FF),
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            val perms = adminPermissions.filter { it.value }.keys.toList()
                                            onAddAdmin(target.id, selectedAdminRole, perms, adminNotes)
                                            verifiedUserForAdmin = null
                                            adminUserIdInput = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("grant_admin_role_btn")
                                    ) {
                                        Text("Grant Admin Privilege 🛡️", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        val admins = allRoles.filter { it.role.contains("Admin", ignoreCase = true) }
                        Text(
                            text = "Current Platform Administrators (${admins.size})",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val admins = allRoles.filter { it.role.contains("Admin", ignoreCase = true) }
                    items(admins, key = { it.userId }) { adm ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF141D2D),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2979FF).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF2979FF), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = adm.username.ifBlank { "Admin ${adm.userId}" },
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Role: ${adm.role} • ID: ${adm.userId}",
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp
                                    )
                                }
                                Button(
                                    onClick = { onRemoveAdmin(adm.userId, "Revoked by Owner") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Revoke", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirm Manager Assignment Dialog
    if (showConfirmManagerDialog && verifiedUserForManager != null) {
        val target = verifiedUserForManager!!
        val permsList = managerPermissions.filter { it.value }.keys.toList()
        AlertDialog(
            onDismissRequest = { showConfirmManagerDialog = false },
            title = {
                Text("Confirm Manager Assignment", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column {
                    Text("Appoint ${target.username} (ID: ${target.id}) as:", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(selectedManagerRole, color = Color(0xFF7C4DFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Granted ${permsList.size} permissions with $managerStatus status.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSendManager(target.id, selectedManagerRole, permsList, managerStatus, managerNotes)
                        showConfirmManagerDialog = false
                        verifiedUserForManager = null
                        managerUserIdInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))
                ) {
                    Text("Confirm & Send Role 👑", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmManagerDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF162238)
        )
    }

    // Ban User Dialog
    if (showBanDialog && inspectingUser != null) {
        val target = inspectingUser!!
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            title = { Text("Ban User Account", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252)) },
            text = {
                Column {
                    Text("Specify administrative reason for banning ${target.username} (${target.id}):", color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = banReasonInput,
                        onValueChange = { banReasonInput = it },
                        placeholder = { Text("Reason (e.g. Terms violation, toxicity)...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleBanUser(target.id, true, banReasonInput.ifBlank { "Owner Moderation Ban" })
                        showBanDialog = false
                        banReasonInput = ""
                        inspectingUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Confirm Ban ⛔", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1F1A24)
        )
    }
}
