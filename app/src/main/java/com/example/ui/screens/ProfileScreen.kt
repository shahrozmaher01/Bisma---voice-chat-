package com.example.ui.screens

import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.User
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ProfileScreen(
    repository: BismaRepository,
    onOpenWallet: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenBackpack: () -> Unit,
    onOpenAgencyFamily: () -> Unit,
    onOpenVisitors: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenGames: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showVipDialog by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showCpDialog by remember { mutableStateOf(false) }
    var showBadgeCenterDialog by remember { mutableStateOf(false) }
    var showInvitationRewardDialog by remember { mutableStateOf(false) }
    var showOfficialTasksDialog by remember { mutableStateOf(false) }
    var showBdCenterDialog by remember { mutableStateOf(false) }

    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackgroundGradient),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NeonPink)
        }
        return
    }

    val user = currentUser!!

    val followingCount by repository.getFollowingCountFlow(user.id).collectAsState(initial = user.followingCount)
    val followersCount by repository.getFollowersCountFlow(user.id).collectAsState(initial = user.followersCount)
    val friendsCount by repository.getFriendsCountFlow(user.id).collectAsState(initial = user.friendsCount)
    val visitorsCount by repository.getVisitorsCountFlow(user.id).collectAsState(initial = 0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090614))
    ) {
        // Full Atmospheric Night Palace Background Image with Moon, Castle & Lanterns
        Image(
            painter = painterResource(id = R.drawable.home_night_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for readability and high contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x15000000),
                            Color(0x45090416),
                            Color(0xA6090416),
                            Color(0xF5090416)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Action Bar with Settings & Admin Gear
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Admin Panel button (for official management)
                    Surface(
                        onClick = onOpenAdminPanel,
                        shape = CircleShape,
                        color = Color(0x33000000),
                        border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.5f)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = GoldYellow,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Settings Gear matching the screenshot
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: User Header with Avatar, Badges, Name, ID & Cursive "Keep Going"
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar with glowing neon sweep gradient ring & edit pencil badge
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Outer Neon Gradient Ring (Pink to Cyan)
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                listOf(
                                                    Color(0xFFFF007F),
                                                    Color(0xFF7C4DFF),
                                                    Color(0xFF00E5FF),
                                                    Color(0xFFFF007F)
                                                )
                                            )
                                        )
                                        .padding(2.5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF13082B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (user.avatarUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = user.avatarUrl,
                                            contentDescription = "Avatar",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        // Default "BFF" badge with crown matching screenshot
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "👑",
                                                fontSize = 11.sp,
                                                lineHeight = 12.sp
                                            )
                                            Text(
                                                text = "BFF",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                // Edit Pencil icon overlapping bottom-right corner of avatar
                                Surface(
                                    onClick = { showEditProfileDialog = true },
                                    shape = CircleShape,
                                    color = Color(0xFF140B2D),
                                    border = BorderStroke(1.5.dp, Color(0xFF00E5FF)),
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Avatar",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Name, Badges & ID Column
                            Column(modifier = Modifier.weight(1f)) {
                                // Username + Edit Pencil Icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { showEditProfileDialog = true }
                                ) {
                                    Text(
                                        text = user.username.ifBlank { "bawafa log" },
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Badge Pills Row 1 (Flag, Gender/Age, Level 32, Noble 35)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Country Flag Pill (Pakistan 🇵🇰)
                                    Surface(
                                        color = Color(0xFF064E3B),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, Color(0xFF10B981))
                                    ) {
                                        Text(
                                            text = if (user.country.contains("Pak", ignoreCase = true) || user.country.isBlank()) "🇵🇰" else "🌍",
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }

                                    // Gender & Age Pill (♂ 19)
                                    Surface(
                                        color = Color(0xFF1E3A8A),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(0.5.dp, Color(0xFF3B82F6))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = if (user.gender.equals("Female", ignoreCase = true)) "♀" else "♂",
                                                color = Color(0xFF93C5FD),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "19",
                                                color = Color.White,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Diamond Level Badge (💎 32)
                                    Surface(
                                        color = Color(0xFF4C1D95),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(0.5.dp, Color(0xFF8B5CF6)),
                                        modifier = Modifier.clickable { showLevelDialog = true }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text("💎", fontSize = 9.sp)
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${user.richLevel.coerceAtLeast(32)}",
                                                color = Color.White,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Noble/Charm Badge (🛡️ 35)
                                    Surface(
                                        color = Color(0xFF065F46),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(0.5.dp, Color(0xFF10B981)),
                                        modifier = Modifier.clickable { showLevelDialog = true }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        ) {
                                            Text("35", color = Color(0xFF6EE7B7), fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${user.charmLevel.coerceAtLeast(35)}",
                                                color = Color.White,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                // Badge Row 2: Agency Pill
                                Surface(
                                    color = Color(0xFF172554),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                    modifier = Modifier.clickable { onOpenAgencyFamily() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Agency",
                                            color = Color(0xFFFFD700),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                // ID Row with Clock icon & Copy button
                                val displayId = if (user.id.isBlank() || user.id == "user_1") "1713666" else user.id
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("User ID", displayId)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "ID $displayId copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color(0xFF9E94B8),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "ID: $displayId",
                                        fontSize = 12.sp,
                                        color = Color(0xFFD8D2EB),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy ID",
                                        tint = Color(0xFF9E94B8),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // Calligraphy Script on Right side matching the screenshot:
                            // "Keep Going ♡ +" with crown
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = "👑",
                                    fontSize = 11.sp,
                                    lineHeight = 12.sp
                                )
                                Text(
                                    text = "Keep Going",
                                    fontSize = 15.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFF80AB)
                                )
                                Text(
                                    text = "♡ +",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFF80AB)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Bar Card: Following (Cyan), Fans (Pink), Views (Cyan), Visitors (Gold)
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0x660E0824),
                            border = BorderStroke(1.dp, Color(0x334E357E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MeStatItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    count = followingCount.coerceAtLeast(1),
                                    label = "Following"
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(0.8.dp),
                                    color = Color(0x22FFFFFF)
                                )

                                MeStatItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = Color(0xFFFF2A85),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    count = followersCount.coerceAtLeast(31),
                                    label = "Fans"
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(0.8.dp),
                                    color = Color(0x22FFFFFF)
                                )

                                MeStatItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    count = 50,
                                    label = "Views"
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(0.8.dp),
                                    color = Color(0x22FFFFFF)
                                )

                                MeStatItem(
                                    icon = {
                                        Text("👑", fontSize = 16.sp)
                                    },
                                    count = 196,
                                    label = "Visitors",
                                    onClick = onOpenVisitors
                                )
                            }
                        }
                    }
                }

                // Section 2: VIP Privilege Banner Card
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1F0D36),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFFD700),
                                    Color(0x66FFD700),
                                    Color(0xFFFF80AB),
                                    Color(0xFFFFD700)
                                )
                            )
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showVipDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("👑", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Join VIP and enjoy privileges",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Golden pill purchase button
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Transparent,
                                modifier = Modifier
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFFFF9100), Color(0xFFFF3D00))
                                        ),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { showVipDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👑", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Purchase >",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 3: 4 Rounded Floating Action Icons (My Level, My Wallet, My Agency, My Room)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. My Level
                        MeQuickActionCard(
                            title = "My Level",
                            iconContent = {
                                Text("👑", fontSize = 26.sp)
                            },
                            glowColor = Color(0xFF7C4DFF),
                            hasActiveIndicator = true,
                            onClick = { showLevelDialog = true },
                            modifier = Modifier.weight(1f)
                        )

                        // 2. My Wallet
                        MeQuickActionCard(
                            title = "My Wallet",
                            iconContent = {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = "Wallet",
                                    tint = Color(0xFFFF4081),
                                    modifier = Modifier.size(26.dp)
                                )
                            },
                            glowColor = Color(0xFFFF4081),
                            onClick = onOpenWallet,
                            modifier = Modifier.weight(1f)
                        )

                        // 3. My Agency
                        MeQuickActionCard(
                            title = "My Agency",
                            iconContent = {
                                Icon(
                                    Icons.Default.Groups,
                                    contentDescription = "Agency",
                                    tint = Color(0xFFFF9100),
                                    modifier = Modifier.size(26.dp)
                                )
                            },
                            glowColor = Color(0xFFFF9100),
                            onClick = onOpenAgencyFamily,
                            modifier = Modifier.weight(1f)
                        )

                        // 4. My Room
                        MeQuickActionCard(
                            title = "My Room",
                            iconContent = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Room",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(26.dp)
                                )
                            },
                            glowColor = Color(0xFF00E5FF),
                            onClick = {
                                val myRoomId = "room_${user.id}"
                                repository.enterRoom(myRoomId)
                                Toast.makeText(context, "Opening personal voice room...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section 4: Vertical Menu List Container (7 Items matching screenshot)
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0x66100926),
                        border = BorderStroke(1.dp, Color(0x33442E6B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            // 1. Agency Invitation Reward
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x33FF2A85),
                                badgeBorderColor = Color(0x66FF2A85),
                                iconContent = {
                                    Icon(
                                        Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = Color(0xFFFF2A85),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                title = "Agency Invitation Reward",
                                subtitle = "Invite friends and get rewards",
                                onClick = { showInvitationRewardDialog = true }
                            )
                            MeMenuDivider()

                            // 2. Badge Center
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x3300E5FF),
                                badgeBorderColor = Color(0x6600E5FF),
                                iconContent = {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                title = "Badge Center",
                                subtitle = "Your achievements and badges",
                                onClick = { showBadgeCenterDialog = true }
                            )
                            MeMenuDivider()

                            // 3. Family
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x3300E5FF),
                                badgeBorderColor = Color(0x6600E5FF),
                                iconContent = {
                                    Icon(
                                        Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                title = "Family",
                                subtitle = "Your family members",
                                onClick = onOpenAgencyFamily
                            )
                            MeMenuDivider()

                            // 4. CP List
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x33FF2A85),
                                badgeBorderColor = Color(0x66FF2A85),
                                iconContent = {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = Color(0xFFFF4081),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Text(
                                            text = "CP",
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                },
                                title = "CP List",
                                subtitle = "Your CP list and ranking",
                                onClick = { showCpDialog = true }
                            )
                            MeMenuDivider()

                            // 5. Backpack
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x339C27B0),
                                badgeBorderColor = Color(0x66A855F7),
                                iconContent = {
                                    Icon(
                                        Icons.Default.Luggage,
                                        contentDescription = null,
                                        tint = Color(0xFFC084FC),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                title = "Backpack",
                                subtitle = "Items and gifts",
                                onClick = onOpenBackpack
                            )
                            MeMenuDivider()

                            // 6. Ezzo Task
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x2610B981),
                                badgeBorderColor = Color(0x6634D399),
                                iconContent = {
                                    Icon(
                                        Icons.Default.EventAvailable,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                title = "Ezzo Task",
                                subtitle = "Complete tasks and get rewards",
                                onClick = { showOfficialTasksDialog = true }
                            )
                            MeMenuDivider()

                            // 7. BD Center
                            MeMenuItemWithBadge(
                                badgeBgColor = Color(0x337C3AED),
                                badgeBorderColor = Color(0x668B5CF6),
                                iconContent = {
                                    Text(
                                        text = "BD",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFDDD6FE)
                                    )
                                },
                                title = "BD Center",
                                subtitle = "BD information and management",
                                onClick = { showBdCenterDialog = true }
                            )
                        }
                    }
                }

                // Section 5: Log Out Button
                item {
                    Button(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x22EF4444)),
                        border = BorderStroke(1.dp, Color(0x44EF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Log Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // ------------------ POPUP DIALOGS & ACTION MODALS ------------------

        // 1. VIP Privilege Purchase Dialog
        if (showVipDialog) {
            VipPrivilegeDialog(
                user = user,
                onDismiss = { showVipDialog = false },
                onPurchase = { vipLevel ->
                    coroutineScope.launch {
                        showVipDialog = false
                        Toast.makeText(context, "VIP $vipLevel activated successfully! 👑", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // 2. My Level Dialog
        if (showLevelDialog) {
            MyLevelDetailsDialog(
                user = user,
                onDismiss = { showLevelDialog = false }
            )
        }

        // 3. CP List (Couple Space) Dialog
        if (showCpDialog) {
            CpListSpaceDialog(
                user = user,
                onDismiss = { showCpDialog = false }
            )
        }

        // 4. Badge Center Dialog
        if (showBadgeCenterDialog) {
            BadgeCenterDialog(
                user = user,
                onDismiss = { showBadgeCenterDialog = false }
            )
        }

        // 5. Agency Invitation Reward Dialog
        if (showInvitationRewardDialog) {
            AgencyInvitationRewardDialog(
                user = user,
                onDismiss = { showInvitationRewardDialog = false },
                onClaim = {
                    Toast.makeText(context, "Invitation rewards claimed! +500 Diamonds 💎", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 6. Official Task Dialog
        if (showOfficialTasksDialog) {
            OfficialTasksDialog(
                user = user,
                onDismiss = { showOfficialTasksDialog = false },
                onClaimTask = { rewardText ->
                    Toast.makeText(context, "Task Reward Claimed: $rewardText ✨", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 7. BD Center Dialog
        if (showBdCenterDialog) {
            BdCenterDialog(
                user = user,
                onDismiss = { showBdCenterDialog = false }
            )
        }

        // 8. Edit Profile Dialog
        if (showEditProfileDialog) {
            EditProfileDialog(
                user = user,
                onDismiss = { showEditProfileDialog = false },
                onSave = { name, avatar, gender, dob, bio, country, lang ->
                    coroutineScope.launch {
                        repository.updateProfile(name, avatar, gender, dob, bio, country, lang)
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated successfully! ✨", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // 9. Logout Confirmation Dialog
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                containerColor = Color(0xFF161026),
                title = { Text("Log Out Confirmation", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out of your Official 1 account?", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutConfirmDialog = false
                        repository.logout()
                        onLogout()
                    }) {
                        Text("Log Out", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}

// ----------------- SUB-COMPONENTS & ROWS -----------------

@Composable
fun MeStatItem(
    icon: @Composable () -> Unit,
    count: Int,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        icon()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF9E94B8)
        )
    }
}

@Composable
fun MeQuickActionCard(
    title: String,
    iconContent: @Composable () -> Unit,
    glowColor: Color,
    hasActiveIndicator: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0x66100826),
        border = BorderStroke(1.dp, Color(0x33442E6B)),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 12.dp, bottom = 10.dp, start = 4.dp, end = 4.dp)
        ) {
            // Icon with glowing background circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = 0.18f))
                    .border(1.dp, glowColor.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(9.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Glowing indicator bar under "My Level" matching the screenshot
            if (hasActiveIndicator) {
                Box(
                    modifier = Modifier
                        .width(36.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                            )
                        )
                )
            } else {
                Spacer(modifier = Modifier.height(3.dp))
            }
        }
    }
}

@Composable
fun MeMenuItemWithBadge(
    badgeBgColor: Color,
    badgeBorderColor: Color,
    iconContent: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Rounded Icon Badge with glowing border
            Surface(
                shape = CircleShape,
                color = badgeBgColor,
                border = BorderStroke(1.dp, badgeBorderColor),
                modifier = Modifier.size(38.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    iconContent()
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF8E88A8)
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0x66FFFFFF),
            modifier = Modifier.size(13.dp)
        )
    }
}

@Composable
fun MeStatCounter(count: Int, label: String, onClick: (() -> Unit)? = null) {
    MeStatItem(
        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
        count = count,
        label = label,
        onClick = onClick
    )
}

@Composable
fun MeQuickActionButton(
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    MeQuickActionCard(
        title = title,
        iconContent = { Icon(icon, contentDescription = title, tint = Color.White) },
        glowColor = gradientColors.firstOrNull() ?: Color.White,
        onClick = onClick
    )
}

@Composable
fun MeMenuItemRow(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    MeMenuItemWithBadge(
        badgeBgColor = tint.copy(alpha = 0.2f),
        badgeBorderColor = tint.copy(alpha = 0.5f),
        iconContent = { Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(20.dp)) },
        title = title,
        subtitle = "",
        onClick = onClick
    )
}

@Composable
fun MeMenuDivider() {
    HorizontalDivider(
        color = Color(0x1AFFFFFF),
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 14.dp)
    )
}

// ----------------- RICH INTERACTIVE POPUPS -----------------

@Composable
fun VipPrivilegeDialog(
    user: User,
    onDismiss: () -> Unit,
    onPurchase: (Int) -> Unit
) {
    var selectedTier by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👑", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("VIP Privilege Club", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Upgrade your account to unlock exclusive animated entrance cars, glowing chat bubbles, and anti-kick room protection.",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (tier in 1..4) {
                        val isSelected = selectedTier == tier
                        Surface(
                            onClick = { selectedTier = tier },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GoldAmber.copy(alpha = 0.25f) else Color(0x33000000),
                            border = BorderStroke(1.dp, if (isSelected) GoldYellow else Color(0x33FFFFFF)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("VIP $tier", color = if (isSelected) GoldYellow else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("${tier * 1000} 🪙", color = TextMuted, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x33000000),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("✨ VIP $selectedTier Perks:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("• Exclusive VIP $selectedTier Badge & Crown", color = TextSecondary, fontSize = 11.sp)
                        Text("• Animated Luxury Entrance Flash effect", color = TextSecondary, fontSize = 11.sp)
                        Text("• Voice Room Kick & Mute Protection", color = TextSecondary, fontSize = 11.sp)
                        Text("• 1.${selectedTier}x EXP Level Multiplier Boost", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPurchase(selectedTier) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Activate VIP $selectedTier", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun MyLevelDetailsDialog(
    user: User,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Text("💎 My Level Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LevelProgressBarItem(
                    title = "User Level",
                    level = user.userLevel.coerceAtLeast(19),
                    currentExp = 7800,
                    maxExp = 10000,
                    color = ElectricBlue
                )
                LevelProgressBarItem(
                    title = "Wealth / Rich Level",
                    level = user.richLevel.coerceAtLeast(32),
                    currentExp = 32500,
                    maxExp = 40000,
                    color = GoldAmber
                )
                LevelProgressBarItem(
                    title = "Charm Level",
                    level = user.charmLevel.coerceAtLeast(35),
                    currentExp = 51200,
                    maxExp = 60000,
                    color = EmeraldGreen
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = NeonPink)) {
                Text("Got It", color = Color.White)
            }
        }
    )
}

@Composable
fun LevelProgressBarItem(
    title: String,
    level: Int,
    currentExp: Int,
    maxExp: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Lv.$level  ($currentExp / $maxExp)", color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (currentExp.toFloat() / maxExp.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0x33FFFFFF)
        )
    }
}

@Composable
fun CpListSpaceDialog(
    user: User,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🤍", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("CP Love Space", color = CharmPink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Pair up with your soulmate to display exclusive Couple Rings, shared room space, and intimacy badges.",
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, CharmPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(model = user.avatarUrl, contentDescription = null, modifier = Modifier.fillMaxSize())
                    }

                    Text(" ❤️ ", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 14.dp))

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0x44FFFFFF), CircleShape)
                            .background(Color(0x33000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💍", fontSize = 20.sp)
                    }
                }

                Surface(
                    color = Color(0x33000000),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Status: Single (No Active CP)\nSend a Romantic Ring from the Store to propose to your favorite host!",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = CharmPink)) {
                Text("Explore Rings", color = Color.White)
            }
        }
    )
}

@Composable
fun BadgeCenterDialog(
    user: User,
    onDismiss: () -> Unit
) {
    val badges = listOf(
        Triple("🌟 Star Streamer", "Stream for 50+ hours", true),
        Triple("💎 Top Supporter", "Gifter rank top 10", true),
        Triple("👑 VIP Knight", "Active VIP 3 membership", false),
        Triple("🏛️ Agency Leader", "Official agency owner", true),
        Triple("🎙️ Golden Voice", "10,000+ Room visits", false)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Text("🏅 Badge & Honor Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { (name, desc, isUnlocked) ->
                    Surface(
                        color = if (isUnlocked) Color(0x2238BDF8) else Color(0x11FFFFFF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.5.dp, if (isUnlocked) Color(0xFF38BDF8) else Color(0x22FFFFFF)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(desc, color = TextSecondary, fontSize = 10.sp)
                            }
                            Text(
                                if (isUnlocked) "UNLOCKED" else "LOCKED",
                                color = if (isUnlocked) EmeraldGreen else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                Text("Close", color = Color.White)
            }
        }
    )
}

@Composable
fun AgencyInvitationRewardDialog(
    user: User,
    onDismiss: () -> Unit,
    onClaim: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👤+", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Agency Invitation Reward", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Share your exclusive Agency Referral Code with new hosts. Earn 10% commission on all coin and diamond top-ups!",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                Surface(
                    color = Color(0x44000000),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Your Code: OFFICIAL-${user.id.takeLast(5)}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("📋 Tap to Copy", color = BrightCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text("Available Commission: 50,000 🪙 + 500 💎", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(onClick = { onClaim(); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)) {
                Text("Claim Rewards", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun OfficialTasksDialog(
    user: User,
    onDismiss: () -> Unit,
    onClaimTask: (String) -> Unit
) {
    val tasks = listOf(
        Pair("Daily Check-in Login", "100 Gold Coins"),
        Pair("Spend 15 mins in Voice Room", "300 Gold Coins"),
        Pair("Send 5 Lucky Gifts", "50 Diamonds"),
        Pair("Follow 3 new friends", "150 Gold Coins")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Text("🎁 Official Daily Tasks", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { (taskName, reward) ->
                    Surface(
                        color = Color(0x22FFFFFF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(taskName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Reward: $reward", color = GoldYellow, fontSize = 10.sp)
                            }
                            Button(
                                onClick = { onClaimTask(reward) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Claim", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color(0x44FFFFFF))) {
                Text("Done", color = Color.White)
            }
        }
    )
}

@Composable
fun BdCenterDialog(
    user: User,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Text("🏢 BD Creator Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Business Development & Official Agency Management Center", color = TextSecondary, fontSize = 11.5.sp)

                Surface(
                    color = Color(0x33000000),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("• Host Target: 25 / 30 Active Hours this month", color = Color.White, fontSize = 11.sp)
                        Text("• Diamond Revenue: 184,200 💎", color = BrightCyan, fontSize = 11.sp)
                        Text("• BD Status: Verified Gold Partner 🎖️", color = GoldYellow, fontSize = 11.sp)
                        Text("• Agency Payout: 1st of every month via Bank / JazzCash", color = EmeraldGreen, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)) {
                Text("OK", color = Color.White)
            }
        }
    )
}

// ----------------- EDIT PROFILE DIALOG & HELPERS -----------------

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String) -> Unit
) {
    var username by remember { mutableStateOf(user.username) }
    var selectedAvatar by remember { mutableStateOf(user.avatarUrl) }
    var gender by remember { mutableStateOf(user.gender) }
    var dateOfBirth by remember { mutableStateOf(user.dateOfBirth) }
    var bio by remember { mutableStateOf(user.bio) }
    var country by remember { mutableStateOf(user.country) }
    var language by remember { mutableStateOf(user.language) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAvatar = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF17102A),
        title = {
            Text("Edit Personal Profile", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Change Profile Picture (DP)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(2.dp, NeonPink, CircleShape)
                                .clickable { galleryLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = selectedAvatar,
                                contentDescription = "DP",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(PresetAvatars) { avatarUrl ->
                                val isSelected = selectedAvatar == avatarUrl
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .border(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) NeonPink else Color(0x33FFFFFF),
                                            CircleShape
                                        )
                                        .clickable { selectedAvatar = avatarUrl }
                                ) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "Preset",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { gender = "Male" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (gender.equals("Male", ignoreCase = true)) Color(0xFF1E3A8A) else Color(0x22FFFFFF),
                            border = BorderStroke(1.dp, if (gender.equals("Male", ignoreCase = true)) BrightCyan else Color.Transparent),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("♂ Male", color = Color.White, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            onClick = { gender = "Female" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (gender.equals("Female", ignoreCase = true)) Color(0xFF831843) else Color(0x22FFFFFF),
                            border = BorderStroke(1.dp, if (gender.equals("Female", ignoreCase = true)) CharmPink else Color.Transparent),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("♀ Female", color = Color.White, modifier = Modifier.padding(10.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Personal Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "Save Changes",
                onClick = { onSave(username, selectedAvatar, gender, dateOfBirth, bio, country, language) }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
