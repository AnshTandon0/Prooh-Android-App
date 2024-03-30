package com.androidants.sampleapp.ui.main

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.common.Utils
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.DeviceInfo
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferencesClass: SharedPreferencesClass
    private var point = 0
    private var arrayList = mutableListOf<VideoData>()
    private lateinit var logReport: LogReport
    private var internetConnection : Boolean = true
    private var data : ArrayList<MutableMap<String , String>> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeLogReport()
        initSharedPreferences()
        initViewModel()
        checkStatus()

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
            point ++
            checkStatus()
            return@setOnErrorListener true
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.getVideoResponse.observe (this) { it ->
            it?.let {
                createArrayList(it)
            }
        }
        viewModel.downloadManagerId.observe(this) {
            for ( data in arrayList )
                if ( data.cid == it.cid ) {
                    data.downloadId = it.downloadId
                }
            Log.d(Constants.TAG  , arrayList.toString())
        }

        viewModel.getInternetConnectionStatus.observe(this){
            internetConnection = it
            if ( it == true )
                getVideoData()
            else
                createOfflineList()
        }
    }

    private fun checkInternetConnectionStatus() {
        lifecycleScope.launch(Dispatchers.IO + Constants.coroutineExceptionHandler)  {
            viewModel.internetConnectionStatus(this@MainActivity)
        }
    }

    private fun getVideoData() {
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.getVideos(sharedPreferencesClass.getScreenCode())
        }
    }

    private fun postLogData() {
        logReport.data.clear()
        logReport.data.addAll(data)
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.postLogs(sharedPreferencesClass.getScreenId() , logReport)
        }

        Log.d(Constants.TAG , logReport.toString())
        data.clear()
    }

    private fun createOfflineList() {
        val data = sharedPreferencesClass.getFileData()
        arrayList.clear()
        arrayList.addAll(data)
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
            if ( exists == false ) {
                sharedPreferencesClass.deleteSuccessId(file.name)
                sharedPreferencesClass.deleteDownloadingId(file.name)
                sharedPreferencesClass.deleteFailureId(file.name)
                file.delete()
            }
        }
    }

    private fun createArrayList(it : GetVideoResponse) {

        sharedPreferencesClass.setScreenId(it.screen?.Id.toString())

        Log.d(Constants.TAG  , "In create list")
        Log.d(Constants.TAG  , it.toString())
        val list = arrayListOf<VideoData>()

        for ( data in it.myScreenVideos )
        {
            val type = data.fileType?.split("/")?.toTypedArray()

            val newData = VideoData( cid = data.cid.toString() , type = type?.get(0).toString() , url = data.video.toString() , filesize = data.fileSize?.toLong() ?: 0 )

            if( !data.duration.toString().isEmpty() )
                newData.duration = data.duration.toString()

            when ( newData.type )
            {
                Constants.TYPE_VIDEO -> {
                    newData.filename = newData.cid + Constants.DOT + type?.get(1).toString()
                }
                Constants.TYPE_IMAGE -> {
                    newData.filename = newData.cid + Constants.DOT + type?.get(1).toString()
                }
                Constants.TYPE_URL -> {
                    newData.address = data.video.toString()
                }
                Constants.TYPE_YOUTUBE -> {
                    newData.address = data.video.toString()
                }
            }

            if ( !sharedPreferencesClass.checkSuccessIdExists(newData.filename) )
            {
                if (!sharedPreferencesClass.checkDownloadingIdExists(newData.filename)){
                    sharedPreferencesClass.addDownloadingId(newData.filename)
                    lifecycleScope.launch(Dispatchers.IO + Constants.coroutineExceptionHandler){
                        viewModel.downloadVideo(this@MainActivity , newData)
                    }
                }
                else if ( checkFileExists(newData.filename , newData.filesize) )
                {
                    newData.address = Constants.DOWNLOAD_FOLDER_PATH + newData.filename
                    sharedPreferencesClass.deleteDownloadingId(newData.filename)
                    sharedPreferencesClass.addSuccessId(newData.filename)
                }
            }
            else if ( newData.address == "" ){
                newData.address = Constants.DOWNLOAD_FOLDER_PATH + newData.filename
                sharedPreferencesClass.deleteDownloadingId(newData.filename)
                sharedPreferencesClass.addSuccessId(newData.filename)
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
        sharedPreferencesClass.saveFileData(list)
        arrayList.clear()
        arrayList.addAll(list)
        deleteAdditionalFiles()
    }

    private fun checkFileExists (fileName : String , size : Long) : Boolean {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        files?.forEach { file ->
            val fileSize = file.length()/1024
            if (file.name == fileName && fileSize == size )
                return true
        }
        return false
    }

    private fun checkStatus()
    {
        Log.d(Constants.TAG  , "In check status function")
        checkInternetConnectionStatus()
        if ( sharedPreferencesClass.checkSuccessEmpty() || arrayList.size == 0 )
        {
            Log.d(Constants.TAG  , "Starting Default video")
            setupInitialVideo()
        }
        else
        {
            if ( point >= arrayList.size )
            {
                point = 0
                Log.d(Constants.TAG , "Resetting Point to 0")
                if ( internetConnection )
                {
                    Log.d(Constants.TAG , "Posting log Data")
                    postLogData()
                }
            }
            while ( point < arrayList.size )
            {
                Log.d(Constants.TAG  , sharedPreferencesClass.checkDownloadingIdExists(arrayList[point].filename).toString())
                Log.d(Constants.TAG  , sharedPreferencesClass.checkSuccessIdExists(arrayList[point].filename).toString())
                if ( arrayList[point].address != "" || checkFileDownloadStatus() )
                {
                    Log.d(Constants.TAG  , "Calling Set Data To Views")
                    setDataToViews()
                    return
                }
                Log.d(Constants.TAG  , "Point Increment")
                point++
            }
            checkStatus()
        }
    }

    private fun setDataToViews()
    {
        val map = mutableMapOf<String , String>()
        map.put(Calendar.getInstance().time.toString() , arrayList[point].cid)
        map.put("deviceInfo" , logReport.deviceInfo.toString())
        data.add(map)

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
                if ( !internetConnection )
                {
                    point ++
                    checkStatus()
                    return
                }
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

    private fun checkFileDownloadStatus() : Boolean
    {
        Log.d(Constants.TAG  , "Checking File Download Status")
        if(sharedPreferencesClass.checkSuccessIdExists(arrayList[point].filename))
        {
            Log.d(Constants.TAG  , "File Downloading Completed")
            arrayList[point].address = Constants.DOWNLOAD_FOLDER_PATH + arrayList[point].filename
            sharedPreferencesClass.addSuccessId(arrayList[point].filename)
            sharedPreferencesClass.deleteDownloadingId(arrayList[point].filename)
            return true
        }
        else if ( sharedPreferencesClass.checkFailureIdExists(arrayList[point].filename) )
        {
            Log.d(Constants.TAG  , "File downloading Failed")
            lifecycleScope.launch {
                viewModel.downloadVideo(this@MainActivity , arrayList[point])
            }
            sharedPreferencesClass.deleteFailureId(arrayList[point].filename)
        }
        Log.d(Constants.TAG  , "File still in downloading state")
        return false
    }

    // Todo add code for desync part
}