package com.androidants.sampleapp.data.model.file

import com.google.gson.annotations.SerializedName


data class GetFilesResponse(

  @SerializedName("Active") var activeCampaigns: ArrayList<FileData> = arrayListOf(),
  @SerializedName("screenId") var screenId: String? = null,
  @SerializedName("Hold") var holdCampaigns: ArrayList<FileData> = arrayListOf(),
  @SerializedName("Pause") var pauseCampaigns: ArrayList<FileData> = arrayListOf()

)