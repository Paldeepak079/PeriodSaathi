package com.deepak.periodsaathi.ui.screens.partner

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.deepak.periodsaathi.ui.theme.*

enum class PartnerSubScreen {
    ROLE_SELECTION,
    INVITE,
    JOIN,
    DASHBOARD,
    INSIGHTS,
    QUIZZES,
    QUIZ_DETAIL,
    SETTINGS
}

@Composable
fun PartnerModeScreen(
    viewModel: PartnerViewModel = hiltViewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()
    
    // Internal navigation state to orchestrate all partner sub-screens cleanly
    var currentSubScreen by remember { mutableStateOf(PartnerSubScreen.ROLE_SELECTION) }
    var selectedQuizId by remember { mutableStateOf("") }

    // Synchronize current sub-screen based on Room database connection status in real-time
    LaunchedEffect(connectionState) {
        when (connectionState) {
            is ConnectionUIState.Connected -> {
                val state = connectionState as ConnectionUIState.Connected
                if (state.isPrimary) {
                    currentSubScreen = PartnerSubScreen.SETTINGS
                } else {
                    currentSubScreen = PartnerSubScreen.DASHBOARD
                }
            }
            is ConnectionUIState.ActiveInvite -> {
                currentSubScreen = PartnerSubScreen.INVITE
            }
            else -> {
                // If revoked or idle and not actively choosing JOIN/INVITE, reset to selection
                if (currentSubScreen != PartnerSubScreen.JOIN && currentSubScreen != PartnerSubScreen.INVITE) {
                    currentSubScreen = PartnerSubScreen.ROLE_SELECTION
                }
            }
        }
    }

    AnimatedContent(
        targetState = currentSubScreen,
        transitionSpec = {
            fadeIn(spring()) togetherWith fadeOut(spring())
        },
        label = "partnerScreenTransition"
    ) { subScreen ->
        when (subScreen) {
            PartnerSubScreen.ROLE_SELECTION -> {
                RoleSelectionGatewayView(
                    onSelectPrimary = { currentSubScreen = PartnerSubScreen.INVITE },
                    onSelectPartner = { currentSubScreen = PartnerSubScreen.JOIN }
                )
            }
            
            PartnerSubScreen.INVITE -> {
                PartnerInviteScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = PartnerSubScreen.ROLE_SELECTION },
                    onNavigateToDashboard = { currentSubScreen = PartnerSubScreen.SETTINGS }
                )
            }

            PartnerSubScreen.JOIN -> {
                PartnerJoinScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = PartnerSubScreen.ROLE_SELECTION },
                    onNavigateToDashboard = { currentSubScreen = PartnerSubScreen.DASHBOARD }
                )
            }

            PartnerSubScreen.DASHBOARD -> {
                PartnerDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToInsights = { currentSubScreen = PartnerSubScreen.INSIGHTS },
                    onNavigateToQuizzes = { currentSubScreen = PartnerSubScreen.QUIZZES },
                    onNavigateToSettings = { currentSubScreen = PartnerSubScreen.SETTINGS }
                )
            }

            PartnerSubScreen.INSIGHTS -> {
                PartnerInsightsScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = PartnerSubScreen.DASHBOARD }
                )
            }

            PartnerSubScreen.QUIZZES -> {
                PartnerQuizScreen(
                    viewModel = viewModel,
                    onBack = { currentSubScreen = PartnerSubScreen.DASHBOARD },
                    onNavigateToQuizDetail = { quizId ->
                        selectedQuizId = quizId
                        currentSubScreen = PartnerSubScreen.QUIZ_DETAIL
                    }
                )
            }

            PartnerSubScreen.QUIZ_DETAIL -> {
                PartnerQuizDetailScreen(
                    quizId = selectedQuizId,
                    viewModel = viewModel,
                    onBack = { currentSubScreen = PartnerSubScreen.QUIZZES }
                )
            }

            PartnerSubScreen.SETTINGS -> {
                PartnerSettingsScreen(
                    viewModel = viewModel,
                    onBack = {
                        val state = connectionState
                        if (state is ConnectionUIState.Connected && !state.isPrimary) {
                            currentSubScreen = PartnerSubScreen.DASHBOARD
                        } else {
                            // Primary settings can close back to selection if revoked, else stays
                            currentSubScreen = PartnerSubScreen.ROLE_SELECTION
                        }
                    },
                    onNavigateToInvite = {
                        currentSubScreen = PartnerSubScreen.ROLE_SELECTION
                    }
                )
            }
        }
    }
}

@Composable
fun RoleSelectionGatewayView(
    onSelectPrimary: () -> Unit,
    onSelectPartner: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Surface, WarmCream, SoftLavender.copy(alpha = 0.3f))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Partner Mode 💕",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DeepRose,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A beautiful space for couples to share read-only cycle predictions, fertile days, and couples' communication quizzes.",
                fontSize = 14.sp,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Option 1: Primary Tracker (Invite Partner)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { onSelectPrimary() },
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(BlushPink.copy(alpha = 0.1f), Color.Transparent)))
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("I am tracking my cycle 🌸", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = DeepRose)
                        Text("➔", fontSize = 18.sp, color = DeepRose)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Generate a secure 6-digit invitation code to share cycle days, fertile predictions, and daily support suggestions with your partner.",
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Option 2: Supportive Partner (Enter Code)
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { onSelectPartner() },
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(listOf(SoftLavender.copy(alpha = 0.1f), Color.Transparent)))
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("I am the partner 🤝", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = OnSecondaryContainer)
                        Text("➔", fontSize = 18.sp, color = OnSecondaryContainer)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter a 6-digit sync code shared by your partner to connect, view predictions, and take couples quizzes to improve text harmony.",
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
