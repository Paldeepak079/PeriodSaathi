package com.example.periodsaathi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.periodsaathi.data.datastore.UserPreferences
import com.example.periodsaathi.ui.theme.Background
import com.example.periodsaathi.ui.theme.GlassBorder
import com.example.periodsaathi.ui.theme.GlassWhite
import com.example.periodsaathi.ui.theme.OnSurface
import com.example.periodsaathi.ui.theme.OnSurfaceVariant
import com.example.periodsaathi.ui.theme.Outline
import com.example.periodsaathi.ui.theme.OutlineVariant
import com.example.periodsaathi.ui.theme.Primary
import com.example.periodsaathi.ui.theme.PrimaryContainer
import com.example.periodsaathi.ui.theme.WarmCream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGuestMode: () -> Unit,
    preferences: UserPreferences
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        WarmCream,
                        PrimaryContainer.copy(alpha = 0.15f),
                        Background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Period Saathi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(top = 32.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .blur(50.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer.copy(alpha = 0.3f))
                    )
                    Card(
                        modifier = Modifier.size(180.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = GlassWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🌸",
                                fontSize = 72.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Welcome to Period Saathi",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your empathetic cycle companion.",
                    fontSize = 18.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            preferences.setLoggedIn("Priya")
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GlassWhite,
                        contentColor = OnSurface
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "G",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4285F4)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = OutlineVariant
                    )
                    Text(
                        text = "or explore as guest",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Outline,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = OutlineVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            preferences.setGuestMode()
                            onGuestMode()
                        }
                    }
                ) {
                    Text(
                        text = "Continue as Guest →",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "By continuing, you agree to our",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Row {
                    Text(
                        text = "Terms",
                        fontSize = 12.sp,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        text = " & ",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Privacy Policy",
                        fontSize = 12.sp,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}
