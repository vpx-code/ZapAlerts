package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.databinding.ActivityLoginBinding
import com.xvlaze.zapalerts.model.User


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

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
            binding.signIn.setOnClickListener {
                val authParams =
                    HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setAccessToken()
                        .createParams()
                val service = HuaweiIdAuthManager.getService(this, authParams)
                startActivityForResult(service.signInIntent, 100)
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            viewModel.signIn(data)
            viewModel.isSignInSuccessful.observe(this) {
                if (it) {
                    finish()
                    Intent(this, LoginActivity::class.java).apply {
                        startActivity(this)
                    }
                }
                else {
                    Toast.makeText(this@LoginActivity, "Something went wrong, please try again later.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}