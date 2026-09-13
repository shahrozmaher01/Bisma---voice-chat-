package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.BismaRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthAndPermanentRoomTest {

    private lateinit var context: Context
    private lateinit var repository: BismaRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        repository = BismaRepository(context)
    }

    @Test
    fun testGoogleSignInCreatesPermanentIdentityAndRoom() = runBlocking {
        val email = "alex.rivera@aura.live"
        val name = "Alex Rivera"
        val avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"

        // First login with Google
        val loginResult = repository.loginWithGoogle(
            email = email,
            displayName = name,
            avatarUrl = avatar
        )

        assertTrue(loginResult.isSuccess)
        val user1 = loginResult.getOrNull()
        assertNotNull(user1)
        val permanentUserId = user1!!.id
        assertTrue(permanentUserId.isNotEmpty())

        // Verify permanent room was automatically provisioned
        val room1 = repository.getMyCreatedRoomFlow().filterNotNull().first()
        val permanentRoomId = room1.id
        assertEquals(permanentUserId, room1.ownerId)

        // Simulate Logout
        repository.logout()
        val currentUserAfterLogout = repository.currentUser.first()
        assertNull(currentUserAfterLogout)

        // Verify user and room still exist in the database (Logout never deletes data)
        val storedUser = repository.db.userDao().getUserById(permanentUserId)
        assertNotNull(storedUser)
        val storedRoom = repository.db.roomDao().getRoomById(permanentRoomId)
        assertNotNull(storedRoom)

        // Simulate Logging back in with the same Google account
        val secondLoginResult = repository.loginWithGoogle(
            email = email,
            displayName = name,
            avatarUrl = avatar
        )
        assertTrue(secondLoginResult.isSuccess)
        val user2 = secondLoginResult.getOrNull()
        assertNotNull(user2)

        // Crucial Check: User ID must remain identical (One Google Identity -> One permanent User ID)
        assertEquals(permanentUserId, user2!!.id)

        // Crucial Check: Room ID must remain identical (One User ID -> One permanent Room ID)
        val room2 = repository.getMyCreatedRoomFlow().filterNotNull().first()
        assertEquals(permanentRoomId, room2.id)
    }

    @Test
    fun testDuplicateRoomCreationIsPrevented() = runBlocking {
        val email = "creator.sarah@aura.live"
        val avatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200"
        val loginResult = repository.loginWithGoogle(email = email, displayName = "Sarah", avatarUrl = avatar)
        assertTrue(loginResult.isSuccess)
        val user = loginResult.getOrNull()!!

        val initialRoom = repository.getMyCreatedRoomFlow().filterNotNull().first()

        // Attempt to create another room with createRoom
        val attemptedRoomId = repository.createRoom(
            title = "Sarah's Second Room",
            description = "Trying to create duplicate",
            coverUrl = avatar,
            seatCount = 10,
            country = "Global",
            category = "Music",
            isLocked = false,
            password = ""
        )

        // Must return the existing permanent room ID rather than creating a duplicate
        assertEquals(initialRoom.id, attemptedRoomId)

        // Verify that the database only has one room for this owner
        val ownerRooms = repository.db.roomDao().searchRooms("").filter { it.ownerId == user.id }
        assertEquals(1, ownerRooms.size)
    }

    @Test
    fun testRoomSettingsAndDPAreUpdatedWithoutDuplicate() = runBlocking {
        val email = "host.sherry@aura.live"
        val avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200"
        val loginResult = repository.loginWithGoogle(email = email, displayName = "Sherry Host", avatarUrl = avatar)
        assertTrue(loginResult.isSuccess)

        val room = repository.getMyCreatedRoomFlow().filterNotNull().first()
        val roomId = room.id

        // Update Room DP
        val newDpUrl = "content://media/external/images/media/12345"
        val dpUpdateSuccess = repository.updateRoomDP(roomId, newDpUrl)
        assertTrue(dpUpdateSuccess)

        // Update Room Settings
        val settingsResult = repository.updateRoomSettings(
            roomId = roomId,
            title = "Sherry's Cosmic VIP Lounge",
            description = "Nightly acoustic music & chats",
            announcement = "Please be respectful to speakers on mic.",
            rules = "1. No toxicity. 2. 3 minutes mic turn.",
            seatCount = 12,
            isLocked = false,
            password = ""
        )
        assertTrue(settingsResult.isSuccess)

        // Verify changes in database
        val updatedRoom = repository.db.roomDao().getRoomById(roomId)!!
        assertEquals("Sherry's Cosmic VIP Lounge", updatedRoom.title)
        assertEquals("Nightly acoustic music & chats", updatedRoom.description)
        assertEquals("Please be respectful to speakers on mic.", updatedRoom.announcement)
        assertEquals("1. No toxicity. 2. 3 minutes mic turn.", updatedRoom.rules)
        assertEquals(12, updatedRoom.seatCount)
        assertEquals(newDpUrl, updatedRoom.coverUrl)
    }
}
