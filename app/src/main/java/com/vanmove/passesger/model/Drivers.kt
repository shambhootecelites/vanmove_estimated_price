package com.vanmove.passesger.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class Drivers {
    var driver_id: String
    var vehicle_class_id: String
    var vehicle_class_name: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var distance: String
    var duration: String? = null
    var marker: Marker? = null
    var is_porter: String? = null
    var driver_updated_latlng: LatLng
    var isIs_checked = false
        private set
    private var is_driver_available = false

    constructor(
        driver_id: String,
        vehicle_class_id: String,
        distance: String,
        driver_updated_latlng: LatLng,
        marker: Marker?,
        is_driver_available: Boolean,
        is_porter: String
    ) {
        this.driver_id = driver_id
        this.vehicle_class_id = vehicle_class_id
        this.distance = distance
        this.marker = marker
        this.driver_updated_latlng = driver_updated_latlng
        this.is_driver_available = is_driver_available
    }

    constructor(
        driver_id: String,
        vehicle_class_id: String,
        distance: String,
        driver_updated_latlng: LatLng
    ) {
        this.driver_id = driver_id
        this.vehicle_class_id = vehicle_class_id
        this.distance = distance
        this.driver_updated_latlng = driver_updated_latlng
    }

    fun is_checked(): Boolean {
        return isIs_checked
    }

    fun setIs_checked(is_checked: Boolean) {
        isIs_checked = is_checked
    }

    fun Is_driver_available(): Boolean {
        return is_driver_available
    }

    fun setIs_driver_available(is_driver_available: Boolean) {
        this.is_driver_available = is_driver_available
    }
}