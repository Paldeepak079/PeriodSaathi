package com.deepak.periodsaathi.ui.screens.community

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumPost
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.StateFlow

private val CATEGORIES = listOf("all", "general", "cramps", "mood", "fertility", "vent", "saved")

@Composable
fun SecretChatsScreen(
    onBack: () -> Unit = {},
    viewModel: SecretChatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val posts by viewModel.allPosts.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.updateDraftImageUrl(uri.toString())
        }
    }

    val filteredPosts = remember(posts, uiState.selectedCategory, searchQuery) {
        val baseList = when (uiState.selectedCategory) {
            "all" -> posts
            "saved" -> posts.filter { it.isUpvotedByMe }
            else -> posts.filter { it.category == uiState.selectedCategory }
        }
        if (searchQuery.isBlank()) baseList
        else {
            baseList.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true) ||
                it.anonymousAlias.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Back button
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Primary)
                }

                // Profile button (Cute mascot image inside a circle with a notification red dot)
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .graphicsLayer { clip = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.deepak.periodsaathi.R.drawable.pslogo),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    // Red Notification Badge Dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .align(Alignment.TopEnd)
                            .background(Color.Red, CircleShape)
                    )
                }

                // Capsule Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search", fontSize = 12.sp, color = OnSurfaceVariant.copy(0.6f)) },
                    leadingIcon = { Icon(Icons.Rounded.Search, null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Rounded.Close, null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = OnSurface,
                        unfocusedTextColor = OnSurface
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                )

                // Bookmark button
                var isBookmarked by remember { mutableStateOf(false) }
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        isBookmarked = !isBookmarked
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Notification Bell with Badge (resets on tap)
                var unreadCount by remember { mutableStateOf(1) }
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            unreadCount = 0
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.NotificationsNone, null, tint = Primary, modifier = Modifier.size(20.dp))
                    }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$unreadCount", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(CATEGORIES) { cat ->
                    val isSelected = uiState.selectedCategory == cat
                    val scale by animateFloatAsState(
                        if (isSelected) 1.05f else 1f,
                        spring(Spring.DampingRatioMediumBouncy), label = "catScale"
                    )
                    val label = if (cat == "saved") "🔖 Saved" else cat.replaceFirstChar { it.uppercase() }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.setCategory(cat)
                        },
                        label = {
                            Text(label, style = MaterialTheme.typography.labelMedium)
                        },
                        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryContainer,
                            selectedLabelColor = Primary
                        )
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (filteredPosts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("No posts yet", fontSize = 32.sp)
                            Text("No posts yet matching selection",
                                style = MaterialTheme.typography.bodyLarge,
                                color = OnSurfaceVariant)
                            Text("Be the first to share!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant.copy(0.7f))
                        }
                    }
                } else {
                    items(filteredPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            isExpanded = uiState.expandedPostId == post.id,
                            commentDraft = if (uiState.expandedPostId == post.id) uiState.commentDraft else "",
                            onExpand = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.expandPost(post.id)
                            },
                            onUpvote = { viewModel.toggleUpvote(post) },
                            onCommentDraftChange = viewModel::updateCommentDraft,
                            onSubmitComment = { viewModel.submitComment(post.id) },
                            getComments = { viewModel.getCommentsForPost(post.id) }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !uiState.isComposingPost,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.startComposing()
                },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Edit, "New Post")
            }
        }

        AnimatedVisibility(
            visible = uiState.isComposingPost,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            ComposePostDialog(
                title = uiState.draftTitle,
                content = uiState.draftContent,
                imageUrl = uiState.draftImageUrl,
                isPosting = uiState.isPosting,
                onTitleChange = viewModel::updateDraftTitle,
                onContentChange = viewModel::updateDraftContent,
                onImageSelected = { imageLauncher.launch("image/*") },
                onClearImage = { viewModel.updateDraftImageUrl(null) },
                onSubmit = { viewModel.submitPost() },
                onDismiss = { viewModel.cancelComposing() }
            )
        }
    }
}

@Composable
private fun PostCard(
    post: ForumPost,
    isExpanded: Boolean,
    commentDraft: String,
    onExpand: () -> Unit,
    onUpvote: () -> Unit,
    onCommentDraftChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    getComments: () -> StateFlow<List<ForumComment>>
) {
    val comments by getComments().collectAsState()

    GlassCard(
        modifier = Modifier.fillMaxWidth().animateContentSize(
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape).background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(post.anonymousAlias.first().toString(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary)
                    }
                    Column {
                        Text(post.anonymousAlias,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Primary)
                        Text(formatTime(post.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant)
                    }
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(BlushPink.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(post.category, style = MaterialTheme.typography.labelSmall, color = Primary)
                }
            }

            Text(post.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = OnSurface)
            Text(post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3)

            post.imageUrl?.let { imgUri ->
                Spacer(modifier = Modifier.height(4.dp))
                UriImage(
                    uriString = imgUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val upvoteScale by animateFloatAsState(
                            if (post.isUpvotedByMe) 1.2f else 1f,
                            spring(Spring.DampingRatioHighBouncy), label = "upvScale"
                        )
                        IconButton(onClick = onUpvote, modifier = Modifier.size(32.dp)) {
                            Icon(
                                if (post.isUpvotedByMe) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                null,
                                tint = if (post.isUpvotedByMe) Color(0xFFE91E63) else OnSurfaceVariant,
                                modifier = Modifier.size(18.dp).graphicsLayer { scaleX = upvoteScale; scaleY = upvoteScale }
                            )
                        }
                        Text("${post.upvotes}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Rounded.ChatBubbleOutline, null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                        Text("${post.commentCount}", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
                TextButton(onClick = onExpand) {
                    Text(if (isExpanded) "Collapse" else "Read More",
                        style = MaterialTheme.typography.labelSmall, color = Primary)
                }
            }

            if (isExpanded) {
                HorizontalDivider(color = OutlineVariant.copy(0.5f))
                comments.forEach { comment -> CommentRow(comment = comment) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = commentDraft,
                        onValueChange = onCommentDraftChange,
                        placeholder = { Text("Add anonymous comment...", style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    IconButton(onClick = onSubmitComment, enabled = commentDraft.isNotBlank()) {
                        Icon(Icons.Rounded.Send, null,
                            tint = if (commentDraft.isNotBlank()) Primary else OnSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentRow(comment: ForumComment) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(SecondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(comment.anonymousAlias.first().toString(),
                style = MaterialTheme.typography.labelSmall, color = Primary)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(comment.anonymousAlias,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Primary)
                Text(formatTime(comment.createdAt), style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
            }
            Text(comment.content, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
    }
}

@Composable
private fun ComposePostDialog(
    title: String,
    content: String,
    imageUrl: String?,
    isPosting: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onImageSelected: () -> Unit,
    onClearImage: () -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp)
                    .navigationBarsPadding().imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New Post",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(BlushPink.copy(0.3f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("Anonymous", style = MaterialTheme.typography.labelSmall, color = Primary)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, null, tint = OnSurfaceVariant)
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Title") },
                    placeholder = { Text("What is on your mind?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    label = { Text("Content") },
                    placeholder = { Text("Share your experience, question, or vent safely...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp)
                )

                // Image preview if attached
                imageUrl?.let { imgUri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        UriImage(
                            uriString = imgUri,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = onClearImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(28.dp)
                                .background(Color.Black.copy(0.6f), CircleShape)
                        ) {
                            Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Attach Image Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onImageSelected,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryContainer)
                    ) {
                        Icon(Icons.Rounded.PhotoLibrary, "Gallery Image", tint = Primary)
                    }
                    Text(
                        text = if (imageUrl != null) "Photo attached ✓" else "Add photo from gallery",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (imageUrl != null) Primary else OnSurfaceVariant,
                        fontWeight = if (imageUrl != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                Button(
                    onClick = onSubmit,
                    enabled = title.isNotBlank() && content.isNotBlank() && !isPosting,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (isPosting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Post Anonymously", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun UriImage(
    uriString: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        try {
            val uri = android.net.Uri.parse(uriString)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Uploaded Image",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.BrokenImage, "Failed to load", tint = OnSurfaceVariant)
        }
    }
}

private fun formatTime(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000L -> "just now"
        diff < 3_600_000L -> "${diff / 60_000}m ago"
        diff < 86_400_000L -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    }
}
