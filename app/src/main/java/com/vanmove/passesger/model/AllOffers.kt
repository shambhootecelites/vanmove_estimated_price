package com.vanmove.passesger.model

import java.io.Serializable

/**
 * Created by Linez-001 on 6/22/2017.
 */
class AllOffers : Serializable {
    var request_id: String? = null
    var timse_stamp: String? = null
    var pick_up_name: String? = null
    var drop_off_name: String? = null
    var offered_advance: String? = null
    var offered_price: String? = null
    var is_offer_advance_paid: String? = null
    var payment_type: String? = null
    var offer_status: String? = null

    constructor() {}
    constructor(
        request_id: String?,
        timse_stamp: String?,
        pick_up_name: String?,
        drop_off_name: String?,
        offered_advance: String?,
        offered_price: String?,
        is_offer_advance_paid: String?,
        payment_type: String?,
        offer_status: String?
    ) {
        this.request_id = request_id
        this.timse_stamp = timse_stamp
        this.pick_up_name = pick_up_name
        this.drop_off_name = drop_off_name
        this.offered_advance = offered_advance
        this.offered_price = offered_price
        this.is_offer_advance_paid = is_offer_advance_paid
        this.payment_type = payment_type
        this.offer_status = offer_status
    }

}