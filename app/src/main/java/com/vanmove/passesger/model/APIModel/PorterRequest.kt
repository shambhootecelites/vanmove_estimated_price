package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PorterRequest (
    @SerializedName("latitude")
    @Expose
    val latitude: String,

    @SerializedName("longitude")
    @Expose
    val longitude: String,

    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("is_status")
    @Expose
    val is_status: String
)