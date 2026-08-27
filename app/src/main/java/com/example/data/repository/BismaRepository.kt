package com.example.data.repository

import android.content.Context
import com.example.data.local.BismaDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class BismaRepository(private val context: Context) {
    private val db = BismaDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _currentUserId = MutableStateFlow("883921")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    val currentUser: Flow<User?> = _currentUserId.flatMapLatest { id ->
        db.userDao().getUserByIdFlow(id)
    }

    val activeRooms: Flow<List<VoiceRoom>> = db.roomDao().getAllActiveRoomsFlow()
    val topWealthUsers: Flow<List<User>> = db.userDao().getTopWealthUsers()
    val topCharmUsers: Flow<List<User>> = db.userDao().getTopCharmUsers()
    val allMoments: Flow<List<MomentPost>> = db.momentDao().getAllMomentsFlow()
    val storeItems: Flow<List<StoreItem>> = db.storeDao().getAllStoreItemsFlow()
    val backpackItems: Flow<List<StoreItem>> = db.storeDao().getBackpackItemsFlow()
    val agencies: Flow<List<Agency>> = db.agencyFamilyDao().getAllAgenciesFlow()
    val families: Flow<List<Family>> = db.agencyFamilyDao().getAllFamiliesFlow()

    // Active in-room state
    private val _activeRoomId = MutableStateFlow<String?>(null)
    val activeRoomId: StateFlow<String?> = _activeRoomId.asStateFlow()

    val currentRoom: Flow<VoiceRoom?> = _activeRoomId.flatMapLatest { roomId ->
        if (roomId == null) flowOf(null) else db.roomDao().getRoomByIdFlow(roomId)
    }

    val currentRoomSeats: Flow<List<RoomSeat>> = _activeRoomId.flatMapLatest { roomId ->
        if (roomId == null) flowOf(emptyList()) else db.seatDao().getSeatsForRoomFlow(roomId)
    }

    val currentRoomMessages: Flow<List<ChatMessage>> = _activeRoomId.flatMapLatest { roomId ->
        if (roomId == null) flowOf(emptyList()) else db.chatDao().getMessagesFlow(roomId)
    }

    // Sound effect trigger event
    private val _soundEffectEvent = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val soundEffectEvent: SharedFlow<String> = _soundEffectEvent.asSharedFlow()

    // Gift animation banner event
    private val _giftBannerEvent = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 5)
    val giftBannerEvent: SharedFlow<ChatMessage> = _giftBannerEvent.asSharedFlow()

    init {
        scope.launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingUser = db.userDao().getUserById("883921")
        if (existingUser == null) {
            val defaultUser = User(
                id = "883921",
                username = "Princess Bisma 👑",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80",
                bio = "Welcome to Bisma Voice Chat! Let's connect & sing 🎵",
                country = "🇵🇰 Pakistan",
                language = "English",
                userLevel = 5,
                richLevel = 3,
                charmLevel = 4,
                vipLevel = 2,
                coins = 5000,
                diamonds = 120,
                followersCount = 18,
                followingCount = 6,
                friendsCount = 4,
                equippedFrameId = "frame_vip_neon"
            )
            db.userDao().insertOrUpdate(defaultUser)

            // Seed other community users
            val sampleUsers = listOf(
                User(id = "104928", username = "Ali Khan 🎙️", avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300", country = "🇵🇰 Pakistan", userLevel = 8, richLevel = 6, charmLevel = 5, vipLevel = 3, coins = 12000),
                User(id = "209411", username = "Aarav Sharma 🎸", avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300", country = "🇮🇳 India", userLevel = 7, richLevel = 5, charmLevel = 7, vipLevel = 4, coins = 25000),
                User(id = "305182", username = "Zara Noor ✨", avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300", country = "🇵🇰 Pakistan", userLevel = 6, richLevel = 4, charmLevel = 8, vipLevel = 3, coins = 18000),
                User(id = "402819", username = "Tanvir Ahmed 🇧🇩", avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300", country = "🇧🇩 Bangladesh", userLevel = 4, richLevel = 2, charmLevel = 3, vipLevel = 1, coins = 4500),
                User(id = "509124", username = "Pooja Thapa 🇳🇵", avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300", country = "🇳🇵 Nepal", userLevel = 5, richLevel = 3, charmLevel = 4, vipLevel = 2, coins = 8000),
                User(id = "601832", username = "Hamza Sheikh 👑", avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300", country = "🇸🇦 Saudi", userLevel = 12, richLevel = 10, charmLevel = 9, vipLevel = 5, coins = 85000),
                User(id = "708912", username = "Sara Khan 💖", avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300", country = "🇦🇪 UAE", userLevel = 6, richLevel = 5, charmLevel = 7, vipLevel = 3, coins = 32000)
            )
            sampleUsers.forEach { db.userDao().insertOrUpdate(it) }

            // Seed default rooms
            val sampleRooms = listOf(
                VoiceRoom(
                    id = "772184",
                    title = "🎤 Bisma Royal Lounge | Urdu & Hindi Songs",
                    description = "24/7 Live Singing, Chill Vibes, and Friendly Conversations.",
                    coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
                    ownerId = "883921",
                    ownerName = "Princess Bisma 👑",
                    ownerAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                    ownerVip = 2,
                    country = "🇵🇰 Pakistan",
                    seatCount = 8,
                    onlineCount = 840,
                    category = "Singing & Chill",
                    isFeatured = true
                ),
                VoiceRoom(
                    id = "883109",
                    title = "✨ Desi Beats & Late Night Talks",
                    description = "Share your stories, poems, and join the microphone!",
                    coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400",
                    ownerId = "104928",
                    ownerName = "Ali Khan 🎙️",
                    ownerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                    ownerVip = 3,
                    country = "🇵🇰 Pakistan",
                    seatCount = 10,
                    onlineCount = 520,
                    category = "Talk & Podcast"
                ),
                VoiceRoom(
                    id = "994012",
                    title = "🎸 Bollywood Acoustic Live Jam",
                    description = "Guitarists & Vocalists open mic room.",
                    coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400",
                    ownerId = "209411",
                    ownerName = "Aarav Sharma 🎸",
                    ownerAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300",
                    ownerVip = 4,
                    country = "🇮🇳 India",
                    seatCount = 8,
                    onlineCount = 310,
                    category = "Music"
                ),
                VoiceRoom(
                    id = "652190",
                    title = "🎮 Ludo King Tournament & Voice Fun",
                    description = "Playing 4-player Ludo matches while chatting on mic!",
                    coverUrl = "https://images.unsplash.com/photo-1612287233207-61c028247072?w=400",
                    ownerId = "305182",
                    ownerName = "Zara Noor ✨",
                    ownerAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300",
                    ownerVip = 3,
                    country = "🇵🇰 Pakistan",
                    seatCount = 8,
                    onlineCount = 670,
                    category = "Gaming & Ludo"
                ),
                VoiceRoom(
                    id = "541829",
                    title = "💖 Singles Blind Date & CP Matching 🌹",
                    description = "Find your soulmate and play romantic voice games.",
                    coverUrl = "https://images.unsplash.com/photo-1518199266791-5375a83190b7?w=400",
                    ownerId = "708912",
                    ownerName = "Sara Khan 💖",
                    ownerAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300",
                    ownerVip = 3,
                    country = "🇦🇪 UAE",
                    seatCount = 8,
                    onlineCount = 1240,
                    category = "Dating & Singles",
                    isFeatured = true
                ),
                VoiceRoom(
                    id = "432901",
                    title = "🌙 Midnight Shayari & Heart-to-Heart",
                    description = "Calm ambience, Urdu poetry, and soothing discussions.",
                    coverUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400",
                    ownerId = "104928",
                    ownerName = "Ali Khan 🎙️",
                    ownerAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300",
                    ownerVip = 3,
                    country = "🇵🇰 Pakistan",
                    seatCount = 6,
                    onlineCount = 490,
                    category = "Late Night & Chill"
                ),
                VoiceRoom(
                    id = "321876",
                    title = "👑 Imperial VIP Club & High Rollers",
                    description = "Exclusive lounge for top gifting leaders and VIP members.",
                    coverUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400",
                    ownerId = "601832",
                    ownerName = "Hamza Sheikh 👑",
                    ownerAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300",
                    ownerVip = 5,
                    country = "🇸🇦 Saudi",
                    seatCount = 10,
                    onlineCount = 1580,
                    category = "Royal VIP",
                    isFeatured = true
                ),
                VoiceRoom(
                    id = "210985",
                    title = "🇧🇩 Dhaka Acoustic Melodies & Adda",
                    description = "Bengali folk songs, modern rock, and warm tea-time adda.",
                    coverUrl = "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=400",
                    ownerId = "402819",
                    ownerName = "Tanvir Ahmed 🇧🇩",
                    ownerAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300",
                    ownerVip = 1,
                    country = "🇧🇩 Bangladesh",
                    seatCount = 8,
                    onlineCount = 410,
                    category = "Singing & Chill"
                ),
                VoiceRoom(
                    id = "109874",
                    title = "🇳🇵 Himalayan Acoustic Chill & Folk",
                    description = "Nepali acoustic flute, guitar & mountain vibes.",
                    coverUrl = "https://images.unsplash.com/photo-1486572788966-cfd3dfdd4a48?w=400",
                    ownerId = "509124",
                    ownerName = "Pooja Thapa 🇳🇵",
                    ownerAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300",
                    ownerVip = 2,
                    country = "🇳🇵 Nepal",
                    seatCount = 6,
                    onlineCount = 290,
                    category = "Music"
                )
            )
            sampleRooms.forEach { room ->
                db.roomDao().insertOrUpdate(room)
                initSeatsForRoom(room.id, room.seatCount, room.ownerId, room.ownerName, room.ownerAvatar, room.ownerVip)
            }

            // Seed store items
            val items = listOf(
                StoreItem("frame_vip_neon", "Neon Pink Crown Frame", "Frames", 1200, "👑", 0xFFFF2A85, isPermanent = true, isOwned = true, isEquipped = true),
                StoreItem("frame_galaxy_gold", "Galaxy Gold Ring", "Frames", 2500, "🪐", 0xFFFFD700, isPermanent = true),
                StoreItem("frame_cyber_blue", "Cyberpunk Hologram", "Frames", 1800, "⚡", 0xFF00E5FF, isPermanent = true),
                StoreItem("frame_heart_romance", "Romantic Rose CP Frame", "Frames", 3000, "💖", 0xFFFF4081, isPermanent = true),
                StoreItem("head_angel_wings", "Angel Wings Halo", "Headwear", 1500, "🪽", 0xFFFFFFFF),
                StoreItem("head_devil_horns", "Neon Devil Horns", "Headwear", 1400, "😈", 0xFFFF1744),
                StoreItem("entry_supercar", "Lamborghini Entry Effect", "Entry Effects", 5000, "🏎️", 0xFFFFD700),
                StoreItem("entry_dragon", "Phoenix Flame Entry", "Entry Effects", 8000, "🔥", 0xFFFF8800),
                StoreItem("bubble_neon_glow", "Pink Neon Chat Bubble", "Chat Bubbles", 800, "💬", 0xFFFF2A85),
                StoreItem("sound_laser_wave", "Laser Sound Waves", "Sound Waves", 1000, "🌊", 0xFF00E5FF)
            )
            db.storeDao().insertAll(items)

            // Seed Agencies & Families
            db.agencyFamilyDao().insertAgency(Agency("ag_1", "Diamond Elite Agency", "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=200", "883921", "Princess Bisma", 24, 3, "Top verified voice talent hub.", 180000, 1))
            db.agencyFamilyDao().insertAgency(Agency("ag_2", "Royal Stars Entertainment", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=200", "104928", "Ali Khan", 19, 2, "Building international voice creators.", 95000, 2))

            db.agencyFamilyDao().insertFamily(Family("fam_1", "🌟 Bisma Royal Family", "https://images.unsplash.com/photo-1557683316-973673baf926?w=200", "883921", "Princess Bisma", 38, 4, "United by voice, bonded by love!", 78000, 1))
            db.agencyFamilyDao().insertFamily(Family("fam_2", "🔥 Desi Vibe Squad", "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=200", "209411", "Aarav Sharma", 27, 3, "Music & fun always.", 42000, 2))

            // Seed Moments
            db.momentDao().insertMoment(
                MomentPost(
                    id = "m_1",
                    authorId = "883921",
                    authorName = "Princess Bisma 👑",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300",
                    authorVip = 2,
                    content = "Thank you so much to everyone who joined our room tonight! Over 800+ friends hanging out 🎉✨ Don't forget to follow our family.",
                    imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600",
                    likesCount = 42,
                    commentsCount = 8
                )
            )

            // Seed notifications
            db.notificationDao().insertNotification(
                NotificationItem(
                    id = "notif_1",
                    userId = "883921",
                    type = "system",
                    title = "🎉 Welcome to Bisma Voice Chat",
                    message = "Your VIP tier and wallet balance have been credited. Enjoy real-time voice rooms and social features!",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private suspend fun initSeatsForRoom(
        roomId: String,
        count: Int,
        ownerId: String,
        ownerName: String,
        ownerAvatar: String,
        ownerVip: Int
    ) {
        db.seatDao().clearSeatsForRoom(roomId)
        val seats = mutableListOf<RoomSeat>()
        // Seat 0 is host seat
        seats.add(
            RoomSeat(
                roomId = roomId,
                seatIndex = 0,
                userId = ownerId,
                username = ownerName,
                avatarUrl = ownerAvatar,
                vipLevel = ownerVip,
                userLevel = 5,
                frameId = "frame_vip_neon",
                isMuted = false,
                isSpeaking = true
            )
        )
        for (i in 1 until count) {
            seats.add(
                RoomSeat(
                    roomId = roomId,
                    seatIndex = i,
                    userId = null,
                    username = null,
                    avatarUrl = null
                )
            )
        }
        db.seatDao().insertSeats(seats)
    }

    // Room operations
    suspend fun createRoom(
        title: String,
        description: String,
        coverUrl: String,
        seatCount: Int,
        country: String,
        category: String,
        isLocked: Boolean,
        password: String
    ): String {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return ""
        val newRoomId = (Random.nextInt(100000, 999999)).toString()
        val room = VoiceRoom(
            id = newRoomId,
            title = title.ifBlank { "${user.username}'s Party Room" },
            description = description.ifBlank { "Welcome to our live voice chat!" },
            coverUrl = coverUrl.ifBlank { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400" },
            ownerId = user.id,
            ownerName = user.username,
            ownerAvatar = user.avatarUrl,
            ownerVip = user.vipLevel,
            country = country,
            seatCount = seatCount,
            isLocked = isLocked,
            password = password,
            category = category,
            onlineCount = 1
        )
        db.roomDao().insertOrUpdate(room)
        initSeatsForRoom(newRoomId, seatCount, user.id, user.username, user.avatarUrl, user.vipLevel)
        _activeRoomId.value = newRoomId
        return newRoomId
    }

    fun getSeatsForRoom(roomId: String): Flow<List<RoomSeat>> {
        return db.seatDao().getSeatsForRoomFlow(roomId)
    }

    suspend fun refreshLiveListenerCounts() {
        val rooms = db.roomDao().getAllActiveRoomsFlow().firstOrNull() ?: return
        rooms.forEach { room ->
            val delta = Random.nextInt(-8, 15)
            val newCount = (room.onlineCount + delta).coerceAtLeast(12)
            db.roomDao().updateOnlineCount(room.id, newCount)
        }
    }

    fun enterRoom(roomId: String) {
        _activeRoomId.value = roomId
        scope.launch {
            val user = db.userDao().getUserById(_currentUserId.value)
            if (user != null) {
                db.chatDao().insertMessage(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        targetId = roomId,
                        isRoomChat = true,
                        senderId = user.id,
                        senderName = user.username,
                        senderAvatar = user.avatarUrl,
                        senderVip = user.vipLevel,
                        content = "✨ entered the room!"
                    )
                )
            }
        }
    }

    fun leaveRoom() {
        val roomId = _activeRoomId.value
        if (roomId != null) {
            scope.launch {
                // Clear user from seats if sitting
                val userId = _currentUserId.value
                val seats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: emptyList()
                seats.filter { it.userId == userId }.forEach { seat ->
                    db.seatDao().updateSeat(seat.copy(userId = null, username = null, avatarUrl = null, isSpeaking = false))
                }
            }
        }
        _activeRoomId.value = null
    }

    suspend fun takeSeat(roomId: String, seatIndex: Int) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val updatedSeat = RoomSeat(
            roomId = roomId,
            seatIndex = seatIndex,
            userId = user.id,
            username = user.username,
            avatarUrl = user.avatarUrl,
            vipLevel = user.vipLevel,
            userLevel = user.userLevel,
            frameId = user.equippedFrameId,
            isMuted = false,
            isSpeaking = false
        )
        db.seatDao().updateSeat(updatedSeat)
    }

    suspend fun leaveSeat(roomId: String, seatIndex: Int) {
        val seat = RoomSeat(
            roomId = roomId,
            seatIndex = seatIndex,
            userId = null,
            username = null,
            avatarUrl = null
        )
        db.seatDao().updateSeat(seat)
    }

    suspend fun toggleMic(roomId: String, seatIndex: Int, isMuted: Boolean) {
        val seats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: return
        val seat = seats.find { it.seatIndex == seatIndex } ?: return
        db.seatDao().updateSeat(seat.copy(isMuted = isMuted, isSpeaking = !isMuted))
    }

    suspend fun sendRoomChatMessage(roomId: String, text: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            targetId = roomId,
            isRoomChat = true,
            senderId = user.id,
            senderName = user.username,
            senderAvatar = user.avatarUrl,
            senderVip = user.vipLevel,
            content = text
        )
        db.chatDao().insertMessage(msg)
    }

    suspend fun sendVirtualGift(roomId: String, gift: VirtualGift, targetUserId: String, targetUserName: String): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        if (user.coins < gift.costCoins) return false

        // Deduct coins from sender, increase rich level points
        db.userDao().updateBalance(user.id, -gift.costCoins, 0)
        val newRichLevel = ((user.richLevel * 1000 + gift.costCoins) / 1000).toInt().coerceAtMost(50)
        db.userDao().updateLevels(user.id, newRichLevel, user.charmLevel)

        // Give diamonds and charm points to receiver
        val diamondReward = (gift.costCoins / 10).coerceAtLeast(1)
        val targetUser = db.userDao().getUserById(targetUserId)
        if (targetUser != null) {
            db.userDao().updateBalance(targetUserId, 0, diamondReward)
            val newCharmLevel = ((targetUser.charmLevel * 1000 + gift.charmPoints * 100) / 1000).toInt().coerceAtMost(50)
            db.userDao().updateLevels(targetUserId, targetUser.richLevel, newCharmLevel)
        }

        // Record transactions
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Gift Sent",
                amountCoins = -gift.costCoins,
                description = "Sent ${gift.iconEmoji} ${gift.name} to $targetUserName"
            )
        )

        val giftMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            targetId = roomId,
            isRoomChat = true,
            senderId = user.id,
            senderName = user.username,
            senderAvatar = user.avatarUrl,
            senderVip = user.vipLevel,
            content = "sent ${gift.iconEmoji} ${gift.name} to $targetUserName! 💖",
            giftName = gift.name,
            giftIcon = gift.iconEmoji
        )
        db.chatDao().insertMessage(giftMsg)
        _giftBannerEvent.tryEmit(giftMsg)
        return true
    }

    suspend fun triggerSoundEffect(effectName: String) {
        _soundEffectEvent.emit(effectName)
    }

    // Wallet operations
    suspend fun rechargeCoins(coinAmount: Long, priceUsd: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        db.userDao().updateBalance(user.id, coinAmount, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Recharge",
                amountCoins = coinAmount,
                description = "Top-up package $priceUsd ($coinAmount Coins)"
            )
        )
    }

    suspend fun exchangeDiamondsToCoins(diamondAmount: Long): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        if (user.diamonds < diamondAmount) return false
        val gainedCoins = diamondAmount * 10
        db.userDao().updateBalance(user.id, gainedCoins, -diamondAmount)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Diamond Exchange",
                amountCoins = gainedCoins,
                amountDiamonds = -diamondAmount,
                description = "Exchanged $diamondAmount Diamonds for $gainedCoins Coins"
            )
        )
        return true
    }

    fun getTransactions(): Flow<List<WalletTransaction>> {
        return _currentUserId.flatMapLatest { id ->
            db.walletTransactionDao().getTransactionsFlow(id)
        }
    }

    // Moments
    suspend fun createMoment(content: String, imageUrl: String?) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val moment = MomentPost(
            id = UUID.randomUUID().toString(),
            authorId = user.id,
            authorName = user.username,
            authorAvatar = user.avatarUrl,
            authorVip = user.vipLevel,
            content = content,
            imageUrl = imageUrl
        )
        db.momentDao().insertMoment(moment)
    }

    suspend fun toggleLikeMoment(momentId: String, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        db.momentDao().toggleLike(momentId, delta, !currentLiked)
    }

    suspend fun deleteMoment(momentId: String) {
        db.momentDao().deleteMoment(momentId)
    }

    // Store & Backpack
    suspend fun buyStoreItem(itemId: String): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        val items = db.storeDao().getAllStoreItemsFlow().firstOrNull() ?: return false
        val item = items.find { it.id == itemId } ?: return false
        if (user.coins < item.price) return false

        db.userDao().updateBalance(user.id, -item.price, 0)
        db.storeDao().markOwned(itemId)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Store Purchase",
                amountCoins = -item.price,
                description = "Purchased ${item.name}"
            )
        )
        return true
    }

    suspend fun equipItem(itemId: String, category: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        db.storeDao().unequipCategory(category)
        db.storeDao().setEquipped(itemId, true)
        if (category == "Frames") {
            db.userDao().updateEquippedFrame(user.id, itemId)
        }
    }

    suspend fun unequipItem(itemId: String, category: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        db.storeDao().setEquipped(itemId, false)
        if (category == "Frames") {
            db.userDao().updateEquippedFrame(user.id, null)
        }
    }

    // Friends & Social
    fun getFriends(): Flow<List<Friendship>> = _currentUserId.flatMapLatest { id ->
        db.socialDao().getFriendsFlow(id)
    }

    fun getPendingFriendRequests(): Flow<List<Friendship>> = _currentUserId.flatMapLatest { id ->
        db.socialDao().getPendingFriendRequestsFlow(id)
    }

    suspend fun sendFriendRequest(targetUserId: String) {
        val senderId = _currentUserId.value
        val user = db.userDao().getUserById(senderId) ?: return
        db.socialDao().insertFriendship(Friendship(userId = senderId, friendId = targetUserId, status = "pending"))
        db.notificationDao().insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = targetUserId,
                type = "friend_request",
                title = "New Friend Request",
                message = "${user.username} sent you a friend request.",
                senderId = user.id,
                senderName = user.username,
                senderAvatar = user.avatarUrl
            )
        )
    }

    suspend fun acceptFriendRequest(senderId: String) {
        val currentId = _currentUserId.value
        db.socialDao().insertFriendship(Friendship(userId = senderId, friendId = currentId, status = "accepted"))
    }

    suspend fun rejectFriendRequest(senderId: String) {
        val currentId = _currentUserId.value
        db.socialDao().deleteFriendship(senderId, currentId)
    }

    suspend fun toggleFollow(targetUserId: String): Boolean {
        val currentId = _currentUserId.value
        val isFollowing = db.socialDao().isFollowing(currentId, targetUserId)
        if (isFollowing) {
            db.socialDao().deleteFollow(currentId, targetUserId)
            return false
        } else {
            db.socialDao().insertFollow(Follow(followerId = currentId, followingId = targetUserId))
            val user = db.userDao().getUserById(currentId)
            if (user != null) {
                db.notificationDao().insertNotification(
                    NotificationItem(
                        id = UUID.randomUUID().toString(),
                        userId = targetUserId,
                        type = "follow",
                        title = "New Follower",
                        message = "${user.username} started following you!",
                        senderId = user.id,
                        senderName = user.username,
                        senderAvatar = user.avatarUrl
                    )
                )
            }
            return true
        }
    }

    suspend fun recordProfileVisit(visitedUserId: String) {
        val visitor = db.userDao().getUserById(_currentUserId.value) ?: return
        if (visitor.id == visitedUserId) return
        db.visitorDao().insertVisitor(
            VisitorRecord(
                id = UUID.randomUUID().toString(),
                targetUserId = visitedUserId,
                visitorId = visitor.id,
                visitorName = visitor.username,
                visitorAvatar = visitor.avatarUrl,
                visitorVip = visitor.vipLevel
            )
        )
    }

    fun getVisitors(): Flow<List<VisitorRecord>> = _currentUserId.flatMapLatest { id ->
        db.visitorDao().getVisitorsFlow(id)
    }

    fun getNotifications(): Flow<List<NotificationItem>> = _currentUserId.flatMapLatest { id ->
        db.notificationDao().getNotificationsFlow(id)
    }

    // Minigames
    suspend fun playLucky77(betCoins: Long): Triple<List<Int>, Long, String> {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return Triple(listOf(7, 7, 7), 0, "Error")
        if (user.coins < betCoins) return Triple(listOf(1, 2, 3), 0, "Insufficient coins")

        val r1 = Random.nextInt(1, 8)
        val r2 = Random.nextInt(1, 8)
        val r3 = Random.nextInt(1, 8)

        var winMultiplier = 0
        if (r1 == 7 && r2 == 7 && r3 == 7) winMultiplier = 77
        else if (r1 == r2 && r2 == r3) winMultiplier = 15
        else if (r1 == r2 || r2 == r3 || r1 == r3) winMultiplier = 2

        val netGains = (betCoins * winMultiplier) - betCoins
        db.userDao().updateBalance(user.id, netGains, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = if (netGains >= 0) "Game Win" else "Game Bet",
                amountCoins = netGains,
                description = "Lucky 77 ($r1-$r2-$r3) bet $betCoins -> won ${betCoins * winMultiplier}"
            )
        )
        val message = if (winMultiplier > 0) "JACKPOT! Won ${betCoins * winMultiplier} coins! 🎉" else "Better luck next spin!"
        return Triple(listOf(r1, r2, r3), betCoins * winMultiplier, message)
    }

    suspend fun updateProfile(username: String, avatarUrl: String, bio: String, country: String, language: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val updated = user.copy(
            username = username.ifBlank { user.username },
            avatarUrl = avatarUrl.ifBlank { user.avatarUrl },
            bio = bio,
            country = country,
            language = language
        )
        db.userDao().insertOrUpdate(updated)
    }

    suspend fun setVipLevel(vip: Int) {
        db.userDao().updateVip(_currentUserId.value, vip)
    }

    suspend fun searchUsers(query: String): List<User> = db.userDao().searchUsers(query)
    suspend fun searchRooms(query: String): List<VoiceRoom> = db.roomDao().searchRooms(query)
    suspend fun getAllUsers(): List<User> = db.userDao().getAllUsers()

    fun switchUser(userId: String) {
        _currentUserId.value = userId
    }
}
