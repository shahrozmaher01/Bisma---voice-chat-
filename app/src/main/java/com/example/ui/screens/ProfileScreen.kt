package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

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

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackgroundGradient), contentAlignment = Alignment.Center) {
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
            .background(DarkBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Top Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onOpenAdminPanel,
                        modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceCard)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = GoldYellow, modifier = Modifier.size(18.dp))
                    }
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(34.dp).clip(CircleShape).background(SurfaceCard)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // User Card Info (with DP, Name, ID, Gender, DOB, Edit Button)
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Transparent frame avatar overlay
                                AvatarWithFrame(
                                    avatarUrl = user.avatarUrl,
                                    size = 76.dp,
                                    frameId = user.equippedFrameId,
                                    vipLevel = user.vipLevel,
                                    onClick = { showEditProfileDialog = true }
                                )

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.username,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        VipBadge(vipLevel = user.vipLevel)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // ID copy row
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("User ID", user.id)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "ID ${user.id} copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Text(
                                            text = "ID: ${user.id} • ${user.country}",
                                            fontSize = 12.sp,
                                            color = GoldYellow,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GoldYellow, modifier = Modifier.size(13.dp))
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Gender and Date of Birth Badges
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Gender Badge
                                        Surface(
                                            color = if (user.gender.equals("Female", ignoreCase = true)) CharmPink.copy(alpha = 0.25f) else ElectricBlueDark.copy(alpha = 0.35f),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(0.5.dp, if (user.gender.equals("Female", ignoreCase = true)) CharmPink else ElectricBlue)
                                        ) {
                                            Text(
                                                text = if (user.gender.equals("Female", ignoreCase = true)) "👩 Female" else "👨 Male",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        // Date of Birth Badge
                                        Surface(
                                            color = Color(0x339C27B0),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(0.5.dp, NeonViolet)
                                        ) {
                                            Text(
                                                text = "🎂 ${user.dateOfBirth}",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Level Badges Row
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LevelBadge(level = user.userLevel, type = LevelType.USER)
                                LevelBadge(level = user.richLevel, type = LevelType.RICH)
                                LevelBadge(level = user.charmLevel, type = LevelType.CHARM)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Bio
                            Text(
                                text = user.bio,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Edit Profile Button (Prominent)
                            Button(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, NeonPink),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeonPink, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Edit Profile (DP, Name, Gender, DOB)",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Stats Row: Following, Followers, Friends, Visitors
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x33140A22))
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatItem(count = followingCount, label = "Following")
                                StatItem(count = followersCount, label = "Followers")
                                StatItem(count = friendsCount, label = "Friends")
                                StatItem(count = visitorsCount, label = "Visitors", onClick = onOpenVisitors)
                            }
                        }
                    }
                }

                // Wallet Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2A1544), Color(0xFF190C2B), Color(0xFF10071C))
                                )
                            )
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "My Wallet & Balance",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                TextButton(
                                    onClick = onOpenWallet,
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Recharge ❯", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🪙", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "${user.coins}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = GoldYellow)
                                        Text(text = "Gold Coins", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💎", fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "${user.diamonds}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BrightCyan)
                                        Text(text = "Diamonds", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Main Features Menu Grid
                item {
                    Text(
                        text = "Social & Community Center",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProfileMenuCard(icon = Icons.Default.Storefront, title = "Store", subtitle = "Frames & Effects", color = NeonPink, modifier = Modifier.weight(1f), onClick = onOpenStore)
                            ProfileMenuCard(icon = Icons.Default.Backpack, title = "Backpack", subtitle = "Equip Outfits", color = ElectricBlue, modifier = Modifier.weight(1f), onClick = onOpenBackpack)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProfileMenuCard(icon = Icons.Default.CorporateFare, title = "Agency & Family", subtitle = "Clans & Guilds", color = GoldAmber, modifier = Modifier.weight(1f), onClick = onOpenAgencyFamily)
                            ProfileMenuCard(icon = Icons.Default.SportsEsports, title = "Lucky Games", subtitle = "Lucky 77 Slots", color = EmeraldGreen, modifier = Modifier.weight(1f), onClick = onOpenGames)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProfileMenuCard(icon = Icons.Default.Visibility, title = "Visitors List", subtitle = "Who viewed profile", color = CharmPink, modifier = Modifier.weight(1f), onClick = onOpenVisitors)
                            ProfileMenuCard(icon = Icons.Default.Share, title = "Share Profile", subtitle = "Invite Friends", color = NeonViolet, modifier = Modifier.weight(1f), onClick = {
                                Toast.makeText(context, "Profile link copied to share!", Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                }

                // Logout Button
                item {
                    Button(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FF1744)),
                        border = BorderStroke(1.dp, Color(0x66FF1744)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Logout, contentDescription = "Log Out", tint = DarkRed, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Log Out of Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // EDIT PROFILE DIALOG
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

        // LOGOUT CONFIRMATION DIALOG
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                containerColor = SurfaceDark,
                title = { Text("Confirm Log Out", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to log out of Bisma Voice Chat?", color = TextSecondary) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutConfirmDialog = false
                        repository.logout()
                        onLogout()
                    }) {
                        Text("Log Out", color = DarkRed, fontWeight = FontWeight.Bold)
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

@Composable
fun StatItem(count: Int, label: String, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Text(text = "$count", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
fun ProfileMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
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
        containerColor = SurfaceDark,
        title = {
            Text("Edit Personal Profile", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section: Change DP
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
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Surface(
                                    color = NeonPink,
                                    shape = CircleShape,
                                    modifier = Modifier.padding(bottom = 2.dp).size(22.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = "Change Photo",
                                        tint = Color.White,
                                        modifier = Modifier.padding(3.dp).fillMaxSize()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Button to select from gallery
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, NeonPink),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NeonPink
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Collections,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = NeonPink
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Choose from Gallery",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Or choose an avatar:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Avatar Presets Row
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
                                            if (isSelected) NeonPink else SurfaceCardBorder,
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

                // Name
                item {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                // Gender Selection
                item {
                    Column {
                        Text(text = "Gender", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GenderSelectionChip(
                                title = "Female 👩",
                                isSelected = gender.equals("Female", ignoreCase = true),
                                modifier = Modifier.weight(1f),
                                onClick = { gender = "Female" }
                            )
                            GenderSelectionChip(
                                title = "Male 👨",
                                isSelected = gender.equals("Male", ignoreCase = true),
                                modifier = Modifier.weight(1f),
                                onClick = { gender = "Male" }
                            )
                        }
                    }
                }

                // Date of Birth
                item {
                    Column {
                        Text(text = "Date of Birth", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceCard)
                                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    showDatePicker(context, dateOfBirth) { newDob ->
                                        dateOfBirth = newDob
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "🎂 $dateOfBirth", color = Color.White, fontSize = 13.sp)
                            Text(text = "Select 📅", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Bio
                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Personal Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                // Country
                item {
                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country / Region") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = SurfaceCardBorder,
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
