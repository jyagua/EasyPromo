package com.ufc.easypromo.models

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.Exclude
import com.ufc.easypromo.R
import com.ufc.easypromo.util.NotificationHelper

data class Product(
    val id: Long = 0L,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val store: String = "",
    // FIX: Removed the incorrect "@delegate:" annotation target.
    @get:Exclude @Transient val isFavorite: MutableState<Boolean> = mutableStateOf(false),
    @get:Exclude @Transient val isInCart: MutableState<Boolean> = mutableStateOf(false),
    val imageRes: Int = 0,
    @get:Exclude @Transient var lastNotifiedPrice: MutableState<Double?> = mutableStateOf(null),
    val imageUrl: String? = null,
    val promotionLink: String? = null,
    val originalPrice: Double = 0.0,
    val discountPercent: Double = 0.0
)

@SuppressLint("VisibleForTests")
suspend fun checkPriceDropsAndNotify(
    context: Context,
    products: List<Product>,
    notificationsEnabled: Boolean
) {
    if (!notificationsEnabled) return

    products.filter { it.isFavorite.value }.forEach { product ->
        val lastPrice = product.lastNotifiedPrice.value
        if (lastPrice != null && product.price < lastPrice) {
            NotificationHelper.sendPriceDropNotification(context, product)
            product.lastNotifiedPrice.value = product.price
        } else if (lastPrice == null) {
            product.lastNotifiedPrice.value = product.price
        }
    }
}

val productList = listOf(
    Product(
        id = 1L,
        name = "Smartphone XYZ",
        description = "Última geração com câmera avançada.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "Amazon",
        imageRes = R.drawable.phone,
        imageUrl = null
    ),
    Product(
        id = 2L,
        name = "Fone Bluetooth Pro",
        description = "Som de alta qualidade e bateria duradoura.",
        price = 499.99,
        category = "Acessórios",
        store = "Amazon",
        imageRes = R.drawable.earphone,
        imageUrl = null
    ),
    Product(
        id = 5L,
        name = "Smartphone ABC",
        description = "Penúltima geração com câmera mediana.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "Amazon",
        imageRes = R.drawable.phone,
        imageUrl = null
    ),
    Product(
        id = 6L,
        name = "Smartphone DEF",
        description = "Primeira geração com câmera mediana.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "AliExpress",
        imageRes = R.drawable.phone,
        imageUrl = null
    ),
    Product(
        id = 7L,
        name = "Smartphone GHI",
        description = "Segunda geração com câmera mediana.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "Amazon",
        imageRes = R.drawable.phone,
        imageUrl = null
    ),
    Product(
        id = 3L,
        name = "Relógio Inteligente X",
        description = "Monitore sua saúde com estilo.",
        price = 299.99,
        category = "Wearables",
        store = "AliExpress",
        imageRes = R.drawable.watch,
        imageUrl = null
    ),
    Product(
        id = 4L,
        name = "Câmera de Segurança Wi-Fi",
        description = "Proteja sua casa com tecnologia de ponta.",
        price = 199.99,
        category = "Segurança",
        store = "AliExpress",
        imageRes = R.drawable.securitycamera,
        imageUrl = null
    ),
)