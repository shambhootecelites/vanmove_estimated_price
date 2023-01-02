package com.vanmove.passesger.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.APIModel.PorterHistoryModel
import com.vanmove.passesger.model.APIModel.PorterRequest
import com.vanmove.passesger.model.APIModel.Status

class PorterDetailHistory {
    @SerializedName("status")
    @Expose
    val status: Status? = null

    @SerializedName("request")
    @Expose
    val request: PorterRequest? = null

    @SerializedName("porters")
    @Expose
    val porters: PorterHistoryModel? = null
}