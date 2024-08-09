package com.androidants.sampleapp.data.model.log

import com.google.gson.annotations.SerializedName

data class CampaignLogs(
    @SerializedName("campaignId") var campaignId: String = "",
    @SerializedName("mediaPlaybackDetails") var mediaPlaybackDetails: CampaignMediaDetails
)

data class CampaignMediaDetails(
    @SerializedName("time") var time: String = "",
    @SerializedName("mediaId") var mediaId: String = "",
    @SerializedName("screenId") var screenId: String = "",
    @SerializedName("screenStatus") var screenStatus: String = ""
)
