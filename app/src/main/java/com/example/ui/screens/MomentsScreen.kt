package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.MomentPost
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MomentsScreen(
    repository: BismaRepository,
    onOpenUserProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val moments by repository.allMoments.collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var showCreateDialog by remember { mutableStateOf(false) }

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Social Moments 📸",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.background(PrimaryGradient, RoundedCornerShape(18.dp))
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Post", tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Post Moment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (moments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No moments posted yet. Be the first to share!", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(moments) { post ->
                        MomentCard(
                            post = post,
                            isAuthor = post.authorId == currentUser?.id,
                            onUserClick = { onOpenUserProfile(post.authorId) },
                            onLike = {
                                coroutineScope.launch {
                                    repository.toggleLikeMoment(post.id, post.isLiked)
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    repository.deleteMoment(post.id)
                                    Toast.makeText(context, "Moment deleted", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onReport = {
                                Toast.makeText(context, "Moment reported to safety team", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

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
    }
}

@Composable
fun MomentCard(
    post: MomentPost,
    isAuthor: Boolean,
    onUserClick: () -> Unit,
    onLike: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Post Author Header
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = post.authorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (post.authorVip > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                VipBadge(vipLevel = post.authorVip)
                            }
                        }
                        Text(text = "Just now • Public", color = TextMuted, fontSize = 10.sp)
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
                                onClick = {
                                    showMenu = false
                                    onReport()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Post Text
            Text(
                text = post.content,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // Post Image
            if (!post.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Moment Photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row (Like, Comment, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) NeonPink else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${post.likesCount}", color = TextSecondary, fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Comments", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${post.commentsCount}", color = TextSecondary, fontSize = 12.sp)
                }

                IconButton(onClick = { }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CreateMomentDialog(
    onDismiss: () -> Unit,
    onPublish: (String, String?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val samplePhotos = listOf(
        "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=600",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600",
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=600",
        "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600"
    )
    var selectedPhoto by remember { mutableStateOf<String?>(samplePhotos.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Publish New Moment", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("What's on your mind? Share your voice & thoughts...") },
                    modifier = Modifier.fillMaxWidth().height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Attach Image from Gallery:", color = TextSecondary, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    samplePhotos.forEach { url ->
                        val isSelected = selectedPhoto == url
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(if (isSelected) 2.dp else 0.dp, if (isSelected) NeonPink else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedPhoto = if (isSelected) null else url },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        confirmButton = {
            NeonButton(
                text = "Publish",
                onClick = {
                    if (text.isNotBlank()) {
                        onPublish(text.trim(), selectedPhoto)
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
