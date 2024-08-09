package com.androidants.sampleapp.data.model.file

import com.google.gson.annotations.SerializedName


data class Screen (

  @SerializedName("_id"        ) var Id         : String?           = null,
  @SerializedName("name"       ) var name       : String?           = null,
  @SerializedName("screenCode" ) var screenCode : String?           = null,
  @SerializedName("campaigns"  ) var campaigns  : ArrayList<Campaign> = arrayListOf()

)

data class Campaign (
  @SerializedName("campaignId"        ) var Id         : String?           = null,
  @SerializedName("campaignType"      ) var name       : ArrayList<String> = arrayListOf(),
)