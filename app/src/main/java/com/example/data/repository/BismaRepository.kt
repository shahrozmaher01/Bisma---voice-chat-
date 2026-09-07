package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.admin.AdminSecurityManager
import com.example.data.admin.AdminService
import com.example.data.admin.AdminWebServer
import com.example.data.local.BismaDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import kotlin.random.Random

class BismaRepository(private val context: Context) {
    val db = BismaDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs: SharedPreferences = context.getSharedPreferences("bisma_auth_prefs", Context.MODE_PRIVATE)

    // Admin & Web Server Infrastructure
    val adminSecurityManager = AdminSecurityManager(db)
    val adminService = AdminService(db, adminSecurityManager)
    val adminWebServer = AdminWebServer(adminService)

    init {
        scope.launch {
            adminService.initializeDefaultConfigs()
            adminWebServer.start()
        }
    }

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("KEY_IS_LOGGED_IN", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserId = MutableStateFlow(prefs.getString("KEY_CURRENT_USER_ID", "") ?: "")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    val currentUser: Flow<User?> = _currentUserId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(null) else db.userDao().getUserByIdFlow(id)
    }

    val currentUserRole: Flow<UserRoleAssignment?> = _currentUserId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(null) else db.userRoleDao().getRoleForUserFlow(id)
    }

    val allUserRoles: Flow<List<UserRoleAssignment>> = db.userRoleDao().getAllRolesFlow()
    val allAppConfigs: Flow<List<AppConfigEntity>> = db.appConfigDao().getAllConfigsFlow()
    val allReports: Flow<List<ReportEntity>> = db.reportDao().getAllReportsFlow()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = db.auditLogDao().getRecentAuditLogsFlow()

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

    // Floating emoji reaction event
    private val _emojiReactionEvent = MutableSharedFlow<RoomEmojiEvent>(extraBufferCapacity = 20)
    val emojiReactionEvent: SharedFlow<RoomEmojiEvent> = _emojiReactionEvent.asSharedFlow()

    // Lucky bag event
    private val _luckyBagEvent = MutableSharedFlow<LuckyBagEvent>(extraBufferCapacity = 10)
    val luckyBagEvent: SharedFlow<LuckyBagEvent> = _luckyBagEvent.asSharedFlow()

    // Active lucky bag state
    val activeLuckyBag = MutableStateFlow<LuckyBagEvent?>(null)
    val activeLuckyBags: Flow<List<LuckyBagEvent>> = activeLuckyBag.map { if (it != null) listOf(it) else emptyList() }

    // Rate limiter tracker for emojis (userId -> lastTimestamp)
    private val emojiRateLimits = mutableMapOf<String, Long>()

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

    // Rocket launch animation event
    private val _rocketLaunchEvent = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val rocketLaunchEvent: SharedFlow<String> = _rocketLaunchEvent.asSharedFlow()

    init {
        scope.launch {
            cleanAndSeedStoreCatalog()
        }
    }

    private suspend fun cleanAndSeedStoreCatalog() {
        // Purge any legacy demo/fake accounts and rooms
        val fakeUserIds = listOf("883921", "104928", "209411", "305182", "402819", "509124", "601832", "708912")
        fakeUserIds.forEach { fakeId ->
            db.userDao().deleteUserById(fakeId)
        }
        val fakeRoomIds = listOf("772184", "883109", "994012", "652190", "541829", "432901", "321876", "210985", "109874")
        fakeRoomIds.forEach { roomId ->
            db.roomDao().deleteRoomById(roomId)
            db.seatDao().clearSeatsForRoom(roomId)
        }
        db.agencyFamilyDao().deleteAgencyById("ag_1")
        db.agencyFamilyDao().deleteAgencyById("ag_2")
        db.agencyFamilyDao().deleteFamilyById("fam_1")
        db.agencyFamilyDao().deleteFamilyById("fam_2")
        db.momentDao().deleteMoment("m_1")
        db.notificationDao().deleteNotification("notif_1")

        // Seed 10 items in each category (Headwear, Entry Effects, Chat Bubbles, Sound Waves)
        val items = listOf(
            // Headwear (10 items)
            StoreItem("head_angel_wings", "Angel Wings Halo", "Headwear", 1500, "🪽", 0xFFFFFFFF, isOwned = false, isEquipped = false),
            StoreItem("head_devil_horns", "Neon Devil Horns", "Headwear", 1400, "😈", 0xFFFF1744, isOwned = false, isEquipped = false),
            StoreItem("head_imperial_tiara", "Golden Imperial Tiara", "Headwear", 2200, "👑", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("head_cyber_cat_ears", "Cyberpunk Cat Ears", "Headwear", 1800, "🐱", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("head_dragon_horns", "Mystic Dragon Horns", "Headwear", 3000, "🐉", 0xFFFF2A85, isOwned = false, isEquipped = false),
            StoreItem("head_sakura_crown", "Sakura Blossom Crown", "Headwear", 1600, "🌸", 0xFFFF80AB, isOwned = false, isEquipped = false),
            StoreItem("head_galaxy_stars", "Galaxy Star Headband", "Headwear", 2500, "✨", 0xFF7C4DFF, isOwned = false, isEquipped = false),
            StoreItem("head_sultan_turban", "Royal Sultan Turban", "Headwear", 2800, "👳", 0xFFFFAB00, isOwned = false, isEquipped = false),
            StoreItem("head_crystal_antlers", "Arctic Crystal Antlers", "Headwear", 2100, "🦌", 0xFF80D8FF, isOwned = false, isEquipped = false),
            StoreItem("head_phoenix_feathers", "Phoenix Feather Crest", "Headwear", 3500, "🪶", 0xFFFF6D00, isOwned = false, isEquipped = false),

            // Entry Effects (10 items)
            StoreItem("entry_supercar", "Lamborghini Supercar", "Entry Effects", 5000, "🏎️", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("entry_phoenix", "Phoenix Flame Burst", "Entry Effects", 8000, "🔥", 0xFFFF8800, isOwned = false, isEquipped = false),
            StoreItem("entry_space_shuttle", "Cyber Space Shuttle", "Entry Effects", 7500, "🚀", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("entry_pegasus_chariot", "Royal Pegasus Chariot", "Entry Effects", 9500, "🦄", 0xFFFF4081, isOwned = false, isEquipped = false),
            StoreItem("entry_thunder_portal", "Thunder Storm Portal", "Entry Effects", 6500, "⚡", 0xFFFFEB3B, isOwned = false, isEquipped = false),
            StoreItem("entry_golden_dragon", "Golden Dragon Descent", "Entry Effects", 12000, "🐉", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("entry_diamond_aurora", "Diamond Aurora Waves", "Entry Effects", 8800, "💎", 0xFF00E676, isOwned = false, isEquipped = false),
            StoreItem("entry_magic_carpet", "Magic Carpet Fly-In", "Entry Effects", 6000, "🧞", 0xFF7C4DFF, isOwned = false, isEquipped = false),
            StoreItem("entry_meteor_shower", "Meteor Shower Flight", "Entry Effects", 7000, "🌠", 0xFFFF3D00, isOwned = false, isEquipped = false),
            StoreItem("entry_crystal_carriage", "Crystal Castle Carriage", "Entry Effects", 11000, "🏰", 0xFFE040FB, isOwned = false, isEquipped = false),

            // Chat Bubbles (10 items)
            StoreItem("bubble_neon_glow", "Pink Neon Glow", "Chat Bubbles", 800, "💬", 0xFFFF2A85, isOwned = false, isEquipped = false),
            StoreItem("bubble_gold_royale", "Golden Royale Bubble", "Chat Bubbles", 1500, "⚜️", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("bubble_cyber_cyan", "Cyber Matrix Cyan", "Chat Bubbles", 1000, "📟", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("bubble_romantic_heart", "Romantic Hearts Bloom", "Chat Bubbles", 1200, "💖", 0xFFFF4081, isOwned = false, isEquipped = false),
            StoreItem("bubble_emerald_galaxy", "Emerald Galaxy Glow", "Chat Bubbles", 1100, "🟢", 0xFF00E676, isOwned = false, isEquipped = false),
            StoreItem("bubble_flame_inferno", "Flame Inferno Bubble", "Chat Bubbles", 1300, "🔥", 0xFFFF5722, isOwned = false, isEquipped = false),
            StoreItem("bubble_frost_crystal", "Ice Frost Crystal", "Chat Bubbles", 950, "❄️", 0xFF80D8FF, isOwned = false, isEquipped = false),
            StoreItem("bubble_purple_aura", "Midnight Purple Aura", "Chat Bubbles", 1150, "🔮", 0xFF7C4DFF, isOwned = false, isEquipped = false),
            StoreItem("bubble_sunset_radiance", "Sunset Radiance", "Chat Bubbles", 900, "🌅", 0xFFFF9800, isOwned = false, isEquipped = false),
            StoreItem("bubble_rainbow_prism", "Rainbow Prism Glow", "Chat Bubbles", 1600, "🌈", 0xFFE040FB, isOwned = false, isEquipped = false),

            // Sound Waves (10 items)
            StoreItem("sound_laser_wave", "Laser Sound Wave", "Sound Waves", 1000, "🌊", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("sound_neon_bass", "Neon Bass Pulse", "Sound Waves", 1200, "🔊", 0xFFFF2A85, isOwned = false, isEquipped = false),
            StoreItem("sound_golden_acoustic", "Golden Acoustic Ring", "Sound Waves", 1400, "🔔", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("sound_cyber_sonic", "Cyber Sonic Blast", "Sound Waves", 1500, "⚡", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("sound_heartbeat_pulse", "Heartbeat Resonance", "Sound Waves", 1100, "💓", 0xFFFF4081, isOwned = false, isEquipped = false),
            StoreItem("sound_ocean_tide", "Ocean Tide Rhythm", "Sound Waves", 950, "🌊", 0xFF00B0FF, isOwned = false, isEquipped = false),
            StoreItem("sound_lightning_beat", "Electric Lightning Beat", "Sound Waves", 1300, "⚡", 0xFFFFEB3B, isOwned = false, isEquipped = false),
            StoreItem("sound_royal_orchestra", "Royal Orchestra Symphony", "Sound Waves", 1800, "🎻", 0xFFFFAB00, isOwned = false, isEquipped = false),
            StoreItem("sound_firework_boom", "Firework Boom Wave", "Sound Waves", 1600, "🎆", 0xFFFF1744, isOwned = false, isEquipped = false),
            StoreItem("sound_crystal_chime", "Crystal Chime Melody", "Sound Waves", 1050, "🎵", 0xFF69F0AE, isOwned = false, isEquipped = false)
        )
        db.storeDao().insertAll(items)
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
        return createOrGetRoom(title, description, coverUrl, seatCount, country, category, isLocked, password)
    }

    suspend fun createOrGetRoom(
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
        val existingRoom = db.roomDao().getRoomByOwnerId(user.id)
        if (existingRoom != null) {
            val updated = existingRoom.copy(
                title = title.ifBlank { existingRoom.title },
                description = description.ifBlank { existingRoom.description },
                coverUrl = coverUrl.ifBlank { existingRoom.coverUrl },
                seatCount = seatCount,
                country = country,
                category = category,
                isLocked = isLocked,
                password = password,
                isActive = true,
                onlineCount = 1
            )
            db.roomDao().insertOrUpdate(updated)
            _activeRoomId.value = updated.id
            return updated.id
        }

        val newRoomId = (Random.nextInt(100000, 999999)).toString()
        val room = VoiceRoom(
            id = newRoomId,
            title = title.ifBlank { "${user.username}'s Voice Room" },
            description = description.ifBlank { "Welcome to AURA Live voice chat!" },
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

    fun getMyCreatedRoomFlow(): Flow<VoiceRoom?> = _currentUserId.flatMapLatest { uid ->
        db.roomDao().getOwnerRoomFlow(uid)
    }

    fun getSeatsForRoom(roomId: String): Flow<List<RoomSeat>> {
        return db.seatDao().getSeatsForRoomFlow(roomId)
    }

    fun getFollowersCountFlow(userId: String): Flow<Int> = db.socialDao().getFollowersCountFlow(userId)
    fun getFollowingCountFlow(userId: String): Flow<Int> = db.socialDao().getFollowingCountFlow(userId)
    fun getFriendsCountFlow(userId: String): Flow<Int> = db.socialDao().getFriendsCountFlow(userId)
    fun getVisitorsCountFlow(userId: String): Flow<Int> = db.visitorDao().getVisitorsCountFlow(userId)

    suspend fun refreshLiveListenerCounts() {
        val rooms = db.roomDao().getAllActiveRoomsFlow().firstOrNull() ?: return
        rooms.forEach { room ->
            val seats = db.seatDao().getSeatsForRoomFlow(room.id).firstOrNull() ?: emptyList()
            val activeOccupants = seats.count { it.userId != null }
            val realCount = if (activeOccupants > 0) activeOccupants else 0
            db.roomDao().updateOnlineCount(room.id, realCount)
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
            // Update real listener count
            val room = db.roomDao().getRoomById(roomId)
            if (room != null) {
                val seats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: emptyList()
                val activeOccupants = seats.count { it.userId != null }
                val realCount = activeOccupants.coerceAtLeast(1)
                db.roomDao().updateOnlineCount(roomId, realCount)
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
                val remainingOccupants = seats.count { it.userId != null && it.userId != userId }
                db.roomDao().updateOnlineCount(roomId, remainingOccupants.coerceAtLeast(0))
            }
        }
        _activeRoomId.value = null
    }

    suspend fun takeSeat(roomId: String, seatIndex: Int) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val currentSeats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: emptyList()

        // Strict 1 Seat per user rule: Clear any other seat occupied by this user in this room
        currentSeats.filter { it.userId == user.id && it.seatIndex != seatIndex }.forEach { oldSeat ->
            db.seatDao().updateSeat(oldSeat.copy(userId = null, username = null, avatarUrl = null, isSpeaking = false))
        }

        val targetSeat = currentSeats.find { it.seatIndex == seatIndex }
        if (targetSeat != null && targetSeat.userId != null && targetSeat.userId != user.id) {
            // Seat is already occupied by someone else
            return
        }

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

        // Sync room online user count
        val refreshedSeats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: emptyList()
        val count = refreshedSeats.count { it.userId != null }.coerceAtLeast(1)
        db.roomDao().updateOnlineCount(roomId, count)
    }

    suspend fun leaveSeat(roomId: String, seatIndex: Int) {
        val seat = RoomSeat(
            roomId = roomId,
            seatIndex = seatIndex,
            userId = null,
            username = null,
            avatarUrl = null,
            isSpeaking = false
        )
        db.seatDao().updateSeat(seat)
    }

    // Feedback Management
    fun getUserFeedbacksFlow(userId: String): Flow<List<FeedbackItem>> = db.feedbackDao().getUserFeedbacksFlow(userId)

    suspend fun submitAuraFeedback(category: String, subject: String, message: String): Result<FeedbackItem> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return@withContext Result.failure(Exception("Not logged in"))
        val feedback = FeedbackItem(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            userName = user.username,
            category = category,
            subject = subject.trim(),
            message = message.trim(),
            status = "Open",
            officialReply = null,
            timestamp = System.currentTimeMillis()
        )
        db.feedbackDao().insertFeedback(feedback)
        Result.success(feedback)
    }

    // AURA Task Operations
    suspend fun getAuraTasks(): List<AuraTask> = withContext(Dispatchers.IO) {
        val userId = _currentUserId.value
        val user = db.userDao().getUserById(userId) ?: return@withContext emptyList()
        val followingCount = db.socialDao().getFollowingCountFlow(userId).firstOrNull() ?: 0
        val moments = db.momentDao().getUserMomentsFlow(userId).firstOrNull() ?: emptyList()
        val walletTx = db.walletTransactionDao().getTransactionsFlow(userId).firstOrNull() ?: emptyList()
        val giftsSent = walletTx.count { it.description.contains("Sent Gift", ignoreCase = true) }

        val p = prefs
        listOf(
            AuraTask(
                id = "task_follow_creator",
                title = "Follow a Voice Creator",
                description = "Follow at least 1 host or room creator on AURA Live",
                rewardCoins = 100,
                rewardDiamonds = 5,
                targetCount = 1,
                currentCount = followingCount.coerceAtMost(1),
                isCompleted = followingCount >= 1,
                isClaimed = p.getBoolean("TASK_CLAIMED_follow", false),
                iconEmoji = "👥"
            ),
            AuraTask(
                id = "task_post_moment",
                title = "Publish Your First Moment",
                description = "Share a photo and story with the AURA community",
                rewardCoins = 150,
                rewardDiamonds = 10,
                targetCount = 1,
                currentCount = moments.size.coerceAtMost(1),
                isCompleted = moments.isNotEmpty(),
                isClaimed = p.getBoolean("TASK_CLAIMED_moment", false),
                iconEmoji = "📸"
            ),
            AuraTask(
                id = "task_send_gift",
                title = "Send a Voice Room Gift",
                description = "Support a speaker by sending a gift in any active voice room",
                rewardCoins = 200,
                rewardDiamonds = 15,
                targetCount = 1,
                currentCount = giftsSent.coerceAtMost(1),
                isCompleted = giftsSent >= 1,
                isClaimed = p.getBoolean("TASK_CLAIMED_gift", false),
                iconEmoji = "🎁"
            ),
            AuraTask(
                id = "task_active_voice",
                title = "Active Voice Participant",
                description = "Send 3 gifts or participate in voice room interactions",
                rewardCoins = 500,
                rewardDiamonds = 25,
                targetCount = 3,
                currentCount = giftsSent.coerceAtMost(3),
                isCompleted = giftsSent >= 3,
                isClaimed = p.getBoolean("TASK_CLAIMED_active", false),
                iconEmoji = "🎙️"
            )
        )
    }

    suspend fun claimAuraTaskReward(taskId: String): Result<String> = withContext(Dispatchers.IO) {
        val tasks = getAuraTasks()
        val task = tasks.find { it.id == taskId } ?: return@withContext Result.failure(Exception("Task not found"))
        if (!task.isCompleted) {
            return@withContext Result.failure(Exception("Task requirement not completed yet!"))
        }
        if (task.isClaimed) {
            return@withContext Result.failure(Exception("Task reward already claimed."))
        }

        val key = when (taskId) {
            "task_follow_creator" -> "TASK_CLAIMED_follow"
            "task_post_moment" -> "TASK_CLAIMED_moment"
            "task_send_gift" -> "TASK_CLAIMED_gift"
            else -> "TASK_CLAIMED_active"
        }
        prefs.edit().putBoolean(key, true).apply()
        db.userDao().updateBalance(_currentUserId.value, task.rewardCoins, task.rewardDiamonds)
        Result.success("Claimed ${task.rewardCoins} Coins & ${task.rewardDiamonds} Diamonds! 🎉")
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

    fun getFollowingMomentsFlow(): Flow<List<MomentPost>> = _currentUserId.flatMapLatest { userId ->
        val followingFlow = db.socialDao().getFollowingFlow(userId)
        val allMomentsFlow = db.momentDao().getAllMomentsFlow()
        combine(followingFlow, allMomentsFlow) { follows, moments ->
            val followingIds = follows.map { it.followingId }.toSet()
            moments.filter { it.authorId in followingIds || it.authorId == userId }
        }
    }

    fun getCommentsForMoment(momentId: String): Flow<List<MomentComment>> {
        return db.momentCommentDao().getCommentsForMomentFlow(momentId)
    }

    suspend fun addCommentToMoment(momentId: String, content: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val comment = MomentComment(
            id = UUID.randomUUID().toString(),
            momentId = momentId,
            authorId = user.id,
            authorName = user.username,
            authorAvatar = user.avatarUrl,
            authorVip = user.vipLevel,
            content = content
        )
        db.momentCommentDao().insertComment(comment)
    }

    suspend fun shareMomentToFriend(moment: MomentPost, friendUserId: String) {
        val currentUser = db.userDao().getUserById(_currentUserId.value) ?: return
        db.chatDao().insertMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                targetId = friendUserId,
                isRoomChat = false,
                senderId = currentUser.id,
                senderName = currentUser.username,
                senderAvatar = currentUser.avatarUrl,
                senderVip = currentUser.vipLevel,
                content = "📸 Shared a Moment from ${moment.authorName}: \"${moment.content}\""
            )
        )
    }

    suspend fun toggleLikeMoment(momentId: String, currentLiked: Boolean) {
        val delta = if (currentLiked) -1 else 1
        db.momentDao().toggleLike(momentId, delta, !currentLiked)
    }

    suspend fun deleteMoment(momentId: String) {
        db.momentDao().deleteMoment(momentId)
    }

    // Emoji reaction in Voice Room
    suspend fun sendEmojiReaction(roomId: String, emoji: String) {
        sendEmojiReaction(emoji, 0)
    }

    suspend fun sendEmojiReaction(emoji: String, seatIndex: Int = 0) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val roomId = _activeRoomId.value ?: return

        val lastSent = emojiRateLimits[user.id] ?: 0L
        val now = System.currentTimeMillis()
        if (now - lastSent < 400L) {
            return // rate limit
        }
        emojiRateLimits[user.id] = now

        val event = RoomEmojiEvent(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            userId = user.id,
            userName = user.username,
            userAvatar = user.avatarUrl,
            seatIndex = seatIndex,
            emoji = emoji
        )
        _emojiReactionEvent.emit(event)
    }

    // Lucky bag
    suspend fun spawnLuckyBag(roomId: String, coins: Int, claimers: Int = 10): Boolean {
        return spawnLuckyBag(coins.toLong(), claimers)
    }

    suspend fun spawnLuckyBag(coins: Long, claimers: Int = 10): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        val roomId = _activeRoomId.value ?: return false
        if (user.coins < coins) return false

        db.userDao().updateBalance(user.id, -coins, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Gift Sent",
                amountCoins = -coins,
                description = "Dropped Lucky Bag in Room #$roomId"
            )
        )

        val bag = LuckyBagEvent(
            id = UUID.randomUUID().toString(),
            roomId = roomId,
            senderId = user.id,
            senderName = user.username,
            senderAvatar = user.avatarUrl,
            totalCoins = coins,
            remainingCoins = coins,
            claimedCount = 0,
            maxClaims = claimers
        )
        activeLuckyBag.value = bag
        _luckyBagEvent.emit(bag)
        return true
    }

    suspend fun claimLuckyBag(bagId: String): Pair<Boolean, Long> {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return Pair(false, 0L)
        val bag = activeLuckyBag.value ?: return Pair(false, 0L)
        if (bag.id != bagId || bag.remainingCoins <= 0 || bag.claimedCount >= bag.maxClaims) return Pair(false, 0L)

        val share = (bag.remainingCoins / (bag.maxClaims - bag.claimedCount)).coerceAtLeast(10L)
        val actualGained = share.coerceAtMost(bag.remainingCoins)

        val updatedBag = bag.copy(
            remainingCoins = bag.remainingCoins - actualGained,
            claimedCount = bag.claimedCount + 1
        )
        activeLuckyBag.value = if (updatedBag.remainingCoins > 0 && updatedBag.claimedCount < updatedBag.maxClaims) updatedBag else null

        db.userDao().updateBalance(user.id, actualGained, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Game Win",
                amountCoins = actualGained,
                description = "Claimed Lucky Bag Reward"
            )
        )
        return Pair(true, actualGained)
    }

    // Rocket Launch
    suspend fun launchRocket(roomId: String): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        val rocketCost = 5000L
        if (user.coins < rocketCost) return false

        db.userDao().updateBalance(user.id, -rocketCost, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Gift Sent",
                amountCoins = -rocketCost,
                description = "Launched Super Rocket in Room #$roomId"
            )
        )

        val room = db.roomDao().getRoomById(roomId)
        if (room != null) {
            // Reward room owner with diamonds
            val ownerReward = 500L
            db.userDao().updateBalance(room.ownerId, 0, ownerReward)
        }

        _rocketLaunchEvent.emit("🚀 ${user.username} Launched a Giant Interstellar Rocket!")
        return true
    }

    // CP Relationship
    fun getCpForUserFlow(userId: String): Flow<CpRelationship?> = db.cpDao().getCpForUserFlow(userId)
    fun getTopCpListFlow(): Flow<List<CpRelationship>> = db.cpDao().getTopCpListFlow()

    suspend fun proposeOrAcceptCp(targetUserId: String, ringName: String) {
        val user1 = db.userDao().getUserById(_currentUserId.value) ?: return
        val user2 = db.userDao().getUserById(targetUserId) ?: return
        val cp = CpRelationship(
            user1Id = user1.id,
            user1Name = user1.username,
            user1Avatar = user1.avatarUrl,
            user2Id = user2.id,
            user2Name = user2.username,
            user2Avatar = user2.avatarUrl,
            intimacyScore = 2500,
            cpLevel = 1,
            ringName = ringName.ifBlank { "Eternal Diamond Band 💍" }
        )
        db.cpDao().insertOrUpdateCp(cp)
    }

    suspend fun dissolveCp(userId: String) {
        db.cpDao().dissolveCp(userId)
    }

    fun getAgencyMembersFlow(agencyId: String): Flow<List<User>> = db.userDao().getAgencyMembersFlow(agencyId)
    fun getAgencyJoinRequestsFlow(agencyId: String): Flow<List<AgencyJoinRequest>> = db.agencyInteractionDao().getPendingRequestsForAgencyFlow(agencyId)

    suspend fun requestToJoinAgency(agencyId: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val agency = db.agencyFamilyDao().getAgencyById(agencyId) ?: return
        val req = AgencyJoinRequest(
            id = UUID.randomUUID().toString(),
            agencyId = agencyId,
            agencyName = agency.name,
            userId = user.id,
            userName = user.username,
            userAvatar = user.avatarUrl
        )
        db.agencyInteractionDao().insertJoinRequest(req)
    }

    suspend fun respondToAgencyJoinRequest(requestId: String, accept: Boolean) {
        val requests = db.agencyInteractionDao().getRequestsByUserFlow(_currentUserId.value).firstOrNull() ?: emptyList()
        db.agencyInteractionDao().updateRequestStatus(requestId, if (accept) "accepted" else "rejected")
    }

    suspend fun removeUserFromAgency(agencyId: String, userId: String) {
        val user = db.userDao().getUserById(userId) ?: return
        db.userDao().insertOrUpdate(user.copy(agencyId = null, agencyName = null))
        val agency = db.agencyFamilyDao().getAgencyById(agencyId)
        if (agency != null && agency.memberCount > 1) {
            db.agencyFamilyDao().insertAgency(agency.copy(memberCount = agency.memberCount - 1))
        }
    }

    suspend fun leaveAgency(userId: String) {
        val user = db.userDao().getUserById(userId) ?: return
        val agencyId = user.agencyId
        db.userDao().insertOrUpdate(user.copy(agencyId = null, agencyName = null))
        if (agencyId != null) {
            val agency = db.agencyFamilyDao().getAgencyById(agencyId)
            if (agency != null && agency.memberCount > 1) {
                db.agencyFamilyDao().insertAgency(agency.copy(memberCount = agency.memberCount - 1))
            }
        }
    }

    suspend fun updateAgencyProfile(agencyId: String, name: String, announcement: String, logoUrl: String) {
        val agency = db.agencyFamilyDao().getAgencyById(agencyId) ?: return
        db.agencyFamilyDao().insertAgency(
            agency.copy(
                name = name.ifBlank { agency.name },
                announcement = announcement.ifBlank { agency.announcement },
                logoUrl = logoUrl.ifBlank { agency.logoUrl }
            )
        )
    }

    suspend fun createAgency(name: String, agencyCode: String, bdId: String, logoUrl: String): String {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return ""
        val newAgencyId = "AG_" + Random.nextInt(10000, 99999)
        val code = if (agencyCode.isNotBlank()) agencyCode else "BISMA_" + Random.nextInt(100, 999)
        val agency = Agency(
            id = newAgencyId,
            name = name.ifBlank { "${user.username}'s Agency" },
            logoUrl = logoUrl.ifBlank { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300" },
            ownerId = user.id,
            ownerName = user.username,
            agencyCode = code,
            bdId = bdId.ifBlank { "BD_OFFICIAL" },
            memberCount = 1,
            level = 1,
            announcement = "Welcome to $name!"
        )
        db.agencyFamilyDao().insertAgency(agency)
        db.userDao().insertOrUpdate(user.copy(agencyId = newAgencyId, agencyName = agency.name))
        return newAgencyId
    }

    // Agency System
    suspend fun searchAgencyByCode(code: String): Agency? {
        val all = db.agencyFamilyDao().getAllAgenciesFlow().firstOrNull() ?: emptyList()
        return all.find { it.agencyCode.equals(code.trim(), ignoreCase = true) || it.id.equals(code.trim(), ignoreCase = true) }
    }

    suspend fun createAgency(name: String, logoUrl: String, bdId: String): String {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return ""
        val newAgencyId = "AG_" + Random.nextInt(10000, 99999)
        val generatedCode = "BISMA_" + Random.nextInt(100, 999)
        val agency = Agency(
            id = newAgencyId,
            name = name.ifBlank { "${user.username}'s Agency" },
            logoUrl = logoUrl.ifBlank { "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300" },
            ownerId = user.id,
            ownerName = user.username,
            agencyCode = generatedCode,
            bdId = bdId.ifBlank { "BD_OFFICIAL" },
            memberCount = 1,
            level = 1,
            announcement = "Welcome to $name!"
        )
        db.agencyFamilyDao().insertAgency(agency)
        return newAgencyId
    }

    suspend fun requestJoinAgency(agencyId: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val agency = db.agencyFamilyDao().getAgencyById(agencyId) ?: return
        val req = AgencyJoinRequest(
            id = UUID.randomUUID().toString(),
            agencyId = agencyId,
            agencyName = agency.name,
            userId = user.id,
            userName = user.username,
            userAvatar = user.avatarUrl
        )
        db.agencyInteractionDao().insertJoinRequest(req)
    }

    fun getPendingRequestsForAgency(agencyId: String): Flow<List<AgencyJoinRequest>> {
        return db.agencyInteractionDao().getPendingRequestsForAgencyFlow(agencyId)
    }

    suspend fun respondToAgencyRequest(requestId: String, agencyId: String, accept: Boolean) {
        db.agencyInteractionDao().updateRequestStatus(requestId, if (accept) "accepted" else "rejected")
        if (accept) {
            val agency = db.agencyFamilyDao().getAgencyById(agencyId)
            if (agency != null) {
                db.agencyFamilyDao().insertAgency(agency.copy(memberCount = agency.memberCount + 1))
            }
        }
    }

    suspend fun inviteUserToAgency(agencyId: String, inviteeUserId: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val agency = db.agencyFamilyDao().getAgencyById(agencyId) ?: return
        val inv = AgencyInvitation(
            id = UUID.randomUUID().toString(),
            agencyId = agencyId,
            agencyName = agency.name,
            agencyLogo = agency.logoUrl,
            inviterId = user.id,
            inviterName = user.username,
            inviteeId = inviteeUserId
        )
        db.agencyInteractionDao().insertInvitation(inv)
    }

    fun getPendingInvitationsForUser(userId: String): Flow<List<AgencyInvitation>> {
        return db.agencyInteractionDao().getPendingInvitationsForUserFlow(userId)
    }

    suspend fun respondToAgencyInvitation(invitationId: String, agencyId: String, accept: Boolean) {
        db.agencyInteractionDao().updateInvitationStatus(invitationId, if (accept) "accepted" else "rejected")
        if (accept) {
            val agency = db.agencyFamilyDao().getAgencyById(agencyId)
            if (agency != null) {
                db.agencyFamilyDao().insertAgency(agency.copy(memberCount = agency.memberCount + 1))
            }
        }
    }

    suspend fun removeAgencyMember(agencyId: String) {
        val agency = db.agencyFamilyDao().getAgencyById(agencyId) ?: return
        if (agency.memberCount > 1) {
            db.agencyFamilyDao().insertAgency(agency.copy(memberCount = agency.memberCount - 1))
        }
    }

    suspend fun updateAgencyDetails(agencyId: String, name: String, logoUrl: String, announcement: String) {
        val agency = db.agencyFamilyDao().getAgencyById(agencyId) ?: return
        db.agencyFamilyDao().insertAgency(
            agency.copy(
                name = name.ifBlank { agency.name },
                logoUrl = logoUrl.ifBlank { agency.logoUrl },
                announcement = announcement.ifBlank { agency.announcement }
            )
        )
    }

    // Room Host and Admin Management
    suspend fun lockSeat(roomId: String, seatIndex: Int, isLocked: Boolean) {
        val seats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: return
        val seat = seats.find { it.seatIndex == seatIndex } ?: return
        db.seatDao().updateSeat(seat.copy(isLocked = isLocked))
    }

    suspend fun moveSeat(roomId: String, fromIndex: Int, toIndex: Int) {
        val seats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: return
        val fromSeat = seats.find { it.seatIndex == fromIndex } ?: return
        val toSeat = seats.find { it.seatIndex == toIndex } ?: return

        db.seatDao().updateSeat(
            fromSeat.copy(userId = null, username = null, avatarUrl = null, isSpeaking = false)
        )
        db.seatDao().updateSeat(
            toSeat.copy(
                userId = fromSeat.userId,
                username = fromSeat.username,
                avatarUrl = fromSeat.avatarUrl,
                vipLevel = fromSeat.vipLevel,
                userLevel = fromSeat.userLevel,
                frameId = fromSeat.frameId,
                isMuted = fromSeat.isMuted,
                isSpeaking = false
            )
        )
    }

    suspend fun kickUserFromRoom(roomId: String, targetUserId: String) {
        val seats = db.seatDao().getSeatsForRoomFlow(roomId).firstOrNull() ?: emptyList()
        seats.filter { it.userId == targetUserId }.forEach { seat ->
            db.seatDao().updateSeat(seat.copy(userId = null, username = null, avatarUrl = null))
        }
    }

    suspend fun blockUserFromRoom(roomId: String, targetUserId: String) {
        val room = db.roomDao().getRoomById(roomId) ?: return
        val blockedList = room.blockedUserIds.split(",").filter { it.isNotBlank() }.toMutableList()
        if (!blockedList.contains(targetUserId)) {
            blockedList.add(targetUserId)
        }
        db.roomDao().insertOrUpdate(room.copy(blockedUserIds = blockedList.joinToString(",")))
        kickUserFromRoom(roomId, targetUserId)
    }

    suspend fun unblockUserFromRoom(roomId: String, targetUserId: String) {
        val room = db.roomDao().getRoomById(roomId) ?: return
        val blockedList = room.blockedUserIds.split(",").filter { it.isNotBlank() && it != targetUserId }
        db.roomDao().insertOrUpdate(room.copy(blockedUserIds = blockedList.joinToString(",")))
    }

    suspend fun toggleRoomAdmin(roomId: String, targetUserId: String) {
        val room = db.roomDao().getRoomById(roomId) ?: return
        val adminList = room.adminUserIds.split(",").filter { it.isNotBlank() }.toMutableList()
        if (adminList.contains(targetUserId)) {
            adminList.remove(targetUserId)
        } else {
            adminList.add(targetUserId)
        }
        db.roomDao().insertOrUpdate(room.copy(adminUserIds = adminList.joinToString(",")))
    }

    suspend fun toggleRoomSoundMute(roomId: String) {
        val room = db.roomDao().getRoomById(roomId) ?: return
        db.roomDao().insertOrUpdate(room.copy(soundMuted = !room.soundMuted))
    }

    suspend fun toggleRoomMusic(roomId: String, isPlaying: Boolean, track: String) {
        val room = db.roomDao().getRoomById(roomId) ?: return
        db.roomDao().insertOrUpdate(room.copy(isMusicPlaying = isPlaying, musicTrackName = track))
    }

    suspend fun updateRoomSettings(
        roomId: String,
        title: String,
        announcement: String,
        coverUrl: String,
        seatCount: Int,
        bg: String,
        allowPublicChat: Boolean,
        heartbeat: Boolean
    ) {
        val room = db.roomDao().getRoomById(roomId) ?: return
        db.roomDao().insertOrUpdate(
            room.copy(
                title = title.ifBlank { room.title },
                announcement = announcement.ifBlank { room.announcement },
                coverUrl = coverUrl.ifBlank { room.coverUrl },
                seatCount = seatCount,
                backgroundRes = bg,
                allowPublicChat = allowPublicChat,
                heartbeatValueDisplay = heartbeat
            )
        )
    }

    // VIP Purchase
    suspend fun purchaseVip(level: Int, costCoins: Long): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        if (user.coins < costCoins) return false

        db.userDao().updateBalance(user.id, -costCoins, 0)
        db.userDao().updateVip(user.id, level)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Store Purchase",
                amountCoins = -costCoins,
                description = "Upgraded to VIP $level"
            )
        )
        return true
    }

    fun isIncognitoEntryAllowed(vipLevel: Int): Boolean = vipLevel >= 6

    // Feedback
    suspend fun submitFeedback(category: String, message: String, contact: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        db.reportDao().insertReport(
            ReportEntity(
                id = UUID.randomUUID().toString(),
                reporterId = user.id,
                reporterName = user.username,
                targetType = "Feedback",
                targetId = "SYS_FEEDBACK",
                targetTitleOrName = "User Feedback: $category",
                reason = category,
                details = "Message: $message | Contact: $contact"
            )
        )
    }

    // Recharge with Gateway
    suspend fun rechargeWithGateway(method: String, coins: Long, priceStr: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        db.userDao().updateBalance(user.id, coins, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Recharge",
                amountCoins = coins,
                description = "Purchased $coins Coins via $method ($priceStr)"
            )
        )
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
        if (currentId.isBlank()) return
        db.socialDao().insertFriendship(Friendship(userId = senderId, friendId = currentId, status = "accepted"))
        val currentFriends = db.socialDao().getFriendsFlow(currentId).firstOrNull()?.size ?: 0
        val senderFriends = db.socialDao().getFriendsFlow(senderId).firstOrNull()?.size ?: 0
        val currentUser = db.userDao().getUserById(currentId)
        if (currentUser != null) {
            db.userDao().insertOrUpdate(currentUser.copy(friendsCount = currentFriends))
        }
        val senderUser = db.userDao().getUserById(senderId)
        if (senderUser != null) {
            db.userDao().insertOrUpdate(senderUser.copy(friendsCount = senderFriends))
        }
    }

    suspend fun rejectFriendRequest(senderId: String) {
        val currentId = _currentUserId.value
        if (currentId.isBlank()) return
        db.socialDao().deleteFriendship(senderId, currentId)
    }

    suspend fun toggleFollow(targetUserId: String): Boolean {
        val currentId = _currentUserId.value
        if (currentId.isBlank() || currentId == targetUserId) return false
        val isFollowing = db.socialDao().isFollowing(currentId, targetUserId)
        if (isFollowing) {
            db.socialDao().deleteFollow(currentId, targetUserId)
            val currentFollowing = db.socialDao().getFollowingFlow(currentId).firstOrNull()?.size ?: 0
            val targetFollowers = db.socialDao().getFollowersFlow(targetUserId).firstOrNull()?.size ?: 0
            val currentUser = db.userDao().getUserById(currentId)
            if (currentUser != null) {
                db.userDao().insertOrUpdate(currentUser.copy(followingCount = currentFollowing))
            }
            val targetUser = db.userDao().getUserById(targetUserId)
            if (targetUser != null) {
                db.userDao().insertOrUpdate(targetUser.copy(followersCount = targetFollowers))
            }
            return false
        } else {
            db.socialDao().insertFollow(Follow(followerId = currentId, followingId = targetUserId))
            val currentFollowing = db.socialDao().getFollowingFlow(currentId).firstOrNull()?.size ?: 0
            val targetFollowers = db.socialDao().getFollowersFlow(targetUserId).firstOrNull()?.size ?: 0
            val currentUser = db.userDao().getUserById(currentId)
            if (currentUser != null) {
                db.userDao().insertOrUpdate(currentUser.copy(followingCount = currentFollowing))
            }
            val targetUser = db.userDao().getUserById(targetUserId)
            if (targetUser != null) {
                db.userDao().insertOrUpdate(targetUser.copy(followersCount = targetFollowers))
            }
            if (currentUser != null) {
                db.notificationDao().insertNotification(
                    NotificationItem(
                        id = UUID.randomUUID().toString(),
                        userId = targetUserId,
                        type = "follow",
                        title = "New Follower",
                        message = "${currentUser.username} started following you!",
                        senderId = currentUser.id,
                        senderName = currentUser.username,
                        senderAvatar = currentUser.avatarUrl
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

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest((password + "bisma_voice_salt_2026").toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun generateUniqueUserId(): String = withContext(Dispatchers.IO) {
        var idCandidate: String
        do {
            idCandidate = Random.nextInt(100000, 999999).toString()
        } while (db.userDao().countUserById(idCandidate) > 0)
        idCandidate
    }

    suspend fun loginWithId(id: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        val trimmedId = id.trim()
        val user = db.userDao().getUserById(trimmedId)
            ?: return@withContext Result.failure(Exception("Account with ID '$trimmedId' not found. Please create an account."))

        val inputHash = hashPassword(password)
        // If the user's passwordHash is empty (legacy) or matches inputHash
        if (user.passwordHash.isNotEmpty() && user.passwordHash != inputHash) {
            return@withContext Result.failure(Exception("Incorrect password. Please try again or use Forgot Password."))
        }

        // Set session
        _currentUserId.value = user.id
        _isLoggedIn.value = true
        prefs.edit()
            .putString("KEY_CURRENT_USER_ID", user.id)
            .putBoolean("KEY_IS_LOGGED_IN", true)
            .apply()

        Result.success(user)
    }

    suspend fun createAccount(
        username: String,
        avatarUrl: String,
        gender: String,
        dateOfBirth: String,
        password: String,
        email: String? = null,
        customId: String? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        val nameTrimmed = username.trim()
        if (nameTrimmed.isEmpty()) {
            return@withContext Result.failure(Exception("Please enter your name."))
        }
        if (password.length < 4) {
            return@withContext Result.failure(Exception("Password must be at least 4 characters long."))
        }

        val assignedId = if (!customId.isNullOrBlank() && db.userDao().countUserById(customId) == 0) {
            customId
        } else {
            generateUniqueUserId()
        }

        val newUser = User(
            id = assignedId,
            username = nameTrimmed,
            avatarUrl = avatarUrl.ifBlank {
                if (gender.equals("Female", ignoreCase = true))
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300"
                else
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300"
            },
            gender = gender,
            dateOfBirth = dateOfBirth,
            passwordHash = hashPassword(password),
            email = email,
            bio = "Hey there! I am using Bisma Voice Chat ✨",
            country = "🇵🇰 Pakistan",
            language = "English",
            userLevel = 1,
            richLevel = 0,
            charmLevel = 0,
            vipLevel = 0,
            coins = 0,
            diamonds = 0,
            followersCount = 0,
            followingCount = 0,
            friendsCount = 0,
            equippedFrameId = null
        )

        db.userDao().insertOrUpdate(newUser)

        // Save session
        _currentUserId.value = newUser.id
        _isLoggedIn.value = true
        prefs.edit()
            .putString("KEY_CURRENT_USER_ID", newUser.id)
            .putBoolean("KEY_IS_LOGGED_IN", true)
            .apply()

        Result.success(newUser)
    }

    suspend fun loginWithGoogle(
        email: String,
        displayName: String,
        avatarUrl: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val existing = db.userDao().getUserByEmail(email.trim())
        if (existing != null) {
            _currentUserId.value = existing.id
            _isLoggedIn.value = true
            prefs.edit()
                .putString("KEY_CURRENT_USER_ID", existing.id)
                .putBoolean("KEY_IS_LOGGED_IN", true)
                .apply()
            return@withContext Result.success(existing)
        }

        // Create new account for Google user
        val newId = generateUniqueUserId()
        val googleUser = User(
            id = newId,
            username = displayName.ifBlank { "User $newId" },
            avatarUrl = avatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300" },
            gender = "Male",
            dateOfBirth = "2000-01-01",
            passwordHash = hashPassword(UUID.randomUUID().toString()),
            email = email.trim(),
            bio = "Hey there! I am using Bisma Voice Chat ✨",
            country = "🇵🇰 Pakistan",
            coins = 0,
            diamonds = 0,
            userLevel = 1,
            richLevel = 0,
            charmLevel = 0,
            vipLevel = 0,
            followersCount = 0,
            followingCount = 0,
            friendsCount = 0,
            equippedFrameId = null
        )

        db.userDao().insertOrUpdate(googleUser)

        _currentUserId.value = googleUser.id
        _isLoggedIn.value = true
        prefs.edit()
            .putString("KEY_CURRENT_USER_ID", googleUser.id)
            .putBoolean("KEY_IS_LOGGED_IN", true)
            .apply()

        Result.success(googleUser)
    }

    suspend fun resetPassword(idOrEmail: String, newPassword: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val query = idOrEmail.trim()
        if (newPassword.length < 4) {
            return@withContext Result.failure(Exception("New password must be at least 4 characters."))
        }

        val user = db.userDao().getUserById(query) ?: db.userDao().getUserByEmail(query)
            ?: return@withContext Result.failure(Exception("No account found matching ID or Email '$query'."))

        val newHash = hashPassword(newPassword)
        db.userDao().updatePassword(user.id, newHash)
        Result.success(true)
    }

    fun logout() {
        _isLoggedIn.value = false
        prefs.edit()
            .putBoolean("KEY_IS_LOGGED_IN", false)
            .apply()
    }

    suspend fun updateProfile(
        username: String,
        avatarUrl: String,
        gender: String,
        dateOfBirth: String,
        bio: String,
        country: String,
        language: String
    ) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        val updated = user.copy(
            username = username.ifBlank { user.username },
            avatarUrl = avatarUrl.ifBlank { user.avatarUrl },
            gender = gender.ifBlank { user.gender },
            dateOfBirth = dateOfBirth.ifBlank { user.dateOfBirth },
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
        _isLoggedIn.value = true
        prefs.edit()
            .putString("KEY_CURRENT_USER_ID", userId)
            .putBoolean("KEY_IS_LOGGED_IN", true)
            .apply()
    }
}
