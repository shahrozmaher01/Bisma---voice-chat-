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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.admin.AdminService
import com.example.data.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerUsersView(
    allUsers: List<User>,
    onToggleBanUser: (userId: String, isBanned: Boolean, reason: String) -> Unit,
    onAdjustCoins: (userId: String, deltaCoins: Long, reason: String) -> Unit,
    onUpdateVip: (userId: String, vipLevel: Int) -> Unit = { _, _ -> },
    onAssignFrame: (userId: String, frameId: String) -> Unit = { _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Dialog States
    var inspectingUser by remember { mutableStateOf<User?>(null) }
    var editingUser by remember { mutableStateOf<User?>(null) }
    var userForVip by remember { mutableStateOf<User?>(null) }
    var userForFrame by remember { mutableStateOf<User?>(null) }
    var userForBan by remember { mutableStateOf<User?>(null) }
    var userForMute by remember { mutableStateOf<User?>(null) }
    var userForTransactions by remember { mutableStateOf<User?>(null) }
    var userForActivity by remember { mutableStateOf<User?>(null) }

    // Muted user tracking (in-memory for demo / session)
    val mutedUsers = remember { mutableStateMapOf<String, Boolean>() }

    // Ban inputs
    var banReasonInput by remember { mutableStateOf("Violating Community Guidelines") }

    // Edit inputs
    var editUsername by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }
    var editLevel by remember { mutableStateOf("1") }
    var editCoinsAdjust by remember { mutableStateOf("0") }

    val filteredUsers = remember(allUsers, searchQuery, selectedFilter, mutedUsers) {
        allUsers.filter { user ->
            val matchesQuery = searchQuery.isBlank() ||
                    user.username.contains(searchQuery, ignoreCase = true) ||
                    user.id.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Active" -> !user.isBanned && mutedUsers[user.id] != true
                "Muted" -> mutedUsers[user.id] == true
                "Banned" -> user.isBanned
                "VIP" -> user.vipLevel > 0
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Page Title & Header Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "User Management",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredUsers.size} Users Registered",
                    color = Color(0xFF00E5FF),
                    fontSize = 12.sp
                )
            }
        }

        // Search Bar (User ID & Name)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by User ID or Name...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF00E5FF)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00E5FF),
                unfocusedBorderColor = Color(0xFF223250),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF131D31),
                unfocusedContainerColor = Color(0xFF131D31)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("user_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Active", "VIP", "Muted", "Banned").forEach { filter ->
                val isSel = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFF00E5FF) else Color(0xFF131D31))
                        .border(1.dp, if (isSel) Color(0xFF00E5FF) else Color(0xFF223250), RoundedCornerShape(8.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSel) Color.Black else Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // User List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredUsers, key = { it.id }) { user ->
                val isMuted = mutedUsers[user.id] == true
                val accountStatus = when {
                    user.isBanned -> "Banned"
                    isMuted -> "Muted"
                    else -> "Active"
                }
                val statusColor = when (accountStatus) {
                    "Banned" -> Color(0xFFFF5252)
                    "Muted" -> Color(0xFFFFAB00)
                    else -> Color(0xFF00E676)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF131D31),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (user.isBanned) Color(0xFFFF5252).copy(alpha = 0.5f) else Color(0xFF223250)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Profile Picture
                            Box(contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" },
                                    contentDescription = user.username,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, if (user.vipLevel > 0) Color(0xFFFFD700) else Color(0xFF00E5FF), CircleShape)
                                )
                                if (user.vipLevel > 0) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFFD700),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.BottomEnd)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("V", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Name & Info
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user.username,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF00E5FF).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Lv.${user.userLevel}",
                                            color = Color(0xFF00E5FF),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    if (user.vipLevel > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFD700).copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "VIP ${user.vipLevel}",
                                                color = Color(0xFFFFD700),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ID: ${user.id} • ${user.country}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Coins: %,d 🪙".format(user.coins),
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = statusColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = accountStatus,
                                            color = statusColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // User Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // View Profile
                            Button(
                                onClick = { inspectingUser = user },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Profile", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // Edit User
                            Button(
                                onClick = {
                                    editingUser = user
                                    editUsername = user.username
                                    editBio = user.bio
                                    editLevel = user.userLevel.toString()
                                    editCoinsAdjust = "0"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF).copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Edit", color = Color(0xFF2979FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // VIP & Frame Menu
                            var showMoreActions by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.weight(1f)) {
                                Button(
                                    onClick = { showMoreActions = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Perks", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }

                                DropdownMenu(
                                    expanded = showMoreActions,
                                    onDismissRequest = { showMoreActions = false },
                                    modifier = Modifier.background(Color(0xFF16233B))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Add/Remove VIP", color = Color.White, fontSize = 12.sp) },
                                        onClick = {
                                            showMoreActions = false
                                            userForVip = user
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Add/Remove Frame", color = Color.White, fontSize = 12.sp) },
                                        onClick = {
                                            showMoreActions = false
                                            userForFrame = user
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("View Transactions", color = Color.White, fontSize = 12.sp) },
                                        onClick = {
                                            showMoreActions = false
                                            userForTransactions = user
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("View Activity", color = Color.White, fontSize = 12.sp) },
                                        onClick = {
                                            showMoreActions = false
                                            userForActivity = user
                                        }
                                    )
                                }
                            }

                            // Mute Toggle
                            Button(
                                onClick = {
                                    mutedUsers[user.id] = !isMuted
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMuted) Color(0xFFFFAB00) else Color(0xFF1E2D4A)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                modifier = Modifier.weight(0.9f)
                            ) {
                                Text(
                                    text = if (isMuted) "Unmute" else "Mute",
                                    color = if (isMuted) Color.Black else Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Ban Toggle
                            Button(
                                onClick = {
                                    if (user.isBanned) {
                                        onToggleBanUser(user.id, false, "Owner Unban")
                                    } else {
                                        userForBan = user
                                        banReasonInput = "Terms of Service Violation"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isBanned) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                modifier = Modifier.weight(0.9f)
                            ) {
                                Text(
                                    text = if (user.isBanned) "Unban" else "Ban",
                                    color = if (user.isBanned) Color(0xFF00E676) else Color(0xFFFF5252),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG 1: VIEW COMPLETE USER PROFILE & ACTIVITY HISTORY
    inspectingUser?.let { user ->
        AlertDialog(
            onDismissRequest = { inspectingUser = null },
            confirmButton = {
                TextButton(onClick = { inspectingUser = null }) {
                    Text("Close", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("User Profile & History", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = user.avatarUrl.ifBlank { "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150" },
                            contentDescription = null,
                            modifier = Modifier.size(56.dp).clip(CircleShape).border(2.dp, Color(0xFFFFD700), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(user.username, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("ID: ${user.id}", color = Color(0xFF00E5FF), fontSize = 12.sp)
                            Text("VIP Tier: ${if (user.vipLevel > 0) "VIP ${user.vipLevel}" else "None"}", color = Color(0xFFFFD700), fontSize = 11.sp)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0A0F1D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D4A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Bio: ${user.bio.ifBlank { "No bio provided." }}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text("Balance: %,d Coins • %,d Diamonds".format(user.coins, user.diamonds), color = Color(0xFFFFD700), fontSize = 11.sp)
                            Text("Agency: ${user.agencyName ?: "Independent"}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("Account Status: ${if (user.isBanned) "BANNED" else "ACTIVE"}", color = if (user.isBanned) Color(0xFFFF5252) else Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("Recent Activity History", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0A0F1D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("• Joined Voice Room #104 (Music Lounge)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("• Sent 500 Coins gift 'Crown' to Host Elena", color = Color(0xFFFFD700), fontSize = 11.sp)
                            Text("• Claimed Daily Sign-in reward +50 XP", color = Color(0xFF00E5FF), fontSize = 11.sp)
                            Text("• Account Verified via SMS Authentication", color = Color(0xFF00E676), fontSize = 11.sp)
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // DIALOG 2: EDIT USER
    editingUser?.let { user ->
        AlertDialog(
            onDismissRequest = { editingUser = null },
            confirmButton = {
                Button(
                    onClick = {
                        val delta = editCoinsAdjust.toLongOrNull() ?: 0L
                        if (delta != 0L) {
                            onAdjustCoins(user.id, delta, "Owner Edit Profile: $editUsername")
                        }
                        editingUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                ) {
                    Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingUser = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            title = { Text("Edit User Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCoinsAdjust,
                        onValueChange = { editCoinsAdjust = it },
                        label = { Text("Adjust Coins (+/- Amount)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // DIALOG 3: BAN USER CONFIRMATION
    userForBan?.let { user ->
        AlertDialog(
            onDismissRequest = { userForBan = null },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleBanUser(user.id, true, banReasonInput)
                        userForBan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Confirm Ban", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userForBan = null }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            title = { Text("Ban User: ${user.username}", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provide mandatory reason for audit logging:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    OutlinedTextField(
                        value = banReasonInput,
                        onValueChange = { banReasonInput = it },
                        label = { Text("Ban Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // DIALOG 4: ADD/REMOVE VIP
    userForVip?.let { user ->
        AlertDialog(
            onDismissRequest = { userForVip = null },
            confirmButton = {
                TextButton(onClick = { userForVip = null }) {
                    Text("Done", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                }
            },
            title = { Text("Manage VIP: ${user.username}", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0 to "None", 1 to "VIP 1", 3 to "VIP 3", 5 to "VIP 5", 7 to "VIP 7 (Supreme)").forEach { (tier, name) ->
                        Surface(
                            onClick = {
                                onUpdateVip(user.id, tier)
                                userForVip = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (user.vipLevel == tier) Color(0xFFFFD700).copy(alpha = 0.25f) else Color(0xFF0A0F1D),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (user.vipLevel == tier) Color(0xFFFFD700) else Color(0xFF1E2D4A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                if (user.vipLevel == tier) {
                                    Text("CURRENT", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // DIALOG 5: VIEW RECENT TRANSACTIONS
    userForTransactions?.let { user ->
        AlertDialog(
            onDismissRequest = { userForTransactions = null },
            confirmButton = {
                TextButton(onClick = { userForTransactions = null }) {
                    Text("Close", color = Color(0xFF00E5FF))
                }
            },
            title = { Text("Transactions: ${user.username}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        Triple("+5,000 Coins", "Store Recharge • Credit Card", Color(0xFF00E676)),
                        Triple("-500 Coins", "Gift Sent to Room Host", Color(0xFFFF5252)),
                        Triple("+2,000 Coins", "Owner Admin Adjustment", Color(0xFFFFD700)),
                        Triple("-1,200 Coins", "Lucky Bag Participation", Color(0xFFFF5252))
                    ).forEach { (amount, desc, color) ->
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
                                    Text(desc, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Text("Completed • Verified", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                                }
                                Text(amount, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // DIALOG 6: VIEW RECENT ACTIVITY
    userForActivity?.let { user ->
        AlertDialog(
            onDismissRequest = { userForActivity = null },
            confirmButton = {
                TextButton(onClick = { userForActivity = null }) {
                    Text("Close", color = Color(0xFF00E5FF))
                }
            },
            title = { Text("Activity Log: ${user.username}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Joined Room #101 (Night Lounge) • 2h ago",
                        "Updated status message • 5h ago",
                        "Followed host Zara Official • Yesterday",
                        "Equipped Neon Circle Frame • 2 days ago"
                    ).forEach { act ->
                        Text("• $act", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}
