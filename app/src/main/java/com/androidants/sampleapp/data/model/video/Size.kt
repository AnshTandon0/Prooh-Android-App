package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class Size (

  @SerializedName("length"          ) var length          : Int?    = null,
  @SerializedName("width"           ) var width           : Int?    = null,
  @SerializedName("measurementUnit" ) var measurementUnit : String? = null

)