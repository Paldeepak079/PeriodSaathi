package com.deepak.periodsaathi.ui.screens.phasecoach

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepak.periodsaathi.domain.model.CyclePhase
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.Background
import com.deepak.periodsaathi.ui.theme.BlushPink
import com.deepak.periodsaathi.ui.theme.ChipShape
import com.deepak.periodsaathi.ui.theme.OnPrimaryContainer
import com.deepak.periodsaathi.ui.theme.OnSurface
import com.deepak.periodsaathi.ui.theme.OnSurfaceVariant
import com.deepak.periodsaathi.ui.theme.Primary
import com.deepak.periodsaathi.ui.theme.PrimaryContainer

@Composable
fun PhaseCoachScreen(
    viewModel: PhaseCoachViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentPhase by viewModel.currentPhase.collectAsStateWithLifecycle()
    val cycleDay by viewModel.cycleDay.collectAsStateWithLifecycle()
    val currentTipIndex by viewModel.currentTipIndex.collectAsStateWithLifecycle()
    val favoritedTips by viewModel.favoritedTips.collectAsStateWithLifecycle()

    val phaseHeadline = viewModel.getPhaseHeadline(currentPhase)
    val phaseEmoji = viewModel.getPhaseEmoji(currentPhase)
    val phaseDescription = viewModel.getPhaseDescription(currentPhase, cycleDay)
    val superpower = viewModel.getSuperpower(currentPhase)
    val actions = viewModel.getPhaseActions(currentPhase)
    val tips = viewModel.getTipsForPhase(currentPhase)
    val currentTip = tips.getOrNull(currentTipIndex)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        PhaseCoachMeshBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Phase Tabs
            item {
                PhaseTabs(
                    phases = viewModel.allPhases,
                    selectedPhase = currentPhase,
                    onPhaseSelected = { phase -> viewModel.selectPhase(phase) },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
                )
            }

            // Phase Coach Card
            item {
                PhaseCoachCard(
                    headline = "$phaseHeadline $phaseEmoji",
                    description = phaseDescription,
                    actions = actions,
                    superpowerTitle = superpower.first,
                    superpowerDescription = superpower.second,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Daily Tip section
            item {
                DailyTipSection(
                    tip = currentTip,
                    tipIndex = currentTipIndex,
                    totalTips = tips.size,
                    isFavorited = currentTipIndex in favoritedTips,
                    onPrevious = { viewModel.previousTip() },
                    onNext = { viewModel.nextTip() },
                    onDismiss = { viewModel.dismissTip(currentTipIndex) },
                    onToggleFavorite = { viewModel.toggleFavorite(currentTipIndex) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

            // Activity card
            item {
                ActivityCard(
                    onClick = {
                        Toast
                            .makeText(context, "🧘 Activity suggestion coming soon", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun PhaseCoachMeshBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-60).dp)
                .size(280.dp)
                .background(
                    Brush.radialGradient(listOf(PrimaryContainer.copy(0.4f), Color.Transparent)),
                    CircleShape
                )
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 60.dp)
                .size(200.dp)
                .background(
                    Brush.radialGradient(listOf(BlushPink.copy(0.3f), Color.Transparent)),
                    CircleShape
                )
                .blur(50.dp)
        )
    }
}

@Composable
private fun PhaseTabs(
    phases: List<CyclePhase>,
    selectedPhase: CyclePhase,
    onPhaseSelected: (CyclePhase) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(phases) { phase ->
            val isSelected = phase == selectedPhase
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryContainer else Color.Transparent,
                animationSpec = spring(dampingRatio = 0.6f),
                label = "tabBg"
            )
            val textColor = if (isSelected) OnPrimaryContainer else OnSurfaceVariant

            Surface(
                shape = ChipShape,
                color = bgColor,
                modifier = Modifier
                    .height(40.dp)
                    .clickable { onPhaseSelected(phase) }
            ) {
                Text(
                    text = phase.displayName,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun PhaseCoachCard(
    headline: String,
    description: String,
    actions: List<Triple<String, String, String>>,
    superpowerTitle: String,
    superpowerDescription: String,
    modifier: Modifier = Modifier
) {
    val chipCtx = LocalContext.current
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Decorative element
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(listOf(PrimaryContainer.copy(0.3f), Color.Transparent)),
                        CircleShape
                    )
                    .blur(20.dp)
            )

            Text(
                text = headline,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                ),
                color = Primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                actions.forEach { (label, emoji, _) ->
                    ActionChip(
                        label = "$emoji $label",
                        onClick = {
                            Toast.makeText(chipCtx, "$emoji $label — noted!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Superpower callout
            SuperpowerCard(
                title = superpowerTitle,
                description = superpowerDescription
            )
        }
    }
}

@Composable
private fun ActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryContainer.copy(alpha = 0.3f)),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun SuperpowerCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Primary.copy(alpha = 0.06f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.15f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Superpower",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Primary.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$title \u2728",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailyTipSection(
    tip: DailyTip?,
    tipIndex: Int,
    totalTips: Int,
    isFavorited: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Tip",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = OnSurface
            )
            Text(
                text = "${tipIndex + 1}/$totalTips",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tip != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Stacked underlay card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = 4.dp, y = 4.dp)
                        .matchParentSize()
                ) {}

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = tip.emoji, fontSize = 22.sp)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tip.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = tip.body,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Navigation row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Prev / Next buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    IconButton(onClick = onPrevious) {
                                        Text("\u2716", color = Primary, fontSize = 14.sp)
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    IconButton(onClick = onDismiss) {
                                        Text("\u2718", color = Primary, fontSize = 14.sp)
                                    }
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = if (isFavorited) Primary else Color.Transparent,
                                border = if (isFavorited) null else androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = onToggleFavorite) {
                                    Text(
                                        "\u2764",
                                        color = if (isFavorited) Color.White else Primary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dots indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(totalTips) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (index == tipIndex) 20.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(if (index == tipIndex) Primary else Color(0xFFE0E0E0))
                )
            }
        }
    }
}

@Composable
private fun ActivityCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF1F0),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Primary.copy(alpha = 0.25f),
                                Primary.copy(alpha = 0.55f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier
                ) {
                    Text(
                        text = "ACTIVITY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gentle Pelvic Stretches",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PhaseCoachScreenPreview() {
    MaterialTheme {
        PhaseCoachScreen()
    }
}
