package com.vanmove.passesger.model

import java.io.Serializable

class SavedPlacesData : Serializable {
    var id: String? = null
    var user_id: String? = null
    var destination: String? = null
    var latitude: String? = null
    var longitude: String? = null


    constructor() {}
    constructor(
        id: String?,
        user_id: String?,
        destination: String?,
        latitude: String?,
        longitude: String?
    ) {
        this.id = id
        this.user_id = user_id
        this.destination = destination
        this.latitude = latitude
        this.longitude = longitude
    }

}