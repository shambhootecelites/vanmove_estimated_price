package com.vanmove.passesger.activities.ApplyPromoCode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.APIModel.Status
import com.vanmove.passesger.model.PromoCode

class PromoCodeValidRespone {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("promo_code")
    @Expose
    var promo_code: PromoCode? = null




}