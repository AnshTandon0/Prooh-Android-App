package com.androidants.sampleapp.data.model.log

import com.google.gson.annotations.SerializedName

data class ScreenLogs(
    @SerializedName("screenId") var screenId: String = "",
    @SerializedName("screenDeviceId") var screenDeviceId: String = "",
    @SerializedName("screenIp") var screenIp: String = "",
    @SerializedName("screenMac") var screenMac: String = "",
    @SerializedName("screenDisplay") var screenDisplay: String = "" ,
    @SerializedName("mediaPlaybackDetails") var mediaPlaybackDetails: ArrayList<ScreenMediaDetails> = arrayListOf()
)

data class ScreenMediaDetails(
    @SerializedName("time") var time: String = "",
    @SerializedName("mediaId") var mediaId: String = "",
    @SerializedName("campaignId") var campaignId: String = "",
    @SerializedName("screenStatus") var screenStatus: String = ""
)
