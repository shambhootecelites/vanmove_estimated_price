package com.vanmove.passesger.fragments.MoveNavigationScreen

import android.Manifest
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.AdvancedPayment.AdvancedPaymentActivity
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.activities.SelectVechileActivity
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DateTimePicker
import com.vanmove.passesger.utils.NotificationUtils
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageThreeButtons
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageTwoButtons
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.fragment_connecting.view.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ConnectingFragment : Fragment(R.layout.fragment_connecting), OnMapReadyCallback,
    OnClickTwoButtonsAlertDialog2 {
    var vehicle_name: String? = null
    var RegistrationID: String? = null
    var passenger_id: String? = null
    private var myMapFragment: SupportMapFragment? = null
    private var fm: FragmentManager? = null
    private var myMap: GoogleMap? = null
    private var myCircle: Circle? = null
    var vAnimator: ValueAnimator? = null
    private var reqID: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linkViews()
        RegistrationID = getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id = getPreferences(CONSTANTS.passenger_id, context!!)


        view.cancel_job.setOnClickListener {
            showAlertMessageTwoButtons(
                getContext(), "Cancel Job",
                "Are you sure you want to cancel this job?", "Yes", "No",
                "cancel_job", this@ConnectingFragment
            )
        }
    }

    private fun linkViews() {
        vAnimator = ValueAnimator()
        fm = childFragmentManager
        myMapFragment = fm!!.findFragmentById(R.id.mapp_connected) as SupportMapFragment?
        myMapFragment!!.getMapAsync(this@ConnectingFragment)
    }

    override fun onResume() {
        super.onResume()
        Log.d("Request_Json", "$mMessageReceiver")
        context!!.registerReceiver(mMessageReceiver, IntentFilter(CONSTANTS.unique_name_accepted))
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            context!!.unregisterReceiver(mMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun cancelRequest(
        passenger_id: String?,
        RegistrationID_: String?,
        request_id: String?
    ) {
        val url =
            Utils.update_booking_request_status_passenger
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = request_id
        postParam["status"] = "CANCELLED"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam as Map<*, *>),
                Response.Listener {
                    startActivity(Intent(activity, MainScreenActivity::class.java))
                    activity!!.finish()
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
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val driver_not_found = intent.getStringExtra("driver_not_found")
            reqID = intent.getStringExtra("reqID")
            savePreferences(CONSTANTS.REQUEST_ID, reqID, context)
            if (driver_not_found.equals("true", ignoreCase = true)) {
                showAlertMessageThreeButtons(
                    context, "Booking Message",
                    "Sorry the van you selected has just been booked by another close by customer, you can make a new request for a later time today or tomorrow, Alternatively you can simply select a different size van that is available now.",
                    "Book for later", "Change job details", "Cancel Rqeuest", "not_found",
                    this@ConnectingFragment
                )
            } else {
                val newFragment = ConnectedFragment()
                val transaction =
                    fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_fragments, newFragment)
                transaction.commitAllowingStateLoss()
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        val pickup_lat = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )!!.toDouble()
        val pickup_lon = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            context!!
        )!!.toDouble()
        val location = LatLng(pickup_lat, pickup_lon)
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val cameraPosition = CameraPosition.Builder()
            .target(location).zoom(15f).build()
        myMap!!.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(cameraPosition)
        )
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.black_marker)
        myMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(pickup_lat, pickup_lon))
                .icon(icon)
        )
        if (vAnimator!!.isStarted) {
            vAnimator!!.end()
        }
        myCircle = myMap!!.addCircle(
            CircleOptions().center(LatLng(pickup_lat, pickup_lon))
                .fillColor(Color.parseColor("#97C1D7"))
                .strokeColor(Color.parseColor("#97C1D7")).radius(450.0)
        )
        vAnimator!!.repeatCount = ValueAnimator.INFINITE
        vAnimator!!.repeatMode = ValueAnimator.RESTART /* PULSE */
        vAnimator!!.setIntValues(0, 100)
        vAnimator!!.duration = 2000
        vAnimator!!.setEvaluator(IntEvaluator())
        vAnimator!!.interpolator = AccelerateDecelerateInterpolator()
        vAnimator!!.addUpdateListener { valueAnimator ->
            val animatedFraction = valueAnimator.animatedFraction
            myCircle!!.setRadius(animatedFraction * 100.toDouble())
        }
        vAnimator!!.start()
    }


    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "cancel_job") {
            val request_id =
                getPreferences(CONSTANTS.REQUEST_ID, context!!)
            cancelRequest(passenger_id, RegistrationID, request_id)
        } else if (dialog_name == "not_found") {
            DateTimePicker.ShowDatePicker(
                activity!!.supportFragmentManager,
                listener
            )
            Utils.showToastLong(getString(R.string.job_time))
        }
    }

    override fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "not_found") {
            val distance_in_miles =
                getPreferences(
                    CONSTANTS.distance_in_miles,
                    context!!
                )
            startActivity(
                Intent(activity, SelectVechileActivity::class.java)
                    .putExtra("distance_in_miles", distance_in_miles)
            )
            activity!!.finish()
        }
    }

    override fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "not_found") {
            startActivity(
                Intent(activity, MainScreenActivity::class.java)
            )
            activity!!.finish()
        }
    }

    private val listener: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            val compare_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            CONSTANTS.current_ride_data["timestamp"] =  compare_format.format(date)

            newBookingRequestFuture()
        }

        override fun onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    }

    private fun newBookingRequestFuture() {
        val url = Utils.new_upcoming_move
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(CONSTANTS.current_ride_data as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            savePreferences(
                                CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                                "",
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                                "",
                                context!!
                            )

                            savePreferences(
                                CONSTANTS.date_future_booking_str,
                                "",
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.time_slots_future_booking_str,
                                "",
                                context!!
                            )

                            NotificationUtils().showNotification( getString(R.string.app_name),
                                "New Upcoming job created")


                            val request_id = jsonObject.getString("request_id")
                            startActivity(
                                Intent(context, AdvancedPaymentActivity::class.java)
                                    .putExtra("request_id", request_id)

                            )
                            activity!!.finish()
                        } else {
                            Toast.makeText(
                                context,
                                "" + jsonStatus.getString("message"),
                                Toast.LENGTH_SHORT
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
                    headers["registration_id"] = RegistrationID!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }
}