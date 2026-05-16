package com.example.periodsaathi.ui.screens.payment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class Product(val id: String, val name: String, val price: String, val features: List<String>, val isBestValue: Boolean = false)

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {
    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    val products = listOf(
        Product("premium_themes", "Premium Themes", "₹99", listOf("All themes", "Custom colors"), false),
        Product("ad_free", "Ad-Free Forever", "₹149", listOf("No ads", "Priority support"), false),
        Product("full_bundle", "Full Premium", "₹199", listOf("Everything", "Exclusive rewards"), true)
    )

    fun initiatePurchase(productId: String) {
        _paymentState.value = PaymentState.Loading
        // Simulate purchase
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(2000)
            _paymentState.value = PaymentState.Success
        }
    }
}

sealed class PaymentState { data object Idle, data object Loading, data object Success }

private object kotlinx { val GlobalScope = kotlinx.coroutines.GlobalScope; fun delay(timeMs: Long) = kotlinx.coroutines.delay(timeMs) }