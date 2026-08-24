package com.priyanshparekh.fairshare.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object KeycloakRetrofitInstance {

    private val BASE_URL = "http://10.0.2.2:8081"

    private val instance by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val keycloakApiService by lazy {
        instance.create(KeycloakApiService::class.java)
    }
}