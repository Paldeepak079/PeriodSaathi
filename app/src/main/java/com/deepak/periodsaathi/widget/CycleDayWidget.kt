package com.deepak.periodsaathi.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.deepak.periodsaathi.MainActivity
import com.deepak.periodsaathi.R

class CycleDayWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(DpSize(110.dp, 110.dp), DpSize(220.dp, 110.dp))
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = WidgetDataRepository(context).getCycleWidgetState()
        val openAppAction = actionForOpenApp(context)

        provideContent {
            val size = LocalSize.current

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFF8F5)))
                    .padding(8.dp)
                    .clickable(onClick = openAppAction)
            ) {
                if (state.isError) {
                    ErrorWidgetContent()
                } else if (size.width >= 220.dp) {
                    MediumWidgetContent(state)
                } else {
                    SmallWidgetContent(state)
                }
            }
        }
    }

    private fun actionForOpenApp(context: Context): Action {
        return actionStartActivity(
            ComponentName(context, MainActivity::class.java)
        )
    }

    @Composable
    private fun ErrorWidgetContent() {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌸",
                style = TextStyle(fontSize = 24.sp)
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "No data yet",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF9E9E9E))
                )
            )
            Text(
                text = "Tap to start",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFFBDBDBD))
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
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = "Saathi",
                modifier = GlanceModifier.size(36.dp)
            )
            Spacer(GlanceModifier.height(4.dp))
            Text(
                text = "Day ${state.cycleDay}",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF3D2C35))
                )
            )
            Text(
                text = state.phaseEmoji,
                style = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "\uD83D\uDCA7 ${state.waterCount}/${state.totalWater}",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF6B5B95))
                )
            )
        }
    }

    @Composable
    private fun MediumWidgetContent(state: CycleWidgetState) {
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = "Saathi",
                modifier = GlanceModifier.size(48.dp)
            )
            Spacer(GlanceModifier.width(12.dp))
            Column {
                Text(
                    text = "Day ${state.cycleDay}",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF3D2C35))
                    )
                )
                Text(
                    text = "${state.phaseEmoji} ${state.phaseName}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF6B5B95))
                    )
                )
                Text(
                    text = "\uD83D\uDCA7 ${state.waterCount} of ${state.totalWater} glasses",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF6B5B95))
                    )
                )
            }
        }
    }
}
