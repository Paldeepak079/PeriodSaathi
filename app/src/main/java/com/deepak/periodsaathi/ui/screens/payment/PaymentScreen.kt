package com.deepak.periodsaathi.ui.screens.payment

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.deepak.periodsaathi.ui.components.GlassCard
import com.deepak.periodsaathi.ui.components.ConfettiOverlay
import com.deepak.periodsaathi.ui.theme.*
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    onBack: () -> Unit = {},
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val paymentState by viewModel.paymentState.collectAsState()
    val activePurchases by viewModel.activePurchases.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var showMockCardDialog by remember { mutableStateOf(false) }
    var mockClientSecret by remember { mutableStateOf("") }

    val activity = LocalContext.current as? androidx.activity.ComponentActivity
    val paymentSheet = remember(activity) {
        if (activity != null) {
            try {
                com.stripe.android.PaymentConfiguration.init(context, com.deepak.periodsaathi.BuildConfig.STRIPE_PUBLISHABLE_KEY)
            } catch (_: Exception) { }
            try {
                PaymentSheet(activity) { paymentResult ->
                    when (paymentResult) {
                        is PaymentSheetResult.Completed -> {
                            val prodId = selectedProductId ?: return@PaymentSheet
                            viewModel.onPaymentSuccess(prodId, "stripe_intent_live_success")
                        }
                        is PaymentSheetResult.Canceled -> {
                            viewModel.onPaymentError(0, "Payment cancelled gracefully.")
                        }
                        is PaymentSheetResult.Failed -> {
                            viewModel.onPaymentError(-1, paymentResult.error.localizedMessage ?: "Payment failed.")
                        }
                    }
                }
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Aesthetic ambient pink/lavender background glow circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-50).dp)
                .blur(100.dp)
                .background(BlushPink.copy(0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(80.dp)
                .background(SoftLavender.copy(0.3f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Saathi Premium 🌸",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "Unlock exclusive insight & styling limits",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Current Plan Glass Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Brush.linearGradient(listOf(BlushPink.copy(0.15f), SoftLavender.copy(0.15f))))
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Star, null, tint = Primary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                val isAnyPremium = activePurchases.isNotEmpty()
                                Text(
                                    text = if (isAnyPremium) "Premium Member 👑" else "Basic Account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Primary
                                )
                                Text(
                                    text = if (isAnyPremium) "Thank you for supporting Saathi!" else "Upgrade to experience infinite possibilities",
                                    fontSize = 12.sp,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Title
                item {
                    Text(
                        text = "Choose Your Upgrade",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }

                // Products list
                items(viewModel.products) { product ->
                    val isOwned = product.id in activePurchases
                    ProductCard(
                        product = product,
                        isOwned = isOwned,
                        onPurchase = {
                            selectedProductId = product.id
                            viewModel.initiateStripePurchase(product.id) { clientSecret, customerId, ephemeralKey ->
                                if (clientSecret.startsWith("pi_mock_")) {
                                    // Simulated Mock secure gateway for development/testing
                                    mockClientSecret = clientSecret
                                    showMockCardDialog = true
                                } else {
                                    // Production-grade live Stripe Payment Sheet
                                    paymentSheet?.presentWithPaymentIntent(clientSecret)
                                }
                            }
                        },
                        isLoading = paymentState is PaymentState.Loading && selectedProductId == product.id
                    )
                }

                item {
                    TextButton(
                        onClick = { viewModel.restorePurchases() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Already purchased? Restore Purchases", color = OnSurfaceVariant)
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }

        // Custom Confetti Pop on Premium Success state
        AnimatedVisibility(
            visible = paymentState is PaymentState.Success,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.6f)),
                contentAlignment = Alignment.Center
            ) {
                ConfettiOverlay(
                    visible = paymentState is PaymentState.Success,
                    onComplete = {}
                )

                GlassCard(
                    modifier = Modifier.padding(32.dp).widthIn(max = 350.dp),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(BlushPink, SoftLavender))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Celebration, null, tint = Color.White, modifier = Modifier.size(36.dp))
                        }

                        Text(
                            text = "Welcome to Premium! 🎉",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )

                        Text(
                            text = "You've successfully unlocked all elite features, custom theme limits, and deep AI insights. Welcome onboard!",
                            fontSize = 14.sp,
                            color = OnSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Button(
                            onClick = { viewModel.resetState() },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Let's Explore!", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Custom Error Message Modal
        AnimatedVisibility(
            visible = paymentState is PaymentState.Error,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.5f)),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier.padding(32.dp).widthIn(max = 320.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Rounded.ErrorOutline,
                            null,
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "Payment Failed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = OnSurface
                        )

                        val errorMsg = (paymentState as? PaymentState.Error)?.message ?: "Unknown Error"
                        Text(
                            text = errorMsg,
                            fontSize = 14.sp,
                            color = OnSurfaceVariant
                        )

                        Button(
                            onClick = { viewModel.resetState() },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }

        // Premium Mock Credit Card Inputs Dialog for instant development sandbox testing
        if (showMockCardDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.6f)),
                contentAlignment = Alignment.Center
            ) {
                var cardNumber by remember { mutableStateOf("") }
                var expiryDate by remember { mutableStateOf("") }
                var cvvCode by remember { mutableStateOf("") }
                var isPaying by remember { mutableStateOf(false) }

                GlassCard(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .widthIn(max = 400.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Secure Stripe Sandbox",
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { showMockCardDialog = false }) {
                                Icon(Icons.Rounded.Close, null, tint = OnSurfaceVariant)
                            }
                        }

                        // Premium Glass Credit Card Graphic
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            BlushPink.copy(0.7f),
                                            SoftLavender.copy(0.7f)
                                        )
                                    )
                                )
                                .border(1.dp, Color.White.copy(0.3f), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Rounded.CreditCard, null, tint = Color.White, modifier = Modifier.size(32.dp))
                                    Text(
                                        "STRIPE TEST",
                                        color = Color.White.copy(0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = if (cardNumber.isBlank()) "•••• •••• •••• ••••" else cardNumber,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("CARD HOLDER", fontSize = 8.sp, color = Color.White.copy(0.6f))
                                        Text("TEST CARD", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("EXPIRES", fontSize = 8.sp, color = Color.White.copy(0.6f))
                                        Text(
                                            text = if (expiryDate.isBlank()) "--/--" else expiryDate,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Use mock test card number 4242 4242 4242 4242 to authorize a successful test purchase.",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant
                        )

                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 19) cardNumber = it },
                            label = { Text("Card Number") },
                            placeholder = { Text("4242 4242 4242 4242") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { if (it.length <= 5) expiryDate = it },
                                label = { Text("MM/YY") },
                                placeholder = { Text("12/28") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = cvvCode,
                                onValueChange = { if (it.length <= 3) cvvCode = it },
                                label = { Text("CVC") },
                                placeholder = { Text("•••") },
                                modifier = Modifier.weight(1f),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Button(
                            onClick = {
                                isPaying = true
                                coroutineScope.launch {
                                    delay(2000) // Beautiful processing latency simulation
                                    isPaying = false
                                    showMockCardDialog = false
                                    val prodId = selectedProductId ?: return@launch
                                    viewModel.onPaymentSuccess(prodId, mockClientSecret)
                                }
                            },
                            enabled = cardNumber.length >= 16 && expiryDate.length >= 4 && cvvCode.length >= 3 && !isPaying,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isPaying) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Pay Securely with Stripe", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    isOwned: Boolean,
    onPurchase: () -> Unit,
    isLoading: Boolean
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (product.isBestValue) Modifier.border(2.dp, WarmGold, RoundedCornerShape(24.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (product.isBestValue) {
                        Brush.linearGradient(
                            listOf(
                                BlushPink.copy(0.12f),
                                SoftLavender.copy(0.12f)
                            )
                        )
                    } else Brush.linearGradient(
                        listOf(
                            Color.White.copy(0.1f),
                            Color.White.copy(0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = product.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
                    if (product.isBestValue) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = WarmGold,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Best Value",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Text(text = product.price, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Primary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            product.features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, color = OnSurfaceVariant, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onPurchase,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOwned) MintGreen else if (product.isBestValue) WarmGold else Primary,
                    contentColor = if (isOwned) Color.Black else Color.White
                ),
                enabled = !isLoading && !isOwned,
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (isOwned) "Activated ✓" else "Buy Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
