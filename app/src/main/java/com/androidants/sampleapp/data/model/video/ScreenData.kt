package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class ScreenData (

    @SerializedName("railwayData"   ) var railwayData   : RailwayData?   = RailwayData(),
    @SerializedName("erickshawData" ) var erickshawData : ErickshawData? = ErickshawData(),
    @SerializedName("qrScanData"    ) var qrScanData    : QrScanData?    = QrScanData(),
    @SerializedName("_id"           ) var Id            : String?        = null,
    @SerializedName("screen"        ) var screen        : String?        = null,
    @SerializedName("dataType"      ) var dataType      : String?        = null,
    @SerializedName("createdAt"     ) var createdAt     : String?        = null,
    @SerializedName("updatedAt"     ) var updatedAt     : String?        = null,
    @SerializedName("__v"           ) var _v            : Int?           = null

)