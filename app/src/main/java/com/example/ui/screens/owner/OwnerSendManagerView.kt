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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerSendManagerView(
    allUsers: List<User>,
    allRoles: List<UserRoleAssignment>,
    onSendOrAssignManager: (targetUserId: String, roleTitle: String, permissions: List<String>, status: String, notes: String) -> Unit,
    onRemoveManager: (targetUserId: String, reason: String) -> Unit
) {
    var searchUserIdInput by remember { mutableStateOf("") }
    var searchedUser by remember { mutableStateOf<User?>(null) }
    var searchError by remember { mutableStateOf<String?>(null) }

    // Manager Configuration Form
    var managerRoleType by remember { mutableStateOf("Manager") }
    var managerStatus by remember { mutableStateOf("Active") } // Active, Inactive

    // Permissions Checklist
    val allManagerPermissions = remember {
        listOf(
            "User Management" to "Manage user bans, mutes, and profile moderation",
            "Room Management" to "Monitor, lock, or end unauthorized voice rooms",
            "Host Management" to "Review streamer hours and target performance",
            "Agency Management" to "Assist agency leaders and register new hosts",
            "Reports" to "Investigate community flags and harassment reports",
            "Announcements" to "Post operational bulletins in community rooms",
            "Other Permissions" to "Owner-delegated operational tasks"
        )
    }

    val selectedPermissions = remember {
        mutableStateMapOf(
            "User Management" to true,
            "Room Management" to true,
            "Host Management" to true,
            "Agency Management" to false,
            "Reports" to true,
            "Announcements" to false,
            "Other Permissions" to false
        )
    }

    // Confirmation & Action feedback
    var showConfirmDialog by remember { mutableStateOf(false) }
    var successNotification by remember { mutableStateOf<String?>(null) }
    var duplicateAlert by remember { mutableStateOf<String?>(null) }

    // Remove Confirmation
    var managerToRemove by remember { mutableStateOf<UserRoleAssignment?>(null) }
    var removeReasonInput by remember { mutableStateOf("Administrative restructuring") }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // Filter Manager List
    val assignedManagers = remember(allRoles) {
        allRoles.filter { it.role.contains("Manager", ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Page Title & Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Send Manager",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dedicated Manager Assignment & Privileges",
                    color = Color(0xFF7C4DFF),
                    fontSize = 12.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF7C4DFF).copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF))
            ) {
                Text(
                    text = "${assignedManagers.size} Active Managers",
                    color = Color(0xFFB388FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Success Notification Banner
        AnimatedVisibility(visible = successNotification != null) {
            successNotification?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(msg, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { successNotification = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Duplicate Assignment Warning
        AnimatedVisibility(visible = duplicateAlert != null) {
            duplicateAlert?.let { alertMsg ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFAB00).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFAB00)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFAB00), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(alertMsg, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { duplicateAlert = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // STEP 1: SEARCH & VERIFY USER
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "1. Enter User ID & Search",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchUserIdInput,
                                onValueChange = {
                                    searchUserIdInput = it
                                    searchError = null
                                    duplicateAlert = null
                                },
                                placeholder = { Text("Enter User ID (e.g. 565656565666555)", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF7C4DFF),
                                    unfocusedBorderColor = Color(0xFF223250),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("send_manager_id_input")
                            )

                            Button(
                                onClick = {
                                    val trimmed = searchUserIdInput.trim()
                                    if (trimmed.isBlank()) {
                                        searchError = "Please enter a valid User ID."
                                        searchedUser = null
                                        return@Button
                                    }
                                    val found = allUsers.find { it.id == trimmed }
                                    if (found != null) {
                                        searchedUser = found
                                        searchError = null

                                        // Check for duplicate Manager assignments
                                        val existingManager = allRoles.find { it.userId == found.id && it.role.contains("Manager", ignoreCase = true) }
                                        if (existingManager != null) {
                                            duplicateAlert = "Note: ${found.username} is already an assigned ${existingManager.role}. Submitting will update their permissions."
                                        } else {
                                            duplicateAlert = null
                                        }
                                    } else {
                                        searchedUser = null
                                        searchError = "No user found with ID: $trimmed"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("search_manager_user_btn")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Search User", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        searchError?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(err, color = Color(0xFFFF5252), fontSize = 11.sp)
                        }

                        // Display Verified User Card
                        searchedUser?.let { u ->
                            val currentRole = allRoles.find { it.userId == u.id }?.role ?: "Normal User"
                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Verified User Profile", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF0A0F1D),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = u.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" },
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp, Color(0xFF7C4DFF), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(u.username, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("User ID: ${u.id}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Current Role: $currentRole", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (u.isBanned) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFF00E676).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    if (u.isBanned) "Banned" else "Active",
                                                    color = if (u.isBanned) Color(0xFFFF5252) else Color(0xFF00E676),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
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

            // STEP 2: MANAGER ROLE & STATUS SELECTION
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "2. Manager Role & Status",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Role Type
                        Text("Select Role Title", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Manager", "Room Manager", "Operations Manager", "Agency Manager").forEach { title ->
                                val isSel = managerRoleType == title
                                Surface(
                                    onClick = { managerRoleType = title },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) Color(0xFF7C4DFF).copy(alpha = 0.25f) else Color(0xFF0A0F1D),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFF7C4DFF) else Color(0xFF1E2D4A)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = title,
                                        color = if (isSel) Color.White else Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status Selection
                        Text("Manager Status", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Active" to Color(0xFF00E676), "Inactive" to Color(0xFFFF5252)).forEach { (st, color) ->
                                val isSel = managerStatus == st
                                Surface(
                                    onClick = { managerStatus = st },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSel) color.copy(alpha = 0.2f) else Color(0xFF0A0F1D),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) color else Color(0xFF1E2D4A)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = st,
                                        color = if (isSel) color else Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 3: MANAGER PERMISSIONS
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "3. Select Manager Permissions",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Owner-controlled privilege delegation",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            allManagerPermissions.forEach { (perm, desc) ->
                                val isChecked = selectedPermissions[perm] ?: false
                                Surface(
                                    onClick = { selectedPermissions[perm] = !isChecked },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isChecked) Color(0xFF7C4DFF).copy(alpha = 0.12f) else Color(0xFF0A0F1D),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isChecked) Color(0xFF7C4DFF).copy(alpha = 0.6f) else Color(0xFF1E2D4A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { selectedPermissions[perm] = it },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF7C4DFF),
                                                uncheckedColor = Color.White.copy(alpha = 0.4f)
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(perm, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(desc, color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Send Manager Button
                        Button(
                            onClick = {
                                if (searchedUser == null) {
                                    searchError = "Please search and select a user first."
                                    return@Button
                                }
                                showConfirmDialog = true
                            },
                            enabled = searchedUser != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7C4DFF),
                                disabledContainerColor = Color(0xFF7C4DFF).copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("send_manager_submit_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Manager", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // STEP 4: CURRENT ASSIGNED MANAGERS LIST
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Assigned Manager List",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${assignedManagers.size} Staff",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            if (assignedManagers.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF131D31),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "No managers assigned yet. Use the tool above to appoint a new manager.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(assignedManagers, key = { it.userId }) { manager ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF131D31),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF7C4DFF).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C4DFF)),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("👑", fontSize = 18.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(manager.username, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF7C4DFF).copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                manager.role,
                                                color = Color(0xFFB388FF),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text("Manager ID: ${manager.userId}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    Text("Assigned Date: ${dateFormatter.format(Date(manager.assignedAt))}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                }

                                Button(
                                    onClick = {
                                        managerToRemove = manager
                                        removeReasonInput = "Revoked by Owner"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("remove_manager_${manager.userId}")
                                ) {
                                    Text("Remove", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Permissions list
                            Text("Permissions: ${manager.permissions.ifBlank { "Standard Manager Rights" }}", color = Color(0xFF00E5FF), fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Last Activity: Moderated room 24m ago • Status: ${manager.notes?.ifBlank { "Active" } ?: "Active"}", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }

    // CONFIRMATION DIALOG FOR SEND MANAGER
    if (showConfirmDialog && searchedUser != null) {
        val user = searchedUser!!
        val perms = selectedPermissions.filter { it.value }.keys.toList()
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onSendOrAssignManager(
                            user.id,
                            managerRoleType,
                            perms,
                            managerStatus,
                            "Owner Directive via Send Manager Console"
                        )
                        showConfirmDialog = false
                        successNotification = "Manager role '$managerRoleType' successfully assigned to ${user.username} (${user.id})!"
                        duplicateAlert = null
                        searchUserIdInput = ""
                        searchedUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    modifier = Modifier.testTag("confirm_send_manager_btn")
                ) {
                    Text("Confirm Assignment", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF7C4DFF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Manager Assignment", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Are you sure you want to assign Manager role to this user?", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0A0F1D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("• User: ${user.username} (${user.id})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text("• Role: $managerRoleType", color = Color(0xFFB388FF), fontSize = 11.sp)
                            Text("• Status: $managerStatus", color = Color(0xFF00E676), fontSize = 11.sp)
                            Text("• Granted Permissions: ${perms.joinToString(", ")}", color = Color(0xFF00E5FF), fontSize = 10.sp)
                        }
                    }
                    Text("This action will update the user's account role and record an entry into the immutable Audit Log.", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // REMOVE MANAGER DIALOG
    managerToRemove?.let { mgr ->
        AlertDialog(
            onDismissRequest = { managerToRemove = null },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveManager(mgr.userId, removeReasonInput)
                        managerToRemove = null
                        successNotification = "Manager role for ${mgr.username} has been removed."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Confirm Removal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { managerToRemove = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            title = { Text("Remove Manager: ${mgr.username}", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Revoke manager status and rights for User ID: ${mgr.userId}", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    OutlinedTextField(
                        value = removeReasonInput,
                        onValueChange = { removeReasonInput = it },
                        label = { Text("Removal Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}
