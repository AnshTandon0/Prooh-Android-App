package com.androidants.sampleapp.ui.splash

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.ui.usecases.GetInternetConnectionStatusUseCase
import com.androidants.sampleapp.ui.usecases.GetVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getVideosUseCase: GetVideosUseCase,
    private val getInternetConnectionStatusUseCase: GetInternetConnectionStatusUseCase
) : ViewModel() {
    private var _getVideoResponse = MutableLiveData<GetVideoResponse>()
    private var _getInternetConnectionStatus = MutableLiveData<Boolean>()
    val getVideoResponse : MutableLiveData<GetVideoResponse> by lazy {
        _getVideoResponse
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

    suspend fun internetConnectionStatus(context: Context) {
        _getInternetConnectionStatus.postValue(getInternetConnectionStatusUseCase.invoke(context))
    }
}