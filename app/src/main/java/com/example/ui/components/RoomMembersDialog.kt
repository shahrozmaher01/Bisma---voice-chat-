package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RoomSeat
import com.example.data.model.User
import com.example.data.model.VoiceRoom
import com.example.data.repository.BismaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RoomMembersDialog(
    room: VoiceRoom,
    repository: BismaRepository,
    isCallerHostOrAdmin: Boolean,
    onDismiss: () -> Unit,
    onSelectUser: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val seats by repository.getSeatsForRoom(room.id).collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var membersList by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(room.id, seats) {
        isLoading = true
        membersList = repository.getRoomMembers(room.id)
        isLoading = false
    }

    val adminIds = remember(room.adminUserIds) {
        room.adminUserIds.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    val filteredMembers = remember(membersList, searchQuery) {
        if (searchQuery.isBlank()) membersList
        else membersList.filter {
            it.username.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF140A28),
            border = BorderStroke(1.dp, Color(0xFF4A2885))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Room Members (${membersList.size}) 👥",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Active members, speakers & admins",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by name or ID...", color = TextSecondary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NeonPink, modifier = Modifier.size(32.dp))
                    }
                } else if (filteredMembers.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No members found", color = TextSecondary, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredMembers, key = { it.id }) { user ->
                            val userSeat = seats.find { it.userId == user.id }
                            val isOwner = room.ownerId == user.id
                            val isAdmin = adminIds.contains(user.id)

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onSelectUser(user.id) },
                                color = Color(0x222B1854),
                                border = BorderStroke(0.5.dp, Color(0x33FFFFFF))
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
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        AvatarWithFrame(
                                            avatarUrl = user.avatarUrl,
                                            size = 42.dp,
                                            frameId = user.equippedFrameId,
                                            vipLevel = user.vipLevel,
                                            isSpeaking = userSeat?.isSpeaking == true
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = user.username,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                LevelBadge(level = user.userLevel, type = LevelType.USER)
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                // Role badge
                                                when {
                                                    isOwner -> {
                                                        Text("👑 Host", color = GoldYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    isAdmin -> {
                                                        Text("🛡️ Admin", color = ElectricBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    userSeat != null -> {
                                                        Text("🎙️ Seat ${userSeat.seatIndex + 1}", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    else -> {
                                                        Text("🎧 Audience", color = TextSecondary, fontSize = 10.sp)
                                                    }
                                                }

                                                Text("• ID: ${user.id.take(8)}", color = TextSecondary, fontSize = 10.sp)
                                            }
                                        }
                                    }

                                    // Quick action buttons for Host
                                    if (room.ownerId == currentUser?.id && user.id != currentUser?.id) {
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.toggleRoomAdmin(room.id, user.id)
                                                    Toast.makeText(
                                                        context,
                                                        if (isAdmin) "Removed admin rights" else "Promoted to Room Admin! 🛡️",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isAdmin) Icons.Default.Shield else Icons.Default.AddModerator,
                                                contentDescription = "Admin Toggle",
                                                tint = if (isAdmin) GoldYellow else Color(0x66FFFFFF),
                                                modifier = Modifier.size(18.dp)
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
    }
}
