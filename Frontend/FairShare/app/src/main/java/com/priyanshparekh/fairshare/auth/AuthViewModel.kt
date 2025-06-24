package com.priyanshparekh.fairshare.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException
import com.priyanshparekh.fairshare.model.User
import com.priyanshparekh.fairshare.model.UserDevice
import com.priyanshparekh.fairshare.network.RetrofitInstance
import com.priyanshparekh.fairshare.utils.Status
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val POOL_ID = "us-east-2_SC50DiBgh"
    private val CLIENT_ID = "7205d2ge6b6q8gchq8ucb3um33"

    private val _signUpStatus = MutableLiveData<Status<String>>()
    val signUpStatus: LiveData<Status<String>> = _signUpStatus

    fun signUp(context: Context, name: String, phone: String, email: String, password: String, profilePic: String) {
        _signUpStatus.value = Status.LOADING()

        val signUpRequest = CognitoUserAttributes()
        signUpRequest.addAttribute("name", name)
        signUpRequest.addAttribute("phone_number", "+$phone")
        signUpRequest.addAttribute("email", email)

        val clientConfiguration = ClientConfiguration()
        val cognitoUserPool = CognitoUserPool(context, POOL_ID, CLIENT_ID, "", clientConfiguration, Regions.US_EAST_2)

        cognitoUserPool.signUpInBackground(email, password, signUpRequest, null, object :
            SignUpHandler {
            override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {

                addUser(User(email.substring(0, email.indexOf('@')), email, name, profilePic))

                login(context, email, password)
            }

            override fun onFailure(exception: Exception?) {

                when (exception) {
                    is InvalidPasswordException -> _signUpStatus.value = Status.ERROR("Password must have numeric characters")

                    is UsernameExistsException -> _signUpStatus.value = Status.ERROR("Email already exists")

                    is InvalidParameterException -> _signUpStatus.value = Status.ERROR("Invalid phone number format")

                    else -> {
                        _signUpStatus.value = Status.ERROR(exception?.message ?: "")

                        exception?.let { e ->
                            throw e
                        }
                    }
                }
            }
        })
    }

    private val _loginStatus = MutableLiveData<Status<String>>()
    val loginStatus: LiveData<Status<String>> = _loginStatus

    fun login(context: Context, email: String, password: String) {
        _loginStatus.value = Status.LOADING()

        val clientConfiguration = ClientConfiguration()
        val cognitoUserPool = CognitoUserPool(context, POOL_ID, CLIENT_ID, "", clientConfiguration, Regions.US_EAST_2)

        val user = cognitoUserPool.getUser(email)

        val authDetails = AuthenticationDetails(email, password, null)

        user.getSessionInBackground(object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {

                context.getSharedPreferences("loginPref", Context.MODE_PRIVATE).apply {
                    edit().apply {
                        putString("username", userSession?.username)
                        putString("accessToken", userSession?.accessToken?.jwtToken)
                        putString("idToken", userSession?.idToken?.jwtToken)
                        putString("refreshToken", userSession?.refreshToken?.token)
                        putLong("expiryTime", userSession?.accessToken?.expiration?.time!!)
                        apply()
                    }
                }
                _loginStatus.value = Status.SUCCESS(email)
            }

            override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation?,
                userId: String?
            ) {
                authenticationContinuation?.setAuthenticationDetails(authDetails)
                authenticationContinuation?.continueTask()
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) { }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) { }

            override fun onFailure(exception: java.lang.Exception?) {

                when (exception) {
                    is NotAuthorizedException -> _loginStatus.value = Status.ERROR("Incorrect Username or Password")

                    is InvalidPasswordException -> _loginStatus.value = Status.ERROR("Password must have numeric characters")

                    else -> {
                        _loginStatus.value = Status.ERROR(exception?.message ?: "")
                        exception?.let {
                            throw it
                        }
                    }
                }
            }
        })
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.addUser(user)

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    _signUpStatus.value = Status.SUCCESS(body.email)
                } else {
                    _signUpStatus.value = Status.ERROR("Error adding user to db")
                }
            } else {
                _signUpStatus.value = Status.ERROR("Error adding user to db")
            }
        }
    }

    private val _userStatus = MutableLiveData<Status<User>>()
    val userStatus: LiveData<Status<User>> = _userStatus

    fun getUser(username: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.apiService.getUserByUsername(username)

            if (response.isSuccessful) {
                val body =  response.body()

                if (body != null) {
                    _userStatus.value = Status.SUCCESS(body)
                } else {
                    _userStatus.value = Status.ERROR("Error getting user")
                }
            } else {
                _userStatus.value = Status.ERROR("Error getting user")
            }
        }
    }

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun checkLoginStatus(context: Context) {
        val clientConfiguration = ClientConfiguration()
        val cognitoUserPool = CognitoUserPool(context, POOL_ID, CLIENT_ID, "", clientConfiguration, Regions.US_EAST_2)

        val user = cognitoUserPool.currentUser

        user.getSessionInBackground(object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                _isLoggedIn.value = userSession?.isValid == true
            }

            override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation?,
                userId: String?
            ) {
                authenticationContinuation?.continueTask()
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            }

            override fun onFailure(exception: java.lang.Exception?) {
                _isLoggedIn.value = false
            }
        })
    }

    fun registerDevice(userDevice: UserDevice) {
        viewModelScope.launch {
            RetrofitInstance.apiService.registerDevice(userDevice)
        }
    }

    fun unregisterDevice(userId: String, fcmToken: String) {
        viewModelScope.launch {
            RetrofitInstance.apiService.unregisterDevice(userId, fcmToken)
        }
    }
}