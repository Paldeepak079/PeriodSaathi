package com.example.periodsaathi.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.periodsaathi.ui.components.*
import com.example.periodsaathi.ui.theme.*

@Composable
fun PaymentScreen(viewModel: PaymentViewModel = hiltViewModel()) {
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text(text = "Go Premium 🌟", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Primary) }

            item { GlassCard(modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Current Plan: Free", color = OnSurfaceVariant)
                Text(text = "Upgrade for exclusive features!", color = OnSurface)
            }}}

            items(viewModel.products) { product ->
                GlassCard(modifier = Modifier.fillMaxWidth().then(
                    if (product.isBestValue) Modifier.border(2.dp, ButterYellow, RoundedCornerShape(28.dp)) else Modifier
                )) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(text = product.name, fontWeight = FontWeight.Bold, color = OnSurface)
                                product.features.forEach { Text(text = "• $it", fontSize = 12.sp, color = OnSurfaceVariant) }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = product.price, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
                                if (product.isBestValue) { Text(text = "BEST VALUE", fontSize = 10.sp, color = ButterYellow) }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        SpringBounceButton(text = "Buy Now", onClick = { viewModel.initiatePurchase(product.id) }, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}