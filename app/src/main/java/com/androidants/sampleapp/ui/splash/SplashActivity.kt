package com.androidants.sampleapp.ui.splash

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.databinding.ActivitySplashBinding
import com.androidants.sampleapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
        checkPermission()
    }

    private fun initSharedPreferences() {
        sharedPreferencesClass = SharedPreferencesClass(this@SplashActivity)
    }

    private fun setViews() {
        binding.code.text = checkScreenCode()

        binding.code.setOnClickListener {
            binding.code.text = generateRandomString()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        viewModel.getInternetConnectionStatus.observe(this){
            Log.d(Constants.TAG , it.toString())
            if ( it == true )
                getVideoData()
            else if ( sharedPreferencesClass.checkSuccessEmpty() ){
                checkInternetConnectionStatus()
                Log.d(Constants.TAG , "in shared pref")
            }
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
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.internetConnectionStatus(this@SplashActivity)
        }
    }

    private fun getVideoData() {
        lifecycleScope.launch(Dispatchers.IO + Constants.coroutineExceptionHandler)  {
            viewModel.getVideos(sharedPreferencesClass.getScreenCode())
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

    private fun checkPermission() {
        Log.d(Constants.TAG  , "Check Permission")
        if (ContextCompat.checkSelfPermission(this@SplashActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ) {
            Log.d(Constants.TAG  , "Permission Not Given")
            Log.d(Constants.TAG  , "Asking for Permission")
            ActivityCompat.requestPermissions(this@SplashActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) ,0)
        }
        else
        {
            setViews()
            initViewModel()
            checkInternetConnectionStatus()
            Log.d(Constants.TAG  , "Permission Given")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                setViews()
                initViewModel()
                checkInternetConnectionStatus()
                Log.d(Constants.TAG  , "Permission Given")
            }
        }
    }
}