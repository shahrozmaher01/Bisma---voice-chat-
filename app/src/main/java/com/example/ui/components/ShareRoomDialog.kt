package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.VoiceRoom
import com.example.ui.theme.*

@Composable
fun ShareRoomDialog(
    room: VoiceRoom,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val roomDeepLink = "https://auralive.app/room/${room.id}"
    val shareInvitationText = """
        🎙️ Join my AURA Live Voice Room!
        ✨ Room: ${room.title}
        🆔 Room ID: ${room.id}
        👑 Host: ${room.ownerName}
        🔗 Link: $roomDeepLink
    """.trimIndent()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF160B29),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(NeonPink, ElectricBlue)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Room Invitation 💌",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Room Invitation Card Preview
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x3328154D),
                    border = BorderStroke(1.dp, Color(0x449D4EDD))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = room.coverUrl.ifEmpty { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400" },
                            contentDescription = "Room DP",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, NeonPink, CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = room.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Room ID: ${room.id}",
                            color = ElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Text(
                            text = "Hosted by ${room.ownerName} • ${room.onlineCount} Active",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Link Display & Copy Button
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x22FFFFFF),
                    border = BorderStroke(0.5.dp, Color(0x44FFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = roomDeepLink,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AURA Room Link", roomDeepLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Room link copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ElectricBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AURA Invitation", shareInvitationText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Full invitation copied! 📋", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1854)),
                        border = BorderStroke(1.dp, Color(0xFF9D4EDD)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Info", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Join my voice chat on AURA Live!")
                                putExtra(Intent.EXTRA_TEXT, shareInvitationText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Voice Room"))
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Link", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
