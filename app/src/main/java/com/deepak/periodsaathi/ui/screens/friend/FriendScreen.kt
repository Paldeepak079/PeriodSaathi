package com.deepak.periodsaathi.ui.screens.friend

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.HolographicCodeBox
import com.deepak.periodsaathi.ui.theme.*

data class SyncedFriend(
    val id: String,
    val name: String,
    val phase: String,
    val cycleDay: Int,
    val totalDays: Int,
    val status: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current

    val inviteCode = "SAATHI" // Stable shimmery code

    var friendCodeInput by remember { mutableStateOf(TextFieldValue("")) }
    var isSubmitting by remember { mutableStateOf(false) }
    var mockFriendsList by remember {
        mutableStateOf(
            listOf(
                SyncedFriend("1", "Aanya", "Luteal Phase", 16, 28, "Synced", "🥑"),
                SyncedFriend("2", "Priya", "Menstrual Phase", 2, 28, "Synced", "🌸"),
                SyncedFriend("3", "Sarah", "Follicular Phase", 8, 30, "Synced", "⚡"),
                SyncedFriend("4", "Riya", "PMS Phase", 26, 28, "Awaiting Sync", "🌙")
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF7F2), // Warm Cream
                        Color(0xFFFFF0F5), // Lavender-blush
                        Color(0xFFE8F5E9)  // Gentle Mint Green tone
                    )
                )
            )
    ) {
        // Shimmering branding blobs for depth
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-40).dp, y = 120.dp)
                .size(260.dp)
                .background(Brush.radialGradient(listOf(Color(0x20FFB5C8), Color.Transparent)), CircleShape)
                .blur(70.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = (-120).dp)
                .size(280.dp)
                .background(Brush.radialGradient(listOf(Color(0x18C9B8FF), Color.Transparent)), CircleShape)
                .blur(70.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // ── Top App Bar ──────────────────────────────────────────────────
            TopAppBar(
                title = {
                    Text(
                        text = "Sync Friends 👭",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            HorizontalDivider(color = OutlineVariant.copy(0.4f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // ── Shimmery Holographic Invite Code Card ─────────────────────
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Invite a Friend to Sync",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Synchronize cycle phases with your sisterhood or close friends to support and coordinate self-care together!",
                                fontSize = 12.sp,
                                color = OnSurfaceVariant,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            // shimmery holographic code block
                            HolographicCodeBox(
                                code = inviteCode,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        clipboard.setText(AnnotatedString(inviteCode))
                                        Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Copy Code", fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Sync cycle phases with me on Period Saathi! 🌸 Use my invite code: $inviteCode"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Share Code via"))
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryContainer)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = OnPrimaryContainer)
                                }
                            }
                        }
                    }
                }

                // ── Add Friend By Code ─────────────────────────────────────────
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Enter Friend's Code 🔗",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextField(
                                    value = friendCodeInput,
                                    onValueChange = { friendCodeInput = it },
                                    placeholder = { Text("e.g. LUNA25", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(0.4f))
                                        .border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        if (friendCodeInput.text.isNotBlank()) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            isSubmitting = true
                                            Toast.makeText(context, "Linking Friend...", Toast.LENGTH_SHORT).show()
                                            // Simulate successful link
                                            mockFriendsList = mockFriendsList + SyncedFriend(
                                                id = (mockFriendsList.size + 1).toString(),
                                                name = friendCodeInput.text.trim().uppercase(),
                                                phase = "Follicular Phase",
                                                cycleDay = 5,
                                                totalDays = 28,
                                                status = "Synced",
                                                emoji = "⚡"
                                            )
                                            friendCodeInput = TextFieldValue("")
                                            isSubmitting = false
                                            Toast.makeText(context, "Successfully Synced!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(50.dp),
                                    enabled = !isSubmitting && friendCodeInput.text.isNotBlank()
                                ) {
                                    Text("Link", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ── Synced Sisterhood List ──────────────────────────────────────
                item {
                    Text(
                        text = "Your Synced Sisterhood 🌸",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                items(mockFriendsList) { friend ->
                    FriendItemRow(friend = friend)
                }
            }
        }
    }
}

@Composable
fun FriendItemRow(friend: SyncedFriend) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(friend.emoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = friend.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = friend.phase,
                        fontSize = 11.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (friend.status == "Synced") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = friend.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (friend.status == "Synced") Color(0xFF2E7D32) else Color(0xFFEF6C00),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Day ${friend.cycleDay}/${friend.totalDays}",
                    fontSize = 10.sp,
                    color = OnSurfaceVariant.copy(0.7f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
