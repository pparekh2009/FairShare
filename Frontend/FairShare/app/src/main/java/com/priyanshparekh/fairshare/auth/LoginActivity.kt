package com.priyanshparekh.fairshare.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.priyanshparekh.fairshare.home.HomeActivity
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.ActivityLoginBinding
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.Status

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.linkToSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        authViewModel.loginStatus.observe(this) { status ->
            when (status) {
                is Status.SUCCESS -> {

                    val email = status.data
                    val username = email.substring(0, email.indexOf('@'))

                    val fcmToken = getSharedPreferences(Constants.PREF_TOKEN, MODE_PRIVATE).getString(Constants.PrefKeys.KEY_TOKEN, "")
                    authViewModel.completeLogin(this, username, fcmToken)


                }
                is Status.ERROR -> {
                    Snackbar.make(binding.root, status.message, Snackbar.LENGTH_LONG).apply {
                        setAction("OK") {
                            this.dismiss()
                        }
                        setBackgroundTint(resources.getColor(R.color.md_theme_error, theme))
                        setTextColor(resources.getColor(R.color.md_theme_onError, theme))
                        show()
                    }
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
            }
        }

        authViewModel.completeLoginStatus.observe(this) { status ->
            when (status) {
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Snackbar.make(binding.root, status.message, Snackbar.LENGTH_LONG).apply {
                        setAction("OK") {
                            this.dismiss()
                        }
                        setBackgroundTint(resources.getColor(R.color.md_theme_error, theme))
                        setTextColor(resources.getColor(R.color.md_theme_onError, theme))
                        show()
                    }
                }
                is Status.SUCCESS -> {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailInput.text?.toString() ?: ""
            val password = binding.passwordInput.text?.toString() ?: ""

            if (email.isEmpty()) {
                binding.emailInputLayout.error = "Email cannot be empty"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordInputLayout.error = "Password cannot be empty"
                return@setOnClickListener
            }

            authViewModel.login(this, email, password)
        }
    }
}