package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun RoomUserProfileDialog(
    userId: String,
    room: VoiceRoom,
    repository: BismaRepository,
    isCallerHostOrAdmin: Boolean,
    onDismiss: () -> Unit,
    onVisitProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val targetUser by repository.getUserFlow(userId).collectAsState(initial = null)
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val seats by repository.getSeatsForRoom(room.id).collectAsState(initial = emptyList())

    val userSeat = seats.find { it.userId == userId }
    val isSelf = currentUser?.id == userId
    val isRoomOwner = room.ownerId == userId
    val isRoomAdmin = room.adminUserIds.split(",").map { it.trim() }.contains(userId)

    var isFollowing by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        isFollowing = repository.isFollowingUser(userId)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF190F2E),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(NeonPink, ElectricBlue)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Room Status Tag
                    val (statusText, statusColor) = when {
                        isRoomOwner -> "👑 Room Host" to GoldYellow
                        isRoomAdmin -> "🛡️ Room Admin" to ElectricBlue
                        userSeat != null -> "🎙️ Seat ${userSeat.seatIndex + 1}" to EmeraldGreen
                        else -> "🎧 Listener" to TextSecondary
                    }
                    Surface(
                        color = statusColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Avatar with Frame & Level
                Box(contentAlignment = Alignment.Center) {
                    AvatarWithFrame(
                        avatarUrl = targetUser?.avatarUrl ?: "",
                        size = 72.dp,
                        frameId = targetUser?.equippedFrameId,
                        vipLevel = targetUser?.vipLevel ?: 0,
                        isSpeaking = userSeat?.isSpeaking == true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Username & Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = targetUser?.username ?: "User",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if ((targetUser?.vipLevel ?: 0) > 0) {
                        VipBadge(vipLevel = targetUser!!.vipLevel)
                    }
                    LevelBadge(level = targetUser?.userLevel ?: 1, type = LevelType.USER)
                }

                // User ID
                Text(
                    text = "ID: ${targetUser?.id ?: userId}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Bio / Signature
                val bio = targetUser?.bio ?: "Loving AURA Live community 🎵"
                Text(
                    text = bio,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                // Stats: Followers & Following
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x3328174E))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${targetUser?.followersCount ?: 0}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = "Followers", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0x33FFFFFF)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${targetUser?.followingCount ?: 0}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = "Following", color = TextSecondary, fontSize = 10.sp)
                    }
                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0x33FFFFFF)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${targetUser?.coins ?: 0}",
                            color = GoldYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(text = "Wealth", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Actions: Follow & Visit Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isSelf) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val nowFollowing = repository.toggleFollow(userId)
                                    isFollowing = nowFollowing
                                    Toast.makeText(
                                        context,
                                        if (nowFollowing) "Followed ${targetUser?.username}! ❤️" else "Unfollowed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) Color(0x33FFFFFF) else NeonPink
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = if (isFollowing) BorderStroke(1.dp, Color(0x55FFFFFF)) else null
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Favorite,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = if (isFollowing) TextSecondary else Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isFollowing) "Following" else "Follow",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFollowing) TextSecondary else Color.White
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onVisitProfile(userId)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1854)),
                        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Visit Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Privileged Host & Admin Tools Section
                if (isCallerHostOrAdmin && !isSelf && !isRoomOwner) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0x22FFFFFF))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Host & Admin Controls",
                        color = GoldYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Take Down (if sitting on a seat)
                        if (userSeat != null) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val ok = repository.takeDownUserFromSeat(room.id, userSeat.seatIndex)
                                        if (ok) {
                                            Toast.makeText(context, "${targetUser?.username ?: "User"} taken down to audience", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Take Down from Seat", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Mute/Unmute Mic
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val ok = repository.toggleMic(room.id, userSeat.seatIndex, !userSeat.isMuted)
                                        if (ok) {
                                            Toast.makeText(context, if (userSeat.isMuted) "Mic unmuted" else "Mic muted", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                border = BorderStroke(1.dp, if (userSeat.isMuted) EmeraldGreen else Color(0xFFFF5252)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (userSeat.isMuted) Icons.Default.Mic else Icons.Default.MicOff,
                                        contentDescription = null,
                                        tint = if (userSeat.isMuted) EmeraldGreen else Color(0xFFFF5252),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (userSeat.isMuted) "Unmute Microphone" else "Mute Microphone",
                                        color = if (userSeat.isMuted) EmeraldGreen else Color(0xFFFF5252),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Remove from Room (Kick)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.kickUserFromRoom(room.id, userId)
                                        Toast.makeText(context, "Removed from room", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B21A8)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Remove", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Block from Room
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.blockUserFromRoom(room.id, userId)
                                        Toast.makeText(context, "User blocked from room", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Block User", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
