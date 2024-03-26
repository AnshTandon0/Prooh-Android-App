package com.androidants.sampleapp.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.ui.usecases.DownloadVideoAndImageUseCase
import com.androidants.sampleapp.ui.usecases.GetInternetConnectionStatusUseCase
import com.androidants.sampleapp.ui.usecases.GetVideosUseCase
import com.androidants.sampleapp.ui.usecases.PostLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getVideosUseCase: GetVideosUseCase ,
    private val downloadVideoAndImageUseCase: DownloadVideoAndImageUseCase ,
    private val getInternetConnectionStatusUseCase: GetInternetConnectionStatusUseCase ,
    private val postLogsUseCase: PostLogsUseCase
) : ViewModel() {

    private var _getVideoResponse = MutableLiveData<GetVideoResponse>()
    private var _downloadManagerId = MutableLiveData<VideoData>()
    private var _getInternetConnectionStatus = MutableLiveData<Boolean>()
    val getVideoResponse : MutableLiveData<GetVideoResponse> by lazy {
        _getVideoResponse
    }
    val downloadManagerId :MutableLiveData<VideoData> by lazy {
        _downloadManagerId
    }
    val getInternetConnectionStatus : MutableLiveData<Boolean> by lazy {
        _getInternetConnectionStatus
    }

    suspend fun getVideos (screenCode:String) {
        val response = getVideosUseCase.invoke(screenCode)
        if ( response.code() == 200 )
            _getVideoResponse.postValue(response.body())
        else
            _getVideoResponse.postValue(null)
    }

    suspend fun downloadVideo(context : Context, videoData: VideoData ) {
        _downloadManagerId.postValue(downloadVideoAndImageUseCase.invoke(context , videoData))
    }

    suspend fun internetConnectionStatus(context: Context) {
        _getInternetConnectionStatus.postValue(getInternetConnectionStatusUseCase.invoke(context))
    }

    suspend fun postLogs ( screenId : String , logReport: LogReport )
    {
        postLogsUseCase.invoke(screenId , logReport)
    }

}