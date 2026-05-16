package com.example.periodsaathi.ui.screens.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun WardrobeScreen(viewModel: WardrobeViewModel = hiltViewModel()) {
    val accessories by viewModel.accessories.collectAsStateWithLifecycle()
    val points by viewModel.points.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "Saathi's Wardrobe 👗", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💎 Wardrobe Points", fontWeight = FontWeight.Bold, color = OnSurface)
                    Text(text = "$points pts", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ButterYellow)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Accessories", fontWeight = FontWeight.Bold, color = OnSurface)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(3), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(accessories) { accessory ->
                    AccessoryCard(accessory = accessory, onEquip = { viewModel.equip(accessory.id) }, onUnlock = { viewModel.unlock(accessory.id) })
                }
            }
        }
    }
}

@Composable
private fun AccessoryCard(accessory: Accessory, onEquip: () -> Unit, onUnlock: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth().then(if (accessory.isEquipped) Modifier.border(2.dp, Primary, RoundedCornerShape(28.dp)) else Modifier)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = accessory.emoji, fontSize = 32.sp)
            Text(text = accessory.name, fontSize = 10.sp, color = OnSurface)
            if (!accessory.isUnlocked) {
                Text(text = "${accessory.cost} pts", fontSize = 10.sp, color = OnSurfaceVariant)
            }
        }
    }
}