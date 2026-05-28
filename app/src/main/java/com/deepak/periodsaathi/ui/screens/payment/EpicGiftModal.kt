package com.deepak.periodsaathi.ui.screens.payment

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.deepak.periodsaathi.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class BoxTier { STANDARD, RARE, EPIC }

@Composable
fun EpicGiftModal(
    onSubscribe: (tier: String, price: String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentTier by remember { mutableStateOf<BoxTier?>(null) }
    var openedStandard by remember { mutableStateOf(false) }
    var openedRare by remember { mutableStateOf(false) }
    var openedEpic by remember { mutableStateOf(false) }
    var showParticles by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            if (showParticles) ParticleBurst()
            Surface(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                color = Background
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Unlock Your Gift",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Primary)
                    Text("Tap each box to reveal your exclusive Period Saathi deal",
                        style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        GiftBox(isOpened = openedStandard, label = "Standard", color = BabyBlue, enabled = true,
                            onClick = { if (!openedStandard) { haptic.performHapticFeedback(HapticFeedbackType.LongPress); openedStandard = true; currentTier = BoxTier.STANDARD } })
                        GiftBox(isOpened = openedRare, label = "Rare", color = SoftLavender, enabled = openedStandard,
                            onClick = { if (!openedRare && openedStandard) { haptic.performHapticFeedback(HapticFeedbackType.LongPress); openedRare = true; currentTier = BoxTier.RARE } })
                        GiftBox(isOpened = openedEpic, label = "EPIC!", color = WarmGold, enabled = openedRare,
                            onClick = { if (!openedEpic && openedRare) { haptic.performHapticFeedback(HapticFeedbackType.LongPress); openedEpic = true; currentTier = BoxTier.EPIC; showParticles = true } })
                    }
                    AnimatedContent(targetState = currentTier,
                        transitionSpec = { slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut() },
                        label = "tierOffer"
                    ) { tier ->
                        when (tier) {
                            BoxTier.STANDARD -> TierOffer(title = "Standard Plan", originalPrice = "599", discountedPrice = "499", discount = "17% OFF", color = BabyBlue, period = "per year", onSelect = { onSubscribe("standard", "499") })
                            BoxTier.RARE -> TierOffer(title = "Rare Plan", originalPrice = "999", discountedPrice = "699", discount = "30% OFF", color = SoftLavender, period = "per year", onSelect = { onSubscribe("rare", "699") })
                            BoxTier.EPIC -> TierOffer(title = "EPIC Plan - Best Deal!", originalPrice = "1499", discountedPrice = "899", discount = "40% OFF", color = WarmGold, period = "per year - All Features", highlight = true, onSelect = { onSubscribe("epic", "899") })
                            null -> Box(modifier = Modifier.height(80.dp), contentAlignment = Alignment.Center) {
                                Text("Tap a box to reveal your deal!", color = OnSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Maybe later", color = OnSurfaceVariant, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun GiftBox(isOpened: Boolean, label: String, color: Color, enabled: Boolean = true, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (isOpened) 1.15f else if (enabled) 1f else 0.85f, spring(Spring.DampingRatioMediumBouncy), label = "boxScale")
    val rotation by animateFloatAsState(if (isOpened) 15f else 0f, spring(Spring.DampingRatioHighBouncy), label = "boxRot")
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(enabled = enabled && !isOpened, onClick = onClick)) {
        Box(
            modifier = Modifier.size(72.dp).scale(scale).graphicsLayer { rotationZ = rotation }
                .clip(RoundedCornerShape(16.dp))
                .background(if (isOpened) color else color.copy(alpha = if (enabled) 0.4f else 0.2f)),
            contentAlignment = Alignment.Center
        ) { Text(if (isOpened) "OK" else "Box", fontSize = 24.sp) }
        Text(if (isOpened) "Opened" else label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isOpened) color else if (enabled) OnSurface else OnSurfaceVariant.copy(0.5f))
    }
}

@Composable
private fun TierOffer(title: String, originalPrice: String, discountedPrice: String, discount: String, color: Color, period: String, highlight: Boolean = false, onSelect: () -> Unit) {
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = if (highlight) 0.2f else 0.1f), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Primary)
                Box(modifier = Modifier.clip(CircleShape).background(color).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text(discount, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                }
            }
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Rs $originalPrice", style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough), color = OnSurfaceVariant)
                Text("Rs $discountedPrice", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold), color = Primary)
                Text(period, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
            Button(onClick = onSelect, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = color)) {
                Text("Unlock This Plan", fontWeight = FontWeight.SemiBold, color = if (highlight) OnSurface else Color.White)
            }
        }
    }
}

@Composable
private fun ParticleBurst() {
    val particles = remember { List(30) { Triple(Random.nextFloat() * 360f, Random.nextFloat() * 180f + 60f, listOf(BlushPink, SoftLavender, WarmGold, BabyBlue, SoftCoral).random()) } }
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val progress by infiniteTransition.animateFloat(initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1500), repeatMode = RepeatMode.Restart), label = "pp")
    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { (angle, distance, color) ->
            val rad = Math.toRadians(angle.toDouble())
            val x = (cos(rad) * distance * progress).toFloat()
            val y = (sin(rad) * distance * progress).toFloat()
            Box(modifier = Modifier.offset(x = x.dp, y = y.dp).align(Alignment.Center)
                .size((8f * (1f - progress)).dp).clip(CircleShape)
                .graphicsLayer { alpha = 1f - progress }.background(color))
        }
    }
}
