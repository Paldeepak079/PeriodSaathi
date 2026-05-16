package com.example.periodsaathi.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.ui.theme.GlassWhite
import com.example.periodsaathi.ui.theme.OnSurfaceVariant
import com.example.periodsaathi.ui.theme.Primary

@Composable
fun BottomNavBar(
    currentRoute: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavScreen(Screen.Home, "Home", "🏠"),
        BottomNavScreen(Screen.Calendar, "Calendar", "📅"),
        BottomNavScreen(Screen.Wellness, "Wellness", "🧘"),
        BottomNavScreen(Screen.More, "More", "👤")
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = GlassWhite,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute::class == item.screen::class

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onNavigate(item.screen) }
                ) {
                    Text(
                        text = item.icon,
                        fontSize = 22.sp
                    )
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Primary else OnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private data class BottomNavScreen(
    val screen: Screen,
    val label: String,
    val icon: String
)