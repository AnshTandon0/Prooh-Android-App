package com.androidants.sampleapp.data.repository

import android.content.Context
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.data.model.video.MyScreenVideos
import retrofit2.Response

interface MainRepository {
    suspend fun getVideoLinks(screenCode:String) : Response<GetVideoResponse>

    suspend fun downloadVideo( context : Context , videoData: VideoData ) : VideoData

    suspend fun getInternetConnectionStatus(context: Context) : Boolean

    suspend fun postLogs ( screenId : String , logReport: LogReport ) : Response<ArrayList<MyScreenVideos>>
}