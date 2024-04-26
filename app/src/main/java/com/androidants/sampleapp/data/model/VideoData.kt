package com.androidants.sampleapp.data.model

data class VideoData(

    var cid : String = "",
    var downloadId : Long = 0,
    var fileType : String = "",
    var duration: String = "5",
    var url : String = "",
    var awsUrl : String = "",
    var address : String = "",
    var filename : String = "",
    var index : Int = 0,
    var filesize : Long = 0
)
