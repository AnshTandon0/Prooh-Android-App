package com.androidants.sampleapp.data.repository

import android.content.Context
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.file.FileData
import com.androidants.sampleapp.data.model.file.GetFilesResponse
import com.androidants.sampleapp.data.model.log.CampaignLogs
import com.androidants.sampleapp.data.model.log.LogReportInput
import com.androidants.sampleapp.data.model.log.LogReportOutput
import retrofit2.Response

interface MainRepository {
    suspend fun getVideoLinks(screenCode:String) : Response<GetFilesResponse>

    suspend fun downloadVideo( context : Context , videoData: VideoData ) : VideoData

    suspend fun getInternetConnectionStatus(context: Context) : Boolean

    suspend fun postLogs (logReportInput: LogReportInput ) : Response<LogReportOutput>

    suspend fun deleteAdditionalFiles ( context: Context , activeCampaigns : ArrayList<VideoData> ,
                                        holdCampaigns : ArrayList<VideoData> , pausedCampaigns : ArrayList<VideoData> )

    suspend fun checkFileExists ( context: Context , videoData: VideoData ) : Pair<Boolean , VideoData>
}