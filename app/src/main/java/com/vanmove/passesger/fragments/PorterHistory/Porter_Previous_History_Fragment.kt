package com.vanmove.passesger.fragments.PorterHistory

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.Porters_Previous_Adpater
import com.vanmove.passesger.model.Porters_History
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.fragment_previos_porter.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Porter_Previous_History_Fragment : Fragment(R.layout.fragment_previos_porter) {
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    private val GetPorterHistoryList =
        ArrayList<Porters_History>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)

        portersHistory
    }


    private val portersHistory: Unit
        get() {
            view!!.progress_bar.visible()

            val url = Utils.porter_get_users_bookings
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                updateTLS(context)
            }
            val postParam: MutableMap<String?, String?> =
                HashMap()
            postParam["user_type"] = "passenger"
            val jsonObjReq: JsonObjectRequest =
                object : JsonObjectRequest(
                    Method.POST,
                    url, JSONObject(postParam as Map<*, *>),
                    Response.Listener { jsonObject ->
                        Log.d("TAG", jsonObject.toString())
                        val result = jsonObject.toString()
                        GetPorterHistoryList.clear()
                        try {
                            val jsonStatus = jsonObject.getJSONObject("status")
                            if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                                val first = JSONObject(result)
                                val JA = first.getJSONArray("bookings")
                                try {
                                    GetPorterHistoryList.clear()
                                    for (i in 0 until JA.length()) {
                                        val jsonObject_inner = JA.getJSONObject(i)
                                        val request_id =
                                            jsonObject_inner.getString("id")
                                        val pick_up_name =
                                            jsonObject_inner.getString("pickup")
                                        val request_trip_end_time =
                                            jsonObject_inner.getString("request_trip_end_time")
                                        val rate_grand_total =
                                            jsonObject_inner.getString("rate_grand_total")
                                        val porters_history = Porters_History(
                                            request_id,
                                            pick_up_name,
                                            request_trip_end_time,
                                            rate_grand_total
                                        )
                                        GetPorterHistoryList.add(porters_history)
                                    }
                                    if (GetPorterHistoryList.size == 0) {
                                        view!!.tv_message_no_booking!!.visibility = View.VISIBLE
                                    } else {
                                        view!!.tv_message_no_booking!!.visibility = View.GONE
                                    }

                                    view!!.lv_upcoming_booked!!.adapter =  Porters_Previous_Adpater(context!!, GetPorterHistoryList)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    jsonStatus.getString("message"),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        view!!.progress_bar.gone()

                    }
                    , Response.ErrorListener { error ->
                        VolleyLog.d("TAG", "Error: " + error.message)
                        view!!.progress_bar.gone()
                    }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers =
                            HashMap<String, String>()
                        headers["Content-Type"] = "application/json; charset=utf-8"
                        headers["registration_id"] = RegistrationID!!
                        headers["user_id"] = passenger_id!!
                        return headers
                    }
                }
            MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
        }
}