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
fun OwnerAdminView(
    allUsers: List<User>,
    allRoles: List<UserRoleAssignment>,
    onAddOrUpdateAdmin: (targetUserId: String, adminRole: String, permissions: List<String>, notes: String) -> Unit,
    onRemoveAdmin: (targetUserId: String, reason: String) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") } // All, Super Admin, Admin Leader, Admin
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // Dialog States
    var showAddAdminDialog by remember { mutableStateOf(false) }
    var editingAdminRole by remember { mutableStateOf<UserRoleAssignment?>(null) }
    var editingAdminPermissions by remember { mutableStateOf<UserRoleAssignment?>(null) }
    var adminToRemove by remember { mutableStateOf<UserRoleAssignment?>(null) }
    var removeReasonInput by remember { mutableStateOf("Admin privilege revocation") }

    // Active status tracking in memory
    val adminStatusMap = remember { mutableStateMapOf<String, String>() }

    // Filtered Admin List
    val adminList = remember(allRoles, selectedCategoryFilter) {
        val admins = allRoles.filter {
            it.role.contains("Admin", ignoreCase = true) && !it.role.contains("Manager", ignoreCase = true)
        }
        when (selectedCategoryFilter) {
            "Super Admin" -> admins.filter { it.role.equals("Super Admin", ignoreCase = true) }
            "Admin Leader" -> admins.filter { it.role.equals("Admin Leader", ignoreCase = true) }
            "Admin" -> admins.filter { it.role.equals("Admin", ignoreCase = true) }
            else -> admins
        }
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
                    text = "Admin Management",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "High-Level Executive & Security Oversight",
                    color = Color(0xFF2979FF),
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { showAddAdminDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_add_admin_modal_btn")
            ) {
                Icon(Icons.Default.AddModerator, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Admin", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Admin Categories filter: All, Super Admin, Admin Leader, Admin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Super Admin", "Admin Leader", "Admin").forEach { category ->
                val isSel = selectedCategoryFilter == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF2979FF) else Color(0xFF131D31))
                        .border(1.dp, if (isSel) Color(0xFF2979FF) else Color(0xFF223250), RoundedCornerShape(8.dp))
                        .clickable { selectedCategoryFilter = category }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSel) Color.White else Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Admin List
        if (adminList.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Text(
                    text = "No administrators in category '$selectedCategoryFilter'. Click 'Add Admin' to promote an executive.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(adminList, key = { it.userId }) { admin ->
                    val status = adminStatusMap[admin.userId] ?: "Active"
                    val isActive = status == "Active"

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF131D31),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when (admin.role) {
                                "Super Admin" -> Color(0xFFFFD700).copy(alpha = 0.5f)
                                "Admin Leader" -> Color(0xFF00E5FF).copy(alpha = 0.5f)
                                else -> Color(0xFF2979FF).copy(alpha = 0.4f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Profile icon
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF2979FF).copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2979FF)),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF2979FF), modifier = Modifier.size(22.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(admin.username, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (admin.role) {
                                                "Super Admin" -> Color(0xFFFFD700)
                                                "Admin Leader" -> Color(0xFF00E5FF)
                                                else -> Color(0xFF2979FF)
                                            }
                                        ) {
                                            Text(
                                                text = admin.role,
                                                color = if (admin.role == "Super Admin") Color.Black else Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text("User ID: ${admin.userId}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isActive) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF5252).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = status,
                                                color = if (isActive) Color(0xFF00E676) else Color(0xFFFF5252),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Last Active: 12m ago", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                    }
                                }

                                // Status toggle: Activate / Deactivate
                                Button(
                                    onClick = {
                                        adminStatusMap[admin.userId] = if (isActive) "Deactivated" else "Active"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isActive) Color(0xFFFFAB00).copy(alpha = 0.2f) else Color(0xFF00E676).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isActive) "Deactivate" else "Activate",
                                        color = if (isActive) Color(0xFFFFAB00) else Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Permissions preview
                            Text(
                                text = "Permissions: ${admin.permissions.ifBlank { "Full Platform Administration" }}",
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Admin Actions Row: Edit Role, Edit Permissions, Remove Admin
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { editingAdminRole = admin },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit Role", color = Color.White, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { editingAdminPermissions = admin },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Permissions", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        adminToRemove = admin
                                        removeReasonInput = "Revoked by Owner Directive"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Text("Remove", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ADD ADMIN MODAL / FLOW
    if (showAddAdminDialog) {
        var addUserIdInput by remember { mutableStateOf("") }
        var searchedCandidate by remember { mutableStateOf<User?>(null) }
        var candidateError by remember { mutableStateOf<String?>(null) }
        var selectedRole by remember { mutableStateOf("Admin") } // Super Admin, Admin Leader, Admin

        val adminPermChecklist = remember {
            mutableStateMapOf(
                "Full Moderation" to true,
                "Room Monitoring & Termination" to true,
                "Agency & Host Inspection" to true,
                "Audit Log Access" to true,
                "Financial Review" to false,
                "System Alerts & Announcements" to true
            )
        }

        AlertDialog(
            onDismissRequest = { showAddAdminDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val candidate = searchedCandidate
                        if (candidate != null) {
                            val perms = adminPermChecklist.filter { it.value }.keys.toList()
                            onAddOrUpdateAdmin(candidate.id, selectedRole, perms, "Owner promoted via Admin Management")
                            showAddAdminDialog = false
                        }
                    },
                    enabled = searchedCandidate != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                ) {
                    Text("Confirm Promotion", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAdminDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF2979FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Administrator", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter User ID to verify candidate:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = addUserIdInput,
                            onValueChange = {
                                addUserIdInput = it
                                candidateError = null
                            },
                            placeholder = { Text("User ID...", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val trimmed = addUserIdInput.trim()
                                val found = allUsers.find { it.id == trimmed }
                                if (found != null) {
                                    searchedCandidate = found
                                    candidateError = null
                                } else {
                                    searchedCandidate = null
                                    candidateError = "User not found."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                        ) {
                            Text("Search", fontSize = 12.sp)
                        }
                    }

                    candidateError?.let { err ->
                        Text(err, color = Color(0xFFFF5252), fontSize = 11.sp)
                    }

                    searchedCandidate?.let { user ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0A0F1D),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" },
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(user.username, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("ID: ${user.id} • Lv.${user.userLevel}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Text("Select Admin Role Category:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Admin", "Admin Leader", "Super Admin").forEach { role ->
                            val isSel = selectedRole == role
                            Surface(
                                onClick = { selectedRole = role },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) Color(0xFF2979FF).copy(alpha = 0.25f) else Color(0xFF0A0F1D),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFF2979FF) else Color(0xFF1E2D4A)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = role,
                                    color = if (isSel) Color.White else Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Text("Select Admin Permissions:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        adminPermChecklist.keys.forEach { perm ->
                            val isChecked = adminPermChecklist[perm] ?: false
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { adminPermChecklist[perm] = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2979FF))
                                )
                                Text(perm, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // EDIT ROLE MODAL
    editingAdminRole?.let { admin ->
        var newRole by remember { mutableStateOf(admin.role) }
        AlertDialog(
            onDismissRequest = { editingAdminRole = null },
            confirmButton = {
                Button(
                    onClick = {
                        val currentPerms = admin.permissions.split(",").filter { it.isNotBlank() }
                        onAddOrUpdateAdmin(admin.userId, newRole, currentPerms, "Owner modified admin role")
                        editingAdminRole = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                ) {
                    Text("Update Role", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingAdminRole = null }) { Text("Cancel") }
            },
            title = { Text("Edit Role: ${admin.username}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Admin", "Admin Leader", "Super Admin").forEach { roleTitle ->
                        Surface(
                            onClick = { newRole = roleTitle },
                            shape = RoundedCornerShape(8.dp),
                            color = if (newRole == roleTitle) Color(0xFF2979FF).copy(alpha = 0.25f) else Color(0xFF0A0F1D),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (newRole == roleTitle) Color(0xFF2979FF) else Color(0xFF1E2D4A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(roleTitle, color = Color.White, fontSize = 13.sp)
                                if (newRole == roleTitle) Text("SELECTED", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // EDIT PERMISSIONS MODAL
    editingAdminPermissions?.let { admin ->
        var customPermsInput by remember { mutableStateOf(admin.permissions) }
        AlertDialog(
            onDismissRequest = { editingAdminPermissions = null },
            confirmButton = {
                Button(
                    onClick = {
                        val perms = customPermsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onAddOrUpdateAdmin(admin.userId, admin.role, perms, "Owner updated permissions")
                        editingAdminPermissions = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Save Permissions", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { editingAdminPermissions = null }) { Text("Cancel") } },
            title = { Text("Permissions: ${admin.username}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Comma-separated list of administrative privileges:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    OutlinedTextField(
                        value = customPermsInput,
                        onValueChange = { customPermsInput = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // REMOVE ADMIN MODAL
    adminToRemove?.let { admin ->
        AlertDialog(
            onDismissRequest = { adminToRemove = null },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveAdmin(admin.userId, removeReasonInput)
                        adminToRemove = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Confirm Revocation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { adminToRemove = null }) { Text("Cancel") } },
            title = { Text("Remove Admin: ${admin.username}", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Revoke admin role '${admin.role}' and remove administrative authorization.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    OutlinedTextField(
                        value = removeReasonInput,
                        onValueChange = { removeReasonInput = it },
                        label = { Text("Revocation Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}
