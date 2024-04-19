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
import com.androidants.sampleapp.data.model.file.FileData
import com.androidants.sampleapp.data.model.file.GetFilesResponse
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api : ApiCalls
) : MainRepository {

    override suspend fun getVideoLinks(screenCode:String): Response<GetFilesResponse> {
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
            .setMimeType(videoData.fileType)
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

    override suspend fun postLogs(screenId: String , logReport: LogReport): Response<ArrayList<FileData>> {
        return api.postLogs(screenId , logReport)
    }

    override suspend fun deleteAdditionalFiles(
        context: Context,
        activeCampaigns: ArrayList<VideoData>,
        holdCampaigns: ArrayList<VideoData>,
        pausedCampaigns: ArrayList<VideoData>
    ) {
        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)
        val sharedPreferencesClass = SharedPreferencesClass(context)

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
            if (!exists || file.length() == 0L) {
                Log.d(Constants.TAG_NORMAL  , "Deleting file")
                Log.d(Constants.TAG_NORMAL  , file.name)
                sharedPreferencesClass.deleteSuccessId(file.name)
                sharedPreferencesClass.deleteDownloadingId(file.name)
                sharedPreferencesClass.deleteFailureId(file.name)
                file.delete()
            }
        }
    }

    override suspend fun checkFileExists(context: Context, videoData: VideoData): Pair<Boolean , VideoData> {
        Log.d(Constants.TAG_NORMAL  , "In Check File exists")
        if (videoData.fileType == Constants.TYPE_URL || videoData.fileType == Constants.TYPE_YOUTUBE)
            return Pair(true , videoData)

        val directoryPath = Constants.DOWNLOAD_FOLDER_PATH
        val directory = File(directoryPath)
        val sharedPreferencesClass = SharedPreferencesClass(context)

        val files = directory.listFiles()?.filter { it.isFile }
        Log.d(Constants.TAG_NORMAL  , files.toString())
        files?.forEach { file ->
            val fileSize = file.length()
            Log.d(Constants.TAG_NORMAL  , fileSize.toString())
            Log.d(Constants.TAG_NORMAL  , videoData.filesize.toString())
            if (file.name == videoData.filename && fileSize == videoData.filesize ) {
                sharedPreferencesClass.deleteDownloadingId(videoData.filename)
                sharedPreferencesClass.addSuccessId(videoData.filename)
                return Pair(true , videoData)
            }
        }
        return Pair(false , videoData)
    }

}