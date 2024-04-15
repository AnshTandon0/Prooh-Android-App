package com.androidants.sampleapp.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.common.SharedPreferencesClass
import com.androidants.sampleapp.data.ApiCalls
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.data.model.video.MyScreenVideos
import retrofit2.Response
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api : ApiCalls
) : MainRepository {

    override suspend fun getVideoLinks(screenCode:String): Response<GetVideoResponse> {
        return api.getVideos(screenCode)
    }

    override suspend fun downloadVideo(context: Context , videoData: VideoData): VideoData {
        val downloadManager = context.getSystemService(DownloadManager::class.java)
        var urlDownload = videoData.url.trim().toUri()
        val sharedPreferencesClass = SharedPreferencesClass(context)

        if ( sharedPreferencesClass.checkFailureIdExists(videoData.filename) && videoData.awsUrl != "" )
        {
            urlDownload = videoData.awsUrl.trim().toUri()
        }

        val request = DownloadManager.Request(urlDownload)
            .setMimeType(videoData.type)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(videoData.filename)
            .setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS , videoData.filename )
        videoData.downloadId = downloadManager.enqueue(request)
        return videoData
    }

    override suspend fun getInternetConnectionStatus(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.d("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.d("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.d("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    override suspend fun postLogs(screenId: String , logReport: LogReport): Response<ArrayList<MyScreenVideos>> {
        return api.postLogs(screenId , logReport)
    }

}