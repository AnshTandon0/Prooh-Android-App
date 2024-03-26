package com.androidants.sampleapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.common.Utils
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.DeviceInfo
import com.androidants.sampleapp.data.model.log.LogInput
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferencesClass: SharedPreferencesClass
    private var point = 0
    private var arrayList = mutableListOf<VideoData>()
    private lateinit var logReport: LogReport


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        checkPermission()
        initializeLogReport()
        initSharedPreferences()
        setupInitialVideo()
        initViewModel()
        checkInternetConnectionStatus()

        binding.videoView.setOnCompletionListener {
            checkStatus()
        }
    }

    @SuppressLint("HardwareIds")
    private fun initializeLogReport() {
        val deviceInfo = DeviceInfo()
        deviceInfo.deviceIp = Utils.getIPAddress(true)
        deviceInfo.deviceMac = Utils.getMACAddress("eth0")
        deviceInfo.deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        deviceInfo.deviceDisplay = android.os.Build.MODEL
        logReport = LogReport(deviceInfo = deviceInfo)
        Log.d(Constants.TAG , logReport.toString())
    }

    private fun initSharedPreferences() {
        sharedPreferencesClass = SharedPreferencesClass(this@MainActivity)
    }

    private fun setupInitialVideo() {
        binding.videoView.setVideoPath(Constants.INITIAL_VIDEO_PATH)
        binding.videoView.start()
        binding.videoView.setOnErrorListener { mediaPlayer, i, i2 ->
            checkStatus()
            return@setOnErrorListener true
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.getVideoResponse.observe (this) { it ->
            createArrayList(it)
        }
        viewModel.downloadManagerId.observe(this) {
            for ( data in arrayList )
                if ( data.cid == it.cid ) {
                    data.downloadId = it.downloadId
                }
            Log.d(Constants.TAG  , arrayList.toString())
        }

        viewModel.getInternetConnectionStatus.observe(this){
            if ( it == true )
                getVideoData()
            else
                checkInternetConnectionStatus()
        }
    }

    private fun checkInternetConnectionStatus() {
        lifecycleScope.launch {
            viewModel.internetConnectionStatus(this@MainActivity)
        }
    }

    private fun getVideoData() {
        lifecycleScope.launch {
            viewModel.getVideos(sharedPreferencesClass.getScreenCode())
        }
    }

    private fun postLogData() {
        lifecycleScope.launch {
            viewModel.postLogs(sharedPreferencesClass.getScreenId() , logReport)
        }

        Log.d(Constants.TAG , logReport.toString())
        logReport.data.clear()
    }

    private fun checkFileExists (fileName : String) : Boolean {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        files?.forEach { file ->
            if (file.name == fileName)
                return true
        }
        return false
    }

    private fun deleteAdditionalFiles() {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        files?.forEach { file ->
            var exists = false
            arrayList.forEach { videoData ->
                if ( file.name == videoData.filename )
                    exists = true
            }
            if ( exists == false )
                file.delete()
        }
    }

    private fun createArrayList(it : GetVideoResponse) {

        sharedPreferencesClass.setScreenId(it.screen?.Id.toString())

        Log.d(Constants.TAG  , it.toString())
        val list = arrayListOf<VideoData>()

        for ( data in it.myScreenVideos )
        {
            val newData = VideoData( cid = data.cid.toString() , type = data.fileType.toString() , url = data.video.toString() )

            if( !data.duration.toString().isEmpty() )
                newData.duration = data.duration.toString()

            when ( newData.type )
            {
                Constants.TYPE_VIDEO -> {
                    newData.filename = newData.cid + Constants.VIDEO_TYPE
                }
                Constants.TYPE_IMAGE -> {
                    newData.filename = newData.cid + Constants.IMAGE_TYPE
                }
                Constants.TYPE_URL -> {
                    newData.address = data.video.toString()
                    newData.status = Constants.STATUS_DONE
                }
            }

            if ( !checkFileExists(newData.filename) && data.fileType != Constants.TYPE_URL)
            {
                lifecycleScope.launch{
                    viewModel.downloadVideo(this@MainActivity , newData)
                }
            }
            else{
                newData.status = Constants.STATUS_DONE
                newData.address = Constants.DOWNLOAD_FOLDER_PATH + newData.filename
            }

            if ( data.atIndex.size == 0 )
                data.atIndex.add(0)

            for ( id in data.atIndex )
            {
                newData.index = id
                list.add(newData)
            }
        }
        list.sortBy { it.index }
        arrayList.clear()
        arrayList.addAll(list)
        deleteAdditionalFiles()
    }

    private fun checkStatus()
    {
        if ( sharedPreferencesClass.checkSuccessEmpty() )
        {
            setupInitialVideo()
        }
        else
        {
            if ( point >= arrayList.size )
            {
                point = 0
                postLogData()
                getVideoData()
            }
            while ( point < arrayList.size )
            {
                if ( arrayList[point].status == Constants.STATUS_DONE || checkDownloadStatus() )
                {
                    Log.d(Constants.TAG  , "Calling Set Data")
                    setData()
                    return
                }
                Log.d(Constants.TAG  , "Point Increment")
                point++
            }
            checkStatus()
        }
    }

    private fun setData()
    {
        val map = mutableMapOf<String , String>()
        map.put(Calendar.getInstance().time.toString() , arrayList[point].cid)
        map.put("deviceInfo" , logReport.deviceInfo.toString())
        logReport.data.add(map)

        Log.d(Constants.TAG , arrayList[point].toString())
        when( arrayList[point].type )
        {
            Constants.TYPE_VIDEO ->{
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                Log.d(Constants.TAG , arrayList[point].address)
                binding.videoView.setVideoPath(arrayList[point].address)
                binding.videoView.start()
                Log.d(Constants.TAG  , "Video Preview Start")
            }
            Constants.TYPE_IMAGE -> {
                binding.imageView.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
                binding.videoView.visibility = View.GONE
                val file = File(arrayList[point].address)
                binding.imageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                object : CountDownTimer(arrayList[point].duration.toLong() * 1000, 1000){
                    override fun onTick(p0: Long){}
                    override fun onFinish() {
                        checkStatus()
                    }
                }.start()
                Log.d(Constants.TAG  , "Image Preview Start")
            }
            Constants.TYPE_URL -> {
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
                binding.webView.loadUrl(arrayList[point].url)
                binding.webView.getSettings().javaScriptEnabled = true
                binding.webView.webViewClient = WebViewClient()
                object : CountDownTimer(arrayList[point].duration.toLong() * 1000, 1000){
                    override fun onTick(p0: Long){}
                    override fun onFinish() {
                        checkStatus()
                    }
                }.start()
                Log.d(Constants.TAG  , "Url Preview Start")
            }
        }
        point ++
    }

    private fun checkDownloadStatus() : Boolean
    {
        Log.d(Constants.TAG  , "Check Status")
        if(sharedPreferencesClass.checkSuccessIdExists(arrayList[point].downloadId.toString()))
        {
            Log.d(Constants.TAG  , "Check Status Cnf")
            arrayList[point].status = Constants.STATUS_DONE
            arrayList[point].address = Constants.DOWNLOAD_FOLDER_PATH + arrayList[point].filename
            sharedPreferencesClass.addSuccessId(arrayList[point].filename)
            return true
        }
        else if ( sharedPreferencesClass.checkFailureIdExists(arrayList[point].downloadId.toString()) )
        {
            Log.d(Constants.TAG  , "Check Status failed")
            lifecycleScope.launch {
                viewModel.downloadVideo(this@MainActivity , arrayList[point])
            }
            sharedPreferencesClass.deleteFailureId(arrayList[point].downloadId.toString())
        }
        Log.d(Constants.TAG  , "Check Status None")
        return false
    }

    private fun checkPermission() {
        Log.d(Constants.TAG  , "Check Status None")
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED ) {
            Log.d(Constants.TAG  , "Check Status None")
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) ,0)
        }
        else
        {
            Log.d(Constants.TAG  , "Check Status")
            setupInitialVideo()
            initViewModel()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupInitialVideo()
                initViewModel()
            }
        }
    }
}