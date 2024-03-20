package com.androidants.sampleapp.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.ui.usecases.DownloadVideoAndImageUseCase
import com.androidants.sampleapp.ui.usecases.GetVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getVideosUseCase: GetVideosUseCase ,
    private val downloadVideoAndImageUseCase: DownloadVideoAndImageUseCase
) : ViewModel() {

    private var _getVideoResponse = MutableLiveData<GetVideoResponse>()
    private var _downloadManagerId = MutableLiveData<VideoData>()
    val getVideoResponse : MutableLiveData<GetVideoResponse> by lazy {
        _getVideoResponse
    }
    val downloadManagerId :MutableLiveData<VideoData> by lazy {
        _downloadManagerId
    }

    suspend fun getVideos () {
        _getVideoResponse.postValue(getVideosUseCase.invoke())
    }

    suspend fun downloadVideo(context : Context, videoData: VideoData ) {
        _downloadManagerId.postValue(downloadVideoAndImageUseCase.invoke(context , videoData))
    }

}