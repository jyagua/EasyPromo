package com.ufc.easypromo.aliexpress.data.model

import com.google.gson.annotations.SerializedName

// Models for aliexpress.affiliate.category.get
data class CategoryResponse(
    @SerializedName("aliexpress_affiliate_category_get_response")
    val categoryGetResponse: CategoryGetResponse?
)

data class CategoryGetResponse(
    @SerializedName("resp_result")
    val respResult: CategoryRespResult?
)

data class CategoryRespResult(
    @SerializedName("result")
    val result: CategoryResult?
)

data class CategoryResult(
    @SerializedName("categories")
    val categories: Categories?
)

data class Categories(
    @SerializedName("category")
    val categoryList: List<Category>?
)

data class Category(
    @SerializedName("category_id")
    val categoryId: Long,

    @SerializedName("category_name")
    val categoryName: String
)