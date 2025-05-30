package com.ufc.easypromo.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.ufc.easypromo.R

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val store: String, // Filtro para dizer de qual loja é o produto e separar de acordo
    val isFavorite: MutableState<Boolean> = mutableStateOf(false),
    val isInCart: MutableState<Boolean> = mutableStateOf(false),
    val imageRes: Int
)

val productList = listOf(
    Product(
        id = 1,
        name = "Smartphone XYZ",
        description = "Última geração com câmera avançada.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "Amazon",
        imageRes = R.drawable.phone
    ),
    Product(
        id = 2,
        name = "Fone Bluetooth Pro",
        description = "Som de alta qualidade e bateria duradoura.",
        price = 499.99,
        category = "Acessórios",
        store = "Amazon",
        imageRes = R.drawable.earphone
    ),
    Product(
        id = 5,
        name = "Smartphone ABC",
        description = "Penúltima geração com câmera mediana.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "Amazon",
        imageRes = R.drawable.phone
    ),
    Product(
        id = 6,
        name = "Smartphone DEF",
        description = "Primeira geração com câmera mediana.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "AliExpress",
        imageRes = R.drawable.phone
    ),
    Product(
        id = 7,
        name = "Smartphone GHI",
        description = "Segunda geração com câmera mediana.",
        price = 2999.99,
        category = "Eletrônicos",
        store = "Amazon",
        imageRes = R.drawable.phone
    ),
    Product(
        id = 3,
        name = "Relógio Inteligente X",
        description = "Monitore sua saúde com estilo.",
        price = 299.99,
        category = "Wearables",
        store = "AliExpress",
        imageRes = R.drawable.watch
    ),
    Product(
        id = 4,
        name = "Câmera de Segurança Wi-Fi",
        description = "Proteja sua casa com tecnologia de ponta.",
        price = 199.99,
        category = "Segurança",
        store = "AliExpress",
        imageRes = R.drawable.securitycamera
    ),
)