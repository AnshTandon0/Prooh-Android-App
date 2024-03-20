package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class ErickshawData (

  @SerializedName("adIntervals"          ) var adIntervals          : ArrayList<String> = arrayListOf(),
  @SerializedName("defaultContents"      ) var defaultContents      : ArrayList<String> = arrayListOf(),
  @SerializedName("defaultContentNumber" ) var defaultContentNumber : Int?              = null

)