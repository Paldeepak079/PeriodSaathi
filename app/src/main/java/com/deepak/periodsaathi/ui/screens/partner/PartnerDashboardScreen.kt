package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.data.repository.PartnerCycleInsights
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PartnerDashboardScreen(
    viewModel: PartnerViewModel,
    onNavigateToInsights: () -> Unit,
    onNavigateToQuizzes: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val cycleInsights by viewModel.cycleInsights.collectAsStateWithLifecycle()
    val dailyTip by viewModel.dailyTip.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()

    val partnerName = when (val state = connectionState) {
        is ConnectionUIState.Connected -> state.partnerName
        else -> "Deepak"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        SoftLavender.copy(alpha = 0.3f),
                        WarmCream
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Dashboard Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$partnerName's View 👁️",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepRose
                    )
                    
                    val syncTime = cycleInsights?.lastSyncTime ?: System.currentTimeMillis()
                    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
                    val timeStr = Instant.ofEpochMilli(syncTime)
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime()
                        .format(formatter)

                    Text(
                        text = "Last synced today at $timeStr",
                        fontSize = 11.sp,
                        color = Outline
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable { onNavigateToSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚙️", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Today's Status Main Bento Card
            cycleInsights?.let { insights ->
                TodayStatusCard(insights = insights)
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DeepRose)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Growing Bud-to-Bloom Ovulation Card
            cycleInsights?.let { insights ->
                OvulationBloomCard(daysAway = insights.ovulationDaysAway)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conception Chance Gauge Card
            cycleInsights?.let { insights ->
                ConceptionGaugeCard(chance = insights.conceptionChance)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Typewriter Support Tip
            SupportTipCard(tip = dailyTip)

            Spacer(modifier = Modifier.height(16.dp))

            // Read-Only Calendar Preview Matrix
            MiniPreviewCalendarCard()

            Spacer(modifier = Modifier.height(24.dp))

            // Feature Navigation Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToInsights,
                    colors = ButtonDefaults.buttonColors(containerColor = SoftLavender),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("Detailed Insights 📈", color = OnSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onNavigateToQuizzes,
                    colors = ButtonDefaults.buttonColors(containerColor = DeepRose),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("Couples Quizzes 💖", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TodayStatusCard(insights: PartnerCycleInsights) {
    val phaseColor = when (insights.phase) {
        CyclePhase.MENSTRUAL -> SoftCoral
        CyclePhase.FOLLICULAR -> MintGreen
        CyclePhase.OVULATORY, CyclePhase.OVULATION -> ButterYellow
        else -> SoftLavender
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(phaseColor)
                )
                Text(
                    text = "Current Status",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Day ${insights.cycleDay}",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepRose
            )

            Text(
                text = "${insights.phase.displayName} Phase ${insights.phase.emoji}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = GlassBorder, thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Period Predicted", fontSize = 11.sp, color = Outline)
                    Text("In ${insights.expectedPeriodDaysAway} Days 🩸", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(35.dp)
                        .background(GlassBorder)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Conception Chance", fontSize = 11.sp, color = Outline)
                    Text("${insights.conceptionChance} 🔥", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
            }
        }
    }
}

@Composable
fun OvulationBloomCard(daysAway: Int) {
    // Bloom scale goes from 0.1f (Bud) to 1f (Bloom)
    // If daysAway is 0, full bloom (1f)
    // If daysAway is > 10, bud (0.1f)
    val bloomFactor = (1f - (daysAway.toFloat() / 10f)).coerceIn(0.15f, 1f)

    // Animate bloom on load
    val animatedBloom = remember { Animatable(0f) }
    LaunchedEffect(bloomFactor) {
        animatedBloom.animateTo(
            targetValue = bloomFactor,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1.2f)
            ) {
                Text(
                    text = "Fertility Bloom 🌸",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
                Spacer(modifier = Modifier.height(6.dp))
                val description = when {
                    daysAway == 0 -> "Ovulation is today! The fertility garden is in full, radiant bloom."
                    daysAway <= 3 -> "Ovulation is in $daysAway days. The flower is opening up beautifully."
                    else -> "Ovulation predicted in $daysAway days. The bud is safely sleeping and preparing to grow."
                }
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
            
            // Flower Canvas drawing
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .height(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    // 1. Draw Stem
                    val stemPath = Path().apply {
                        moveTo(w / 2, h)
                        quadraticTo(w / 2 - 15.dp.toPx(), h / 2 + 10.dp.toPx(), w / 2, h / 2 - 10.dp.toPx())
                    }
                    drawPath(path = stemPath, color = MintGreen, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))

                    // 2. Draw Leaf
                    drawOval(
                        color = MintGreen,
                        topLeft = androidx.compose.ui.geometry.Offset(w / 2 - 22.dp.toPx(), h / 2 + 12.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(15.dp.toPx(), 8.dp.toPx())
                    )

                    // 3. Draw Bloom petals based on animated factor
                    val bloomScale = animatedBloom.value
                    val centerOffset = androidx.compose.ui.geometry.Offset(w / 2, h / 2 - 15.dp.toPx())
                    val radius = 18.dp.toPx() * bloomScale

                    // Draw outer petals
                    drawCircle(color = BlushPink.copy(alpha = 0.8f), radius = radius, center = centerOffset)
                    drawCircle(color = SoftCoral.copy(alpha = 0.9f), radius = radius * 0.7f, center = centerOffset.copy(x = centerOffset.x - 4.dp.toPx()))
                    drawCircle(color = SoftCoral.copy(alpha = 0.9f), radius = radius * 0.7f, center = centerOffset.copy(x = centerOffset.x + 4.dp.toPx()))
                    
                    // Core Bud
                    drawCircle(color = DeepRose, radius = radius * 0.4f, center = centerOffset)
                }
            }
        }
    }
}

@Composable
fun ConceptionGaugeCard(chance: String) {
    val targetSweep = when (chance) {
        "High" -> 150f
        "Medium" -> 90f
        else -> 30f
    }
    
    val animatedSweep = remember { Animatable(0f) }
    LaunchedEffect(targetSweep) {
        animatedSweep.animateTo(
            targetValue = targetSweep,
            animationSpec = tween(1200, easing = EaseOutBack)
        )
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .weight(0.8f),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    // Arc meter path
                    drawArc(
                        color = SoftLavender,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(5.dp.toPx(), 10.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(w - 10.dp.toPx(), h * 1.5f),
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Swept arc indicator color
                    val arcColor = when (chance) {
                        "High" -> SoftCoral
                        "Medium" -> ButterYellow
                        else -> MintGreen
                    }
                    drawArc(
                        color = arcColor,
                        startAngle = 180f,
                        sweepAngle = animatedSweep.value,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(5.dp.toPx(), 10.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(w - 10.dp.toPx(), h * 1.5f),
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Needle pivot circle
                    drawCircle(color = DeepRose, radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w / 2, h - 5.dp.toPx()))
                }
            }

            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = "Conception Window 🔥",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your daily chance of conceiving is currently $chance.",
                    fontSize = 13.sp,
                    color = OnSurface
                )
            }
        }
    }
}

@Composable
fun SupportTipCard(tip: String) {
    var displayedText by remember { mutableStateOf("") }
    
    // Typewriter effect trigger on tip changes
    LaunchedEffect(tip) {
        displayedText = ""
        tip.forEachIndexed { index, _ ->
            displayedText = tip.substring(0, index + 1)
            delay(40)
        }
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Partner Support Tip 💡",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepRose
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = displayedText,
                fontSize = 14.sp,
                color = OnSurface,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MiniPreviewCalendarCard() {
    val today = LocalDate.now()
    val daysInMonth = today.lengthOfMonth()
    
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Calendar Preview (View-Only) 📅",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepRose
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Render a 7-column matrix for dates
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val dates = (1..daysInMonth).toList()
                val chunkedDates = dates.chunked(7)
                chunkedDates.forEach { rowDates ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowDates.forEach { dayNumber ->
                            // Simple cycle simulation mapping
                            val isPeriod = dayNumber in 1..5
                            val isFertile = dayNumber in 11..16
                            
                            val cellBg = when {
                                isPeriod -> BlushPink.copy(alpha = 0.5f)
                                isFertile -> MintGreen.copy(alpha = 0.5f)
                                else -> GlassWhite
                            }
                            
                            val cellBorder = when {
                                dayNumber == today.dayOfMonth -> DeepRose
                                else -> Color.Transparent
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(cellBg, RoundedCornerShape(8.dp))
                                    .border(if (cellBorder != Color.Transparent) 1.5.dp else 0.dp, cellBorder, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (dayNumber == today.dayOfMonth) FontWeight.ExtraBold else FontWeight.Normal,
                                    color = OnSurface
                                )
                            }
                        }
                        // Fill extra spacing if last row has less than 7 items
                        if (rowDates.size < 7) {
                            for (j in 0 until (7 - rowDates.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(BlushPink.copy(alpha = 0.7f), RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Predicted Period", fontSize = 11.sp, color = OnSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(MintGreen.copy(alpha = 0.7f), RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fertile Window", fontSize = 11.sp, color = OnSurfaceVariant)
                }
            }
        }
    }
}
