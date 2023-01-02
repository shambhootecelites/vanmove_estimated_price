package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BookingDetailRespone {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("request")
    @Expose
    var request: Request? = null

    @SerializedName("driver")
    @Expose
    var driver: Driver? = null

    @SerializedName("rating")
    @Expose
    var rating: Rating? = null

    class Rating {

        @SerializedName("rating_passenger")
        @Expose
        var rating_passenger: String? = null

        @SerializedName("rating_driver")
        @Expose
        var rating_driver: String? = null

        @SerializedName("moves")
        @Expose
        var moves: String? = null
    }

    class Driver {

        @SerializedName("driver_id")
        @Expose
        var driver_id: String? = null

        @SerializedName("driver_first_name")
        @Expose
        var driver_first_name: String? = null


        @SerializedName("driver_last_name")
        @Expose
        var driver_last_name: String? = null


        @SerializedName("driver_latitude")
        @Expose
        var driver_latitude: String? = null

        @SerializedName("driver_longitude")
        @Expose
        var driver_longitude: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("mobile")
        @Expose
        var mobile: String? = null

        @SerializedName("country_code")
        @Expose
        var country_code: String? = null

        @SerializedName("picture")
        @Expose
        var picture: String? = null


    }

}