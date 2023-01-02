package com.vanmove.passesger.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.ContactFragment
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.fragment_trip_hant.*
import kotlinx.android.synthetic.main.fragment_trip_hant.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TripSummaryFragment : Fragment(R.layout.fragment_trip_hant), OnMapReadyCallback {

    var payment_type = ""
    var charge_id = ""
    var rate_grand_total = ""
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    private var reqID: String? = null
    private var myMap: GoogleMap? = null
    private var driver_routes_list: ArrayList<LatLng>? = null
    private var marker_pick_up: Marker? = null
    private var marker_drop_off: Marker? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reqID = getPreferences(CONSTANTS.REQUEST_ID, context!!)
        RegistrationID = getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id = getPreferences(CONSTANTS.passenger_id, context!!)
        linkViews()
        showMap(savedInstanceState)
        bookingDetails
    }


    private fun linkViews() {
        driver_routes_list = ArrayList()

        view!!.Next!!.setOnClickListener{


            val afterTripDetail = PaymentSummary()
            val bundle = Bundle()
            bundle.putString("payment_type", payment_type)
            bundle.putString("charge_id", charge_id)
            bundle.putString("rate_grand_total", rate_grand_total)
            afterTripDetail.arguments = bundle
            val transaction =
                activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container_fragments, afterTripDetail)
            transaction.commit()
        }



        view!!.contact_Number.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    ContactFragment::class.java
                )
            )
        }
    }




    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    // 1 hour 3 minute
    private val bookingDetails:


            Unit
        get() {
            progress_bar!!.visibility = View.VISIBLE
            val url = Utils.get_booking_detail
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                updateTLS(context)
            }
            val postParam: MutableMap<String?, String?> =
                HashMap()
            postParam["request_id"] = reqID
            val jsonObjReq: JsonObjectRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url, JSONObject(postParam as Map<*, *>),
                    Response.Listener { jsonObject ->
                        try {
                            val jsonStatus = jsonObject.getJSONObject("status")
                            if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                                val jsonRequest = jsonObject.getJSONObject("request")
                                val pickup = jsonRequest.getString("pickup")
                                val insurance = jsonRequest.getString("is_insurance")
                                val destination =
                                    jsonRequest.getString("destination")
                                val pickup_latitude =
                                    jsonRequest.getString("pickup_latitude")
                                val pickup_longitude =
                                    jsonRequest.getString("pickup_longitude")
                                val destination_latitude =
                                    jsonRequest.getString("destination_latitude")
                                val destination_longitude =
                                    jsonRequest.getString("destination_longitude")
                                var request_trip_start_time =
                                    jsonRequest.getString("request_trip_start_time")
                                var request_trip_end_time =
                                    jsonRequest.getString("request_trip_end_time")
                                val is_type = jsonRequest.getString("is_type")
                                val is_future = jsonRequest.getString("is_future")
                                val offered_price =
                                    jsonRequest.getString("offered_price")
                                val offered_advance =
                                    jsonRequest.getString("offered_advance")
                                rate_grand_total = jsonRequest.getString("rate_grand_total")
                                payment_type = jsonRequest.getString("payment_type")
                                charge_id = jsonRequest.getString("charge_id")
                                if (insurance == "0") {
                                    tv_insurance!!.text = "free"
                                } else {
                                    tv_insurance!!.text = "£20.00"
                                }
                                val vehicle_class_name = jsonRequest.getString("vehicle_class_name")
                                if (is_type.equals("Regular", ignoreCase = true)) {
                                    try {
                                        if (is_future == "1") {
                                            estimated_price!!.visible()
                                        } else {
                                            estimated_price!!.gone()
                                        }
                                        val hourly_rate =
                                            jsonRequest.getString("hourly_rate")
                                        val rate_distance_total =
                                            jsonRequest.getString("rate_distance_total")
                                        val rate_time_total =
                                            jsonRequest.getString("rate_time_total")
                                        val rate_helper_hourly_total =
                                            jsonRequest.getString("rate_helper_hourly_total")
                                        val rate_helper_per_minute_total =
                                            jsonRequest.getString("rate_helper_per_minute_total")
                                        val helpers_count =
                                            jsonRequest.getString("helpers_count")
                                        val rate_helper_per_minute =
                                            jsonRequest.getString("rate_helper_per_minute")
                                        val rate_grand_total =
                                            jsonRequest.getString("rate_grand_total")
                                        val helpers_count_ = helpers_count.toDouble()
                                        val hourly_rate_ = hourly_rate.toDouble()
                                        val rate_distance_total_ =
                                            rate_distance_total.toDouble()
                                        val rate_time_total_ =
                                            rate_time_total.toDouble()
                                        val rate_helper_hourly_total_ =
                                            helpers_count_ * rate_helper_hourly_total.toDouble()
                                        val rate_helper_per_minute_total_ =
                                            rate_helper_per_minute_total.toDouble()
                                        val simpleDateFormat =
                                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val requestDriveStartTime =
                                            jsonRequest.getString("request_drive_start_time")
                                        val requestTripEndTime =
                                            jsonRequest.getString("request_trip_end_time")
                                        val rate_per_minute_fare =
                                            jsonRequest.getString("rate_per_minute_fare")
                                        try {
                                            val date1 =
                                                simpleDateFormat.parse(requestDriveStartTime)
                                            val date2 =
                                                simpleDateFormat.parse(requestTripEndTime)
                                            val difference =
                                                date2.time - date1.time
                                            val days =
                                                (difference / (1000 * 60 * 60 * 24)).toInt()
                                            var hours =
                                                ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                                            val min =
                                                (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
                                            hours = if (hours < 0) -hours else hours
                                            print(" UMER SHERAZ: min $min")
                                            print(" UMER SHERAZ: hours $hours")
                                            var duration_ = ""
                                            var helper_ = ""
                                            if (hours == 0) {
                                                duration_ = "" + hourly_rate_
                                                helper_ = "" + helpers_count_ *
                                                        rate_helper_hourly_total_
                                            } else {
                                                // 1 hour 3 minute
                                                val total_hour_min = hours * 60
                                                var total_min = total_hour_min + min
                                                total_min =
                                                    total_min - 60 // minius first hour minute

                                                // GT DUARTION
                                                val remaining_min_price =
                                                    total_min * rate_per_minute_fare.toDouble()
                                                val total =
                                                    hourly_rate_ + remaining_min_price
                                                duration_ = "" + total

                                                // GET HELPER PRICE
                                                val remaining_min_price_helper =
                                                    total_min * rate_helper_per_minute.toDouble()
                                                val total_helper =
                                                    rate_helper_hourly_total_ + remaining_min_price_helper
                                                helper_ = "" + helpers_count_ * total_helper
                                            }
                                            tv_duartion!!.text =
                                                CONSTANTS.CURRENCY + CONSTANTS.precision.format(
                                                    duration_.toDouble()
                                                )
                                            Helper_Duration!!.text =
                                                CONSTANTS.CURRENCY + CONSTANTS.precision.format(
                                                    rate_helper_hourly_total_
                                                )
                                            tv_distance!!.text =
                                                CONSTANTS.CURRENCY + CONSTANTS.precision.format(
                                                    rate_distance_total_
                                                )
                                            val sub_total =
                                                (duration_.toDouble() + rate_helper_hourly_total_
                                                        + rate_distance_total_)
                                            savePreferences(
                                                CONSTANTS.sub_total,
                                                "" + sub_total,
                                                context!!
                                            )
                                            tv_sub_total!!.text =
                                                CONSTANTS.CURRENCY + CONSTANTS.precision.format(
                                                    sub_total
                                                )
                                            val grand_total =
                                                sub_total + insurance.toDouble()
                                            tv_amount_due!!.text =
                                                CONSTANTS.CURRENCY + CONSTANTS.precision.format(
                                                    rate_grand_total.toDouble()
                                                )
                                            tv_final_rate!!.text =
                                                CONSTANTS.CURRENCY + CONSTANTS.precision.format(
                                                    rate_grand_total.toDouble()
                                                )
                                        } catch (e: ParseException) {
                                            e.printStackTrace()
                                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    try {
                                        val offered_price_double =
                                            offered_price.toDouble()
                                        val offered_advance_double =
                                            offered_advance.toDouble()
                                        val pending_amount =
                                            offered_price_double - offered_advance_double
                                        tv_sub_total!!.text = CONSTANTS.CURRENCY + pending_amount
                                        tv_amount_due!!.text = CONSTANTS.CURRENCY + pending_amount
                                        tv_distance!!.text = "N/A"
                                        tv_final_rate!!.text = CONSTANTS.CURRENCY + pending_amount
                                        savePreferences(
                                            CONSTANTS.sub_total,
                                            "" + pending_amount,
                                            context!!
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                request_trip_start_time =
                                    changeDateFormat(
                                        request_trip_start_time,
                                        "yyyy-MM-dd HH:mm:ss",
                                        "dd-MMM-yyyy h:mm a"
                                    )
                                request_trip_end_time =
                                    changeDateFormat(
                                        request_trip_end_time,
                                        "yyyy-MM-dd HH:mm:ss",
                                        "dd-MMM-yyyy h:mm a"
                                    )
                                tv_trip_start_time!!.text = "" + request_trip_start_time
                                tv_trip_end_time!!.text = "" + request_trip_end_time
                                tv_pick_up_name!!.text = "" + pickup
                                tv_drop_off_name!!.text = "" + destination
                                tv_duration_2!!.text =
                                    jsonRequest.getString("distance_time_total") + " min"
                                tv_distance_2!!.text =
                                    jsonRequest.getString("distance_mile_total") + " miles"
                                tv_vehicle_name!!.text = "" + vehicle_class_name
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
                                    CONSTANTS.vehicle_class_name,
                                    vehicle_class_name,
                                    context!!
                                )
                                val jsonPassenger = jsonObject.getJSONObject("driver")
                                val driver_id = jsonPassenger.getString("driver_id")
                                val driver_first_name = jsonPassenger.getString("driver_first_name")
                                val driver_last_name = jsonPassenger.getString("driver_last_name")
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
                                view!!.tv_passenger_name!!.text =
                                    "You were moved by $driver_first_name"
                                Picasso.get().load(Utils.imageUrl + picture)
                                    .error(R.drawable.ic_man)
                                    .placeholder(R.drawable.ic_man).into(view!!.profile_image)
                                val pick_up_latLng =
                                    LatLng(pickup_latitude.toDouble(), pickup_longitude.toDouble())
                                val dropofflatLng = LatLng(
                                    destination_latitude.toDouble(),
                                    destination_longitude.toDouble()
                                )
                                driver_routes_list!!.clear()
                                driver_routes_list!!.add(pick_up_latLng)
                                driver_routes_list!!.add(dropofflatLng)
                                val polylineOptions = PolylineOptions()
                                    .addAll(driver_routes_list)
                                    .geodesic(true)
                                    .color(
                                        ContextCompat.getColor(
                                            activity!!,
                                            R.color.colorPrimary
                                        )
                                    )
                                    .width(8f)
                                val markerOptions_pick_up = MarkerOptions()
                                markerOptions_pick_up.position(pick_up_latLng)
                                markerOptions_pick_up.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black));
                                val markerOptions_drop_off = MarkerOptions()
                                markerOptions_drop_off.position(dropofflatLng)
                                markerOptions_drop_off.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black));
                                marker_pick_up = myMap!!.addMarker(markerOptions_pick_up)
                                marker_drop_off = myMap!!.addMarker(markerOptions_drop_off)
                                myMap!!.addPolyline(polylineOptions)
                                val builder = LatLngBounds.Builder()
                                builder.include(marker_pick_up!!.getPosition())
                                builder.include(marker_drop_off!!.getPosition())
                                val bounds = builder.build()
                                val scale =
                                    activity!!.applicationContext.resources
                                        .displayMetrics.density
                                val padding = (50 * scale + 0.5f).toInt()
                                myMap!!.moveCamera(
                                    CameraUpdateFactory.newLatLngBounds(
                                        bounds,
                                        padding
                                    )
                                )
                                progress_bar!!.visibility = View.GONE


                            } else {
                                progress_bar!!.visibility = View.GONE
                                Toast.makeText(
                                    context,
                                    jsonStatus.getString("message"),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            progress_bar!!.visibility = View.GONE
                        }
                    }, Response.ErrorListener { error ->
                        VolleyLog.d("TAG", "Error: " + error.message)
                        Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show()
                        progress_bar!!.visibility = View.GONE
                    }) {
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["Content-Type"] = "application/json; charset=utf-8"
                        headers["passenger_id"] = passenger_id!!
                        headers["registration_id"] = RegistrationID!!
                        return headers
                    }
                }
            MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
        }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mapView!!.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mapView!!.onPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            mapView!!.onStop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    private fun showMap(savedInstanceState: Bundle?) {
        try {
            mapView!!.onCreate(savedInstanceState)
            MapsInitializer.initialize(activity)
            mapView!!.getMapAsync(this@TripSummaryFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap!!.uiSettings.setAllGesturesEnabled(false)
    }
}