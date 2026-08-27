package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoreItem
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun StoreBackpackScreen(
    repository: BismaRepository,
    initialTab: Int = 0, // 0: Store, 1: Backpack
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val allItems by repository.storeItems.collectAsState(initial = emptyList())
    val backpackItems by repository.backpackItems.collectAsState(initial = emptyList())

    var selectedMainTab by remember { mutableIntStateOf(initialTab) }
    var selectedCategory by remember { mutableStateOf("Frames") }
    var previewFrameId by remember { mutableStateOf<String?>(currentUser?.equippedFrameId) }

    val categories = listOf("Frames", "Headwear", "Entry Effects", "Chat Bubbles", "Sound Waves")

    LaunchedEffect(currentUser?.equippedFrameId) {
        previewFrameId = currentUser?.equippedFrameId
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
                    text = if (selectedMainTab == 0) "Cosmetics Store" else "My Outfit Backpack",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Top Avatar Preview Stage (Shows avatar clearly with transparent frame overlay!)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Avatar Live Fitting Preview", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    AvatarWithFrame(
                        avatarUrl = currentUser?.avatarUrl,
                        size = 80.dp,
                        frameId = previewFrameId,
                        vipLevel = currentUser?.vipLevel ?: 0
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentUser?.username ?: "User",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Balance: 🪙 ${currentUser?.coins ?: 0} Coins",
                        fontSize = 12.sp,
                        color = GoldYellow,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Main Tab Switcher (Store | Backpack)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    label = { Text("🛍️ Boutique Store") }
                )
                FilterChip(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    label = { Text("🎒 My Backpack (${backpackItems.size})") }
                )
            }

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) SurfaceCard else Color(0xFF140A22))
                            .border(1.dp, if (isSelected) NeonPink else SurfaceCardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }

            // Item Grid
            val displayedItems = if (selectedMainTab == 0) {
                allItems.filter { it.category == selectedCategory }
            } else {
                backpackItems.filter { it.category == selectedCategory }
            }

            if (displayedItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedMainTab == 0) "No items in this category" else "No owned items in this category yet",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentPadding = PaddingValues(bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedItems) { item ->
                        StoreItemCard(
                            item = item,
                            isBackpack = selectedMainTab == 1,
                            onPreview = {
                                if (item.category == "Frames") {
                                    previewFrameId = item.id
                                }
                            },
                            onBuy = {
                                coroutineScope.launch {
                                    val success = repository.buyStoreItem(item.id)
                                    if (success) {
                                        Toast.makeText(context, "Item purchased & stored in Backpack! 🎉", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Insufficient Coins! Please recharge.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onEquip = {
                                coroutineScope.launch {
                                    repository.equipItem(item.id, item.category)
                                    previewFrameId = item.id
                                    Toast.makeText(context, "Equipped ${item.name}! ✨", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onUnequip = {
                                coroutineScope.launch {
                                    repository.unequipItem(item.id, item.category)
                                    previewFrameId = null
                                    Toast.makeText(context, "Unequipped item", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoreItemCard(
    item: StoreItem,
    isBackpack: Boolean,
    onPreview: () -> Unit,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF25103B))
                    .clickable { onPreview() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.previewIcon, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            Text(
                text = "🪙 ${item.price} Coins",
                fontSize = 11.sp,
                color = GoldYellow,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!isBackpack) {
                if (item.isOwned) {
                    Surface(
                        color = EmeraldGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "✓ Owned",
                            color = EmeraldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Buy", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                if (item.isEquipped) {
                    OutlinedButton(
                        onClick = onUnequip,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Unequip", color = DarkRed, fontSize = 10.sp)
                    }
                } else {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Equip", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
