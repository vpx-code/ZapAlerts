package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.databinding.ActivityLoginBinding
import com.xvlaze.zapalerts.model.User


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

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
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                // we need access token to create credential.
                val accessToken = huaweiAccount.accessToken
                val credential = HwIdAuthProvider.credentialWithToken(accessToken)
                AGConnectAuth.getInstance().signIn(credential)
                    .addOnSuccessListener { signInResult -> // onSuccess
                        val user = signInResult.user
                        User.unionId = user.uid
                        Toast.makeText(this, user.uid, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "HwID signIn failed: " + authHuaweiIdTask.exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}