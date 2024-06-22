package com.androidants.sampleapp.data.model.file

import com.google.gson.annotations.SerializedName


data class FileData(

    @SerializedName("screenId") var screenId: String? = null,
    @SerializedName("campaignId") var campaignId: String? = null,
    @SerializedName("mediaId") var mediaId: String? = null,
    @SerializedName("name") var fileName: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("duration") var duration: String? = null,
    @SerializedName("fileType") var fileType: String? = null,
    @SerializedName("fileSize") var fileSize: Int? = null,
    @SerializedName("atIndex") var atIndex: ArrayList<Int> = arrayListOf()

)