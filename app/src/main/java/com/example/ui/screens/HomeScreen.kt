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
    RANKINGS,
    CP_SPACE
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
    val topWealthUsers by repository.topWealthUsers.collectAsState(initial = emptyList())
    val topCharmUsers by repository.topCharmUsers.collectAsState(initial = emptyList())

    var currentTab by remember { mutableStateOf(HomeMainTab.PARTY) }
    var currentMineSubTab by remember { mutableStateOf(MineSubTab.FOLLOWING) }
    var selectedCountry by remember { mutableStateOf("🔥 All") }
    var isRefreshing by remember { mutableStateOf(false) }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showCreateRoomDialog by remember { mutableStateOf(false) }

    val countries = listOf("🔥 All", "🇵🇰 Pakistan", "🇮🇳 India", "🌐 Global", "🪐 AI")

    // Filter rooms based on rule: Active rooms must have onlineCount > 0
    val displayedPartyRooms = remember(allRooms, selectedCountry) {
        var list = allRooms.filter { it.onlineCount > 0 }
        if (selectedCountry != "🔥 All" && selectedCountry != "🌐 Global" && selectedCountry != "🪐 AI") {
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
            .background(Color(0xFF090514))
    ) {
        // Mystical night lake lantern atmospheric background matching screenshot
        Image(
            painter = painterResource(id = R.drawable.home_night_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.55f
        )

        // Subtle gradient overlay for rich contrast and vibrant scene visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x22060212),
                            Color(0x5509041E),
                            Color(0x9908031A),
                            Color(0xDD070216)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Stylish Header (Logo + "Bisma" with Green Dot, "Voice Chat", Search, Notifications with Red Dot, + Room)
            BismaCompactHeader(
                totalListeners = totalActiveListeners,
                onSearchClick = { showSearchDialog = true },
                onNotificationClick = onOpenNotifications,
                onCreateRoomClick = { showCreateRoomDialog = true }
            )

            // Pull to refresh wrapper matching the screenshot's unified layout
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
                            selectedSubTab = currentTab,
                            onSubTabSelected = { tab ->
                                if (tab == HomeMainTab.RANKINGS) {
                                    onOpenRankings(0)
                                } else {
                                    currentTab = tab
                                }
                            },
                            rooms = displayedPartyRooms,
                            topWealth = topWealthUsers,
                            topCharm = topCharmUsers,
                            allRooms = allRooms,
                            onOpenRankings = onOpenRankings,
                            onOpenRoom = onOpenRoom,
                            onCreateRoomClick = { showCreateRoomDialog = true }
                        )
                    }
                    HomeMainTab.MINE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            HomeWelcomeBanner()
                            Spacer(modifier = Modifier.height(4.dp))
                            CountrySelectorRow(
                                countries = countries,
                                selectedCountry = selectedCountry,
                                onCountrySelected = { selectedCountry = it }
                            )
                            HomeSubTabsCapsule(
                                selectedTab = currentTab,
                                onTabSelected = { tab ->
                                    if (tab == HomeMainTab.RANKINGS) {
                                        onOpenRankings(0)
                                    } else {
                                        currentTab = tab
                                    }
                                }
                            )
                            MineTabContent(
                                subTab = currentMineSubTab,
                                onSubTabSelected = { currentMineSubTab = it },
                                followingRooms = followingRooms,
                                myRooms = myRooms,
                                onOpenRoom = onOpenRoom,
                                onCreateRoomClick = { showCreateRoomDialog = true }
                            )
                        }
                    }
                    HomeMainTab.CP_SPACE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp)
                        ) {
                            HomeWelcomeBanner()
                            Spacer(modifier = Modifier.height(4.dp))
                            CountrySelectorRow(
                                countries = countries,
                                selectedCountry = selectedCountry,
                                onCountrySelected = { selectedCountry = it }
                            )
                            HomeSubTabsCapsule(
                                selectedTab = currentTab,
                                onTabSelected = { tab ->
                                    if (tab == HomeMainTab.RANKINGS) {
                                        onOpenRankings(0)
                                    } else {
                                        currentTab = tab
                                    }
                                }
                            )
                            CpSpaceTabContent(onCreateRoomClick = { showCreateRoomDialog = true })
                        }
                    }
                    HomeMainTab.RANKINGS -> {
                        // Handled via onOpenRankings
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Combined App Icon + "Bisma 🟢" Title + "Voice Chat"
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = Color(0xFF1E1038),
                border = BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(listOf(Color(0xFFFF2A85), Color(0xFF9C27B0), Color(0xFF00E5FF)))
                ),
                modifier = Modifier.size(42.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Microphone with neon crown
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "👑",
                            fontSize = 9.sp,
                            lineHeight = 10.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Bisma Voice",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bisma",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Online green dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                }
                Text(
                    text = "Voice Chat",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB5ACCC)
                )
            }
        }

        // Header Actions: Search, Notifications (with red dot), + Room
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search Button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x332E1D52))
                    .border(1.dp, Color(0x33FFFFFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Notifications Button with unread red badge
            Box(
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0x332E1D52))
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Unread red dot in top-right corner
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 1.dp, y = (-1).dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF2A85))
                )
            }

            // + Room Button (Gradient pill button matching screenshot)
            Button(
                onClick = onCreateRoomClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                shape = RoundedCornerShape(19.dp),
                modifier = Modifier
                    .height(38.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF2A85), Color(0xFF9C27B0), Color(0xFF00E5FF))
                        ),
                        RoundedCornerShape(19.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Room",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Room",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun HomeWelcomeBanner() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF100826),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFF2A85),
                    Color(0xFF7C4DFF),
                    Color(0xFF00E5FF)
                )
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .clip(RoundedCornerShape(18.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_welcome_banner),
            contentDescription = "Welcome to Bisma Voice Chat",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CountrySelectorRow(
    countries: List<String>,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(countries) { country ->
            val isSelected = selectedCountry == country
            Surface(
                color = if (isSelected) Color.Transparent else Color(0x55160A2D),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) Color.Transparent else Color(0x334E357E)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (isSelected) {
                            Modifier.background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF2A85), Color(0xFFE056FD))
                                )
                            )
                        } else Modifier
                    )
                    .clickable { onCountrySelected(country) }
            ) {
                Text(
                    text = country,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun HomeSubTabsCapsule(
    selectedTab: HomeMainTab,
    onTabSelected: (HomeMainTab) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0x66160A2D),
        border = BorderStroke(1.dp, Color(0x334E357E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Party
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected(HomeMainTab.PARTY) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎉", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Party",
                        fontSize = 13.5.sp,
                        fontWeight = if (selectedTab == HomeMainTab.PARTY) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == HomeMainTab.PARTY) Color.White else Color(0xFF9E97B6)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                if (selectedTab == HomeMainTab.PARTY) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                )
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.5.dp))
                }
            }

            // 2. Mine
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected(HomeMainTab.MINE) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💎", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Mine",
                        fontSize = 13.5.sp,
                        fontWeight = if (selectedTab == HomeMainTab.MINE) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == HomeMainTab.MINE) Color.White else Color(0xFF9E97B6)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                if (selectedTab == HomeMainTab.MINE) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                )
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.5.dp))
                }
            }

            // 3. Rankings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected(HomeMainTab.RANKINGS) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🏆", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rankings",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF9E97B6)
                    )
                }
                Spacer(modifier = Modifier.height(5.5.dp))
            }

            // 4. CP Space
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected(HomeMainTab.CP_SPACE) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🖤", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CP Space",
                        fontSize = 13.5.sp,
                        fontWeight = if (selectedTab == HomeMainTab.CP_SPACE) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == HomeMainTab.CP_SPACE) Color.White else Color(0xFF9E97B6)
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                if (selectedTab == HomeMainTab.CP_SPACE) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(2.5.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                )
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.5.dp))
                }
            }
        }
    }
}

@Composable
fun CpSpaceTabContent(
    onCreateRoomClick: () -> Unit
) {
    EmptyRoomsCard(
        message = "No active CP Space rooms\nonline right now in CP Space.",
        selectedCountry = "🖤 CP Space",
        onCreateRoomClick = onCreateRoomClick
    )
}

@Composable
fun PartyTabContent(
    countries: List<String>,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    selectedSubTab: HomeMainTab,
    onSubTabSelected: (HomeMainTab) -> Unit,
    rooms: List<VoiceRoom>,
    topWealth: List<User>,
    topCharm: List<User>,
    allRooms: List<VoiceRoom>,
    onOpenRankings: (Int) -> Unit,
    onOpenRoom: (String) -> Unit,
    onCreateRoomClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 2.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Welcome to Bisma Banner
        item(span = { GridItemSpan(2) }) {
            HomeWelcomeBanner()
        }

        // 2. Country Selector Pills
        item(span = { GridItemSpan(2) }) {
            CountrySelectorRow(
                countries = countries,
                selectedCountry = selectedCountry,
                onCountrySelected = onCountrySelected
            )
        }

        // 3. Sub-Tabs Capsule Bar
        item(span = { GridItemSpan(2) }) {
            HomeSubTabsCapsule(
                selectedTab = selectedSubTab,
                onTabSelected = onSubTabSelected
            )
        }

        // 4. Rankings Hero Showcase Card (Top Wealth, Top Charm, Top Room)
        item(span = { GridItemSpan(2) }) {
            HomeRankingsShowcaseCard(
                topWealth = topWealth,
                topCharm = topCharm,
                allRooms = allRooms,
                onOpenRankings = onOpenRankings
            )
        }

        // 5. Voice Rooms or Empty Scenic Card
        if (rooms.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                EmptyRoomsCard(
                    selectedCountry = selectedCountry,
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

/**
 * Rankings Hero Showcase Card matching the exact visual design in the user's screenshot.
 * Displays Top Wealth, Top Charm, and Top Room with crowns, glowing badges, and view buttons.
 */
@Composable
fun HomeRankingsShowcaseCard(
    topWealth: List<User>,
    topCharm: List<User>,
    allRooms: List<VoiceRoom>,
    onOpenRankings: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xF2100826),
        border = BorderStroke(1.2.dp, Color(0x554E327E)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Header Row: Crown + "Rankings" + laurel + "View All >"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👑",
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rankings",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "🌿",
                        fontSize = 16.sp
                    )
                }

                // View All > button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0x331E1038),
                    border = BorderStroke(1.dp, Color(0x44FFFFFF)),
                    modifier = Modifier.clickable { onOpenRankings(0) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "View All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View All",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Column Showcase Cards: Top Wealth, Top Charm, Top Room
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Top Wealth
                RankingsMiniCard(
                    modifier = Modifier.weight(1f),
                    themeColor = Color(0xFFFFB300),
                    crownEmoji = "👑",
                    title = "Top Wealth",
                    subtitle = "Richest Users",
                    graphicType = "wealth",
                    onClick = { onOpenRankings(0) }
                )

                // Card 2: Top Charm
                RankingsMiniCard(
                    modifier = Modifier.weight(1f),
                    themeColor = Color(0xFFFF2A85),
                    crownEmoji = "👑",
                    title = "Top Charm",
                    subtitle = "Most Attractive Users",
                    graphicType = "charm",
                    onClick = { onOpenRankings(1) }
                )

                // Card 3: Top Room
                RankingsMiniCard(
                    modifier = Modifier.weight(1f),
                    themeColor = Color(0xFF00E5FF),
                    crownEmoji = "👑",
                    title = "Top Room",
                    subtitle = "Most Popular Rooms",
                    graphicType = "room",
                    onClick = { onOpenRankings(2) }
                )
            }
        }
    }
}

/**
 * Individual Mini Card within the Rankings Showcase matching the screenshot:
 * Crown, Title, Subtitle, Glowing custom illustration badge, and Chevron > arrow.
 */
@Composable
fun RankingsMiniCard(
    modifier: Modifier = Modifier,
    themeColor: Color,
    crownEmoji: String,
    title: String,
    subtitle: String,
    graphicType: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x66160A2D),
        border = BorderStroke(1.2.dp, themeColor.copy(alpha = 0.85f)),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Crown emoji
            Text(
                text = crownEmoji,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                fontSize = 7.8.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD4CAE8),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Glowing Badge Graphic matching the screenshot with chevron >
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                contentAlignment = Alignment.Center
            ) {
                when (graphicType) {
                    "wealth" -> WealthChestGraphic(themeColor)
                    "charm" -> CharmHeartGraphic(themeColor)
                    "room" -> RoomPortalGraphic(themeColor)
                }

                // Chevron arrow on bottom right
                Text(
                    text = ">",
                    color = themeColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 4.dp, bottom = 2.dp)
                )
            }
        }
    }
}

@Composable
fun WealthChestGraphic(themeColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Glowing gold radial aura
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x55FFB300), Color(0x22FF8F00), Color.Transparent)
                    )
                )
        )
        // Golden treasure chest overflowing with gold coins
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "✨", fontSize = 9.sp)
            Text(text = "🎁", fontSize = 21.sp)
            Text(text = "🪙 🪙", fontSize = 8.sp)
        }
    }
}

@Composable
fun CharmHeartGraphic(themeColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Glowing pink radial aura
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x55FF2A85), Color(0x22E056FD), Color.Transparent)
                    )
                )
        )
        // Glowing pink neon heart with sparkle rings
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "✨", fontSize = 9.sp)
            Text(text = "💖", fontSize = 21.sp)
            Text(text = "💫", fontSize = 8.sp)
        }
    }
}

@Composable
fun RoomPortalGraphic(themeColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Glowing cyan radial aura
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x5500E5FF), Color(0x2200B0FF), Color.Transparent)
                    )
                )
        )
        // Glowing voice portal / stage with soundwaves
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🎶", fontSize = 9.sp)
            Text(text = "🎙️", fontSize = 21.sp)
            Text(text = "✨", fontSize = 8.sp)
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
    message: String? = null,
    selectedCountry: String = "🔥 All",
    onCreateRoomClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF100826),
        border = BorderStroke(
            1.2.dp,
            Brush.linearGradient(
                listOf(Color(0xFFFF2A85), Color(0xFF7C4DFF), Color(0xFF00E5FF))
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(185.dp)
        ) {
            // Scenic atmospheric night background
            Image(
                painter = painterResource(id = R.drawable.home_night_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.45f
            )

            // Deep gradient overlay for contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xCC0E0720),
                                Color(0xEE0B051A),
                                Color(0xF7090416)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Waveform bars on left, circular MicOff icon, waveform bars on right
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Left wave bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(3.dp).height(10.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF7C4DFF)))
                        Box(modifier = Modifier.width(3.dp).height(16.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFFFF2A85)))
                        Box(modifier = Modifier.width(3.dp).height(22.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF00E5FF)))
                        Box(modifier = Modifier.width(3.dp).height(14.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF7C4DFF)))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Circular icon container
                    Surface(
                        shape = CircleShape,
                        color = Color(0x401F153D),
                        border = BorderStroke(1.dp, Color(0x557C4DFF)),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MicOff,
                                contentDescription = null,
                                tint = Color(0xFFE2DCF0),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right wave bars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(3.dp).height(14.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF7C4DFF)))
                        Box(modifier = Modifier.width(3.dp).height(22.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF00E5FF)))
                        Box(modifier = Modifier.width(3.dp).height(16.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFFFF2A85)))
                        Box(modifier = Modifier.width(3.dp).height(10.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF7C4DFF)))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = message ?: "No active voice rooms\nonline right now in $selectedCountry.",
                    color = Color(0xFFE2DCF0),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Launch Voice Room Button
                Button(
                    onClick = onCreateRoomClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF2A85), Color(0xFF7C4DFF), Color(0xFF00E5FF))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Text(
                        text = "+ Launch Voice Room",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
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
