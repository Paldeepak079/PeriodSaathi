package com.deepak.periodsaathi.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.deepak.periodsaathi.MainActivity

class CycleDayWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(DpSize(110.dp, 110.dp), DpSize(220.dp, 110.dp), DpSize(250.dp, 150.dp))
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = WidgetDataRepository(context).getCycleWidgetState()
        val openAppAction = actionForOpenCalendar(context)

        provideContent {
            val size = LocalSize.current
            val widthDp = size.width.value

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(20.dp)
                    .background(ColorProvider(WARM_CREAM))
                    .padding(8.dp)
                    .clickable(onClick = openAppAction)
            ) {
                if (state.isError) {
                    ErrorContent()
                } else if (widthDp >= 250f) {
                    LargeWidgetContent(state)
                } else if (widthDp >= 220f) {
                    MediumWidgetContent(state)
                } else {
                    SmallWidgetContent(state)
                }
            }
        }
    }

    private fun actionForOpenCalendar(context: Context): Action {
        return actionStartActivity(
            ComponentName(context, MainActivity::class.java)
        )
    }

    @Composable
    private fun ErrorContent() {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌸",
                style = TextStyle(fontSize = 28.sp)
            )
            Spacer(GlanceModifier.height(6.dp))
            Text(
                text = "Begin Your Journey",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(PRIMARY_ROSE)
                )
            )
            Text(
                text = "Tap to start tracking",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(SOFT_ROSE)
                )
            )
        }
    }

    @Composable
    private fun SmallWidgetContent(state: CycleWidgetState) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.phaseEmoji,
                style = TextStyle(fontSize = 20.sp)
            )
            Spacer(GlanceModifier.height(2.dp))
            Text(
                text = "${state.cycleDay}",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(PRIMARY_ROSE)
                )
            )
            Text(
                text = state.phaseName,
                style = TextStyle(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(LAVENDER)
                )
            )
            Spacer(GlanceModifier.height(4.dp))
            WaterIndicatorSmall(state.waterCount, state.totalWater)
        }
    }

    @Composable
    private fun MediumWidgetContent(state: CycleWidgetState) {
        Row(
            modifier = GlanceModifier.fillMaxSize().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.phaseEmoji,
                    style = TextStyle(fontSize = 22.sp)
                )
                Spacer(GlanceModifier.height(2.dp))
                Text(
                    text = "Day ${state.cycleDay}",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(PRIMARY_ROSE)
                    )
                )
                Text(
                    text = state.phaseName,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorProvider(LAVENDER)
                    )
                )
            }
            Spacer(GlanceModifier.width(12.dp))
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💧",
                    style = TextStyle(fontSize = 16.sp)
                )
                Text(
                    text = "${state.waterCount}/${state.totalWater}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(BABY_BLUE)
                    )
                )
                Text(
                    text = "glasses",
                    style = TextStyle(
                        fontSize = 9.sp,
                        color = ColorProvider(SOFT_ROSE)
                    )
                )
            }
        }
    }

    @Composable
    private fun LargeWidgetContent(state: CycleWidgetState) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.phaseEmoji,
                    style = TextStyle(fontSize = 28.sp)
                )
                Spacer(GlanceModifier.width(10.dp))
                Column {
                    Text(
                        text = "Day ${state.cycleDay}",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorProvider(PRIMARY_ROSE)
                        )
                    )
                    Text(
                        text = state.phaseName,
                        style = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(LAVENDER)
                        )
                    )
                }
            }
            Spacer(GlanceModifier.height(8.dp))
            WaterIndicatorMedium(state.waterCount, state.totalWater)
        }
    }

    @Composable
    private fun WaterIndicatorSmall(filled: Int, total: Int) {
        Row(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(total.coerceAtMost(8)) { index ->
                Text(
                    text = if (index < filled) "💧" else "·",
                    style = TextStyle(
                        fontSize = 8.sp,
                        color = ColorProvider(if (index < filled) BABY_BLUE else SOFT_ROSE)
                    )
                )
            }
        }
    }

    @Composable
    private fun WaterIndicatorMedium(filled: Int, total: Int) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💧",
                style = TextStyle(fontSize = 14.sp)
            )
            Spacer(GlanceModifier.width(6.dp))
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "$filled of $total glasses today",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = ColorProvider(LAVENDER)
                    )
                )
            }
            Text(
                text = "${(filled * 100 / total.coerceAtLeast(1))}%",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(BABY_BLUE)
                )
            )
        }
    }

    companion object {
        private val WARM_CREAM = androidx.compose.ui.graphics.Color(0xFFFFF8F2)
        private val PRIMARY_ROSE = androidx.compose.ui.graphics.Color(0xFF874E58)
        private val SOFT_ROSE = androidx.compose.ui.graphics.Color(0xFFFFB6C1)
        private val LAVENDER = androidx.compose.ui.graphics.Color(0xFF655781)
        private val BABY_BLUE = androidx.compose.ui.graphics.Color(0xFF42617D)
    }
}
