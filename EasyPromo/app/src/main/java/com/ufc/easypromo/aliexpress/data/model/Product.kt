package com.ufc.easypromo.aliexpress.data.model

import com.google.gson.annotations.SerializedName

// --- Models for aliexpress.affiliate.product.query ---
data class ProductResponse(
    @SerializedName("aliexpress_affiliate_product_query_response")
    val productQueryResponse: HotProductQueryResponse?
)

data class HotProductQueryResponse(
    @SerializedName("resp_result")
    val respResult: RespResult?
)

/**
 * --- Models for aliexpress.affiliate.product.smartmatch ---
 */
data class SmartMatchProductResponse(
    @SerializedName("aliexpress_affiliate_product_smartmatch_response")
    val smartMatchResponse: SmartMatchResult?
)

data class SmartMatchResult(
    @SerializedName("resp_result")
    val respResult: RespResult?
)
// --- End of new section ---

data class RespResult(
    @SerializedName("result")
    val result: ProductResult?
)

data class ProductResult(
    @SerializedName("products")
    val products: Products?,

    @SerializedName("total_record_count")
    val totalRecordCount: Long?
)

data class Products(
    @SerializedName("product")
    val productList: List<Product>?
)

data class Product(
    @SerializedName("product_id")
    val productId: Long,

    @SerializedName("product_title")
    val title: String,

    @SerializedName("product_main_image_url")
    val imageUrl: String,

    @SerializedName("target_sale_price")
    val salePrice: String,

    @SerializedName("promotion_link")
    val promotionLink: String?,

    @SerializedName("target_original_price")
    val originalPrice: String?,

    // ✅ Fix: Added the missing category field to match what the repository requests from the API.
    @SerializedName("first_level_category_name")
    val firstLevelCategoryName: String?
)