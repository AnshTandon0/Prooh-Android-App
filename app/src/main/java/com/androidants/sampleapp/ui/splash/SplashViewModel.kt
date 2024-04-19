package com.androidants.sampleapp.ui.splash

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidants.sampleapp.data.model.file.GetFilesResponse
import com.androidants.sampleapp.ui.usecases.GetFilesDataUseCase
import com.androidants.sampleapp.ui.usecases.GetInternetConnectionStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getFilesDataUseCase: GetFilesDataUseCase,
    private val getInternetConnectionStatusUseCase: GetInternetConnectionStatusUseCase
) : ViewModel() {
    private var _getVideoResponse = MutableLiveData<GetFilesResponse>()
    private var _getInternetConnectionStatus = MutableLiveData<Boolean>()
    val getVideoResponse : MutableLiveData<GetFilesResponse> by lazy {
        _getVideoResponse
    }
    val getInternetConnectionStatus : MutableLiveData<Boolean> by lazy {
        _getInternetConnectionStatus
    }

    suspend fun getVideos (screenCode:String) {
        val response = getFilesDataUseCase.invoke(screenCode)
        if ( response.code() == 200 )
            _getVideoResponse.postValue(response.body())
        else
            _getVideoResponse.postValue(null)
    }

    suspend fun internetConnectionStatus(context: Context) {
        _getInternetConnectionStatus.postValue(getInternetConnectionStatusUseCase.invoke(context))
    }
}