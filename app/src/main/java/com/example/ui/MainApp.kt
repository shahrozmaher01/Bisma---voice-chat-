package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BismaRepository
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object MainTabs : Screen()
    data class VoiceRoom(val roomId: String) : Screen()
    object Wallet : Screen()
    data class StoreBackpack(val initialTab: Int = 0) : Screen()
    object AgencyFamily : Screen()
    data class Rankings(val initialTab: Int = 0) : Screen()
    object Visitors : Screen()
    object Notifications : Screen()
    object Settings : Screen()
    object Games : Screen()
    object AdminPanel : Screen()
}

enum class BottomTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    MOMENT("Moment", Icons.Default.CameraAlt),
    CHAT("Chat", Icons.Default.ChatBubble),
    ME("Me", Icons.Default.Person)
}

@Composable
fun BismaMainApp() {
    val context = LocalContext.current
    val repository = remember { BismaRepository(context) }
    val isLoggedIn by repository.isLoggedIn.collectAsState()

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedBottomTab by remember { mutableStateOf(BottomTab.HOME) }
    var inspectUserId by remember { mutableStateOf<String?>(null) }

    // System Back navigation handling for sub-screens and dialogs
    if (inspectUserId != null) {
        BackHandler {
            inspectUserId = null
        }
    } else if (currentScreen != Screen.Splash && currentScreen != Screen.Login && currentScreen != Screen.MainTabs) {
        BackHandler {
            currentScreen = Screen.MainTabs
        }
    } else if (currentScreen == Screen.MainTabs && selectedBottomTab != BottomTab.HOME) {
        BackHandler {
            selectedBottomTab = BottomTab.HOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        when (val screen = currentScreen) {
            is Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = if (isLoggedIn) Screen.MainTabs else Screen.Login
                    }
                )
            }
            is Screen.Login -> {
                LoginScreen(
                    repository = repository,
                    onLoginSuccess = {
                        currentScreen = Screen.MainTabs
                    }
                )
            }
            is Screen.MainTabs -> {
                Scaffold(
                    bottomBar = {
                        BismaBottomNavigationBar(
                            selectedTab = selectedBottomTab,
                            onTabSelected = { selectedBottomTab = it }
                        )
                    },
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (selectedBottomTab) {
                            BottomTab.HOME -> HomeScreen(
                                repository = repository,
                                onOpenRoom = { roomId ->
                                    repository.enterRoom(roomId)
                                    currentScreen = Screen.VoiceRoom(roomId)
                                },
                                onOpenRankings = { tab ->
                                    currentScreen = Screen.Rankings(tab)
                                },
                                onOpenNotifications = {
                                    currentScreen = Screen.Notifications
                                },
                                onOpenUserProfile = { userId ->
                                    inspectUserId = userId
                                }
                            )
                            BottomTab.MOMENT -> MomentsScreen(
                                repository = repository,
                                onOpenUserProfile = { userId ->
                                    inspectUserId = userId
                                }
                            )
                            BottomTab.CHAT -> ChatScreen(
                                repository = repository,
                                onOpenUserProfile = { userId ->
                                    inspectUserId = userId
                                }
                            )
                            BottomTab.ME -> ProfileScreen(
                                repository = repository,
                                onOpenWallet = { currentScreen = Screen.Wallet },
                                onOpenStore = { currentScreen = Screen.StoreBackpack(0) },
                                onOpenBackpack = { currentScreen = Screen.StoreBackpack(1) },
                                onOpenAgencyFamily = { currentScreen = Screen.AgencyFamily },
                                onOpenVisitors = { currentScreen = Screen.Visitors },
                                onOpenSettings = { currentScreen = Screen.Settings },
                                onOpenGames = { currentScreen = Screen.Games },
                                onOpenAdminPanel = { currentScreen = Screen.AdminPanel },
                                onLogout = { currentScreen = Screen.Login }
                            )
                        }
                    }
                }
            }
            is Screen.VoiceRoom -> {
                VoiceRoomScreen(
                    repository = repository,
                    onCloseRoom = {
                        currentScreen = Screen.MainTabs
                    },
                    onOpenGames = {
                        currentScreen = Screen.Games
                    },
                    onOpenUserProfile = { userId ->
                        inspectUserId = userId
                    }
                )
            }
            is Screen.Wallet -> {
                WalletScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs }
                )
            }
            is Screen.StoreBackpack -> {
                StoreBackpackScreen(
                    repository = repository,
                    initialTab = screen.initialTab,
                    onBack = { currentScreen = Screen.MainTabs }
                )
            }
            is Screen.AgencyFamily -> {
                AgencyFamilyScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs }
                )
            }
            is Screen.Rankings -> {
                RankingsScreen(
                    repository = repository,
                    initialTab = screen.initialTab,
                    onBack = { currentScreen = Screen.MainTabs },
                    onOpenUserProfile = { userId -> inspectUserId = userId },
                    onOpenRoom = { roomId ->
                        repository.enterRoom(roomId)
                        currentScreen = Screen.VoiceRoom(roomId)
                    }
                )
            }
            is Screen.Visitors -> {
                VisitorsScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs },
                    onOpenUserProfile = { userId -> inspectUserId = userId }
                )
            }
            is Screen.Notifications -> {
                NotificationsScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs }
                )
            }
            is Screen.Settings -> {
                SettingsScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs },
                    onLogout = {
                        repository.logout()
                        currentScreen = Screen.Login
                    }
                )
            }
            is Screen.Games -> {
                GamesScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs }
                )
            }
            is Screen.AdminPanel -> {
                AdminPanelScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.MainTabs }
                )
            }
        }

        // Global User Profile Inspection Dialog
        if (inspectUserId != null) {
            UserProfileDetailDialog(
                userId = inspectUserId!!,
                repository = repository,
                onDismiss = { inspectUserId = null }
            )
        }
    }
}

@Composable
fun BismaBottomNavigationBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    Surface(
        color = Color(0xF20B061A),
        tonalElevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x33442C73), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home
            BottomNavTabItem(
                tab = BottomTab.HOME,
                isSelected = selectedTab == BottomTab.HOME,
                onClick = { onTabSelected(BottomTab.HOME) }
            )

            // 2. Moment (with glowing circular camera badge when selected)
            BottomNavTabItem(
                tab = BottomTab.MOMENT,
                isSelected = selectedTab == BottomTab.MOMENT,
                onClick = { onTabSelected(BottomTab.MOMENT) }
            )

            // 3. Chat
            BottomNavTabItem(
                tab = BottomTab.CHAT,
                isSelected = selectedTab == BottomTab.CHAT,
                onClick = { onTabSelected(BottomTab.CHAT) },
                hasBadge = selectedTab != BottomTab.CHAT
            )

            // 4. Me
            BottomNavTabItem(
                tab = BottomTab.ME,
                isSelected = selectedTab == BottomTab.ME,
                onClick = { onTabSelected(BottomTab.ME) }
            )
        }
    }
}

@Composable
private fun BottomNavTabItem(
    tab: BottomTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasBadge: Boolean = false,
    hasCrown: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (tab == BottomTab.MOMENT && isSelected) {
                // Moment circular glowing badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF2A85),
                                    Color(0xFF7C4DFF),
                                    Color(0xFF00E5FF),
                                    Color(0xFFFF2A85)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = tab.title,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (tab == BottomTab.CHAT && isSelected) {
                // Chat rounded pink badge matching screenshot
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFFF2A85),
                                    Color(0xFFE056FD)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = tab.title,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (hasCrown) {
                        Text(
                            text = "👑",
                            fontSize = 9.sp,
                            lineHeight = 10.sp
                        )
                    }
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isSelected) Color(0xFFFF2A85) else Color(0xFF8E88A8),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Red notification badge on Chat
            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 3.dp, y = (-2).dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF2A85))
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = tab.title,
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFFFF2A85) else Color(0xFF8E88A8)
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Selected indicator line
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(18.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(Color(0xFFFF2A85))
            )
        } else {
            Spacer(modifier = Modifier.height(3.dp))
        }
    }
}
