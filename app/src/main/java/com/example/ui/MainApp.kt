package com.example.ui

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

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedBottomTab by remember { mutableStateOf(BottomTab.HOME) }
    var inspectUserId by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        when (val screen = currentScreen) {
            is Screen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
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
                                onOpenAdminPanel = { currentScreen = Screen.AdminPanel }
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
                    onLogout = { currentScreen = Screen.Splash }
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
        color = SurfaceDark,
        tonalElevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, SurfaceCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(NeonPink.copy(alpha = 0.15f))
                            )
                        }
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) NeonPink else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonPink else TextMuted
                    )
                }
            }
        }
    }
}
