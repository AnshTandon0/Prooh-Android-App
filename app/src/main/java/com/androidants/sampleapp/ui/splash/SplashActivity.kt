package com.androidants.sampleapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.databinding.ActivitySplashBinding
import com.androidants.sampleapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.random.Random.Default.nextInt

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel
    private lateinit var sharedPreferencesClass: SharedPreferencesClass
    private val charPool : List<Char> by lazy {
        ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSharedPreferences()
        setViews()
        initViewModel()
        checkInternetConnectionStatus()
    }

    private fun setViews() {
        binding.code.text = checkScreenCode()

        binding.code.setOnClickListener {
            binding.code.text = generateRandomString()
        }
    }

    private fun initSharedPreferences() {
        sharedPreferencesClass = SharedPreferencesClass(this@SplashActivity)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        viewModel.getInternetConnectionStatus.observe(this){
            if ( it == true )
                getVideoData()
            else if ( sharedPreferencesClass.checkSuccessEmpty() )
                checkInternetConnectionStatus()
            else
                startMainActivity()
        }

        viewModel.getVideoResponse.observe(this) {
            if ( it == null )
                getVideoData()
            else
                startMainActivity()
        }
    }

    private fun checkScreenCode () : String {
        if ( sharedPreferencesClass.getScreenCode() != "" )
            return sharedPreferencesClass.getScreenCode()
        else
            return generateRandomString()
    }

    private fun generateRandomString() : String {
        val randomString = (1..Constants.RANDOM_STRING_LENGTH)
            .map { nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
        sharedPreferencesClass.setScreenCode(randomString)
        return randomString
    }

    private fun checkInternetConnectionStatus() {
        lifecycleScope.launch {
            viewModel.internetConnectionStatus(this@SplashActivity)
        }
    }

    private fun getVideoData() {
        lifecycleScope.launch {
            viewModel.getVideos("qXpD36")
        }
    }

    private fun startMainActivity() {
        object : CountDownTimer(2000, 1000){
            override fun onTick(p0: Long){}
            override fun onFinish() {
                startActivity(Intent(this@SplashActivity , MainActivity::class.java))
                finish()
            }
        }.start()
    }
}