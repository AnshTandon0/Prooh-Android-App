package com.androidants.sampleapp.ui.main

import android.app.DownloadManager
import android.graphics.BitmapFactory
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var point = 0
    private var allDownloaded = 0
    private var arrayList = mutableListOf<VideoData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInitialVideo()
        initViewModel()

        binding.videoView.setOnCompletionListener {
            checkStatus()
        }

    }

    private fun setupInitialVideo() {
        binding.videoView.setVideoPath(Constants.INITIAL_VIDEO_PATH)
        binding.videoView.start()
    }

    private fun checkFile (fileName : String) : Boolean {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)

        val files = directory.listFiles()?.filter { it.isFile }
        files?.forEach { file ->
            if (file.name == fileName)
                return false
        }
        return true
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        lifecycleScope.launch {
            viewModel.getVideos()
        }
        viewModel.getVideoResponse.observe (this) { it ->

            for ( data in it.myScreenVideos )
            {
                val newData = VideoData( cid = data.cid.toString() , type = data.fileType.toString() ,
                    duration = data.duration.toString() , url = data.video.toString() )

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
//                        newData.status = Constants.STATUS_DONE
                    }
                }

                if ( checkFile(newData.filename) && data.fileType != Constants.TYPE_URL)
                {
                    lifecycleScope.launch{
                        viewModel.downloadVideo(this@MainActivity , newData)
                    }
                }
                else{
                    newData.status = Constants.STATUS_DONE
                    newData.address = Constants.DOWNLOAD_FOLDER_PATH + newData.filename
                }

                for ( id in data.atIndex )
                {
                    newData.index = id
                    arrayList.add(newData)
                }

            }
            arrayList.sortBy { it.index }
            var i=0
            for ( data in arrayList )
            {
                data.index = i++
            }
        }
        viewModel.downloadManagerId.observe(this) {
            for ( data in arrayList )
                if ( data.cid == it.cid ) {
                    data.downloadId = it.downloadId
                }
            Log.d(Constants.TAG  , arrayList.toString())
        }
    }

    private fun checkStatus()
    {
        if ( point >= arrayList.size )
            point = 0
        while ( point < arrayList.size )
        {
            if ( arrayList[point].status == Constants.STATUS_DONE )
            {
                Log.d(Constants.TAG  , "Calling Set Data")
                setData()
                return
            }
            point++
        }
        binding.videoView.setVideoPath(Constants.INITIAL_VIDEO_PATH)
        binding.videoView.start()
        if(allDownloaded == 0)
            checkDownloadStatus()
    }

    private fun setData()
    {
        when( arrayList[point].type )
        {
            Constants.TYPE_VIDEO ->{
                binding.imageView.visibility = View.GONE
                binding.webView.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                Log.d(Constants.TAG , arrayList[point].address)
                binding.videoView.setVideoPath(arrayList[point].address)
                binding.videoView.start()
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
            }
        }
        point ++
        if(allDownloaded == 0)
            checkDownloadStatus()
    }

    private fun checkDownloadStatus()
    {
        Log.d(Constants.TAG  , "Check Status")
        var ct = 0
        for ( response in arrayList )
        {
            if ( response.address == "" )
            {
                ct ++
                val query = DownloadManager.Query().setFilterById(response.downloadId)
                val downloadManager = getSystemService(DownloadManager::class.java)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.d(Constants.TAG, "downloaded completed")
                            for ( data in arrayList )
                                if ( data.cid == response.cid )
                                {
                                    data.address = Constants.DOWNLOAD_FOLDER_PATH + data.filename
                                    data.status = Constants.STATUS_DONE
                                }
                        }
                        DownloadManager.STATUS_FAILED -> {
                            Log.d(Constants.TAG, "downloaded Failed")
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            Log.d(Constants.TAG, "downloaded Running")
                        }
                        DownloadManager.STATUS_PENDING -> {
                            Log.d(Constants.TAG, "download Pending")
                        }
                        else-> Log.d(Constants.TAG, "Other Issues")
                    }
                }
            }
        }
        if ( ct == 0 )
            allDownloaded = 1
    }
}