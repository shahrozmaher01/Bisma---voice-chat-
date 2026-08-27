package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun UserProfileDetailDialog(
    userId: String,
    repository: BismaRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }
    var isFollowing by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        val all = repository.getAllUsers()
        user = all.find { it.id == userId }
        repository.recordProfileVisit(userId)
    }

    if (user == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = SurfaceDark,
            confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
            text = { Text("Loading profile...", color = TextPrimary) }
        )
        return
    }

    val target = user!!

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with Transparent Frame Overlay
                AvatarWithFrame(
                    avatarUrl = target.avatarUrl,
                    size = 76.dp,
                    frameId = target.equippedFrameId,
                    vipLevel = target.vipLevel
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = target.username,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (target.vipLevel > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VipBadge(vipLevel = target.vipLevel)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ID: ${target.id} • ${target.country}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    LevelBadge(level = target.userLevel, type = LevelType.USER)
                    LevelBadge(level = target.richLevel, type = LevelType.RICH)
                    LevelBadge(level = target.charmLevel, type = LevelType.CHARM)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = target.bio,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val nowFollowing = repository.toggleFollow(target.id)
                                isFollowing = nowFollowing
                                Toast.makeText(context, if (nowFollowing) "Followed ${target.username}!" else "Unfollowed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) SurfaceCard else NeonPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isFollowing) "Following" else "Follow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.sendFriendRequest(target.id)
                                Toast.makeText(context, "Friend request sent to ${target.username}!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Friend", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
