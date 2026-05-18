package com.deepak.periodsaathi.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Product(val id: String, val name: String, val price: String, val features: List<String>, val isBestValue: Boolean = false)

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    object Success : PaymentState()
}

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
        viewModelScope.launch {
            delay(2000)
            _paymentState.value = PaymentState.Success
        }
    }

    fun restorePurchases() {
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}
