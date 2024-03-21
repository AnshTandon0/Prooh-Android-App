package com.androidants.sampleapp.data

import com.androidants.sampleapp.data.model.video.GetVideoResponse
import retrofit2.http.GET

interface ApiCalls {

    @GET("/api/screens/syncScreenCode/qXpD36")
    suspend fun getVideos() : GetVideoResponse

}