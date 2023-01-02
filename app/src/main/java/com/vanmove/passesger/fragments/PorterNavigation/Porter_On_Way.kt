package com.vanmove.passesger.fragments.PorterNavigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.model.PorterDetailHistory
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.Show_Sms_intent
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.custom_dialog_call_sms_options.*
import kotlinx.android.synthetic.main.porter_on_way.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class Porter_On_Way : Fragment(R.layout.porter_on_way),
    View.OnClickListener, OnMapReadyCallback {
    var timer_smoothly: Timer? = null
    private var myMap: GoogleMap? = null
    private var taskSmoothly: TimerTask? = null
    private var maker_car: Marker? = null
    private var sanvedInstance: Bundle? = null
    private var porter_id: String? = null

    private var pic: String? = null
    private var email: String? = null
    private var full_name: String? = null
    private var mobile: String? = null
    private var porter_request_id: String? = ""
    var updated_driver_latlng: LatLng? = null
    var alertDialog: AlertDialog? = null
    private var passengerId: String? = null
    private var registrationId: String? = null
    private var requestId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passengerId = Utils.getPreferences(CONSTANTS.passenger_id, context!!)
        registrationId =  Utils.getPreferences(CONSTANTS.RegistrationID, context!!)
        requestId = Utils.getPreferences(CONSTANTS.REQUEST_ID, context!!)
        activity!!.intent.apply {
            porter_id = getStringExtra("porter_id")
            pic = getStringExtra("pic")
            email = getStringExtra("email")
            full_name = getStringExtra("full_name")
            mobile = getStringExtra("mobile")
            porter_request_id = getStringExtra("porter_request_id")
            savePreferences(CONSTANTS.PORTER_REQUEST_ID, porter_request_id, context!!)
            Log.d("PorterNavigation", "req id: $porter_request_id ,porter_id: $porter_id")
        }

        savePreferences(
            CONSTANTS.driver_first_name, full_name,
            context!!
        )

        savePreferences(CONSTANTS.porter_id, porter_id, context!!)
        savePreferences(
            CONSTANTS.PORTER_REQUEST_ID,
            porter_request_id,
            context!!
        )
        sanvedInstance = this.arguments
        linkViews(sanvedInstance)
    }


    override fun onResume() {
        super.onResume()
        view!!.mapView!!.onResume()
        context!!.registerReceiver(
            mMessageReceiver,
            IntentFilter(CONSTANTS.Porter_Booking_COMPLETED)
        )
        goBackMethod()
    }

    override fun onStop() {
        super.onStop()
        view!!.mapView!!.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        view!!.mapView!!.onLowMemory()
    }

    private fun showMap(savedInstanceState: Bundle?) {
        view!!.mapView!!.onCreate(savedInstanceState)
        try {
            MapsInitializer.initialize(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        view!!.mapView!!.getMapAsync(this)
    }

    private fun linkViews(sanvedInstance: Bundle?) {


        view!!.tv_porter_name.setText(full_name)
        view!!.tv_porter_email.setText(email)
        val url = Utils.imageUrl + pic
        Picasso.get()
            .load(url)
            .error(R.drawable.ic_profile_no_server_pic)
            .into(view!!.iv_porter_pic)
        view!!.iv_btn_call_porter.setOnClickListener {
            showCallSMSSelectionOptions()
        }
        showMap(sanvedInstance)
        showDriversMovement()
    }

    private fun showCallSMSSelectionOptions() {
        val builder =
            AlertDialog.Builder(activity!!)
        builder.setView(R.layout.custom_dialog_call_sms_options)
        alertDialog = builder.create()
        alertDialog!!.show()

        alertDialog!!.rl_dialog_call!!.setOnClickListener(this)
        alertDialog!!.rl_dialog_sms!!.setOnClickListener(this)
        alertDialog!!.close_dialog!!.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        context!!.unregisterReceiver(mMessageReceiver)
    }

    override fun onPause() {
        super.onPause()
        view!!.mapView!!.onPause()
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_dialog_call -> {
                alertDialog!!.dismiss()
                Dial_Number(context!!, mobile!!)
            }
            R.id.rl_dialog_sms -> {
                alertDialog!!.dismiss()
                Show_Sms_intent(mobile!!, context!!)
            }
            R.id.close_dialog -> alertDialog!!.dismiss()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
    }

    private fun showDriversMovement() {
        val handler = Handler()
        timer_smoothly = Timer()
        taskSmoothly = object : TimerTask() {
            override fun run() {
                handler.post { currentPorters }
//                handler.post { currentPortersLocationUpdates }
            }
        }
        timer_smoothly!!.schedule(taskSmoothly, 0, 10000) //execute in every 50000 ms
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
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                val currentPosition = LatLng(
                    startPosition.latitude * (1 - t) + final_position.latitude * t,
                    startPosition.longitude * (1 - t) + final_position.longitude * t
                )
                driver_marker.position = currentPosition
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                } else {
                    driver_marker.isVisible = true
                }
            }
        })
    }


    private val currentPorters: Unit
        get() {
            porter_id = Utils.getPreferences(CONSTANTS.PORTER_ID, context!!)
            requestId = Utils.getPreferences(CONSTANTS.REQUEST_ID, context!!)
            val req = requestId
            Log.d("IDs", "RequestId: $requestId, porterId: $porter_id")

            val postParam: MutableMap<String?, String?> =
                HashMap()
            postParam["request_id"] = requestId
            postParam["user_type"] = "passenger"
            postParam["porter_id"] = porter_id

            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(postParam as Map<*, *>).toString()
            )
            val headers: MutableMap<String?, String?> =
                HashMap()
            headers["Content-Type"] = "application/json; charset=utf-8"
            headers["registration_id"] = registrationId
            headers["user_id"] = passengerId
            CONSTANTS.mApiService.currentPorterDetail(body, headers)!!
                .enqueue(object : Callback<PorterDetailHistory?> {
                    override fun onResponse(
                        call: Call<PorterDetailHistory?>,
                        response: retrofit2.Response<PorterDetailHistory?>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.status!!.code == "1000") {
                                try {
                                    val porterHistory = response.body()
                                    val jsonObject = response.body()!!.request
                                    val latitude = jsonObject!!.latitude.toDouble()
                                    val longitude = jsonObject.longitude.toDouble()
                                    updated_driver_latlng = LatLng(latitude, longitude)
                                    val previous_location =
                                        Location("")
                                    if (maker_car != null) {
                                        previous_location.latitude = maker_car!!.position.latitude
                                        previous_location.longitude = maker_car!!.position.longitude
                                    } else {
                                        previous_location.latitude = latitude
                                        previous_location.longitude = longitude
                                    }
                                    val update_location =
                                        Location("")
                                    update_location.latitude = updated_driver_latlng!!.latitude
                                    update_location.longitude = updated_driver_latlng!!.longitude
                                    if (maker_car == null) {
                                        val icon_driver =
                                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_porter)
                                        val markerOptions = MarkerOptions()
                                        markerOptions.position(updated_driver_latlng!!)
                                        markerOptions.icon(icon_driver)
                                        markerOptions.flat(true)
                                        maker_car = myMap!!.addMarker(markerOptions)
                                    }
                                    if (maker_car != null) {
                                        smoothlyMoveTaxi(maker_car!!, updated_driver_latlng!!)
                                    }
                                    myMap!!.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            updated_driver_latlng,
                                            13f
                                        )
                                    )

                                }catch (ex: Exception){
                                    ex.printStackTrace()
                                }
                            }else {
                                Toast.makeText(
                                    context,
                                    response.body()!!.status!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, response.raw().message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<PorterDetailHistory?>,
                        e: Throwable
                    ) {
                        Utils.showToast(e.message)
                    }
                })
        }


//TODO: Update current location Porter
    private val currentPortersLocationUpdates: Unit
        private get() {
            val url = Utils.load_driver_location
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                updateTLS(context)
            }
            val postParam: MutableMap<String?, String?> =
                HashMap()
            postParam["type"] = "passenger"
            postParam["user_id"] = passengerId
            postParam["request_id"] = requestId
            val jsonObjReq: JsonObjectRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    JSONObject(postParam as Map<*, *>),
                    Response.Listener { jsonObject ->
                        Log.d("PorterNavigation", jsonObject.toString())
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
                                    if (maker_car == null) {
                                        val icon_driver =
                                            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_porter)
                                        val markerOptions = MarkerOptions()
                                        markerOptions.position(updated_driver_latlng!!)
                                        markerOptions.icon(icon_driver)
                                        markerOptions.flat(true)
                                        maker_car = myMap!!.addMarker(markerOptions)
                                    }
                                    if (maker_car != null) {
                                        smoothlyMoveTaxi(maker_car!!, updated_driver_latlng!!)
                                    }
                                    myMap!!.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            updated_driver_latlng,
                                            13f
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    ,
                    Response.ErrorListener { error ->
                        VolleyLog.d(
                            "TAG",
                            "Error: " + error.message
                        )
                        Log.d("PorterNavigation", error.message.toString())
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["Content-Type"] = "application/json; charset=utf-8"
                        headers["user_id"] = passengerId!!
                        headers["registration_id"] = registrationId!!
                        return headers
                    }
                }
            MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
        }

    private fun goBackMethod() {
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                activity!!.finish()
                true
            } else false
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            try {
                val porter_fare = intent.getStringExtra("porter_fare")
                val fragment: Fragment = PaymentPorter()
                val transaction =
                    fragmentManager!!.beginTransaction()
                val bundle = Bundle()
                bundle.putString("porter_fare", porter_fare)
                fragment.arguments = bundle
                transaction.replace(R.id.container_fragments, fragment)
                transaction.commitAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}