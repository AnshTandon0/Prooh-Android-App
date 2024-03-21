package com.androidants.sampleapp.data.model

import com.androidants.sampleapp.common.Constants

data class VideoData(
    var cid : String = "",
    var downloadId : Long = 0,
    var type : String = "",
    var duration: String = "5" ,
    var url : String = "",
    var address : String = "" ,
    var status : String = Constants.STATUS_DOWNLOADING,
    var filename : String = "" ,
    var index : Int = 0
)
