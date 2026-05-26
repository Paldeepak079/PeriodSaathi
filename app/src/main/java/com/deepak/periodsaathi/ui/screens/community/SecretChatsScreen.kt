package com.deepak.periodsaathi.ui.screens.community

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
//  Secret Chats Screen  (formerly "Community")
//  An anonymous, glassmorphic community space for period health discussions.
//  No usernames are shown — only anonymous flower avatars.
// ─────────────────────────────────────────────────────────────────────────────

private data class ChatChannel(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val memberCount: Int,
    val lastMessage: String,
    val isJoined: Boolean = false
)

private data class ChatMessage(
    val id: String,
    val text: String,
    val avatarEmoji: String,
    val timeAgo: String,
    val likes: Int = 0,
    val isLiked: Boolean = false
)

private val channels = listOf(
    ChatChannel("1", "PMS Struggles 😤", "😤", "Vent, get support, share tips", 2341, "Anyone else bloated for days?"),
    ChatChannel("2", "Cycle Syncing ✨", "✨", "Optimize your life to your cycle", 1823, "Follicular phase workout ideas?"),
    ChatChannel("3", "Fertility & TTC 🤰", "🤰", "Trying to conceive — stories & support", 3102, "BFP this month!! So happy 💕"),
    ChatChannel("4", "PCOS Warriors 💪", "💪", "PCOS management, stories, research", 4511, "Inositol has been life-changing"),
    ChatChannel("5", "Pregnancy Support 👶", "👶", "Pregnancy tracking & community", 2890, "Second trimester is so much better"),
    ChatChannel("6", "Period Pain SOS 🆘", "🆘", "Dysmenorrhea & cramp relief", 1654, "Heat pad + ginger tea saved me"),
    ChatChannel("7", "Mental Health & Hormones 🧠", "🧠", "Emotional cycles, PMDD, anxiety", 3277, "Tracking my mood changed everything"),
    ChatChannel("8", "Ask a Question 🙋", "🙋", "No such thing as a stupid question", 5812, "Is this discharge normal?"),
)

private val sampleMessages = listOf(
    ChatMessage("m1", "Has anyone tried cycle syncing their workouts? It's literally changed my life 🌙", "🌸", "2 min ago", 23),
    ChatMessage("m2", "PCOS girlies — I started spearmint tea and my acne improved SO much after 3 weeks!", "🌺", "5 min ago", 47),
    ChatMessage("m3", "Reminder that your cycle is NOT always 28 days and that's completely normal 💕", "🌻", "8 min ago", 89),
    ChatMessage("m4", "Day 3 of my period and I feel like a melted candle. Is this normal? 😭", "🌷", "12 min ago", 34),
    ChatMessage("m5", "Finally got my BFP after 14 months TTC. Don't give up, it DOES happen 🙏", "🌹", "15 min ago", 201),
    ChatMessage("m6", "Anyone else's PMS make them feel like a completely different person?", "💐", "18 min ago", 67),
    ChatMessage("m7", "Luteal phase hit different this month. Anyone else feel this tired?", "🌸", "22 min ago", 42),
)

@Composable
fun SecretChatsScreen() {
    var selectedChannel by remember { mutableStateOf<ChatChannel?>(null) }
    var showNewPost by remember { mutableStateOf(false) }

    // Animated background
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val bgAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f, targetValue = 0.22f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOut), RepeatMode.Reverse),
        label = "bgAlpha"
    )

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        // Soft animated blob
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-40).dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(listOf(Primary.copy(alpha = bgAlpha), Color.Transparent)),
                    CircleShape
                )
                .blur(60.dp)
        )

        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🔒 Secret Chats",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold),
                        color = Primary
                    )
                    Text(
                        text = "Anonymous. Safe. Just us. 💕",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
                // New post button
                FilledIconButton(
                    onClick = { showNewPost = true },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = "New post", tint = OnPrimary)
                }
            }

            // ── Privacy notice chip ───────────────────────────────────────
            Surface(
                color = PrimaryContainer.copy(alpha = 0.6f),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Lock, null, tint = Primary, modifier = Modifier.size(14.dp))
                    Text(
                        "Completely anonymous • No usernames • No real names",
                        style = MaterialTheme.typography.labelSmall,
                        color = Primary
                    )
                }
            }

            // ── Channel chips ─────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(channels) { channel ->
                    val isSelected = selectedChannel?.id == channel.id
                    Surface(
                        onClick = { selectedChannel = if (isSelected) null else channel },
                        shape = RoundedCornerShape(50.dp),
                        color = if (isSelected) Primary else PrimaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.border(
                            1.dp,
                            if (isSelected) Primary else OutlineVariant,
                            RoundedCornerShape(50.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(channel.emoji, fontSize = 14.sp)
                            Text(
                                channel.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) OnPrimary else OnSurface
                            )
                        }
                    }
                }
            }

            // ── Feed ──────────────────────────────────────────────────────
            val displayChannel = selectedChannel
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = rememberLazyListState()
            ) {
                // Channel header if selected
                if (displayChannel != null) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(displayChannel.emoji, fontSize = 28.sp)
                                    Column {
                                        Text(displayChannel.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Primary)
                                        Text("${displayChannel.memberCount.toLocaleString()} members",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = OnSurfaceVariant)
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(displayChannel.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant)
                            }
                        }
                    }
                }

                items(sampleMessages) { msg ->
                    SecretChatMessageCard(message = msg)
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }

        // ── New post bottom sheet ─────────────────────────────────────────
        if (showNewPost) {
            NewPostSheet(
                onDismiss = { showNewPost = false }
            )
        }
    }
}

@Composable
private fun SecretChatMessageCard(message: ChatMessage) {
    var liked by remember { mutableStateOf(message.isLiked) }
    var likeCount by remember { mutableIntStateOf(message.likes) }
    val scale by animateFloatAsState(
        targetValue = if (liked) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Anonymous flower avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(message.avatarEmoji, fontSize = 20.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Anonymous Saathi",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Primary
                    )
                    Text(message.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant)
                }
                // Lock badge
                Surface(
                    color = PrimaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Lock, null, tint = Primary, modifier = Modifier.size(10.dp))
                        Text("Private", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = Primary)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Message text
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like
                Row(
                    modifier = Modifier.clickable {
                        liked = !liked
                        likeCount = if (liked) likeCount + 1 else likeCount - 1
                    },
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        null,
                        tint = if (liked) Primary else OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "$likeCount",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (liked) Primary else OnSurfaceVariant
                    )
                }
                // Reply
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.ChatBubbleOutline, null,
                        tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                    Text("Reply",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant)
                }
                Spacer(Modifier.weight(1f))
                // Hug
                Surface(
                    color = PrimaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50.dp),
                    onClick = {}
                ) {
                    Text("🤗 Send Hug", fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = Primary)
                }
            }
        }
    }
}

@Composable
private fun NewPostSheet(onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf(channels.first()) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                // Handle
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)
                    .width(40.dp).height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OutlineVariant))
                Spacer(Modifier.height(20.dp))
                Text("Share Anonymously 🌸",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Primary)
                Text("No one will know it's you",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("What's on your mind? 💕", color = OnSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50.dp)
                    ) { Text("Cancel") }
                    Button(
                        onClick = {
                            scope.launch {
                                delay(200)
                                onDismiss()
                            }
                        },
                        enabled = text.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.weight(2f)
                    ) { Text("Post Anonymously 🌸", color = OnPrimary) }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

private fun Int.toLocaleString(): String {
    return if (this >= 1000) "${this / 1000}.${(this % 1000) / 100}k" else this.toString()
}
