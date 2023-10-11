package com.rabbithole.biometricauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CpuUsageInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    private lateinit var ivFingerprint: ImageView
    private lateinit var tvInfo: TextView
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivFingerprint = findViewById(R.id.ivFingerprint)
        tvInfo = findViewById(R.id.tvInfo)

        ivFingerprint.setOnClickListener{
            checkDeviceHasBiometric()
        }



    }

    private fun createBiometricListener(){
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object: BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                tvInfo.text = errString
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                tvInfo.text = "Auth failed"
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                tvInfo.text = "Auth success"
            }
        })
    }

    private fun createPromptInfo(){
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for app")
            .setSubtitle("Login use your biometric")
            .setNegativeButtonText("Cancel")
            .build()
    }

    private fun checkDeviceHasBiometric() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)){
            BiometricManager.BIOMETRIC_SUCCESS -> {
                tvInfo.text = "App can auth is using biometric."
                createBiometricListener()
                createPromptInfo()
                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                tvInfo.text = "No biometric feature available on this device."
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                tvInfo.text = "No biometric feature are currently unavailable."
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                tvInfo.text = "Device not enable biometric feature."
            }
            else -> {
                tvInfo.text = "Something went wrong!"
            }
        }

    }
}