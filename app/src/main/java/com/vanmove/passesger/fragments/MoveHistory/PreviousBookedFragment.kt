package com.vanmove.passesger.fragments.MoveHistory

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MoveInvoice
import com.vanmove.passesger.adapters.BookedMovesAdapter
import com.vanmove.passesger.interfaces.OnAlertDialogButtonClicks
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.Booking
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.universal.NetworkConnectivity.isInternetConnectivityAvailable
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.ShowAlertMessage.setOnAlertDialogButtonClicks
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageTwoButtons
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.showToastTest
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.fragment_previous_booked.*
import kotlinx.android.synthetic.main.fragment_previous_booked.view.*
import org.json.JSONException
import java.util.*

class PreviousBookedFragment : Fragment(R.layout.fragment_previous_booked),
    OnAlertDialogButtonClicks, OnItemClickRecycler {

    private var booking_previous_list: ArrayList<Booking>? = null
    private var RegistrationID: String? = null
    private var passenger_id: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        booking_previous_list = ArrayList()

        getDriverPreviousBookings(passenger_id, RegistrationID)

    }


    override fun onDialogPositiveButtonPressed() {
        if (isInternetConnectivityAvailable(activity!!)) {
            getDriverPreviousBookings(passenger_id, RegistrationID)
        } else {
            setOnAlertDialogButtonClicks(this@PreviousBookedFragment)
            showAlertMessageTwoButtons(
                activity, "",
                "No Internet Connection Available!", "Try Again", "cancel"
            )
        }
    }

    override fun onDialogNegativeButtonPressed() {
        tv_message_no_booking!!.visibility = View.VISIBLE
    }

    private fun getDriverPreviousBookings(
        passenger_id: String?,
        registration_id: String?
    ) {

        view!!.progress_bar.visible()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.get_passenger_previous_moves,
            null,
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code") == "1000") {
                        val jsonArray = jsonObject.getJSONArray("bookings")
                        if (jsonArray.length() > 0) {
                            tv_message_no_booking!!.visibility = View.GONE
                        } else {
                            tv_message_no_booking!!.visibility = View.VISIBLE
                        }
                        booking_previous_list!!.clear()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject_inner = jsonArray.getJSONObject(i)
                            val request_id =
                                jsonObject_inner.getString("request_id")
                            val pickup = jsonObject_inner.getString("pickup")
                            val destination =
                                jsonObject_inner.getString("destination")
                            val timestamp =
                                jsonObject_inner.getString("request_trip_start_time")
                            val rate_grand_total =
                                jsonObject_inner.getString("rate_grand_total")
                            val offered_advance =
                                jsonObject_inner.getDouble("offered_advance")
                            val is_future = jsonObject_inner.getInt("is_future")
                            val booking = Booking()
                            booking.request_id = request_id
                            booking.pick_up_name = pickup
                            booking.drop_off_name = destination
                            booking.trip_end_time = timestamp
                            booking.rate_grand_total = rate_grand_total
                            booking.is_future = is_future
                            booking.offered_advance = offered_advance
                            booking_previous_list!!.add(booking)
                        }

                        lv_previous_booked!!.adapter = BookedMovesAdapter(
                            getContext()!!,
                            booking_previous_list!!, this@PreviousBookedFragment
                        )
                    } else {
                        showToastTest(
                            activity,
                            jsonStatus.getString("message")
                        )
                    }
                    view!!.progress_bar.gone()


                } catch (e: JSONException) {
                }
            }
            , Response.ErrorListener {
                view!!.progress_bar.gone()

            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["passenger_id"] = passenger_id!!
                headers["registration_id"] = registration_id!!
                return headers
            }
        }
        MyRequestQueue.getRequestInstance(activity)!!.addRequest(jsonObjReq)
    }


    override fun onClickRecycler(view: View?, position: Int) {
        startActivity(
            Intent(activity, MoveInvoice::class.java)
                .putExtra("reqID", booking_previous_list!![position].request_id)
        )
    }
}