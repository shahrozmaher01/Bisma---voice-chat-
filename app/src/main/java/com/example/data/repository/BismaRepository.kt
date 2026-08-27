package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
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
    private val db = BismaDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs: SharedPreferences = context.getSharedPreferences("bisma_auth_prefs", Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("KEY_IS_LOGGED_IN", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserId = MutableStateFlow(prefs.getString("KEY_CURRENT_USER_ID", "") ?: "")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    val currentUser: Flow<User?> = _currentUserId.flatMapLatest { id ->
        if (id.isBlank()) flowOf(null) else db.userDao().getUserByIdFlow(id)
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

        // Seed store catalog items (shop items only, not owned until purchased)
        val existingStore = db.storeDao().getAllStoreItemsFlow().firstOrNull() ?: emptyList()
        if (existingStore.isEmpty()) {
            val items = listOf(
                StoreItem("frame_vip_neon", "Neon Pink Crown Frame", "Frames", 1200, "👑", 0xFFFF2A85, isPermanent = true, isOwned = false, isEquipped = false),
                StoreItem("frame_galaxy_gold", "Galaxy Gold Ring", "Frames", 2500, "🪐", 0xFFFFD700, isPermanent = true, isOwned = false, isEquipped = false),
                StoreItem("frame_cyber_blue", "Cyberpunk Hologram", "Frames", 1800, "⚡", 0xFF00E5FF, isPermanent = true, isOwned = false, isEquipped = false),
                StoreItem("frame_heart_romance", "Romantic Rose CP Frame", "Frames", 3000, "💖", 0xFFFF4081, isPermanent = true, isOwned = false, isEquipped = false),
                StoreItem("head_angel_wings", "Angel Wings Halo", "Headwear", 1500, "🪽", 0xFFFFFFFF, isOwned = false, isEquipped = false),
                StoreItem("head_devil_horns", "Neon Devil Horns", "Headwear", 1400, "😈", 0xFFFF1744, isOwned = false, isEquipped = false),
                StoreItem("entry_supercar", "Lamborghini Entry Effect", "Entry Effects", 5000, "🏎️", 0xFFFFD700, isOwned = false, isEquipped = false),
                StoreItem("entry_dragon", "Phoenix Flame Entry", "Entry Effects", 8000, "🔥", 0xFFFF8800, isOwned = false, isEquipped = false),
                StoreItem("bubble_neon_glow", "Pink Neon Chat Bubble", "Chat Bubbles", 800, "💬", 0xFFFF2A85, isOwned = false, isEquipped = false),
                StoreItem("sound_laser_wave", "Laser Sound Waves", "Sound Waves", 1000, "🌊", 0xFF00E5FF, isOwned = false, isEquipped = false)
            )
            db.storeDao().insertAll(items)
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

    fun getFollowersCountFlow(userId: String): Flow<Int> = db.socialDao().getFollowersCountFlow(userId)
    fun getFollowingCountFlow(userId: String): Flow<Int> = db.socialDao().getFollowingCountFlow(userId)
    fun getFriendsCountFlow(userId: String): Flow<Int> = db.socialDao().getFriendsCountFlow(userId)
    fun getVisitorsCountFlow(userId: String): Flow<Int> = db.visitorDao().getVisitorsCountFlow(userId)

    suspend fun refreshLiveListenerCounts() {
        val rooms = db.roomDao().getAllActiveRoomsFlow().firstOrNull() ?: return
        rooms.forEach { room ->
            val seats = db.seatDao().getSeatsForRoomFlow(room.id).firstOrNull() ?: emptyList()
            val activeOccupants = seats.count { it.userId != null }
            val realCount = if (activeOccupants > 0) activeOccupants else 1
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
