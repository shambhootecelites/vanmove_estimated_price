package com.vanmove.passesger.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.kaopiz.kprogresshud.KProgressHUD
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnClickOneButtonAlertDialog
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DialogUtils
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.showToast
import kotlinx.android.synthetic.main.forget_pwd.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ForgotActivity : AppCompatActivity(), OnClickOneButtonAlertDialog,
    View.OnClickListener, TextWatcher {
    var context: Context? = null
    private var registration_id: String? = null
    var send_otp = ""
    private var progressDialog: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_pwd)
        progressDialog = DialogUtils.showProgressDialog(this, cancelable = false)

        initializations()
        context = this@ForgotActivity
        val preferences = getSharedPreferences(
            CONSTANTS.SHARED_PREFERENCE_APP,
            Context.MODE_PRIVATE
        )
        registration_id = preferences.getString(CONSTANTS.PREFERENCE_EXTRA_REGISTRATION_ID, "")
    }

    private fun initializations() {

        iv_back.setOnClickListener { finish() }
        cv_enter_new_password.setVisibility(View.GONE)
        btn_enter_mobile_no.setOnClickListener(this)
        btn_resend_code.setOnClickListener(this)
        btn_enter_new_password.setOnClickListener(this)
        submit_code.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_enter_mobile_no -> {
                val mobile = et_mobile_number.text.toString()

                if (mobile.isEmpty()) {
                    showAlertMessage(
                        this,
                        "Please enter your mobile number"
                    )
                } else if (mobile[0] != '0') {
                    showAlertMessage(
                        this,
                        "Mobile number should start from zero"
                    )

                } else if (mobile.length != 11) {
                    showAlertMessage(
                        this,
                        "Please enter your valid mobile number"
                    )
                }
                else {
                    runAPICheckMobileNumber(
                        registration_id, "",
                        et_mobile_number!!.text.toString()
                    )
                }
            }
            R.id.btn_resend_code -> runAPIRequestOTP(registration_id)
            R.id.btn_enter_new_password -> {
                val new_password: String = et_new_password!!.text.toString()
                val confirmPassword: String = et_confirm_password!!.text.toString()

                if (new_password.length <= 0) {
                    Toast.makeText(context, "Please enter new password", Toast.LENGTH_LONG).show()
                }
                else if (new_password.length < 5){
                    Toast.makeText(context, "Password should be at least 5 characters long", Toast.LENGTH_LONG).show()
                }else if (confirmPassword.length <= 0) {
                    Toast.makeText(context, "Please enter confirm password", Toast.LENGTH_LONG).show()
                }
                else if (new_password == confirmPassword) {
                    runAPIEnterNewPassword(et_new_password!!.text.toString(), registration_id)
                }else {
                    showToast("Password does not match")
                }
            }
            R.id.submit_code -> runAPICheckOTP(pinView!!.pinResults)
        }
    }


    private fun runAPICheckMobileNumber(
        reg_id: String?,
        country_code: String,
        mobile_number: String
    ) {
        progressDialog!!.show()
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = mobile_number
        postParam["country_code"] = country_code
        postParam["device"] = "Android"
        postParam["type"] = "passenger"
        postParam["registration_id"] = reg_id
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.mobile_check,
            JSONObject(postParam as Map<*, *>),
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")

                    when (jsonStatus.getString("code")) {
                        "3001" -> {
                            cv_mobile_no!!.visibility = View.GONE
                            cv_enter_otp!!.visibility = View.VISIBLE
                            tv_passenger_no.setText(
                                "Enter the 4-digit code sent to you in " + et_mobile_number!!.text.toString()
                            )
                            runAPIRequestOTP(registration_id)
                        }
                        else-> {

                            showToast(jsonStatus.getString("message"))
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                progressDialog!!.dismiss()
            }
            , Response.ErrorListener {
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
        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    private fun runAPIRequestOTP(request_id: String?) {
        progressDialog!!.show()
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = et_mobile_number!!.text.toString()
        postParam["country_code"] = ""
        postParam["device"] = "android"
        postParam["type"] = "" + Utils.userType
        postParam["registration_id"] = request_id
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.sendOtpUrl,
            JSONObject(postParam as Map<*, *>),
            Response.Listener { jsonObject ->

                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                        btn_resend_code!!.isEnabled = false
                        object : CountDownTimer(60000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                btn_resend_code!!.text =
                                    "Resend Verification Code in " + millisUntilFinished / 1000 + " second(s)"
                            }

                            override fun onFinish() {
                                btn_resend_code!!.isEnabled = true
                                btn_resend_code!!.text = "Resend Verification Code"
                            }
                        }.start()
                        send_otp = jsonObject.getString("data")
                    } else {
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                progressDialog!!.dismiss()
            }
            , Response.ErrorListener {
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
        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    private fun runAPICheckOTP(received_otp: String) {
        if (received_otp.isEmpty()) {
            Toast.makeText(context, "Enter otp code", Toast.LENGTH_SHORT).show()
        } else if (received_otp == send_otp) {
            cv_enter_otp!!.visibility = View.GONE
            cv_enter_new_password!!.visibility = View.VISIBLE
        } else {
            Toast.makeText(context, "Otp not matched", Toast.LENGTH_SHORT).show()
        }
    }

    private fun runAPIEnterNewPassword(
        new_password: String,
        registration_id: String?
    ) {
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["mobile"] = et_mobile_number!!.text.toString()
        postParam["country_code"] = ""
        postParam["device"] = "android"
        postParam["type"] = "passenger"
        postParam["password"] = new_password
        postParam["registration_id"] = registration_id
        progressDialog!!.show()
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.forgot_password,
            JSONObject(postParam as Map<*, *>),
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                        Toast.makeText(
                            context,
                            "Your password has been reset",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()
                    } else {
                        Toast.makeText(
                            context,
                            jsonStatus.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                }
                progressDialog!!.dismiss()
            }
            , Response.ErrorListener {
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
        jsonObjReq.retryPolicy = DefaultRetryPolicy(
            50000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialog: DialogInterface?
    ) {
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}