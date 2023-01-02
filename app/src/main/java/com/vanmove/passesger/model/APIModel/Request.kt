package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Request {

    @SerializedName("pickup")
    @Expose
    var pickup: String? = null



    @SerializedName("destination")
    @Expose
    var destination: String? = null

    @SerializedName("request_id")
    @Expose
    var requestId: String? = null



    @SerializedName("vehicle_class_id")
    @Expose
    var vehicleClassId: Int? = null

    @SerializedName("pickup_latitude")
    @Expose
    var pickupLatitude: String? = null

    @SerializedName("pickup_longitude")
    @Expose
    var pickupLongitude: String? = null

    @SerializedName("destination_latitude")
    @Expose
    var destinationLatitude: String? = null

    @SerializedName("destination_longitude")
    @Expose
    var destinationLongitude: String? = null

    @SerializedName("details")
    @Expose
    var details: String? = null

    @SerializedName("request_driver_accepted_time")
    @Expose
    var requestDriverAcceptedTime: String? = null

    @SerializedName("inventory_items")
    @Expose
    var inventoryItems: String? = null

    @SerializedName("special_instructions")
    @Expose
    var specialInstructions: String? = null

    @SerializedName("request_driver_arrive_time")
    @Expose
    var requestDriverArriveTime: String? = null

    @SerializedName("request_passenger_onboard_time")
    @Expose
    var requestPassengerOnboardTime: String? = null

    @SerializedName("request_trip_start_time")
    @Expose
    var requestTripStartTime: String? = null

    @SerializedName("request_drive_start_time")
    @Expose
    var requestDriveStartTime: String? = null

    @SerializedName("request_trip_end_time")
    @Expose
    var requestTripEndTime: String? = null

    @SerializedName("helpers_count")
    @Expose
    var helpersCount: Int? = null

    @SerializedName("hourly_rate")
    @Expose
    var hourlyRate: String? = null

    @SerializedName("rate_per_mile_fare")
    @Expose
    var ratePerMileFare: String? = null

    @SerializedName("rate_per_minute_fare")
    @Expose
    var ratePerMinuteFare: String? = null

    @SerializedName("rate_driver_commision")
    @Expose
    var rateDriverCommision: String? = null

    @SerializedName("rate_helper_hourly")
    @Expose
    var rateHelperHourly: String? = null

    @SerializedName("rate_helper_per_minute")
    @Expose
    var rateHelperPerMinute: String? = null

    @SerializedName("distance_mile_total")
    @Expose
    var distanceMileTotal: String? = null

    @SerializedName("distance_time_total")
    @Expose
    var distanceTimeTotal: String? = null

    @SerializedName("rate_distance_total")
    @Expose
    var rateDistanceTotal: String? = null

    @SerializedName("rate_time_total")
    @Expose
    var rateTimeTotal: String? = null

    @SerializedName("rate_helper_hourly_total")
    @Expose
    var rateHelperHourlyTotal: String? = null

    @SerializedName("rate_helper_per_minute_total")
    @Expose
    var rateHelperPerMinuteTotal: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    @SerializedName("other_Stops")
    @Expose
    var otherStops: String? = null

    @SerializedName("rate_grand_total")
    @Expose
    var rateGrandTotal: String? = null

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null

    @SerializedName("is_status")
    @Expose
    var isStatus: String? = null

    @SerializedName("is_active")
    @Expose
    var isActive: Int? = null

    @SerializedName("is_payment")
    @Expose
    var isPayment: String? = null

    @SerializedName("is_insurance")
    @Expose
    var isInsurance: Int? = null

    @SerializedName("is_type")
    @Expose
    var isType: String? = null

    @SerializedName("offered_price")
    @Expose
    var offeredPrice = 0.0

    @SerializedName("offered_advance")
    @Expose
    var offeredAdvance = 0.0



    @SerializedName("is_flexible")
    @Expose
    var isFlexible: String? = null

    @SerializedName("service_name")
    @Expose
    var serviceName: String? = null

    @SerializedName("vehicle_class_name")
    @Expose
    var vehicleClassName: String? = null

    @SerializedName("estimated_payment")
    @Expose
    var estimated_payment: String? = null

    @SerializedName("estimated_duartion")
    @Expose
    var estimated_duartion: String? = null


    @SerializedName("is_future")
    @Expose
    var is_future = 0

    @SerializedName("is_offer_advance_paid")
    @Expose
    var is_offer_advance_paid = 0

}