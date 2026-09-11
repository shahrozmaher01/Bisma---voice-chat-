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

    // Owner Panel Unified State Flows
    val users: Flow<List<User>> = db.userDao().getAllUsersFlow()
    val userRoles: Flow<List<UserRoleAssignment>> = allUserRoles
    val rooms: Flow<List<VoiceRoom>> = db.roomDao().getAllRoomsFlow()
    val withdrawalRequests: Flow<List<WithdrawalRequest>> = db.withdrawalRequestDao().getAllWithdrawalsFlow()
    val rechargePackages: Flow<List<RechargePackage>> = db.rechargePackageDao().getActivePackagesFlow()
    val moderationReports: Flow<List<ReportEntity>> = allReports
    val appConfigs: Flow<List<AppConfigEntity>> = allAppConfigs
    val officialAssignmentsFlow: Flow<List<OfficialFrameAssignment>> = db.officialFrameDao().getAllAssignmentsFlow()

    suspend fun getOfficialAssignmentsSnapshot(): List<OfficialFrameAssignment> = withContext(Dispatchers.IO) {
        db.officialFrameDao().getAllAssignments()
    }

    val activeRooms: Flow<List<VoiceRoom>> = db.roomDao().getAllActiveRoomsFlow()
    val topWealthUsers: Flow<List<User>> = db.userDao().getTopWealthUsers()
    val topCharmUsers: Flow<List<User>> = db.userDao().getTopCharmUsers()
    val allMoments: Flow<List<MomentPost>> = db.momentDao().getAllMomentsFlow()
    val storeItems: Flow<List<StoreItem>> = db.storeDao().getAllStoreItemsFlow()
    val backpackItems: Flow<List<StoreItem>> = db.storeDao().getBackpackItemsFlow()
    val agencies: Flow<List<Agency>> = db.agencyFamilyDao().getAllAgenciesFlow()
    val families: Flow<List<Family>> = db.agencyFamilyDao().getAllFamiliesFlow()

    // Currency System Flows
    val currencyConfigFlow: Flow<CurrencyConfig> = db.currencyConfigDao().getConfigFlow().map { it ?: CurrencyConfig() }
    val rechargePackagesFlow: Flow<List<RechargePackage>> = db.rechargePackageDao().getActivePackagesFlow()


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

        // Seed items in categories (VIP Luxury, Neon Glow, Anime Fantasy, Official & Roles, Romantic CP, Entry Effects, Chat Bubbles, Sound Waves)
        val items = listOf(
            // VIP Luxury Frames
            StoreItem("frame_imperial_tiara", "Imperial Gold Tiara", "VIP Luxury", 2200, "👑", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("frame_sultan_crown", "Royal Sultan Crown", "VIP Luxury", 3500, "👳", 0xFFFFAB00, isOwned = false, isEquipped = false),
            StoreItem("frame_galaxy_gold", "Galaxy 24K Gold Orbit", "VIP Luxury", 4500, "✨", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("frame_diamond_monarch", "Diamond Monarch Halo", "VIP Luxury", 5000, "💎", 0xFF00E5FF, isOwned = false, isEquipped = false),

            // Neon Glow Frames
            StoreItem("frame_devil_horns", "Neon Devil Horns", "Neon Glow", 1400, "😈", 0xFFFF1744, isOwned = false, isEquipped = false),
            StoreItem("frame_cyber_cyan", "Cyberpunk Hologram", "Neon Glow", 1800, "🐱", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("frame_electric_violet", "Electric Violet Pulse", "Neon Glow", 2000, "⚡", 0xFFE040FB, isOwned = false, isEquipped = false),
            StoreItem("frame_neon_matrix", "Neon Matrix Wireframe", "Neon Glow", 2400, "🌐", 0xFF00E676, isOwned = false, isEquipped = false),

            // Anime Fantasy Frames
            StoreItem("frame_angel_wings", "Angelic Wings Aura", "Anime Fantasy", 1500, "🪽", 0xFFFFFFFF, isOwned = false, isEquipped = false),
            StoreItem("frame_dragon_horns", "Mystic Dragon Spirit", "Anime Fantasy", 3000, "🐉", 0xFFFF2A85, isOwned = false, isEquipped = false),
            StoreItem("frame_sakura_blossom", "Sakura Petal Breeze", "Anime Fantasy", 1600, "🌸", 0xFFFF80AB, isOwned = false, isEquipped = false),
            StoreItem("frame_phoenix_crest", "Phoenix Flame Crest", "Anime Fantasy", 3200, "🪶", 0xFFFF6D00, isOwned = false, isEquipped = false),

            // Official & Roles Frames
            StoreItem("frame_official_admin", "Official Super Admin", "Official & Roles", 10000, "🛡️", 0xFFFFD700, isOwned = false, isEquipped = false),
            StoreItem("frame_official_host", "Elite Verified Host", "Official & Roles", 8000, "🎙️", 0xFF00E5FF, isOwned = false, isEquipped = false),
            StoreItem("frame_official_manager", "Agency Senior Manager", "Official & Roles", 9000, "💼", 0xFFE040FB, isOwned = false, isEquipped = false),

            // Romantic CP Frames
            StoreItem("frame_romantic_heart", "Romantic Sweetheart Glow", "Romantic CP", 1800, "💖", 0xFFFF4081, isOwned = false, isEquipped = false),
            StoreItem("frame_twin_flame", "Twin Flame Infinity", "Romantic CP", 2500, "🔥", 0xFFFF5722, isOwned = false, isEquipped = false),
            StoreItem("frame_starlight_lovers", "Starlight Celestial Couple", "Romantic CP", 2800, "🌟", 0xFF7C4DFF, isOwned = false, isEquipped = false),

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

        // Seed Currency Configuration (Official Rate: 1 USD = 28,000 Coins)
        val existingConfig = db.currencyConfigDao().getConfig()
        if (existingConfig == null) {
            db.currencyConfigDao().insertOrUpdate(
                CurrencyConfig(
                    id = "aura_currency_config",
                    coinsPerUsd = 28000L,
                    diamondsPerUsd = 2800L,
                    hostGiftCommissionPercent = 70.0,
                    agencyGiftCommissionPercent = 10.0,
                    platformFeePercent = 20.0,
                    minWithdrawalDiamonds = 10000L,
                    maxDailyWithdrawalDiamonds = 5000000L,
                    isWithdrawalEnabled = true,
                    isRechargeEnabled = true
                )
            )
        }

        // Seed Default Recharge Packages based on official rate (1 USD = 28,000 Coins)
        val existingPackages = db.rechargePackageDao().getAllPackages()
        if (existingPackages.isEmpty()) {
            val packages = listOf(
                RechargePackage(
                    id = "pkg_28k",
                    coins = 28000L,
                    priceUsd = 0.99,
                    bonusCoins = 0L,
                    label = "Starter Pack",
                    isPopular = false,
                    isBestValue = false,
                    sortOrder = 1
                ),
                RechargePackage(
                    id = "pkg_145k",
                    coins = 140000L,
                    priceUsd = 4.99,
                    bonusCoins = 5000L,
                    label = "+5,000 Bonus",
                    isPopular = true,
                    isBestValue = false,
                    sortOrder = 2
                ),
                RechargePackage(
                    id = "pkg_300k",
                    coins = 280000L,
                    priceUsd = 9.99,
                    bonusCoins = 20000L,
                    label = "+20,000 Bonus",
                    isPopular = false,
                    isBestValue = false,
                    sortOrder = 3
                ),
                RechargePackage(
                    id = "pkg_750k",
                    coins = 700000L,
                    priceUsd = 24.99,
                    bonusCoins = 50000L,
                    label = "+50,000 Bonus",
                    isPopular = false,
                    isBestValue = false,
                    sortOrder = 4
                ),
                RechargePackage(
                    id = "pkg_1500k",
                    coins = 1400000L,
                    priceUsd = 49.99,
                    bonusCoins = 150000L,
                    label = "Popular Host Support",
                    isPopular = false,
                    isBestValue = false,
                    sortOrder = 5
                ),
                RechargePackage(
                    id = "pkg_3200k",
                    coins = 2800000L,
                    priceUsd = 99.99,
                    bonusCoins = 400000L,
                    label = "Best Value Mega Pack",
                    isPopular = false,
                    isBestValue = true,
                    sortOrder = 6
                )
            )
            db.rechargePackageDao().insertAll(packages)
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

    val unreadNotificationsCountFlow: Flow<Int> = _currentUserId.flatMapLatest { uid ->
        db.notificationDao().getUnreadNotificationsCountFlow(uid)
    }

    val unreadChatCountFlow: Flow<Int> = _currentUserId.flatMapLatest { uid ->
        db.chatDao().getUnreadPrivateMessagesCountFlow(uid)
    }

    suspend fun markAllNotificationsRead() {
        db.notificationDao().markAllRead(_currentUserId.value)
    }

    suspend fun markChatMessagesRead(peerId: String) {
        db.chatDao().markPrivateMessagesRead(_currentUserId.value, peerId)
    }

    suspend fun getOrCreatePrimaryRoom(): VoiceRoom {
        val user = db.userDao().getUserById(_currentUserId.value) ?: throw IllegalStateException("User not logged in")
        val existing = db.roomDao().getRoomByOwnerId(user.id)
        if (existing != null) {
            if (!existing.isActive) {
                val reactivated = existing.copy(isActive = true, onlineCount = 1)
                db.roomDao().insertOrUpdate(reactivated)
            }
            _activeRoomId.value = existing.id
            return existing
        }
        val newRoomId = (Random.nextInt(100000, 999999)).toString()
        val room = VoiceRoom(
            id = newRoomId,
            title = "${user.username}'s Room",
            description = "Welcome to ${user.username}'s official room! 🎙️",
            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
            ownerId = user.id,
            ownerName = user.username,
            ownerAvatar = user.avatarUrl,
            ownerVip = user.vipLevel,
            country = user.country,
            seatCount = 8,
            isLocked = false,
            password = "",
            category = "Party",
            onlineCount = 1,
            isActive = true
        )
        db.roomDao().insertOrUpdate(room)
        initSeatsForRoom(newRoomId, 8, user.id, user.username, user.avatarUrl, user.vipLevel)
        _activeRoomId.value = newRoomId
        return room
    }

    suspend fun saveAndCompressImage(uriString: String, prefix: String): String = withContext(Dispatchers.IO) {
        if (!uriString.startsWith("content://") && !uriString.startsWith("file://")) {
            return@withContext uriString
        }
        try {
            val uri = android.net.Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext uriString
            val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return@withContext uriString

            val maxDim = 1024
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scaledBitmap = if (width > maxDim || height > maxDim) {
                val ratio = width.toFloat() / height.toFloat()
                val newWidth = if (width > height) maxDim else (maxDim * ratio).toInt()
                val newHeight = if (height >= width) maxDim else (maxDim / ratio).toInt()
                android.graphics.Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            } else {
                originalBitmap
            }

            val mediaDir = java.io.File(context.filesDir, "room_media").apply { mkdirs() }
            val destFile = java.io.File(mediaDir, "${prefix}_${System.currentTimeMillis()}.jpg")
            val outputStream = java.io.FileOutputStream(destFile)
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()

            if (scaledBitmap != originalBitmap) {
                scaledBitmap.recycle()
            }
            originalBitmap.recycle()

            destFile.absolutePath
        } catch (e: Exception) {
            uriString
        }
    }

    suspend fun updateRoomDP(roomId: String, newDpUrl: String): Boolean {
        val room = db.roomDao().getRoomById(roomId) ?: return false
        val currentUid = _currentUserId.value
        val isOwner = room.ownerId == currentUid
        val isAdmin = room.adminUserIds.split(",").contains(currentUid)
        if (!isOwner && !isAdmin) return false
        val processedUrl = saveAndCompressImage(newDpUrl, "room_dp_${roomId}")
        val updated = room.copy(coverUrl = processedUrl)
        db.roomDao().insertOrUpdate(updated)
        return true
    }

    suspend fun resetRoomDP(roomId: String): Boolean {
        val room = db.roomDao().getRoomById(roomId) ?: return false
        val currentUid = _currentUserId.value
        val isOwner = room.ownerId == currentUid
        val isAdmin = room.adminUserIds.split(",").contains(currentUid)
        if (!isOwner && !isAdmin) return false
        val defaultCover = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400"
        val updated = room.copy(coverUrl = defaultCover)
        db.roomDao().insertOrUpdate(updated)
        return true
    }

    suspend fun updateRoomWallpaper(roomId: String, wallpaperUrl: String): Boolean {
        val room = db.roomDao().getRoomById(roomId) ?: return false
        val currentUid = _currentUserId.value
        val isOwner = room.ownerId == currentUid
        val isAdmin = room.adminUserIds.split(",").contains(currentUid)
        if (!isOwner && !isAdmin) return false
        val processedUrl = saveAndCompressImage(wallpaperUrl, "room_wp_${roomId}")
        val updated = room.copy(wallpaperUrl = processedUrl)
        db.roomDao().insertOrUpdate(updated)
        return true
    }

    suspend fun updateRoomAnnouncement(roomId: String, announcement: String): Boolean {
        val room = db.roomDao().getRoomById(roomId) ?: return false
        val currentUid = _currentUserId.value
        val isOwner = room.ownerId == currentUid
        val isAdmin = room.adminUserIds.split(",").contains(currentUid)
        if (!isOwner && !isAdmin) return false
        val updated = room.copy(announcement = announcement)
        db.roomDao().insertOrUpdate(updated)
        return true
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

        val config = db.currencyConfigDao().getConfig() ?: CurrencyConfig()

        // 1. Deduct coins from sender, increase rich level points
        db.userDao().updateBalance(user.id, -gift.costCoins, 0)
        val newRichLevel = ((user.richLevel * 1000 + gift.costCoins) / 1000).toInt().coerceAtMost(50)
        db.userDao().updateLevels(user.id, newRichLevel, user.charmLevel)

        // 2. Calculate Diamond Earnings based on Server Config & Commissions
        // Rate: coinsPerUsd (28,000) and diamondsPerUsd (2,800) -> 1 Diamond per 10 Coins gross value
        val grossDiamonds = (gift.costCoins * config.diamondsPerUsd / config.coinsPerUsd.toDouble()).toLong().coerceAtLeast(1L)
        val hostDiamonds = (grossDiamonds * (config.hostGiftCommissionPercent / 100.0)).toLong().coerceAtLeast(1L)
        val agencyDiamonds = (grossDiamonds * (config.agencyGiftCommissionPercent / 100.0)).toLong()

        val targetUser = db.userDao().getUserById(targetUserId)
        if (targetUser != null) {
            // Reward Host with diamonds
            db.userDao().updateBalance(targetUserId, 0, hostDiamonds)
            val newCharmLevel = ((targetUser.charmLevel * 1000 + gift.charmPoints * 100) / 1000).toInt().coerceAtMost(50)
            db.userDao().updateLevels(targetUserId, targetUser.richLevel, newCharmLevel)

            // Reward Agency if host belongs to an agency
            if (!targetUser.agencyId.isNullOrBlank()) {
                val agency = db.agencyFamilyDao().getAgencyById(targetUser.agencyId)
                if (agency != null) {
                    val updatedAgencyIncome = agency.totalIncome + agencyDiamonds
                    db.agencyFamilyDao().updateAgencyIncome(agency.id, updatedAgencyIncome)
                    // If agency owner exists, also credit their agency commission
                    val agencyOwner = db.userDao().getUserById(agency.ownerId)
                    if (agencyOwner != null && agencyDiamonds > 0) {
                        db.userDao().updateBalance(agencyOwner.id, 0, agencyDiamonds)
                        db.walletTransactionDao().insertTransaction(
                            WalletTransaction(
                                id = UUID.randomUUID().toString(),
                                userId = agencyOwner.id,
                                type = "Agency Commission",
                                amountCoins = 0,
                                amountDiamonds = agencyDiamonds,
                                description = "Commission (${config.agencyGiftCommissionPercent.toInt()}%) from gift ${gift.name} sent to host ${targetUser.username}"
                            )
                        )
                    }
                }
            }

            // Record Host incoming transaction
            db.walletTransactionDao().insertTransaction(
                WalletTransaction(
                    id = UUID.randomUUID().toString(),
                    userId = targetUser.id,
                    type = "Gift Received",
                    amountCoins = 0,
                    amountDiamonds = hostDiamonds,
                    description = "Received ${gift.iconEmoji} ${gift.name} from ${user.username} (+${hostDiamonds} 💎)"
                )
            )
        }

        // Record Sender transaction
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

    // Server-Authoritative Wallet operations
    suspend fun rechargeCoins(packageId: String, paymentMethod: String = "Google Play"): Result<Long> {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return Result.failure(Exception("User not logged in"))
        val pkg = db.rechargePackageDao().getPackageById(packageId)
            ?: return Result.failure(Exception("Recharge package not found"))

        val totalCoinsGranted = pkg.coins + pkg.bonusCoins
        db.userDao().updateBalance(user.id, totalCoinsGranted, 0)

        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Recharge",
                amountCoins = totalCoinsGranted,
                description = "Recharge \$${pkg.priceUsd} via $paymentMethod (+${pkg.coins}${if (pkg.bonusCoins > 0) " +${pkg.bonusCoins} Bonus" else ""} Coins)"
            )
        )
        return Result.success(totalCoinsGranted)
    }

    // Direct custom recharge helper
    suspend fun rechargeCustomCoins(coinAmount: Long, priceUsd: String, paymentMethod: String = "In-App Billing"): Boolean {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return false
        if (coinAmount <= 0) return false
        db.userDao().updateBalance(user.id, coinAmount, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Recharge",
                amountCoins = coinAmount,
                description = "Top-up package $priceUsd via $paymentMethod ($coinAmount Coins)"
            )
        )
        return true
    }

    suspend fun exchangeDiamondsToCoins(diamondAmount: Long): Result<Long> {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return Result.failure(Exception("User not logged in"))
        if (user.diamonds < diamondAmount) {
            return Result.failure(Exception("Insufficient diamonds"))
        }
        if (diamondAmount <= 0) {
            return Result.failure(Exception("Amount must be greater than 0"))
        }

        val config = db.currencyConfigDao().getConfig() ?: CurrencyConfig()
        // Rate: 1 USD = 28,000 Coins; 1 USD = 2,800 Diamonds -> 10 Coins per Diamond
        val coinsPerDiamond = (config.coinsPerUsd / config.diamondsPerUsd).coerceAtLeast(1L)
        val gainedCoins = diamondAmount * coinsPerDiamond

        db.userDao().updateBalance(user.id, gainedCoins, -diamondAmount)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Diamond Exchange",
                amountCoins = gainedCoins,
                amountDiamonds = -diamondAmount,
                description = "Exchanged $diamondAmount Diamonds for $gainedCoins Coins (Rate: 1💎 = ${coinsPerDiamond}🪙)"
            )
        )
        return Result.success(gainedCoins)
    }

    suspend fun submitWithdrawalRequest(
        diamondAmount: Long,
        paymentMethod: String,
        accountTitle: String,
        accountNumber: String,
        accountNotes: String = ""
    ): Result<WithdrawalRequest> {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return Result.failure(Exception("User not logged in"))
        val config = db.currencyConfigDao().getConfig() ?: CurrencyConfig()

        if (!config.isWithdrawalEnabled) {
            return Result.failure(Exception("Withdrawals are currently paused for system maintenance."))
        }
        if (diamondAmount < config.minWithdrawalDiamonds) {
            return Result.failure(Exception("Minimum withdrawal is ${config.minWithdrawalDiamonds} Diamonds."))
        }
        if (diamondAmount > config.maxDailyWithdrawalDiamonds) {
            return Result.failure(Exception("Maximum daily withdrawal is ${config.maxDailyWithdrawalDiamonds} Diamonds."))
        }
        if (user.diamonds < diamondAmount) {
            return Result.failure(Exception("Insufficient diamond balance."))
        }

        // Calculate USD value: Diamonds / diamondsPerUsd
        val usdAmount = diamondAmount.toDouble() / config.diamondsPerUsd.toDouble()

        // Deduct diamonds immediately (escrow until processed)
        db.userDao().updateBalance(user.id, 0, -diamondAmount)

        val request = WithdrawalRequest(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            userName = user.username,
            userAvatar = user.avatarUrl,
            diamondAmount = diamondAmount,
            usdAmount = usdAmount,
            paymentMethod = paymentMethod,
            accountTitle = accountTitle,
            accountNumber = accountNumber,
            accountNotes = accountNotes,
            status = "Pending"
        )
        db.withdrawalRequestDao().insertRequest(request)

        val usdFormatted = String.format(java.util.Locale.US, "%.2f", usdAmount)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "Withdrawal Request",
                amountCoins = 0,
                amountDiamonds = -diamondAmount,
                description = "Withdrawal of $diamondAmount Diamonds ($$usdFormatted USD) via $paymentMethod [Status: Pending]"
            )
        )

        return Result.success(request)
    }

    suspend fun getWalletStats(userId: String): Map<String, Long> {
        val totalPurchased = db.walletTransactionDao().getTotalCoinsPurchased(userId)
        val totalSpent = db.walletTransactionDao().getTotalCoinsSpent(userId)
        val totalEarnedDiamonds = db.walletTransactionDao().getTotalDiamondsEarned(userId)
        val totalWithdrawnDiamonds = db.walletTransactionDao().getTotalDiamondsWithdrawnOrExchanged(userId)
        return mapOf(
            "totalPurchased" to totalPurchased,
            "totalSpent" to totalSpent,
            "totalEarnedDiamonds" to totalEarnedDiamonds,
            "totalWithdrawnDiamonds" to totalWithdrawnDiamonds
        )
    }

    fun getUserWithdrawalsFlow(userId: String): Flow<List<WithdrawalRequest>> {
        return db.withdrawalRequestDao().getUserWithdrawalsFlow(userId)
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
        val isFrameCategory = category == "Frames" || category.contains("Frame", ignoreCase = true) ||
                category in listOf("VIP Luxury", "Neon Glow", "Anime Fantasy", "Official & Roles", "Romantic CP", "Headwear")
        if (isFrameCategory) {
            db.userDao().updateEquippedFrame(user.id, itemId)
            db.seatDao().updateUserFrameInSeats(user.id, itemId)
        }
    }

    suspend fun unequipItem(itemId: String, category: String) {
        val user = db.userDao().getUserById(_currentUserId.value) ?: return
        db.storeDao().setEquipped(itemId, false)
        val isFrameCategory = category == "Frames" || category.contains("Frame", ignoreCase = true) ||
                category in listOf("VIP Luxury", "Neon Glow", "Anime Fantasy", "Official & Roles", "Romantic CP", "Headwear")
        if (isFrameCategory) {
            db.userDao().updateEquippedFrame(user.id, null)
            db.seatDao().updateUserFrameInSeats(user.id, null)
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

        getOrCreatePrimaryRoom()
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
            bio = "Hey there! I am using AURA Live Voice Chat ✨",
            country = "🇵🇰 Pakistan",
            language = "English",
            userLevel = 1,
            richLevel = 0,
            charmLevel = 0,
            vipLevel = 0,
            coins = 2000,
            diamonds = 50,
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

        getOrCreatePrimaryRoom()
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

    // ==========================================
    // OWNER PANEL COMPREHENSIVE OPERATIONS
    // ==========================================

    suspend fun getOwnerStats(): OwnerDashboardStats = withContext(Dispatchers.IO) {
        val totalUsers = db.userDao().countTotalUsers().toLong()
        val activeUsers = db.userDao().countActiveUsers().toLong()
        val rooms = db.roomDao().searchRooms("")
        val totalRooms = rooms.size.toLong()
        val activeRooms = rooms.count { it.isActive }.toLong()
        val totalAgencies = db.agencyFamilyDao().getAllAgencies().size.toLong()
        val allRoles = db.userRoleDao().getAllRoles()
        val totalAdmins = allRoles.count { it.role.contains("Admin", ignoreCase = true) }.toLong()
        val totalManagers = allRoles.count { it.role.contains("Manager", ignoreCase = true) }.toLong()
        val totalHosts = db.adminLinkUserDao().countLinkUsers("565656565666555").toLong().coerceAtLeast(6L)
        val allUsers = db.userDao().getAllUsers()
        val totalCoins = allUsers.sumOf { it.coins }
        val pendingWithdrawals = db.withdrawalRequestDao().getWithdrawalsByStatus("Pending").size
        val pendingReports = db.reportDao().countPendingReports()

        OwnerDashboardStats(
            totalUsers = totalUsers.coerceAtLeast(12L),
            onlineUsers = (activeUsers / 2).coerceAtLeast(4L),
            totalRooms = totalRooms.coerceAtLeast(4L),
            activeRooms = activeRooms.coerceAtLeast(2L),
            totalAgencies = totalAgencies.coerceAtLeast(3L),
            totalHosts = totalHosts,
            totalAdmins = totalAdmins.coerceAtLeast(2L),
            totalManagers = totalManagers.coerceAtLeast(3L),
            totalCoinsInCirculation = totalCoins.coerceAtLeast(1580000L),
            totalRevenueUsd = 12450.0 + (totalCoins / 28000.0),
            pendingWithdrawalsCount = pendingWithdrawals,
            pendingReportsCount = pendingReports,
            dailyNewUsers = 18,
            weeklyActiveUsers = 46,
            monthlyRevenueUsd = 3450.0
        )
    }

    suspend fun sendOrAssignManager(
        targetUserId: String,
        managerRoleTitle: String,
        permissions: List<String>,
        status: String = "Active",
        notes: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(targetUserId.trim())
            ?: return@withContext Result.failure(Exception("User with ID '$targetUserId' was not found."))

        val existingRole = db.userRoleDao().getRoleForUser(user.id)
        val permString = permissions.joinToString(",")

        val assignment = UserRoleAssignment(
            userId = user.id,
            username = user.username,
            role = managerRoleTitle.ifBlank { "Manager" },
            assignedBy = _currentUserId.value.ifBlank { "Owner" },
            assignedByName = "AURA Owner",
            assignedAt = System.currentTimeMillis(),
            permissions = permString,
            assignedArea = "All Rooms & Operations",
            notes = "$status | $notes"
        )
        db.userRoleDao().insertOrUpdateRole(assignment)

        // Notification to user
        db.notificationDao().insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = "role_update",
                title = "Official Role Assigned: $managerRoleTitle",
                message = "The Owner has appointed you as $managerRoleTitle with authorized moderation privileges.",
                senderName = "AURA Live Owner Office 👑",
                timestamp = System.currentTimeMillis()
            )
        )

        // Audit Log
        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "ASSIGN_MANAGER_ROLE",
                targetType = "User",
                targetId = user.id,
                targetName = user.username,
                previousValue = existingRole?.role ?: "User",
                newValue = "$managerRoleTitle ($status) [Perms: $permString]",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Manager role '$managerRoleTitle' successfully assigned to ${user.username} (${user.id})!")
    }

    suspend fun removeManager(targetUserId: String, reason: String = ""): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(targetUserId.trim())
        val existingRole = db.userRoleDao().getRoleForUser(targetUserId)
            ?: return@withContext Result.failure(Exception("No assigned role found for user $targetUserId."))

        db.userRoleDao().removeRoleForUser(targetUserId)

        db.notificationDao().insertNotification(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                userId = targetUserId,
                type = "role_update",
                title = "Manager Role Revoked",
                message = "Your managerial assignment has been revoked by the Owner. Reason: ${reason.ifBlank { "Administrative decision" }}",
                senderName = "AURA Live Owner Office 👑",
                timestamp = System.currentTimeMillis()
            )
        )

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "REMOVE_MANAGER_ROLE",
                targetType = "User",
                targetId = targetUserId,
                targetName = user?.username ?: targetUserId,
                previousValue = existingRole.role,
                newValue = "User (Removed. Reason: $reason)",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Manager role removed for user $targetUserId.")
    }

    suspend fun addOrUpdateAdmin(
        targetUserId: String,
        adminRoleTitle: String,
        permissions: List<String>,
        notes: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(targetUserId.trim())
            ?: return@withContext Result.failure(Exception("User with ID '$targetUserId' not found."))

        val existingRole = db.userRoleDao().getRoleForUser(user.id)
        val permString = permissions.joinToString(",")

        val assignment = UserRoleAssignment(
            userId = user.id,
            username = user.username,
            role = adminRoleTitle.ifBlank { "Admin" },
            assignedBy = _currentUserId.value.ifBlank { "Owner" },
            assignedByName = "AURA Owner",
            assignedAt = System.currentTimeMillis(),
            permissions = permString,
            assignedArea = "Platform Wide",
            notes = notes
        )
        db.userRoleDao().insertOrUpdateRole(assignment)

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "ASSIGN_ADMIN_ROLE",
                targetType = "User",
                targetId = user.id,
                targetName = user.username,
                previousValue = existingRole?.role ?: "User",
                newValue = "$adminRoleTitle [Perms: $permString]",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Admin role '$adminRoleTitle' granted to ${user.username}!")
    }

    suspend fun adjustCoinsOwner(
        targetUserId: String,
        deltaCoins: Long,
        reason: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (reason.isBlank()) {
            return@withContext Result.failure(Exception("An explicit Owner authorization reason is mandatory."))
        }
        val user = db.userDao().getUserById(targetUserId.trim())
            ?: return@withContext Result.failure(Exception("User not found."))

        db.userDao().updateBalance(user.id, deltaCoins, 0)
        db.walletTransactionDao().insertTransaction(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                type = if (deltaCoins >= 0) "Owner Credit" else "Owner Debit",
                amountCoins = deltaCoins,
                amountDiamonds = 0,
                description = "Owner Adjustment: $reason"
            )
        )

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "OWNER_COIN_ADJUSTMENT",
                targetType = "Wallet",
                targetId = user.id,
                targetName = user.username,
                previousValue = "${user.coins} Coins",
                newValue = "${user.coins + deltaCoins} Coins (${if (deltaCoins > 0) "+$deltaCoins" else "$deltaCoins"}). Note: $reason",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Updated balance for ${user.username}: ${if (deltaCoins > 0) "+$deltaCoins" else "$deltaCoins"} Coins.")
    }

    suspend fun toggleUserBanOwner(
        targetUserId: String,
        isBanned: Boolean,
        reason: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(targetUserId.trim())
            ?: return@withContext Result.failure(Exception("User not found."))

        db.userDao().updateBanStatus(user.id, isBanned)
        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = if (isBanned) "BAN_USER" else "UNBAN_USER",
                targetType = "User",
                targetId = user.id,
                targetName = user.username,
                previousValue = "isBanned=${user.isBanned}",
                newValue = "isBanned=$isBanned. Reason: ${reason.ifBlank { "Owner Directive" }}",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(if (isBanned) "User ${user.username} has been BANNED." else "User ${user.username} UNBANNED.")
    }

    suspend fun assignOfficialFrameOwner(
        targetUserId: String,
        frameId: String,
        days: Int = 30
    ): Result<String> = withContext(Dispatchers.IO) {
        val user = db.userDao().getUserById(targetUserId.trim())
            ?: return@withContext Result.failure(Exception("User not found."))

        val frameDef = AdminService.OFFICIAL_FRAMES.find { it.id == frameId }
            ?: return@withContext Result.failure(Exception("Frame $frameId not found."))

        val now = System.currentTimeMillis()
        val expiry = now + (days.toLong() * 86400000L)
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)

        val assignment = OfficialFrameAssignment(
            id = "frame_assign_${UUID.randomUUID().toString().take(8)}",
            userId = user.id,
            userName = user.username,
            frameId = frameDef.id,
            frameName = frameDef.name,
            days = days,
            sendDate = now,
            expiryDate = expiry,
            status = "Active",
            adminId = _currentUserId.value.ifBlank { "Owner" },
            adminName = "AURA Owner",
            sendDateFormatted = dateFormat.format(java.util.Date(now)),
            expiryDateFormatted = dateFormat.format(java.util.Date(expiry))
        )
        db.officialFrameDao().insertAssignment(assignment)
        db.userDao().updateEquippedFrame(user.id, frameDef.id)

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "ASSIGN_OFFICIAL_FRAME",
                targetType = "Frame",
                targetId = user.id,
                targetName = "${user.username} (${frameDef.name})",
                previousValue = user.equippedFrameId,
                newValue = "${frameDef.name} for $days days",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Assigned ${frameDef.name} Badge & Frame to ${user.username} for $days days!")
    }

    suspend fun revokeOfficialFrameOwner(assignmentId: String): Result<String> = withContext(Dispatchers.IO) {
        val assignment = db.officialFrameDao().getAssignmentById(assignmentId)
            ?: return@withContext Result.failure(Exception("Assignment not found."))

        db.officialFrameDao().updateStatus(assignmentId, "Revoked")
        val user = db.userDao().getUserById(assignment.userId)
        if (user != null && user.equippedFrameId == assignment.frameId) {
            db.userDao().updateEquippedFrame(user.id, null)
        }

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "REVOKE_OFFICIAL_FRAME",
                targetType = "Frame",
                targetId = assignment.userId,
                targetName = "${assignment.userName} (${assignment.frameName})",
                previousValue = "Active",
                newValue = "Revoked",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Revoked ${assignment.frameName} from ${assignment.userName}.")
    }

    suspend fun createVoiceRoomOwner(
        title: String,
        ownerId: String,
        category: String,
        seatCount: Int = 8
    ): Result<VoiceRoom> = withContext(Dispatchers.IO) {
        val owner = db.userDao().getUserById(ownerId.trim())
            ?: return@withContext Result.failure(Exception("Owner user ID not found."))

        val newRoom = VoiceRoom(
            id = "room_${UUID.randomUUID().toString().take(8)}",
            title = title.ifBlank { "${owner.username}'s VIP Lounge" },
            description = "Official Room managed by AURA Operations",
            coverUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300",
            ownerId = owner.id,
            ownerName = owner.username,
            ownerAvatar = owner.avatarUrl,
            seatCount = seatCount,
            onlineCount = 1,
            isLocked = false,
            category = category.ifBlank { "Chat & Music" },
            announcement = "Welcome to our room! Follow rules and have fun.",
            isActive = true,
            createdAt = System.currentTimeMillis()
        )
        db.roomDao().insertOrUpdate(newRoom)

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "CREATE_ROOM_OWNER",
                targetType = "Room",
                targetId = newRoom.id,
                targetName = newRoom.title,
                previousValue = null,
                newValue = "Created with $seatCount seats, Category: $category",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(newRoom)
    }

    suspend fun deleteVoiceRoomOwner(roomId: String, reason: String): Result<String> = withContext(Dispatchers.IO) {
        val room = db.roomDao().getRoomById(roomId)
            ?: return@withContext Result.failure(Exception("Room not found."))

        db.roomDao().closeRoom(roomId)
        db.seatDao().clearSeatsForRoom(roomId)

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "DELETE_ROOM_OWNER",
                targetType = "Room",
                targetId = roomId,
                targetName = room.title,
                previousValue = "isActive=${room.isActive}",
                newValue = "Closed/Deleted. Reason: $reason",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Room '${room.title}' has been deleted/closed.")
    }

    suspend fun createAgencyOwner(
        name: String,
        code: String,
        ownerId: String,
        announcement: String
    ): Result<Agency> = withContext(Dispatchers.IO) {
        val owner = db.userDao().getUserById(ownerId.trim())
            ?: return@withContext Result.failure(Exception("Agency Leader User ID not found."))

        val newAgency = Agency(
            id = "agency_${UUID.randomUUID().toString().take(6)}",
            name = name.trim(),
            logoUrl = "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=200",
            ownerId = owner.id,
            ownerName = owner.username,
            agencyCode = code.ifBlank { "AG${Random.nextInt(1000, 9999)}" },
            bdId = "BD_OFFICIAL",
            announcement = announcement.ifBlank { "Welcome to $name Agency!" },
            memberCount = 1,
            totalIncome = 0,
            level = 1
        )
        db.agencyFamilyDao().insertAgency(newAgency)
        db.userDao().insertOrUpdate(owner.copy(agencyId = newAgency.id))

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "CREATE_AGENCY_OWNER",
                targetType = "Agency",
                targetId = newAgency.id,
                targetName = newAgency.name,
                previousValue = null,
                newValue = "Code: ${newAgency.agencyCode}, Leader: ${owner.username}",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(newAgency)
    }

    suspend fun deleteAgencyOwner(agencyId: String, reason: String): Result<String> = withContext(Dispatchers.IO) {
        val agency = db.agencyFamilyDao().getAgencyById(agencyId)
            ?: return@withContext Result.failure(Exception("Agency not found."))

        db.agencyFamilyDao().deleteAgencyById(agencyId)

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "DELETE_AGENCY_OWNER",
                targetType = "Agency",
                targetId = agencyId,
                targetName = agency.name,
                previousValue = "Members: ${agency.memberCount}",
                newValue = "Deleted. Reason: $reason",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Agency '${agency.name}' deleted.")
    }

    suspend fun broadcastAnnouncementOwner(
        title: String,
        message: String,
        targetAudience: String = "All Users"
    ): Result<String> = withContext(Dispatchers.IO) {
        val users = when (targetAudience) {
            "Managers & Admins" -> {
                val staffIds = db.userRoleDao().getAllRoles().map { it.userId }.toSet()
                db.userDao().getAllUsers().filter { staffIds.contains(it.id) }
            }
            "Agencies & Hosts" -> {
                db.userDao().getAllUsers().filter { !it.agencyId.isNullOrBlank() }
            }
            else -> db.userDao().getAllUsers()
        }

        users.forEach { u ->
            db.notificationDao().insertNotification(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    userId = u.id,
                    type = "announcement",
                    title = "📢 $title",
                    message = message,
                    senderName = "AURA Live Official Announcement 👑",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "BROADCAST_ANNOUNCEMENT",
                targetType = "Notification",
                targetId = targetAudience,
                targetName = title,
                previousValue = null,
                newValue = "Sent to ${users.size} users. Message: $message",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Broadcast delivered to ${users.size} recipients ($targetAudience)!")
    }

    suspend fun resolveReportOwner(reportId: String, status: String, notes: String): Result<String> = withContext(Dispatchers.IO) {
        db.reportDao().resolveReport(
            reportId = reportId,
            status = status,
            resolvedAt = System.currentTimeMillis(),
            resolvedBy = "AURA Live Owner 👑",
            notes = notes
        )

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "RESOLVE_REPORT_OWNER",
                targetType = "Report",
                targetId = reportId,
                targetName = "Report $reportId",
                previousValue = "Pending",
                newValue = "$status. Notes: $notes",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Report updated to $status.")
    }

    suspend fun handleWithdrawalOwner(requestId: String, action: String, notes: String): Result<String> = withContext(Dispatchers.IO) {
        val req = db.withdrawalRequestDao().getWithdrawalById(requestId)
            ?: return@withContext Result.failure(Exception("Withdrawal not found."))

        val newStatus = when (action.uppercase()) {
            "APPROVE" -> "Approved"
            "REJECT" -> "Rejected"
            else -> "Under Review"
        }

        if (newStatus == "Rejected" && req.status != "Rejected") {
            db.userDao().updateBalance(req.userId, 0, req.diamondAmount)
            db.walletTransactionDao().insertTransaction(
                WalletTransaction(
                    id = UUID.randomUUID().toString(),
                    userId = req.userId,
                    type = "Withdrawal Refund",
                    amountCoins = 0,
                    amountDiamonds = req.diamondAmount,
                    description = "Refund: Withdrawal of ${req.diamondAmount} 💎 rejected by Owner. $notes"
                )
            )
        }

        db.withdrawalRequestDao().updateWithdrawalStatus(
            id = req.id,
            status = newStatus,
            notes = notes,
            admin = "AURA Owner 👑",
            timestamp = System.currentTimeMillis()
        )

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "WITHDRAWAL_OWNER_$action",
                targetType = "Withdrawal",
                targetId = req.id,
                targetName = "${req.userName} (${req.diamondAmount} 💎)",
                previousValue = req.status,
                newValue = "$newStatus. Notes: $notes",
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Withdrawal #$requestId marked as $newStatus.")
    }

    suspend fun updateAppConfigOwner(key: String, value: String, category: String = "general"): Result<String> = withContext(Dispatchers.IO) {
        val prev = db.appConfigDao().getConfigByKey(key)
        db.appConfigDao().insertOrUpdateConfig(
            AppConfigEntity(
                key = key,
                value = value,
                category = category,
                updatedAt = System.currentTimeMillis(),
                updatedBy = "AURA Owner"
            )
        )

        db.auditLogDao().insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminId = _currentUserId.value.ifBlank { "Owner" },
                adminName = "Owner",
                adminRole = "Super Admin / Owner",
                action = "UPDATE_CONFIG_OWNER",
                targetType = "Config",
                targetId = key,
                targetName = key,
                previousValue = prev?.value,
                newValue = value,
                isSuccess = true,
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Setting '$key' updated to '$value'.")
    }
}
