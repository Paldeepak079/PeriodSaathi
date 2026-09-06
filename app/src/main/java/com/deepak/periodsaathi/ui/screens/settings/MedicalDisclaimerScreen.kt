package com.deepak.periodsaathi.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*

@Composable
fun MedicalDisclaimerScreen(onBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Primary)
                }
                Text(
                    "Medical Disclaimer",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Primary
                )
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            "Important Notice",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                    }

                    DisclaimerParagraph(
                        title = "General Wellness & Educational Purpose",
                        body = "Period Saathi is designed to provide general wellness information and educational content related to menstrual health. The app is intended for informational purposes only and should not be considered a comprehensive guide to women's health."
                    )

                    DisclaimerParagraph(
                        title = "Not Medical Advice",
                        body = "The information provided by Period Saathi — including cycle predictions, symptom tracking, health insights, remedies, AI-generated responses, and wellness suggestions — is not a substitute for professional medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional for any medical concerns."
                    )

                    DisclaimerParagraph(
                        title = "Accuracy of Information",
                        body = "While we strive to provide accurate and up-to-date information, cycle predictions, symptom analyses, health insights, remedies, AI responses, and wellness suggestions may not always be accurate or applicable to your individual situation. Predictions are based on statistical models and may vary from person to person."
                    )

                    DisclaimerParagraph(
                        title = "No Medical Diagnosis",
                        body = "Period Saathi does not provide medical diagnoses. The app does not examine, test, or diagnose any health conditions. Any health-related information displayed is for general awareness and should not be interpreted as a diagnosis."
                    )

                    DisclaimerParagraph(
                        title = "Consult a Healthcare Professional",
                        body = "If you experience severe pain, unusual symptoms, heavy bleeding, irregular cycles, or any health concerns, please consult a qualified healthcare professional immediately. In case of a medical emergency, contact your local emergency services or visit the nearest hospital."
                    )

                    DisclaimerParagraph(
                        title = "AI-Generated Content",
                        body = "Responses generated by the AI assistant within Period Saathi are based on general health information and should not be treated as personalized medical advice. AI responses may contain inaccuracies and should be verified with a healthcare professional."
                    )

                    DisclaimerParagraph(
                        title = "Limitation of Liability",
                        body = "Period Saathi and its developers are not responsible for any decisions made or actions taken based on the information provided by the app. Use of the app is at your own risk."
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DisclaimerParagraph(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = OnSurface
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}
