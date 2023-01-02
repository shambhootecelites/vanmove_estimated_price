package com.vanmove.passesger.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PorterBooking {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("user_id")
    @Expose
    var userId: String? = null

    @SerializedName("user_type")
    @Expose
    var userType: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("pickup_latitude")
    @Expose
    var pickupLatitude: String? = null

    @SerializedName("pickup_longitude")
    @Expose
    var pickupLongitude: String? = null

    @SerializedName("pickup")
    @Expose
    var pickup: String? = null

    @SerializedName("special_instructions")
    @Expose
    var specialInstructions: String? = null

    @SerializedName("request_trip_start_time")
    @Expose
    var requestTripStartTime: Any? = null

    @SerializedName("request_trip_end_time")
    @Expose
    var requestTripEndTime: Any? = null

    @SerializedName("porters_count")
    @Expose
    var portersCount: Int? = null

    @SerializedName("2hours_rate")
    @Expose
    private var _2hoursRate: String? = null

    @SerializedName("rate_per_minute_fare")
    @Expose
    var ratePerMinuteFare: String? = null

    @SerializedName("rate_porter_commision")
    @Expose
    var ratePorterCommision: String? = null

    @SerializedName("rate_time_total")
    @Expose
    var rateTimeTotal: Any? = null

    @SerializedName("rate_porter_commision_total")
    @Expose
    var ratePorterCommisionTotal: Any? = null

    @SerializedName("rate_grand_total")
    @Expose
    var rateGrandTotal: Any? = null

    @SerializedName("rate_per_porter")
    @Expose
    var ratePerPorter: String? = null

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null

    @SerializedName("rating_by_porter")
    @Expose
    var ratingByPorter: Int? = null

    @SerializedName("rating_by_user")
    @Expose
    var ratingByUser: Int? = null

    @SerializedName("feedback_by_porter")
    @Expose
    var feedbackByPorter: String? = null

    @SerializedName("feedback_by_user")
    @Expose
    var feedbackByUser: String? = null

    @SerializedName("is_status")
    @Expose
    var isStatus: Int? = null

    @SerializedName("is_payment")
    @Expose
    var isPayment: String? = null

    @SerializedName("is_active")
    @Expose
    var isActive: Int? = null

    @SerializedName("is_delete")
    @Expose
    var isDelete: Int? = null

    @SerializedName("is_notifiy")
    @Expose
    var isNotifiy: String? = null

    @SerializedName("is_type")
    @Expose
    var isType: String? = null

    @SerializedName("is_created_from")
    @Expose
    var isCreatedFrom: Any? = null

    @SerializedName("updated_timestamp")
    @Expose
    var updatedTimestamp: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    fun get_2hoursRate(): String? {
        return _2hoursRate
    }

    fun set_2hoursRate(_2hoursRate: String?) {
        this._2hoursRate = _2hoursRate
    }

}