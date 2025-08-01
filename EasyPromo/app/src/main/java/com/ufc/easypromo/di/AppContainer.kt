package com.ufc.easypromo.di

import com.google.gson.GsonBuilder
import com.ufc.easypromo.aliexpress.data.api.AliExpressApiService
import com.ufc.easypromo.aliexpress.data.repository.AliExpressRepository
import com.ufc.easypromo.aliexpress.viewmodel.AliExpressViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple dependency container for the application.
 */
class AppContainer {

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-sg.aliexpress.com/") // CORRECTED base URL
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val aliExpressApiService = retrofit.create(AliExpressApiService::class.java)

    internal val aliExpressRepository = AliExpressRepository(aliExpressApiService)

    val aliExpressViewModelFactory = AliExpressViewModelFactory(aliExpressRepository)
}