package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.UpcomingBookings

class UpcomingBookingsListModel {
    @SerializedName("status")
    @Expose
    var status: Status? = null

    @SerializedName("bookings")
    @Expose
    var bookings: List<UpcomingBookings>? = null

}