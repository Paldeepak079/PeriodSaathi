package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.animation.core.*
import com.deepak.periodsaathi.domain.model.CyclePhase
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
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun PartnerInsightsScreen(
    viewModel: PartnerViewModel,
    onBack: () -> Unit
) {
    val cycleInsights by viewModel.cycleInsights.collectAsState()
    val currentDay = cycleInsights?.cycleDay ?: 22
    val currentPhase = cycleInsights?.phase ?: CyclePhase.LUTEAL
    
    val hormonePoints = remember(currentDay) {
        viewModel.getHormoneDataPoints(currentDay)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Surface,
                        WarmCream,
                        BlushPink.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("⬅️", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Cycle Insights 📈",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepRose
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Horizontal Progress Timeline
            TimelineProgressCard(currentDay = currentDay, currentPhase = currentPhase)

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Animated Hormone Graph
            HormoneGraphCard(points = hormonePoints, currentDay = currentDay)

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Explanations of Cycle Phases
            PhasesExplainerSection()

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TimelineProgressCard(currentDay: Int, currentPhase: CyclePhase) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Cycle Timeline",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepRose
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(GlassWhite, CircleShape)
            ) {
                val progressFactor = (currentDay.toFloat() / 28f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFactor)
                        .fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(BlushPink, DeepRose)), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Day 1 (Period)", fontSize = 11.sp, color = Outline)
                Text("Day $currentDay", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DeepRose)
                Text("Day 28 (Cycle End)", fontSize = 11.sp, color = Outline)
            }
        }
    }
}

@Composable
fun HormoneGraphCard(points: List<HormoneDataPoint>, currentDay: Int) {
    // Animation factor on load
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = EaseInOutCubic)
        )
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Hormone Trends",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepRose
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Estrogen (Pink) & Progesterone (Lavender) curve preview.",
                fontSize = 11.sp,
                color = Outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing for Double line graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val stepX = w / (points.size - 1)

                    val estrogenPath = Path()
                    val progesteronePath = Path()

                    points.forEachIndexed { i, p ->
                        val x = i * stepX
                        
                        // Map 0..100 levels to height
                        val yEstrogen = h - (p.estrogenLevel / 100f * h * 0.8f) - (h * 0.1f)
                        val yProgesterone = h - (p.progesteroneLevel / 100f * h * 0.8f) - (h * 0.1f)

                        // Multiply by animated progress factor to make it slide up
                        val progress = animatedProgress.value
                        val finalYEstrogen = h - ((h - yEstrogen) * progress)
                        val finalYProgesterone = h - ((h - yProgesterone) * progress)

                        if (i == 0) {
                            estrogenPath.moveTo(x, finalYEstrogen)
                            progesteronePath.moveTo(x, finalYProgesterone)
                        } else {
                            estrogenPath.lineTo(x, finalYEstrogen)
                            progesteronePath.lineTo(x, finalYProgesterone)
                        }

                        // Draw phase indicators labels along the bottom x axis
                        if (p.label.isNotEmpty()) {
                            // Custom labels can be rendered or drawn. For simplicity, we draw circles at key points
                            drawCircle(color = Outline.copy(alpha = 0.4f), radius = 3.dp.toPx(), center = androidx.compose.ui.geometry.Offset(x, h - 8.dp.toPx()))
                        }
                    }

                    // Stroke the Estrogen line
                    drawPath(
                        path = estrogenPath,
                        color = BlushPink,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Stroke the Progesterone line
                    drawPath(
                        path = progesteronePath,
                        color = Lavender,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    
                    // Draw indicator for current day
                    val dayProgress = currentDay.toFloat() / 28f
                    val needleX = dayProgress * w
                    drawLine(
                        color = DeepRose.copy(alpha = 0.5f),
                        start = androidx.compose.ui.geometry.Offset(needleX, 0f),
                        end = androidx.compose.ui.geometry.Offset(needleX, h),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).background(BlushPink, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Estrogen", fontSize = 11.sp, color = OnSurfaceVariant)
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Box(modifier = Modifier.size(10.dp).background(Lavender, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Progesterone", fontSize = 11.sp, color = OnSurfaceVariant)
            }
        }
    }
}

@Composable
fun PhasesExplainerSection() {
    var expandedPhase by remember { mutableStateOf<String?>("MENSTRUAL") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Cycle Phase Guide",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DeepRose,
            modifier = Modifier.align(Alignment.Start)
        )

        PhaseExplainCard(
            title = "Menstrual Phase 🩸",
            subtitle = "Day 1 to 5",
            description = "Estrogen and progesterone drop to their lowest. Energy is low and cramping is common. She needs extra comfort, warm teas, and plenty of understanding.",
            isExpanded = expandedPhase == "MENSTRUAL",
            onClick = { expandedPhase = if (expandedPhase == "MENSTRUAL") null else "MENSTRUAL" }
        )

        PhaseExplainCard(
            title = "Follicular Phase 🌱",
            subtitle = "Day 6 to 12",
            description = "Estrogen starts rising. Energy levels bounce back, and she feels more social and optimistic. Excellent time for outgoing activities or dates.",
            isExpanded = expandedPhase == "FOLLICULAR",
            onClick = { expandedPhase = if (expandedPhase == "FOLLICULAR") null else "FOLLICULAR" }
        )

        PhaseExplainCard(
            title = "Ovulatory Phase 🥚",
            subtitle = "Day 13 to 16",
            description = "Hormones surge to their peak, initiating ovulation. Fertility is at its highest. Confidence and social energy is soaring.",
            isExpanded = expandedPhase == "OVULATORY",
            onClick = { expandedPhase = if (expandedPhase == "OVULATORY") null else "OVULATORY" }
        )

        PhaseExplainCard(
            title = "Luteal Phase & PMS 🌙",
            subtitle = "Day 17 to 28",
            description = "Progesterone dominates. She enters recovery and nesting mode. As progesterone drops towards the end, PMS cramps or fatigue might arise. Make tea and slide heating bag nearby.",
            isExpanded = expandedPhase == "LUTEAL",
            onClick = { expandedPhase = if (expandedPhase == "LUTEAL") null else "LUTEAL" }
        )
    }
}

@Composable
fun PhaseExplainCard(
    title: String,
    subtitle: String,
    description: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DeepRose)
                    Text(text = subtitle, fontSize = 11.sp, color = Outline)
                }
                Text(if (isExpanded) "▲" else "▼", fontSize = 12.sp, color = Outline)
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = OnSurface,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
