package com.example.periodsaathi.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.periodsaathi.ui.components.GlassCard
import com.example.periodsaathi.ui.theme.*

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val paymentState by viewModel.paymentState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A0E2E), Color(0xFF1A1228))))
            .padding(16.dp)
    ) {
        Text(text = "Go Premium 🌟", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Unlock all features", color = SoftLavender)

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                // Current plan card
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "👑", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Current Plan", color = SoftLavender, fontSize = 12.sp)
                            Text(text = "Free", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            items(viewModel.products) { product ->
                ProductCard(
                    product = product,
                    onPurchase = { viewModel.initiatePurchase(product.id) },
                    isLoading = paymentState is PaymentState.Loading
                )
            }

            item {
                // Restore purchases
                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already purchased? Restore", color = SoftLavender)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Success overlay
        if (paymentState is PaymentState.Success) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(modifier = Modifier.padding(32.dp), shape = RoundedCornerShape(24.dp)) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🎉", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Purchase Successful!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Welcome to Premium!", color = SoftLavender)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.resetState() },
                            colors = ButtonDefaults.buttonColors(containerColor = BlushPink)
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onPurchase: () -> Unit, isLoading: Boolean) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (product.isBestValue) Modifier.border(2.dp, WarmGold, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (product.isBestValue) {
                        Surface(color = WarmGold, shape = RoundedCornerShape(4.dp)) {
                            Text(
                                text = "Best Value",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(text = product.price, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BlushPink)
            }

            Spacer(modifier = Modifier.height(12.dp))

            product.features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✓", color = MintGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, color = SoftLavender, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (product.isBestValue) WarmGold else BlushPink,
                    contentColor = if (product.isBestValue) Color.Black else Color.White
                ),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = if (isLoading) "Processing..." else "Buy Now", fontWeight = FontWeight.Bold)
            }
        }
    }
}