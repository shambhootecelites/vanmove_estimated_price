package com.vanmove.passesger.fragments.MoveNavigationScreen

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.RelativeLayout
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
import com.vanmove.passesger.interfaces.DirectionFinderListener
import com.vanmove.passesger.model.Route
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DirectionFinder
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.Show_Sms_intent
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.fragment_vanon_its_way.view.*
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class FragmentVanOnItsWay : Fragment(R.layout.fragment_vanon_its_way), OnMapReadyCallback,
    DirectionFinderListener {
    var timer_smoothly: Timer? = null

    var number = ""
    var pickup_latitude: String? = null
    var pickup_longitude: String? = null
    var custome_marker_object: Marker? = null
    private var myMapFragment: SupportMapFragment? = null
    private var fm: FragmentManager? = null
    private var myMap: GoogleMap? = null
    private val vAnimator = ValueAnimator()
    private var taskSmoothly: TimerTask? = null
    private var myCircle: Circle? = null
    private var maker_car: Marker? = null
    private var RegistrationID: String? = null
    private var originMarkers: MutableList<Marker?> =
        ArrayList()
    var polylineOptions_path: PolylineOptions? = null
    private var polyline_path: Polyline? = null
    private var request_id: String? = null
    private var passenger_id: String? = null
    private var driverId: String? = null

    var distance = ""
    var duration = ""
    var alertDialog: AlertDialog? = null
    private var asynchronous_task_map_movement: TimerTask? = null
    private var timer_map_movement: Timer? = null
    private var count_animation = -1


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        driverId = getPreferences(CONSTANTS.driver_id, context!!)
        linkViews()
        request_id = getPreferences(CONSTANTS.REQUEST_ID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
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
                            var request_driver_arrive_time =
                                jsonRequest.getString("request_driver_arrive_time")
                            val status = jsonRequest.getString("is_status")
                            val vehicle_class_name =
                                jsonRequest.getString("vehicle_class_name")
                            pickup_latitude = jsonRequest.getString("pickup_latitude")
                             pickup_longitude = jsonRequest.getString("pickup_longitude")
                            Timber.d("LiveScreen: Latitude: $pickup_latitude, Longitude: $pickup_longitude")
                            request_driver_arrive_time =
                                changeDateFormat(request_driver_arrive_time,
                                    "yyyy-MM-dd HH:mm:ss",
                                    "HH:mm dd-MMMM-yyyy")
                            if (status == "5" || status == "ARRIVED") {
                                view!!.tv_driver_status!!.text = """
                                    YOUR DRIVER HAS ARRIVED
                                    $request_driver_arrive_time
                                    """.trimIndent()
                                view!!.estiamed_time_tv!!.gone()
                            }
                            val jsonPassenger = jsonObject.getJSONObject("driver")
                            val driver_id = jsonPassenger.getString("driver_id")
                            val driver_first_name =
                                jsonPassenger.getString("driver_first_name")
                            val driver_last_name =
                                jsonPassenger.getString("driver_last_name")
                            val mobile = jsonPassenger.getString("mobile")
                            val country_code =
                                jsonPassenger.getString("country_code")
                            val picture = jsonPassenger.getString("picture")
                            val vehicle = jsonObject.getJSONObject("vehicle")
                            val vehicle_license_number =
                                vehicle.getString("vehicle_license_number")
                            getDriverLocation(driver_id, RegistrationID!!)


                            view!!.tv_Van!!.text = vehicle_class_name
                            view!!.tv_reg!!.text = vehicle_license_number
                            view!!.tv_driver_name!!.text = "$driver_first_name $driver_last_name"
                            number = country_code + mobile
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

    override fun onResume() {
        super.onResume()
        try {
            context!!.registerReceiver(mMessageReceiver, IntentFilter(CONSTANTS.broadcat_name))
            myMapFragment!!.onResume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun linkViews() {
        fm = childFragmentManager
        myMapFragment = fm!!.findFragmentById(R.id.map_on_its_way) as SupportMapFragment?
        myMapFragment!!.getMapAsync(this)


        view!!.rl_dialog_call.setOnClickListener {
            Dial_Number(context!!, number)
        }
        view!!.rl_dialog_sms.setOnClickListener {
            Show_Sms_intent(number, context!!)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            context!!.unregisterReceiver(mMessageReceiver)
            myMapFragment!!.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        myMapFragment!!.onPause()
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
        getBookingDetails(passenger_id, RegistrationID, request_id)


    }

    override fun onDirectionFinderSuccess(
        routes: List<Route?>?,
        operationName: String?
    ) {
        try {
            if (operationName == "path") {
                originMarkers = ArrayList()
                if (polylineOptions_path == null) {
                    for (route in routes!!) {
                        val builder = LatLngBounds.Builder()
                        builder.include(route!!.startLocation)
                        builder.include(route.endLocation)
                        val bounds = builder.build()
                        val scale =
                            activity!!.applicationContext.resources
                                .displayMetrics.density
                        val padding = (50 * scale + 0.5f).toInt()
                        myMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                        maker_car = myMap!!.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vanmove))
                                .position(route.startLocation!!)
                        )
                        if (vAnimator.isStarted) {
                            vAnimator.end()
                        }
                        myCircle = myMap!!.addCircle(
                            CircleOptions().center(route.endLocation)
                                .fillColor(Color.parseColor("#97C1D7"))
                                .strokeColor(Color.parseColor("#97C1D7"))
                                .radius(350.0)
                        )
                        vAnimator.repeatCount = ValueAnimator.INFINITE
                        vAnimator.repeatMode = ValueAnimator.RESTART /* PULSE */
                        vAnimator.setIntValues(0, 100)
                        vAnimator.duration = 2000
                        vAnimator.setEvaluator(IntEvaluator())
                        vAnimator.interpolator = AccelerateDecelerateInterpolator()
                        vAnimator.addUpdateListener { valueAnimator ->
                            val animatedFraction =
                                valueAnimator.animatedFraction
                            myCircle!!.setRadius(animatedFraction * 100.toDouble())
                        }
                        originMarkers.add(maker_car)

                        custome_marker_object = myMap!!.addMarker(
                            MarkerOptions()
                                .position(route.endLocation!!)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                        )
                        polylineOptions_path =
                            PolylineOptions().geodesic(true).color(Color.BLACK)
                                .width(7f)
                        for (i in route.points!!.indices) {
                            polylineOptions_path!!.add(route.points!![i])
                        }
                        polyline_path = myMap!!.addPolyline(polylineOptions_path)
                        distance = route.distance!!.text
                        duration = route.duration!!.text
                        try {
                            if (distance.contains("km")) {
                                distance = distance.replace("km", "")
                            } else if (distance.contains("m")) {
                                distance = distance.replace("m", "")
                            }
                            val dis = distance.toDouble()
                            val dis_mile = dis * 0.621371
                            view!!.estiamed_time_tv!!.text =
                                "${String.format("%.2f", dis_mile)} Miles - ETA ${duration}"

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    custome_marker_object!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                } else {
                    for (i in routes!!.indices) {
                        val route = routes[i]
                        if (polyline_path != null) {
                            polyline_path!!.points = route!!.points
                        }
                        distance = route!!.distance!!.text
                        duration = route.duration!!.text
                        try {
                            if (distance.contains("km")) {
                                distance = distance.replace("km", "")
                            } else if (distance.contains("m")) {
                                distance = distance.replace("m", "")
                            }
                            val dis = distance.toDouble()
                            val dis_mile = dis * 0.621371
                            view!!.estiamed_time_tv!!.text =
                                "${String.format("%.2f", dis_mile)} Miles - ETA ${duration}"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    custome_marker_object!!.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_black))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDirectionFinderStart(operationName: String?) {
        if (operationName == "path") {
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
                handler.post { getDriverLocation(driver_id, RegistrationID!!) }
            }
        }
        timer_smoothly!!.schedule(taskSmoothly, 0, 10000)
    }

    private fun getDriverLocation(
        driver_id: String,
        RegistrationID: String
    ) {
        val url = Utils.load_driver_location
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["type"] = "passenger"
        postParam["user_id"] = driverId
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
                            val jsonRequest = jsonObject.getJSONObject("data")
                            val location_current_latitude =
                                jsonRequest.getString("location_current_latitude")
                            val location_current_longitude =
                                jsonRequest.getString("location_current_longitude")


                            var updated_driver_latlng: LatLng? = null
                            try {

                                /*this is for current location*/
                                val latitude_d =
                                    location_current_latitude.toDouble()
                                val longitude_d =
                                    location_current_longitude.toDouble()
                                updated_driver_latlng = LatLng(latitude_d, longitude_d)
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
                                update_location.latitude = updated_driver_latlng.latitude
                                update_location.longitude = updated_driver_latlng.longitude
                                drawShortestPath(
                                    updated_driver_latlng,
                                    LatLng(
                                        pickup_latitude!!.toDouble(),
                                        pickup_longitude!!.toDouble()
                                    )
                                )
                                val distance_covered =
                                    previous_location.distanceTo(update_location)
                                if (distance_covered >= Utils.DISTANCE_TO_ROTATE) {
                                    val bearing2 =
                                        previous_location.bearingTo(update_location)
                                    maker_car!!.rotation = bearing2
                                }
                                if (maker_car != null) {
                                    smoothlyMoveTaxi(updated_driver_latlng)
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
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["user_id"] = passenger_id!!
                    headers["registration_id"] = RegistrationID
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
        val distance_covered = location_driver.distanceTo(location_pick_up)
        if (distance_covered >= 100) {
            val directionFinder = DirectionFinder(
                this@FragmentVanOnItsWay, source,
                destinations, "path"
            )
            directionFinder.showDirection()
        } else {
            if (polyline_path != null) {
                polyline_path!!.remove()
            }
            maker_car = myMap!!.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vanmove))
                    .position(source)
            )
            val cameraPosition = CameraPosition.Builder()
                .target(source)
                .zoom(15f)
                .tilt(0f)
                .build()
            myMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun smoothlyMoveTaxi(final_position: LatLng) {
        try {
            val startPosition = maker_car!!.position
            val handler = Handler()
            val start = SystemClock.uptimeMillis()
            val interpolator: Interpolator =
                AccelerateDecelerateInterpolator()
            val durationInMs = 10000f
            val hideMarker = false
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
                    maker_car!!.position = currentPosition

                    // Repeat till progress is complete.
                    if (t < 1) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16)
                    } else {
                        if (hideMarker) {
                            maker_car!!.isVisible = false
                        } else {
                            maker_car!!.isVisible = true
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (intent != null) {
                val status = intent.getStringExtra("status")
                if (status == "Driver has arrived") {
                    view!!.tv_driver_status!!.text =
                        "YOUR DRIVER HAS ARRIVED \n${Utils.GetCurrentTimeStamp()}"
                    view!!.estiamed_time_tv!!.gone()
                } else if (status == "Job started") {
                    val newFragment = FragmentMoveStarted()
                    val transaction =
                        fragmentManager!!.beginTransaction()
                    transaction.replace(R.id.container_fragments, newFragment)
                    transaction.commitAllowingStateLoss()
                }
            }
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
                                builder.include(custome_marker_object!!.position)
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
                                builder.include(custome_marker_object!!.position)
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

    companion object {
        private fun createDrawableFromView(
            context: Context?,
            view: View?
        ): Bitmap {
            val displayMetrics = DisplayMetrics()
            (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            view!!.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }
    }
}