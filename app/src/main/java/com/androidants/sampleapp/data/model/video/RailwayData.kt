package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class RailwayData (

  @SerializedName("stationCode" ) var stationCode : String?           = null,
  @SerializedName("stationName" ) var stationName : String?           = null,
  @SerializedName("trains"      ) var trains      : ArrayList<String> = arrayListOf()

)