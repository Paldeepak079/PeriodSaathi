package com.deepak.periodsaathi.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.deepak.periodsaathi.MainActivity

class PeriodCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = WidgetDataRepository(context).getCountdownState()
        val openAppAction = actionForOpenHome(context)

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(24.dp)
                    .background(ColorProvider(GLASS_BG))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clickable(onClick = openAppAction),
                contentAlignment = Alignment.CenterStart
            ) {
                if (state.isError) {
                    EmptyState()
                } else {
                    CountdownContent(state)
                }
            }
        }
    }

    private fun actionForOpenHome(context: Context): Action {
        return actionStartActivity(
            ComponentName(context, MainActivity::class.java)
        )
    }

    @Composable
    private fun EmptyState() {
        Column {
            Text(
                text = "🌸 Start tracking your cycle",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(PRIMARY_ROSE)
                ),
                maxLines = 1
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = "Tap to begin your journey",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(SOFT_ROSE)
                ),
                maxLines = 1
            )
        }
    }

    @Composable
    private fun CountdownContent(state: CountdownState) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                if (state.isInPeriod) {
                    Text(
                        text = "🌸 On Period",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(PRIMARY_ROSE)
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "Take care of yourself today",
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = ColorProvider(LAVENDER)
                        ),
                        maxLines = 1
                    )
                } else if (state.daysUntilPeriod != null) {
                    Text(
                        text = "Next period in ${state.daysUntilPeriod} days",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(PRIMARY_ROSE)
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = cyclePhaseHint(state.daysUntilPeriod),
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = ColorProvider(LAVENDER)
                        ),
                        maxLines = 1
                    )
                } else {
                    Text(
                        text = "🌸 Track your first cycle",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(PRIMARY_ROSE)
                        ),
                        maxLines = 1
                    )
                }
            }
            if (state.daysUntilPeriod != null && !state.isInPeriod) {
                Spacer(GlanceModifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${state.daysUntilPeriod}",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(PRIMARY_ROSE)
                        )
                    )
                    Text(
                        text = "days",
                        style = TextStyle(
                            fontSize = 9.sp,
                            color = ColorProvider(LAVENDER)
                        )
                    )
                }
            }
        }
    }

    private fun cyclePhaseHint(daysLeft: Int): String = when {
        daysLeft <= 3 -> "Almost there — be prepared"
        daysLeft <= 7 -> "Your luteal phase"
        daysLeft <= 14 -> "Mid-cycle — you're glowing"
        else -> "Fresh follicular phase"
    }

    companion object {
        private val GLASS_BG = androidx.compose.ui.graphics.Color(0x55FFFFFF)
        private val PRIMARY_ROSE = androidx.compose.ui.graphics.Color(0xFF874E58)
        private val SOFT_ROSE = androidx.compose.ui.graphics.Color(0xFFFFB6C1)
        private val LAVENDER = androidx.compose.ui.graphics.Color(0xFF655781)
    }
}
