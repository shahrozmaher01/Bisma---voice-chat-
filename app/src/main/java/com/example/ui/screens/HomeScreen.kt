package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.example.data.model.RoomSeat
import com.example.data.model.User
import com.example.data.model.VoiceRoom
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class RoomViewMode {
    GRID,
    LIST
}

enum class RoomSortOption(val label: String, val icon: String) {
    POPULAR("Popular", "🔥"),
    SPEAKERS("Active Mic", "🎙️"),
    NEWEST("Newest", "✨"),
    VIP_HOSTS("VIP Hosts", "👑")
}

data class RoomCategoryItem(
    val id: String,
    val name: String,
    val icon: String,
    val gradient: Brush
)

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

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Recommend, 1: Following, 2: VIP, 3: Mine
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedCountry by remember { mutableStateOf("🔥 All") }
    var selectedSort by remember { mutableStateOf(RoomSortOption.POPULAR) }
    var viewMode by remember { mutableStateOf(RoomViewMode.GRID) }
    var isRefreshingCounts by remember { mutableStateOf(false) }

    var showSearchDialog by remember { mutableStateOf(false) }
    var showCreateRoomDialog by remember { mutableStateOf(false) }

    val countries = listOf("🔥 All", "🇵🇰 Pakistan", "🇮🇳 India", "🇧🇩 Bangladesh", "🇳🇵 Nepal", "🇸🇦 Saudi", "🇦🇪 UAE", "🌐 Global")

    val categories = remember {
        listOf(
            RoomCategoryItem("ALL", "All Rooms", "🌟", Brush.horizontalGradient(listOf(Color(0xFFEC4899), Color(0xFFA855F7)))),
            RoomCategoryItem("Singing & Chill", "Singing & Chill", "🎤", Brush.horizontalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6)))),
            RoomCategoryItem("Talk & Podcast", "Talk & Podcast", "💬", Brush.horizontalGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)))),
            RoomCategoryItem("Music", "Music & Jam", "🎸", Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))),
            RoomCategoryItem("Gaming & Ludo", "Gaming & Ludo", "🎮", Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4)))),
            RoomCategoryItem("Dating & Singles", "Dating & CP", "💖", Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFFFB7185)))),
            RoomCategoryItem("Late Night & Chill", "Late Night", "🌙", Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7)))),
            RoomCategoryItem("Royal VIP", "Royal VIP", "👑", Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
        )
    }

    // Filter and sort rooms dynamically
    val displayedRooms = remember(allRooms, selectedTab, selectedCategory, selectedCountry, selectedSort, currentUser) {
        var filtered = allRooms

        // 1. Tab filter
        filtered = when (selectedTab) {
            1 -> filtered.filter { it.isFeatured || it.ownerVip >= 3 } // Following/Hot
            2 -> filtered.filter { it.ownerVip >= 3 || it.category == "Royal VIP" } // VIP Lounges
            3 -> filtered.filter { it.ownerId == currentUser?.id } // Mine
            else -> filtered // Recommend
        }

        // 2. Category filter
        if (selectedCategory != "ALL") {
            filtered = filtered.filter { it.category == selectedCategory }
        }

        // 3. Country / Region filter
        if (selectedCountry != "🔥 All" && selectedCountry != "🌐 Global") {
            val countryKey = selectedCountry.replace(Regex("[^A-Za-z]"), "").trim()
            filtered = filtered.filter { it.country.contains(countryKey, ignoreCase = true) || it.country == selectedCountry }
        }

        // 4. Sorting
        when (selectedSort) {
            RoomSortOption.POPULAR -> filtered.sortedByDescending { it.onlineCount }
            RoomSortOption.SPEAKERS -> filtered.sortedByDescending { it.seatCount }
            RoomSortOption.NEWEST -> filtered.sortedByDescending { it.createdAt }
            RoomSortOption.VIP_HOSTS -> filtered.sortedByDescending { it.ownerVip }
        }
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
            // Top Header with logo, online badge, search, refresh counts, and create room
            HomeTopHeader(
                totalListeners = totalActiveListeners,
                isRefreshing = isRefreshingCounts,
                onRefreshClick = {
                    if (!isRefreshingCounts) {
                        coroutineScope.launch {
                            isRefreshingCounts = true
                            repository.refreshLiveListenerCounts()
                            delay(600)
                            isRefreshingCounts = false
                        }
                    }
                },
                onSearchClick = { showSearchDialog = true },
                onNotificationClick = onOpenNotifications,
                onCreateRoomClick = { showCreateRoomDialog = true }
            )

            // Top Navigation Tabs (Recommend | Following | VIP Lounges | Mine)
            HomeTopTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            LazyVerticalGrid(
                columns = if (viewMode == RoomViewMode.GRID) GridCells.Fixed(2) else GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Promotional Banner Carousel
                item(span = { GridItemSpan(maxLineSpan) }) {
                    PromoBannerCarousel()
                }

                // 3 Ranking Shortcut Cards (Top Wealth, Top Charm, Top Room)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RankingsSection(
                        onRankingsClick = onOpenRankings
                    )
                }

                // Dynamic Categories Selector Section
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RoomCategoriesSection(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        allRooms = allRooms,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                // Country / Region Filter Pills
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CountryFilterSection(
                        countries = countries,
                        selectedCountry = selectedCountry,
                        onCountrySelected = { selectedCountry = it }
                    )
                }

                // Social Browsing Controls Header (Sort Selector, Active Rooms Count & View Mode Toggle)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SocialBrowsingControls(
                        roomCount = displayedRooms.size,
                        selectedSort = selectedSort,
                        onSortSelected = { selectedSort = it },
                        viewMode = viewMode,
                        onToggleViewMode = {
                            viewMode = if (viewMode == RoomViewMode.GRID) RoomViewMode.LIST else RoomViewMode.GRID
                        }
                    )
                }

                // Empty state or Room Grid / List
                if (displayedRooms.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
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
                                    text = if (selectedTab == 3) "You don't have an active room yet" else "No active voice rooms found in this category / region",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                NeonButton(
                                    text = "Create Room Now",
                                    onClick = { showCreateRoomDialog = true },
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                )
                            }
                        }
                    }
                } else {
                    if (viewMode == RoomViewMode.GRID) {
                        items(displayedRooms, key = { it.id }) { room ->
                            RoomGridCard(
                                room = room,
                                repository = repository,
                                onClick = { onOpenRoom(room.id) }
                            )
                        }
                    } else {
                        items(displayedRooms, key = { it.id }) { room ->
                            RoomSocialListCard(
                                room = room,
                                repository = repository,
                                onClick = { onOpenRoom(room.id) }
                            )
                        }
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
fun HomeTopHeader(
    totalListeners: Int,
    isRefreshing: Boolean,
    onRefreshClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onCreateRoomClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refresh_rotation"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // App Logo and Dynamic Online Listeners Badge
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.bisma_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Bisma",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Live Community Badge
                    Surface(
                        color = Color(0x3310B981),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalListeners Live",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
                Text(
                    text = "VOICE CHAT & SOCIAL",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPink,
                    letterSpacing = 1.sp
                )
            }
        }

        // Header Action buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Live count refresh button
            IconButton(
                onClick = onRefreshClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Counts",
                    tint = if (isRefreshing) ElectricBlue else Color.White,
                    modifier = Modifier
                        .size(17.dp)
                        .rotate(if (isRefreshing) rotation else 0f)
                )
            }

            // Search button
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Notifications
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SurfaceCard)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
            }

            // Create Room Button (+)
            Button(
                onClick = onCreateRoomClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(34.dp)
                    .background(PrimaryGradient, RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "Room", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun HomeTopTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Recommend", "⭐ Following", "👑 VIP Lounges", "🎧 My Rooms")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(tabs.size) { index ->
            val title = tabs[index]
            val isSelected = selectedTab == index
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = if (isSelected) 16.sp else 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
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
fun RoomCategoriesSection(
    categories: List<RoomCategoryItem>,
    selectedCategory: String,
    allRooms: List<VoiceRoom>,
    onCategorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📂", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Room Categories",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                text = "Explore vibes",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat.id
                val roomCount = if (cat.id == "ALL") allRooms.size else allRooms.count { it.category == cat.id }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) cat.gradient else Brush.horizontalGradient(listOf(SurfaceCard, SurfaceCard)))
                        .border(
                            1.dp,
                            if (isSelected) Color.White.copy(alpha = 0.5f) else SurfaceCardBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onCategorySelected(cat.id) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(text = cat.icon, fontSize = 14.sp)
                        Text(
                            text = cat.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = Color.White
                        )
                        // Room count pill
                        Surface(
                            color = if (isSelected) Color.Black.copy(alpha = 0.3f) else Color(0x33EC4899),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$roomCount",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else NeonPinkLight,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialBrowsingControls(
    roomCount: Int,
    selectedSort: RoomSortOption,
    onSortSelected: (RoomSortOption) -> Unit,
    viewMode: RoomViewMode,
    onToggleViewMode: () -> Unit
) {
    var expandedSortMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Room count indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(EmeraldGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Active Rooms ($roomCount)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Sorting & View Mode Switcher
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Sort Dropdown Button
            Box {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.clickable { expandedSortMenu = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(text = selectedSort.icon, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedSort.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = expandedSortMenu,
                    onDismissRequest = { expandedSortMenu = false },
                    modifier = Modifier.background(SurfaceDark)
                ) {
                    RoomSortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = option.icon, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = option.label,
                                        color = if (selectedSort == option) NeonPink else TextPrimary,
                                        fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = {
                                onSortSelected(option)
                                expandedSortMenu = false
                            }
                        )
                    }
                }
            }

            // View Mode Toggle Button (Grid <-> List)
            IconButton(
                onClick = onToggleViewMode,
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = if (viewMode == RoomViewMode.GRID) Icons.Default.ViewAgenda else Icons.Default.GridView,
                    contentDescription = "Switch View",
                    tint = ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun RoomGridCard(
    room: VoiceRoom,
    repository: BismaRepository,
    onClick: () -> Unit
) {
    val seats by repository.getSeatsForRoom(room.id).collectAsState(initial = emptyList())
    val activeSpeakers = remember(seats) {
        seats.filter { !it.userId.isNullOrBlank() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column {
            // Room Cover Image with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
            ) {
                AsyncImage(
                    model = room.coverUrl,
                    contentDescription = room.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x33000000), Color(0x66000000), Color(0xEE130E29))
                            )
                        )
                )

                // Top Online Badge + Country Flag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Flag badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = room.country.split(" ").firstOrNull() ?: "🌍",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    // Online User Count with live sound wave
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            SpeakingWaveAnimation(modifier = Modifier.height(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatCount(room.onlineCount),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Password lock badge
                if (room.isLocked) {
                    Surface(
                        color = DarkRed.copy(alpha = 0.85f),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Category tag on bottom-left of image
                Surface(
                    color = NeonViolet.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(topEnd = 8.dp),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Text(
                        text = room.category,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }

                // Hot indicator if listener count > 500
                if (room.onlineCount >= 500) {
                    Surface(
                        color = Color(0xEEEC4899),
                        shape = RoundedCornerShape(topStart = 8.dp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Text(
                            text = "🔥 HOT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Room Info & Speaker Preview Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = room.title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Host row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AvatarWithFrame(
                        avatarUrl = room.ownerAvatar,
                        size = 18.dp,
                        vipLevel = room.ownerVip
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.ownerName,
                        fontSize = 10.5.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "ID:${room.id.takeLast(4)}",
                        fontSize = 8.5.sp,
                        color = TextMuted
                    )
                }

                // Active Speaker Preview Bubbles (if any speakers on stage)
                if (activeSpeakers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy((-6).dp)
                        ) {
                            activeSpeakers.take(3).forEach { seat ->
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, if (seat.isSpeaking) EmeraldGreen else SurfaceCardBorder, CircleShape)
                                ) {
                                    AsyncImage(
                                        model = seat.avatarUrl ?: room.ownerAvatar,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                        Text(
                            text = "🎙️ ${activeSpeakers.size}/${room.seatCount}",
                            fontSize = 8.5.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoomSocialListCard(
    room: VoiceRoom,
    repository: BismaRepository,
    onClick: () -> Unit
) {
    val seats by repository.getSeatsForRoom(room.id).collectAsState(initial = emptyList())
    val activeSpeakers = remember(seats) {
        seats.filter { !it.userId.isNullOrBlank() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Room Cover Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = room.coverUrl,
                    contentDescription = room.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Country Flag
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(bottomEnd = 6.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = room.country.split(" ").firstOrNull() ?: "🌍",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }

                // Live Listeners Wave Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topStart = 6.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        SpeakingWaveAnimation(modifier = Modifier.height(8.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = formatCount(room.onlineCount),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Room Details & Stage Speakers
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = room.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (room.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = DarkRed,
                            modifier = Modifier.size(14.dp).padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Host & Category info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarWithFrame(
                        avatarUrl = room.ownerAvatar,
                        size = 16.dp,
                        vipLevel = room.ownerVip
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.ownerName,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = NeonViolet.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(0.5.dp, NeonViolet.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = room.category,
                            fontSize = 8.5.sp,
                            color = NeonPurpleLight,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Stage Speakers & Join Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Speaker avatars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy((-6).dp)
                    ) {
                        if (activeSpeakers.isEmpty()) {
                            Text(
                                text = "🎙️ Host on Mic",
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                        } else {
                            activeSpeakers.take(4).forEach { seat ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, if (seat.isSpeaking) EmeraldGreen else SurfaceCardBorder, CircleShape)
                                ) {
                                    AsyncImage(
                                        model = seat.avatarUrl ?: room.ownerAvatar,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${activeSpeakers.size}/${room.seatCount} Mics",
                                fontSize = 9.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Quick Join Pill Button
                    Surface(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.7f)),
                        modifier = Modifier.clickable { onClick() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Listen",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonPinkLight
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "❯", fontSize = 9.sp, color = NeonPink)
                        }
                    }
                }
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return if (count >= 1000) {
        String.format("%.1fk", count / 1000.0)
    } else {
        count.toString()
    }
}

@Composable
fun PromoBannerCarousel() {
    val banners = listOf(
        Triple("🌟 Super Voice Gala 2026", "Join live singing contests & win 50,000 Coins!", Brush.horizontalGradient(listOf(NeonPink, NeonViolet))),
        Triple("👑 VIP Royalty Carnival", "Exclusive animated avatar frames & entrance perks", Brush.horizontalGradient(listOf(GoldYellow, GoldOrange))),
        Triple("💖 CP Sweet Heart Month", "Pair with your special one & claim romantic badges", Brush.horizontalGradient(listOf(CharmPink, NeonPinkLight)))
    )
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(105.dp)
        ) { page ->
            val (title, sub, gradient) = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(gradient)
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = sub,
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Explore Event ❯",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Indicator dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (pagerState.currentPage == index) 7.dp else 4.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == index) NeonPink else SurfaceCardBorder)
                )
            }
        }
    }
}

@Composable
fun RankingsSection(
    onRankingsClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Wealth
        RankingCard(
            title = "Top Wealth",
            icon = "🏆",
            brush = Brush.verticalGradient(listOf(Color(0xFF382604), Color(0xFF160F02))),
            border = GoldAmber,
            modifier = Modifier.weight(1f),
            onClick = { onRankingsClick(0) }
        )

        // Top Charm
        RankingCard(
            title = "Top Charm",
            icon = "💜",
            brush = Brush.verticalGradient(listOf(Color(0xFF380B28), Color(0xFF180411))),
            border = CharmRose,
            modifier = Modifier.weight(1f),
            onClick = { onRankingsClick(1) }
        )

        // Top Room
        RankingCard(
            title = "Top Room",
            icon = "💚",
            brush = Brush.verticalGradient(listOf(Color(0xFF042D23), Color(0xFF02130F))),
            border = EmeraldGreen,
            modifier = Modifier.weight(1f),
            onClick = { onRankingsClick(2) }
        )
    }
}

@Composable
fun RankingCard(
    title: String,
    icon: String,
    brush: Brush,
    border: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
            .border(1.dp, border.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Rankings ❯",
                fontSize = 8.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun CountryFilterSection(
    countries: List<String>,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        countries.forEach { country ->
            val isSelected = selectedCountry == country
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) SurfaceCard else Color(0xFF100B22))
                    .border(
                        1.2.dp,
                        if (isSelected) NeonPink else SurfaceCardBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onCountrySelected(country) }
                    .padding(horizontal = 9.dp, vertical = 5.dp)
            ) {
                Text(
                    text = country,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextSecondary
                )
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
    var searchQuery by remember { mutableStateOf("") }
    var searchMode by remember { mutableIntStateOf(0) } // 0: Users, 1: Rooms
    var userResults by remember { mutableStateOf<List<User>>(emptyList()) }
    var roomResults by remember { mutableStateOf<List<VoiceRoom>>(emptyList()) }

    LaunchedEffect(searchQuery, searchMode) {
        if (searchQuery.isNotBlank()) {
            if (searchMode == 0) {
                userResults = repository.searchUsers(searchQuery)
            } else {
                roomResults = repository.searchRooms(searchQuery)
            }
        } else {
            userResults = emptyList()
            roomResults = emptyList()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Column {
                Text("Search Bisma Community", color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = searchMode == 0,
                        onClick = { searchMode = 0 },
                        label = { Text("Find Users") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = searchMode == 1,
                        onClick = { searchMode = 1 },
                        label = { Text("Find Rooms") }
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (searchMode == 0) "Enter User Name or ID..." else "Enter Room Name or ID...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonPink) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (searchMode == 0) {
                    if (userResults.isEmpty() && searchQuery.isNotBlank()) {
                        Text("No users found", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                    }
                    userResults.forEach { u ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectUser(u.id) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvatarWithFrame(avatarUrl = u.avatarUrl, size = 36.dp, vipLevel = u.vipLevel)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(u.username, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("ID: ${u.id} • ${u.country}", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                } else {
                    if (roomResults.isEmpty() && searchQuery.isNotBlank()) {
                        Text("No rooms found", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                    }
                    roomResults.forEach { r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectRoom(r.id) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = r.coverUrl,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)),
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
    var selectedCategory by remember { mutableStateOf("Singing & Chill") }
    var isLocked by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    val seatOptions = listOf(4, 6, 8, 10, 12, 15, 20)
    val categories = listOf("Singing & Chill", "Talk & Podcast", "Music", "Gaming & Ludo", "Dating & Singles", "Late Night & Chill", "Royal VIP")

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

                Text("Select Category", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
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
                            category = selectedCategory,
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
