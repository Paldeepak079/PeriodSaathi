package com.deepak.periodsaathi.ui.screens.friend

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import kotlinx.coroutines.launch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deepak.periodsaathi.data.model.FriendEntity
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.HolographicCodeBox
import com.deepak.periodsaathi.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    onBack: () -> Unit,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    val inviteCode by viewModel.inviteCode.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val isLinking by viewModel.isLinking.collectAsStateWithLifecycle()
    val linkError by viewModel.linkError.collectAsStateWithLifecycle()

    var friendCodeInput by remember { mutableStateOf(TextFieldValue("")) }
    var friendNameInput by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(linkError) {
        linkError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF7F2),
                        Color(0xFFFFF0F5),
                        Color(0xFFE8F5E9)
                    )
                )
            )
    ) {
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
            TopAppBar(
                title = {
                    Text(
                        text = "Sync Friends",
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
                // Invite Code Card
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

                            if (inviteCode.isNotBlank()) {
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
                                            clipboard.setText(androidx.compose.ui.text.AnnotatedString(inviteCode))
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
                                                    "Sync cycle phases with me on Period Saathi! Use my invite code: $inviteCode"
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
                            } else {
                                CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }

                // Add Friend By Code
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
                                text = "Enter Friend's Code",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )

                            TextField(
                                value = friendNameInput,
                                onValueChange = { friendNameInput = it },
                                placeholder = { Text("Friend's name (optional)", fontSize = 12.sp) },
                                modifier = Modifier
                                    .fillMaxWidth()
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextField(
                                    value = friendCodeInput,
                                    onValueChange = { friendCodeInput = it },
                                    placeholder = { Text("e.g. AB23CD", fontSize = 12.sp) },
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
                                            viewModel.linkFriend(
                                                code = friendCodeInput.text.trim(),
                                                name = friendNameInput.text.trim()
                                            )
                                            friendCodeInput = TextFieldValue("")
                                            friendNameInput = TextFieldValue("")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(50.dp),
                                    enabled = !isLinking && friendCodeInput.text.isNotBlank()
                                ) {
                                    if (isLinking) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Link", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Synced Friends List Header
                item {
                    Text(
                        text = "Your Synced Friends",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                if (friends.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "\uD83D\uDC6D",
                                    fontSize = 36.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No friends synced yet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OnSurface
                                )
                                Text(
                                    text = "Share your invite code or enter a friend's code to get started.",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    items(friends, key = { it.id }) { friend ->
                        FriendItemRow(
                            friend = friend,
                            onRemove = { viewModel.removeFriend(friend.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendItemRow(
    friend: FriendEntity,
    onRemove: () -> Unit
) {
    var showRemoveDialog by remember { mutableStateOf(false) }

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
                    Text(
                        text = friend.name.take(1).uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = friend.name.ifBlank { "Friend" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = friend.status,
                        fontSize = 11.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = if (friend.status == "SYNCED") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { showRemoveDialog = true }
                ) {
                    Text(
                        text = if (friend.status == "SYNCED") "Synced" else "Pending",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (friend.status == "SYNCED") Color(0xFF2E7D32) else Color(0xFFEF6C00),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (showRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveDialog = false },
            title = { Text("Remove Friend") },
            text = { Text("Remove ${friend.name.ifBlank { "this friend" }} from your synced list?") },
            confirmButton = {
                TextButton(onClick = {
                    showRemoveDialog = false
                    onRemove()
                }) {
                    Text("Remove", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
