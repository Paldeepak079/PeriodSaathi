package com.deepak.periodsaathi.ui.screens.wardrobe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepak.periodsaathi.ui.components.ConfettiOverlay
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.MascotEmotion
import com.deepak.periodsaathi.ui.components.SaathiMascot
import com.deepak.periodsaathi.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(
    viewModel: WardrobeViewModel = hiltViewModel()
) {
    val totalPoints by viewModel.totalPoints.collectAsStateWithLifecycle()
    val showConfetti by viewModel.showConfetti.collectAsStateWithLifecycle()
    val themes by viewModel.themes.collectAsStateWithLifecycle()
    val seasonalAccessories by viewModel.seasonalAccessories.collectAsStateWithLifecycle()
    val accessories by viewModel.accessories.collectAsStateWithLifecycle()
    val unlockedCount = remember(accessories) { accessories.count { it.isUnlocked } }
    val wardrobePoints = remember(unlockedCount) { unlockedCount * 100 }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top bar with coin balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Wardrobe", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("\uD83E\uDE99", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "$totalPoints coins",
                            color = WarmGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Seasonal Collections title
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Saathi's Closet \uD83D\uDC57",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                Text(
                    "SEASONAL COLLECTIONS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Mascot Stats Panel
            SeasonalMascotPanel(
                wardrobePoints = wardrobePoints,
                unlockedCount = unlockedCount
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Wellness Score
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        "Wellness Score",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        "84%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Secondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.84f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFFADCDED),
                    trackColor = Color.White.copy(alpha = 0.3f),
                    gapSize = 0.dp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "You're doing great! Complete 2 more logs to reach 90%.",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Theme Unlock
            Column {
                Text(
                    "Theme Unlock",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(themes) { theme ->
                        ThemeCard(
                            theme = theme,
                            onClick = { viewModel.toggleTheme(theme.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Seasonal Collections Grid
            SeasonalCollectionsGrid(items = seasonalAccessories, viewModel = viewModel)
        }

        ConfettiOverlay(
            visible = showConfetti,
            onComplete = { viewModel.dismissConfetti() }
        )
    }
}

@Composable
private fun ThemeCard(
    theme: ThemeData,
    onClick: () -> Unit = {}
) {
    val gradients = mapOf(
        "Mint Dream" to listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB)),
        "Peach Sunset" to listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2)),
        "Rose Gold" to listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))
    )
    val gradient = gradients[theme.name] ?: listOf(Color.Gray, Color.Gray)
    val isLocked = !theme.isUnlocked

    GlassCard(
        modifier = Modifier
            .width(120.dp)
            .height(160.dp)
            .then(
                if (theme.isActive) Modifier.border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(enabled = !isLocked) { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.verticalGradient(gradient))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = theme.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
                if (theme.isActive) {
                    Text(
                        "Active",
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                } else if (isLocked) {
                    Text(
                        "${theme.cost} \uD83E\uDE99",
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            if (isLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\uD83D\uDD12", fontSize = 20.sp)
                        Text(
                            "${theme.cost}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonalMascotPanel(
    wardrobePoints: Int,
    unlockedCount: Int
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                SaathiMascot(emotion = MascotEmotion.HAPPY, size = 80.dp)
                Text(
                    "\uD83C\uDF38",
                    fontSize = 20.sp,
                    modifier = Modifier.offset(x = 30.dp, y = (-25).dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Freshly Picked",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Wardrobe Pts",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant
                        )
                        Text(
                            "%,d".format(wardrobePoints),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(OutlineVariant)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Unlocked",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant
                        )
                        Text(
                            "$unlockedCount/48",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonalCollectionsGrid(
    items: List<SeasonalAccessory>,
    viewModel: WardrobeViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val rows = items.chunked(2)
        rows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (item.state == SeasonalItemState.PLACEHOLDER) {
                            SeasonalPlaceholder()
                        } else {
                            SeasonalItemCard(
                                item = item,
                                onClick = { viewModel.toggleSeasonalAccessory(item.name) }
                            )
                        }
                    }
                }
                if (row.size < 2 && index == rows.lastIndex) {
                    Box(modifier = Modifier.weight(1f)) { Box(Modifier.fillMaxWidth()) }
                }
            }
        }
    }
}

@Composable
private fun SeasonalItemCard(
    item: SeasonalAccessory,
    onClick: () -> Unit = {}
) {
    val isActive = item.state == SeasonalItemState.ACTIVE
    val isGrayed = item.state == SeasonalItemState.LOCKED_GRAYED
    val baseAlpha = if (isGrayed) 0.5f else if (item.state == SeasonalItemState.LOCKED || item.state == SeasonalItemState.LOCKED_LIMITED) 0.75f else 1f

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(baseAlpha)
            .clickable(enabled = !isGrayed) { onClick() }
            .then(
                if (isActive) Modifier.border(2.dp, Primary, RoundedCornerShape(16.dp))
                else if (item.hasGoldGlow) Modifier.border(1.5.dp, WarmGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isActive) TertiaryContainer.copy(alpha = 0.3f)
                            else SecondaryContainer.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 36.sp)

                    if (item.badgeText != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isActive) Primary else ErrorContainer
                        ) {
                            Text(
                                item.badgeText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) OnPrimary else OnErrorContainer
                            )
                        }
                    }

                    if (item.state == SeasonalItemState.LOCKED && item.cost > 0 && item.badgeText == null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = OnSecondaryContainer.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("\u2B50", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    "${item.cost}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSecondaryContainer
                                )
                            }
                        }
                    }

                    if (isActive) {
                        Surface(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MintGreen.copy(alpha = 0.3f)
                        ) {
                            Text(
                                "\u2705 Equipped",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MintGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    item.name,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = OnSurface
                )

                Text(
                    item.subtitle,
                    fontSize = 10.sp,
                    color = if (isActive) Primary else OnSurfaceVariant
                )

                if (item.actionLabel != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val isViewQuest = item.actionLabel == "View Quest"
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = if (isViewQuest) Color.Transparent else Secondary,
                        border = if (isViewQuest) BorderStroke(1.dp, Primary) else null
                    ) {
                        Text(
                            item.actionLabel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isViewQuest) Primary else OnSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonalPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                val path = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(Offset.Zero, size),
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    )
                }
                drawPath(
                    path,
                    color = OutlineVariant,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("+", fontSize = 32.sp, color = Outline)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "More styles coming soon",
                fontSize = 11.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
