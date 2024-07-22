package com.androidants.sampleapp.ui.main

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.MyExceptionHandler
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.common.Utils
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.file.FileData
import com.androidants.sampleapp.data.model.log.*
import com.androidants.sampleapp.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
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
    private lateinit var logReportInput: LogReportInput
    private var internetConnection : Boolean = true
    private var addLogReportBool = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler(this))

        initSharedPreferences()
        initializeLogReport()
        initViewModel()
        checkStatus()
    }

    private fun initSharedPreferences() {
        sharedPreferencesClass = SharedPreferencesClass(this@MainActivity)
    }

    @SuppressLint("HardwareIds")
    private fun initializeLogReport() {

        if ( sharedPreferencesClass.checkLogs() )
            logReportInput = sharedPreferencesClass.getLogs()
        else
        {
            val screenLogs = ScreenLogs()
            screenLogs.screenIp = Utils.getIPAddress(true)
            screenLogs.screenMac = Utils.getMACAddress("wlan0")
            screenLogs.screenDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            screenLogs.screenId = sharedPreferencesClass.getScreenId()
            screenLogs.screenDisplay = android.os.Build.MODEL
            logReportInput = LogReportInput(screenLogs = screenLogs)
            sharedPreferencesClass.saveLogs(logReportInput)
        }
        Log.d(Constants.TAG_NORMAL , logReportInput.toString())
    }

    private fun setupInitialVideo() {
        binding.imageView.visibility = View.GONE
        binding.webView.visibility = View.GONE
        binding.youtubePlayer.visibility = View.GONE
        binding.videoView.visibility = View.VISIBLE
        binding.videoView.setVideoPath(Constants.INITIAL_VIDEO_PATH)
        binding.videoView.start()
        binding.videoView.setOnErrorListener { mediaPlayer, i, i2 ->
            checkStatus()
            return@setOnErrorListener true
        }

        binding.videoView.setOnCompletionListener {
            if( addLogReportBool )
                addLog()
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
            if ( it.screenId == Constants.NOT_SYNCED )
            {
                finalList.clear()
                activeCampaigns.clear()
                sharedPreferencesClass.saveFileData(finalList)
                sharedPreferencesClass.deleteAllSuccessId()
                deleteAllFiles()
            }
            else if (it.screenId == Constants.ERROR){
                createOfflineList()
            }
            else {
                sharedPreferencesClass.setScreenId(it.screenId ?: "")
                it.activeCampaigns.let {
                    createList(it , Constants.ACTIVE_CAMPAIGN_LIST)
                }
                it.holdCampaigns.let {
                    createList(it , Constants.HOLD_CAMPAIGN_LIST)
                }
                it.pauseCampaigns.let {
                    createList(it , Constants.PAUSED_CAMPAIGN_LIST)
                }
            }
        }
        viewModel.downloadManagerId.observe(this) {
            for ( data in activeCampaigns )
                if ( data.screenId == it.screenId ) {
                    data.downloadId = it.downloadId
                }
            Log.d(Constants.TAG_NORMAL  , "Download Manager Id")
            Log.d(Constants.TAG_NORMAL  , it.toString())
        }

        viewModel.getInternetConnectionStatus.observe(this){
            internetConnection = it
            if ( it == true )
                getData()
            else
                createOfflineList()
        }

        viewModel.checkFileExists.observe(this) {

            if ( it.first ) {
                sharedPreferencesClass.addSuccessId(it.second.filename)
            } else if ( sharedPreferencesClass.getAllDownloadingIdSize() < 2 &&
                !sharedPreferencesClass.checkDownloadingIdExists(it.second.filename) )
            {
                Log.d(Constants.TAG_NORMAL  , "Downloading file")
                sharedPreferencesClass.addDownloadingId(it.second.filename)
                sharedPreferencesClass.deleteFailureId(it.second.filename)
                downloadVideo(it.second)
            }
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

    private fun checkFileExists (videoData: VideoData)
    {
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.checkFileExists(this@MainActivity , videoData)
        }
    }

    private fun deleteAdditionalFiles() {
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.deleteAdditionalFiles(this@MainActivity , activeCampaigns as ArrayList<VideoData>, holdCampaigns as ArrayList<VideoData> , pausedCampaigns as ArrayList<VideoData>)
        }
    }

    private fun postLogData() {
        logReportInput.screenLogs.mediaPlaybackDetails.clear()
        logReportInput.campaignLogs.clear()
        logReportInput.screenLogs.mediaPlaybackDetails.addAll(sharedPreferencesClass.getLogs().screenLogs.mediaPlaybackDetails)
        logReportInput.campaignLogs.addAll(sharedPreferencesClass.getLogs().campaignLogs)
        Log.d(Constants.TAG_NORMAL  , "Log Report")
        Log.d(Constants.TAG_NORMAL , logReportInput.toString())
        lifecycleScope.launch (Dispatchers.IO + Constants.coroutineExceptionHandler) {
            viewModel.postLogs(logReportInput)
        }

        Log.d(Constants.TAG_NORMAL  , "Log Report")
        Log.d(Constants.TAG_NORMAL , logReportInput.toString())
        val logReport = sharedPreferencesClass.getLogs()
        logReport.screenLogs.mediaPlaybackDetails.clear()
        logReport.campaignLogs.clear()
        sharedPreferencesClass.saveLogs(logReport)
    }

    private fun createOfflineList() {
        val data = sharedPreferencesClass.getFileData()
        finalList.clear()
        finalList.addAll(data)
    }

    private fun checkStatus()
    {
        checkInternetConnectionStatus()
        Log.d(Constants.TAG_NORMAL  , "In check status function")
        if ( finalList.size == 0 ) {
            Log.d(Constants.TAG_NORMAL  , "Starting Default video")
            setupInitialVideo()
        }
        else {
            point ++
            addLogReportBool = true
            if ( point >= finalList.size ) {
                point = 0
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
        if ( point >= finalList.size )
            return

        val screenMediaDetails = ScreenMediaDetails(time = Calendar.getInstance().time.toString() , mediaId = finalList[point].mediaId
            , campaignId = finalList[point].campaignId , screenStatus = if (internetConnection)  "online" else  "offline" )
        val logReport = sharedPreferencesClass.getLogs()
        logReport.screenLogs.screenId = finalList[point].screenId
        logReport.screenLogs.mediaPlaybackDetails.add(screenMediaDetails)

        val campaignMediaDetails = CampaignMediaDetails(time = Calendar.getInstance().time.toString() , mediaId = finalList[point].mediaId
            , screenId = finalList[point].screenId , screenStatus = if (internetConnection)  "online" else  "offline" )
        val campaignLog = CampaignLogs(campaignId = finalList[point].campaignId , campaignMediaDetails)
        logReport.campaignLogs.add(campaignLog)

        sharedPreferencesClass.saveLogs(logReport)
        Log.d(Constants.TAG_NORMAL  , "Adding Log Report")
        Log.d(Constants.TAG_NORMAL , logReport.toString())
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setDataToViews()
    {
        Log.d(Constants.TAG_NORMAL  , "In Set Data to Views")
        Log.d(Constants.TAG_NORMAL , finalList.toString())
        when( finalList[point].fileType )
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
                        addLog()
                        checkStatus()
                    }
                }.start()
                Log.d(Constants.TAG_NORMAL  , "Image Preview Start")
            }
            Constants.TYPE_URL -> {
                if ( !internetConnection )
                {
                    checkStatus()
                    return
                }
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.VISIBLE
                binding.youtubePlayer.visibility = View.GONE
                binding.videoView.visibility = View.GONE
                binding.webView.loadUrl(finalList[point].url)
                binding.webView.getSettings().javaScriptEnabled = true
                binding.webView.webViewClient = object : WebViewClient() {
                    override fun onPageCommitVisible(view: WebView?, url: String?) {
                        super.onPageCommitVisible(view, url)
                        addLog()
                    }
                }
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
                    checkStatus()
                    return
                }
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.GONE
                binding.youtubePlayer.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
                Log.d(Constants.TAG_NORMAL  , finalList.toString())
                Log.d(Constants.TAG_NORMAL  , point.toString())

                val listener: YouTubePlayerListener = object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        Log.d(Constants.TAG_NORMAL  , "YouTube Preview Start")
                        Log.d(Constants.TAG_NORMAL  , point.toString())
                        youTubePlayer.loadVideo(finalList[point-1].address, 0F)
                        youTubePlayer.play()
                    }
                }

                binding.youtubePlayer.initialize(listener)
                lifecycle.addObserver(binding.youtubePlayer)

                object : CountDownTimer(finalList[point].duration.toLong() * 1000, 1000){
                    override fun onTick(p0: Long){}
                    override fun onFinish() {
                        checkStatus()
                    }
                }.start()
                Log.d(Constants.TAG_NORMAL  , "You Tube Preview Start")
            }
        }
    }

    private fun createList (it : ArrayList<FileData>, listType : String )
    {
        val list = arrayListOf<VideoData>()

        for ( data in it )
        {
            val newData = VideoData( screenId = data.screenId.toString() , campaignId = data.campaignId.toString() ,
                mediaId = data.mediaId.toString() , fileType = data.fileType.toString() , url = data.url.toString() ,
                filesize = data.fileSize?.toLong() ?: 0 , filename = data.fileName.toString())

            if( !data.duration.toString().isEmpty() )
                newData.duration = data.duration.toString()

            when ( newData.fileType )
            {
                Constants.TYPE_URL -> {
                    newData.address = data.url.toString()
                }
                Constants.TYPE_YOUTUBE -> {
                    newData.address = getYouTubeId(newData.url).toString()
                }
            }

            if ( data.atIndex.size == 0 )
                data.atIndex.add(0)

            for ( id in data.atIndex )
            {
                newData.index = id
                val tempData = newData.copy()
                list.add(tempData)
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

        while ( i < activeCampaigns.size )
        {
            if ( sharedPreferencesClass.checkSuccessIdExists(activeCampaigns[i].filename) ) {
                Log.d(Constants.TAG_NORMAL  , "File exists")
                if( activeCampaigns[i].address == "" )
                    activeCampaigns[i].address = Constants.DOWNLOAD_FOLDER_PATH + activeCampaigns[i].filename
                list.add(activeCampaigns[i])
            }
            else
                checkFileExists(activeCampaigns[i])
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

    private fun checkHoldCampaigns()
    {
        var i = 0
        val list = mutableListOf<VideoData>()

//        Log.d(Constants.TAG_NORMAL  , checkFileExists(holdCampaigns[i]).toString())
        while ( i < holdCampaigns.size )
        {
            if ( sharedPreferencesClass.checkSuccessIdExists(holdCampaigns[i].filename) )
            {
                Log.d(Constants.TAG_NORMAL  , "File exists")
                if( holdCampaigns[i].address == "" )
                    holdCampaigns[i].address = Constants.DOWNLOAD_FOLDER_PATH + holdCampaigns[i].filename
                list.add(holdCampaigns[i])
            }
            else
                checkFileExists(holdCampaigns[i])
            i++
        }

        if ( list.size == holdCampaigns.size ){
            if ( pausedCampaigns.size > 0 )
                checkPausedCampaigns()
            return
        }
    }

    private fun checkPausedCampaigns()
    {
        var i = 0
        val list = mutableListOf<VideoData>()

//        Log.d(Constants.TAG_NORMAL  , checkFileExists(pausedCampaigns[i]).toString())
        while ( i < pausedCampaigns.size )
        {
            if ( sharedPreferencesClass.checkSuccessIdExists(pausedCampaigns[i].filename) )
            {
                Log.d(Constants.TAG_NORMAL  , "File exists")
                if( pausedCampaigns[i].address == "" )
                    pausedCampaigns[i].address = Constants.DOWNLOAD_FOLDER_PATH + pausedCampaigns[i].filename
                list.add(pausedCampaigns[i])
            }
            else
                checkFileExists(pausedCampaigns[i])
            i++
        }

        if ( list.size == pausedCampaigns.size ){
            return
        }
    }

    private fun getYouTubeId(youTubeUrl: String): String?
    {
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