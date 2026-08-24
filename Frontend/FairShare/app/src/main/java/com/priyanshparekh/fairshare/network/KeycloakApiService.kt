package com.priyanshparekh.fairshare.network

import com.priyanshparekh.fairshare.auth.KeycloakTokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface KeycloakApiService {

    @FormUrlEncoded
    @POST("/realms/fairshare/protocol/openid-connect/token")
    suspend fun login(
        @Field("grant_type") grantType: String = "password",
        @Field("client_id") clientId: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<KeycloakTokenResponse>

    // Non-suspend Call so TokenAuthenticator (a synchronous OkHttp callback) can block on it with execute()
    @FormUrlEncoded
    @POST("/realms/fairshare/protocol/openid-connect/token")
    fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String
    ): Call<KeycloakTokenResponse>

}