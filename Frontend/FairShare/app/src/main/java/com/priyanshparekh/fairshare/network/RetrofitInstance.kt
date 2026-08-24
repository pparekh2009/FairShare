package com.priyanshparekh.fairshare.network

import android.annotation.SuppressLint
import android.content.Context
import com.priyanshparekh.fairshare.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@SuppressLint("StaticFieldLeak")
object RetrofitInstance {

    private var context: Context? = null

    fun init(context: Context) {
        this.context = context
    }

    private val BASE_URL = "http://10.0.2.2:8080"

    private val tokenProvider = {
        context?.getSharedPreferences(Constants.PREF_LOGIN, Context.MODE_PRIVATE)?.getString(Constants.LoginKeys.KEY_ACCESS_TOKEN, "")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenProvider))
        .authenticator(TokenAuthenticator { context })
        .build()

    private val instance by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService by lazy {
        instance.create(ApiService::class.java)
    }
}