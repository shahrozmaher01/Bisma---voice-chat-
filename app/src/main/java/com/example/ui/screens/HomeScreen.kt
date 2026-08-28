package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.User
import com.example.data.model.VoiceRoom
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class HomeMainTab {
    PARTY,
    MINE,
    RANKINGS
}

enum class MineSubTab {
    FOLLOWING,
    MY_ROOM
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: BismaRepository,
    onOpenRoom: (String) -> Unit,
    onOpenRankings: (Int) -> Unit, // 0: Wealth, 1: Charm, 2: Room
    onOpenNotifications: () -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allRooms by repository.activeRooms.collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val followingList by repository.getFriends().collectAsState(initial = emptyList())

    var currentTab by remember { mutableStateOf(HomeMainTab.PARTY) }
    var currentMineSubTab by remember { mutableStateOf(MineSubTab.FOLLOWING) }
    var selectedCountry by remember { mutableStateOf("🔥 All") }
    var isRefreshing by remember { mutableStateOf(false) }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showCreateRoomDialog by remember { mutableStateOf(false) }

    val countries = listOf("🔥 All", "🇵🇰 Pakistan", "🇮🇳 India", "🇧🇩 Bangladesh", "🇳🇵 Nepal", "🇸🇦 Saudi", "🇦🇪 UAE", "🌐 Global")

    // Filter rooms based on rule: Active rooms must have onlineCount > 0
    val displayedPartyRooms = remember(allRooms, selectedCountry) {
        var list = allRooms.filter { it.onlineCount > 0 }
        if (selectedCountry != "🔥 All" && selectedCountry != "🌐 Global") {
            val countryKey = selectedCountry.replace(Regex("[^A-Za-z]"), "").trim()
            list = list.filter { it.country.contains(countryKey, ignoreCase = true) || it.country == selectedCountry }
        }
        list.sortedByDescending { it.onlineCount }
    }

    val followingRooms = remember(allRooms, followingList) {
        val followingIds = followingList.map { it.friendId }.toSet()
        allRooms.filter { it.onlineCount > 0 && (it.ownerId in followingIds || it.isFeatured) }
    }

    val myRooms = remember(allRooms, currentUser) {
        allRooms.filter { it.ownerId == currentUser?.id }
    }

    val totalActiveListeners = remember(allRooms) {
        allRooms.sumOf { it.onlineCount }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Stylish Compact Header (Logo + "Bisma", Live Counter Badge, Search, Notifications, + Room)
            BismaCompactHeader(
                totalListeners = totalActiveListeners,
                onSearchClick = { showSearchDialog = true },
                onNotificationClick = onOpenNotifications,
                onCreateRoomClick = { showCreateRoomDialog = true }
            )

            // Primary Navigation Tabs: Party | Mine | Rankings
            BismaMainNavigationTabs(
                selectedTab = currentTab,
                onTabSelected = { tab ->
                    if (tab == HomeMainTab.RANKINGS) {
                        onOpenRankings(0)
                    } else {
                        currentTab = tab
                    }
                }
            )

            // Pull to refresh wrapper
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        repository.refreshLiveListenerCounts()
                        delay(600)
                        isRefreshing = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when (currentTab) {
                    HomeMainTab.PARTY -> {
                        PartyTabContent(
                            countries = countries,
                            selectedCountry = selectedCountry,
                            onCountrySelected = { selectedCountry = it },
                            rooms = displayedPartyRooms,
                            onOpenRoom = onOpenRoom,
                            onCreateRoomClick = { showCreateRoomDialog = true }
                        )
                    }
                    HomeMainTab.MINE -> {
                        MineTabContent(
                            subTab = currentMineSubTab,
                            onSubTabSelected = { currentMineSubTab = it },
                            followingRooms = followingRooms,
                            myRooms = myRooms,
                            onOpenRoom = onOpenRoom,
                            onCreateRoomClick = { showCreateRoomDialog = true }
                        )
                    }
                    HomeMainTab.RANKINGS -> {
                        // Handled via navigation
                    }
                }
            }
        }

        // Search Dialog
        if (showSearchDialog) {
            SearchDialog(
                repository = repository,
                onDismiss = { showSearchDialog = false },
                onSelectUser = { userId ->
                    showSearchDialog = false
                    onOpenUserProfile(userId)
                },
                onSelectRoom = { roomId ->
                    showSearchDialog = false
                    onOpenRoom(roomId)
                }
            )
        }

        // Create Room Dialog
        if (showCreateRoomDialog) {
            CreateRoomDialog(
                repository = repository,
                onDismiss = { showCreateRoomDialog = false },
                onRoomCreated = { newRoomId ->
                    showCreateRoomDialog = false
                    onOpenRoom(newRoomId)
                }
            )
        }
    }
}

@Composable
fun BismaCompactHeader(
    totalListeners: Int,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCreateRoomClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Combined App Icon + "Bisma" Title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(NeonPink, ElectricBlue)))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bisma_logo),
                    contentDescription = "Bisma Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Bisma",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Live indicator
            Surface(
                color = Color(0x2200E676),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0x5500E676))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalListeners",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E676)
                    )
                }
            }
        }

        // Header Actions: Search, Notifications, + Room
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF))
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Button(
                onClick = onCreateRoomClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(36.dp)
                    .background(PrimaryGradient, RoundedCornerShape(18.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Room", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun BismaMainNavigationTabs(
    selectedTab: HomeMainTab,
    onTabSelected: (HomeMainTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val tabs = listOf(
            HomeMainTab.PARTY to "Party",
            HomeMainTab.MINE to "Mine",
            HomeMainTab.RANKINGS to "Rankings 🏆"
        )

        tabs.forEach { (tab, label) ->
            val isSelected = selectedTab == tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(tab) }
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = label,
                    fontSize = if (isSelected) 17.sp else 15.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(PrimaryGradient)
                    )
                } else {
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}

@Composable
fun PartyTabContent(
    countries: List<String>,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    rooms: List<VoiceRoom>,
    onOpenRoom: (String) -> Unit,
    onCreateRoomClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Country Selector Pills
        item(span = { GridItemSpan(2) }) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(countries) { country ->
                    val isSelected = selectedCountry == country
                    Surface(
                        color = if (isSelected) NeonPink else SurfaceCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isSelected) NeonPink else SurfaceCardBorder),
                        modifier = Modifier.clickable { onCountrySelected(country) }
                    ) {
                        Text(
                            text = country,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (rooms.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                EmptyRoomsCard(
                    message = "No active voice rooms online right now in $selectedCountry.",
                    onCreateRoomClick = onCreateRoomClick
                )
            }
        } else {
            items(rooms, key = { it.id }) { room ->
                SquareRoomCard(
                    room = room,
                    onClick = { onOpenRoom(room.id) }
                )
            }
        }
    }
}

@Composable
fun MineTabContent(
    subTab: MineSubTab,
    onSubTabSelected: (MineSubTab) -> Unit,
    followingRooms: List<VoiceRoom>,
    myRooms: List<VoiceRoom>,
    onOpenRoom: (String) -> Unit,
    onCreateRoomClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Sub-tabs: Following | My Room
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = if (subTab == MineSubTab.FOLLOWING) NeonPink else SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (subTab == MineSubTab.FOLLOWING) NeonPink else SurfaceCardBorder),
                modifier = Modifier.clickable { onSubTabSelected(MineSubTab.FOLLOWING) }
            ) {
                Text(
                    text = "⭐ Following (${followingRooms.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (subTab == MineSubTab.FOLLOWING) Color.White else TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Surface(
                color = if (subTab == MineSubTab.MY_ROOM) NeonPink else SurfaceCard,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (subTab == MineSubTab.MY_ROOM) NeonPink else SurfaceCardBorder),
                modifier = Modifier.clickable { onSubTabSelected(MineSubTab.MY_ROOM) }
            ) {
                Text(
                    text = "🎧 My Room (${myRooms.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (subTab == MineSubTab.MY_ROOM) Color.White else TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        when (subTab) {
            MineSubTab.FOLLOWING -> {
                if (followingRooms.isEmpty()) {
                    EmptyRoomsCard(
                        message = "No followed creators are currently live.",
                        onCreateRoomClick = onCreateRoomClick
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(followingRooms, key = { it.id }) { room ->
                            SquareRoomCard(
                                room = room,
                                onClick = { onOpenRoom(room.id) }
                            )
                        }
                    }
                }
            }
            MineSubTab.MY_ROOM -> {
                if (myRooms.isEmpty()) {
                    EmptyRoomsCard(
                        message = "You haven't created your own Voice Room yet.",
                        onCreateRoomClick = onCreateRoomClick
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(myRooms, key = { it.id }) { room ->
                            SquareRoomCard(
                                room = room,
                                onClick = { onOpenRoom(room.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 4:4 (1:1 square) room card.
 * Shows ONLY:
 * 1. Room DP (Cover)
 * 2. Room Name
 * 3. Country Flag
 * 4. Current Online-User Count
 */
@Composable
fun SquareRoomCard(
    room: VoiceRoom,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 1:1 format
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Room DP / Cover image (fills square)
        AsyncImage(
            model = room.coverUrl,
            contentDescription = room.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Scrim for crisp readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x55000000),
                            Color(0x11000000),
                            Color(0xCC000000),
                            Color(0xF00F081D)
                        )
                    )
                )
        )

        // Top badges: Country Flag & Online User Count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Country flag badge
            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = room.country.split(" ").firstOrNull() ?: "🌍",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            // Online user count badge
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.onlineCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Bottom: Room Name
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = room.title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyRoomsCard(
    message: String,
    onCreateRoomClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MicOff,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonButton(
                text = "+ Launch Voice Room",
                onClick = onCreateRoomClick,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}

@Composable
fun SearchDialog(
    repository: BismaRepository,
    onDismiss: () -> Unit,
    onSelectUser: (String) -> Unit,
    onSelectRoom: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var userResults by remember { mutableStateOf<List<User>>(emptyList()) }
    var roomResults by remember { mutableStateOf<List<VoiceRoom>>(emptyList()) }

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            userResults = repository.searchUsers(query)
            roomResults = repository.searchRooms(query)
        } else {
            userResults = emptyList()
            roomResults = emptyList()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Search Bisma", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search by User ID, Name, or Room ID") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = NeonPink)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (userResults.isNotEmpty()) {
                        Text("Users", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        userResults.forEach { u ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCard)
                                    .clickable { onSelectUser(u.id) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = u.avatarUrl,
                                    contentDescription = u.username,
                                    modifier = Modifier.size(36.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(u.username, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("ID: ${u.id} • ${u.country}", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    if (roomResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rooms", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        roomResults.forEach { r ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCard)
                                    .clickable { onSelectRoom(r.id) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = r.coverUrl,
                                    contentDescription = r.title,
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(r.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text("ID: ${r.id} • ${r.onlineCount} Online", color = TextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NeonPink)
            }
        }
    )
}

@Composable
fun CreateRoomDialog(
    repository: BismaRepository,
    onDismiss: () -> Unit,
    onRoomCreated: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var roomTitle by remember { mutableStateOf("") }
    var roomDescription by remember { mutableStateOf("") }
    var selectedSeats by remember { mutableIntStateOf(8) }
    var selectedCountry by remember { mutableStateOf("🇵🇰 Pakistan") }
    var isLocked by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    val seatOptions = listOf(4, 6, 8, 10, 12, 15, 20)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Create Voice Chat Room", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = roomTitle,
                    onValueChange = { roomTitle = it },
                    label = { Text("Room Title") },
                    placeholder = { Text("e.g. Acoustic Jam & Poetry Lounge") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = roomDescription,
                    onValueChange = { roomDescription = it },
                    label = { Text("Room Announcement") },
                    placeholder = { Text("Welcome rules & vibe...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Text("Select Seat Layout", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    seatOptions.forEach { seats ->
                        FilterChip(
                            selected = selectedSeats == seats,
                            onClick = { selectedSeats = seats },
                            label = { Text("$seats Seats") }
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isLocked,
                        onCheckedChange = { isLocked = it },
                        colors = CheckboxDefaults.colors(checkedColor = NeonPink)
                    )
                    Text("Password Protected Room", color = TextPrimary, fontSize = 13.sp)
                }

                if (isLocked) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Room Password") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "Launch Room",
                onClick = {
                    coroutineScope.launch {
                        val newId = repository.createRoom(
                            title = roomTitle,
                            description = roomDescription,
                            coverUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
                            seatCount = selectedSeats,
                            country = selectedCountry,
                            category = "Voice Lounge",
                            isLocked = isLocked,
                            password = password
                        )
                        onRoomCreated(newId)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
