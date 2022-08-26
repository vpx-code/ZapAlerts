package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.auth.*
import com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN
import com.huawei.hmf.tasks.Task
import com.huawei.hmf.tasks.TaskExecutors
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.databinding.ActivityLoginBinding
import com.xvlaze.zapalerts.model.User


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var countryCode: String
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AGConnectAuth.getInstance().currentUser != null) {
            User.unionId = AGConnectAuth.getInstance().currentUser.uid
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        } else {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    binding.bg.setImageResource(R.drawable.city_night)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    binding.bg.setImageResource(R.drawable.city_day)
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    binding.bg.setImageResource(R.drawable.city_day)
                }
            }


            binding.sendCode.setOnClickListener {
                val settings = VerifyCodeSettings.newBuilder()
                    .action(ACTION_REGISTER_LOGIN)
                    .sendInterval(30)
                    .build()

                val task: Task<VerifyCodeResult> =
                    PhoneAuthProvider.requestVerifyCode(
                        binding.countryPicker.selectedCountryCode,
                        binding.phoneNumber.text.toString(),
                        settings
                    )

                task.addOnSuccessListener(TaskExecutors.uiThread()) {
                    countryCode = binding.countryPicker.selectedCountryCode
                    phoneNumber = binding.phoneNumber.text.toString()

                    binding.signUp.setOnClickListener {
                        signUp()
                    }

                    binding.signIn.setOnClickListener {
                        signIn()
                    }
                }
            }
        }
    }

    private fun signIn() {
        val credential = PhoneAuthProvider.credentialWithVerifyCode(
            countryCode,
            phoneNumber,
            null,
            binding.verificationCode.text.toString()
        )

        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
            // The verification code application is successful.
            PhoneUser.Builder()
                .setCountryCode(countryCode)
                .setPhoneNumber(phoneNumber) // The value of phoneNumber must contains the country/region code and mobile number.
                .setVerifyCode(binding.verificationCode.text.toString())
                .build()

            AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener {
                    User.unionId = it.user.uid
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
                .addOnFailureListener {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
        }
    }

    private fun signUp() {
        val phoneUser = PhoneUser.Builder()
            .setCountryCode(countryCode)
            .setPhoneNumber(phoneNumber) // The value of phoneNumber must contains the country/region code and mobile number.
            .setVerifyCode(binding.verificationCode.text.toString())
            .build()

        AGConnectAuth.getInstance().createUser(phoneUser)
            .addOnSuccessListener {
                User.unionId = it.user.uid
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(
                    this@LoginActivity,
                    "Something went wrong, please try again",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}