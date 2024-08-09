package com.androidants.sampleapp.data.model.log

import com.google.gson.annotations.SerializedName

data class LogReportInput(
    @SerializedName("screenLogs") val screenLogs: ScreenLogs ,
    @SerializedName("campaignLogs") var campaignLogs: ArrayList<CampaignLogs> = arrayListOf()
)

data class LogReportOutput(
    @SerializedName("screen") val screen: String ,
    @SerializedName("campaign") val campaign: String
)