package com.vanmove.passesger.fragments.MoveNavigationScreen

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.FullImage.ZoomImage
import com.vanmove.passesger.fragments.TripSummaryFragment
import com.vanmove.passesger.interfaces.DirectionFinderListener
import com.vanmove.passesger.model.Route
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DirectionFinder
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.KmToMile
import com.vanmove.passesger.utils.Utils.Show_Sms_intent
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.custom_marker_layout.view.*
import kotlinx.android.synthetic.main.fragment_move_started.view.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class FragmentMoveStarted : Fragment(R.layout.fragment_move_started), OnMapReadyCallback,
    DirectionFinderListener {
    var custome_marker: View? = null
    var timer_smoothly: Timer? = null
    var destination_latitude: String? = null
    var destination_longitude: String? = null
    private var fm: FragmentManager? = null
    private var myMap: GoogleMap? = null
    private var taskSmoothly: TimerTask? = null
    private var progressDialog: ProgressDialog? = null
    private var maker_car: Marker? = null
    private var polyline_list: MutableList<Polyline>? = null
    private var RegistrationID: String? = null
    private var mobile: String? = null
    private var request_id: String? = null
    private var passenger_id: String? = null
    private val polyline_path: Polyline? = null
    var request_drive_start_time: String? = ""
    var alertDialog: AlertDialog? = null
    var updated_driver_latlng: LatLng? = null
    var destination_latlng: LatLng? = null
    var latitude_pickup = 0.0
    var longitude_pickup = 0.0
    private var asynchronous_task_map_movement: TimerTask? = null
    private var timer_map_movement: Timer? = null
    private var count_animation = -1
    var latitude_destination = 0.0
    var longitude_destination = 0.0
    var tv_marker: TextView? = null

    private var myMapFragment: SupportMapFragment? = null

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            try {
                request_drive_start_time = changeDateFormat(
                    request_drive_start_time,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd-MMM-yyyy HH:mm"
                )
                val sdf = SimpleDateFormat("dd-MMM-yyyy HH:mm")
                val currentDateandTime = sdf.format(Date())
                view!!.tv_driver_status!!.text = """YOUR MOVE HAS ENDED
Start Time : $request_drive_start_time
 End Time : $currentDateandTime"""
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_fragments, TripSummaryFragment())
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(context)
        progressDialog!!.show()
        progressDialog!!.setMessage("Please wait")
        request_id = getPreferences(CONSTANTS.REQUEST_ID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        destination_latitude = getPreferences(
            CONSTANTS.destination_latitude,
            context!!
        )
        destination_longitude = getPreferences(
            CONSTANTS.destination_longitude,
            context!!
        )
        getBookingDetails(passenger_id, RegistrationID, request_id)
        linkViews()
    }

    override fun onResume() {
        super.onResume()
        try {
            myMapFragment!!.onResume()
            context!!.registerReceiver(
                mMessageReceiver,
                IntentFilter(CONSTANTS.unique_name_completed)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun linkViews() {
        fm = childFragmentManager
        myMapFragment = fm!!.findFragmentById(R.id.myMapFragment) as SupportMapFragment?

        custome_marker =
            (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_layout,
                null
            )
        tv_marker = custome_marker!!.num_txt


        view!!.rl_dialog_call.setOnClickListener {
            Dial_Number(context!!, mobile!!)
        }
        view!!.rl_dialog_sms.setOnClickListener {
            Show_Sms_intent(mobile!!, context!!)
        }

        view!!.Navigate.setOnClickListener {
            open_map(
                LatLng(latitude_pickup, longitude_pickup),
                LatLng(latitude_destination, longitude_destination)
            )
        }



        myMapFragment!!.getMapAsync(this@FragmentMoveStarted)
        progressDialog!!.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            myMapFragment!!.onDestroy()
            context!!.unregisterReceiver(mMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            myMapFragment!!.onPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (taskSmoothly != null) {
                timer_smoothly!!.cancel()
                taskSmoothly!!.cancel()
                taskSmoothly = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
    }

    override fun onDirectionFinderSuccess(
        routes: List<Route?>?,
        operationName: String?
    ) {
        try {
            if (operationName == "path") {
                polyline_list = ArrayList()
                for (route in routes!!) {
                    val polylineOptions =
                        PolylineOptions().geodesic(true).color(Color.BLACK)
                            .width(7f)
                    for (i in route!!.points!!.indices) polylineOptions.add(route.points!![i])
                    polyline_list!!.add(myMap!!.addPolyline(polylineOptions))
                    val distance = route.distance!!.value
                    val duration = route.duration!!.text
                    val distance_mile =
                        KmToMile("" + distance)
                    view!!.slide_btn_textmove!!.text =
                        "To dropoff ${distance_mile} Miles - ${duration}"
                    tv_marker!!.text = duration + ""
                }
            } else {
                for (i in routes!!.indices) {
                    val route = routes[i]
                    if (polyline_path != null) {
                        polyline_path.points = route!!.points
                    }
                    val distance = route!!.distance!!.value
                    val duration = route.duration!!.text
                    try {
                        val distance_mile =
                            KmToMile("" + distance)
                        view!!.slide_btn_textmove!!.text =
                            "To dropoff ${distance_mile} Miles - ${duration}"
                        tv_marker!!.text = duration + ""
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDirectionFinderStart(operationName: String?) {
        if (operationName == "path") {
            progressDialog = ProgressDialog.show(
                context, "Please wait.",
                "Finding direction..!", true
            )
            if (polyline_list != null) {
                for (polyline in polyline_list!!) {
                    polyline.remove()
                }
            }
            progressDialog!!.dismiss()
        }
    }

    private fun showDriversMovement(
        driver_id: String,
        RegistrationID: String?
    ) {
        val handler = Handler()
        timer_smoothly = Timer()
        taskSmoothly = object : TimerTask() {
            override fun run() {
                handler.post { getDriverLocation(driver_id, RegistrationID) }
            }
        }
        timer_smoothly!!.schedule(taskSmoothly, 0, 10000)
    }

    private fun smoothlyMoveTaxi(
        driver_marker: Marker,
        final_position: LatLng
    ) {
        val startPosition = driver_marker.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val durationInMs = 10000f
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                val currentPosition = LatLng(
                    startPosition.latitude * (1 - t) + final_position.latitude * t,
                    startPosition.longitude * (1 - t) + final_position.longitude * t
                )
                driver_marker.position = currentPosition

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    driver_marker.isVisible = true
                }
            }
        })
    }

    private fun open_map(source: LatLng, destination: LatLng) {
        val uri = "http://maps.google.com/maps?f=d&hl=en&saddr=" + source.latitude +
                "," + source.longitude + "&daddr=" + destination.latitude + "," + destination.longitude
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(Intent.createChooser(intent, "Select an application"))
    }


    private fun getBookingDetails(
        passenger_id: String?, RegistrationID_: String?,
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
                            request_drive_start_time =
                                jsonRequest.getString("request_drive_start_time")
                            val date = changeDateFormat(
                                Utils.GetCurrentTimeStamp(),
                                "HH:mm dd-MMMM-yyyy",
                                "dd-MMM-yyyy"
                            )
                            val time = changeDateFormat(
                                Utils.GetCurrentTimeStamp(),
                                "HH:mm dd-MMMM-yyyy",
                                "HH:mm"
                            )
                            view!!.tv_driver_status!!.text =
                                "YOUR MOVE HAS STARTED\nStart Date: ${date}\n Start Time : ${time}"

                            latitude_pickup = pickup_latitude.toDouble()
                            longitude_pickup = pickup_longitude.toDouble()
                            latitude_destination = destination_latitude.toDouble()
                            longitude_destination = destination_longitude.toDouble()
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
                            Timber.d("DriverLatLng: Lat: $driver_latitude, Lng: $driver_longitude")
                            Log.d("DriverLatLng:"," Lat: $driver_latitude, Lng: $driver_longitude")
                            val email = jsonPassenger.getString("email")
                            mobile = jsonPassenger.getString("mobile")
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
                            val vehicle = jsonObject.getJSONObject("vehicle")
                            val vehicle_license_number =
                                vehicle.getString("vehicle_license_number")

                            view!!.tv_Van!!.text = vehicle_class_name
                            view!!.tv_reg!!.text = vehicle_license_number
                            view!!.tv_driver_name!!.text = "$driver_first_name $driver_last_name"
                            Picasso.get()
                                .load(Utils.imageUrl + picture)
                                .error(R.drawable.ic_profile_no_server_pic)
                                .into(view!!.iv_vehicle)
                            view!!.iv_vehicle!!.setOnClickListener {
                                startActivity(
                                    Intent(activity, ZoomImage::class.java)
                                        .putExtra(CONSTANTS.image_link, picture)
                                )
                            }
                            val pickup_location =
                                LatLng(latitude_pickup, longitude_pickup)
                            val des_location =
                                LatLng(latitude_destination, longitude_destination)
                            val pickup_marker = MarkerOptions()
                            pickup_marker.position(pickup_location)
                            pickup_marker.title("Pickup")
                            pickup_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                            myMap!!.addMarker(pickup_marker)
                            val markerOptions_drop_off = MarkerOptions()
                            markerOptions_drop_off.position(des_location)
                            markerOptions_drop_off.title("Drop Off")
                            markerOptions_drop_off.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                            myMap!!.addMarker(markerOptions_drop_off)
                            val builder = LatLngBounds.Builder()
                            builder.include(pickup_location)
                            builder.include(des_location)
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
                            drawShortestPath(pickup_location, des_location)
                            showDriversMovement(driver_id, RegistrationID)
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

    private fun getDriverLocation(
        driver_id: String,
        RegistrationID: String?
    ) {
        val url = Utils.load_driver_location
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["type"] = "driver"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val jsonRequest = jsonObject.getJSONObject("data")
                            val location_current_latitude =
                                jsonRequest.getString("location_current_latitude")
                            val location_current_longitude =
                                jsonRequest.getString("location_current_longitude")
                            try {
                                val latitude_d =
                                    location_current_latitude.toDouble()
                                val longitude_d =
                                    location_current_longitude.toDouble()
                                updated_driver_latlng = LatLng(latitude_d, longitude_d)
                                destination_latlng = LatLng(
                                    latitude_destination,
                                    longitude_destination
                                )
                                val previous_location =
                                    Location("")
                                if (maker_car != null) {
                                    previous_location.latitude = maker_car!!.position.latitude
                                    previous_location.longitude = maker_car!!.position.longitude
                                } else {
                                    previous_location.latitude = latitude_d
                                    previous_location.longitude = longitude_d
                                }
                                val update_location =
                                    Location("")
                                update_location.latitude = updated_driver_latlng!!.latitude
                                update_location.longitude = updated_driver_latlng!!.longitude
                                val distance_covered =
                                    previous_location.distanceTo(update_location)
                                if (maker_car == null) {
                                    val icon_driver =
                                        BitmapDescriptorFactory.fromResource(R.drawable.ic_vanmove)
                                    val markerOptions = MarkerOptions()
                                    markerOptions.position(updated_driver_latlng!!)
                                    markerOptions.icon(icon_driver)
                                    markerOptions.flat(true)
                                    maker_car = myMap!!.addMarker(markerOptions)
                                }
                                if (maker_car != null) {
                                    smoothlyMoveTaxi(maker_car!!, updated_driver_latlng!!)
                                }
                                if (distance_covered >= Utils.DISTANCE_TO_ROTATE) {
                                    val bearing2 =
                                        previous_location.bearingTo(update_location)
                                    maker_car!!.rotation = bearing2
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
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
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["user_id"] = driver_id
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }

    private fun drawShortestPath(source: LatLng, destinations: LatLng) {
        val location_pick_up = Location("")
        location_pick_up.latitude = destinations.latitude
        location_pick_up.longitude = destinations.longitude
        val location_driver = Location("")
        location_driver.latitude = source.latitude
        location_driver.longitude = source.longitude

        //checking if the distance covered is greater than 100 or not
        val distance_covered = location_driver.distanceTo(location_pick_up)
        if (distance_covered >= 100) {
            val directionFinder = DirectionFinder(
                this@FragmentMoveStarted, source,
                destinations, "path"
            )
            directionFinder.showDirection()
        } else {
            polyline_path?.remove()
        }
    }

    private fun startMapAnimation() {
        if (asynchronous_task_map_movement == null) {
            startMapMovementHandler()
            Log.d("handler_state", "Map Movement Handler is started")
        }
    }

    private fun startMapMovementHandler() {
        val handler = Handler()
        timer_map_movement = Timer()
        asynchronous_task_map_movement = object : TimerTask() {
            override fun run() {
                handler.post {
                    try {
                        if (maker_car != null) {
                            if (count_animation >= 0) {
                                val builder = LatLngBounds.Builder()
                                builder.include(maker_car!!.position)
                                builder.include(destination_latlng)
                                val bounds = builder.build()
                                val scale =
                                    activity!!.applicationContext.resources
                                        .displayMetrics.density
                                val padding = (50 * scale + 0.5f).toInt()
                                myMap!!.animateCamera(
                                    CameraUpdateFactory.newLatLngBounds(
                                        bounds,
                                        padding
                                    )
                                )
                            } else {
                                val builder = LatLngBounds.Builder()
                                builder.include(maker_car!!.position)
                                builder.include(destination_latlng)
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
                                count_animation = 1
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
        timer_map_movement!!.schedule(asynchronous_task_map_movement, 0, 2000)
    }
}