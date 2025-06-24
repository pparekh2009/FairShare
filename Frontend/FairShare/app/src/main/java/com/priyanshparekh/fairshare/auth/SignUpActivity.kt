package com.priyanshparekh.fairshare.auth

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.databinding.ActivitySignUpBinding
import com.priyanshparekh.fairshare.home.HomeActivity
import com.priyanshparekh.fairshare.model.UserDevice
import com.priyanshparekh.fairshare.utils.Constants
import com.priyanshparekh.fairshare.utils.LetterAvatar
import com.priyanshparekh.fairshare.utils.Status
import java.io.ByteArrayOutputStream

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.linkToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding.profileImageInput.setImageResource(R.drawable.baseline_person_24)

        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.nameInputLayout.error != "") {
                    binding.nameInputLayout.error = ""
                }


                val name = p0.toString().split(" ")
                val initials = when(name.size) {
                    0 -> ""
                    1 -> {
                        if (name[0].isNotEmpty()) {
                            "${name[0][0]}"
                        } else {
                            ""
                        }
                    }
                    2 -> {
                        if (name[1].isNotEmpty()) {
                            "${name[0][0]}${name[1][0]}"
                        } else {
                            "${name[0][0]}"
                        }

                    }
                    else -> "${name[0][0]}${name[1][0]}"
                }

                if (initials.isNotEmpty()) {
                    binding.profileImageInput.setImageDrawable(LetterAvatar(this@SignUpActivity, Color.BLACK, initials, 25))
                } else {
                    binding.profileImageInput.setImageResource(R.drawable.baseline_person_24)
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        binding.phoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.phoneInputLayout.error != "") {
                    binding.phoneInputLayout.error = ""
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        binding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.emailInputLayout.error != "") {
                    binding.emailInputLayout.error = ""
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.passwordInputLayout.error != "") {
                    binding.passwordInputLayout.error = ""
                }
            }

            override fun afterTextChanged(p0: Editable?) { }

        })

        authViewModel.signUpStatus.observe(this) { status ->
            when (status) {
                is Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE

                    val email = status.data
                    val username = email.substring(0, email.indexOf('@'))
                    authViewModel.getUser(username)

                    Toast.makeText(this, status.data, Toast.LENGTH_SHORT).show()
                }

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
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
            }
        }

        authViewModel.userStatus.observe(this) { status ->
            when (status) {
                is Status.ERROR -> {
                    binding.loading.visibility = View.GONE

                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is Status.LOADING -> binding.loading.visibility = View.VISIBLE
                is Status.SUCCESS -> {
                    val user = status.data

                    getSharedPreferences(Constants.PREF_USER, MODE_PRIVATE).edit().apply {
                        putLong(Constants.PrefKeys.KEY_USER_ID, user.id ?: -1L)
                        putString(Constants.PrefKeys.KEY_NAME, user.name)
                        putString(Constants.PrefKeys.KEY_USERNAME, user.username)
                        putString(Constants.PrefKeys.KEY_EMAIL, user.email)
                        putString(Constants.PrefKeys.KEY_PROFILE_PIC, user.profilePic)
                        apply()
                    }

                    val fcmToken = getSharedPreferences(Constants.PREF_TOKEN, Context.MODE_PRIVATE).getString(Constants.PrefKeys.KEY_TOKEN, "")

                    val userDevice = UserDevice(null, user.id!!, fcmToken!!, null)
                    authViewModel.registerDevice(userDevice)

                    binding.loading.visibility = View.GONE

                    val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.btnSignup.setOnClickListener {
            val name = binding.nameInput.text?.toString() ?: ""
            val phone = binding.phoneInput.text?.toString() ?: ""
            val email = binding.emailInput.text?.toString() ?: ""
            val password = binding.passwordInput.text?.toString() ?: ""

            val drawable = binding.profileImageInput.drawable
            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val imageInByte = baos.toByteArray()

            val base64NoWrap = Base64.encodeToString(imageInByte, Base64.NO_WRAP)


            if (name.isEmpty()) {
                binding.nameInputLayout.error = "Name cannot be empty"
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                binding.phoneInputLayout.error = "Phone Number cannot be empty"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.emailInputLayout.error = "Email cannot be empty"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordInputLayout.error = "Password cannot be empty"
                return@setOnClickListener
            }

            authViewModel.signUp(this, name, phone, email, password, base64NoWrap)
        }
    }
}