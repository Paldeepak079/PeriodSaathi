package com.deepak.periodsaathi.ui.screens.wardrobe

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun WardrobeScreen(
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    val accessories by viewModel.accessories.collectAsState()
    val equippedItems by viewModel.equippedItems.collectAsState()
    val totalPoints by viewModel.totalPoints.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0E2E), Color(0xFF1A1228))))
            .padding(16.dp)
    ) {
        // Left side - Accessories grid
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Wardrobe", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Points display
            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⭐", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$totalPoints pts", color = WarmGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accessories) { accessory ->
                    AccessoryCard(
                        accessory = accessory,
                        isEquipped = accessory.id in equippedItems,
                        onEquip = { viewModel.equipAccessory(accessory.id) },
                        onUnlock = { viewModel.unlockAccessory(accessory.id, accessory.cost) },
                        canAfford = totalPoints >= accessory.cost
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Right side - Mascot preview
        GlassCard(
            modifier = Modifier
                .width(160.dp)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Your Saathi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

                Spacer(modifier = Modifier.height(24.dp))

                // Mascot display with equipped items
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "😊", fontSize = 80.sp)

                    // Equipped accessories around mascot
                    if ("1" in equippedItems) {
                        Text(text = "👑", fontSize = 24.sp, modifier = Modifier.offset(x = 40.dp, y = (-30).dp))
                    }
                    if ("2" in equippedItems) {
                        Text(text = "🎀", fontSize = 20.sp, modifier = Modifier.offset(x = (-40).dp, y = (-20).dp))
                    }
                    if ("3" in equippedItems) {
                        Text(text = "👓", fontSize = 24.sp, modifier = Modifier.offset(y = 30.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = if (equippedItems.isEmpty()) "No items equipped" else "${equippedItems.size} items equipped",
                    color = SoftLavender, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AccessoryCard(
    accessory: Accessory,
    isEquipped: Boolean,
    onEquip: () -> Unit,
    onUnlock: () -> Unit,
    canAfford: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isEquipped) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "accessoryScale"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isEquipped) Modifier.border(2.dp, BlushPink, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .clickable {
                    if (accessory.isUnlocked) onEquip() else if (canAfford) onUnlock()
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = accessory.emoji, fontSize = 32.sp)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = accessory.name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (isEquipped) FontWeight.Bold else FontWeight.Normal
            )

            if (!accessory.isUnlocked) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = if (canAfford) MintGreen.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${accessory.cost} pts",
                        color = if (canAfford) MintGreen else Color.Red,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
