package com.vanmove.passesger.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
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
import com.vanmove.passesger.utils.Utils.inPhoneNumberValidate
import kotlinx.android.synthetic.main.sign_up_layout.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUp : AppCompatActivity(), View.OnClickListener {


    private var gps: GPSTracker? = null
    private var latitude = 0.0
    private var longitude = 0.0
    var registration_id: String? = null
    val MY_PERMISSION_REQUEST_CODE = 7171
    val REQUEST_GPS_SETTINGS = 107
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.sign_up_layout)
        linkViews()
        val preferences = getSharedPreferences(
            CONSTANTS.SHARED_PREFERENCE_APP,
            Context.MODE_PRIVATE
        )
        registration_id = preferences.getString(CONSTANTS.PREFERENCE_EXTRA_REGISTRATION_ID, "")
        gps = GPSTracker(this@SignUp, this)
        if (gps!!.canGetLocation()) {
            latitude = gps!!.latitude
            longitude = gps!!.longitude
        }
    }

    private fun linkViews() {

        iv_back.setOnClickListener(this)
        Horizontal_Progress.setVisibility(View.GONE)
        btn_continue.setOnClickListener(this)
        privacy_policy.setOnClickListener(this)
        term_condition.setOnClickListener(this)


    }


    override fun onClick(v: View) {

        val pattern: Pattern = Pattern.compile("[a-zA-Z]*")


        val firstName = et_first_name!!.text.toString()
        val lastName = et_last_name!!.text.toString()
        val matcherFirstName: Matcher = pattern.matcher(firstName)
        val matcherLastName: Matcher = pattern.matcher(lastName)


        when (v.id) {
            R.id.btn_continue -> if (firstName.isEmpty()) {
                Snackbar.make(contextView!!, "Please enter your first name", Snackbar.LENGTH_LONG)
                    .show()
            }else if(!matcherFirstName.matches()){
                Snackbar.make(contextView!!, "No special characters and numbers are allowed in the first name", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (lastName.isEmpty()) {
                Snackbar.make(contextView!!, "Please enter your last name", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if(!matcherLastName.matches()){
                Snackbar.make(contextView!!, "No special characters and numbers are allowed in the last name", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (et_mobile_number!!.text.toString().isEmpty()) {
                Snackbar.make(
                    contextView!!,
                    "Please enter your mobile number",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            } else if (et_mobile_number!!.text.toString()[0] != '0') {
                Snackbar.make(
                    contextView!!,
                    "Mobile number should start from zero",
                    Snackbar.LENGTH_LONG
                ).show()
            }
            else if (et_mobile_number!!.text.toString().length != 11) {
                Snackbar.make(
                    contextView!!,
                    "Please enter your valid mobile number",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }

            else if (!inPhoneNumberValidate(et_mobile_number.getText().toString())) {
                Snackbar.make(
                    contextView!!,
                    "Please enter your valid mobile number",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }

            else if (et_email_field!!.text.toString().isEmpty()) {
                Snackbar.make(
                    contextView!!,
                    "Please enter your email address",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
            else if (!validateEmaillId(et_email_field!!.text.toString())) {
                Snackbar.make(contextView!!, "Please enter your valid email", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (et_password_field!!.text.toString().isEmpty()) {
                Snackbar.make(contextView!!, "Please enter your password", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (et_password_field!!.text.toString().length < 5) {
                Snackbar.make(
                    contextView!!,
                    "Please enter at least 5 characters password",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }

            else if (!isValidPasswordFormat(et_password_field!!.text.toString())) {
                Snackbar.make(
                    contextView!!,
                    "The password must have a special, uppercase, lowercase & a numeric character",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
          /*  else if (et_password_field!!.text.toString().length < 5) {
                Snackbar.make(
                    contextView!!,
                    "Please enter at least 5 characters password",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
*/
            else if (et_password_field!!.text.toString().isEmpty()) {
                Snackbar.make(contextView!!, "Please enter Confirm password", Snackbar.LENGTH_LONG)
                    .show()
            }
            else if (!confirm_pwd!!.text.toString().equals(et_password_field!!.text.toString())) {
                Snackbar.make(contextView!!, "Confirm password is wrong", Snackbar.LENGTH_LONG)
                    .show()
            }


            else {
                checkForAppPermissionsAndGPSStatus()
            }
            R.id.privacy_policy -> Open_Chrome(CONSTANTS.privacy)
            R.id.term_condition -> Open_Chrome(CONSTANTS.term_condition)
            R.id.iv_back -> finish()
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
                Toast.makeText(this@SignUp, getString(R.string.gps_msg), Toast.LENGTH_LONG).show()
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
                    registerPassenger(
                        et_password_field!!.text.toString(),
                        et_mobile_number!!.text.toString(),
                        registration_id,
                        et_first_name!!.text.toString(),
                        et_last_name!!.text.toString(),
                        et_email_field!!.text.toString()
                    )
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(this@SignUp, getString(R.string.gps_msg), Toast.LENGTH_LONG)
                        .show()
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
                registerPassenger(
                    et_password_field!!.text.toString(),
                    et_mobile_number!!.text.toString(),
                    registration_id,
                    et_first_name!!.text.toString(),
                    et_last_name!!.text.toString(),
                    et_email_field!!.text.toString()
                )
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable =
                            exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this@SignUp,
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

    private fun registerPassenger(
        str_password: String, str_phone: String,
        RegistrationID: String?,
        fname: String, lname: String,
        str_email: String
    ) {
        Horizontal_Progress!!.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Utils.updateTLS(this)
        }
        val url = Utils.register_passengerUrl
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = str_phone
        postParam["country_code"] = ""
        postParam["password"] = str_password
        postParam["email"] = str_email
        postParam["payment_method"] = "CASH"
        postParam["registration_id"] = RegistrationID
        postParam["first_name"] = fname.trim()
        postParam["last_name"] = lname.trim()
        postParam["device"] = "android"
        postParam["stripe_id"] = ""
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
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val jsonData = jsonObject.getJSONObject("data")
                            Utils.savePreferences(
                                CONSTANTS.card_number,
                                jsonData.getString("card_number"),
                                this
                            )
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
                                CONSTANTS.mobile_passenger,
                                jsonData.getString("mobile"),
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
                                CONSTANTS.stripe_customer_id,
                                jsonData.getString("stripe_id"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.PAY_BY,
                                jsonData.getString("payment_method"),
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.login,
                                "false",
                                this
                            )
                            Utils.savePreferences(
                                CONSTANTS.str_password,
                                et_password_field!!.text.toString(),
                                this
                            )
                            val intent = Intent(this, PinEntryFragment::class.java)
                            intent.putExtra("str_phone", str_phone)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                            Horizontal_Progress!!.visibility = View.GONE
                        } else {
                            Horizontal_Progress!!.visibility = View.GONE
                            Snackbar.make(
                                contextView!!,
                                jsonStatus.getString("message"),
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Horizontal_Progress!!.visibility = View.GONE
                        Snackbar.make(contextView!!, e.message!!, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    Toast.makeText(this, "Error$error", Toast.LENGTH_SHORT).show()
                    Horizontal_Progress!!.visibility = View.GONE
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
        MyRequestQueue.getRequestInstance(this@SignUp)!!.addRequest(jsonObjReq)
    }

    private fun Open_Chrome(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            startActivity(intent)
        } catch (ex: Exception) {
            Snackbar.make(contextView!!, ex.message!!, Snackbar.LENGTH_LONG)
                .show()
        }
    }


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
        fun isValidEmail(target: String?): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun validateEmaillId(emailId: String): Boolean {
            return Pattern.compile(
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
            ).matcher(emailId).matches()
        }
      /*  fun isValidPasswordFormat(password: String): Boolean {
            val passwordREGEX = Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                   // ".{8,}" +               //at least 8 characters
                    "$");
            return passwordREGEX.matcher(password).matches()
        }*/
        fun isValidPasswordFormat(password: String): Boolean {
            val passwordREGEX = Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{5,}" +               //at least 8 characters
                    "$");
            return passwordREGEX.matcher(password).matches()
        }
    }
}