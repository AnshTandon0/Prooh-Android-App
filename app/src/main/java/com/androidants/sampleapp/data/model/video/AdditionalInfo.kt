package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class AdditionalInfo (

  @SerializedName("budget"            ) var budget            : Int?              = null,
  @SerializedName("audianceReach"     ) var audianceReach     : Int?              = null,
  @SerializedName("crowdMobilityType" ) var crowdMobilityType : ArrayList<String> = arrayListOf(),
  @SerializedName("ageRange"          ) var ageRange          : ArrayList<String> = arrayListOf()

)