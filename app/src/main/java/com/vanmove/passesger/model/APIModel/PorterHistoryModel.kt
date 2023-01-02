package com.vanmove.passesger.model.APIModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PorterHistoryModel : Serializable {
    @SerializedName("request_id")
    @Expose
    var requestId: String? = null

    @SerializedName("porter_id")
    @Expose
    var porterId: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var last_name: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("country_code")
    @Expose
    var countryCode: String? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("picture")
    @Expose
    var picture: String? = null

    @SerializedName("is_status")
    @Expose
    var isStatus: Int? = null

    @SerializedName("porter_lat")
    @Expose
    var porter_lat: String? = null

    @SerializedName("porter_lng")
    @Expose
    var porter_lng: String? = null

    @SerializedName("job_lat")
    @Expose
    var job_lat: String? = null

    @SerializedName("job_lng")
    @Expose
    var job_lng: String? = null

}