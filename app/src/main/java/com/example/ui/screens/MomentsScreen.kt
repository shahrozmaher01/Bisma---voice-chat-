package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.CpRelationship
import com.example.data.model.MomentComment
import com.example.data.model.MomentPost
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class MomentsTab {
    FOLLOWING,
    WORLD,
    CP_SPACE
}

@Composable
fun MomentsScreen(
    repository: BismaRepository,
    onOpenUserProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allMoments by repository.allMoments.collectAsState(initial = emptyList())
    val followingMoments by repository.getFollowingMomentsFlow().collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val userCp by repository.getCpForUserFlow(currentUser?.id ?: "").collectAsState(initial = null)
    val topCpList by repository.getTopCpListFlow().collectAsState(initial = emptyList())
    val friendsList by repository.getFriends().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(MomentsTab.WORLD) }
    var selectedFilter by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var activeCommentMoment by remember { mutableStateOf<MomentPost?>(null) }
    var activeShareMoment by remember { mutableStateOf<MomentPost?>(null) }
    var showCpProposeDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf(
        Triple("All", "🔥", "All"),
        Triple("Trending", "👑", "Trending"),
        Triple("Friends", "👥", "Friends"),
        Triple("Nearby", "📍", "Nearby"),
        Triple("Hot", "🔥", "Hot")
    )

    val displayedMoments = remember(allMoments, selectedFilter, friendsList) {
        when (selectedFilter) {
            "Trending" -> allMoments.sortedByDescending { it.likesCount + it.commentsCount }
            "Friends" -> allMoments.filter { post -> friendsList.any { it.friendId == post.authorId || it.userId == post.authorId } }
            "Hot" -> allMoments.filter { it.likesCount > 0 }.sortedByDescending { it.likesCount }
            else -> allMoments
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Full Atmospheric Night Lake Background matching screenshot
        Image(
            painter = painterResource(id = R.drawable.home_night_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x33060212),
                            Color(0x550A041E),
                            Color(0xAA08031A),
                            Color(0xEE070216)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Navigation Bar: Following | World 🌍 | CP Space 💖 | (+)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Following Pill Tab
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedTab == MomentsTab.FOLLOWING) Color(0x66231345) else Color(0x44140C29),
                    border = BorderStroke(1.dp, if (selectedTab == MomentsTab.FOLLOWING) Color(0x66FF4081) else Color(0x22442C73)),
                    modifier = Modifier.clickable { selectedTab = MomentsTab.FOLLOWING }
                ) {
                    Text(
                        text = "Following",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == MomentsTab.FOLLOWING) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == MomentsTab.FOLLOWING) Color.White else Color(0xFFC7BFD9),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                    )
                }

                // World 🌍 Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = MomentsTab.WORLD }
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "World 🌍",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (selectedTab == MomentsTab.WORLD) Color.White else Color(0xFFC7BFD9)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    if (selectedTab == MomentsTab.WORLD) {
                        Box(
                            modifier = Modifier
                                .width(38.dp)
                                .height(3.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                                    )
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier.height(3.5.dp))
                    }
                }

                // CP Space 💖 Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { selectedTab = MomentsTab.CP_SPACE }
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "CP Space",
                        fontSize = 16.sp,
                        fontWeight = if (selectedTab == MomentsTab.CP_SPACE) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == MomentsTab.CP_SPACE) Color.White else Color(0xFFC7BFD9)
                    )
                    Text(
                        text = "💖",
                        fontSize = 11.sp,
                        lineHeight = 12.sp
                    )
                }

                // Circular Gradient (+) Post Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFF2A85), Color(0xFF8B5CF6))
                            )
                        )
                        .clickable { showCreateDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Post Moment",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Filter Pills Row (All, Trending, Friends, Nearby, Hot)
            if (selectedTab != MomentsTab.CP_SPACE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    filterOptions.forEach { (key, icon, label) ->
                        val isFilterSelected = selectedFilter == key
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isFilterSelected) Color.Transparent else Color(0x66180E2E),
                            border = if (isFilterSelected) null else BorderStroke(1.dp, Color(0x334E357E)),
                            modifier = Modifier
                                .then(
                                    if (isFilterSelected) {
                                        Modifier.background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFFFF2A85), Color(0xFF7C4DFF))
                                            ),
                                            RoundedCornerShape(20.dp)
                                        )
                                    } else Modifier
                                )
                                .clickable { selectedFilter = key }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(text = icon, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isFilterSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Main Content Body based on selected tab
            when (selectedTab) {
                MomentsTab.FOLLOWING -> {
                    if (followingMoments.isEmpty()) {
                        EmptyMomentsState(onPostClick = { showCreateDialog = true })
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(followingMoments, key = { it.id }) { post ->
                                MomentCardItem(
                                    post = post,
                                    isAuthor = post.authorId == currentUser?.id,
                                    onUserClick = { onOpenUserProfile(post.authorId) },
                                    onLike = {
                                        coroutineScope.launch {
                                            repository.toggleLikeMoment(post.id, post.isLiked)
                                        }
                                    },
                                    onCommentClick = { activeCommentMoment = post },
                                    onShareClick = { activeShareMoment = post },
                                    onDelete = {
                                        coroutineScope.launch {
                                            repository.deleteMoment(post.id)
                                            Toast.makeText(context, "Moment deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                MomentsTab.WORLD -> {
                    if (displayedMoments.isEmpty()) {
                        EmptyMomentsState(onPostClick = { showCreateDialog = true })
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(displayedMoments, key = { it.id }) { post ->
                                MomentCardItem(
                                    post = post,
                                    isAuthor = post.authorId == currentUser?.id,
                                    onUserClick = { onOpenUserProfile(post.authorId) },
                                    onLike = {
                                        coroutineScope.launch {
                                            repository.toggleLikeMoment(post.id, post.isLiked)
                                        }
                                    },
                                    onCommentClick = { activeCommentMoment = post },
                                    onShareClick = { activeShareMoment = post },
                                    onDelete = {
                                        coroutineScope.launch {
                                            repository.deleteMoment(post.id)
                                            Toast.makeText(context, "Moment deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                MomentsTab.CP_SPACE -> {
                    CpSpaceContent(
                        userCp = userCp,
                        currentUserId = currentUser?.id ?: "",
                        topCpList = topCpList,
                        onProposeClick = { showCpProposeDialog = true },
                        onDissolveClick = {
                            coroutineScope.launch {
                                repository.dissolveCp(currentUser?.id ?: "")
                                Toast.makeText(context, "CP Relationship ended", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onOpenUserProfile = onOpenUserProfile
                    )
                }
            }
        }

        // Post creation dialog
        if (showCreateDialog) {
            CreateMomentDialog(
                onDismiss = { showCreateDialog = false },
                onPublish = { content, imageUrl ->
                    coroutineScope.launch {
                        repository.createMoment(content, imageUrl)
                        showCreateDialog = false
                        Toast.makeText(context, "Moment published! ✨", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Comments Popup Sheet / Dialog
        activeCommentMoment?.let { moment ->
            MomentCommentsSheet(
                moment = moment,
                repository = repository,
                onDismiss = { activeCommentMoment = null }
            )
        }

        // Share to Friend Dialog
        activeShareMoment?.let { moment ->
            ShareMomentDialog(
                moment = moment,
                friends = friendsList,
                onDismiss = { activeShareMoment = null },
                onShareToFriend = { friendUserId ->
                    coroutineScope.launch {
                        repository.shareMomentToFriend(moment, friendUserId)
                        activeShareMoment = null
                        Toast.makeText(context, "Moment shared with friend! 💌", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Propose CP Dialog
        if (showCpProposeDialog) {
            ProposeCpDialog(
                friends = friendsList,
                onDismiss = { showCpProposeDialog = false },
                onPropose = { partnerId, ringName ->
                    coroutineScope.launch {
                        repository.proposeOrAcceptCp(partnerId, ringName)
                        showCpProposeDialog = false
                        Toast.makeText(context, "CP Bond established! 💍💖", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyMomentsState(
    onPostClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Glowing 3D Neon Camera Illustration on cyber pedestal
            Image(
                painter = painterResource(id = R.drawable.moment_camera_empty),
                contentDescription = "No moments",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            // Heart divider line: ─── ♥ ───
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color.Transparent, Color(0x88FF4081))
                            )
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "♥",
                    color = Color(0xFFFF4081),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0x88FF4081), Color.Transparent)
                            )
                        )
                )
            }

            // Description text
            Text(
                text = "No moments posted yet. Be the first to share!",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Vibrant gradient Post Moment button
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF2A85), Color(0xFF00E5FF))
                        )
                    )
                    .clickable { onPostClick() }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Post Moment",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MomentCardItem(
    post: MomentPost,
    isAuthor: Boolean,
    onUserClick: () -> Unit,
    onLike: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Author Avatar & Name & Dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onUserClick() }
                ) {
                    AvatarWithFrame(avatarUrl = post.authorAvatar, size = 42.dp, vipLevel = post.authorVip)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = post.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (post.authorVip > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VipBadge(vipLevel = post.authorVip)
                            }
                        }
                        Text(text = "Social Moment", color = TextMuted, fontSize = 11.sp)
                    }
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextSecondary)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = SurfaceDark
                    ) {
                        if (isAuthor) {
                            DropdownMenuItem(
                                text = { Text("Delete Moment", color = DarkRed) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Report Post", color = TextPrimary) },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content text
            Text(
                text = post.content,
                color = TextPrimary,
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            )

            // Attached Photo
            if (!post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Moment Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Like, Comment, Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLike() }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) NeonPink else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${post.likesCount}", color = if (post.isLiked) NeonPink else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Comment button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onCommentClick() }
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comments", tint = TextSecondary, modifier = Modifier.size(19.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Comment", color = TextSecondary, fontSize = 12.sp)
                }

                // Share button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onShareClick() }
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(19.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Share", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MomentCommentsSheet(
    moment: MomentPost,
    repository: BismaRepository,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val comments by repository.getCommentsForMoment(moment.id).collectAsState(initial = emptyList())
    var commentText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Comments (${comments.size})", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp)
            ) {
                if (comments.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No comments yet. Say something nice!", color = TextSecondary, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(comments) { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCard)
                                    .padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                AsyncImage(
                                    model = c.authorAvatar,
                                    contentDescription = c.authorName,
                                    modifier = Modifier.size(32.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(c.authorName, color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(c.content, color = TextPrimary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Write a comment...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                coroutineScope.launch {
                                    repository.addCommentToMoment(moment.id, commentText.trim())
                                    commentText = ""
                                }
                            }
                        },
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(PrimaryGradient)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun CreateMomentDialog(
    onDismiss: () -> Unit,
    onPublish: (String, String?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                imageUrl = uri.toString()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Create Post ✨", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("What's on your mind?", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                        border = BorderStroke(1.dp, NeonPink),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Pick Image", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gallery Photo", fontSize = 12.sp)
                    }

                    if (imageUrl.isNotBlank()) {
                        TextButton(onClick = { imageUrl = "" }) {
                            Text("Clear", color = DarkRed, fontSize = 12.sp)
                        }
                    }
                }

                if (imageUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Selected Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "Publish",
                onClick = {
                    if (content.isNotBlank()) {
                        onPublish(content.trim(), imageUrl.trim().ifBlank { null })
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

@Composable
fun ShareMomentDialog(
    moment: MomentPost,
    friends: List<com.example.data.model.Friendship>,
    onDismiss: () -> Unit,
    onShareToFriend: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Share Moment to Friend", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                if (friends.isEmpty()) {
                    Text("No friends available to send direct message.", color = TextSecondary, fontSize = 13.sp)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(friends) { f ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceCard)
                                    .clickable { onShareToFriend(f.friendId) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Friend ID: ${f.friendId}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Button(
                                    onClick = { onShareToFriend(f.friendId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Send", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun CpSpaceContent(
    userCp: CpRelationship?,
    currentUserId: String,
    topCpList: List<CpRelationship>,
    onProposeClick: () -> Unit,
    onDissolveClick: () -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // CP Card banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent,
                border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(Color(0xFFFF4081), Color(0xFF7C4DFF)))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x55FF4081), Color(0x337C4DFF), Color(0x99130E29))
                            )
                        )
                        .padding(16.dp)
                ) {
                    if (userCp != null) {
                        val partnerName = if (userCp.user1Id == currentUserId) userCp.user2Name else userCp.user1Name
                        val partnerAvatar = if (userCp.user1Id == currentUserId) userCp.user2Avatar else userCp.user1Avatar
                        val partnerId = if (userCp.user1Id == currentUserId) userCp.user2Id else userCp.user1Id

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💖 Sweet Couple Space", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Dual Avatar Connection
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    model = if (userCp.user1Id == currentUserId) userCp.user1Avatar else userCp.user2Avatar,
                                    contentDescription = "My Avatar",
                                    modifier = Modifier.size(54.dp).clip(CircleShape).border(2.dp, NeonPink, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("💍", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                AsyncImage(
                                    model = partnerAvatar,
                                    contentDescription = partnerName,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, ElectricBlue, CircleShape)
                                        .clickable { onOpenUserProfile(partnerId) },
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Couple: $partnerName", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                            Text(text = "Ring: ${userCp.ringName}", color = NeonPink, fontSize = 12.sp)
                            Text(text = "Intimacy Score: ${userCp.intimacyScore} 🔥", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = onDissolveClick,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkRed),
                                border = BorderStroke(1.dp, DarkRed),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Dissolve CP Bond", fontSize = 11.sp)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("💍 Find Your Soulmate & CP Partner", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Bind with a special friend to unlock exclusive couple rings, intimate floating effects, and CP ranking rewards!",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            NeonButton(
                                text = "💖 Propose CP with Friend",
                                onClick = onProposeClick,
                                modifier = Modifier.fillMaxWidth(0.8f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("🏆 Top CP Intimacy Leaderboard", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (topCpList.isEmpty()) {
            item {
                Text("No CP couples on the leaderboard yet. Be the first couple!", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            items(topCpList) { cp ->
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💖", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("${cp.user1Name} & ${cp.user2Name}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Ring: ${cp.ringName}", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        Text("${cp.intimacyScore} pts", color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProposeCpDialog(
    friends: List<com.example.data.model.Friendship>,
    onDismiss: () -> Unit,
    onPropose: (String, String) -> Unit
) {
    var selectedFriendId by remember { mutableStateOf("") }
    var selectedRing by remember { mutableStateOf("Eternal Diamond Ring 💍") }

    val rings = listOf("Eternal Diamond Ring 💍", "Rose Gold Heart Band 💖", "Galaxy Crystal Crown 👑", "Royal Platinum Tiara ✨")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Propose CP Relationship", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text("Select Friend to Propose to:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                if (friends.isEmpty()) {
                    Text("Add friends first to propose a CP bond.", color = TextMuted, fontSize = 12.sp)
                } else {
                    friends.forEach { f ->
                        val isSelected = selectedFriendId == f.friendId
                        Surface(
                            color = if (isSelected) NeonPink.copy(alpha = 0.3f) else SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) NeonPink else SurfaceCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { selectedFriendId = f.friendId }
                        ) {
                            Text("Friend ID: ${f.friendId}", color = Color.White, modifier = Modifier.padding(10.dp), fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Select Couple Ring:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                rings.forEach { ring ->
                    val isSelected = selectedRing == ring
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRing = ring }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedRing = ring },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonPink)
                        )
                        Text(ring, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "Bind CP",
                onClick = {
                    if (selectedFriendId.isNotBlank()) {
                        onPropose(selectedFriendId, selectedRing)
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
