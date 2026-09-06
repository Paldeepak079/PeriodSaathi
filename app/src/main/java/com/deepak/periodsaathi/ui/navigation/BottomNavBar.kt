package com.deepak.periodsaathi.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.deepak.periodsaathi.ui.theme.BlushPink
import com.deepak.periodsaathi.ui.theme.WarmCream
import kotlin.math.roundToInt

data class NavTab(
    val label: String,
    val icon: ImageVector,
    val route: Any,
    val contentDescription: String
)

val bottomNavTabs = listOf(
    NavTab("Home", Icons.Rounded.Home, Home, "Home tab"),
    NavTab("Calendar", Icons.Rounded.CalendarMonth, Calendar, "Calendar tab"),
    NavTab("Wellness", Icons.Rounded.Favorite, Wellness, "Wellness tab"),
    NavTab("Secret Chats", Icons.Rounded.Lock, Community, "Secret Chats tab"),
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val activeTabIndex = bottomNavTabs.indexOfFirst { tab ->
        tab.route::class.qualifiedName == currentRoute
    }.coerceAtLeast(0)

    var indicatorOffsetPx by remember { mutableFloatStateOf(0f) }
    var indicatorWidthPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    NavigationBar(
        containerColor = WarmCream.copy(alpha = 0.92f),
        tonalElevation = 4.dp,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Sliding indicator — animates via spring on offset change
            val animatedOffsetX by androidx.compose.animation.core.animateFloatAsState(
                targetValue = indicatorOffsetPx,
                animationSpec = spring(dampingRatio = 0.6f),
                label = "indicatorOffsetX"
            )

            if (indicatorWidthPx > 0f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                        .width(with(density) { indicatorWidthPx.toDp() })
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BlushPink)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavTabs.forEachIndexed { index, tab ->
                    val isActive = index == activeTabIndex
                    val iconScale by animateFloatAsState(
                        targetValue = if (isActive) 1.15f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.6f),
                        label = "iconScale"
                    )
                    val iconAlpha = if (isActive) 1.0f else 0.4f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .semantics {
                                this.contentDescription = tab.contentDescription
                                role = Role.Tab
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                navController.navigate(tab.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            }
                            .onGloballyPositioned { coordinates ->
                                val pos = coordinates.positionInParent()
                                val size = coordinates.size
                                if (index == activeTabIndex) {
                                    indicatorOffsetPx = pos.x
                                    indicatorWidthPx = size.width.toFloat()
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(26.dp)) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isActive) BlushPink else Color(0xFF2D2D2D).copy(alpha = 0.4f),
                                modifier = Modifier
                                    .size(26.dp)
                                    .alpha(iconAlpha)
                            )
                        }
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            color = if (isActive) BlushPink else Color(0xFF2D2D2D).copy(alpha = 0.5f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
