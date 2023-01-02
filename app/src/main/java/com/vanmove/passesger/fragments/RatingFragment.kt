package com.vanmove.passesger.fragments

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.showToastTest
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.rating_fragment.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class RatingFragment : Fragment(R.layout.rating_fragment),
    View.OnClickListener {

    private var str_rating: String? = null
    private var str_review: String? = null
    private var progressDialog: ProgressDialog? = null
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    private var reqID: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        reqID = getPreferences(CONSTANTS.REQUEST_ID, context!!)
        linkViews()
    }


    private fun linkViews() {

        view!!.tv_driver_name!!.text = "" + getPreferences(
            CONSTANTS.driver_first_name,
            context!!
        )

        view!!.iv_fb!!.setOnClickListener(this)
        view!!.iv_twitter!!.setOnClickListener(this)
        view!!.iv_email!!.setOnClickListener(this)
        view!!.btn_submit!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_fb -> showToastTest(context, "share via fb")
            R.id.iv_twitter -> showToastTest(
                context,
                "share via twitter"
            )
            R.id.iv_email -> showToastTest(
                context,
                "share via email"
            )
            R.id.btn_submit -> submitRating()
        }
    }

    private fun submitRating() {
        str_review = view!!.et_review!!.text.toString()
        str_rating = "" + view!!.ratingBar!!.rating
        if (TextUtils.isEmpty(str_rating)) {
            showToast("please give rating")
        } else if (TextUtils.isEmpty(str_review)) {
            showToast("Feedback cannot be empty")
        } else {
            view!!.btn_submit!!.isEnabled = false
            updaterating()
        }
    }

    private fun updaterating() {
        val driver_id =
            getPreferences(CONSTANTS.driver_id, context!!)
        progressDialog = ProgressDialog.show(
            context, "",
            "Wait please....", true
        )
        val url = Utils.update_request_rating_passenger
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["driver_id"] = driver_id
        postParam["request_id"] = reqID
        postParam["rating"] = str_rating
        postParam["feedback"] = str_review
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    val result = jsonObject.toString()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            view!!.btn_submit!!.isEnabled = true
                            val newFragment = HomeFrragment()
                            val transaction =
                                fragmentManager!!.beginTransaction()
                            transaction.replace(R.id.container_fragments, newFragment)
                            transaction.commitAllowingStateLoss()
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                context,
                                "Thank you for your feedback.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            progressDialog!!.dismiss()
                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progressDialog!!.dismiss()
                        view!!.btn_submit!!.isEnabled = true
                    }
                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    progressDialog!!.dismiss()
                    view!!.btn_submit!!.isEnabled = true
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["registration_id"] = RegistrationID!!
                    headers["passenger_id"] = passenger_id!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }
}