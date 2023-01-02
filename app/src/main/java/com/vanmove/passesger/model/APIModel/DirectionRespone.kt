package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DirectionRespone {
    @SerializedName("status")
    @Expose
    var status: Status? = null

}