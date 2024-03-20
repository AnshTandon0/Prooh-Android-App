package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class CampaignLogs (

  @SerializedName("deviceInfo" ) var deviceInfo : String? = null,
  @SerializedName("playTime"   ) var playTime   : String? = null,
  @SerializedName("_id"        ) var Id         : String? = null,
  @SerializedName("createdAt"  ) var createdAt  : String? = null,
  @SerializedName("updatedAt"  ) var updatedAt  : String? = null

)