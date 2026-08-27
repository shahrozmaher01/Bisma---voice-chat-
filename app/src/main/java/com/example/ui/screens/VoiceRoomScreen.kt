package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRoomScreen(
    repository: BismaRepository,
    onCloseRoom: () -> Unit,
    onOpenGames: () -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val room by repository.currentRoom.collectAsState(initial = null)
    val seats by repository.currentRoomSeats.collectAsState(initial = emptyList())
    val messages by repository.currentRoomMessages.collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var chatInputText by remember { mutableStateOf("") }
    var showEmojiSheet by remember { mutableStateOf(false) }
    var showGiftSheet by remember { mutableStateOf(false) }
    var showSoundboardSheet by remember { mutableStateOf(false) }
    var showHostToolsSheet by remember { mutableStateOf(false) }
    var showMediaSheet by remember { mutableStateOf(false) }
    var activeGiftBanner by remember { mutableStateOf<ChatMessage?>(null) }
    val floatingReactions = remember { mutableStateListOf<RoomFloatingReaction>() }

    val mySeat = seats.find { it.userId == currentUser?.id }
    val isHost = room?.ownerId == currentUser?.id

    // Listen for gift animation events
    LaunchedEffect(Unit) {
        repository.giftBannerEvent.collect { giftMsg ->
            activeGiftBanner = giftMsg
            delay(3500)
            if (activeGiftBanner == giftMsg) {
                activeGiftBanner = null
            }
        }
    }

    // Auto-scroll chat to bottom
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (room == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(DarkBackgroundGradient),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NeonPink)
        }
        return
    }

    val currentRoom = room!!

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Room Top Header Bar
            RoomTopBar(
                room = currentRoom,
                onCloseRoom = {
                    repository.leaveRoom()
                    onCloseRoom()
                },
                onHostTools = { showHostToolsSheet = true },
                onMediaClick = { showMediaSheet = true }
            )

            // Room Announcement Banner
            RoomAnnouncementBanner(announcement = currentRoom.announcement)

            // Live Audio Activity Status & Spectrogram Banner
            val speakingSeats = seats.filter { it.isSpeaking && it.userId != null }
            RoomAudioActivityBanner(
                speakingSeats = speakingSeats,
                mySeat = mySeat,
                onQuickMuteToggle = {
                    if (mySeat != null) {
                        coroutineScope.launch {
                            repository.toggleMic(currentRoom.id, mySeat.seatIndex, !mySeat.isMuted)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Room Seats Grid (Layout changes according to seatCount: 4, 6, 8, 10, 12, 15, 20)
            val columns = when {
                currentRoom.seatCount <= 6 -> 3
                currentRoom.seatCount <= 12 -> 4
                else -> 5
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.48f)
                    .padding(horizontal = 10.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(seats) { seat ->
                        RoomSeatItem(
                            seat = seat,
                            isHostSeat = seat.seatIndex == 0,
                            isMySeat = seat.userId == currentUser?.id,
                            onSeatClick = {
                                if (seat.userId == null) {
                                    coroutineScope.launch {
                                        repository.takeSeat(currentRoom.id, seat.seatIndex)
                                    }
                                } else if (seat.userId == currentUser?.id) {
                                    coroutineScope.launch {
                                        repository.leaveSeat(currentRoom.id, seat.seatIndex)
                                    }
                                } else {
                                    onOpenUserProfile(seat.userId)
                                }
                            }
                        )
                    }
                }
            }

            // Live Scrolling Chat Feed
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.38f)
                    .padding(horizontal = 12.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(messages) { msg ->
                        RoomChatMessageBubble(
                            message = msg,
                            onUserClick = { onOpenUserProfile(msg.senderId) }
                        )
                    }
                }
            }

            // Bottom In-Room Controller Bar
            RoomBottomControlBar(
                chatText = chatInputText,
                onChatTextChange = { chatInputText = it },
                onSendChat = {
                    if (chatInputText.isNotBlank()) {
                        coroutineScope.launch {
                            repository.sendRoomChatMessage(currentRoom.id, chatInputText.trim())
                            chatInputText = ""
                        }
                    }
                },
                isMicMuted = mySeat?.isMuted ?: true,
                hasSeat = mySeat != null,
                onToggleMic = {
                    if (mySeat != null) {
                        coroutineScope.launch {
                            repository.toggleMic(currentRoom.id, mySeat.seatIndex, !mySeat.isMuted)
                        }
                    } else {
                        Toast.makeText(context, "Please take an open seat to use mic", Toast.LENGTH_SHORT).show()
                    }
                },
                onOpenEmoji = { showEmojiSheet = true },
                onOpenGifts = { showGiftSheet = true },
                onOpenSoundboard = { showSoundboardSheet = true },
                onOpenGames = onOpenGames
            )
        }

        // Floating Animated Emoji Reactions Layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            floatingReactions.forEach { reaction ->
                key(reaction.id) {
                    FloatingReactionBubble(
                        reaction = reaction,
                        onFinished = {
                            floatingReactions.removeAll { it.id == reaction.id }
                        }
                    )
                }
            }
        }

        // Active Animated Gift Banner Overlay
        AnimatedVisibility(
            visible = activeGiftBanner != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp)
                .align(Alignment.TopCenter)
        ) {
            if (activeGiftBanner != null) {
                GiftAnimationOverlay(giftMessage = activeGiftBanner!!)
            }
        }

        // Emoji & Reactions Picker Bottom Sheet
        if (showEmojiSheet) {
            EmojiPickerBottomSheet(
                onEmojiSelected = { emoji ->
                    // 1. Trigger floating reaction animation
                    floatingReactions.add(
                        RoomFloatingReaction(
                            id = System.currentTimeMillis() + (0..99999).random(),
                            emoji = emoji,
                            xOffsetDp = (-70..40).random().toFloat()
                        )
                    )
                    // 2. Broadcast in real chat room
                    coroutineScope.launch {
                        repository.sendRoomChatMessage(currentRoom.id, emoji)
                    }
                },
                onDismiss = { showEmojiSheet = false }
            )
        }

        // Virtual Gift Bottom Sheet
        if (showGiftSheet) {
            VirtualGiftBottomSheet(
                repository = repository,
                roomId = currentRoom.id,
                seats = seats,
                onDismiss = { showGiftSheet = false }
            )
        }

        // Soundboard Bottom Sheet
        if (showSoundboardSheet) {
            SoundboardBottomSheet(
                repository = repository,
                onDismiss = { showSoundboardSheet = false }
            )
        }

        // Host Tools Sheet
        if (showHostToolsSheet) {
            HostToolsBottomSheet(
                room = currentRoom,
                isHost = isHost,
                onDismiss = { showHostToolsSheet = false }
            )
        }

        // Media Sheet
        if (showMediaSheet) {
            RoomMediaBottomSheet(
                onDismiss = { showMediaSheet = false }
            )
        }
    }
}

@Composable
fun RoomTopBar(
    room: VoiceRoom,
    onCloseRoom: () -> Unit,
    onHostTools: () -> Unit,
    onMediaClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Room Info (Title, ID, Online)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(onClick = onCloseRoom, modifier = Modifier.size(34.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = room.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID: ${room.id}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0x3300E5FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            SpeakingWaveAnimation(modifier = Modifier.height(8.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${room.onlineCount} Online",
                                fontSize = 9.sp,
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Icons (Media, Host Tools, Close)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onMediaClick,
                modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceCard)
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = ElectricBlue, modifier = Modifier.size(18.dp))
            }
            IconButton(
                onClick = onHostTools,
                modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceCard)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Tools", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun RoomAnnouncementBanner(announcement: String) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x33301B52))
            .clickable { expanded = !expanded }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📢", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = announcement,
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = if (expanded) 6 else 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RoomAudioActivityBanner(
    speakingSeats: List<RoomSeat>,
    mySeat: RoomSeat?,
    onQuickMuteToggle: () -> Unit
) {
    val activeSpeaker = speakingSeats.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    if (activeSpeaker != null)
                        listOf(Color(0x5500E5FF), Color(0x3300C853), Color(0x221A0B2E))
                    else
                        listOf(Color(0x331E1236), Color(0x22120A22))
                )
            )
            .border(
                1.dp,
                if (activeSpeaker != null) BrightCyan.copy(alpha = 0.6f) else SurfaceCardBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Active Speaker Avatar & Audio Visualizer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (activeSpeaker != null) {
                    AvatarWithFrame(
                        avatarUrl = activeSpeaker.avatarUrl,
                        size = 28.dp,
                        frameId = activeSpeaker.frameId,
                        vipLevel = activeSpeaker.vipLevel,
                        isSpeaking = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎙️ ${activeSpeaker.username}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            AudioActivityEqualizer(
                                isSpeaking = true,
                                barCount = 4,
                                maxBarHeight = 12.dp,
                                minBarHeight = 3.dp,
                                barColor = BrightCyan
                            )
                        }
                        Text(
                            text = "HD Voice 48kHz • Low Latency",
                            fontSize = 9.sp,
                            color = BrightCyan.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Audio Stage • Ready to talk",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Text(
                            text = "Tap any open seat to join the conversation",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Right: Live Decibel Meter & Quick Mute Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LiveDecibelMeter(
                    isSpeaking = activeSpeaker != null
                )

                if (mySeat != null) {
                    Surface(
                        onClick = onQuickMuteToggle,
                        color = if (mySeat.isMuted) DarkRed.copy(alpha = 0.25f) else EmeraldGreen.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (mySeat.isMuted) DarkRed else EmeraldGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (mySeat.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute Toggle",
                                tint = if (mySeat.isMuted) DarkRed else EmeraldGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (mySeat.isMuted) "MUTED" else "LIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (mySeat.isMuted) DarkRed else EmeraldGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoomSeatItem(
    seat: RoomSeat,
    isHostSeat: Boolean,
    isMySeat: Boolean,
    onSeatClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSeatClick() }
            .padding(vertical = 4.dp)
    ) {
        if (seat.userId != null) {
            // Occupied Seat
            Box(contentAlignment = Alignment.Center) {
                AvatarWithFrame(
                    avatarUrl = seat.avatarUrl,
                    size = 52.dp,
                    frameId = seat.frameId,
                    vipLevel = seat.vipLevel,
                    isSpeaking = seat.isSpeaking
                )

                // Mic Status Badge (Bottom Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (seat.isMuted) DarkRed else EmeraldGreen)
                        .border(1.dp, Color.Black, CircleShape)
                        .padding(2.5.dp)
                ) {
                    Icon(
                        imageVector = if (seat.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // User Name
            Text(
                text = seat.username ?: "User",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMySeat) NeonPink else Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Visual Audio Equalizer Indicator (Visible when speaking)
            if (seat.isSpeaking) {
                AudioActivityEqualizer(
                    isSpeaking = true,
                    barCount = 4,
                    maxBarHeight = 10.dp,
                    minBarHeight = 2.dp,
                    barColor = EmeraldGreen,
                    modifier = Modifier.padding(top = 1.dp)
                )
            } else if (isHostSeat) {
                Surface(
                    color = GoldAmber,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = "HOST 👑",
                        color = Color.Black,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.5.dp)
                    )
                }
            } else {
                Text(
                    text = "Seat ${seat.seatIndex + 1}",
                    fontSize = 8.sp,
                    color = TextMuted
                )
            }
        } else {
            // Empty Seat
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard.copy(alpha = 0.6f))
                    .border(1.dp, SurfaceCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isHostSeat) Icons.Default.Star else Icons.Default.Mic,
                        contentDescription = "Empty Seat",
                        tint = if (isHostSeat) GoldYellow else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "${seat.seatIndex + 1}",
                        fontSize = 8.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (isHostSeat) "Host" else "Seat ${seat.seatIndex + 1}",
                fontSize = 9.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun RoomChatMessageBubble(
    message: ChatMessage,
    onUserClick: () -> Unit
) {
    if (message.giftName != null) {
        // Gift announcement message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.horizontalGradient(listOf(Color(0x44FF2A85), Color(0x229D4EDD))))
                .border(1.dp, Color(0x66FF2A85), RoundedCornerShape(10.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = message.giftIcon ?: "🎁", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${message.senderName} ${message.content}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow
                )
            }
        }
    } else {
        // Standard text message
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 1.dp)
        ) {
            if (message.senderVip > 0) {
                VipBadge(vipLevel = message.senderVip, modifier = Modifier.padding(end = 4.dp))
            }
            Text(
                text = "${message.senderName}: ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue,
                modifier = Modifier.clickable { onUserClick() }
            )
            Text(
                text = message.content,
                fontSize = 11.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun RoomBottomControlBar(
    chatText: String,
    onChatTextChange: (String) -> Unit,
    onSendChat: () -> Unit,
    isMicMuted: Boolean,
    hasSeat: Boolean,
    onToggleMic: () -> Unit,
    onOpenEmoji: () -> Unit,
    onOpenGifts: () -> Unit,
    onOpenSoundboard: () -> Unit,
    onOpenGames: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Chat Input Field
            OutlinedTextField(
                value = chatText,
                onValueChange = onChatTextChange,
                placeholder = { Text("Say something...", fontSize = 11.sp, color = TextMuted) },
                singleLine = true,
                modifier = Modifier.weight(1f).height(44.dp),
                shape = RoundedCornerShape(22.dp),
                trailingIcon = {
                    if (chatText.isNotBlank()) {
                        IconButton(onClick = onSendChat) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = NeonPink, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPink,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            // Dedicated Emoji 😊 Button
            IconButton(
                onClick = onOpenEmoji,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, CircleShape)
            ) {
                Text(
                    text = "😊",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Prominent Mute / Unmute Toggle Button
            InteractiveMuteToggleButton(
                isMuted = isMicMuted,
                hasSeat = hasSeat,
                onToggle = onToggleMic
            )

            // Soundboard
            IconButton(
                onClick = onOpenSoundboard,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
            ) {
                Icon(Icons.Default.GraphicEq, contentDescription = "Effects", tint = NeonPurpleLight, modifier = Modifier.size(20.dp))
            }

            // Games
            IconButton(
                onClick = onOpenGames,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
            ) {
                Icon(Icons.Default.SportsEsports, contentDescription = "Games", tint = GoldYellow, modifier = Modifier.size(20.dp))
            }

            // Gift Button
            IconButton(
                onClick = onOpenGifts,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PrimaryGradient)
            ) {
                Icon(Icons.Default.CardGiftcard, contentDescription = "Gift", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
fun GiftAnimationOverlay(giftMessage: ChatMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF8E005B),
                        Color(0xFF37006B),
                        Color(0xFF003666)
                    )
                )
            )
            .border(2.dp, GoldYellow, RoundedCornerShape(20.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = giftMessage.giftIcon ?: "🎁",
                fontSize = 38.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "LUXURY GIFT SENT! ✨",
                    color = GoldYellow,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${giftMessage.senderName} ${giftMessage.content}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualGiftBottomSheet(
    repository: BismaRepository,
    roomId: String,
    seats: List<RoomSeat>,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentUser by repository.currentUser.collectAsState(initial = null)

    val gifts = listOf(
        VirtualGift("g_rose", "Red Rose", "🌹", 10, 1, "bloom"),
        VirtualGift("g_heart", "Love Heart", "💖", 50, 5, "hearts"),
        VirtualGift("g_car", "Super Sports Car", "🏎️", 500, 50, "drive"),
        VirtualGift("g_yacht", "Mega Yacht", "🛥️", 1500, 150, "cruise"),
        VirtualGift("g_rocket", "Galaxy Rocket", "🚀", 5000, 500, "launch"),
        VirtualGift("g_crown", "Imperial Crown", "👑", 10000, 1000, "royalty")
    )

    var selectedGift by remember { mutableStateOf(gifts.first()) }
    val eligibleReceivers = seats.filter { it.userId != null }
    var selectedTargetUserId by remember {
        mutableStateOf(eligibleReceivers.firstOrNull()?.userId ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header with User Coin Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Send Virtual Gift",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🪙 ${currentUser?.coins ?: 0} Coins", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Select Target Seat / User
            Text("Select Receiver on Seat:", color = TextSecondary, fontSize = 11.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                eligibleReceivers.forEach { s ->
                    val isSelected = selectedTargetUserId == s.userId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SurfaceCard else Color(0xFF140A22))
                            .border(1.5.dp, if (isSelected) NeonPink else SurfaceCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedTargetUserId = s.userId ?: "" }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AvatarWithFrame(avatarUrl = s.avatarUrl, size = 22.dp, vipLevel = s.vipLevel)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = s.username ?: "User",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gift Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().height(210.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gifts) { gift ->
                    val isSelected = selectedGift == gift
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x44FF2A85) else SurfaceCard)
                            .border(1.dp, if (isSelected) NeonPink else SurfaceCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedGift = gift }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = gift.iconEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gift.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "🪙 ${gift.costCoins}",
                                fontSize = 10.sp,
                                color = GoldYellow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeonButton(
                text = "Send ${selectedGift.iconEmoji} (${selectedGift.costCoins} Coins)",
                onClick = {
                    val targetName = eligibleReceivers.find { it.userId == selectedTargetUserId }?.username ?: "All Room"
                    coroutineScope.launch {
                        val success = repository.sendVirtualGift(roomId, selectedGift, selectedTargetUserId, targetName)
                        if (success) {
                            Toast.makeText(context, "Gift sent successfully! 💖", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Insufficient Coins! Please recharge in Wallet.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundboardBottomSheet(
    repository: BismaRepository,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val effects = listOf(
        Pair("👏 Applause", "applause"),
        Pair("🎉 Cheers", "cheers"),
        Pair("📢 Airhorn", "airhorn"),
        Pair("💋 Kiss Sound", "kiss"),
        Pair("😂 Laugh Track", "laugh"),
        Pair("🥁 Drumroll", "drumroll"),
        Pair("✨ Magic Sparkle", "magic"),
        Pair("🔔 Victory Bell", "bell")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Room Sound Effects & Audio Soundboard",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(effects) { (label, key) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCard)
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                coroutineScope.launch {
                                    repository.triggerSoundEffect(key)
                                }
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostToolsBottomSheet(
    room: VoiceRoom,
    isHost: Boolean,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Host & Room Management Tools",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            val tools = listOf(
                Pair(Icons.Default.Lock, "Lock All Empty Seats"),
                Pair(Icons.Default.CleaningServices, "Clean Room Chat Feed"),
                Pair(Icons.Default.MicOff, "Mute All Speakers"),
                Pair(Icons.Default.Campaign, "Update Room Announcement"),
                Pair(Icons.Default.Image, "Change Room Background Wallpaper"),
                Pair(Icons.Default.Share, "Share Room Invitation Link")
            )

            tools.forEach { (icon, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomMediaBottomSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Room Background Music & YouTube Player",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Stream authorized background ambient tracks & legal music playlists in your voice room.",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            val tracks = listOf(
                Pair("Chill Lofi Acoustic Beats", "3:45"),
                Pair("Desi Ghazal & Soft Harmonium", "5:20"),
                Pair("Romantic Urdu Poetry Strings", "4:15"),
                Pair("Bollywood Ambient Instrumental", "3:50")
            )

            tracks.forEach { (title, duration) ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(text = "Duration: $duration", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ElectricBlue)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class RoomFloatingReaction(
    val id: Long = System.currentTimeMillis() + (0..99999).random(),
    val emoji: String,
    val xOffsetDp: Float = (-70..40).random().toFloat()
)

@Composable
fun FloatingReactionBubble(
    reaction: RoomFloatingReaction,
    onFinished: () -> Unit
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(reaction.id) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
        onFinished()
    }

    val progress = animProgress.value
    val yOffset = (-340 * progress).dp
    val wobble = (kotlin.math.sin(progress * 4 * Math.PI) * 22).dp + reaction.xOffsetDp.dp
    val alpha = if (progress < 0.7f) 1f else (1f - (progress - 0.7f) / 0.3f).coerceIn(0f, 1f)
    val scale = if (progress < 0.15f) {
        (progress / 0.15f) * 1.35f
    } else {
        1.35f - (progress - 0.15f) * 0.4f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.BottomEnd)
            .padding(end = 50.dp)
            .offset(x = wobble, y = yOffset)
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            }
    ) {
        Text(
            text = reaction.emoji,
            fontSize = 32.sp
        )
    }
}

