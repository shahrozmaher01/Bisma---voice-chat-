package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
    var selectedCategory by remember { mutableStateOf("Headwear") }

    val categories = listOf("Headwear", "Entry Effects", "Chat Bubbles", "Sound Waves")

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = if (selectedMainTab == 0) "Fashion Store ✨" else "My Backpack 🎒",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    color = Color(0x33FFD700),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0x88FFD700))
                ) {
                    Text(
                        text = "🪙 ${currentUser?.coins ?: 0}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Main Tab Switcher (Store | Backpack)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = if (selectedMainTab == 0) NeonPink else SurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (selectedMainTab == 0) NeonPink else SurfaceCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMainTab = 0 }
                ) {
                    Text(
                        text = "🛍️ Store",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedMainTab == 0) Color.White else TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Surface(
                    color = if (selectedMainTab == 1) NeonPink else SurfaceCard,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (selectedMainTab == 1) NeonPink else SurfaceCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedMainTab = 1 }
                ) {
                    Text(
                        text = "🎒 Backpack (${backpackItems.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedMainTab == 1) Color.White else TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }

            // Category Filter Pills: Headwear, Entry Effects, Chat Bubbles, Sound Waves
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        color = if (isSelected) ElectricBlue else SurfaceCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isSelected) ElectricBlue else SurfaceCardBorder),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            val displayedItems = if (selectedMainTab == 0) {
                allItems.filter { it.category == selectedCategory }
            } else {
                backpackItems.filter { it.category == selectedCategory }
            }

            if (displayedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedMainTab == 0) "No items in this category." else "You don't own any $selectedCategory yet.\nCheck the boutique store!",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedItems, key = { it.id }) { item ->
                        StoreItemCard(
                            item = item,
                            isBackpack = selectedMainTab == 1,
                            onBuy = {
                                coroutineScope.launch {
                                    val success = repository.buyStoreItem(item.id)
                                    if (success) {
                                        Toast.makeText(context, "Purchased ${item.name}! Added to Backpack 🎒", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Insufficient Coins! Please recharge.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onEquipToggle = {
                                coroutineScope.launch {
                                    if (item.isEquipped) {
                                        repository.unequipItem(item.id, item.category)
                                        Toast.makeText(context, "Unequipped ${item.name}", Toast.LENGTH_SHORT).show()
                                    } else {
                                        repository.equipItem(item.id, item.category)
                                        Toast.makeText(context, "Equipped ${item.name} ✨", Toast.LENGTH_SHORT).show()
                                    }
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
    onBuy: () -> Unit,
    onEquipToggle: () -> Unit
) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (item.isEquipped) NeonPink else SurfaceCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon / Emoji Stage
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(item.frameColorHex).copy(alpha = 0.15f))
                    .border(1.5.dp, Color(item.frameColorHex).copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.previewIcon, fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = item.category,
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!isBackpack) {
                // Store Mode: Show price & Buy button
                if (item.isOwned) {
                    Surface(
                        color = Color(0x3300E676),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Owned ✓",
                            color = Color(0xFF00E676),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onBuy,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🪙 ${item.price}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Backpack Mode: Equip / Unequip Button
                Button(
                    onClick = onEquipToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isEquipped) Color(0xFF333344) else ElectricBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (item.isEquipped) "Unequip" else "Equip ✨",
                        color = if (item.isEquipped) TextSecondary else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
