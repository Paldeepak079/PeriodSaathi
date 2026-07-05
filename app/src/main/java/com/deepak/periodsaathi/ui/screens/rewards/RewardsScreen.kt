package com.deepak.periodsaathi.ui.screens.rewards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.PrimaryButton
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun RewardsScreen(
    onNavigateToWardrobe: () -> Unit,
    viewModel: RewardsViewModel = hiltViewModel()
) {
    val totalPoints by viewModel.totalPoints.collectAsState()
    val themes by viewModel.themes.collectAsState()
    val accessories by viewModel.accessories.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val claimingId by viewModel.claimingId.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Rewards Shop", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)
            GlassCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "\u2B50", fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$totalPoints",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmGold
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Spend your points to unlock themes, accessories, and badges!",
            fontSize = 12.sp,
            color = OnSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (themes.isNotEmpty()) {
                item {
                    Text(text = "\uD83C\uDFA8 Themes", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Spacer(Modifier.height(10.dp))
                    RewardRow(
                        items = themes,
                        claimingId = claimingId,
                        onClaim = { viewModel.claimReward(it.reward) }
                    )
                }
            }
            if (accessories.isNotEmpty()) {
                item {
                    Text(text = "\uD83D\uDC8D Mascot Accessories", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Spacer(Modifier.height(10.dp))
                    RewardRow(
                        items = accessories,
                        claimingId = claimingId,
                        onClaim = { viewModel.claimReward(it.reward) }
                    )
                }
            }
            if (badges.isNotEmpty()) {
                item {
                    Text(text = "\uD83C\uDFC6 Badges", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Spacer(Modifier.height(10.dp))
                    RewardRow(
                        items = badges,
                        claimingId = claimingId,
                        onClaim = { viewModel.claimReward(it.reward) }
                    )
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    text = "View My Wardrobe",
                    onClick = onNavigateToWardrobe,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun RewardRow(
    items: List<RewardItem>,
    claimingId: String?,
    onClaim: (RewardItem) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items, key = { it.reward.id }) { item ->
            RewardCard(
                item = item,
                isClaiming = claimingId == item.reward.id,
                onClaim = { onClaim(item) }
            )
        }
    }
}

@Composable
private fun RewardCard(
    item: RewardItem,
    isClaiming: Boolean,
    onClaim: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (item.unlocked) Color(0xFFE8F5E9) else Color.White,
        animationSpec = tween(300), label = "bg"
    )

    GlassCard(
        modifier = Modifier
            .width(160.dp)
            .then(
                if (item.unlocked) Modifier.border(1.dp, SoftCoral.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (item.unlocked) SoftCoral.copy(alpha = 0.15f) else SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.reward.emoji, fontSize = 26.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.reward.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))

            if (item.unlocked) {
                Text(text = "\u2705 Owned", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
            } else {
                Text(
                    text = "${item.reward.pointsRequired} pts",
                    fontSize = 12.sp,
                    color = WarmGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                PrimaryButton(
                    text = if (isClaiming) "..." else "Claim",
                    onClick = onClaim,
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    enabled = !isClaiming
                )
            }
        }
    }
}
