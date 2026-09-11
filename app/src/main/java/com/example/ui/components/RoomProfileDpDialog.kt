package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.data.repository.BismaRepository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class CuratedRoomDp(
    val name: String,
    val iconEmoji: String,
    val url: String
)

data class CuratedRoomWallpaper(
    val name: String,
    val previewEmoji: String,
    val url: String
)

val CuratedRoomDps = listOf(
    CuratedRoomDp(
        "Neon Beats DJ",
        "🎧",
        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400"
    ),
    CuratedRoomDp(
        "Acoustic Stage",
        "🎙️",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400"
    ),
    CuratedRoomDp(
        "Cyberpunk Neon",
        "🌌",
        "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400"
    ),
    CuratedRoomDp(
        "Anime Chill Lounge",
        "🌸",
        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400"
    ),
    CuratedRoomDp(
        "Royal Golden Lounge",
        "👑",
        "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=400"
    ),
    CuratedRoomDp(
        "Cosmic Nebula Lounge",
        "🔮",
        "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=400"
    )
)

val CuratedRoomWallpapers = listOf(
    CuratedRoomWallpaper(
        "Midnight Stars",
        "🌌",
        "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=800"
    ),
    CuratedRoomWallpaper(
        "Cyber Grid",
        "⚡",
        "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800"
    ),
    CuratedRoomWallpaper(
        "Velvet Lounge",
        "🍷",
        "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?w=800"
    ),
    CuratedRoomWallpaper(
        "Cosmic Aurora",
        "✨",
        "https://images.unsplash.com/photo-1531306728370-e2ebd9d7bb99?w=800"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomProfileDpDialog(
    room: VoiceRoom,
    isHostOrAdmin: Boolean,
    repository: BismaRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0: Room Profile & DP, 1: Wallpaper, 2: Announcement
    var pendingDpUrl by remember { mutableStateOf<String?>(null) }
    var pendingWallpaperUrl by remember { mutableStateOf<String?>(null) }
    var announcementText by remember { mutableStateOf(room.announcement) }
    var isSaving by remember { mutableStateOf(false) }

    // Gallery Picker for Room DP
    val dpPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingDpUrl = uri.toString()
            Toast.makeText(context, "Image selected from gallery! Tap 'Save Room DP' to apply.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Picker for Room Wallpaper
    val wallpaperPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingWallpaperUrl = uri.toString()
            Toast.makeText(context, "Wallpaper selected from gallery! Tap 'Apply Wallpaper' to apply.", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140C28)),
            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFF9D4EDD), Color(0xFFFF2A85))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar with Title and Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👑", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Room Profile & DP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Circular Room DP Preview with Glowing Neon Border
                Box(contentAlignment = Alignment.Center) {
                    val activeDp = pendingDpUrl ?: room.coverUrl.ifBlank {
                        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400"
                    }

                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .shadow(16.dp, CircleShape, spotColor = NeonPink)
                            .clip(CircleShape)
                            .border(
                                3.dp,
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF9D4EDD),
                                        Color(0xFFFF2A85),
                                        Color(0xFF00E5FF),
                                        Color(0xFF9D4EDD)
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        AsyncImage(
                            model = activeDp,
                            contentDescription = "Room Display Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Camera Icon Overlay for Host/Admin
                    if (isHostOrAdmin) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .offset(x = 2.dp, y = 2.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(NeonPink)
                                .clickable {
                                    dpPhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Change DP",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Room Title
                Text(
                    text = room.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Room ID with Copy Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33241547))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Room ID", room.id))
                            Toast.makeText(context, "Room ID ${room.id} copied! 📋", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Room ID: ${room.id}",
                        color = Color(0xFFB5A9D2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy ID",
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Host Details Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    AsyncImage(
                        model = room.ownerAvatar,
                        contentDescription = "Host Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Host: ${room.ownerName}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0x3300E5FF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.5.dp, ElectricBlue)
                    ) {
                        Text(
                            text = room.category,
                            color = ElectricBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Host / Admin Management Tabs
                if (isHostOrAdmin) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFF1B1135),
                        contentColor = NeonPink,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = NeonPink,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Room DP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) Color.White else TextSecondary) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Wallpaper", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) Color.White else TextSecondary) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 2) Color.White else TextSecondary) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // TAB 0: ROOM DP MANAGEMENT
                    if (selectedTab == 0) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Button: Select Image from Phone Gallery
                            Button(
                                onClick = {
                                    dpPhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1854)),
                                border = BorderStroke(1.dp, Color(0xFF9D4EDD)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pick from Phone Gallery 📱", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Curated Presets
                            Text(
                                text = "Or Pick Curated HD Room DP:",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(CuratedRoomDps) { item ->
                                    val isSelected = pendingDpUrl == item.url || (pendingDpUrl == null && room.coverUrl == item.url)
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color(0x44FF2A85) else Color(0x22FFFFFF))
                                            .border(
                                                BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonPink else Color(0x33FFFFFF)),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { pendingDpUrl = item.url }
                                            .padding(6.dp)
                                    ) {
                                        AsyncImage(
                                            model = item.url,
                                            contentDescription = item.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.iconEmoji,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            // If user picked or selected a new DP: Preview and Save button
                            if (pendingDpUrl != null && pendingDpUrl != room.coverUrl) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0x3300E5FF)),
                                    border = BorderStroke(1.dp, ElectricBlue),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Crop & DP Preview (1:1 Center Mask)",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, NeonPink, CircleShape)
                                        ) {
                                            AsyncImage(
                                                model = pendingDpUrl,
                                                contentDescription = "Preview",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { pendingDpUrl = null },
                                                modifier = Modifier.weight(1f).height(38.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, Color(0x66FFFFFF))
                                            ) {
                                                Text("Cancel", color = Color.White, fontSize = 12.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    isSaving = true
                                                    coroutineScope.launch {
                                                        val success = repository.updateRoomDP(room.id, pendingDpUrl!!)
                                                        isSaving = false
                                                        if (success) {
                                                            Toast.makeText(context, "Room DP updated successfully! ✨", Toast.LENGTH_SHORT).show()
                                                            pendingDpUrl = null
                                                            onDismiss()
                                                        } else {
                                                            Toast.makeText(context, "Only room owner or admin can update DP.", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.weight(1.5f).height(38.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                                shape = RoundedCornerShape(10.dp),
                                                enabled = !isSaving
                                            ) {
                                                Text(if (isSaving) "Saving..." else "Save Room DP ✨", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Restore Default Room DP Button
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val success = repository.resetRoomDP(room.id)
                                        if (success) {
                                            Toast.makeText(context, "Room DP restored to default image", Toast.LENGTH_SHORT).show()
                                            pendingDpUrl = null
                                            onDismiss()
                                        } else {
                                            Toast.makeText(context, "Only room owner or admin can reset DP", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                border = BorderStroke(1.dp, Color(0x44FF5252)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Restore Default Room DP", color = Color(0xFFFF5252), fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // TAB 1: WALLPAPER MANAGEMENT
                    if (selectedTab == 1) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    wallpaperPhotoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1854)),
                                border = BorderStroke(1.dp, Color(0xFF9D4EDD)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Wallpaper, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pick Wallpaper from Gallery 🖼️", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "Curated Room Wallpapers:",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(CuratedRoomWallpapers) { item ->
                                    val isSelected = pendingWallpaperUrl == item.url || (pendingWallpaperUrl == null && room.wallpaperUrl == item.url)
                                    Card(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .height(60.dp)
                                            .clickable { pendingWallpaperUrl = item.url },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) NeonPink else Color(0x33FFFFFF))
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            AsyncImage(
                                                model = item.url,
                                                contentDescription = item.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color(0x55000000)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(item.name, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                            }
                                        }
                                    }
                                }
                            }

                            if (pendingWallpaperUrl != null && pendingWallpaperUrl != room.wallpaperUrl) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.updateRoomWallpaper(room.id, pendingWallpaperUrl!!)
                                            Toast.makeText(context, "Room Wallpaper applied for all members! 🌌", Toast.LENGTH_SHORT).show()
                                            pendingWallpaperUrl = null
                                            onDismiss()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Apply Selected Wallpaper ✨", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // TAB 2: ROOM ANNOUNCEMENT
                    if (selectedTab == 2) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Edit Room Announcement Notice:",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            OutlinedTextField(
                                value = announcementText,
                                onValueChange = { announcementText = it },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPink,
                                    unfocusedBorderColor = Color(0x44FFFFFF),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.updateRoomAnnouncement(room.id, announcementText.trim())
                                        Toast.makeText(context, "Room Announcement updated! 📢", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Broadcast Announcement 📢", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Regular Listener View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22241547))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "📢 Announcement",
                            color = NeonPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = room.announcement,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1854)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close Profile", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
