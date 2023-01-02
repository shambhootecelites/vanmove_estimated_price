package com.vanmove.passesger.fragments.OfferHistory

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.AdvancedPayment.AdvancedPaymentActivity
import com.vanmove.passesger.activities.ContactFragment
import com.vanmove.passesger.activities.EditOffer
import com.vanmove.passesger.adapters.All_Offers_Adaptor
import com.vanmove.passesger.interfaces.OnClickOneButtonAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.APIModel.UpcomingBookingsListModel
import com.vanmove.passesger.model.MessageEvent
import com.vanmove.passesger.model.UpcomingBookings
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithOneButtons
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.Show_Sms_intent
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.custom_dialog_call_sms_options2.view.*
import kotlinx.android.synthetic.main.driver_dialog.*
import kotlinx.android.synthetic.main.driver_dialog.Cancel
import kotlinx.android.synthetic.main.fragment_upcoming_booked.view.*
import kotlinx.android.synthetic.main.offer_menu.view.*
import kotlinx.android.synthetic.main.quick_view_offer.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UpcomingOffer : Fragment(R.layout.fragment_upcoming_booked), OnItemClickRecycler,
    View.OnClickListener, OnClickTwoButtonsAlertDialog, OnClickOneButtonAlertDialog {
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    var dialog: BottomSheetDialog? = null
    var alertDialog: AlertDialog? = null
    var list: List<UpcomingBookings>? = ArrayList()
    var bottomSheerDialog: BottomSheetDialog? = null
    var bookings: UpcomingBookings? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bottomSheerDialog = BottomSheetDialog(context!!)
        if (!EventBus.getDefault().isRegistered(this@UpcomingOffer)) {
            EventBus.getDefault().register(this@UpcomingOffer)
        }
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)

        upComingBookings
    }


    private val upComingBookings: Unit
        get() {
            view!!.progress_bar.visible()

            CONSTANTS.mApiService.get_passenger_upcomimg_offer(CONSTANTS.headers)!!
                .enqueue(object : Callback<UpcomingBookingsListModel?> {
                    override fun onResponse(
                        call: Call<UpcomingBookingsListModel?>,
                        response: Response<UpcomingBookingsListModel?>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.status!!.code == "1000") {
                                list = response.body()!!.bookings
                                if (list!!.size > 0) {
                                    view!!.tv_message_no_booking!!.visibility = View.GONE
                                } else {
                                    view!!.tv_message_no_booking!!.visibility = View.VISIBLE
                                }
                                view!!.lv_upcoming_booked!!.adapter = All_Offers_Adaptor(
                                    context!!,
                                    list!!, this@UpcomingOffer
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    response.body()!!.status!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(context, response.raw().message, Toast.LENGTH_SHORT)
                                .show()
                        }
                        view!!.progress_bar.gone()

                    }

                    override fun onFailure(
                        call: Call<UpcomingBookingsListModel?>,
                        t: Throwable
                    ) {
                        view!!.progress_bar.gone()

                        showToast(t.message)
                    }
                })
        }

    override fun onClickRecycler(view: View?, position: Int) {
        val id = view!!.id
        bookings = list!![position]
        if (id == R.id.detail_btn) {
            Show_Menu()
        }
    }

    private fun showCallSMSSelectionOptions() {
        val parentView = layoutInflater.inflate(R.layout.custom_dialog_call_sms_options2, null)
        bottomSheerDialog!!.setContentView(parentView)
        parentView.rl_dialog_call!!.setOnClickListener(this)
        parentView.rl_dialog_sms!!.setOnClickListener(this)
        parentView.close_dialog!!.setOnClickListener { bottomSheerDialog!!.dismiss() }
        bottomSheerDialog!!.show()
    }

    private fun Show_Menu() {
        val view = layoutInflater.inflate(R.layout.offer_menu, null)
        dialog = BottomSheetDialog(context!!)
        dialog!!.apply {
            setContentView(view)
            show()
        }
        view.run {
            Edit.setOnClickListener(this@UpcomingOffer)
            quick_view.setOnClickListener(this@UpcomingOffer)
            Cancel.setOnClickListener(this@UpcomingOffer)
            Cancel_Move.setOnClickListener(this@UpcomingOffer)
            Driver_Detail.setOnClickListener(this@UpcomingOffer)
            Conatct_Support.setOnClickListener(this@UpcomingOffer)
            pay_advanced.setOnClickListener(this@UpcomingOffer)
            if (bookings!!.isStatus == 2) {
                pay_advanced.visibility = View.VISIBLE
            } else {
                pay_advanced.visibility = View.GONE
            }
        }


    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.rl_dialog_call) {
            bottomSheerDialog!!.dismiss()
            Dial_Number(
                context!!, bookings!!.countryCode +
                        bookings!!.mobile
            )
        } else if (id == R.id.rl_dialog_sms) {
            bottomSheerDialog!!.dismiss()
            Show_Sms_intent(
                bookings!!.countryCode +
                        bookings!!.mobile, context!!
            )
        } else if (id == R.id.quick_view) {
            dialog!!.dismiss()
            Show_QuickView()
        } else if (id == R.id.Cancel) {
            dialog!!.dismiss()
        } else if (id == R.id.Cancel_Move) {
            dialog!!.dismiss()
            val first_name =
                getPreferences(CONSTANTS.first_name, context!!)
            val last_name =
                getPreferences(CONSTANTS.last_name, context!!)
            val title = "Dear $first_name $last_name"
            showAlertMessageWithTwoButtons(
                context,
                this@UpcomingOffer,
                "Confirm_Dialog_Cancel",
                title,
                getString(R.string.cancel_booking_msg),
                "Yes- Cancel",
                "No"
            )
        } else if (id == R.id.Driver_Detail) {
            dialog!!.dismiss()
            if (bookings!!.isStatus == 0) {
                showAlertMessageWithOneButtons(
                    context,
                    this@UpcomingOffer,
                    "driver_profile", "Alert",
                    "A driver has not yet been assign to you " +
                            "offer.They should be in contact shortly or check back later",
                    "Ok"
                )
            } else {
                Driver_Detail()
            }
        } else if (id == R.id.Conatct_Support) {
            dialog!!.dismiss()
            startActivity(Intent(context, ContactFragment::class.java))
        } else if (id == R.id.Edit) {
            dialog!!.dismiss()
            val data = Gson().toJson(bookings!!)
            startActivity(
                Intent(context, EditOffer::class.java)
                    .putExtra("data", data)
                    .putExtra("type", CONSTANTS.Fixed)
            )
        } else if (id == R.id.pay_advanced) {
            dialog!!.dismiss()
            Payment_Fragmnet()
        }
    }

    private fun Driver_Detail() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.driver_dialog)
        alertDialog = builder.create()

        alertDialog!!.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            Cancel!!.setOnClickListener { dismiss() }
            bookings!!.run {
                Picasso.get()
                    .load(Utils.imageUrl + picture)
                    .error(R.drawable.error_image).into(driver_image)
                driver_name!!.text = "$firstName $last_name"
                driver_contact!!.text = "$countryCode$mobile"
                driver_contact.setOnClickListener {
                    showCallSMSSelectionOptions()
                }
                try {
                    driver_rate!!.rating = rating!!.toFloat()
                } catch (er: Exception) {
                }
            }

        }

    }

    private fun Show_QuickView() {
        val builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.quick_view_offer)
        alertDialog = builder.create()

        alertDialog!!.run {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
            setCancelable(true)
            setCanceledOnTouchOutside(true)


            Cancel!!.setOnClickListener { dismiss() }

            bookings!!.run {
                val isInsurance = "" + isInsurance
                if (isInsurance == "0") {
                    Insurance_Cover!!.text = CONSTANTS.Standard_insurance_
                } else {
                    Insurance_Cover!!.text = CONSTANTS.Costs_insurance
                }

                val date = changeDateFormat(
                    timestamp,
                    "yyyy-MM-dd HH:mm:ss",
                    "EEEE dd MM yyyy"
                )

                val time = changeDateFormat(
                    timestamp,
                    "yyyy-MM-dd HH:mm:ss",
                    "h:mm a"
                )
                when (is_flexible) {
                    "No" -> {
                        job_date!!.text = "$date at $time"

                    }
                    "Yes" -> {
                        job_date!!.text = "Flexible"

                    }
                }

                Estimated_Duartion!!.text = estimated_duartion
                Offer_Amount!!.text = CONSTANTS.CURRENCY + CONSTANTS.decimalFormat(offered_price)
                when (helpersCount.toString()) {
                    "0", "1" -> {
                        Service_Type!!.text = "$vehicleClassName - $helpersCount Helper"
                    }
                    "2", "3" -> {
                        Service_Type!!.text = "$vehicleClassName - $helpersCount Helpers"
                    }
                }


                Additional_Stops!!.text = otherStops

                val advanced_amount = (offered_price * 0.25)
                advanced_amount.let {
                    Deposit_Paid!!.text =
                        CONSTANTS.CURRENCY + CONSTANTS.decimalFormat(it)

                }
                val Balance_Due_ = offered_price - offered_advance
                Balance_Due!!.text =
                    CONSTANTS.CURRENCY + CONSTANTS.decimalFormat(Balance_Due_)
            }

        }

    }

    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "Confirm_Dialog_Cancel") {
            cancelRequest(
                passenger_id, RegistrationID,
                bookings!!.requestId
            )
        }
    }

    private fun Payment_Fragmnet() {
        startActivity(
            Intent(context, AdvancedPaymentActivity::class.java)
                .putExtra("request_id", "" + bookings!!.requestId)

        )
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}
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
                com.android.volley.Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            upComingBookings
                            showToast(
                                "Booking Request Cancelled"
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
                com.android.volley.Response.ErrorListener { error ->
                    VolleyLog.d(
                        "TAG",
                        "Error: " + error.message
                    )
                }) {
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

    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialog: DialogInterface?
    ) {
        if (dialog_name == "driver_profile") {
            dialog!!.dismiss()
        }
    }

    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        if (event.key == CONSTANTS.edit_offer) {
            upComingBookings
        }
    }
}