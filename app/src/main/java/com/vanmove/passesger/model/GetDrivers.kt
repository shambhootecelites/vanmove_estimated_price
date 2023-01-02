package com.vanmove.passesger.model

import com.google.android.gms.maps.model.Marker

/**
 * Created by farhan on 6/21/2017.
 */
class GetDrivers {
    var driver_id: String
    var vehicle_class_id: String
    var vehicle_class_name: String
    var latitude_driver: String
    var longitude_driver: String
    var distance_driver: String
    var marker: Marker? = null

    constructor(
        driver_id: String,
        vehicle_class_id: String,
        vehicle_class_name: String,
        latitude_driver: String,
        longitude_driver: String,
        distance_driver: String
    ) {
        this.driver_id = driver_id
        this.vehicle_class_id = vehicle_class_id
        this.vehicle_class_name = vehicle_class_name
        this.latitude_driver = latitude_driver
        this.longitude_driver = longitude_driver
        this.distance_driver = distance_driver
    }



}