package com.vanmove.passesger.fragments.PromoCode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.APIModel.Status
import com.vanmove.passesger.model.PromoCode
import java.io.Serializable

class PromoCodeRespone {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("promo_codes")
    @Expose
    var promo_codes: List<PromoCode>? = null








}