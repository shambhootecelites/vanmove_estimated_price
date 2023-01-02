package com.vanmove.passesger.fragments.MoveNavigationScreen

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ConnectedFragment : Fragment(R.layout.fragment_connected) {
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    private var reqID: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reqID = getPreferences(CONSTANTS.REQUEST_ID, context!!)
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        getBookingDetails(passenger_id, RegistrationID, reqID)

    }


    private fun getBookingDetails(
        passenger_id: String?,
        RegistrationID_: String?,
        request_id: String?
    ) {
        val url = Utils.get_booking_detail
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = request_id
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val jsonRequest = jsonObject.getJSONObject("request")
                            val pickup_latitude =
                                jsonRequest.getString("pickup_latitude")
                            val pickup_longitude =
                                jsonRequest.getString("pickup_longitude")
                            val destination_latitude =
                                jsonRequest.getString("destination_latitude")
                            val destination_longitude =
                                jsonRequest.getString("destination_longitude")
                            val vehicle_class_name =
                                jsonRequest.getString("vehicle_class_name")
                            savePreferences(
                                CONSTANTS.pickup_latitude,
                                pickup_latitude,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.pickup_longitude,
                                pickup_longitude,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.destination_latitude,
                                destination_latitude,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.destination_longitude,
                                destination_longitude,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.vehicle_class_name,
                                vehicle_class_name,
                                context!!
                            )
                            val jsonPassenger = jsonObject.getJSONObject("driver")
                            val driver_id = jsonPassenger.getString("driver_id")
                            val driver_first_name =
                                jsonPassenger.getString("driver_first_name")
                            val driver_last_name =
                                jsonPassenger.getString("driver_last_name")
                            val driver_latitude =
                                jsonPassenger.getString("driver_latitude")
                            val driver_longitude =
                                jsonPassenger.getString("driver_longitude")
                            val email = jsonPassenger.getString("email")
                            val mobile = jsonPassenger.getString("mobile")
                            val country_code =
                                jsonPassenger.getString("country_code")
                            val picture = jsonPassenger.getString("picture")
                            savePreferences(
                                CONSTANTS.driver_id,
                                driver_id,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.driver_first_name,
                                "$driver_first_name $driver_last_name",
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.driver_latitude,
                                driver_latitude,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.driver_longitude,
                                driver_longitude,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.email,
                                email,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.mobile,
                                country_code + mobile,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.picture,
                                picture,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.polyline,
                                "true",
                                context!!
                            )
                            try {
                                val newFragment = FragmentVanOnItsWay()
                                val transaction =
                                    fragmentManager!!.beginTransaction()
                                transaction.replace(R.id.container_fragments, newFragment)
                                transaction.commitAllowingStateLoss()
                            } catch (error: Exception) {
                                error.printStackTrace()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error -> VolleyLog.d("TAG", "Error: " + error.message) }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["passenger_id"] = passenger_id!!
                    headers["registration_id"] = RegistrationID_!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }


}