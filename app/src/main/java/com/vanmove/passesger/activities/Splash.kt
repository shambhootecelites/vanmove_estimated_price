package com.vanmove.passesger.activities

import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanmove.passesger.R
import com.vanmove.passesger.model.DeminsionModel
import com.vanmove.passesger.model.GetServices
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*

class Splash : AppCompatActivity() {


    private var RegistrationID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_loading_new)
        Clear()
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, this)

        checkForGPSStatus()


        val deferred = GlobalScope.async { services }

    }

    private fun Clear() {

        savePreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            "",
            this
        )
        savePreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            "",
            this
        )
        savePreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            "",
            this
        )
        savePreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
            "",
            this
        )
        savePreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
            "",
            this
        )
        savePreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
            "",
            this
        )

        Utils.getInventoryImagesList.clear()
        Utils.deminsionList.clear()
        Utils.deminsionList.add(
            DeminsionModel(
                "",
                "", "", "", "", ""
            )
        )
        savePreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
            "",
            this
        )
        savePreferences(
            CONSTANTS.date_future_booking_str,
            "",
            this
        )
        savePreferences(CONSTANTS.time_slots_future_booking_str,
            "",
            this
        )
        savePreferences(CONSTANTS.helpers, "0", this)
    }

    fun reduceViewSize() {


        Handler().postDelayed({
            val login = getPreferences(CONSTANTS.login, this)
            if (login.equals("true", ignoreCase = true)) {
                val passenger_id = getPreferences(CONSTANTS.passenger_id, this)
                val RegistrationID = getPreferences(CONSTANTS.RegistrationID, this)
                CONSTANTS.headers.clear()
                CONSTANTS.headers["Content-Type"] = "application/json; charset=utf-8"
                CONSTANTS.headers["passenger_id"] = passenger_id!!
                CONSTANTS.headers["registration_id"] = RegistrationID!!
                CONSTANTS.headers["user_id"] = passenger_id
                val intent = Intent(this, MainScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                val intent = Intent(this, Ask::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            finish()

        }, 3000)
    }

    private fun checkForGPSStatus() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = CONSTANTS.UPDATE_INTERVAL.toLong()
        mLocationRequest.fastestInterval = CONSTANTS.FATEST_INTERVAL.toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val client = LocationServices.getSettingsClient(this)
        val task =
            client.checkLocationSettings(builder.build())
        task.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
                reduceViewSize()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable =
                            exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@Splash,
                            REQUEST_GPS_SETTINGS
                        )
                    } catch (ignored: SendIntentException) {
                    } catch (ignored: ClassCastException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GPS_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    reduceViewSize()
                }
                Activity.RESULT_CANCELED -> {
                    checkForGPSStatus()
                    Toast.makeText(this@Splash, getString(R.string.gps_msg), Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private val services: Unit
        get() {
            val url = Utils.get_services
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                updateTLS(this)
            }
            val postParam: Map<String?, String?> =
                HashMap()
            val jsonObjReq: JsonObjectRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url,
                    JSONObject(postParam),
                    Response.Listener { jsonObject ->
                        Log.d("TAG", jsonObject.toString())
                        val result = jsonObject.toString()
                        Utils.GetServicesList.clear()
                        try {
                            val jsonStatus = jsonObject.getJSONObject("status")
                            if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                                val respone = JSONObject(result)
                                val JA = respone.getJSONArray("services")


                                val listType: Type =
                                    object : TypeToken<List<GetServices?>?>() {}.getType()

                                Utils.GetServicesList = Gson().fromJson(JA.toString(), listType)


                            } else {
                                Toast.makeText(
                                    this,
                                    jsonStatus.getString("message"),
                                    Toast.LENGTH_LONG
                                ).show()
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
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["Content-Type"] = "application/json; charset=utf-8"
                        headers["registration_id"] = RegistrationID!!
                        return headers
                    }
                }
            Volley.newRequestQueue(this).add(jsonObjReq)
        }

    companion object {
        const val REQUEST_GPS_SETTINGS = 107
    }
}


