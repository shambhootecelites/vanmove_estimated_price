package com.vanmove.passesger.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

/**
 * Created by Linez-001 on 6/22/2017.
 */
class Booking : Serializable {
    var request_id: String? = null
    var pick_up_name: String? = null
    var drop_off_name: String? = null
    var pick_up_latitude: String? = null
    var pick_up_longitude: String? = null
    var drop_off_latitude: String? = null
    var drop_off_longitude: String? = null
    var booking_detail: String? = null
    var inventory_items: String? = null
    var special_instruction: String? = null
    var trip_start_time: String? = null
    var drive_start_time: String? = null
    var trip_end_time: String? = null
    var helper_count: String? = null
    var payment_type: String? = null
    var vehicle_class_id: String? = null
    var vehicle_class_name: String? = null
    var passenger_first_name: String? = null
    var passenger_last_name: String? = null
    var passenger_email: String? = null
    var passenger_mobile_code: String? = null
    var passenger_mobile_number: String? = null
    var passenger_picture: String? = null
    var passenger_rating: String? = null
    var driver_rating: String? = null
    var trip_date_time: String? = null
    var pick_up_latlng: LatLng? = null
    var drop_off_latlng: LatLng? = null
    var rate_grand_total: String? = null
    var offered_advance = 0.0
    var is_future = 0

    constructor() {}
    constructor(
        request_id: String?,
        pick_up_name: String?,
        drop_off_name: String?,
        pick_up_latitude: String?,
        pick_up_longitude: String?,
        drop_off_latitude: String?,
        drop_off_longitude: String?,
        booking_detail: String?,
        inventory_items: String?,
        special_instruction: String?,
        trip_start_time: String?,
        drive_start_time: String?,
        trip_end_time: String?,
        helper_count: String?,
        payment_type: String?,
        vehicle_class_id: String?,
        vehicle_class_name: String?,
        passenger_first_name: String?,
        passenger_last_name: String?,
        passenger_email: String?,
        passenger_mobile_code: String?,
        passenger_mobile_number: String?,
        passenger_picture: String?,
        passenger_rating: String?,
        driver_rating: String?
    ) {
        this.request_id = request_id
        this.pick_up_name = pick_up_name
        this.drop_off_name = drop_off_name
        this.pick_up_latitude = pick_up_latitude
        this.pick_up_longitude = pick_up_longitude
        this.drop_off_latitude = drop_off_latitude
        this.drop_off_longitude = drop_off_longitude
        this.booking_detail = booking_detail
        this.inventory_items = inventory_items
        this.special_instruction = special_instruction
        this.trip_start_time = trip_start_time
        this.drive_start_time = drive_start_time
        this.trip_end_time = trip_end_time
        this.helper_count = helper_count
        this.payment_type = payment_type
        this.vehicle_class_id = vehicle_class_id
        this.vehicle_class_name = vehicle_class_name
        this.passenger_first_name = passenger_first_name
        this.passenger_last_name = passenger_last_name
        this.passenger_email = passenger_email
        this.passenger_mobile_code = passenger_mobile_code
        this.passenger_mobile_number = passenger_mobile_number
        this.passenger_picture = passenger_picture
        this.passenger_rating = passenger_rating
        this.driver_rating = driver_rating
    }

}