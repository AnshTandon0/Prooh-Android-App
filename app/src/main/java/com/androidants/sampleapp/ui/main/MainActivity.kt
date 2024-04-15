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
import com.androidants.sampleapp.common.MyExceptionHandler
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.common.Utils
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.DeviceInfo
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.MyScreenVideos
import com.androidants.sampleapp.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferencesClass: SharedPreferencesClass
    private var point = 0
    private var activeCampaigns = mutableListOf<VideoData>()
    private var finalList  = mutableListOf<VideoData>()
    private var pausedCampaigns = mutableListOf<VideoData>()
    private var holdCampaigns = mutableListOf<VideoData>()
    private lateinit var logReport: LogReport
    private var internetConnection : Boolean = true
    private var logMap = mutableMapOf<String , String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this))

        initSharedPreferences()
        initializeLogReport()
        initViewModel()
        checkStatus()
        checkInternetConnectionStatus()
    }

    private fun initSharedPreferences() {
        sharedPreferencesClass = SharedPreferencesClass(this@MainActivity)
    }

    @SuppressLint("HardwareIds")
    private fun initializeLogReport() {

        if ( sharedPreferencesClass.checkLogs() )
            logReport = sharedPreferencesClass.getLogs()
        else
        {
            val deviceInfo = DeviceInfo()
            deviceInfo.deviceIp = Utils.getIPAddress(true)
            deviceInfo.deviceMac = Utils.getMACAddress("eth0")
            deviceInfo.deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            deviceInfo.deviceDisplay = android.os.Build.MODEL
            logReport = LogReport(deviceInfo = deviceInfo)
            sharedPreferencesClass.saveLogs(logReport)
        }
        createLogMap()
        Log.d(Constants.TAG_NORMAL , logReport.toString())
    }

    private fun createLogMap() {
        logMap.put("\"" +"deviceIp" + "\"" , "\"" + Utils.getIPAddress(true) + "\"" )
        logMap.put("\"" + "deviceMaac" + "\"" , "\"" + Utils.getMACAddress("eth0") + "\"" )
        logMap.put( "\"" + "deviceDisplay" +"\"" , "\"" + Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) + "\"" )
        logMap.put("\"" + "deviceId" + "\"" , "\"" + android.os.Build.MODEL + "\"" )
    }

    private fun setupInitialVideo() {
        binding.imageView.visibility = View.GONE
        binding.webView.visibility = View.GONE
        binding.youtubePlayer.visibility = View.GONE
        binding.videoView.visibility = View.VISIBLE
        binding.videoView.setVideoPath(Constants.INITIAL_VIDEO_PATH)
        binding.videoView.start()
        binding.videoView.setOnErrorListener { mediaPlayer, i, i2 ->
            point ++
            checkStatus()
            return@setOnErrorListener true
        }

        binding.videoView.setOnCompletionListener {
            checkStatus()
        }
    }

    private fun deleteAllFiles () {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        files?.forEach { file ->
            file.delete()
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.getVideoResponse.observe (this) { it ->
            if ( it == null )
            {
                finalList.clear()
                activeCampaigns.clear()
                sharedPreferencesClass.saveFileData(finalList)
                deleteAllFiles()
            }
            else {
                sharedPreferencesClass.setScreenId(it.screen?.Id.toString())
                it.myScreenVideos.let {
                    createList(it , Constants.ACTIVE_CAMPAIGN_LIST)
                }
                it.holdCampaigns.let {
                    createList(it , Constants.HOLD_CAMPAIGN_LIST)
                }
                it.pausedCampaigns.let {
                    createList(it , Constants.PAUSED_CAMPAIGN_LIST)
                }
            }
        }
        viewModel.downloadManagerId.observe(this) {
            for ( data in activeCampaigns )
                if ( data.cid == it.cid ) {
                    data.downloadId = it.downloadId
                }
            Log.d(Constants.TAG_NORMAL  , "Download Manager Id")
            Log.d(Constants.TAG_NORMAL  , activeCampaigns.toString())
        }

        viewModel.getInternetConnectionStatus.observe(this){
            internetConnection = it
            if ( it == true )
                getData()
            else
                createOfflineList()
        }
    }

    private fun checkInternetConnectionStatus() {
        lifecycleScope.launch(Dispatchers.IO + Constants.coroutineExceptionHandler)  {
            viewModel.internetConnectionStatus(this@MainActivity)
        }
    }

    private fun getData() {
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.getVideos(sharedPreferencesClass.getScreenCode())
        }
    }

    private fun downloadVideo(videoData: VideoData){
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.downloadVideo(this@MainActivity , videoData)
        }
    }

    private fun postLogData() {
        logReport.data.clear()
        logReport.data.addAll(sharedPreferencesClass.getLogs().data)
        Log.d(Constants.TAG_NORMAL  , "Log Report")
        Log.d(Constants.TAG_NORMAL , logReport.toString())
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.postLogs(sharedPreferencesClass.getScreenId() , logReport)
        }

        Log.d(Constants.TAG_NORMAL  , "Log Report")
        Log.d(Constants.TAG_NORMAL , logReport.toString())
        val logReport = sharedPreferencesClass.getLogs()
        logReport.data.clear()
        sharedPreferencesClass.saveLogs(logReport)
    }

    private fun createOfflineList() {
        val data = sharedPreferencesClass.getFileData()
        finalList.clear()
        finalList.addAll(data)
    }

    private fun deleteAdditionalFiles() {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        files?.forEach { file ->
            var exists = false
            activeCampaigns.forEach { videoData ->
                Log.d(Constants.TAG_NORMAL  , videoData.filename)
                if ( !exists && file.name == videoData.filename )
                    exists = true
            }
            holdCampaigns.forEach { videoData ->
                Log.d(Constants.TAG_NORMAL  , videoData.filename)
                if ( !exists && file.name == videoData.filename )
                    exists = true
            }
            pausedCampaigns.forEach { videoData ->
                Log.d(Constants.TAG_NORMAL  , videoData.filename)
                if ( !exists && file.name == videoData.filename )
                    exists = true
            }
            if ( exists == false || file.length() == 0L ) {
                Log.d(Constants.TAG_NORMAL  , "Deleting file")
                Log.d(Constants.TAG_NORMAL  , file.name)
                sharedPreferencesClass.deleteSuccessId(file.name)
                sharedPreferencesClass.deleteDownloadingId(file.name)
                sharedPreferencesClass.deleteFailureId(file.name)
                file.delete()
            }
        }
    }

    private fun checkStatus()
    {
        Log.d(Constants.TAG_NORMAL  , "In check status function")
        if ( finalList.size == 0 ) {
            Log.d(Constants.TAG_NORMAL  , "Starting Default video")
            setupInitialVideo()
        }
        else {
            if ( point >= finalList.size ) {
                point = 0
                checkInternetConnectionStatus()
                Log.d(Constants.TAG_NORMAL , "Resetting Point to 0")
                if ( internetConnection )
                {
                    Log.d(Constants.TAG_NORMAL , "Posting log Data")
                    postLogData()
                }
            }
            setDataToViews()
            Log.d(Constants.TAG_NORMAL , "Setting Data To Views")
        }
    }

    private fun addLog () {
        val map = mutableMapOf<String , String>()
        map.put(Calendar.getInstance().time.toString() , finalList[point].cid)
        val status = if (internetConnection) "\"" + "online" + "\"" else "\"" + "offline" + "\""
        logMap.put("\"" +"deviceStatus" + "\"" , status )
        var temp = logMap.toString()
        temp = temp.replace('=' , ':')
        map.put("deviceInfo" , temp)

        val logReport = sharedPreferencesClass.getLogs()
        logReport.data.add(map)
        sharedPreferencesClass.saveLogs(logReport)
        Log.d(Constants.TAG_NORMAL  , "Adding Log Report")
        Log.d(Constants.TAG_NORMAL , logReport.toString())
    }

    private fun setDataToViews()
    {
        addLog()
        Log.d(Constants.TAG_NORMAL  , "In Set Data to Views")
        Log.d(Constants.TAG_NORMAL , finalList[point].toString())
        when( finalList[point].type )
        {
            Constants.TYPE_VIDEO ->{
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.GONE
                binding.youtubePlayer.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                Log.d(Constants.TAG_NORMAL , finalList[point].address)
                binding.videoView.setVideoPath(finalList[point].address)
                binding.videoView.start()
                Log.d(Constants.TAG_NORMAL  , "Video Preview Start")
            }
            Constants.TYPE_IMAGE -> {
                binding.imageView.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
                binding.youtubePlayer.visibility = View.GONE
                binding.videoView.visibility = View.GONE
                val file = File(finalList[point].address)
                binding.imageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                object : CountDownTimer(finalList[point].duration.toLong() * 1000, 1000){
                    override fun onTick(p0: Long){}
                    override fun onFinish() {
                        checkStatus()
                    }
                }.start()
                Log.d(Constants.TAG_NORMAL  , "Image Preview Start")
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
                binding.youtubePlayer.visibility = View.GONE
                binding.videoView.visibility = View.GONE
                binding.webView.loadUrl(finalList[point].url)
                binding.webView.getSettings().javaScriptEnabled = true
                binding.webView.webViewClient = WebViewClient()
                object : CountDownTimer(finalList[point].duration.toLong() * 1000, 1000){
                    override fun onTick(p0: Long){}
                    override fun onFinish() {
                        binding.webView.loadUrl(Constants.DEFAULT_WEBVIEW_URL)
                        checkStatus()
                    }
                }.start()
                Log.d(Constants.TAG_NORMAL  , "Url Preview Start")
            }

            Constants.TYPE_YOUTUBE -> {
                if ( !internetConnection )
                {
                    point ++
                    checkStatus()
                    return
                }
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.GONE
                binding.youtubePlayer.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
                lifecycle.addObserver(binding.youtubePlayer)

                binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(finalList[point].address, 0F)
                        youTubePlayer.play()
                    }

                    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                        super.onVideoDuration(youTubePlayer, duration)
                        checkStatus()
                    }
                })
                object : CountDownTimer(finalList[point].duration.toLong() * 1000, 1000){
                    override fun onTick(p0: Long){}
                    override fun onFinish() {

                    }
                }.start()
                Log.d(Constants.TAG_NORMAL  , "You Tube Preview Start")
            }
        }
        point ++
    }

    private fun createList ( it : ArrayList<MyScreenVideos> , listType : String )
    {
        val list = arrayListOf<VideoData>()

        for ( data in it )
        {
            val type = data.fileType?.split("/")?.toTypedArray()
            val newData = VideoData( awsUrl = data.awsUrl.toString() , cid = data.cid.toString() , type = type?.get(0).toString() , url = data.video.toString() , filesize = data.fileSize?.toLong() ?: 0 )

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
                    newData.filename = newData.cid
                    if( !sharedPreferencesClass.checkSuccessIdExists(newData.filename) )
                        sharedPreferencesClass.addSuccessId(newData.filename)
                }
                Constants.TYPE_YOUTUBE -> {
                    newData.address = getYouTubeId(newData.url).toString()
                    newData.filename = newData.cid
                    if( !sharedPreferencesClass.checkSuccessIdExists(newData.filename) )
                        sharedPreferencesClass.addSuccessId(newData.filename)
                }
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

        when ( listType )
        {
            Constants.ACTIVE_CAMPAIGN_LIST -> {
                activeCampaigns.clear()
                activeCampaigns.addAll(list)
            }

            Constants.HOLD_CAMPAIGN_LIST -> {
                holdCampaigns.clear()
                holdCampaigns.addAll(list)
            }

            Constants.PAUSED_CAMPAIGN_LIST -> {
                pausedCampaigns.clear()
                pausedCampaigns.addAll(list)
                deleteAdditionalFiles()
                checkActiveCampaigns()
            }
        }
    }

    private fun checkActiveCampaigns()
    {
        var i = 0
        val list = mutableListOf<VideoData>()

        Log.d(Constants.TAG_NORMAL  , checkFileExists(activeCampaigns[i]).toString())
        while ( i < activeCampaigns.size )
        {
            if ( checkFileExists(activeCampaigns[i]) ) {
                Log.d(Constants.TAG_NORMAL  , "File exists")
                if( activeCampaigns[i].address == "" )
                    activeCampaigns[i].address = Constants.DOWNLOAD_FOLDER_PATH + activeCampaigns[i].filename
                list.add(activeCampaigns[i])
            }
            else if (sharedPreferencesClass.getAllDownloadingIdSize() < 2 &&
                !sharedPreferencesClass.checkDownloadingIdExists(activeCampaigns[i].filename)) {
                Log.d(Constants.TAG_NORMAL  , "Downloading file")
                sharedPreferencesClass.addDownloadingId(activeCampaigns[i].filename)
                sharedPreferencesClass.deleteFailureId(activeCampaigns[i].filename)
                downloadVideo(activeCampaigns[i])
            }
            i++
        }

        finalList.clear()
        finalList.addAll(list)
        sharedPreferencesClass.saveFileData(list)

        if ( list.size == activeCampaigns.size ){
            if ( holdCampaigns.size > 0 )
                checkHoldCampaigns()
            else if ( pausedCampaigns.size > 0 )
                checkPausedCampaigns()
            return
        }
    }

    private fun checkHoldCampaigns(  ){
        var i = 0
        val list = mutableListOf<VideoData>()

        Log.d(Constants.TAG_NORMAL  , checkFileExists(holdCampaigns[i]).toString())
        while ( i < holdCampaigns.size )
        {
            if ( checkFileExists(holdCampaigns[i]) )
            {
                Log.d(Constants.TAG_NORMAL  , "File exists")
                if( holdCampaigns[i].address == "" )
                    holdCampaigns[i].address = Constants.DOWNLOAD_FOLDER_PATH + holdCampaigns[i].filename
                list.add(holdCampaigns[i])
                i++
            }
            else if ( sharedPreferencesClass.getAllDownloadingIdSize() < 2 &&
                !sharedPreferencesClass.checkDownloadingIdExists(holdCampaigns[i].filename) ) {
                Log.d(Constants.TAG_NORMAL  , "Downloading file")
                downloadVideo(holdCampaigns[i])
                sharedPreferencesClass.addDownloadingId(holdCampaigns[i].filename)
                sharedPreferencesClass.deleteFailureId(holdCampaigns[i].filename)
            }
        }

        if ( list.size == holdCampaigns.size ){
            if ( pausedCampaigns.size > 0 )
                checkPausedCampaigns()
            return
        }
    }

    private fun checkPausedCampaigns(){
        var i = 0
        val list = mutableListOf<VideoData>()

        Log.d(Constants.TAG_NORMAL  , checkFileExists(pausedCampaigns[i]).toString())
        while ( i < pausedCampaigns.size )
        {
            if ( checkFileExists(pausedCampaigns[i]) )
            {
                Log.d(Constants.TAG_NORMAL  , "File exists")
                if( pausedCampaigns[i].address == "" )
                    pausedCampaigns[i].address = Constants.DOWNLOAD_FOLDER_PATH + pausedCampaigns[i].filename
                list.add(pausedCampaigns[i])
                i++
            }
            else if ( sharedPreferencesClass.getAllDownloadingIdSize() < 2 &&
                !sharedPreferencesClass.checkDownloadingIdExists(pausedCampaigns[i].filename) ) {
                Log.d(Constants.TAG_NORMAL  , "Downloading file")
                sharedPreferencesClass.addDownloadingId(pausedCampaigns[i].filename)
                sharedPreferencesClass.deleteFailureId(pausedCampaigns[i].filename)
                downloadVideo(pausedCampaigns[i])
            }

        }

        if ( list.size == pausedCampaigns.size ){
            return
        }
    }

    private fun checkFileExists (videoData: VideoData) : Boolean {

        Log.d(Constants.TAG_NORMAL  , "In Check File exists")
        if (videoData.type == Constants.TYPE_URL || videoData.type == Constants.TYPE_YOUTUBE)
            return true

        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        Log.d(Constants.TAG_NORMAL  , files.toString())
        files?.forEach { file ->
            val fileSize = file.length()
            Log.d(Constants.TAG_NORMAL  , fileSize.toString())
            Log.d(Constants.TAG_NORMAL  , videoData.filesize.toString())
            if (file.name == videoData.filename && fileSize == videoData.filesize ) {
                sharedPreferencesClass.deleteDownloadingId(videoData.filename)
                return true
            }
        }
        return false
    }

    private fun getYouTubeId(youTubeUrl: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
        val compiledPattern: Pattern = Pattern.compile(pattern)
        val matcher: Matcher = compiledPattern.matcher(youTubeUrl)
        return if (matcher.find()) {
            Log.d(Constants.TAG_NORMAL , "youtube" + matcher.group())
            matcher.group()
        } else {
            "error"
        }
    }
}