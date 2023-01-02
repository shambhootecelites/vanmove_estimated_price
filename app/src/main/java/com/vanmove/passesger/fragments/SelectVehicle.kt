package com.vanmove.passesger.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.gson.Gson
import com.kaopiz.kprogresshud.KProgressHUD
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.ApplyPromoCode.ApplyPromoCodeActivity
import com.vanmove.passesger.activities.FareBreakDown
import com.vanmove.passesger.activities.PorterActivity
import com.vanmove.passesger.activities.VechileList
import com.vanmove.passesger.adapters.Holder
import com.vanmove.passesger.adapters.InsuranceAdapter
import com.vanmove.passesger.adapters.VanBookingAdapter
import com.vanmove.passesger.fragments.MoveFlow.RegularRideJobSheetActivity
import com.vanmove.passesger.fragments.OfferFlow.OfferActivity
import com.vanmove.passesger.interfaces.OnClickOneButtonAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.interfaces.VanSelectionInterface
import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.model.InsurancePlan
import com.vanmove.passesger.model.PromoCode
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithOneButtons
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.CONSTANTS.isPromoCodeApply
import com.vanmove.passesger.utils.CONSTANTS.promoCode
import com.vanmove.passesger.utils.DateTimePicker
import com.vanmove.passesger.utils.DialogUtils
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.calculateFareUpto
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.activity_vechile_all.view.*
import kotlinx.android.synthetic.main.alert_booking_options.*
import kotlinx.android.synthetic.main.alert_insurance.*
import kotlinx.android.synthetic.main.info_dialogue.*
import kotlinx.android.synthetic.main.single_view_taxi_new.view.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectVehicle : Fragment(R.layout.activity_vechile_all),
    View.OnClickListener, VanSelectionInterface, OnItemClickRecycler,
    OnClickTwoButtonsAlertDialog, OnClickOneButtonAlertDialog {
    var alertDialog: AlertDialog? = null
    private var timer: Timer? = null
    private var doAsynchronousTaskGetDriver: TimerTask? = null

    var GetVehicles = ArrayList<GetVehicles>()
    var linearLayoutManager: LinearLayoutManager? = null
    var vechile_type = ""
    var holder: Holder? = null
    var position = 0

    private var distance_in_miles = "0.0"
    private var progressDialog: KProgressHUD? = null


    var estimated_duartion: String? = null
    var max_hour = 0.0
    private var driver_id: String? = null
    private var vehicle_class_id: String? = null
    private var vehicle_class_name: String? = null
    private var latitude_driver: String? = null
    private var longitude_driver: String? = null
    private var getNearestTime = 1
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    private var date_future_booking_str: String? = null
    private var time_slots_future_booking_str: String? = null


    override fun onStop() {
        super.onStop()
        try {
            if (doAsynchronousTaskGetDriver != null) {
                timer!!.cancel()
                doAsynchronousTaskGetDriver!!.cancel()
                doAsynchronousTaskGetDriver = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isPromoCodeApply = false
        progressDialog = DialogUtils.showProgressDialog(context!!, cancelable = false)

        iv_back.setOnClickListener {
            activity!!.finish()
        }

        distance_in_miles = arguments!!.getString("distance_in_miles").toString()
        view.more_vechile!!.setOnClickListener(this)
        view.btn_other_options!!.setOnClickListener(this)
        view.tv_payment!!.setOnClickListener(this)
        view.btn_selected_van!!.setOnClickListener(this)
        view.apply_code!!.setOnClickListener(this)
        RegistrationID = getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id = getPreferences(CONSTANTS.passenger_id, context!!)
        date_future_booking_str = getPreferences(
            CONSTANTS.date_future_booking_str,
            context!!
        )
        time_slots_future_booking_str = getPreferences(
            CONSTANTS.time_slots_future_booking_str,
            context!!
        )
        if (date_future_booking_str!!.isEmpty()||time_slots_future_booking_str!!.isEmpty()) {
            view.is_furture.setText("BOOK NOW")
        } else {
            view.is_furture.setText("BOOK")
        }
        val helpers =
            getPreferences(CONSTANTS.helpers, context!!)
        if (helpers == "0" || helpers == "1") {
            view.helper_count.setText("WITH $helpers HELPER")
        } else {
            view.helper_count.setText("WITH $helpers HELPERS")
        }
        estimated_duartion =
            getPreferences(CONSTANTS.estimated_duartion, context!!)
        view.duration.setText("Duration: $estimated_duartion")
        allVehicles()

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_other_options -> when {
                date_future_booking_str!!.isEmpty() -> {
                    showToast("This feature is not available for ASAP Bookings.")
                }

                vechile_type == "Van" -> {
                    bookingsOtherOptions()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "Alert - Sorry fixed price offer is not currently available for the selected service.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.Make_Offer -> {
                alertDialog!!.dismiss()
                startActivity(
                    Intent(context, OfferActivity::class.java)
                        .putExtra("distanceFromGoogle", distance_in_miles)
                        .putExtra("position", position)
                )
            }
            R.id.Call_us -> {
                alertDialog!!.dismiss()
                DIAL_NUMBER(CONSTANTS.Booking_Team)
            }
            R.id.live_chat -> {
                val intent = Intent(context, ChatWindowActivity::class.java)
                val config = chatWindowConfiguration
                intent.putExtras(config.asBundle())
                startActivity(intent)
            }
            R.id.close_other_booking -> alertDialog!!.dismiss()
            R.id.btn_selected_van -> {

                val payBy = getPreferences(CONSTANTS.PAY_BY, context!!)
                val cardNum = getPreferences(CONSTANTS.card_number, context!!)
                if (date_future_booking_str!!.isEmpty()) {
                    if (CONSTANTS.vehicles!!.driver_time == null) {
                        val msg =
                            "${CONSTANTS.vehicles!!.vehicle_name} is not available for ASAP. Would you like to book for a later time or date?"

                        showAlertMessageWithTwoButtons(
                            context,
                            this@SelectVehicle,
                            "vechile_not_found", "Alert",
                            msg,
                            "Yes", "Cancel"
                        )


                    }else if(payBy == CONSTANTS.CARD){
                     if (cardNum == ""){
                         showAlertMessageWithOneButtons(
                             context,
                             this,
                             "no_card_added",
                             "Alert",
                             "No card is added to the account. Please add a card or change payment method to proceed with your booking.",
                             "Ok")
                     }else{
                         showfareMsg()
                     }
                    }
                    else {
                        showfareMsg()
                    }
                }
                else if(payBy == CONSTANTS.CARD) {
                    if (cardNum == "") {
                        showAlertMessageWithOneButtons(
                            context,
                            this,
                            "no_card_added",
                            "Alert",
                            "No card is added to the account. Please add a card or change payment method to proceed with your booking.",
                            "Ok"
                        )
                    }else{
                        showfareMsg()
                    }
                }
                else {
                    showfareMsg()
                }


            }
            R.id.tv_payment -> startActivity(Intent(context, PaymentMethodFragment::class.java))
/*            R.id.rel_free -> {
                alertDialog!!.tv_free!!.setTextColor(Color.WHITE)
                alertDialog!!.tv_ten!!.setTextColor(Color.BLACK)
                alertDialog!!.rel_free!!.setBackgroundColor(Color.parseColor("#1E71FD"))
                alertDialog!!.rel_ten!!.setBackgroundColor(Color.parseColor("#ffffff"))
                savePreferences(
                    CONSTANTS.insurance,
                    CONSTANTS.Standard_insurance_,
                    context!!
                )
            }
            R.id.rel_ten -> {
                alertDialog!!.tv_free!!.setTextColor(Color.BLACK)
                alertDialog!!.tv_ten!!.setTextColor(Color.WHITE)
                alertDialog!!.rel_ten!!.setBackgroundColor(Color.parseColor("#1E71FD"))
                alertDialog!!.rel_free!!.setBackgroundColor(Color.parseColor("#ffffff"))
                savePreferences(
                    CONSTANTS.insurance,
                    CONSTANTS.Costs_insurance,
                    context!!
                )
            }*/
            R.id.more_vechile -> if (GetVehicles.size > 0) {
                Utils.OnlyVansList.clear()
                Utils.OnlyVansList.addAll(Utils.GetVehicles)
                startActivityForResult(
                    Intent(activity, VechileList::class.java)
                    ,
                    vachile_list_request_code
                )
            }
            R.id.apply_code -> startActivityForResult(
                Intent(
                    context,
                    ApplyPromoCodeActivity::class.java
                ), 20
            )
        }
    }

    private fun showfareMsg() {
        val helpers = getPreferences(CONSTANTS.helpers, context!!)
        val after_hour_complete_rate = CONSTANTS.vehicles!!.rate_per_minute_fare.toDouble() +
                helpers!!.toDouble() * CONSTANTS.vehicles!!.rate_helper_per_minute.toDouble()

        showAlertMessageWithTwoButtons(context,
            this@SelectVehicle,
            "confirm_amount", "Alert",
            "Please note, a minimum fare of  " + view!!.tv_approximated_fare!!.text.toString()
                    + " will be charged for the booking details you have selected, as long as your job is completed within " +
                    +max_hour + " hour(s). Any time used over " + max_hour + " hour(s) will be charged at " +
                    CONSTANTS.CURRENCY + String.format("%.2f", after_hour_complete_rate) + " per minute.",
            "Continue ", "Cancel"
        )
    }

    val chatWindowConfiguration: ChatWindowConfiguration
        get() {
            val email =
                getPreferences(CONSTANTS.username, context!!)
            val first_name =
                getPreferences(CONSTANTS.first_name, context!!)
            val last_name =
                getPreferences(CONSTANTS.last_name, context!!)
            val full_name = "$first_name $last_name"
            return ChatWindowConfiguration(
                CONSTANTS.KEY_LICENCE_NUMBER, null, full_name, email,
                null
            )
        }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                if (requestCode == vachile_list_request_code) {
                    val vechile_position = intent.getIntExtra("vechile_position", 0)
                    linearLayoutManager!!.scrollToPosition(vechile_position)
                    Handler().postDelayed({
                        val holder =
                            view!!.vechile_list!!.findViewHolderForAdapterPosition(vechile_position)
                        holder!!.itemView.performClick()
                    }, 300)
                } else if (requestCode == 20) {
                    val data_ = intent.getStringExtra("data")
                    val message = intent.getStringExtra("message")
                    promoCode = Gson().fromJson(data_, PromoCode::class.java)
                    isPromoCodeApply = true
                    view!!.vechile_list!!.adapter!!.notifyDataSetChanged()
                    view!!.result!!.visibility = View.VISIBLE
                    view!!.result!!.text = "Promo code applied successfully " + promoCode!!.percentage + "% OFF"
                }
            }
        }
    }

    private fun OpenInsurance(list: List<InsurancePlan>) {
        savePreferences(
            CONSTANTS.estimated_payment,
            view!!.tv_approximated_fare!!.text.toString(),
            context!!
        )
        val booking_from_porter =
            getPreferences(CONSTANTS.BOOKING_PORTER, context!!)
        if (booking_from_porter.equals("true", ignoreCase = true)) {
            startActivity(Intent(context, PorterActivity::class.java))
        } else {
            showInsuranceAlert(list)
        }
    }

    private fun DIAL_NUMBER(number: String) {
        try {
            val uri = "tel:$number"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bookingsOtherOptions() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.alert_booking_options)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()
        alertDialog!!.setCancelable(true)
        alertDialog!!.setCanceledOnTouchOutside(true)

        alertDialog!!.Make_Offer!!.setOnClickListener(this)
        alertDialog!!.live_chat!!.setOnClickListener(this)
        alertDialog!!.Call_us!!.setOnClickListener(this)
        alertDialog!!.close_other_booking!!.setOnClickListener(this)
    }

    private fun Open_Chrome(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showInsuranceAlert(list:List<InsurancePlan>) {
        var insurance_planc_price=0.0
        var insurance_plan=""
        val builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.alert_insurance)
        alertDialog = builder.create()
        alertDialog!!.show()

        linearLayoutManager = LinearLayoutManager(
            getContext(),
            LinearLayoutManager.VERTICAL, false
        )
        alertDialog!!.insurance_type_list!!.layoutManager = linearLayoutManager
        alertDialog!!.insurance_type_list!!.adapter = InsuranceAdapter(
            context!!,
            list, object : OnItemClickRecycler {
                override fun onClickRecycler(view: View?, position: Int) {
                  if (list[position].isSelect){
                      insurance_planc_price=list[position].plan_price.toDouble()
                      insurance_plan=list[position].plan_name
                  }
                  else{
                      insurance_planc_price=0.0
                      insurance_plan=""
                  }
                }
            }
        )



        alertDialog!!.term_condition_insurance!!.setOnClickListener {
            Open_Chrome(CONSTANTS.insurance_link)
        }
      //  alertDialog!!.rel_free!!.setOnClickListener(this)
     //   alertDialog!!.rel_ten!!.setOnClickListener(this)
        alertDialog!!.btn_continue!!.setOnClickListener {
            alertDialog!!.dismiss()
            savePreferences(CONSTANTS.insurance_fare, insurance_planc_price.toString(), context!!)
            savePreferences(CONSTANTS.insurance_plan, insurance_plan, context!!)

            val estimated_price=view!!.tv_approximated_fare!!.text.toString().replace( CONSTANTS.CURRENCY,"")
            val totalEstimatedFare=estimated_price.toDouble()+insurance_planc_price
            System.out.println("totalEstimatedFare::"+totalEstimatedFare)
            savePreferences(CONSTANTS.estimated_payment, CONSTANTS.CURRENCY+totalEstimatedFare.toString(), context!!)

            startActivity(Intent(activity, RegularRideJobSheetActivity::class.java))
        }
        alertDialog!!.close_insurance!!.setOnClickListener {
            alertDialog!!.dismiss()
        }
    }

    override fun onVanImageClicking(
        vehicle_class_id: String?, vehicles: GetVehicles?,
        holder: Holder?, position: Int
    ) {
        try {
            this.holder = holder
            this.position = position
            CONSTANTS.vehicles = vehicles
            if (vehicle_class_id == "13") {
                savePreferences(
                    CONSTANTS.BOOKING_PORTER,
                    "true",
                    context!!
                )
            } else {
                savePreferences(
                    CONSTANTS.BOOKING_PORTER,
                    "false",
                    context!!
                )
            }
            savePreferences(
                CONSTANTS.rate_per_mile,
                vehicles!!.rate_per_mile_fare,
                context!!
            )
            savePreferences(
                CONSTANTS.rate_per_minute,
                vehicles.rate_per_minute_fare,
                context!!
            )
            savePreferences(
                CONSTANTS.rate_per_hour,
                vehicles.hourly_rate,
                context!!
            )
            savePreferences(
                CONSTANTS.rate_per_helper_hour,
                vehicles.rate_helper_hourly,
                context!!
            )
            savePreferences(
                CONSTANTS.rate_per_helper_per_minute,
                vehicles.rate_helper_per_minute,
                context!!
            )
            savePreferences(
                CONSTANTS.vehicle_name,
                vehicles.vehicle_name,
                context!!
            )
            vechile_type = vehicles.type
            savePreferences(
                CONSTANTS.vehicle_class_id,
                vehicle_class_id,
                context!!
            )
            view!!.servies_name!!.text = vehicles.vehicle_name

            var price = Fare_Estimation(estimated_duartion, vehicles)

            price = String.format("%.2f", price.toDouble())
            if (isPromoCodeApply) {
                val original_Price = "<del>" + CONSTANTS.CURRENCY + price + "</del>"
                view!!.tv_approximated_fare2.visibility = View.VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view!!.tv_approximated_fare2.setText(
                        Html.fromHtml(
                            original_Price,
                            Html.FROM_HTML_MODE_COMPACT
                        )
                    )
                } else {
                    view!!.tv_approximated_fare2.setText(Html.fromHtml(original_Price))
                }

                var discount_price = (price.toDouble()
                        - ((price.toDouble()
                        * promoCode!!.percentage!!.toDouble()) / 100)).toString()

                discount_price =
                    CONSTANTS.CURRENCY + String.format("%.2f", discount_price.toDouble())
                view!!.tv_approximated_fare.setText(discount_price)


            } else {
                view!!.tv_approximated_fare2.visibility = View.GONE
                view!!.tv_approximated_fare.text =
                    CONSTANTS.CURRENCY + String.format("%.2f", price.toDouble())

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Fare_Estimation(
        time: String?,
        vehicles: GetVehicles
    ): String {
        if (time == CONSTANTS.estimated_duration_list[1]) {
            max_hour = 1.00
            return calculateFareUpto(
                1.00,
                vehicles,
                distance_in_miles,
                context!!
            )


        } else if (time == CONSTANTS.estimated_duration_list[2]) {
            max_hour = 2.00
            return calculateFareUpto(
                2.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        } else if (time == CONSTANTS.estimated_duration_list[3]) {
            max_hour = 2.50
            return calculateFareUpto(
                2.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[4]) {
            max_hour = 3.00
            return calculateFareUpto(
                3.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[5]) {
            max_hour = 3.50
            return calculateFareUpto(
                3.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[6]) {
            max_hour = 4.00
            return calculateFareUpto(
                4.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[7]) {
            max_hour = 4.50
            return calculateFareUpto(
                4.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[8]) {
            max_hour = 5.00
            return calculateFareUpto(
                5.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[9]) {
            max_hour = 5.50
            return calculateFareUpto(
                5.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[10]) {
            max_hour = 6.00
            return calculateFareUpto(
                6.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[11]) {
            max_hour = 6.50
            return calculateFareUpto(
                6.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[12]) {
            max_hour = 7.00
            return calculateFareUpto(
                7.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[13]) {
            max_hour = 7.50
            return calculateFareUpto(
                7.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[14]) {
            max_hour = 8.00
            return calculateFareUpto(
                8.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[15]) {
            max_hour = 8.50
            return calculateFareUpto(
                8.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[16]) {
            max_hour = 9.00
            return calculateFareUpto(
                9.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[17]) {
            max_hour = 9.50
            return calculateFareUpto(
                9.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[18]) {
            max_hour = 10.00
            return calculateFareUpto(
                10.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[19]) {
            max_hour = 10.50
            return calculateFareUpto(
                10.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[20]) {
            max_hour = 11.00
            return calculateFareUpto(
                11.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[21]) {
            max_hour = 11.50
            return calculateFareUpto(
                11.50,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[22]) {
            max_hour = 12.00
            return calculateFareUpto(
                12.00,
                vehicles,
                distance_in_miles,
                context!!
            )

        }
        else {
            return "0"

        }
    }

    override fun onClickRecycler(view: View?, position: Int) {
        val id = view!!.id
        if (id == R.id.tv_cabbii_type) {
            VechileInfo(position)
        } else if (id == R.id.fare_breakdown) {
            show_braekdown(position)
        }
       /* else if (id == R.id.book_later) {
            DateTimePicker.ShowDatePicker(
                activity!!.supportFragmentManager,
                listener
            )
            Utils.showToastLong(getString(R.string.job_time))
        }*/
    }

    private val listener: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            try {
                val compare_format =
                    SimpleDateFormat("yyyy-MM-dd")
                val date_future_booking_str = compare_format.format(date)
                savePreferences(
                    CONSTANTS.date_future_booking_str,
                    date_future_booking_str,
                    context!!
                )

                val compare_time_format =
                    SimpleDateFormat("HH:mm")
                val time_slots_future_booking_str = compare_time_format.format(date)
                savePreferences(
                    CONSTANTS.time_slots_future_booking_str,
                    time_slots_future_booking_str,
                    context!!
                )



                savePreferences(
                    CONSTANTS.estimated_payment,
                    view!!.tv_approximated_fare!!.text.toString(),
                    context!!
                )
               // showInsuranceAlert()
                view!!.vechile_list!!.adapter!!.notifyDataSetChanged()
            } catch (error: Exception) {
            }
        }

        override fun onDateTimeCancel() {}
    }

    private fun show_braekdown(position: Int) {
      /*   String booking_from_porter = Utils.getPreferences(CONSTANTS.BOOKING_PORTER, context);
        if (booking_from_porter.equalsIgnoreCase("true")) {
            holder.view!!.tv_cabbii_type.performClick();
        } else {
            String data = new Gson().toJson(GetVehicles.get(position));
            startActivity(new Intent(context, FareBreakDown.class)
                    .putExtra("move_distance", "" + distance_in_miles)
                    .putExtra("position", position)
                    .putExtra("data", data)
            );
        }
       */
        val data = Gson().toJson(GetVehicles[position])
        startActivity(
            Intent(context, FareBreakDown::class.java)
                .putExtra("move_distance", "" + distance_in_miles)
                .putExtra("position", position)
                .putExtra("data", data)
        )
    }

    private fun VechileInfo(position: Int) {
        GetVehicles[position].run {
            val builder = AlertDialog.Builder(context!!)
            builder.setView(R.layout.info_dialogue)
            val alert_dialog = builder.create()
            alert_dialog.run {
                setCancelable(true)
                setCanceledOnTouchOutside(true)
                show()

                tv_name!!.text = vehicle_name
                Usage!!.text = info
                Capacity!!.text = "Capacity: " + capacity
                Max_Load!!.text = "Max Load: " + weight
                External_DIMESIONS!!.text = "External: " + external_dimension
                Internal_DIMESIONS!!.text = "Internal: " + internal_dimension
                tv_rate_per_mile!!.text =
                    "VAN Rate per mile : " + CONSTANTS.CURRENCY + rate_per_mile_fare
             /*   tv_mint_rate!!.text =
                    "Rate per minute: " + CONSTANTS.CURRENCY + rate_per_minute_fare*/
                tv_hourly_rate!!.text =
                    "VAN Rate per hour : " + CONSTANTS.CURRENCY + hourly_rate
                tv_helper_rate!!.text =
                    "Helper rate per hour: " + CONSTANTS.CURRENCY + rate_helper_hourly
              /*  tv_helper_rate_per_minut!!.text = "Helper rate per minute: " +
                        CONSTANTS.CURRENCY + rate_helper_per_minute*/
                Cancel!!.setOnClickListener { dismiss() }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        val pay_by = getPreferences(CONSTANTS.PAY_BY, context!!)
        if (pay_by == CONSTANTS.CASH) {
            view!!.payment_method!!.text = "Pay cash to driver"
        } else {
            view!!.payment_method!!.text = "Pay by card"
        }
    }

    private fun allVehicles() {
        progressDialog!!.show()

        val url =
            Utils.get_vehicle_classes_passenger_all_Url
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val pickup_lat = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )!!.toDouble()
        val pickup_lng = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            context!!
        )!!.toDouble()
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["latitude"] = pickup_lat
        postParam["longitude"] = pickup_lng
        postParam["helpers_count"] = "0"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    val result = jsonObject.toString()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val first = JSONObject(result)
                            val JA = first.getJSONArray("data")
                            try {
                                    for (i in 0 until JA.length()) {
                                    val jsonData = JA.getJSONObject(i)
                                    val vehicle_id = jsonData.getString("id")
                                    val vehicle_name = jsonData.getString("name")
                                    val type = jsonData.getString("type")
                                    val weight = jsonData.getString("weight")
                                    val capacity = jsonData.getString("capacity")
                                    val internal_dimension =
                                        jsonData.getString("internal_dimension")
                                    val external_dimension =
                                        jsonData.getString("external_dimension")
                                    val info = jsonData.getString("info")
                                    val picture = jsonData.getString("picture")
                                        val date_future_booking_str=getPreferences(CONSTANTS.date_future_booking_str, context!!)
                                        val inputformat = SimpleDateFormat("yyyy-MM-dd")
                                        val mDate = inputformat.parse(date_future_booking_str);
                                        val weekday_name = SimpleDateFormat("EEEE", Locale.ENGLISH).format(mDate)
                                        val  rate_per_mile_fare = jsonData.getString("rate_per_mile_fare")

                                        System.out.println("weekday_name::::"+weekday_name)

                                        var hourly_rate =""

                                        var rate_per_minute_fare =""
                                        var rate_helper_per_minute =""
                                        var rate_helper_hourly =""

                                        if (weekday_name.equals("Friday")||weekday_name.equals("Saturday")||weekday_name.equals("Sunday")){
                                            val  hourlyRate = jsonData.getDouble("weekend_hourly_rate")
                                            hourly_rate=""+hourlyRate;
                                            var ratePerMin=hourlyRate/60
                                            rate_per_minute_fare=""+ratePerMin.toString()

                                            System.out.println("2 weekday_name::::"+weekday_name)
                                            System.out.println("1 hourly_rate::::"+hourly_rate)

                                            System.out.println("1 rate_per_minute_fare::::"+rate_per_minute_fare)
                                            val rateHelperHourly = jsonData.getDouble("weekend_rate_helper_hourly")
                                            rate_helper_hourly=rateHelperHourly.toString()

                                            val rateHelperPerMinute = rateHelperHourly/60
                                            rate_helper_per_minute=""+rateHelperPerMinute.toString();
                                            System.out.println("1 rate_per_minute_fare::::"+rate_helper_per_minute)


                                        }
                                        else{
                                            val  hourlyRate = jsonData.getDouble("hourly_rate")
                                            hourly_rate=""+hourlyRate;
                                            var ratePerMin=hourlyRate/60
                                            rate_per_minute_fare=""+ratePerMin.toString()

                                            val rateHelperHourly = jsonData.getDouble("rate_helper_hourly")
                                            val rateHelperPerMinute = rateHelperHourly/60
                                            rate_helper_hourly=rateHelperHourly.toString()
                                            rate_helper_per_minute=""+rateHelperPerMinute.toString();
                                            System.out.println("2 rate_helper_per_minute::::"+rate_helper_per_minute)
                                            System.out.println("2 hourly_rate::::"+hourly_rate)

                                            System.out.println("2 rate_per_minute_fare::::"+rate_per_minute_fare)


                                        }
                                        val  first_floor_charge = jsonData.getString("first_floor_charge")
                                        val  second_floor_charge = jsonData.getString("second_floor_charge")
                                        val  third_floor_charge = jsonData.getString("third_floor_charge")
                                        val  fourth_above_charge = jsonData.getString("fourth_above_charge")

                                        savePreferences(CONSTANTS.first_floor_charge,
                                            first_floor_charge,
                                            context!!
                                        )
                                        savePreferences(CONSTANTS.second_floor_charge,
                                            second_floor_charge,
                                            context!!
                                        )
                                        savePreferences(CONSTANTS.third_floor_charge,
                                            third_floor_charge,
                                            context!!
                                        )
                                        savePreferences(CONSTANTS.fourth_above_charge,
                                            fourth_above_charge,
                                            context!!
                                        )

                                   // val rate_driver_commision = jsonData.getString("rate_driver_commision")
                                        val rate_driver_commision = "0.0"

                                        val jsonArray_drivers =
                                        jsonData.getJSONArray("drivers")
                                    if (jsonArray_drivers.length() > 0) {
                                        getNearestTime = 0
                                    }
                                    var minute: Int? = null
                                    for (i2 in 0 until jsonArray_drivers.length()) {
                                        val jsonData2 =
                                            jsonArray_drivers.getJSONObject(i2)
                                        driver_id = jsonData2.getString("driver_id")
                                        vehicle_class_id =
                                            jsonData2.getString("vehicle_class_id")
                                        vehicle_class_name =
                                            jsonData2.getString("vehicle_class_name")
                                        latitude_driver = jsonData2.getString("latitude")
                                        longitude_driver = jsonData2.getString("longitude")
                                        val distance_driver = jsonData2.getString("distance")
                                        if (getNearestTime == 0) {
                                            try {
                                                val distance = distance_driver.toDouble()
                                                val seconds = distance * 1000 / 6.8
                                                //6.8 m/s is an assume speed of driver
                                                minute = seconds.toInt() / 60
                                                getNearestTime = 1
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                    val vehicles = GetVehicles(
                                        vehicle_id,
                                        vehicle_name, type, weight, capacity,
                                        info, internal_dimension, external_dimension, picture,
                                        hourly_rate, rate_per_mile_fare, rate_per_minute_fare,
                                        rate_helper_hourly, rate_helper_per_minute,
                                        rate_driver_commision,
                                        driver_id, vehicle_class_id,
                                        vehicle_class_name,
                                        latitude_driver, longitude_driver,
                                        "", minute
                                    )
                                    GetVehicles.add(Utils.getEstimationDurationVehicles(estimated_duartion,vehicles,activity!!) )
                                }
                                Utils.GetVehicles =GetVehicles

                                linearLayoutManager = LinearLayoutManager(
                                    getContext(),
                                    LinearLayoutManager.VERTICAL, false
                                )
                                view!!.vechile_list!!.layoutManager = linearLayoutManager
                                view!!.vechile_list!!.adapter = VanBookingAdapter(
                                    context!!,
                                    GetVehicles, this@SelectVehicle,
                                    this@SelectVehicle, distance_in_miles
                                )


                                moveVehicelContinuously()


                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }
                    progressDialog!!.dismiss()


                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    progressDialog!!.dismiss()

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
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    private fun getInsurance() {
        progressDialog!!.show()

        val url =
            Utils.get_insurance_plans
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }

        val postParam: MutableMap<String?, Any?> =
            HashMap()
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.GET,
                url, JSONObject(postParam),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                  /*  {"status":{"code":"1000","message":"Success"},
                        "data":[{"plan_name":"I have home content insurance","plan_price":"0"},
                        {"plan_name":"Extra coverage upto £5,000 costs £10.00","plan_price":"10"},
                        {"plan_name":"Extra coverage upto £10,000 costs £20.00","plan_price":"20"}]}
*/
                    val result = jsonObject.toString()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            insuranceList =ArrayList()
                            val jsonObjList=jsonObject.getJSONArray("data")
                            val listLenth=jsonObjList.length()-1
                            for (i in 0..listLenth) {
                                var jsonObject=jsonObjList.get(i) as JSONObject

                              //  jsonObject.put("is_select",false)
                                insuranceList!!.add(InsurancePlan(jsonObject.getString("plan_name"),jsonObject.getString("plan_price"),false))
                            }

                            OpenInsurance(insuranceList!!)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }
                    progressDialog!!.dismiss()

                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    progressDialog!!.dismiss()

                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                 //   headers["registration_id"] = RegistrationID!!
                  //  headers["passenger_id"] = passenger_id!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }
    private fun moveVehicelContinuously(

    ) {
        val handler = Handler()
        timer = Timer()
        doAsynchronousTaskGetDriver = object : TimerTask() {
            override fun run() {
                handler.post {
                    GetRealTime()

                }
            }
        }
        timer!!.schedule(doAsynchronousTaskGetDriver, 0, 5000)
    }

    private fun GetRealTime() {

        val url =
            Utils.get_vehicle_classes_passenger_all_Url
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val pickup_lat = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )!!.toDouble()
        val pickup_lng = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            context!!
        )!!.toDouble()
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["latitude"] = pickup_lat
        postParam["longitude"] = pickup_lng
        postParam["helpers_count"] = "0"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    val result = jsonObject.toString()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val first = JSONObject(result)
                            val JA = first.getJSONArray("data")
                            try {
                                for (i in 0 until JA.length()) {
                                    val jsonData = JA.getJSONObject(i)

                                    val jsonArray_drivers =
                                        jsonData.getJSONArray("drivers")
                                    if (jsonArray_drivers.length() > 0) {
                                        getNearestTime = 0
                                    }
                                    var minute: Int? = null
                                    for (i2 in 0 until jsonArray_drivers.length()) {
                                        val jsonData2 =
                                            jsonArray_drivers.getJSONObject(i2)
                                        driver_id = jsonData2.getString("driver_id")
                                        vehicle_class_id =
                                            jsonData2.getString("vehicle_class_id")
                                        vehicle_class_name =
                                            jsonData2.getString("vehicle_class_name")
                                        latitude_driver = jsonData2.getString("latitude")
                                        longitude_driver = jsonData2.getString("longitude")
                                        val distance_driver = jsonData2.getString("distance")
                                        if (getNearestTime == 0) {
                                            try {
                                                val distance = distance_driver.toDouble()
                                                val seconds = distance * 1000 / 6.8
                                                //6.8 m/s is an assume speed of driver
                                                minute = seconds.toInt() / 60
                                                getNearestTime = 1
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }


                                    val old_driver = GetVehicles.get(i)
                                    old_driver.latitude_driver = latitude_driver
                                    old_driver.longitude_driver = longitude_driver
                                    old_driver.driver_time = minute
                                    GetVehicles.set(i, old_driver)
                                    view!!.vechile_list!!.adapter!!.notifyItemChanged(
                                        i,
                                        old_driver
                                    );

                                }

                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }

                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)

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
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "confirm_amount") {
         //  OpenInsurance(insuranceList!!)
            getInsurance()
        } else if (dialog_name == "vechile_not_found") {
            this.holder!!.view!!.book_later!!.performClick()
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}

    companion object {
        const val vachile_list_request_code = 89
        var insuranceList: ArrayList<InsurancePlan>? =null
    }

    override fun clickPositiveDialogButton(dialog_name: String?, dialog: DialogInterface?) {

    }
}