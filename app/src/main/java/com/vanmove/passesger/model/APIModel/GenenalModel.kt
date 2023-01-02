package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GenenalModel {
    @SerializedName("status")
    @Expose
    var status: Status? = null


    @SerializedName("request")
    @Expose
    var request: String? = null

}