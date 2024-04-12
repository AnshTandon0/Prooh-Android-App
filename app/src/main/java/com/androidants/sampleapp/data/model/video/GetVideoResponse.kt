package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class GetVideoResponse (

    @SerializedName("myScreenVideos" ) var myScreenVideos : ArrayList<MyScreenVideos> = arrayListOf(),
    @SerializedName("holdCampaigns" ) var holdCampaigns : ArrayList<MyScreenVideos> = arrayListOf(),
    @SerializedName("pauseCampaigns" ) var pausedCampaigns : ArrayList<MyScreenVideos> = arrayListOf(),
    @SerializedName("screen"         ) var screen         : Screen?                   = Screen(),
    @SerializedName("screenData"     ) var screenData     : ScreenData?               = ScreenData()
)
