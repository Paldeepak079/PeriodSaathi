package com.deepak.periodsaathi.ui.screens.payment

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.theme.*
import com.razorpay.Checkout

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val paymentState by viewModel.paymentState.collectAsState()
    val activePurchases by viewModel.activePurchases.collectAsState()
    val activity = LocalContext.current as? Activity

    var currentProductId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(text = "Go Premium", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Unlock all features", color = OnSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Current Plan", color = OnSurfaceVariant, fontSize = 12.sp)
                            Text(text = "Free", fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
            }

            items(viewModel.products) { product ->
                val isOwned = product.id in activePurchases
                ProductCard(
                    product = product,
                    isOwned = isOwned,
                    onPurchase = {
                        val options = viewModel.initiatePurchase(product.id)
                        if (options != null && activity != null) {
                            currentProductId = product.id
                            Checkout().open(activity, options)
                        }
                    },
                    isLoading = paymentState is PaymentState.Loading && currentProductId == product.id
                )
            }

            item {
                TextButton(
                    onClick = { viewModel.restorePurchases() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already purchased? Restore", color = OnSurfaceVariant)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        when (val state = paymentState) {
            is PaymentState.Success -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(modifier = Modifier.padding(32.dp), shape = RoundedCornerShape(24.dp)) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Purchase Successful!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Welcome to Premium!", color = OnSurfaceVariant)
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
            is PaymentState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(modifier = Modifier.padding(32.dp), shape = RoundedCornerShape(24.dp)) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = state.message, color = Error, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.resetState() },
                                colors = ButtonDefaults.buttonColors(containerColor = BlushPink)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun ProductCard(product: Product, isOwned: Boolean, onPurchase: () -> Unit, isLoading: Boolean) {
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
                    Text(text = product.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = OnSurface)
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, color = OnSurfaceVariant, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOwned) MintGreen else if (product.isBestValue) WarmGold else BlushPink,
                    contentColor = if (isOwned) Color.Black else Color.White
                ),
                enabled = !isLoading && !isOwned,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when {
                        isOwned -> "Owned"
                        isLoading -> "Processing..."
                        else -> "Buy Now"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
