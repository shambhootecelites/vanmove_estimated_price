package com.vanmove.passesger.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UpcomingBookings : Serializable {
    @SerializedName("is_future")
    @Expose
    var is_future: String? = null

    @SerializedName("request_id")
    @Expose
    var requestId: String? = null

    @SerializedName("is_status")
    @Expose
    var isStatus: Int? = null

    @SerializedName("is_insurance")
    @Expose
    var isInsurance: Int? = null

    @SerializedName("pickup")
    @Expose
    var pickup: String? = null

    @SerializedName("destination")
    @Expose
    var destination: String? = null

    @SerializedName("estimated_duartion")
    @Expose
    var estimated_duartion: String? = null

    @SerializedName("estimated_payment")
    @Expose
    var estimated_payment: String? = null

    @SerializedName("request_drive_start_time")
    @Expose
    var requestDriveStartTime: String? = null

    @SerializedName("rate_grand_total")
    @Expose
    var rateGrandTotal: String? = null

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null

    @SerializedName("hourly_rate")
    @Expose
    var hourlyRate: String? = null

    @SerializedName("rate_per_minute_fare")
    @Expose
    var ratePerMinuteFare: String? = null

    @SerializedName("rate_per_mile_fare")
    @Expose
    var ratePerMileFare: String? = null

    @SerializedName("rate_helper_per_minute")
    @Expose
    var rateHelperPerMinute: String? = null

    @SerializedName("rate_helper_hourly")
    @Expose
    var rateHelperHourly: String? = null

    @SerializedName("helpers_count")
    @Expose
    var helpersCount: Int? = null

    @SerializedName("other_Stops")
    @Expose
    var otherStops: String? = null

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

    @SerializedName("pickup_floor")
    @Expose
    var pickupFloor: String? = null

    @SerializedName("dropoff_floor")
    @Expose
    var dropoffFloor: String? = null

    @SerializedName("pickup_lift")
    @Expose
    var pickupLift: String? = null

    @SerializedName("upto_first_hour_rate")
    @Expose
    var uptoFirstHourRate: String? = null

    @SerializedName("dropoff_lift")
    @Expose
    var dropoffLift: String? = null

    @SerializedName("pickup_property")
    @Expose
    var pickupProperty: String? = null

    @SerializedName("dropoff_property")
    @Expose
    var dropoffProperty: String? = null

    @SerializedName("vehicle_class_name")
    @Expose
    var vehicleClassName: String? = null

    @SerializedName("inventory_items")
    @Expose
    var inventoryItems: String? = null

    @SerializedName("special_instructions")
    @Expose
    var specialInstructions: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("picture")
    @Expose
    var picture: String? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("country_code")
    @Expose
    var countryCode: String? = null

    @SerializedName("rating")
    @Expose
    var rating: String? = null

    @SerializedName("move")
    @Expose
    var move: String? = null

    @SerializedName("offered_price")
    @Expose
    var offered_price:Double = 0.0


    @SerializedName("inventory_details")
    @Expose
    var inventory_details: String? = null

    @SerializedName("offered_advance")
    @Expose
    var offered_advance = 0.0

    @SerializedName("is_offer_advance_paid")
    @Expose
    var is_offer_advance_paid = 0

    @SerializedName("pickup_door")
    @Expose
    var pickup_door: String? = null

    @SerializedName("dropoff_door")
    @Expose
    var dropoff_door: String? = null

    @SerializedName("is_flexible")
    @Expose
    var is_flexible: String? = null

}