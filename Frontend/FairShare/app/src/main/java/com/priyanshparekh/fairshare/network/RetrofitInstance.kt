package com.priyanshparekh.fairshare.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {

//    private val BASE_URL = "http://10.0.2.2:8080"
    private val BASE_URL = "http://52.14.31.24:8080"

    private val tokenProvider = {
        "eyJraWQiOiJNbnp1NnFNWjZ3eU5HdE55ZlNIWmp0SlRYczRkSnhXdnR5Q1FrR1hWZVlJPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIwMTliNTUzMC0wMGExLTcwZmUtNmIwOC1iOTM1NmE3NTQ5YjUiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0yLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMl9TQzUwRGlCZ2giLCJjbGllbnRfaWQiOiI3MjA1ZDJnZTZiNnE4Z2NocTh1Y2IzdW0zMyIsIm9yaWdpbl9qdGkiOiJlY2M4YThiMC03NWFiLTQ2NjgtODMzZC1jZTFhZDJlYjA1M2MiLCJldmVudF9pZCI6IjBjMGM4OGZjLTBiMTMtNDgwNS04Mzc5LTAwNDI2MjgyMmRhNyIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE3NDUyNjI3NDAsImV4cCI6MTc0NTM0OTE0MCwiaWF0IjoxNzQ1MjYyNzQwLCJqdGkiOiI5N2YwNDgzOC1iYThiLTQwYmEtOWFjMC0xOThhNjdlYzEwYjQiLCJ1c2VybmFtZSI6IjAxOWI1NTMwLTAwYTEtNzBmZS02YjA4LWI5MzU2YTc1NDliNSJ9.kYvRfqU5TNgbcGEg5CWdyKB2k6Fye3coMOPOeYvNyFD9tkgHBeg_XcUipWUIF5GmQlNnO6E1ivsjCoPunIlxUb4jVLf7HP5sA9wpPQDIYc6bD4iLSa4tuA9C0FfxwDnd_bu7EkklV3kU5fwPHotqJmdzoZ9XE2aM4ctUGogakdal6Ds9QS5tIZ-FQhreMYcjLokpP5ZLU1G9peYVCJpdW22X8Ku4WZuJtxAwIZDgc7v8UFWVXvCIiB-U-2_i4ZgmV28rNQaMGB0SS1y-zm-FQExXLOQISDkBIKHGbMxJWl_88fv0ruEBsXZ9aP1NGzu6JiGc2QRQkDthR_Sixm2zbw"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenProvider))
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