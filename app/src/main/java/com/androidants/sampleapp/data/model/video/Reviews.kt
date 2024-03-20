package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class Reviews (

  @SerializedName("name"      ) var name      : String? = null,
  @SerializedName("comment"   ) var comment   : String? = null,
  @SerializedName("rating"    ) var rating    : Int?    = null,
  @SerializedName("_id"       ) var Id        : String? = null,
  @SerializedName("createdAt" ) var createdAt : String? = null,
  @SerializedName("updatedAt" ) var updatedAt : String? = null

)