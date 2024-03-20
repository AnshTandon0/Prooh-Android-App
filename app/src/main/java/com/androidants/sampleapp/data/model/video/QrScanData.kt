package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class QrScanData (

  @SerializedName("scanDetails" ) var scanDetails : ArrayList<String> = arrayListOf()

)