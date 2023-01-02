package com.vanmove.passesger.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.material.snackbar.Snackbar
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.PinEntryFragment
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.GPSTracker
import com.vanmove.passesger.utils.Utils
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_signin.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SignIn : AppCompatActivity(R.layout.fragment_signin), View.OnClickListener,
    OnItemSelectedListener, LocationListener {
    private var wifi = false
    private var mobileDataEnabled = false
    private var progressDialog: ProgressDialog? = null
    private var str_phone: String? = null
    private var str_password: String? = null
    private var RegistrationID: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var gps: GPSTracker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this)

        linkViews()
    }

    private fun linkViews() {
        gps = GPSTracker(this@SignIn, this)
        if (gps!!.canGetLocation()) {
            latitude = gps!!.latitude
            longitude = gps!!.longitude
        }

        iv_back.setOnClickListener(this)
        tv_dont_have_account.setOnClickListener(this)
        tv_forgot_password!!.setOnClickListener(this)
        RegistrationID = Utils.getPreferences(CONSTANTS.RegistrationID, this)
        et_phone.setText(Paper.book().read<String>(CONSTANTS.login_email, ""))
        et_password.setText(Paper.book().read<String>(CONSTANTS.login_pwd, ""))
        btn_login.setOnClickListener {
            signInMethod()
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.tv_forgot_password) {
            val intent = Intent(this, ForgotActivity::class.java)
            startActivity(intent)
        } else if (v.id == R.id.tv_dont_have_account) {
            startActivity(Intent(this, SignUp::class.java))
        } else if (v.id == R.id.iv_back) {
            finish()
        }
    }

    private fun signInMethod() {
        str_phone = et_phone!!.text.toString()
        str_password = et_password!!.text.toString()
        if (TextUtils.isEmpty(str_phone)) {
            Snackbar.make(contextView!!, "Please enter your email", Snackbar.LENGTH_LONG)
                .show()
        } else if (TextUtils.isEmpty(str_password)) {
            Snackbar.make(contextView!!, "Please enter your password", Snackbar.LENGTH_LONG)
                .show()
        } else {
            Register_Passesger()
        }
    }

    private fun Register_Passesger() {
        wifi = Utils.WifiEnable(this)
        mobileDataEnabled = Utils.isMobileDataEnable(this)
        if (wifi || mobileDataEnabled) {
            checkForAppPermissionsAndGPSStatus()
        } else {
            Snackbar.make(contextView!!, "Sorry....! No Internet connection", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun checkForAppPermissionsAndGPSStatus() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), MY_PERMISSION_REQUEST_CODE
            )
        } else {
            checkForGPSStatus()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForGPSStatus()
            } else {
                Toast.makeText(
                    this@SignIn,
                    getString(R.string.gps_msg),
                    Toast.LENGTH_LONG
                ).show()
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
                    makeJsonObjReqLogin(str_phone, str_password, RegistrationID, "")
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(
                        this@SignIn,
                        getString(R.string.gps_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
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
                makeJsonObjReqLogin(str_phone, str_password, RegistrationID, "")
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable =
                            exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@SignIn,
                            REQUEST_GPS_SETTINGS
                        )
                    } catch (e: SendIntentException) {
                    } catch (e: ClassCastException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }
    }

    private fun makeJsonObjReqLogin(
        str_phone: String?,
        str_password: String?,
        RegistrationID_: String?,
        country_code: String
    ) {
        progressDialog!!.setMessage("Please Wait")
        progressDialog!!.setTitle("Wait...")
        progressDialog!!.show()
        val url = Utils.loginUrl
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Utils.updateTLS(this)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = str_phone
        postParam["password"] = str_password
        postParam["registration_id"] = RegistrationID_
        postParam["type"] = "passenger"
        postParam["device"] = "android"
        postParam["country_code"] = country_code
        if (gps!!.canGetLocation()) {
            latitude = gps!!.latitude
            longitude = gps!!.longitude
        }
        postParam["latitude"] = "" + latitude
        postParam["longitude"] = "" + longitude
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    progressDialog!!.dismiss()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val jsonData = jsonObject.getJSONObject("data")
                            Paper.book().write(CONSTANTS.login_email, str_phone)
                            Paper.book().write(CONSTANTS.login_pwd, str_password)
                            Utils.savePreferences(
                                CONSTANTS.passenger_id,
                                jsonData.getString("id"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.username,
                                jsonData.getString("email"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.mobile_passenger,
                                jsonData.getString("mobile"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.country_code,
                                jsonData.getString("country_code"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.first_name,
                                jsonData.getString("first_name"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.last_name,
                                jsonData.getString("last_name"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.card_number,
                                jsonData.getString("card_number"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.mobile2,
                                jsonData.getString("mobile2"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.picture_user,
                                jsonData.getString("picture"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.login,
                                "true",
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.stripe_customer_id,
                                jsonData.getString("stripe_id"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.PAY_BY,
                                jsonData.getString("payment_method"),
                                this
                            )
                            startActivity(
                                Intent(this, MainScreenActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
                            finish()
                            progressDialog!!.dismiss()
                        } else if (jsonStatus.getString("code")
                                .equals("3002", ignoreCase = true)
                        ) {
                            progressDialog!!.dismiss()
                            val jsonData = jsonObject.getJSONObject("data")
                            val str_phone = jsonData.getString("mobile")
                            startActivity(
                                Intent(this@SignIn, PinEntryFragment::class.java)
                                    .putExtra("str_phone", str_phone)
                            )
                        } else {
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                this@SignIn,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progressDialog!!.dismiss()
                    }
                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    Toast.makeText(this, "Error$error", Toast.LENGTH_SHORT).show()
                    progressDialog!!.dismiss()
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["google_token"] = ""
                    headers["fb_token"] = ""
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(this@SignIn)!!.addRequest(jsonObjReq)
    }

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
    }

    override fun onStatusChanged(
        provider: String,
        status: Int,
        extras: Bundle
    ) {
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass.name
                .startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        const val MY_PERMISSION_REQUEST_CODE = 7171
        const val REQUEST_GPS_SETTINGS = 107
    }
}