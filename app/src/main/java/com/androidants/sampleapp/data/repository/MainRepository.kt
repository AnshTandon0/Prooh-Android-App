package com.androidants.sampleapp.data.repository

import android.content.Context
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.video.GetVideoResponse

interface MainRepository {
    suspend fun getVideoLinks() : GetVideoResponse

    suspend fun downloadVideo( context : Context , videoData: VideoData ) : VideoData
}