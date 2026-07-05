package com.deepak.periodsaathi.widget

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.deepak.periodsaathi.MainActivity

class PeriodCountdownWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val state = WidgetDataRepository(context).getCountdownState()
        val openAppAction = actionForOpenApp(context)

        provideContent {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFF8F5)))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clickable(onClick = openAppAction),
                contentAlignment = Alignment.CenterStart
            ) {
                if (state.isError) {
                    Column {
                        Text(
                            text = "\uD83C\uDF38 Start tracking your cycle",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF9E9E9E))
                            ),
                            maxLines = 1
                        )
                        Spacer(GlanceModifier.height(2.dp))
                        Text(
                            text = "Tap to begin",
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = ColorProvider(androidx.compose.ui.graphics.Color(0xFFBDBDBD))
                            ),
                            maxLines = 1
                        )
                    }
                } else {
                    Text(
                        text = if (state.isInPeriod) "\uD83C\uDF38 You're on your period"
                              else if (state.daysUntilPeriod != null) "\uD83C\uDF38 Next period in ${state.daysUntilPeriod} days"
                              else "\uD83C\uDF38 Start tracking your cycle",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF3D2C35))
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }

    private fun actionForOpenApp(context: Context): Action {
        return actionStartActivity(
            ComponentName(context, MainActivity::class.java)
        )
    }
}
