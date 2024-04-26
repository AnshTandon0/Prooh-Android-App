package com.androidants.sampleapp.ui.usecases

import android.content.Context
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.repository.MainRepository
import javax.inject.Inject

class DeleteAdditionalFilesUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke(context : Context, activeCampaigns : ArrayList<VideoData> ,
                       holdCampaigns : ArrayList<VideoData> , pausedCampaigns : ArrayList<VideoData> ) {
        repository.deleteAdditionalFiles(context , activeCampaigns , holdCampaigns , pausedCampaigns)
    }
}