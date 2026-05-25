package com.deepak.periodsaathi.ui.screens.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.periodsaathi.BuildConfig
import com.deepak.periodsaathi.data.dao.PurchaseDao
import com.deepak.periodsaathi.data.model.PurchaseRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class Product(val id: String, val name: String, val price: String, val pricePaise: Int, val features: List<String>, val isBestValue: Boolean = false)

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Loading : PaymentState()
    data class Success(val productId: String) : PaymentState()
    data class Error(val code: Int, val message: String) : PaymentState()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val purchaseDao: PurchaseDao
) : ViewModel() {

    var pendingProductId: String? = null

    companion object {
        var currentInstance: PaymentViewModel? = null
    }

    init {
        currentInstance = this
    }

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

    fun initiatePurchase(productId: String): JSONObject? {
        val product = products.find { it.id == productId } ?: return null
        _paymentState.value = PaymentState.Loading
        pendingProductId = productId

        try {
            val options = JSONObject()
            options.put("key", BuildConfig.RAZORPAY_KEY_ID)
            options.put("amount", product.pricePaise)
            options.put("currency", "INR")
            options.put("name", "Period Saathi")
            options.put("description", product.name)
            options.put("order_id", "order_mock_${System.currentTimeMillis()}")
            options.put("prefill", JSONObject().apply {
                put("contact", "")
                put("email", "")
            })
            options.put("theme", JSONObject().apply {
                put("color", "#874E58")
            })
            return options
        } catch (e: Exception) {
            _paymentState.value = PaymentState.Error(0, e.message ?: "Failed to initiate payment")
            pendingProductId = null
            return null
        }
    }

    fun onPaymentSuccess(productId: String, razorpayPaymentId: String?) {
        viewModelScope.launch {
            val record = PurchaseRecord(
                productId = productId,
                razorpayPaymentId = razorpayPaymentId,
                isActive = true
            )
            purchaseDao.insertPurchase(record)
            _activePurchases.value = _activePurchases.value + productId
            _paymentState.value = PaymentState.Success(productId)
        }
    }

    fun onPaymentError(code: Int, response: String?) {
        _paymentState.value = PaymentState.Error(code, response ?: "Payment failed")
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
