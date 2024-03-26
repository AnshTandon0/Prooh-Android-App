package com.androidants.sampleapp.data.model.log

import com.google.gson.annotations.SerializedName

data class LogInput(
    var a : MutableMap<String , String> = mutableMapOf()
)
