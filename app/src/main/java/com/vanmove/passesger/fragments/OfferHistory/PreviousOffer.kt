package com.vanmove.passesger.fragments.OfferHistory

import android.content.Intent
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
import com.vanmove.passesger.activities.MoveInvoice
import com.vanmove.passesger.adapters.All_Offers_Adaptor_Completed
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.AllOffers
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.fragment_previous_offer.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PreviousOffer : Fragment(R.layout.fragment_previous_offer) {
    private var RegistrationID: String? = null
    private var passenger_id: String? = null

    var All_Offers_Completed = ArrayList<AllOffers>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id = getPreferences(CONSTANTS.passenger_id, context!!)


        upComingBookings()

    }


    fun upComingBookings() {
        view!!.progress_bar.visible()


        val url = Utils.get_passenger_previous_offer
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: Map<String?, String?> =
            HashMap()
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    val result = jsonObject.toString()
                    All_Offers_Completed.clear()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val first = JSONObject(result)
                            val JA = first.getJSONArray("bookings")
                            if (JA.length() > 0) {
                                view!!.tv_message_no_booking!!.visibility = View.GONE
                            } else {
                                view!!.tv_message_no_booking!!.visibility = View.VISIBLE
                            }
                            try {
                                for (i in 0 until JA.length()) {
                                    val jsonData = JA.getJSONObject(i)
                                    val req_id = jsonData.getString("id")
                                    val timestamp =
                                        jsonData.getString("timestamp")
                                    val pickup = jsonData.getString("pickup")
                                    val destination =
                                        jsonData.getString("destination")
                                    val offered_price =
                                        jsonData.getString("offered_price")
                                    val is_status =
                                        jsonData.getString("is_status")
                                    val offers = AllOffers()
                                    offers.pick_up_name = pickup
                                    offers.drop_off_name = destination
                                    offers.timse_stamp = timestamp
                                    offers.offered_price = offered_price
                                    offers.offer_status = is_status
                                    offers.request_id = req_id
                                    All_Offers_Completed.add(offers)
                                }

                                view!!.lv_upcoming_booked!!.adapter = All_Offers_Adaptor_Completed(
                                    context!!,
                                    All_Offers_Completed, object : OnItemClickRecycler {
                                        override fun onClickRecycler(
                                            view: View?,
                                            position: Int
                                        ) {
                                            startActivity(
                                                Intent(activity, MoveInvoice::class.java)
                                                    .putExtra(
                                                        "reqID",
                                                        All_Offers_Completed[position].request_id
                                                    )
                                            )
                                        }
                                    })
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
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
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                    view!!.progress_bar.gone()

                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    view!!.progress_bar.gone()
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
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