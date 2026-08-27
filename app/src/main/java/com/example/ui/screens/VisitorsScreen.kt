package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VisitorRecord
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VisitorsScreen(
    repository: BismaRepository,
    onBack: () -> Unit,
    onOpenUserProfile: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val visitors by repository.getVisitors().collectAsState(initial = emptyList())

    // Mock initial visitor records if empty for real display
    val sampleVisitors = listOf(
        VisitorRecord(id = "v_1", targetUserId = "883921", visitorId = "104928", visitorName = "Ali Khan 🎙️", visitorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300", visitorVip = 3),
        VisitorRecord(id = "v_2", targetUserId = "883921", visitorId = "209411", visitorName = "Aarav Sharma 🎸", visitorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300", visitorVip = 4),
        VisitorRecord(id = "v_3", targetUserId = "883921", visitorId = "305182", visitorName = "Zara Noor ✨", visitorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300", visitorVip = 3)
    )

    val displayedVisitors = if (visitors.isEmpty()) sampleVisitors else visitors

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Profile Visitors (${displayedVisitors.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(displayedVisitors) { v ->
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenUserProfile(v.visitorId) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarWithFrame(avatarUrl = v.visitorAvatar, size = 44.dp, vipLevel = v.visitorVip)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = v.visitorName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "Viewed your profile recently", color = TextSecondary, fontSize = 10.sp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            repository.sendFriendRequest(v.visitorId)
                                            Toast.makeText(context, "Friend request sent!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
