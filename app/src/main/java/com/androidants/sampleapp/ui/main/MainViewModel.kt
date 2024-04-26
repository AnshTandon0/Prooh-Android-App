package com.androidants.sampleapp.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.model.file.GetFilesResponse
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.ui.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getFilesDataUseCase: GetFilesDataUseCase,
    private val downloadFilesUseCase: DownloadFilesUseCase,
    private val getInternetConnectionStatusUseCase: GetInternetConnectionStatusUseCase,
    private val postLogsUseCase: PostLogsUseCase ,
    private val deleteAdditionalFilesUseCase: DeleteAdditionalFilesUseCase ,
    private val checkFileExistsUseCase: CheckFileExistsUseCase
) : ViewModel() {

    private var _getVideoResponse = MutableLiveData<GetFilesResponse>()
    private var _downloadManagerId = MutableLiveData<VideoData>()
    private var _getInternetConnectionStatus = MutableLiveData<Boolean>()
    private var _checkFileExits = MutableLiveData<Pair<Boolean , VideoData>>()
    val getVideoResponse : MutableLiveData<GetFilesResponse> by lazy {
        _getVideoResponse
    }
    val downloadManagerId :MutableLiveData<VideoData> by lazy {
        _downloadManagerId
    }
    val getInternetConnectionStatus : MutableLiveData<Boolean> by lazy {
        _getInternetConnectionStatus
    }
    val checkFileExists : MutableLiveData<Pair<Boolean , VideoData>> by lazy {
        _checkFileExits
    }

    suspend fun getVideos (screenCode:String) {
        val response = getFilesDataUseCase.invoke(screenCode)
        if ( response.code() == 200 )
            _getVideoResponse.postValue(response.body())
        else
            _getVideoResponse.postValue(null)
    }

    suspend fun downloadVideo(context : Context, videoData: VideoData ) {
        _downloadManagerId.postValue(downloadFilesUseCase.invoke(context , videoData))
    }

    suspend fun internetConnectionStatus(context: Context) {
        _getInternetConnectionStatus.postValue(getInternetConnectionStatusUseCase.invoke(context))
    }

    suspend fun postLogs ( screenId : String , logReport: LogReport )
    {
        postLogsUseCase.invoke(screenId , logReport)
    }

    suspend fun deleteAdditionalFiles( context : Context, activeCampaigns : ArrayList<VideoData> ,
                                       holdCampaigns : ArrayList<VideoData> , pausedCampaigns : ArrayList<VideoData> )
    {
        deleteAdditionalFilesUseCase.invoke(context , activeCampaigns, holdCampaigns, pausedCampaigns)
    }

    suspend fun checkFileExists ( context: Context , videoData: VideoData )
    {
        _checkFileExits.postValue(checkFileExistsUseCase.invoke(context, videoData))
    }

}