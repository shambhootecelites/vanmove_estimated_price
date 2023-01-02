package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.DeminsionModel

class DimensionsModel {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("dimensions")
    @Expose
    var dimensions: List<DeminsionModel>? = null

}