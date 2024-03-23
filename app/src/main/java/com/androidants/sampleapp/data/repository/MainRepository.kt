package com.androidants.sampleapp.data.repository

import android.content.Context
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.video.GetVideoResponse

interface MainRepository {
    suspend fun getVideoLinks(screenCode:String) : GetVideoResponse

    suspend fun downloadVideo( context : Context , videoData: VideoData ) : VideoData

    suspend fun getInternetConnectionStatus(context: Context) : Boolean
}