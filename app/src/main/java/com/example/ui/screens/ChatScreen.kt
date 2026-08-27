package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
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

    LaunchedEffect(Unit) {
        allCommunityUsers = repository.getAllUsers()
    }

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Messages & Friends 💬",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pending Friend Requests Section
                if (pendingRequests.isNotEmpty()) {
                    item {
                        Text(
                            text = "Friend Requests (${pendingRequests.size})",
                            color = NeonPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(pendingRequests) { req ->
                        val senderUser = allCommunityUsers.find { it.id == req.userId }
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onOpenUserProfile(req.userId) }
                                ) {
                                    AvatarWithFrame(avatarUrl = senderUser?.avatarUrl, size = 44.dp, vipLevel = senderUser?.vipLevel ?: 0)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = senderUser?.username ?: "User ${req.userId}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(text = "ID: ${req.userId} • Wants to be friends", color = TextSecondary, fontSize = 10.sp)
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
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Accept", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                repository.rejectFriendRequest(req.userId)
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Reject", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Friends & Direct Chats Section
                item {
                    Text(
                        text = "Connected Friends",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                val friendUserIds = friendsList.map { if (it.userId == currentUser?.id) it.friendId else it.userId }
                val myFriends = allCommunityUsers.filter { friendUserIds.contains(it.id) }

                if (myFriends.isEmpty()) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No friends connected yet", color = TextSecondary, fontSize = 13.sp)
                                Text("Add friends from voice rooms or explore profiles to unlock 1-on-1 private messaging!", color = TextMuted, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                } else {
                    items(myFriends) { friend ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeChatPeer = friend }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AvatarWithFrame(avatarUrl = friend.avatarUrl, size = 46.dp, vipLevel = friend.vipLevel)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = friend.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            if (friend.vipLevel > 0) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                VipBadge(vipLevel = friend.vipLevel)
                                            }
                                        }
                                        Text(text = "Tap to chat • ${friend.country}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                Icon(Icons.Default.Chat, contentDescription = "Chat", tint = NeonPink, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // 1-on-1 Private Chat Dialog
        if (activeChatPeer != null) {
            DirectMessageDialog(
                peer = activeChatPeer!!,
                onDismiss = { activeChatPeer = null }
            )
        }
    }
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
        containerColor = SurfaceDark,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarWithFrame(avatarUrl = peer.avatarUrl, size = 36.dp, vipLevel = peer.vipLevel)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = peer.username, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Private Friendship Chat", color = EmeraldGreen, fontSize = 10.sp)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(260.dp)) {
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
                                    .background(if (isMe) NeonPink else SurfaceCard)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = text, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Write a message...") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
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
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryGradient)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}
