package com.example.periodsaathi.widget

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
import com.example.periodsaathi.R

class CycleDayWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(DpSize(110.dp, 110.dp), DpSize(220.dp, 110.dp))
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val size = LocalSize.current

            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFF8F5)))
                    .padding(8.dp)
            ) {
                if (size.width >= 220.dp) {
                    MediumWidgetContent()
                } else {
                    SmallWidgetContent()
                }
            }
        }
    }

    @Composable
    private fun SmallWidgetContent() {
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
                text = "Day 14",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF3D2C35))
                )
            )
            Text(
                text = "🌱",
                style = TextStyle(fontSize = 16.sp)
            )
            Text(
                text = "💧 4/8",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF6B5B95))
                )
            )
        }
    }

    @Composable
    private fun MediumWidgetContent() {
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
                    text = "Day 14",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF3D2C35))
                    )
                )
                Text(
                    text = "🌱 Follicular",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF6B5B95))
                    )
                )
                Text(
                    text = "💧 4 of 8 glasses",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF6B5B95))
                    )
                )
            }
        }
    }
}