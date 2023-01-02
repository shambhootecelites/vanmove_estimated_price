package com.vanmove.passesger.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.HomeFrragment
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.profile.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class UpdateProfile : Fragment(R.layout.profile),
        View.OnClickListener {

    private var registrationID: String? = null
    private var passenger_id: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linkViews()
    }


    private fun linkViews() {

        btn_update.setOnClickListener(this)
        registrationID =
                getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
                getPreferences(CONSTANTS.passenger_id, context!!)
        val email =
                getPreferences(CONSTANTS.username, context!!)
        val mobile =
                getPreferences(CONSTANTS.mobile_passenger, context!!)
        val first_name =
                getPreferences(CONSTANTS.first_name, context!!)
        val last_name =
                getPreferences(CONSTANTS.last_name, context!!)
        val country_code =
                getPreferences(CONSTANTS.country_code, context!!)
        tv_email.setText(email)
        tv_name.setText("$first_name $last_name")
        tv_mobile.setText(country_code + mobile)
    }

    override fun onResume() {
        super.onResume()
        goBackMethod()
    }

    private fun goBackMethod() {
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                val transaction =
                        fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_fragments, HomeFrragment())
                transaction.commit()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_update) {
            val str_old_pass = et_old_pass!!.text.toString()
            val str_new_pass = et_newpass!!.text.toString()
            val str_confirm_pass = et_confirmpass!!.text.toString()
            if (TextUtils.isEmpty(str_old_pass)) {
                showToast("Please enter your old password")
            } else if (TextUtils.isEmpty(str_new_pass)) {
                showToast("Please enter new password")
            } else if (str_old_pass == str_new_pass) {
                showToast("You cannot use old password as new password")
            } else if (str_new_pass.length < 5) {
                showToast("Password should be at least 5 characters long")
            } else if (str_new_pass != str_confirm_pass) {
                showToast("Password does not match")
            } else {
                changePassword(str_new_pass, str_old_pass, registrationID, passenger_id)
            }
        }
    }

    private fun changePassword(
            password: String,
            password_old: String,
            RegistrationID_: String?,
            user_id: String?
    ) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please Wait")
        progressDialog.setTitle("Wait...")
        progressDialog.show()
        val url = Utils.change_password
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
                HashMap()
        postParam["type"] = Utils.userType
        postParam["password"] = password
        postParam["password_old"] = password_old
        val jsonObjReq: JsonObjectRequest =
                object : JsonObjectRequest(
                        Method.POST,
                        url, JSONObject(postParam as Map<*, *>),
                        Response.Listener { jsonObject ->
                            Log.d("TAG", jsonObject.toString())
                            progressDialog.dismiss()
                            try {
                                val jsonStatus = jsonObject.getJSONObject("status")
                                if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                                    Toast.makeText(
                                            context,
                                            jsonStatus.getString("message"),
                                            Toast.LENGTH_LONG
                                    ).show()
                                    progressDialog.dismiss()
                                    signOutDriver(passenger_id, registrationID)
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                            context,
                                            jsonStatus.getString("message"),
                                            Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                progressDialog.dismiss()
                            }
                        }
                        , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    Toast.makeText(context, "Error$error", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                                HashMap<String, String>()
                        headers["Content-Type"] = "application/json; charset=utf-8"
                        headers["user_id"] = user_id!!
                        headers["registration_id"] = RegistrationID_!!
                        return headers
                    }
                }
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    private fun signOutDriver(
        passenger_id: String?,
        registration_id: String?
    ) {

        // Delete FCM Token
        val deferred = GlobalScope.async {
            FirebaseInstanceId.getInstance().deleteInstanceId()
        }

        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["type"] = Utils.userType
        val progress_dialog = ProgressDialog.show(context, "", "Loading...", true)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(activity)
        }
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.Logout_Url,
            JSONObject(postParam as Map<*, *>),
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code") == "1000") {
                        progress_dialog.dismiss()
                        Utils.savePreferences(
                            CONSTANTS.login,
                            "false",
                            activity!!
                        )
                        val intent = Intent(activity, Ask::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        activity!!.finish()
                    } else {
                        Utils.savePreferences(
                            CONSTANTS.login,
                            "false",
                            activity!!
                        )
                        val intent = Intent(activity, Ask::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        activity!!.finish()
                        progress_dialog.dismiss()
                        Toast.makeText(
                            activity,
                            jsonStatus.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: JSONException) {
                    progress_dialog.dismiss()
                }
            }
            , Response.ErrorListener { error ->
                progress_dialog.dismiss()
                VolleyLog.d("hantash_check", "Error: " + error.message)
                Toast.makeText(activity, error.message + "", Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["user_id"] = passenger_id!!
                headers["registration_id"] = registration_id!!
                return headers
            }
        }
        Volley.newRequestQueue(activity).add(jsonObjReq)
    }
}