package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PorterHistory {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("current_porter_jobs")
    @Expose
    var current_porter_jobs: List<PorterHistoryModel>? = null

}