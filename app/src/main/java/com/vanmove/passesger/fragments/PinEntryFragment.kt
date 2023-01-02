package com.vanmove.passesger.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.kaopiz.kprogresshud.KProgressHUD
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DialogUtils
import com.vanmove.passesger.utils.GPSTracker
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.fragment_pin_entry.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PinEntryFragment : AppCompatActivity(), View.OnClickListener {

    private var str_phone: String? = null
    private var RegistrationID: String? = null
    private val country_code: String? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var gps: GPSTracker? = null
    private var progressDialog: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_pin_entry)
        linkViews()

        iv_back.setOnClickListener{
            finish()
        }
    }

    private fun linkViews() {
        progressDialog = DialogUtils.showProgressDialog(this, cancelable = false)

        str_phone = intent.getStringExtra("str_phone")
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, this)
        text_resend_code.setOnClickListener(this)
        iv_btn_next.setOnClickListener(this)
        tv_passenger_no.setText("Enter the 4-digit code sent to you in $str_phone")
        getOtpMethod(str_phone, RegistrationID, country_code)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_btn_next -> {
                val str_pin = pin_view!!.pinResults
                if (TextUtils.isEmpty(str_pin)) {
                    showToast("Please enter pin code")
                } else if (!str_otp.equals(str_pin, ignoreCase = true)) {
                    showToast("OTP code does not match")
                } else {
                    //Active User
                    active_user(str_phone)
                }
            }
            R.id.text_resend_code -> getOtpMethod(str_phone, RegistrationID, country_code)
        }
    }

    private fun makeJsonObjReqLogin(
        str_phone: String?,
        str_password: String?,
        RegistrationID_: String?,
        country_code: String
    ) {
        progressDialog!!.show()
        val url = Utils.loginUrl
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(this)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = str_phone
        postParam["password"] = str_password
        postParam["registration_id"] = RegistrationID_
        postParam["type"] = "passenger"
        postParam["device"] = "android"
        postParam["country_code"] = country_code
        gps = GPSTracker(this, this)
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
                    Log.d("TAG","response:::"+ jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val jsonData = jsonObject.getJSONObject("data")
                            savePreferences(
                                CONSTANTS.passenger_id,
                                jsonData.getString("id"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.username,
                                jsonData.getString("email"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.mobile_passenger,
                                jsonData.getString("mobile"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.country_code,
                                jsonData.getString("country_code"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.first_name,
                                jsonData.getString("first_name"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.last_name,
                                jsonData.getString("last_name"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.card_number,
                                jsonData.getString("card_number"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.mobile2,
                                jsonData.getString("mobile2"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.picture_user,
                                jsonData.getString("picture"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.login,
                                "true",
                                this
                            )
                            savePreferences(
                                CONSTANTS.stripe_customer_id,
                                jsonData.getString("stripe_id"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.PAY_BY,
                                jsonData.getString("payment_method"),
                                this
                            )
                            savePreferences(
                                CONSTANTS.reqID,
                                "",
                                this
                            )
                            startActivity(
                                Intent(this, MainScreenActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
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
                    progressDialog!!.dismiss()
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
        MyRequestQueue.getRequestInstance(this)!!.addRequest(jsonObjReq)
    }

    private fun active_user(str_phone: String?) {
        progressDialog!!.show()
        val url = Utils.active_user
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(this)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = str_phone
        postParam["type"] = "passenger"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG","response:::"+ jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val str_password =
                                getPreferences(
                                    CONSTANTS.str_password,
                                    this
                                )
                            makeJsonObjReqLogin(str_phone, str_password, RegistrationID, "")
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
                    progressDialog!!.dismiss()
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
        MyRequestQueue.getRequestInstance(this)!!.addRequest(jsonObjReq)
    }


    public override fun onPause() {
        super.onPause()
    }

    private fun getOtpMethod(
        str_phone: String?,
        RegistrationID_: String?,
        country_code: String?
    ) {
        progressDialog!!.show()
        val url = Utils.sendOtpUrl
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(this)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = str_phone
        postParam["country_code"] = ""
        postParam["registration_id"] = RegistrationID_
        postParam["type"] = "" + Utils.userType
        postParam["device"] = "android"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG","response:::"+ jsonObject.toString())

                    text_resend_code!!.isEnabled = false
                    object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            text_resend_code!!.text =
                                "Resend verification code in " + millisUntilFinished / 1000 + " second(s)"
                            //here you can have your logic to set text to edittext
                        }

                        override fun onFinish() {
                            text_resend_code!!.isEnabled = true
                            text_resend_code!!.text = "Resend verification code"
                        }
                    }.start()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            str_otp = jsonObject.getString("data")
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

                    progressDialog!!.dismiss()
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
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(this)!!.addRequest(jsonObjReq)
    }

    companion object {
        var str_otp: String? = null
    }
}