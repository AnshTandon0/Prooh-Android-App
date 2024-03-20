package com.androidants.sampleapp.data.repository

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.data.ApiCalls
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val api : ApiCalls
) : MainRepository {

    override suspend fun getVideoLinks(): GetVideoResponse {
        return api.getVideos()
    }

    override suspend fun downloadVideo(context: Context , videoData: VideoData): VideoData {
        val downloadManager = context.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(videoData.url.trim().toUri())
            .setMimeType(videoData.type)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle(videoData.cid)
            .setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS , videoData.filename )
        videoData.downloadId = downloadManager.enqueue(request)
        return videoData
    }

}