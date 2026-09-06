package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.User
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    repository: BismaRepository,
    onOpenUserProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentUser by repository.currentUser.collectAsState(initial = null)
    val friendsList by repository.getFriends().collectAsState(initial = emptyList())
    val pendingRequests by repository.getPendingFriendRequests().collectAsState(initial = emptyList())

    var activeChatPeer by remember { mutableStateOf<User?>(null) }
    var allCommunityUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var showAddFriendDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        allCommunityUsers = repository.getAllUsers()
    }

    val friendUserIds = friendsList.map { if (it.userId == currentUser?.id) it.friendId else it.userId }
    val myFriends = allCommunityUsers.filter { friendUserIds.contains(it.id) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Full Atmospheric Night Lake Background matching screenshot
        Image(
            painter = painterResource(id = R.drawable.home_night_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33060212),
                            Color(0x660A041E),
                            Color(0xAA08031A),
                            Color(0xEE070216)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Header: Messages & Friends 💬
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Messages ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "& Friends",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE056FD)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💬",
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Chat • Connect • Make New Friends",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB0A4C8)
                )
            }

            // Section Header: Connected Friends ✦  |  [+ Add Friend]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Icon + Title + Indicator line
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0x551E1038))
                            .border(1.5.dp, Color(0xFF9C27B0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Friends",
                            tint = Color(0xFFE056FD),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Connected Friends ✦",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                    )
                                )
                        )
                    }
                }

                // Right: + Add Friend Pill Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF2A85), Color(0xFF00D2FF))
                            )
                        )
                        .clickable { showAddFriendDialog = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Friend",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Pending Friend Requests (if any exist)
            if (pendingRequests.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Friend Requests (${pendingRequests.size})",
                        color = Color(0xFFFF2A85),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    pendingRequests.forEach { req ->
                        val senderUser = allCommunityUsers.find { it.id == req.userId }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0x66180E2E),
                            border = BorderStroke(1.dp, Color(0x44FF2A85)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onOpenUserProfile(req.userId) }
                                ) {
                                    AvatarWithFrame(
                                        avatarUrl = senderUser?.avatarUrl,
                                        size = 40.dp,
                                        vipLevel = senderUser?.vipLevel ?: 0
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = senderUser?.username ?: "User ${req.userId}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Wants to be friends",
                                            color = Color(0xFFB0A4C8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                repository.acceptFriendRequest(req.userId)
                                                Toast.makeText(context, "Friend request accepted! 🎉", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Accept", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                repository.rejectFriendRequest(req.userId)
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Reject", color = Color(0xFFB0A4C8), fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Main Content: Empty State Card matching the screenshot or Friends List
            if (myFriends.isEmpty()) {
                // Large Glassmorphism Card (Center) exactly matching reference screenshot
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0x66140A28),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                Color(0xFFFF2A85),
                                Color(0xFF7C4DFF),
                                Color(0xFF00E5FF)
                            )
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                    ) {
                        // 3D Glowing Character Illustration on circular cyber neon stage
                        Image(
                            painter = painterResource(id = R.drawable.chat_friends_empty),
                            contentDescription = "No Friends",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(230.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Title
                        Text(
                            text = "No friends connected yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtitle
                        Text(
                            text = "Add friends from voice rooms or explore\nprofiles to unlock 1-on-1 private messaging!",
                            color = Color(0xFFB5ACCF),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Button: Add Friends >
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                    )
                                )
                                .clickable { showAddFriendDialog = true }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Friends",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ">",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                // List of Connected Friends
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(myFriends, key = { it.id }) { friend ->
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0x66180E2E),
                            border = BorderStroke(1.dp, Color(0x334E357E)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeChatPeer = friend }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    AvatarWithFrame(
                                        avatarUrl = friend.avatarUrl,
                                        size = 46.dp,
                                        vipLevel = friend.vipLevel
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = friend.username,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.5.sp
                                            )
                                            if (friend.vipLevel > 0) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                VipBadge(vipLevel = friend.vipLevel)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Tap to chat • ${friend.country}",
                                            color = Color(0xFFB0A4C8),
                                            fontSize = 11.5.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x33FF2A85)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Chat",
                                        tint = Color(0xFFFF2A85),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Friends Dialog
        if (showAddFriendDialog) {
            AddFriendDialog(
                currentUserId = currentUser?.id ?: "",
                existingFriendIds = friendUserIds,
                allUsers = allCommunityUsers,
                onDismiss = { showAddFriendDialog = false },
                onAddFriend = { targetUserId ->
                    coroutineScope.launch {
                        repository.sendFriendRequest(targetUserId)
                        Toast.makeText(context, "Friend request sent! ✨", Toast.LENGTH_SHORT).show()
                    }
                },
                onOpenUserProfile = onOpenUserProfile
            )
        }

        // 1-on-1 Private Chat Dialog
        activeChatPeer?.let { peer ->
            DirectMessageDialog(
                peer = peer,
                onDismiss = { activeChatPeer = null }
            )
        }
    }
}

@Composable
fun AddFriendDialog(
    currentUserId: String,
    existingFriendIds: List<String>,
    allUsers: List<User>,
    onDismiss: () -> Unit,
    onAddFriend: (String) -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val candidates = remember(allUsers, existingFriendIds, searchQuery, currentUserId) {
        allUsers.filter { user ->
            user.id != currentUserId &&
            !existingFriendIds.contains(user.id) &&
            (searchQuery.isBlank() || user.username.contains(searchQuery, ignoreCase = true) || user.id.contains(searchQuery))
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF13082B),
        title = {
            Text(
                text = "Discover Friends 👥",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or ID...", color = Color(0xFF8E88A8)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF2A85),
                        unfocusedBorderColor = Color(0x44442C73),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (candidates.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No new users found to add.",
                            color = Color(0xFF8E88A8),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(candidates) { user ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0x441F143A),
                                border = BorderStroke(1.dp, Color(0x33442C73)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onOpenUserProfile(user.id)
                                                onDismiss()
                                            }
                                    ) {
                                        AvatarWithFrame(
                                            avatarUrl = user.avatarUrl,
                                            size = 38.dp,
                                            vipLevel = user.vipLevel
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = user.username,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${user.country} • Level ${user.userLevel}",
                                                color = Color(0xFF8E88A8),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onAddFriend(user.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF2A85)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(
                                            text = "+ Add",
                                            color = Color.White,
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
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF8E88A8))
            }
        }
    )
}

@Composable
fun DirectMessageDialog(
    peer: User,
    onDismiss: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Pair(peer.username, "Hey there! Glad we're connected on Bisma Voice Chat! ✨"),
            Pair("You", "Hello! Great to meet you here 😊")
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF13082B),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithFrame(avatarUrl = peer.avatarUrl, size = 38.dp, vipLevel = peer.vipLevel)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = peer.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "Private Friendship Chat • ${peer.country}", color = Color(0xFF10B981), fontSize = 11.sp)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { (sender, text) ->
                        val isMe = sender == "You"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isMe) Color(0xFFFF2A85) else Color(0x661F143A))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(text = text, color = Color.White, fontSize = 12.5.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Write a message...", color = Color(0xFF8E88A8)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(46.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF2A85),
                            unfocusedBorderColor = Color(0x44442C73),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                messages.add(Pair("You", messageText.trim()))
                                messageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                )
                            )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF8E88A8))
            }
        }
    )
}
