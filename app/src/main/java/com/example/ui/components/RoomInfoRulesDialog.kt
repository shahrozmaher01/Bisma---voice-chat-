package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.VoiceRoom
import com.example.data.repository.BismaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RoomInfoRulesDialog(
    room: VoiceRoom,
    repository: BismaRepository,
    isCallerHostOrAdmin: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val seats by repository.getSeatsForRoom(room.id).collectAsState(initial = emptyList())
    val speakerCount = seats.count { it.userId != null }

    var isEditingRules by remember { mutableStateOf(false) }
    var rulesText by remember {
        mutableStateOf(
            if (room.description.isNotBlank()) room.description
            else "1. Be respectful to all speakers and listeners.\n2. No hate speech, harassment, or offensive language.\n3. Do not spam or scream on the microphone.\n4. Follow Host and Admin instructions.\n5. Enjoy the community vibes and have fun! 🌟"
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.82f)
                .padding(horizontal = 6.dp),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xFF140A28),
            border = BorderStroke(1.dp, Color(0xFF4B2882))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Room Info & Community Rules ℹ️",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Room Overview Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0x3328154D),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = room.coverUrl.ifEmpty { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400" },
                                contentDescription = "Room DP",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, NeonPink, CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = room.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "ID: ${room.id}",
                                        color = ElectricBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Room ID", room.id)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Room ID copied! 📋", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy ID", tint = TextSecondary, modifier = Modifier.size(12.dp))
                                    }
                                }

                                Text(
                                    text = "Host: ${room.ownerName} • ${room.onlineCount} In Room • $speakerCount Speakers",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    // Announcement Section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0x221D0E38),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0x339D4EDD))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Room Announcement", color = NeonPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = room.announcement.ifBlank { "Welcome to our room! Enjoy music and conversations." },
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Community Guidelines & Rules Section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0x221D0E38),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0x334B2882))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Room Rules & Guidelines", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                if (isCallerHostOrAdmin) {
                                    TextButton(
                                        onClick = { isEditingRules = !isEditingRules },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(if (isEditingRules) "Cancel" else "Edit Rules ✏️", color = ElectricBlue, fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            if (isEditingRules) {
                                OutlinedTextField(
                                    value = rulesText,
                                    onValueChange = { rulesText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 4,
                                    maxLines = 8,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonPink,
                                        unfocusedBorderColor = Color(0x44FFFFFF),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val ok = repository.updateRoomRules(room.id, rulesText.trim())
                                            if (ok) {
                                                Toast.makeText(context, "Room rules updated and saved! 📜", Toast.LENGTH_SHORT).show()
                                                isEditingRules = false
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(38.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Save Room Rules 💾", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = rulesText,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
