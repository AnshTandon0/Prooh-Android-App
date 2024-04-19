package com.androidants.sampleapp.data.model.file

import com.google.gson.annotations.SerializedName


data class FileData (

  @SerializedName("_id"      ) var Id       : String?        = null,
  @SerializedName("name"     ) var fileName : String?        = null,
  @SerializedName("cid"      ) var cid      : String?        = null,
  @SerializedName("video"    ) var url      : String?        = null,
  @SerializedName("awsURL"   ) var awsUrl   : String?        = null,
  @SerializedName("duration" ) var duration : String?        = null,
  @SerializedName("fileType" ) var fileType : String?        = null,
  @SerializedName("fileSize" ) var fileSize : Int?           = null,
  @SerializedName("atIndex"  ) var atIndex  : ArrayList<Int> = arrayListOf()

)