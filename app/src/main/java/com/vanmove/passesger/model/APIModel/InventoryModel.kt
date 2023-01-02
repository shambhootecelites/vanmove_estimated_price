package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.Inventories

class InventoryModel {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("inventories")
    @Expose
    var inventories: List<Inventories>? = null

}