package com.androidants.sampleapp.data.model.log

import com.google.gson.annotations.SerializedName

data class LogReport(
    @SerializedName("deviceInfo") var deviceInfo: DeviceInfo ,
    @SerializedName("data") var data : ArrayList<MutableMap<String , String>> = arrayListOf()
)