package com.androidants.sampleapp.data.model.log

import com.google.gson.annotations.SerializedName

data class DeviceInfo(
    @SerializedName("deviceIp") var deviceIp : String = "" ,
    @SerializedName("deviceMaac") var deviceMac : String = "",
    @SerializedName("deviceDisplay") var deviceDisplay : String = "",
    @SerializedName("deviceId") var deviceId : String = "",
    @SerializedName("deviceStatus") var deviceStatus : String = "online"
)
