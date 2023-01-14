package com.blez.beacontransmitter.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.blez.beacontransmitter.R
import com.blez.beacontransmitter.databinding.ActivitySplashScreenBinding
import com.blez.beacontransmitter.ui.SetupActivity
import com.blez.beacontransmitter.ui.register.RegisterActivity
import com.blez.beacontransmitter.utils.CredentialManager
import java.util.*
import kotlin.concurrent.timerTask

class SplashScreen : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var credentialManager: CredentialManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash_screen)
        supportActionBar?.hide()
        credentialManager = CredentialManager(this)
        runActivity()

    }
   private fun runActivity() {
        if (!isDestroyed) {


            val intentRegister = Intent(this, RegisterActivity::class.java)
            val intentBeacon = Intent(this, SetupActivity::class.java)

            val tmtask = timerTask {
                if (!isDestroyed) {
                    if (credentialManager.getUUID() != null) {
                        startActivity(intentBeacon)
                        finish()
                    } else
                        startActivity(intentRegister)
                    finish()
                }
            }
            val timer = Timer()
            timer.schedule(tmtask, 2500)
        }

    }
}