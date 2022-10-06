package com.xvlaze.zapalerts.ui

import android.content.Intent
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
            finish()
        } else {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
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
                    Intent(this, LoginActivity::class.java).apply {
                        startActivity(this)
                    }
                    finish()
                }
                else {
                    Toast.makeText(this@LoginActivity, getString(R.string.something_wrong), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}