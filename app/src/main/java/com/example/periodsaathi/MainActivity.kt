package com.example.periodsaathi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.periodsaathi.navigation.PeriodSaathiNavGraph
import com.example.periodsaathi.ui.theme.PeriodSaathiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PeriodSaathiTheme {
                PeriodSaathiNavGraph(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}