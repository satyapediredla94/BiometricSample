package com.example.biometricsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.biometricsample.BiometricHelper.checkBiometricSupport
import com.example.biometricsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        checkBiometricSupport(this) {
            if (it) {
                mBinding.bioPrompt.setOnClickListener {
                    BiometricHelper.showBiometricPrompt(this)
                }
            }
        }
    }

}