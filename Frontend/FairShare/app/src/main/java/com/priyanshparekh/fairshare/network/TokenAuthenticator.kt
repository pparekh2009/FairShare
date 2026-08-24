package com.priyanshparekh.fairshare.network

import android.content.Context
import com.priyanshparekh.fairshare.utils.Constants
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val contextProvider: () -> Context?) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Only retry once per request chain, otherwise a persistently-401ing endpoint would loop forever
        if (responseCount(response) >= 2) {
            return null
        }

        val context = contextProvider() ?: return null
        val loginPrefs = context.getSharedPreferences(Constants.PREF_LOGIN, Context.MODE_PRIVATE)

        synchronized(this) {
            val failedAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentAccessToken = loginPrefs.getString(Constants.LoginKeys.KEY_ACCESS_TOKEN, null)

            // Another request already refreshed the token while this one was waiting on the lock
            if (!currentAccessToken.isNullOrEmpty() && currentAccessToken != failedAccessToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val refreshToken = loginPrefs.getString(Constants.LoginKeys.KEY_REFRESH_TOKEN, null)
            if (refreshToken.isNullOrEmpty()) {
                return null
            }

            val refreshResponse = try {
                KeycloakRetrofitInstance.keycloakApiService.refreshToken(
                    clientId = "fairshare-public",
                    refreshToken = refreshToken
                ).execute()
            } catch (e: Exception) {
                null
            }

            val body = refreshResponse?.body()
            if (refreshResponse?.isSuccessful != true || body == null) {
                // Refresh token itself is dead (expired/revoked) - clear the session, nothing more we can do here
                loginPrefs.edit().clear().apply()
                return null
            }

            loginPrefs.edit().apply {
                putString(Constants.LoginKeys.KEY_ACCESS_TOKEN, body.access_token)
                putString(Constants.LoginKeys.KEY_REFRESH_TOKEN, body.refresh_token)
                putLong(Constants.LoginKeys.KEY_EXPIRY_TIME, System.currentTimeMillis() + (body.expires_in * 1000L))
                apply()
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${body.access_token}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}
