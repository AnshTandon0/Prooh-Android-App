package com.androidants.sampleapp.data.model.file

import com.google.gson.annotations.SerializedName


data class GetFilesResponse (

  @SerializedName("myScreenVideos" ) var activeCampaigns : ArrayList<FileData> = arrayListOf(),
  @SerializedName("screen"         ) var screen         : Screen?                   = Screen(),
  @SerializedName("holdCampaigns"  ) var holdCampaigns  : ArrayList<FileData>         = arrayListOf(),
  @SerializedName("pauseCampaigns" ) var pauseCampaigns : ArrayList<FileData> = arrayListOf()

)