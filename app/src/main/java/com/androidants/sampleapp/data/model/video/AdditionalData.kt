package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class AdditionalData (

    @SerializedName("footfallClassification" ) var footfallClassification : FootfallClassification? = FootfallClassification(),
    @SerializedName("averageDailyFootfall"   ) var averageDailyFootfall   : Int?                    = null,
    @SerializedName("_id"                    ) var Id                     : String?                 = null

)