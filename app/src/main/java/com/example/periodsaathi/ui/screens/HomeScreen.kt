package com.example.periodsaathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.data.datastore.UserPreferences
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.components.NavItem
import com.example.periodsaathi.ui.components.PeriodSaathiBottomNavBar
import com.example.periodsaathi.ui.components.WellnessRing
import com.example.periodsaathi.ui.theme.Background
import com.example.periodsaathi.ui.theme.Error
import com.example.periodsaathi.ui.theme.GlassBorder
import com.example.periodsaathi.ui.theme.GlassWhite
import com.example.periodsaathi.ui.theme.OnErrorContainer
import com.example.periodsaathi.ui.theme.OnPrimaryContainer
import com.example.periodsaathi.ui.theme.OnSecondaryContainer
import com.example.periodsaathi.ui.theme.OnSurface
import com.example.periodsaathi.ui.theme.OnSurfaceVariant
import com.example.periodsaathi.ui.theme.OnTertiaryContainer
import com.example.periodsaathi.ui.theme.Outline
import com.example.periodsaathi.ui.theme.Primary
import com.example.periodsaathi.ui.theme.PrimaryContainer
import com.example.periodsaathi.ui.theme.Secondary
import com.example.periodsaathi.ui.theme.SecondaryContainer
import com.example.periodsaathi.ui.theme.SurfaceContainerHigh
import com.example.periodsaathi.ui.theme.Tertiary
import com.example.periodsaathi.ui.theme.TertiaryContainer
import com.example.periodsaathi.ui.theme.WarmCream

data class QuickLogItem(val label: String, val icon: String, val tint: Color)

private val quickLogs = listOf(
    QuickLogItem("Water", "💧", Tertiary),
    QuickLogItem("Meals", "🍽", Secondary),
    QuickLogItem("Medicine", "💊", Primary),
    QuickLogItem("Flow", "🩸", Error)
)

@Composable
fun HomeScreen(
    onNavigateToCalendar: () -> Unit,
    preferences: UserPreferences
) {
    val userName by preferences.userName.collectAsState(initial = "Priya")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            HomeTopBar(userName = userName)
        },
        bottomBar = {
            PeriodSaathiBottomNavBar(
                items = listOf(
                    NavItem("Home", "🏠", "home"),
                    NavItem("Calendar", "📅", "calendar"),
                    NavItem("Remedies", "🧘", "remedies"),
                    NavItem("Partner", "❤️", "partner"),
                    NavItem("Profile", "👤", "profile")
                ),
                selectedRoute = "home",
                onItemSelected = {
                    if (it.route == "calendar") onNavigateToCalendar()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Text(text = "+", fontSize = 28.sp, fontWeight = FontWeight.Light)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(WarmCream)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                GreetingSection(userName = userName)
                CentralHeroCard(userName = userName)
                QuickLogSection()
                InsightsBentoSecton()
            }
        }
    }
}

@Composable
private fun HomeTopBar(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassWhite)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌸", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Period Saathi",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable {},
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔔", fontSize = 20.sp)
        }
    }
}

@Composable
private fun GreetingSection(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "Hey $userName 🌸",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnSurface
            )
            Text(
                text = "Day 3 of your cycle",
                fontSize = 16.sp,
                color = OnSurfaceVariant
            )
        }
        Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
        ) {
            WellnessRing(percentage = 82)
            Text(
                text = "82%",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary
            )
        }
    }
}

@Composable
private fun CentralHeroCard(userName: String = "Priya") {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            MiniWeekView()
            Spacer(modifier = Modifier.height(16.dp))
            MascotWithSpeech(userName = userName)
        }
    }
}

@Composable
private fun MiniWeekView() {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val dates = listOf("12", "13", "14", "15", "16", "17", "18")
    val todayIndex = 2

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant
                )
                Box(
                    modifier = when (index) {
                        0 -> Modifier.size(32.dp).clip(CircleShape).background(Color.Transparent)
                        1 -> Modifier.size(32.dp).clip(CircleShape).background(Color.Transparent)
                        2 -> Modifier.size(32.dp).clip(CircleShape).background(Error)
                        3 -> Modifier.size(32.dp).clip(CircleShape).background(Color.Transparent)
                        4 -> Modifier.size(32.dp).clip(CircleShape).background(Color.Transparent)
                        5 -> Modifier.size(36.dp).clip(CircleShape).background(TertiaryContainer.copy(alpha = 0.3f))
                        6 -> Modifier.size(36.dp).clip(CircleShape).background(TertiaryContainer.copy(alpha = 0.3f))
                        else -> Modifier.size(32.dp).clip(CircleShape).background(Color.Transparent)
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dates[index],
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (index) {
                            2 -> Color.White
                            5, 6 -> Tertiary
                            else -> OnSurface
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MascotWithSpeech(userName: String = "Priya") {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassCard(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Hydrate yourself, $userName! 💧",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "🌸", fontSize = 80.sp)
        }
    }
}

@Composable
private fun QuickLogSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Quick Log",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(quickLogs) { item ->
                GlassCard(
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.clickable {}
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = item.icon, fontSize = 16.sp)
                        Text(
                            text = item.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsBentoSecton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GlassCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌙", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sleep",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "7h 20m",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }
        }

        GlassCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "❤️", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "BPM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "74",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            }
        }
    }
}
