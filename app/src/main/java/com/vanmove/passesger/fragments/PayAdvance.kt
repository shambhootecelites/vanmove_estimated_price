package com.vanmove.passesger.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.ContactFragment
import com.vanmove.passesger.activities.CreditCardScreen
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.AlertDialogManager
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage_fixed
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageThreeButtons
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.fragment_pay_advance_move2.view.*
import kotlinx.android.synthetic.main.support.*
import kotlinx.android.synthetic.main.thank_you_msg.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PayAdvance : Fragment(R.layout.fragment_pay_advance_move2),
    View.OnClickListener, OnClickTwoButtonsAlertDialog, OnClickTwoButtonsAlertDialog2 {

    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    private var reqID: String? = null
    var alertDialog: AlertDialog? = null
    var stripe_customer_id: String? = null
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        linkViews()
        try {

            RegistrationID =
                getPreferences(CONSTANTS.RegistrationID, context!!)
            passenger_id =
                getPreferences(CONSTANTS.passenger_id, context!!)
            stripe_customer_id = getPreferences(
                CONSTANTS.stripe_customer_id,
                context!!
            )

            reqID = activity!!.intent.getStringExtra("request_id")

            val type = activity!!.intent.getStringExtra("type")
            var total_price = "0"
            if (type == "Fixed") {
                total_price = activity!!.intent.getStringExtra("offeredPrice").toString()

            } else {
                total_price = activity!!.intent.getStringExtra("estimated_payment").toString()

            }


            total_price = total_price.replace(CONSTANTS.CURRENCY, "")
            getView()!!.tv_offered_amount!!.text = CONSTANTS.CURRENCY + String.format(
                "%.2f",
                total_price.toDouble()
            )
            val advanced_amount = total_price.toDouble() * .25
            getView()!!.advanced_amount_tv!!.text = CONSTANTS.CURRENCY + String.format(
                "%.2f",
                advanced_amount
            )
            val tv_remaining_payment_ = total_price.toDouble() - advanced_amount
            getView()!!.tv_remaining_payment!!.text = CONSTANTS.CURRENCY + String.format(
                "%.2f",
                total_price.toDouble()
            )

         /*   getView()!!.advanced_amount_tv2!!.text = CONSTANTS.CURRENCY + String.format(
                "%.2f",
                advanced_amount
            )*/
            getView()!!.advanced_amount_tv2!!.text = CONSTANTS.CURRENCY + String.format(
                "%.2f",
                total_price.toDouble()
            )
            getView()!!.add_card.setOnClickListener {
                if (stripe_customer_id!!.isEmpty()) {
                    startActivity(
                        Intent(
                            activity, CreditCardScreen::class.java
                        )
                    )
                } else {
                    AlertDialogManager.showAlertMessage(context, "Deposit can only be paid with a card.")

                }


            }
            if (stripe_customer_id!!.isEmpty()) {
                getView()!!.add_card.text = "Card - click to pay via card."
            } else {
                getView()!!.add_card.text = "Pay via card"

            }


            if (arguments != null) {
                if (arguments!!.containsKey("is_offer")) {
                    getView()!!.Job_price_layout.gone()
                    getView()!!.Deposit_layout.gone()
                    getView()!!.Balance_layout.gone()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun linkViews() {

        getView()!!.pay_payment.setOnClickListener(this)

        contact_Number.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    ContactFragment::class.java
                )
            )
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.pay_payment) {
            stripe_customer_id = getPreferences(
                CONSTANTS.stripe_customer_id,
                context!!
            )
            if (stripe_customer_id!!.isEmpty()) {
                showAlertMessageThreeButtons(
                    context, "Alert",
                    "PLease note, in order to make booking, you need to add a payment card to your account.",
                    "Add Now", "Call us", "Cancel Booking", "confirm_card",
                    this@PayAdvance
                )
            } else {
                Payment_Offer()
            }
        } else if (id == R.id.Cancel) {
            alertDialog!!.dismiss()
            val intent = Intent(context, MainScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity!!.finish()
        } else if (id == R.id.detail) {
            alertDialog!!.dismiss()
            val intent = Intent(context, MainScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (arguments != null) {
                intent.putExtra(CONSTANTS.move_to_offer_upcoming, CONSTANTS.move_to_offer_upcoming)
            } else {
                intent.putExtra(CONSTANTS.move_to_move_upcoming, CONSTANTS.move_to_move_upcoming)
            }
            startActivity(intent)
            activity!!.finish()
        }
    }

    private fun Payment_Offer() {
        getView()!!.progress!!.visibility = View.VISIBLE
        val url =
            Utils.update_offer_booking_payment_passenger
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = reqID
        postParam["payment"] = "Paid via Stripe"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val last_four_number =
                                getPreferences(
                                    CONSTANTS.card_number,
                                    context!!
                                )
                            val msg =
                                "Thank you, your deposit of " + getView()!!.advanced_amount_tv!!.text
                                    .toString() + " has been " +
                                        "successfully charged to your card ending with " + last_four_number + "."
                            showAlertMessage_fixed(
                                context, msg,
                                "confirm_successfull", this@PayAdvance
                            )
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
                    getView()!!.progress!!.visibility = View.GONE
                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    getView()!!.progress!!.visibility = View.GONE
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

    private fun Show_Msg(msg: String, ref_number_: String?) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setView(R.layout.thank_you_msg)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
        alertDialog!!.msg_1!!.text = msg
        alertDialog!!.ref_number!!.text = "Ref - $ref_number_"
        alertDialog!!.Cancel!!.setOnClickListener(this)
        alertDialog!!.detail!!.setOnClickListener(this)
    }

    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "confirm_successfull") {
            val vehicle_name = activity!!.intent.getStringExtra("vehicle_name")
            val helpers_count = activity!!.intent.getStringExtra("helpers_count")
            val furture_ride_msg =
                "Thank you for requesting a " + vehicle_name + " with " +
                        helpers_count + " helper(s).Your transporter should be in" +
                        " contact to finalise your move details."
            Show_Msg(furture_ride_msg, reqID)
        } else if (dialog_name == "Confirm_Dialog_Cancel") {
            cancelRequest(passenger_id, RegistrationID, reqID)
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}
    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        dialogInterface!!.dismiss()
        startActivity(Intent(context, CreditCardScreen::class.java))
    }

    override fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        dialogInterface!!.dismiss()
        Dial_Number(context!!, CONSTANTS.Support_Number)
    }

    override fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        dialogInterface!!.dismiss()
        Confirm_Dialog_Cancel()
    }

    private fun Confirm_Dialog_Cancel() {
        showAlertMessageWithTwoButtons(
            context, this@PayAdvance,
            "Confirm_Dialog_Cancel", "Cancel Upcoming Move",
            "Are you sure you want to cancel this move?", "YES", "NO"
        )
    }

    private fun cancelRequest(
        passenger_id: String?,
        RegistrationID_: String?,
        request_id: String?
    ) {
        val url =
            Utils.update_booking_request_status_passenger
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = request_id
        postParam["status"] = "CANCELLED"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val intent = Intent(context, MainScreenActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            if (arguments != null) {
                                intent.putExtra(
                                    CONSTANTS.move_to_offer_upcoming,
                                    CONSTANTS.move_to_offer_upcoming
                                )
                            } else {
                                intent.putExtra(
                                    CONSTANTS.move_to_move_upcoming,
                                    CONSTANTS.move_to_move_upcoming
                                )
                            }
                            startActivity(intent)
                            activity!!.finish()
                            showToast(
                                "Booking Cancelled"
                            )
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
                },
                Response.ErrorListener { error -> VolleyLog.d("TAG", "Error: " + error.message) }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["passenger_id"] = passenger_id!!
                    headers["registration_id"] = RegistrationID_!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }
}