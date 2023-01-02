package com.vanmove.passesger.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.ContactFragment
import com.vanmove.passesger.fragments.PorterNavigation.Circulating_Porters_Request_Fragment
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.GPSTracker
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageThreeButtons
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.showToast
import kotlinx.android.synthetic.main.fragment_send_porter_request.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class Request_Porter_Fragment : Fragment(R.layout.fragment_send_porter_request),
    View.OnClickListener, OnItemClickListener,
    GoogleApiClient.OnConnectionFailedListener, OnClickTwoButtonsAlertDialog,
    OnClickTwoButtonsAlertDialog2 {
    private val PLACE_PICKER_REQUEST = 1

    private var listPopupWindow: ListPopupWindow? = null
    private var porter_count: ArrayList<String>? = null
    private var pickup_address: String? = null
    private var pickup_lat: String? = null
    private var pickup_lon: String? = null
    private var passenger_id: String? = null
    private var RegistrationID: String? = null
    private var special_instructions: String? = null
    private var porter_counts: String? = null
    private var Duration_ = ""
    private var gps: GPSTracker? = null
    private var latitude = 0.0
    private var longitude = 0.0
    var hourly_rate = ""
    var rate_per_minute_fare = ""
    var online_porter_count = ""


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        val helpers =
            getPreferences(CONSTANTS.helpers, context!!)
        var estimated_duartion =
            getPreferences(CONSTANTS.estimated_duartion, context!!)
        pickup_lat = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )
        pickup_lon = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            context!!
        )
        pickup_address = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            context!!
        )
        linkViews()
        view!!.tv_pickup_porter!!.text = pickup_address
        if (!estimated_duartion!!.isEmpty()) {
            if (estimated_duartion == CONSTANTS.estimated_duration_list[1]) {
                estimated_duartion = "1 hour"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[2]) {
                estimated_duartion = "2 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[3]) {
                estimated_duartion = "3 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[4]) {
                estimated_duartion = "4 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[5]) {
                estimated_duartion = "5 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[6]) {
                estimated_duartion = "6 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[7]) {
                estimated_duartion = "7 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[8]) {
                estimated_duartion = "8 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[9]) {
                estimated_duartion = "9 hours"
            } else if (estimated_duartion == CONSTANTS.estimated_duration_list[10]) {
                estimated_duartion = "10 hours"
            }
            view!!.Duration!!.text = estimated_duartion
        }
        view!!.tv_mob_code_selection!!.text = helpers
    }


    private fun linkViews() {

        view!!.tv_pickup_porter.setOnClickListener(this)
        view!!.btn_go_online.setOnClickListener(this)
        view!!.tv_mob_code_selection.setOnClickListener(this)
        view!!.Duration.setOnClickListener(this)
        gps = GPSTracker(activity, context)
        if (gps!!.canGetLocation()) {
            if (latitude <= 0 || longitude <= 0) {
                latitude = gps!!.latitude
                longitude = gps!!.longitude
            }
        } else {
            gps!!.showSettingsAlert()
        }
        porter_value()
    }

    private fun porter_value() {
        val url = Utils.porter_value
        val postParam: Map<String?, Any?> =
            HashMap()
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url,
            JSONObject(postParam),
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                        val porter_value = jsonObject.getJSONObject("porter_value")
                        hourly_rate = porter_value.getString("hourly_rate")
                        rate_per_minute_fare =
                            porter_value.getString("rate_per_minute_fare")
                    } else {
                    }
                } catch (e: JSONException) {
                }
            }
            , Response.ErrorListener { }
        ) {}
        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        MyRequestQueue.getRequestInstance(activity)!!.addRequest(jsonObjReq)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_go_online -> {
                special_instructions = view!!.et_job_detail!!.text.toString()
                porter_counts = view!!.tv_mob_code_selection!!.text.toString()
                pickup_address = view!!.tv_pickup_porter!!.text.toString()
                Duration_ = view!!.Duration!!.text.toString()
                if (TextUtils.isEmpty(pickup_address)) {
                    showToast("Select location first")
                } else if (TextUtils.isEmpty(special_instructions)) {
                    showToast("Please enter job detail")
                } else if (TextUtils.isEmpty(porter_counts)) {
                    showToast(
                        "Please select porters count"
                    )
                } else if (porter_counts == "0") {
                    showToast("Select atleast 1 porter")
                } else if (Duration_.isEmpty()) {
                    showToast("Select Job Duration")
                } else {
                    Duration_ = Duration_.replace(" hours", "")
                    Duration_ = Duration_.replace(" hour", "")
                    val total = hourly_rate.toDouble() * Duration_.toDouble() * porter_counts!!.toInt()
                    var hour = ""
                    hour = if (Duration_.toDouble() == 1.0) {
                        " hour."
                    } else {
                        " hours."
                    }
                    showAlertMessageWithTwoButtons(
                        context,
                        this@Request_Porter_Fragment,
                        "price_confirm",
                        "Alert",
                        """You have requested $porter_counts porter(s) for $Duration_$hour
${CONSTANTS.CURRENCY}$hourly_rate per hour, total pay to porters ${CONSTANTS.CURRENCY}${String.format(
                            "%.2f",
                            total
                        )}. If job goes over $Duration_ hour(s) you will pay each porter extra ${CONSTANTS.CURRENCY}$hourly_rate per hour.""",
                        "OK", "Cancel"
                    )
                }
            }
            R.id.tv_mob_code_selection -> showListPopUpMenuCountryCode()
            R.id.tv_pickup_porter -> {
                if (!Places.isInitialized()) {
                    Places.initialize(
                        context!!,
                        getString(R.string.google_map_api_key)
                    )
                }
                val placeFields3 =
                    Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.LAT_LNG,
                        Place.Field.ADDRESS
                    )
                val intent_work = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, placeFields3
                )
                    .setCountry(CONSTANTS.current_country)
                    .build(context!!)
                startActivityForResult(intent_work, PLACE_PICKER_REQUEST)
            }
            R.id.Duration -> show_estimated_popmenu(v)
        }
    }

    private fun show_estimated_popmenu(view: View) {
        val popup = PopupMenu(context, view)
        val list = CONSTANTS.estimated_duration_list2
        for (title in list) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            view.Duration!!.text = item.title.toString()
            true
        }
        popup.show()
    }

    private fun newBookingRequest(porter_counts: String?) {
        view!!.Horizontal_Progress!!.visibility = View.VISIBLE
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["latitude"] = "" + latitude
        postParam["longitude"] = "" + longitude
        postParam["pickup_latitude"] = pickup_lat
        postParam["pickup_longitude"] = pickup_lon
        postParam["payment_type"] = "CASH"
        postParam["pickup"] = pickup_address
        postParam["special_instructions"] = special_instructions
        postParam["porters_count"] = porter_counts
        postParam["user_type"] = "passenger"
        postParam["duration"] = Duration_

        val headers =
            HashMap<String?, String?>()
        headers["Content-Type"] = "application/json; charset=utf-8"
        headers["user_id"] = passenger_id
        headers["registration_id"] = RegistrationID
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam as Map<*, *>).toString()
        )
        CONSTANTS.mApiService.porter_new_booking_request(body, headers)!!
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: retrofit2.Response<ResponseBody?>
                ) {
                    if (response.body() != null) {
                        try {
                            val respone = response.body()!!.string()
                            try {
                                val jsonStatus = JSONObject(respone)
                                val status = jsonStatus.getJSONObject("status")
                                if (status.getString("code") == "1000") {
                                    val newFragment = Circulating_Porters_Request_Fragment()
                                    val transaction =
                                        fragmentManager!!.beginTransaction()
                                    transaction.replace(R.id.container_fragments, newFragment)
                                    transaction.commitAllowingStateLoss()
                                } else if (status.getString("code") == "1001") {
                                    val request = jsonStatus.getJSONObject("request")
                                    online_porter_count =
                                        request.getString("online_porter_count")
                                        showAlertMessageThreeButtons(
                                            context,
                                            "Alert",
                                            "Sorry there is only $online_porter_count porter(s) available for ASAP. Please contact the office if you need additional porters",
                                            "Contact Office", "Continue with $online_porter_count porter(s)", "Cancel",
                                            "less_porter_online",
                                            this@Request_Porter_Fragment
                                        )

                                } else {
                                    Toast.makeText(
                                        getContext(),
                                        status.getString("message"),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(context, response.raw().message, Toast.LENGTH_SHORT).show()
                    }
                    view!!.Horizontal_Progress!!.visibility = View.GONE
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    view!!.Horizontal_Progress!!.visibility = View.GONE
                    showToast(t.message)
                }
            })
    }

    private fun showListPopUpMenuCountryCode() {
        porter_count = ArrayList()
        for (i in 1..3) {
            porter_count!!.add(i.toString() + "")
        }
        listPopupWindow = ListPopupWindow(context!!)
        listPopupWindow!!.setAdapter(
            ArrayAdapter(
                context!!,
                R.layout.tv_lay,
                porter_count!!
            )
        )
        listPopupWindow!!.anchorView = view!!.tv_mob_code_selection
        listPopupWindow!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        listPopupWindow!!.isModal = true
        listPopupWindow!!.setOnItemClickListener(this)
        listPopupWindow!!.show()
    }

    override fun onItemClick(
        adapterView: AdapterView<*>?,
        view1: View,
        i: Int,
        l: Long
    ) {
        view!!.tv_mob_code_selection!!.text = porter_count!![i] + ""
        listPopupWindow!!.dismiss()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        showToast(connectionResult.errorMessage)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                var place: Place? = null
                place = Autocomplete.getPlaceFromIntent(data!!)
                pickup_lat = place.latLng!!.latitude.toString()
                pickup_lon = place.latLng!!.longitude.toString()
                pickup_address = String.format("%s", place.address)
                view!!.tv_pickup_porter!!.text = pickup_address
            }
        }
    }

    override fun clickPositiveDialogButton(dialog_name: String?) {

        if (dialog_name == "price_confirm") {
            newBookingRequest(porter_counts)
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}
    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "less_porter_online"){
                startActivity(Intent(context, ContactFragment::class.java))
            }
    }

    override fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "less_porter_online"){
            newBookingRequest(online_porter_count)
        }
    }

    override fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {    }
}