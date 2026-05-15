package com.example.periodsaathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.data.database.PeriodSaathiDatabase
import com.example.periodsaathi.data.datastore.UserPreferences
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.components.NavItem
import com.example.periodsaathi.ui.components.PeriodSaathiBottomNavBar
import com.example.periodsaathi.ui.theme.Error
import com.example.periodsaathi.ui.theme.GlassWhite
import com.example.periodsaathi.ui.theme.OnSurface
import com.example.periodsaathi.ui.theme.OnSurfaceVariant
import com.example.periodsaathi.ui.theme.Primary
import com.example.periodsaathi.ui.theme.PrimaryContainer
import com.example.periodsaathi.ui.theme.Secondary
import com.example.periodsaathi.ui.theme.Tertiary
import com.example.periodsaathi.ui.theme.TertiaryContainer
import com.example.periodsaathi.ui.theme.WarmCream

data class DayData(
    val day: Int,
    val type: DayType = DayType.Normal,
    val isCurrentMonth: Boolean = true
)

enum class DayType {
    Normal, Period, Fertile, PredictedPeriod, OtherMonth
}

@Composable
fun CalendarScreen(
    onNavigateToHome: () -> Unit,
    preferences: UserPreferences
) {
    var currentMonth by remember { mutableIntStateOf(3) }
    var currentYear by remember { mutableIntStateOf(2024) }
    var goal by remember { mutableStateOf("baby") }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val daysInMonth = getDaysInMonth(currentMonth, currentYear)
    val firstDayOfWeek = getFirstDayOfWeek(currentMonth, currentYear)
    val periodDays = intArrayOf(1, 2, 3, 4, 5)
    val fertileDays = intArrayOf(14, 15, 16, 17, 18)
    val predictedDays = intArrayOf(28, 29, 30, 31)

    val calendarDays = buildCalendarDays(daysInMonth, firstDayOfWeek, periodDays, fertileDays, predictedDays)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            CalendarTopBar()
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
                selectedRoute = "calendar",
                onItemSelected = {
                    if (it.route == "home") onNavigateToHome()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.size(64.dp, 64.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "+", fontSize = 24.sp, fontWeight = FontWeight.Light)
                    Text(text = "Log", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                GoalToggle(goal = goal, onGoalChange = { goal = it })

                MonthNavigation(
                    monthLabel = "${monthNames[currentMonth - 1]} $currentYear",
                    onPrev = {
                        if (currentMonth == 1) {
                            currentMonth = 12; currentYear--
                        } else currentMonth--
                    },
                    onNext = {
                        if (currentMonth == 12) {
                            currentMonth = 1; currentYear++
                        } else currentMonth++
                    }
                )

                CalendarGrid(days = calendarDays)

                CycleHealthCard()
            }
        }
    }
}

@Composable
private fun CalendarTopBar() {
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
                    .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape),
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
private fun GoalToggle(goal: String, onGoalChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        GlassCard(
            shape = RoundedCornerShape(50),
            modifier = Modifier.clip(RoundedCornerShape(50))
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (goal == "baby") Color.White else Color.Transparent)
                        .clickable { onGoalChange("baby") }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Planning Baby 🌱",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (goal == "baby") Primary else OnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (goal == "avoid") Color.White else Color.Transparent)
                        .clickable { onGoalChange("avoid") }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Avoiding 🧡",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (goal == "avoid") Primary else OnSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthNavigation(monthLabel: String, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthLabel,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassCard(
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onPrev() }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "◀", fontSize = 16.sp, color = Primary)
                }
            }
            GlassCard(
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNext() }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "▶", fontSize = 16.sp, color = Primary)
                }
            }
        }
    }
}

@Composable
private fun CalendarGrid(days: List<DayData?>) {
    val dayHeaders = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier.weight(1f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val rows = days.chunked(7)
            rows.forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day != null) {
                                val bgColor = when (day.type) {
                                    DayType.Period -> Brush.verticalGradient(
                                        listOf(Color(0xFFFFD9DE), Color(0xFFFCB3BE))
                                    )
                                    DayType.Fertile -> Brush.verticalGradient(
                                        listOf(Color(0xFFCDE5FF), Color(0xFFAACAE9))
                                    )
                                    else -> null
                                }

                                val borderMod = if (day.type == DayType.PredictedPeriod) {
                                    Modifier.border(
                                        2.dp,
                                        PrimaryContainer.copy(alpha = 0.7f),
                                        CircleShape
                                    )
                                } else Modifier

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .then(
                                            if (bgColor != null) Modifier.background(
                                                bgColor,
                                                CircleShape
                                            ) else Modifier
                                        )
                                        .then(borderMod)
                                        .clip(CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${day.day}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = when (day.type) {
                                            DayType.Period -> Color.White
                                            DayType.Fertile -> Tertiary
                                            DayType.PredictedPeriod -> OnSurface
                                            else -> OnSurface
                                        }
                                    )
                                }

                                if (day.type == DayType.Fertile) {
                                    Text(
                                        text = "🌿",
                                        fontSize = 10.sp,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CycleHealthCard() {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌸", fontSize = 24.sp)
                }
                Column {
                    Text(
                        text = "Cycle Health",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Secondary
                    )
                    Text(
                        text = "Your pattern looks normal and consistent.",
                        fontSize = 16.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "NEXT PERIOD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "28 Mar",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "FERTILE IN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "13 Days",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Tertiary
                        )
                    }
                }
            }
        }
    }
}

private fun getDaysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

private fun getFirstDayOfWeek(month: Int, year: Int): Int {
    var m = (month - 3) % 12
    if (m < 0) m += 12
    val y = if (month < 3) year - 1 else year
    val c = y / 100
    val ye = y % 100
    return ((m + 1) * 26 / 10 + 6 + ye + ye / 4 + c / 4 - 2 * c) % 7
}

private fun buildCalendarDays(
    daysInMonth: Int,
    firstDayOfWeek: Int,
    periodDays: IntArray,
    fertileDays: IntArray,
    predictedDays: IntArray
): List<DayData?> {
    val days = mutableListOf<DayData?>()

    for (i in 0 until firstDayOfWeek) {
        val prevMonthDay = daysInMonth - firstDayOfWeek + 1 + i
        days.add(DayData(prevMonthDay, DayType.OtherMonth, false))
    }

    for (day in 1..daysInMonth) {
        val type = when (day) {
            in periodDays -> DayType.Period
            in fertileDays -> DayType.Fertile
            in predictedDays -> DayType.PredictedPeriod
            else -> DayType.Normal
        }
        days.add(DayData(day, type, true))
    }

    return days
}
