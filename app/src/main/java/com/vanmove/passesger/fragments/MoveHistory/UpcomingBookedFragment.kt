package com.vanmove.passesger.fragments.MoveHistory

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.vanmove.passesger.adapters.UpcomingAdaptor
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
import com.vanmove.passesger.utils.ShowProgressDialog
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.Show_Sms_intent
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getMinMax
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.custom_dialog_call_sms_options2.*
import kotlinx.android.synthetic.main.driver_dialog.*
import kotlinx.android.synthetic.main.driver_dialog.Cancel
import kotlinx.android.synthetic.main.fragment_upcoming_move.*
import kotlinx.android.synthetic.main.fragment_upcoming_move.view.*
import kotlinx.android.synthetic.main.move_detail.view.*
import kotlinx.android.synthetic.main.quick_view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class UpcomingBookedFragment : Fragment(R.layout.fragment_upcoming_move),
    View.OnClickListener, OnItemClickRecycler, OnClickTwoButtonsAlertDialog,
    OnClickOneButtonAlertDialog {
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    var dialog: BottomSheetDialog? = null
    var UpcomingBookingsList: List<UpcomingBookings>? =
        ArrayList()
    var alertDialog: AlertDialog? = null
    var bookings: UpcomingBookings? = null
    var bottomSheerDialog: BottomSheetDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bottomSheerDialog = BottomSheetDialog(context!!)
        if (!EventBus.getDefault()
                .isRegistered(this@UpcomingBookedFragment)
        ) {
            EventBus.getDefault().register(this@UpcomingBookedFragment)
        }
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        lv_upcoming_booked.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
        )
        upComingBookings()
    }

    private fun upComingBookings() {
        view!!.progress_bar.visible()


        CONSTANTS.mApiService.get_passenger_upcomimg_moves(CONSTANTS.headers)!!
            .enqueue(object : Callback<UpcomingBookingsListModel?> {
                override fun onResponse(
                    call: Call<UpcomingBookingsListModel?>,
                    response: Response<UpcomingBookingsListModel?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.status!!.code == "1000") {
                            UpcomingBookingsList = response.body()!!.bookings
                            if (UpcomingBookingsList!!.size > 0) {
                                tv_message_no_booking!!.visibility = View.GONE
                            } else {
                                tv_message_no_booking!!.visibility = View.VISIBLE
                            }

                            lv_upcoming_booked!!.adapter = UpcomingAdaptor(
                                context!!,
                                UpcomingBookingsList!!, this@UpcomingBookedFragment
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
                    ShowProgressDialog.closeDialog()
                    view!!.progress_bar.gone()
                    Utils.showToast(t.message)
                }
            })
    }


    private fun Show_Menu() {
        val view1 = layoutInflater.inflate(R.layout.move_detail, null)
        dialog = BottomSheetDialog(context!!)
        dialog!!.apply {
            setContentView(view1)
            show()
        }

        view1.run {
            Edit.setOnClickListener(this@UpcomingBookedFragment)
            quick_view.setOnClickListener(this@UpcomingBookedFragment)
            Cancel.setOnClickListener(this@UpcomingBookedFragment)
            Cancel_Move.setOnClickListener(this@UpcomingBookedFragment)
            Driver_Detail.setOnClickListener(this@UpcomingBookedFragment)
            Conatct_Support.setOnClickListener(this@UpcomingBookedFragment)
            Pay_Advanced.setOnClickListener(this@UpcomingBookedFragment)

            Pay_Advanced?.let {
                if (bookings!!.is_offer_advance_paid == 1)
                    it.visibility = View.GONE
                else if (bookings!!.is_offer_advance_paid == 0){
                    it.visibility = View.VISIBLE
                }
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
            Confirm_Dialog_Cancel()
        } else if (id == R.id.Pay_Advanced) {
            dialog!!.dismiss()
            startActivity(
                Intent(context, AdvancedPaymentActivity::class.java)
                    .putExtra("request_id", bookings!!.requestId)
            )
        } else if (id == R.id.Driver_Detail) {
            dialog!!.dismiss()
            if (bookings!!.firstName!!.isEmpty()) {
                showAlertMessageWithOneButtons(
                    context,
                    this@UpcomingBookedFragment,
                    "driver_profile", "Alert",
                    "A driver has not yet been assigned to your " +
                            "job.They should be in contact shortly or check back later",
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
                Intent(context, EditOffer::class.java).apply {
                    putExtra("data", data)
                    putExtra("type", CONSTANTS.Regular)
                }


            )
        }
    }


    private fun Confirm_Dialog_Cancel() {
        val first_name =
            getPreferences(CONSTANTS.first_name, context!!)
        val last_name =
            getPreferences(CONSTANTS.last_name, context!!)
        val title = "Dear $first_name $last_name"

        var msg = ""
        if (bookings!!.isStatus == 12) {
            msg = getString(R.string.cancel_booking_msg)

        } else {
            msg = getString(R.string.cancel_booking_msg)

        }
        showAlertMessageWithTwoButtons(
            context,
            this@UpcomingBookedFragment,
            "Confirm_Dialog_Cancel",
            title,
            msg,
            "Yes- Cancel",
            "No"
        )
    }

    private fun Driver_Detail() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.driver_dialog)
        alertDialog = builder.create()
        alertDialog!!.run {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            Cancel!!.setOnClickListener { dismiss() }

            bookings!!.run {
                Picasso.get().load(
                    Utils.imageUrl + picture
                )
                    .error(R.drawable.error_image).into(driver_image)
                driver_name!!.text =
                    firstName + " " + last_name
                driver_contact!!.text = countryCode +
                        mobile
                driver_contact.setOnClickListener({ showCallSMSSelectionOptions() })
                try {
                    driver_rate!!.rating =
                        rating!!.toFloat()
                } catch (er: Exception) {
                }
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private fun Show_QuickView() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.quick_view)
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
                    Insurance_Cover!!.text = "Yes coverage upto £10,000 costs £20.00"
                }
                val date = changeDateFormat(
                    timestamp, "yyyy-MM-dd HH:mm:ss",
                    "EEEE dd MM yyyy"
                )

                val time = changeDateFormat(
                    timestamp, "yyyy-MM-dd HH:mm:ss",
                    "h:mm a"
                )
                job_date!!.text = "$date at $time"


                when (helpersCount.toString()) {
                    "0", "1" -> {
                        Service_Type!!.text = "$vehicleClassName - $helpersCount Helper"
                    }
                    "2", "3" -> {
                        Service_Type!!.text = "$vehicleClassName - $helpersCount Helpers"
                    }
                }
                val result = getMinMax(estimated_duartion!!)

                first_Hour!!.text =
                    CONSTANTS.CURRENCY + String.format(
                        "%.2f",
                        uptoFirstHourRate!!.toDouble()
                    )
                val after_first_hour_ = ratePerMinuteFare!!.toDouble()
                +helpersCount!!.toDouble() * rateHelperPerMinute!!.toDouble()
                after_first_hour!!.text =
                    CONSTANTS.CURRENCY + String.format("%.2f", after_first_hour_) + "/min"
                if (result[1] == 1.00) {
                    Upto_hour_title!!.text = "Price per hour after 1st hour -"
                    After_Hour_title!!.text = "After 1st hour -"
                } else if (result[1] == 2.00) {
                    Upto_hour_title!!.text = "Price per hour after 2nd hour -"
                    After_Hour_title!!.text = "After 2nd hour -"
                } else if (result[1] == 3.00) {
                    Upto_hour_title!!.text = "Price per hour after 3rd hour -"
                    After_Hour_title!!.text = "After 3rd hour -"
                } else {
                    Upto_hour_title!!.text = "Price per hour after " + result[1] + "th hour -"
                    After_Hour_title!!.text = "After " + result[1] + "th hour -"
                }
                Vechile_Rate!!.text = CONSTANTS.CURRENCY + hourlyRate + "/h  or " +
                        CONSTANTS.CURRENCY +ratePerMinuteFare + "/m + " +
                        CONSTANTS.CURRENCY +ratePerMileFare + "/mile."
                Helper_Rate!!.text =
                    CONSTANTS.CURRENCY + rateHelperHourly + "/h or " +
                            CONSTANTS.CURRENCY + rateHelperPerMinute + "/m ."
                Additional_Stops!!.text =otherStops
                Estimated!!.text = estimated_duartion
                Job_Price!!.text = estimated_payment
                if (is_offer_advance_paid == 1) {
                    Deposit_Paid!!.text =
                        CONSTANTS.CURRENCY + CONSTANTS.decimalFormat(offered_advance)
                }else if (is_offer_advance_paid == 0){
                    Deposit_Paid!!.text = "0.00"
                }
                var estimated_price = estimated_payment
                estimated_price = estimated_price!!.replace(CONSTANTS.CURRENCY, "")
                val Balance_Due_ = estimated_price.toDouble() -offered_advance
                Balance_Due!!.text =
                    CONSTANTS.CURRENCY + CONSTANTS.decimalFormat(Balance_Due_)

            }
        }


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
                com.android.volley.Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            upComingBookings()
                            showToast(
                                "Your booking has been cancelled. Please contact support for refund."
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

    override fun onClickRecycler(view: View?, position: Int) {
        bookings = UpcomingBookingsList!![position]
        val id = view!!.id
        if (id == R.id.action) {
            Show_Menu()
        }
    }

    private fun showCallSMSSelectionOptions() {
        val parentView =
            layoutInflater.inflate(R.layout.custom_dialog_call_sms_options2, null)
        bottomSheerDialog!!.setContentView(parentView)


        rl_dialog_call!!.setOnClickListener(this)
        rl_dialog_sms!!.setOnClickListener(this)
        close_dialog!!.setOnClickListener { bottomSheerDialog!!.dismiss() }
        bottomSheerDialog!!.show()
    }

    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "Confirm_Dialog_Cancel") {
            cancelRequest(
                passenger_id, RegistrationID,
                bookings!!.requestId
            )
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}
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
        if (event.key == CONSTANTS.edit_move) {
            upComingBookings()
        }
    }
}