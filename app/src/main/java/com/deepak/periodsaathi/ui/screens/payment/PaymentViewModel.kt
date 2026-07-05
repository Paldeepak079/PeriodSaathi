package com.deepak.periodsaathi.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.data.dao.PurchaseDao
import com.deepak.periodsaathi.data.model.PurchaseRecord
import com.deepak.periodsaathi.data.repository.StripePaymentService
import com.deepak.periodsaathi.data.repository.PaymentIntentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val pricePaise: Int,
    val features: List<String>,
    val isBestValue: Boolean = false
)

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Loading : PaymentState()
    data class Success(val productId: String) : PaymentState()
    data class Error(val code: Int, val message: String) : PaymentState()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val purchaseDao: PurchaseDao,
    private val stripePaymentService: StripePaymentService
) : ViewModel() {

    var pendingProductId: String? = null

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _activePurchases = MutableStateFlow<Set<String>>(emptySet())
    val activePurchases: StateFlow<Set<String>> = _activePurchases.asStateFlow()

    val products = listOf(
        Product("premium_themes", "Premium Themes", "₹99", 9900, listOf("All themes", "Custom colors")),
        Product("ad_free", "Ad-Free Forever", "₹149", 14900, listOf("No ads", "Priority support")),
        Product("full_bundle", "Full Premium", "₹199", 19900, listOf("Everything", "Exclusive rewards"), true)
    )

    init {
        viewModelScope.launch {
            val purchases = purchaseDao.getActivePurchases().first()
            _activePurchases.value = purchases.map { it.productId }.toSet()
        }
    }

    /**
     * Creates a Stripe PaymentIntent and returns the client secret via a callback
     * so that the Jetpack Compose UI can launch the Stripe Payment Sheet.
     */
    fun initiateStripePurchase(
        productId: String,
        onClientSecretFetched: (clientSecret: String, customerId: String, ephemeralKey: String) -> Unit
    ) {
        val product = products.find { it.id == productId } ?: return
        _paymentState.value = PaymentState.Loading
        pendingProductId = productId

        viewModelScope.launch {
            when (val result = stripePaymentService.createPaymentIntent(
                amount = product.pricePaise,
                currency = "inr",
                productId = productId
            )) {
                is PaymentIntentResult.Success -> {
                    _paymentState.value = PaymentState.Idle // Ready for payment sheet
                    onClientSecretFetched(result.clientSecret, result.customerId, result.ephemeralKey)
                }
                is PaymentIntentResult.Error -> {
                    _paymentState.value = PaymentState.Error(0, result.message)
                    pendingProductId = null
                }
            }
        }
    }

    fun onPaymentSuccess(productId: String, stripeIntentId: String) {
        viewModelScope.launch {
            val record = PurchaseRecord(
                productId = productId,
                stripePaymentIntentId = stripeIntentId,
                isActive = true
            )
            purchaseDao.insertPurchase(record)
            _activePurchases.value = _activePurchases.value + productId
            _paymentState.value = PaymentState.Success(productId)
            pendingProductId = null
        }
    }

    fun onPaymentError(code: Int, message: String?) {
        _paymentState.value = PaymentState.Error(code, message ?: "Payment failed")
        pendingProductId = null
    }

    override fun onCleared() {
        super.onCleared()
        pendingProductId = null
    }

    fun restorePurchases() {
        viewModelScope.launch {
            val purchases = purchaseDao.getActivePurchases().first()
            _activePurchases.value = purchases.map { it.productId }.toSet()
        }
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}
