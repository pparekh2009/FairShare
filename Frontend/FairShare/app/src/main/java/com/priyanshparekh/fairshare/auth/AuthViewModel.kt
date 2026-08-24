package com.priyanshparekh.fairshare.auth

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.network.KeycloakRetrofitInstance
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val tag = this.javaClass.simpleName

    private val _signUpStatus = MutableLiveData<Status<String>>()
    val signUpStatus: LiveData<Status<String>> = _signUpStatus

    fun signUp(context: Context, name: String, email: String, password: String, profilePic: String, fcmToken: String) {
        viewModelScope.launch {
            _signUpStatus.value = Status.LOADING()

            // Step 1
            val response = RetrofitInstance.apiService.signUp(
                SignUpRequest(
                    username = email.substringBefore("@"),
                    email = email,
                    password = password,
                    name = name,
                    profilePic = profilePic,
                    fcmToken = fcmToken
                )
            )


            // Step 5
            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    context.getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).edit().apply {
                        putString(Constants.LoginKeys.KEY_USERNAME, email.substringBefore("@"))
                        putString(Constants.LoginKeys.KEY_ACCESS_TOKEN, body.access_token)
                        putString(Constants.LoginKeys.KEY_REFRESH_TOKEN, body.refresh_token)
                        putLong(Constants.LoginKeys.KEY_EXPIRY_TIME, System.currentTimeMillis() + (body.expires_in * 1000L))
                        apply()
                    }

                    context.getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).edit().apply {
                        putLong(Constants.PrefKeys.KEY_USER_ID, body.id)
                        putString(Constants.PrefKeys.KEY_NAME, body.name)
                        putString(Constants.PrefKeys.KEY_USERNAME, body.username)
                        putString(Constants.PrefKeys.KEY_EMAIL, body.email)
                        putString(Constants.PrefKeys.KEY_PROFILE_PIC, body.profilePic)
                        apply()
                    }
                    _signUpStatus.value = Status.SUCCESS("Sign Up Success")
                } else {
                    Log.d(tag, "signUp: inner if: error: body null")
                    _signUpStatus.value = Status.ERROR("Error signing up")
                }
            } else {
                val errorString = response.errorBody()?.string()
                val plainMessage = try {
                    org.json.JSONObject(errorString).getString("message")
                } catch (e: Exception) {
                    errorString
                }
                val code = response.code()
                Log.d(tag, "signUp: response code: $code")
                when(code) {
                    409 -> {
                        // Conflict
                        val message = "UserAlreadyExistsException:$plainMessage"
                        _signUpStatus.value = Status.ERROR(message)
                    }

                    424 -> {
                        // Failed Dependency
                        val message = "AccountCreatedButLoginFailedException:$plainMessage"
                        _signUpStatus.value = Status.ERROR(message)
                    }

                    502 -> {
                        // Bad Gateway
                        val message = "AuthServiceException:$plainMessage"
                        _signUpStatus.value = Status.ERROR(message)
                    }
                    else ->  {
                        val message = "Exception:$plainMessage"
                        _signUpStatus.value = Status.ERROR(message)
                    }
                }
                Log.d(tag, "signUp: outer if: error: $errorString")
            }
        }
    }

    private val _loginStatus = MutableLiveData<Status<String>>()
    val loginStatus: LiveData<Status<String>> = _loginStatus

    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            _loginStatus.value = Status.LOADING()

            val response = KeycloakRetrofitInstance.keycloakApiService.login(
                clientId = "fairshare-public",
                username = email,
                password = password
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    context.getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE).apply {
                        edit().apply {
                            putString(Constants.LoginKeys.KEY_USERNAME, email.substringBefore("@"))
                            putString(Constants.LoginKeys.KEY_ACCESS_TOKEN, body.access_token)
                            putString(Constants.LoginKeys.KEY_REFRESH_TOKEN, body.refresh_token)
                            putLong(Constants.LoginKeys.KEY_EXPIRY_TIME, System.currentTimeMillis() + (body.expires_in * 1000L))
                            apply()
                        }
                    }
                    _loginStatus.value = Status.SUCCESS(email)
                } else {
                    _loginStatus.value = Status.ERROR("Error logging in")
                }
            } else {
                _loginStatus.value = Status.ERROR("Error logging in")
            }
        }
    }


    private val _completeLoginStatus = MutableLiveData<Status<String>>()
    val completeLoginStatus: LiveData<Status<String>> = _completeLoginStatus

    fun completeLogin(context: Context, username: String, fcmToken: String?) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.completeLogin(
                username = username,
                token = FcmToken(fcmToken!!)
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    context.getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).edit().apply {
                        putLong(Constants.PrefKeys.KEY_USER_ID, body.id)
                        putString(Constants.PrefKeys.KEY_NAME, body.name)
                        putString(Constants.PrefKeys.KEY_USERNAME, body.username)
                        putString(Constants.PrefKeys.KEY_EMAIL, body.email)
                        putString(Constants.PrefKeys.KEY_PROFILE_PIC, body.profilePic)
                        apply()
                    }

                    _completeLoginStatus.value = Status.SUCCESS("Login Successful")
                } else {
                    _completeLoginStatus.value = Status.ERROR("Error Registering Device")
                }
            } else {
                    _completeLoginStatus.value = Status.ERROR("Error Registering Device")
            }
        }
    }

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun checkLoginStatus(context: Context) {
        val loginPrefs = context.getSharedPreferences(Constants.PREF_LOGIN, MODE_PRIVATE)
        val accessToken = loginPrefs.getString(Constants.LoginKeys.KEY_ACCESS_TOKEN, "")
        val expiresIn = loginPrefs.getLong(Constants.LoginKeys.KEY_EXPIRY_TIME, -1L)

        if ((accessToken ?: "").isEmpty() or (expiresIn < System.currentTimeMillis())) {
            _isLoggedIn.value = false
        } else {
            _isLoggedIn.value = true
        }
    }

    fun unregisterDevice(userId: String, fcmToken: String) {
        viewModelScope.launch {
            RetrofitInstance.apiService.unregisterDevice(userId, fcmToken)
        }
    }
}