package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class FootfallClassification (

  @SerializedName("employmentStatus"  ) var employmentStatus  : ArrayList<String> = arrayListOf(),
  @SerializedName("crowdMobilityType" ) var crowdMobilityType : ArrayList<String> = arrayListOf(),
  @SerializedName("maritalStatus"     ) var maritalStatus     : ArrayList<String> = arrayListOf(),
  @SerializedName("workType"          ) var workType          : ArrayList<String> = arrayListOf()

)