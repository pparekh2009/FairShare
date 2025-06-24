package com.priyanshparekh.fairshare.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.regions.Regions
import com.priyanshparekh.fairshare.auth.AuthViewModel
import com.priyanshparekh.fairshare.auth.LoginActivity
import com.priyanshparekh.fairshare.databinding.ActivitySettingsBinding
import com.priyanshparekh.fairshare.utils.Constants

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val authViewModel: AuthViewModel by viewModels()

    private val POOL_ID = "us-east-2_SC50DiBgh"
    private val CLIENT_ID = "7205d2ge6b6q8gchq8ucb3um33"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userPrefs = getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE)
        val tokenPrefs = getSharedPreferences(Constants.PREF_TOKEN, MODE_PRIVATE)
        val settingPrefs = getSharedPreferences(Constants.PREF_SETTING, MODE_PRIVATE)

        var userId = userPrefs.getLong(Constants.PrefKeys.KEY_USER_ID, -1L).toString()
        var fcmToken = tokenPrefs.getString(Constants.PrefKeys.KEY_TOKEN, "")!!

        binding.notificationSwitch.isChecked = settingPrefs.getBoolean(Constants.PrefKeys.KEY_NOTIFICATION, false)

        binding.logout.setOnClickListener {
            val clientConfiguration = ClientConfiguration()
            val cognitoUserPool = CognitoUserPool(this, POOL_ID, CLIENT_ID, "", clientConfiguration, Regions.US_EAST_2)

            cognitoUserPool.currentUser.signOut()

            if (userId.isEmpty()) {
                userId = userPrefs.getLong(Constants.PrefKeys.KEY_USER_ID, -1L).toString()
            }
            if (fcmToken.isEmpty()) {
                fcmToken = tokenPrefs.getString(Constants.PrefKeys.KEY_TOKEN, "")!!
            }
            authViewModel.unregisterDevice(userId, fcmToken)

            userPrefs.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.notificationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            settingPrefs.edit().putBoolean(Constants.PrefKeys.KEY_NOTIFICATION, isChecked).apply()
        }
    }
}