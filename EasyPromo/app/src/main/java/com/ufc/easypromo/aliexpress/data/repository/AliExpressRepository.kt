package com.ufc.easypromo.aliexpress.data.repository

// This is the intermediate data model for the API response
// This is your app's main Product model that the UI uses
import android.util.Log
import com.google.gson.Gson
import com.ufc.easypromo.R
import com.ufc.easypromo.aliexpress.data.api.AliExpressApiService
import com.ufc.easypromo.aliexpress.data.model.ProductResponse
import com.ufc.easypromo.aliexpress.data.model.SmartMatchProductResponse
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.util.SignUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Currency
import java.util.Locale

class AliExpressRepository(private val apiService: AliExpressApiService) {

    private val appKey = "517324"
    private val appSecret = "yO8x6c965zlG0QSGL4l7TngSPXl7IGYE"
    private val trackingId = "default"

    /**
     * Fetches products and maps them from the API model to your app's UI model.
     */
    suspend fun getProducts(page: Int, keywords: String, sortOrder: String): Pair<List<Product>, Long> {
        return withContext(Dispatchers.IO) {
            val params = createApiParams(page, keywords, sortOrder)
            val response = apiService.getProducts(params)

            if (response.isSuccessful) {
                val responseBodyString = response.body()?.string()
                if (responseBodyString != null) {
                    try {
                        val productResponse = Gson().fromJson(responseBodyString, ProductResponse::class.java)
                        val result = productResponse.productQueryResponse?.respResult?.result
                        // Maps the API's Product model to your app's main Product model
                        val productList = result?.products?.productList?.map { apiProduct ->
                            val salePrice = apiProduct.salePrice.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val originalPrice = apiProduct.originalPrice?.replace(",", ".")?.toDoubleOrNull() ?: salePrice
                            val discountPercent = if (originalPrice > 0 && salePrice < originalPrice) {
                                ((originalPrice - salePrice) / originalPrice) * 100
                            } else 0.0

                            Product(
                                id = apiProduct.productId, // Convert Long to String for your model
                                name = apiProduct.title,
                                description = apiProduct.title,
                                price = salePrice,
                                originalPrice = originalPrice,
                                discountPercent = discountPercent,
                                // ✅ Here is the fix: Using the category from the API model
                                category = apiProduct.firstLevelCategoryName ?: "Geral",
                                store = "AliExpress",
                                imageRes = R.drawable.ic_notification_placeholder,
                                imageUrl = apiProduct.imageUrl,
                                promotionLink = apiProduct.promotionLink
                            )
                        } ?: emptyList()
                        val totalCount = result?.totalRecordCount ?: 0L
                        Pair(productList, totalCount)
                    } catch (e: Exception) {
                        Log.e("AliExpressRepo", "Failed to parse JSON. Response was: $responseBodyString", e)
                        throw Exception("API returned an error: $responseBodyString")
                    }
                } else {
                    Pair(emptyList(), 0L)
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AliExpressRepo", "API call failed with code ${response.code()}: $errorBody")
                throw Exception("API call failed: $errorBody")
            }
        }
    }

    /**
     * Fetches smart match products and maps them to your app's UI model.
     */
    suspend fun getSmartMatchProducts(productId: Long): List<Product> {
        return withContext(Dispatchers.IO) {
            val params = createSmartMatchApiParams(productId)
            val response = apiService.getSmartMatchProducts(params)

            if (response.isSuccessful) {
                val responseBodyString = response.body()?.string()
                if (responseBodyString != null) {
                    try {
                        val smartMatchResponse = Gson().fromJson(responseBodyString, SmartMatchProductResponse::class.java)
                        // Maps the API's Product model to your app's main Product model
                        smartMatchResponse.smartMatchResponse?.respResult?.result?.products?.productList?.map { apiProduct ->
                            val salePrice = apiProduct.salePrice.replace(",", ".").toDoubleOrNull() ?: 0.0
                            val originalPrice = apiProduct.originalPrice?.replace(",",".")?.toDoubleOrNull() ?: salePrice
                            val discount = if (originalPrice > 0 && salePrice < originalPrice) ((originalPrice - salePrice) / originalPrice) * 100 else 0.0

                            Product(
                                id = apiProduct.productId, // Convert Long to String for your model
                                name = apiProduct.title,
                                description = apiProduct.title,
                                price = salePrice,
                                originalPrice = originalPrice,
                                discountPercent = discount,
                                // ✅ Here is the fix: Using the category from the API model
                                category = apiProduct.firstLevelCategoryName ?: "Geral",
                                store = "AliExpress",
                                imageRes = R.drawable.ic_notification_placeholder,
                                imageUrl = apiProduct.imageUrl,
                                promotionLink = apiProduct.promotionLink
                            )
                        } ?: emptyList()
                    } catch (e: Exception) {
                        Log.e("AliExpressRepo", "Failed to parse SmartMatch JSON. Response was: $responseBodyString", e)
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    private fun createApiParams(page: Int, keywords: String, sortOrder: String): Map<String, String> {
        val locale = Locale.getDefault()
        val language = if (locale.language == "pt") "pt_BR" else "en_US"
        val currency = Currency.getInstance(locale).currencyCode

        val params = mutableMapOf(
            "app_key" to appKey,
            "method" to "aliexpress.affiliate.product.query",
            "sign_method" to "sha256",
            "timestamp" to System.currentTimeMillis().toString(),
            "v" to "2.0",
            "tracking_id" to trackingId,
            "ship_to_country" to locale.country,
            "target_currency" to currency,
            "target_language" to language,
            "page_no" to page.toString(),
            "page_size" to "15",
            "fields" to "product_title,product_main_image_url,target_sale_price,target_original_price,promotion_link,first_level_category_name"
        )
        if (keywords.isNotBlank()) {
            params["keywords"] = keywords
        }
        if (sortOrder.isNotBlank() && sortOrder != "DISCOUNT_DESC") {
            params["sort"] = sortOrder
        }
        params["sign"] = SignUtil.getSign(params, appSecret)
        return params
    }

    private fun createSmartMatchApiParams(productId: Long): Map<String, String> {
        val params = mutableMapOf(
            "app_key" to appKey,
            "method" to "aliexpress.affiliate.product.smartmatch",
            "sign_method" to "sha256",
            "timestamp" to System.currentTimeMillis().toString(),
            "v" to "2.0",
            "tracking_id" to trackingId,
            "product_ids" to productId.toString(),
            "fields" to "product_title,product_main_image_url,target_sale_price,target_original_price,promotion_link,first_level_category_name"
        )
        params["sign"] = SignUtil.getSign(params, appSecret)
        return params
    }
}