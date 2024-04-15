package com.androidants.sampleapp.data.model.video

import com.google.gson.annotations.SerializedName


data class MyScreenVideos (

    @SerializedName("additionalInfo"    ) var additionalInfo    : AdditionalInfo?   = AdditionalInfo(),
    @SerializedName("_id"               ) var Id                : String?           = null,
    @SerializedName("media"             ) var media             : String?           = null,
    @SerializedName("awsURL"            ) var awsUrl            : String?           = null ,
    @SerializedName("video"             ) var video             : String?           = null,
    @SerializedName("cid"               ) var cid               : String?           = null,
    @SerializedName("thumbnail"         ) var thumbnail         : String?           = null,
    @SerializedName("screen"            ) var screen            : String?           = null,
    @SerializedName("campaignName"      ) var campaignName      : String?           = null,
    @SerializedName("screenName"        ) var screenName        : String?           = null,
    @SerializedName("brandName"         ) var brandName         : String?           = null,
    @SerializedName("uploadedBy"        ) var uploadedBy        : String?           = null,
    @SerializedName("startDate"         ) var startDate         : String?           = null,
    @SerializedName("endDate"           ) var endDate           : String?           = null,
    @SerializedName("master"            ) var master            : String?           = null,
    @SerializedName("ally"              ) var ally              : String?           = null,
    @SerializedName("atIndex"           ) var atIndex           : ArrayList<Int>    = arrayListOf(),
    @SerializedName("fileType"          ) var fileType          : String?           = null,
    @SerializedName("duration"          ) var duration          : String?           = null,
    @SerializedName("fileSize"          ) var fileSize          : Int?              = null,
    @SerializedName("revenue"           ) var revenue           : Int?              = null,
    @SerializedName("vault"             ) var vault             : Int?              = null,
    @SerializedName("totalSlotBooked"   ) var totalSlotBooked   : Int?              = null,
    @SerializedName("remainingSlots"    ) var remainingSlots    : Int?              = null,
    @SerializedName("totalSlotPlayed"   ) var totalSlotPlayed   : Int?              = null,
    @SerializedName("slotPlayedPerDay"  ) var slotPlayedPerDay  : Int?              = null,
    @SerializedName("rentPerSlot"       ) var rentPerSlot       : Double?              = null,
    @SerializedName("rentPerDay"        ) var rentPerDay        : Int?              = null,
    @SerializedName("slotsPlayPerDay"   ) var slotsPlayPerDay   : Int?              = null,
    @SerializedName("totalAmount"       ) var totalAmount       : Int?              = null,
    @SerializedName("isSlotBooked"      ) var isSlotBooked      : Boolean?          = null,
    @SerializedName("isDefaultCampaign" ) var isDefaultCampaign : Boolean?          = null,
    @SerializedName("paidForSlot"       ) var paidForSlot       : Boolean?          = null,
    @SerializedName("status"            ) var status            : String?           = null,
    @SerializedName("campaignLogs"      ) var campaignLogs      : ArrayList<String> = arrayListOf(),
    @SerializedName("createdAt"         ) var createdAt         : String?           = null,
    @SerializedName("updatedAt"         ) var updatedAt         : String?           = null,
    @SerializedName("__v"               ) var _v                : Int?              = null

)