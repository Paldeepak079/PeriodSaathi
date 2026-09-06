package com.deepak.periodsaathi.ui.screens.community

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.deepak.periodsaathi.data.model.ForumComment
import com.deepak.periodsaathi.data.model.ForumPost
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.StateFlow

private val CATEGORIES = listOf("all", "general", "cramps", "mood", "fertility", "vent", "saved")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretChatsScreen(
    onBack: () -> Unit = {},
    viewModel: SecretChatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadCount.collectAsStateWithLifecycle()
    val bookmarkedIds by viewModel.getBookmarkedIds().collectAsStateWithLifecycle(initialValue = emptyList())
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.updateDraftImageUrl(uri.toString())
    }

    val bookmarkedIdSet = remember(bookmarkedIds) { bookmarkedIds.toSet() }

    val filteredPosts = remember(posts, uiState.selectedCategory, searchQuery, bookmarkedIdSet) {
        val baseList = when (uiState.selectedCategory) {
            "all" -> posts
            "saved" -> posts.filter { it.id in bookmarkedIdSet }
            else -> posts.filter { it.category == uiState.selectedCategory }
        }
        if (searchQuery.isBlank()) baseList
        else baseList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.content.contains(searchQuery, ignoreCase = true) ||
            it.anonymousAlias.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top bar ──────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Primary)
                }

                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                            .background(PrimaryContainer).graphicsLayer { clip = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.deepak.periodsaathi.R.drawable.pslogo),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

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

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.toggleBookmark("")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        if (uiState.selectedCategory == "saved") Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                        null, tint = Primary, modifier = Modifier.size(20.dp)
                    )
                }

                Box(modifier = Modifier.size(36.dp), contentAlignment = Alignment.Center) {
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.toggleNotifications()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (uiState.showNotifications) Icons.Rounded.NotificationsActive
                            else Icons.Rounded.NotificationsNone,
                            null, tint = Primary, modifier = Modifier.size(20.dp)
                        )
                    }
                    if (unreadCount > 0 && !uiState.showNotifications) {
                        Box(
                            modifier = Modifier.size(14.dp).align(Alignment.TopEnd)
                                .background(Color.Red, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$unreadCount", color = Color.White,
                                fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Category chips ─────────────────────────
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
                    val label = when (cat) {
                        "saved" -> "🔖 Saved"
                        else -> cat.replaceFirstChar { it.uppercase() }
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.setCategory(cat)
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryContainer, selectedLabelColor = Primary
                        )
                    )
                }
            }

            // ── Post list ─────────────────────────────
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
                            Text("🌸", fontSize = 42.sp)
                            Text("No posts yet", fontSize = 18.sp,
                                fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("Be the first to share!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant.copy(0.7f))
                        }
                    }
                } else {
                    items(filteredPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            isBookmarked = post.id in bookmarkedIdSet,
                            isExpanded = uiState.expandedPostId == post.id,
                            commentDraft = if (uiState.expandedPostId == post.id) uiState.commentDraft else "",
                            onExpand = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.expandPost(post.id)
                            },
                            onUpvote = { viewModel.toggleUpvote(post) },
                            onBookmark = { viewModel.toggleBookmark(post.id) },
                            onImageTap = { viewModel.viewImage(it) },
                            onCommentDraftChange = viewModel::updateCommentDraft,
                            onSubmitComment = { viewModel.submitComment(post.id) },
                            getComments = { viewModel.getCommentsForPost(post.id) }
                        )
                    }
                }
            }
        }

        // ── FAB ──────────────────────────────────────
        AnimatedVisibility(
            visible = !uiState.isComposingPost && !uiState.showNotifications,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            enter = scaleIn() + fadeIn(), exit = scaleOut() + fadeOut()
        ) {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.startComposing()
                },
                containerColor = Primary, contentColor = Color.White
            ) {
                Icon(Icons.Rounded.Edit, "New Post")
            }
        }

        // ── Compose dialog ──────────────────────────
        AnimatedVisibility(
            visible = uiState.isComposingPost,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            ComposePostDialog(
                title = uiState.draftTitle, content = uiState.draftContent,
                imageUrl = uiState.draftImageUrl, isPosting = uiState.isPosting,
                onTitleChange = viewModel::updateDraftTitle,
                onContentChange = viewModel::updateDraftContent,
                onImageSelected = { imageLauncher.launch("image/*") },
                onClearImage = { viewModel.updateDraftImageUrl(null) },
                onSubmit = { viewModel.submitPost() },
                onDismiss = { viewModel.cancelComposing() }
            )
        }

        // ── Notifications sheet ──────────────────────
        if (uiState.showNotifications) {
            NotificationsSheet(
                notifications = notifications,
                onDismiss = { viewModel.dismissNotifications() },
                onMarkAllRead = { viewModel.markAllNotificationsRead() }
            )
        }

        // ── Full-screen image preview ───────────────
        uiState.viewImageUrl?.let { url ->
            FullScreenImagePreview(
                imageUrl = url,
                onDismiss = { viewModel.dismissImagePreview() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Post Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PostCard(
    post: ForumPost,
    isBookmarked: Boolean,
    isExpanded: Boolean,
    commentDraft: String,
    onExpand: () -> Unit,
    onUpvote: () -> Unit,
    onBookmark: () -> Unit,
    onImageTap: (String) -> Unit,
    onCommentDraftChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    getComments: () -> StateFlow<List<ForumComment>>
) {
    val comments by getComments().collectAsStateWithLifecycle()

    GlassCard(
        modifier = Modifier.fillMaxWidth().animateContentSize(
            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // ── Author row ───────────────────────────
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
                            style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        .background(BlushPink.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(post.category, style = MaterialTheme.typography.labelSmall, color = Primary)
                    }
                    IconButton(onClick = onBookmark, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            "Bookmark", tint = Primary, modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── Title & content ──────────────────────
            Text(post.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = OnSurface)
            Text(post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3)

            // ── Image ────────────────────────────────
            post.imageUrl?.let { imgUri ->
                Spacer(modifier = Modifier.height(4.dp))
                UriImage(
                    uriString = imgUri,
                    modifier = Modifier
                        .fillMaxWidth().height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onImageTap(imgUri) }
                )
            }

            // ── Actions ──────────────────────────────
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

            // ── Expanded comments ────────────────────
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

// ─────────────────────────────────────────────────────────────────────────────
//  Comment Row
// ─────────────────────────────────────────────────────────────────────────────
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

// ─────────────────────────────────────────────────────────────────────────────
//  Compose Post Dialog
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ComposePostDialog(
    title: String, content: String, imageUrl: String?, isPosting: Boolean,
    onTitleChange: (String) -> Unit, onContentChange: (String) -> Unit,
    onImageSelected: () -> Unit, onClearImage: () -> Unit,
    onSubmit: () -> Unit, onDismiss: () -> Unit
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
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            .background(BlushPink.copy(0.3f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("Anonymous", style = MaterialTheme.typography.labelSmall, color = Primary)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, null, tint = OnSurfaceVariant)
                        }
                    }
                }
                OutlinedTextField(value = title, onValueChange = onTitleChange,
                    label = { Text("Title") }, placeholder = { Text("What is on your mind?") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = content, onValueChange = onContentChange,
                    label = { Text("Content") }, placeholder = { Text("Share your experience, question, or vent safely...") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp), maxLines = 6,
                    shape = RoundedCornerShape(12.dp))

                imageUrl?.let { imgUri ->
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))) {
                        UriImage(uriString = imgUri, modifier = Modifier.fillMaxSize())
                        IconButton(onClick = onClearImage,
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(28.dp)
                                .background(Color.Black.copy(0.6f), CircleShape)
                        ) { Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(onClick = onImageSelected,
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryContainer)
                    ) { Icon(Icons.Rounded.PhotoLibrary, "Gallery Image", tint = Primary) }
                    Text(if (imageUrl != null) "Photo attached ✓" else "Add photo from gallery",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (imageUrl != null) Primary else OnSurfaceVariant,
                        fontWeight = if (imageUrl != null) FontWeight.SemiBold else FontWeight.Normal)
                }

                Button(onClick = onSubmit,
                    enabled = title.isNotBlank() && content.isNotBlank() && !isPosting,
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
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

// ─────────────────────────────────────────────────────────────────────────────
//  Image Composable — handles content://, file://, and HTTP URLs
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun UriImage(uriString: String, modifier: Modifier = Modifier) {
    if (uriString.startsWith("http://") || uriString.startsWith("https://")) {
        // Network image — use Coil's AsyncImage
        AsyncImage(
            model = uriString,
            contentDescription = "Image",
            modifier = modifier,
            contentScale = ContentScale.Crop,
            error = painterResource(id = android.R.drawable.ic_menu_gallery)
        )
    } else {
        // Local URI (content:// or file://)
        val context = LocalContext.current
        val bitmap = remember(uriString) {
            try {
                val uri = android.net.Uri.parse(uriString)
                if (uri.scheme == "file") {
                    // File URI — decode directly from path
                    val path = uri.path ?: return@remember null
                    BitmapFactory.decodeFile(path)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (_: Exception) { null }
        }
        if (bitmap != null) {
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "Image",
                modifier = modifier, contentScale = ContentScale.Crop)
        } else {
            Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Rounded.BrokenImage, "Failed to load", tint = OnSurfaceVariant) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Notifications Sheet
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NotificationsSheet(
    notifications: List<com.deepak.periodsaathi.data.model.ForumNotification>,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Background
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activity", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onMarkAllRead) {
                            Text("Mark all read", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Rounded.Close, null, tint = OnSurfaceVariant)
                        }
                    }
                }
                if (notifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔔", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No activity yet", fontWeight = FontWeight.Bold, color = OnSurface)
                            Text("Likes and replies will appear here",
                                fontSize = 12.sp, color = OnSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(notifications) { notif ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        if (notif.type == "upvote") "❤️" else "💬",
                                        fontSize = 20.sp
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(notif.message, fontSize = 13.sp,
                                            fontWeight = if (!notif.read) FontWeight.Bold else FontWeight.Normal,
                                            color = OnSurface)
                                        Text(formatTime(notif.createdAt), fontSize = 10.sp, color = OnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Full-Screen Image Preview with Pinch-to-Zoom
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun FullScreenImagePreview(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            UriImage(
                uriString = imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = scale; scaleY = scale
                        translationX = offsetX; translationY = offsetY
                    }
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    .background(Color.Black.copy(0.5f), CircleShape)
            ) {
                Icon(Icons.Rounded.Close, "Close", tint = Color.White)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Time format helper
// ─────────────────────────────────────────────────────────────────────────────
private fun formatTime(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000L -> "just now"
        diff < 3_600_000L -> "${diff / 60_000}m ago"
        diff < 86_400_000L -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    }
}
