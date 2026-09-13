package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.VoiceRoom
import com.example.data.repository.BismaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSettingsDialog(
    room: VoiceRoom,
    repository: BismaRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(room.title) }
    var description by remember { mutableStateOf(room.description) }
    var announcement by remember { mutableStateOf(room.announcement) }
    var rules by remember { mutableStateOf(room.rules) }
    var selectedSeatCount by remember { mutableIntStateOf(room.seatCount) }
    var isLocked by remember { mutableStateOf(room.isLocked) }
    var password by remember { mutableStateOf(room.password) }

    var currentCoverUrl by remember { mutableStateOf(room.coverUrl) }
    var currentWallpaperUrl by remember { mutableStateOf(room.wallpaperUrl) }
    var isSaving by remember { mutableStateOf(false) }

    // Real Photo Pickers
    val dpPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val uriStr = uri.toString()
            currentCoverUrl = uriStr
            scope.launch {
                val ok = repository.updateRoomDP(room.id, uriStr)
                if (ok) {
                    Toast.makeText(context, "Room DP updated and saved! 🖼️", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update Room DP.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val wallpaperPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val uriStr = uri.toString()
            currentWallpaperUrl = uriStr
            scope.launch {
                val ok = repository.updateRoomWallpaper(room.id, uriStr)
                if (ok) {
                    Toast.makeText(context, "Room wallpaper updated! 🎨", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update wallpaper.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .testTag("room_settings_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140A28)),
            border = BorderStroke(1.dp, Brush.linearGradient(listOf(NeonPink, ElectricBlue)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonPink.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = NeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Room Settings",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Permanent Room ID: #${room.id}",
                                color = ElectricBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_room_settings")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(
                    color = SurfaceCardBorder,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Permanent Identity Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1D1038),
                        border = BorderStroke(1.dp, Color(0x447C4DFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🛡️", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "One User → One Permanent Room",
                                    color = GoldYellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Owner: ${room.ownerName} • Created ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(room.createdAt))}\nYour Room ID is permanent and remains linked to your account.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // 2. Room DP / Cover Photo
                    Column {
                        Text(
                            text = "Room Profile Picture (DP)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            AsyncImage(
                                model = currentCoverUrl,
                                contentDescription = "Room DP",
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.5.dp, NeonPink, RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        dpPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("change_room_dp_button")
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Choose from Gallery", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val ok = repository.resetRoomDP(room.id)
                                            if (ok) {
                                                currentCoverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400"
                                                Toast.makeText(context, "Restored default Room DP", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    border = BorderStroke(1.dp, SurfaceCardBorder),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Reset Default", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // 3. Room Wallpaper / Background
                    Column {
                        Text(
                            text = "Room Background Wallpaper",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, ElectricBlue, RoundedCornerShape(10.dp))
                                    .background(SurfaceCard)
                            ) {
                                if (currentWallpaperUrl != null) {
                                    AsyncImage(
                                        model = currentWallpaperUrl,
                                        contentDescription = "Wallpaper",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Neon Night", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    wallpaperPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("change_room_wallpaper_button")
                            ) {
                                Icon(Icons.Default.Wallpaper, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Custom Wallpaper", fontSize = 12.sp)
                            }
                        }
                    }

                    // 4. Room Name / Title
                    Column {
                        Text("Room Name", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("room_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F071F),
                                unfocusedContainerColor = Color(0xFF0F071F)
                            ),
                            singleLine = true
                        )
                    }

                    // 5. Room Announcement
                    Column {
                        Text("Room Announcement (Pin)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = announcement,
                            onValueChange = { announcement = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("room_announcement_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F071F),
                                unfocusedContainerColor = Color(0xFF0F071F)
                            ),
                            maxLines = 3
                        )
                    }

                    // 6. Community Rules
                    Column {
                        Text("Room Rules", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = rules,
                            onValueChange = { rules = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("room_rules_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F071F),
                                unfocusedContainerColor = Color(0xFF0F071F)
                            ),
                            maxLines = 4
                        )
                    }

                    // 7. Seat Count Configuration
                    Column {
                        Text("Mic Seats Capacity", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(4, 8, 10, 12, 15, 20).forEach { seats ->
                                val selected = selectedSeatCount == seats
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selected) NeonPink else SurfaceCard,
                                    border = BorderStroke(1.dp, if (selected) NeonPink else SurfaceCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedSeatCount = seats }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$seats",
                                            color = if (selected) Color.White else TextSecondary,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 8. Room Privacy & Password Lock
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF180D30),
                        border = BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = if (isLocked) NeonPink else ElectricBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Password Protect Room",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Switch(
                                    checked = isLocked,
                                    onCheckedChange = { isLocked = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = NeonPink
                                    )
                                )
                            }

                            if (isLocked) {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = { Text("Enter 4-digit room pass code", color = TextSecondary, fontSize = 12.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("room_password_input"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NeonPink,
                                        unfocusedBorderColor = SurfaceCardBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SurfaceCardBorder)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            if (isLocked && password.isBlank()) {
                                Toast.makeText(context, "Please set a password for your locked room.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isSaving = true
                            scope.launch {
                                val result = repository.updateRoomSettings(
                                    roomId = room.id,
                                    title = title,
                                    description = description,
                                    announcement = announcement,
                                    rules = rules,
                                    seatCount = selectedSeatCount,
                                    isLocked = isLocked,
                                    password = password
                                )
                                isSaving = false
                                result.onSuccess {
                                    Toast.makeText(context, "Room settings saved successfully! ✨", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }.onFailure { err ->
                                    Toast.makeText(context, err.message ?: "Failed to save settings", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_room_settings_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving...")
                        } else {
                            Text("Save Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
