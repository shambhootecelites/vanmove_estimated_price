package com.vanmove.passesger.model

class GetVehicles(
    var vehicle_id: String, var vehicle_name: String,
    var type: String, var weight: String,
    var capacity: String, var info: String, var internal_dimension: String,
    var external_dimension: String,
    var picture: String, var hourly_rate: String, var rate_per_mile_fare: String,
    var rate_per_minute_fare: String, var rate_helper_hourly: String,
    var rate_helper_per_minute: String, var rate_driver_commision: String,
    var driver_id: String?, var vehicle_class_id: String?,
    var vehicle_class_name: String?,
    var latitude_driver: String?, var longitude_driver: String?,
    var distance_driver: String?,
    var driver_time: Int?
)