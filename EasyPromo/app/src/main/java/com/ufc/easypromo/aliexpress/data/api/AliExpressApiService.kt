package com.ufc.easypromo.aliexpress.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AliExpressApiService {

    @FormUrlEncoded
    @POST("sync")
    suspend fun getProducts(
        @FieldMap options: Map<String, String>
    ): Response<ResponseBody>

    /**
     * ✅ NEW: Function to fetch smart-matched (recommended) products.
     */
    @FormUrlEncoded
    @POST("sync")
    suspend fun getSmartMatchProducts(
        @FieldMap options: Map<String, String>
    ): Response<ResponseBody>
}