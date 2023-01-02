package com.vanmove.passesger.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * Created by farhan on 6/21/2017.
 */
class GetDriversMarkers(
    var driver_id: String,
    var vehicle_class_id: String,
    var vehicle_class_name: String,
    var latitude_driver: String,
    var longitude_driver: String,
    var distance_driver: String,
    var driverLatLng: LatLng,
    private var is_checked: Boolean,
    var marker: Marker
) {

    fun is_checked(): Boolean {
        return is_checked
    }

    fun setIs_checked(is_checked: Boolean) {
        this.is_checked = is_checked
    }

}