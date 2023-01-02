package com.vanmove.passesger.fragments.OfferFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CompoundButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.Dimension.AddDeminsion
import com.vanmove.passesger.activities.FullImage.ZoomImage2
import com.vanmove.passesger.activities.OfferConfirm.OfferConfirmActivity
import com.vanmove.passesger.activities.VechileList
import com.vanmove.passesger.adapters.Holder
import com.vanmove.passesger.adapters.InventoryAdaptor2
import com.vanmove.passesger.adapters.VanBookingAdapter2
import com.vanmove.passesger.interfaces.OnClickOneButtonAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.interfaces.VanSelectionInterface
import com.vanmove.passesger.model.DeminsionModel
import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithOneButtons
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DateTimePicker
import com.vanmove.passesger.utils.ShowAlertMessage
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.KmToMile
import com.vanmove.passesger.utils.Utils.calculateFareUpto
import com.vanmove.passesger.utils.Utils.distance_two_point
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.getStringImage
import com.vanmove.passesger.utils.Utils.hideKeyboard
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import kotlinx.android.synthetic.main.add_inventory.*
import kotlinx.android.synthetic.main.add_photo.*
import kotlinx.android.synthetic.main.alert_when.*
import kotlinx.android.synthetic.main.info_dialogue.*
import kotlinx.android.synthetic.main.info_dialogue.Cancel
import kotlinx.android.synthetic.main.inventoy_option.view.*
import kotlinx.android.synthetic.main.location_alert.*
import kotlinx.android.synthetic.main.location_alert.cancel
import kotlinx.android.synthetic.main.make_offer.view.*
import kotlinx.android.synthetic.main.selected_images_layout.view.*
import kotlinx.android.synthetic.main.sepcial_instuction_alert.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class MakeOfferFragment : Fragment(R.layout.make_offer), OnItemSelectedListener,
    View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
    VanSelectionInterface, OnItemClickRecycler, CompoundButton.OnCheckedChangeListener,
    OnClickOneButtonAlertDialog, OnClickTwoButtonsAlertDialog2 {


    var date_booking = ""
    var other_Stops: String? = null
    var instuction_sepcial: String? = null
    private var pickup_address: String? = null
    private var destination_address: String? = null
    private var helpers_count: String? = null
    private var offer_time_estimation: String? = null
    private var is_flexible = "No"
    private var isFromDate: String? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private var first_date: String? = null
    private var second_date: String? = null
    private var what_moving_: String? = null
    private var Quick_Inventory_: String? = null

    var distanceFromGoogle = ""
    var position = 0

    var vehicle_class_id: String? = null
    var currrent_vechile: GetVehicles? = null
    private var bitmap_img: Bitmap? = null
    var alertDialog: AlertDialog? = null
    var alertDialog_Picture: AlertDialog? = null
    var sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
    var c = Calendar.getInstance()
    var dialog: BottomSheetDialog? = null
    var select_image_position = 0
    var min_hour = 0.0
    var max_hour = 0.0
    private val listener: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            date_booking = sdf.format(date)
            try {
                when (isFromDate) {

                    "fixed_date" -> {
                        view!!.tv_when!!.text = date_booking
                    }

                    "first_date" -> {
                        first_date = date_booking
                        alertDialog!!.tv_first_date!!.text = first_date
                        c.time = date
                        c.add(Calendar.DATE, 1) // Adding 5 days
                        date_booking = sdf.format(c.time)
                        view!!.tv_when!!.text = first_date + " - " + second_date

                    }
                    "second_date" -> {
                        second_date = date_booking
                        alertDialog!!.tv_second_date!!.text = second_date
                        view!!.tv_when!!.text = first_date + " - " + second_date
                        savePreferences(
                            CONSTANTS.second_date,
                            second_date,
                            context!!
                        )
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savePreferences(
            CONSTANTS.second_date,
            "",
            context!!
        )
        savePreferences(
            CONSTANTS.str_inventry,
            "",
            context!!
        )
        savePreferences(
            CONSTANTS.str_inventry_detail,
            "",
            context!!
        )
        savePreferences(CONSTANTS.str_aditional_info, "", context!!)

        Utils.deminsionList.clear()
        Utils.deminsionList.add(DeminsionModel("", "", "", "", "", ""))

        Utils.getInventoryImagesList.clear()
        for (i in 0..5) {
            Utils.getInventoryImagesList.add("")
        }
        inilizeView()
        Utils.OnlyVansList.clear()
        for (i in Utils.GetVehicles.indices) {
            if (Utils.GetVehicles[i].type == "Van") {
                Utils.OnlyVansList.add(
                    Utils.GetVehicles[i]
                )
            }
        }
        linearLayoutManager = LinearLayoutManager(
            getContext(),
            LinearLayoutManager.HORIZONTAL, false
        )
        view.recyclerView!!.layoutManager = linearLayoutManager
        view.recyclerView!!.adapter = VanBookingAdapter2(
            activity!!,
            Utils.OnlyVansList, this@MakeOfferFragment,
            this@MakeOfferFragment
        )


        // move to seleted psoition
        view.recyclerView!!.viewTreeObserver
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    linearLayoutManager!!.scrollToPosition(position)
                    Handler().postDelayed({
                        val holder =
                            view.recyclerView!!.findViewHolderForAdapterPosition(position)
                        holder!!.itemView.performClick()
                    }, 300)
                    view.recyclerView!!.viewTreeObserver
                        .removeOnGlobalLayoutListener(this)
                    SpinnerDate()
                }
            })
        destination_address = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
            context!!
        )
        pickup_address = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            context!!
        )
        view.pickup_tv!!.text = pickup_address
        view.tv_desti!!.text = destination_address
        view.et_add_ammount!!.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(7, 2)))

        distanceFromGoogle = activity!!.intent.getStringExtra("distanceFromGoogle").toString()
        position = activity!!.intent.getIntExtra("position", 0)


        // Get Previous Date
        // Set in When for
        getPreferences(CONSTANTS.date_future_booking_str, context!!).let {
            if (!it!!.isEmpty()) {
               val dateTime=it+" "+ getPreferences(CONSTANTS.time_slots_future_booking_str, context!!)

                val date_ = Utils.changeDateFormat(
                    dateTime,
                    "yyyy-MM-dd HH:mm",
                    "yyyy/MM/dd hh:mm a"
                )
                date_booking = date_!!
                view.tv_when!!.text = date_booking


            }

        }

    }


    class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) :
        InputFilter {
        var mPattern: Pattern
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val matcher: Matcher = mPattern.matcher(dest)
            return if (!matcher.matches()) "" else null
        }

        init {
            mPattern =
                Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
        }
    }

    private fun inilizeView() {

        view!!.btn_request.setOnClickListener(this)
        view!!.tv_when.setOnClickListener(this)
        view!!.iv_move_previous.setOnClickListener(this)
        view!!.iv_move_next.setOnClickListener(this)
        view!!.vechile_list.setOnClickListener(this)
        view!!.tv_stops.setOnClickListener(this)
        view!!.spcial_instruction.setOnClickListener(this)
        view!!.add_inventory.setOnClickListener(this)
        view!!.iv_callendar.setOnClickListener(this)
        view!!.privacy_policy.setOnClickListener(this)
        view!!.tv_payment.setOnClickListener(this)
        view!!.fare_breakdown.setOnClickListener(this)
        view!!.term_condition.setOnClickListener(this)
        view!!.accept_book.setOnClickListener(this)
        view!!.Need_dismantling_spinner.setOnClickListener(this)
        view!!.spinner_helper.setOnClickListener(this)
        view!!.Select_estimated_duration_spinner.setOnClickListener(this)
        view!!.add_invetory.setOnClickListener {
            Open_Picture_Dialog()
        }

        view!!.cb_flexible.setOnCheckedChangeListener(this@MakeOfferFragment)
    }

    private fun LaodServies() {
        val helpers = getPreferences(CONSTANTS.helpers, context!!)
        val estimated_duartion = getPreferences(CONSTANTS.estimated_duartion, context!!)
        view!!.services_name!!.text =
            currrent_vechile!!.vehicle_name + " | " + helpers + " " + "helper(s) | " + estimated_duartion
    }

    private fun SpinnerDate() {


        val estimated_duartion =
            getPreferences(CONSTANTS.estimated_duartion, context!!)
        val helpers =
            getPreferences(CONSTANTS.helpers, context!!)
        for (i in 0 until CONSTANTS.estimated_duration_list.size) {
            if (estimated_duartion == CONSTANTS.estimated_duration_list[i]) {
                view!!.Select_estimated_duration_spinner!!.setText(CONSTANTS.estimated_duration_list[i])
            }
        }
        for (i in 0 until CONSTANTS.helper_list.size) {
            if (helpers == i.toString()) {
                view!!.spinner_helper!!.setText(CONSTANTS.helper_list[i + 1])
            }
        }
        Fare_Estimation(estimated_duartion)

    }

    @SuppressLint("SetTextI18n")
    private fun Fare_Estimation(time: String?) {
        if (!distanceFromGoogle.isEmpty()) {
            if (time == CONSTANTS.estimated_duration_list[1]) {
                min_hour = 1.0
                max_hour = 1.0
                val fare = calculateFareUpto(
                    min_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + fare
            } else if (time == CONSTANTS.estimated_duration_list[2]) {
                min_hour = 1.0
                max_hour = 2.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[3]) {
                min_hour = 2.0
                max_hour = 3.0
                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[4]) {
                min_hour = 3.0
                max_hour = 4.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[5]) {
                min_hour = 4.0
                max_hour = 5.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[6]) {
                min_hour = 5.0
                max_hour = 6.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[7]) {
                min_hour = 6.0
                max_hour = 7.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[8]) {
                min_hour = 7.0
                max_hour = 8.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[9]) {
                min_hour = 8.0
                max_hour = 9.0

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            } else if (time == CONSTANTS.estimated_duration_list[10]) {
                min_hour = 9.0
                max_hour = 10.0
                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            }
        }
    }

    override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.Need_dismantling_spinner -> {
                setdimatchingVales(v)
            }
            R.id.spinner_helper -> {
                showhelper(v)
            }
            R.id.Select_estimated_duration_spinner -> {
                DurationMenu(v)
            }
            R.id.btn_request -> {
                Validition()
            }
           /* R.id.iv_callendar, R.id.tv_when -> alertDatesOptions()*/


            R.id.iv_move_previous -> view!!.recyclerView!!.smoothScrollBy(-300, -300)
            R.id.iv_move_next -> view!!.recyclerView!!.smoothScrollBy(300, 300)
            R.id.vechile_list -> startActivityForResult(
                Intent(activity, VechileList::class.java)
                , vachile_list_request_code
            )
            R.id.rel_one_week -> {
                savePreferences(
                    CONSTANTS.second_date,
                    "",
                    context!!
                )
                c.time = Date()
                c.add(Calendar.DATE, 7)
                date_booking = sdf.format(c.time)
                alertDialog!!.dismiss()
                view!!.tv_when!!.text = "Within One Week"
            }
            R.id.rel_on_fixed_date -> {
                savePreferences(
                    CONSTANTS.second_date,
                    "",
                    context!!
                )
                isFromDate = "fixed_date"
                DateTimePicker.ShowDatePicker(
                    activity!!.supportFragmentManager,
                    listener
                )
                Utils.showToastLong(getString(R.string.job_time))
                alertDialog!!.dismiss()
            }
            R.id.rel_urgently -> {
                savePreferences(
                    CONSTANTS.second_date,
                    "",
                    context!!
                )
                c.time = Date()
                c.add(Calendar.DATE, 0)
                date_booking = sdf.format(c.time)
                view!!.tv_when!!.text = "Urgently"
                alertDialog!!.dismiss()
            }
            R.id.rel_between_dates -> {
                alertDialog!!.iv_between_dates!!.setBackgroundResource(R.drawable.arrow_down)
                alertDialog!!.rel_between_dates2!!.visibility = View.VISIBLE
            }
            R.id.privacy_policy -> Open_Chrome(CONSTANTS.privacy)
            R.id.term_condition -> Open_Chrome(CONSTANTS.term_condition)
            R.id.tv_first_date -> {
                isFromDate = "first_date"
                DateTimePicker.ShowDatePicker(
                    activity!!.supportFragmentManager,
                    listener
                )
            }
            R.id.tv_second_date -> {
                isFromDate = "second_date"
                DateTimePicker.ShowDatePicker(
                    activity!!.supportFragmentManager,
                    listener
                )
            }
            R.id.rel_dont_date -> {
                savePreferences(
                    CONSTANTS.second_date,
                    "",
                    context!!
                )
                alertDialog!!.dismiss()
                c.time = Date()
                c.add(Calendar.DATE, 365)
                date_booking = sdf.format(c.time)
                view!!.tv_when!!.text = "No Date Yet"
                alertDialog!!.dismiss()
            }
            R.id.Cancel -> alertDialog!!.dismiss()
            R.id.gallery -> {
                dialog!!.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    check_gallery_permission()
                } else {
                    opne_gallery()
                }
            }
            R.id.camera -> {
                dialog!!.dismiss()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    check_camera_permission()
                } else {
                    open_camera_intent()
                }
            }

            R.id.tv_stops -> aditionalStopsAlert()
            R.id.spcial_instruction -> Spceial_Instruction_Dialog()


            R.id.add_inventory -> add_inventory_Dialog()
            R.id.what_moving -> {
                ShowServiceList(v)
            }
            R.id.add_Demensions -> startActivity(Intent(context, AddDeminsion::class.java))
            R.id.add_photo -> Open_Picture_Dialog()
            R.id.Cancel_Picture -> alertDialog_Picture!!.dismiss()
            R.id.fare_breakdown -> showAlertMessageWithOneButtons(
                context,
                this@MakeOfferFragment,
                "fare_estimated_guide", "Fare Guide",
                "Enter the maximum offer you are prepared to pay for your move",
                "Ok"
            )
            R.id.accept_book -> activity!!.finish()
            R.id.tv_payment -> showAlertMessageWithOneButtons(
                context,
                this@MakeOfferFragment,
                "fare_estimated_guide", "Fare Guide",
                " You have estimated your job would take  ${view!!.Select_estimated_duration_spinner.text} , hence the guide price of " + view!!.tv_approximated_fare!!.text
                    .toString(),
                "Ok"
            )
        }
    }

    private fun DurationMenu(v: View) {

        val popup = PopupMenu(context, v)
        for (title in CONSTANTS.estimated_duration_list) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            val title = item.title.toString()
            view!!.Select_estimated_duration_spinner!!.text = title
            offer_time_estimation = title
            Fare_Estimation(offer_time_estimation)
            savePreferences(
                CONSTANTS.estimated_duartion,
                offer_time_estimation,
                context!!
            )
            LaodServies()

            true
        }
        popup.show()


    }

    private fun setdimatchingVales(v: View) {

        val popup = PopupMenu(context, v)
        for (title in CONSTANTS.Need_dismantling_List) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            val title = item.title.toString()
            view!!.Need_dismantling_spinner!!.text = title

            savePreferences(
                CONSTANTS.dismantling,
                title,
                context!!
            )
            if (title == CONSTANTS.Need_dismantling_List[0]) {
                CONSTANTS.dismantlingRate = 0.0
            } else if (title == CONSTANTS.Need_dismantling_List[1]) {
                CONSTANTS.dismantlingRate = 15.00
            } else if (title == CONSTANTS.Need_dismantling_List[2]) {
                CONSTANTS.dismantlingRate = 0.0
            }
            if (min_hour != 0.0 && max_hour != 0.0) {
                if (min_hour == 1.0 && max_hour == 1.0) {
                    val fare =
                        calculateFareUpto(
                            min_hour,
                            currrent_vechile!!,
                            distanceFromGoogle,
                            context!!
                        )
                    view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + fare
                } else {

                    val far2 =
                        calculateFareUpto(
                            max_hour,
                            currrent_vechile!!,
                            distanceFromGoogle,
                            context!!
                        )
                    view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2
                }
            }
            true
        }
        popup.show()


    }

    private fun showhelper(v: View) {

        val popup = PopupMenu(context, v)
        for (title in CONSTANTS.helper_list) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            val title = item.title.toString()
            view!!.spinner_helper!!.text = title


            if (title == CONSTANTS.helper_list[0]) {
                helpers_count = "0"
            } else if (title == CONSTANTS.helper_list[1]) {
                helpers_count = "0"
            } else if (title == CONSTANTS.helper_list[2]) {
                helpers_count = "1"
            } else if (title == CONSTANTS.helper_list[3]) {
                helpers_count = "2"
            } else if (title == CONSTANTS.helper_list[4]) {
                helpers_count = "3"
            }
            savePreferences(
                CONSTANTS.helpers,
                helpers_count,
                context!!
            )
            if (min_hour != 0.0 && max_hour != 0.0) {
                if (min_hour == 1.0 && max_hour == 1.0) {
                    val fare =
                        calculateFareUpto(
                            min_hour,
                            currrent_vechile!!,
                            distanceFromGoogle,
                            context!!
                        )
                    view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + fare
                } else {

                    val far2 =
                        calculateFareUpto(
                            max_hour,
                            currrent_vechile!!,
                            distanceFromGoogle,
                            context!!
                        )
                    view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2
                }
            }
            LaodServies()

            true
        }
        popup.show()


    }

    private fun Validition() {

        val amount = view!!.et_add_ammount!!.text.toString()

        if (TextUtils.isEmpty(pickup_address)) {
            showToast(
                "Please select pickup address"
            )
        } else if (TextUtils.isEmpty(destination_address)) {
            showToast(
                "Please Select destination address"
            )
        } else if (TextUtils.isEmpty(date_booking) && is_flexible.equals(
                "No"
            )
        ) {
            showToast(
                "Please select booking date"
            )
        } else if (helpers_count == CONSTANTS.helper_list[0]) {
            showToast("Please Select helpers")
        } else if (view!!.Select_estimated_duration_spinner.text.contains("Select estimated duration")) {
            showToast(
                "Select estimated duration"
            )
        } else if (TextUtils.isEmpty(amount)) {
            showToast("Please add offer amount")
        } else if (amount.toDouble() == 0.toDouble()) {
            showToast(
                "Offer amount must be graeter than zero"
            )
        } else {
            savePreferences(
                CONSTANTS.estimated_payment,
                view!!.tv_approximated_fare!!.text.toString(),
                context!!
            )

            savePreferences(
                CONSTANTS.is_flexible,
                is_flexible,
                context!!
            )
            savePreferences(
                CONSTANTS.date_booking,
                date_booking,
                context!!
            )
            savePreferences(
                CONSTANTS.str_inventry,
                what_moving_,
                context!!
            )
            savePreferences(
                CONSTANTS.str_inventry_detail,
                Quick_Inventory_,
                context!!
            )
            savePreferences(
                CONSTANTS.offered_amount,
                String.format("%.2f", amount.toDouble()),
                context!!
            )
            startActivity(Intent(context, OfferConfirmActivity::class.java))
        }
    }

    private fun ShowServiceList(v: View) {
        val popup = PopupMenu(context, v)
        for (title in Utils.GetServicesList) {
            popup.menu.add(title.name)
        }
        popup.setOnMenuItemClickListener { item ->
            alertDialog!!.what_moving!!.text = item.title.toString()
            true
        }
        popup.show()
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

    private fun add_inventory_Dialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.add_inventory)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()

        if (!TextUtils.isEmpty(Quick_Inventory_)) {
            alertDialog!!.Quick_Inventory!!.setText(Quick_Inventory_)
        }
        if (!TextUtils.isEmpty(what_moving_)) {
            alertDialog!!.what_moving!!.setText(what_moving_)
        }
        alertDialog!!.what_moving!!.setOnClickListener(this)
        alertDialog!!.close_inventory!!.setOnClickListener {
            alertDialog!!.Quick_Inventory.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.done_invenotry!!.setOnClickListener {
            what_moving_ = alertDialog!!.what_moving!!.text.toString()
            Quick_Inventory_ = alertDialog!!.Quick_Inventory!!.text.toString()
            alertDialog!!.Quick_Inventory.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.add_Demensions!!.setOnClickListener(this)
        alertDialog!!.add_photo!!.setOnClickListener(this)

    }

    private fun Spceial_Instruction_Dialog() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.sepcial_instuction_alert)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()
        if (!TextUtils.isEmpty(instuction_sepcial)) {
            alertDialog!!.special_instructions_tv!!.setText(instuction_sepcial)
        }


        alertDialog!!.Submit_instruction!!.setOnClickListener {

            instuction_sepcial = alertDialog!!.special_instructions_tv!!.text.toString()
            savePreferences(
                CONSTANTS.str_aditional_info,
                instuction_sepcial,
                context!!
            )
            alertDialog!!.special_instructions_tv!!.hideKeyboard()

            alertDialog!!.dismiss()


        }
        alertDialog!!.cancel!!.setOnClickListener {
            alertDialog!!.special_instructions_tv!!.hideKeyboard()
            alertDialog!!.dismiss()
        }
    }

    private fun aditionalStopsAlert() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.location_alert)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()
        if (!TextUtils.isEmpty(other_Stops)) {
            alertDialog!!.extra_steps!!.setText(other_Stops)
        }
        alertDialog!!.Submit!!.setOnClickListener {
            other_Stops = alertDialog!!.extra_steps!!.text.toString()
            alertDialog!!.extra_steps.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.cancel!!.setOnClickListener {
            alertDialog!!.extra_steps.hideKeyboard()
            alertDialog!!.dismiss()
        }
    }

    private fun open_botton_sheet() {
        val view = layoutInflater.inflate(R.layout.selected_images_layout, null)
        dialog = BottomSheetDialog(context!!)
        dialog!!.setContentView(view)
        dialog!!.show()
        view.gallery.setOnClickListener(this)
        view.camera.setOnClickListener(this)
        view.Cancel_2.setOnClickListener { dialog!!.dismiss() }
    }

    private fun check_camera_permission() {
        val Camera = Check_Camera()
        val READ = Check_READ_EXTERNAL_STORAGE()
        val WRITE = Check_WRITE_EXTERNAL_STORAGE()
        if (!Camera
            || !READ || !WRITE
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                67
            )
        } else {
            open_camera_intent()
        }
    }

    private fun Check_WRITE_EXTERNAL_STORAGE(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Check_READ_EXTERNAL_STORAGE(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Check_Camera(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun check_gallery_permission() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                5454
            )
        } else {
            opne_gallery()
        }
    }

    private fun opne_gallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            67 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                open_camera_intent()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            5454 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                opne_gallery()
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun open_camera_intent() {
        val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera_intent, REQUEST_CODE_CAMERA)
    }

    private fun Open_Picture_Dialog() {
        val builder =
            AlertDialog.Builder(activity!!)
        builder.setView(R.layout.add_photo)
        alertDialog_Picture = builder.create()

        alertDialog_Picture!!.run {
            show()
            Cancel_Picture!!.setOnClickListener {
                dismiss()
            }
            inventory_rcv!!.adapter = InventoryAdaptor2(
                context, Utils.getInventoryImagesList,
                this@MakeOfferFragment
            )
        }


    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                if (requestCode == 31) {
                    pickup_address = intent.getStringExtra("pickup")
                    val pickup_latitude = intent.getStringExtra("latitude")
                    val pickup_longitude = intent.getStringExtra("longitude")
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA
                        , pickup_address,
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA
                        , pickup_latitude,
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA
                        , pickup_longitude,
                        context!!
                    )
                    view!!.pickup_tv!!.text = pickup_address
                    Distance_Calculating()
                } else if (requestCode == 32) {
                    destination_address = intent.getStringExtra("pickup")
                    val destination_latitude = intent.getStringExtra("latitude")
                    val destination_longitude = intent.getStringExtra("longitude")
                    view!!.tv_desti!!.text = destination_address
                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA
                        , destination_address,
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA
                        , destination_latitude,
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA
                        , destination_longitude,
                        context!!
                    )
                    Distance_Calculating()
                } else if (requestCode == REQUEST_CODE_GALLERY) {
                    val image_path = intent.data
                    try {
                        bitmap_img = MediaStore.Images.Media.getBitmap(
                            activity!!.contentResolver,
                            image_path
                        )
                        val base_64_image =
                            getStringImage(bitmap_img!!)
                        Calulating_Image_Selected()
                        Calulating_Image_Selected()
                        Utils.getInventoryImagesList[select_image_position] = base_64_image
                        alertDialog_Picture!!.inventory_rcv!!.adapter!!.notifyDataSetChanged()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (requestCode == REQUEST_CODE_CAMERA) {
                    bitmap_img = intent.extras!!["data"] as Bitmap?
                    val base_64_image =
                        getStringImage(bitmap_img!!)
                    Calulating_Image_Selected()
                    Utils.getInventoryImagesList[select_image_position] = base_64_image
                    alertDialog_Picture!!.inventory_rcv!!.adapter!!.notifyDataSetChanged()
                } else if (requestCode == vachile_list_request_code) {
                    val vechile_position = intent.getIntExtra("vechile_position", 0)
                    linearLayoutManager!!.scrollToPosition(vechile_position)
                    Handler().postDelayed({
                        val holder =
                            view!!.recyclerView!!.findViewHolderForAdapterPosition(vechile_position)
                        holder!!.itemView.performClick()
                    }, 300)
                }
            }
        }
    }

    private fun Distance_Calculating() {
        val start_point_latitude = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )
        val start_point_longitude =
            getPreferences(
                CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
                context!!
            )
        val end_point_latitude = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
            context!!
        )
        val end_point_longitude = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
            context!!
        )
        val distance_two_point = distance_two_point(
            LatLng(start_point_latitude!!.toDouble(), start_point_longitude!!.toDouble()),
            LatLng(end_point_latitude!!.toDouble(), end_point_longitude!!.toDouble())
        )
        distanceFromGoogle = KmToMile("" + distance_two_point)
    }

    private fun Calulating_Image_Selected() {
        var total_size = 0
        Utils.getInventoryImagesList.forEach {
            if (!it.isEmpty()) {
                total_size = total_size + 1
            }
        }
        if (total_size == 1) {
            view!!.total_images_size!!.text = "$total_size image selected."
        } else {
            view!!.total_images_size!!.text = "$total_size images selected."
        }
    }

    override fun onResume() {
        super.onResume()
        Calulating_Image_Selected()
    }


    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        showToast(connectionResult.errorMessage)
    }

    private fun alertDatesOptions() {
        val builder =
            AlertDialog.Builder(activity!!)
        builder.setView(R.layout.alert_when)
        alertDialog = builder.create()
        val window = alertDialog!!.window
        val wlp = window!!.attributes
        wlp.gravity = Gravity.BOTTOM
        wlp.gravity = Gravity.RIGHT
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
        alertDialog!!.show()
        alertDialog!!.Cancel!!.setOnClickListener { alertDialog!!.dismiss() }

        alertDialog!!.rel_one_week!!.setOnClickListener(this@MakeOfferFragment)
        alertDialog!!.rel_on_fixed_date!!.setOnClickListener(this@MakeOfferFragment)
        alertDialog!!.rel_urgently!!.setOnClickListener(this@MakeOfferFragment)
        alertDialog!!.rel_between_dates!!.setOnClickListener(this@MakeOfferFragment)
        alertDialog!!.tv_first_date!!.setOnClickListener(this@MakeOfferFragment)
        alertDialog!!.tv_second_date!!.setOnClickListener(this@MakeOfferFragment)
        alertDialog!!.rel_dont_date!!.setOnClickListener(this@MakeOfferFragment)
    }


    override fun onClickRecycler(view: View?, position: Int) {

        if (view!!.id == R.id.iv_vehicle_info) {
            val builder = AlertDialog.Builder(context!!)
            builder.setView(R.layout.info_dialogue)
            val alert_dialog = builder.create()
            alert_dialog.run {
                setCancelable(true)
                setCanceledOnTouchOutside(true)
                show()

                Utils.OnlyVansList[position].run {
                    tv_name!!.text = vehicle_name
                    Usage!!.text = info
                    Capacity!!.text = "Capacity: " + capacity
                    Max_Load!!.text = "Max Load: " + weight
                    External_DIMESIONS!!.text = "External: " + external_dimension
                    Internal_DIMESIONS!!.text = "Internal: " + internal_dimension
                    tv_rate_per_mile!!.text =
                        "Rate per mile: : " + CONSTANTS.CURRENCY + rate_per_mile_fare
                    tv_mint_rate!!.text =
                        "Rate per minute: " + CONSTANTS.CURRENCY + rate_per_minute_fare
                    tv_hourly_rate!!.text =
                        "Rate per hour: : " + CONSTANTS.CURRENCY + hourly_rate
                    tv_helper_rate!!.text =
                        "Helper rate per hour: " + CONSTANTS.CURRENCY + rate_helper_hourly
                    tv_helper_rate_per_minut!!.text = "Helper rate per minute: " +
                            CONSTANTS.CURRENCY + rate_helper_per_minute
                }

                Cancel!!.setOnClickListener { dismiss() }
            }

        } else {
            select_image_position = position
            if (Utils.getInventoryImagesList[select_image_position].isEmpty()) {
                open_botton_sheet()
            } else {
                inventoy_option()
            }
        }

    }

    private fun inventoy_option() {
        val view = layoutInflater.inflate(R.layout.inventoy_option, null)
        dialog = BottomSheetDialog(requireContext())

        dialog!!.run {

            view.run {
                setContentView(this)
                show()
                edit_inventory.setOnClickListener {
                    dismiss()
                    open_botton_sheet()
                }

                delete_inventory.setOnClickListener {
                    dismiss()
                    ShowAlertMessage.showAlertMessageTwoButtons(
                        context, "Delete Inventory", getString(R.string.delete_inventory),
                        "Yes", "No",
                        "Delete_Inventory",
                        this@MakeOfferFragment
                    )
                }
                View_inventory.setOnClickListener {
                    dismiss()

                    startActivity(
                        Intent(activity, ZoomImage2::class.java)
                            .putExtra("select_image_position", select_image_position)
                    )
                }
                Cancel.setOnClickListener { dismiss() }
            }

        }

    }

    override fun onCheckedChanged(
        buttonView: CompoundButton,
        isChecked: Boolean
    ) {
        if (view!!.cb_flexible!!.isChecked) {
            is_flexible = "Yes"
            view!!.tv_when!!.isEnabled = false

        } else {
            is_flexible = "No"
            view!!.tv_when!!.isEnabled = true
        }
    }

    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialog: DialogInterface?
    ) {

        when (dialog_name) {
            "fare_estimated_guide" -> dialog!!.dismiss()
            "Delete_Inventory" -> {
                Utils.getInventoryImagesList[select_image_position] = ""
                alertDialog_Picture!!.inventory_rcv!!.adapter!!.notifyDataSetChanged()

            }

        }

    }

    override fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        when (dialog_name) {

        }
    }

    override fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        when (dialog_name) {

        }
    }

    override fun onVanImageClicking(
        vehicle_class_id: String?, vehicles: GetVehicles?,
        holder: Holder?, position: Int
    ) {
        this.vehicle_class_id = vehicle_class_id
        currrent_vechile = vehicles
        if (min_hour != 0.0 && max_hour != 0.0) {
            if (min_hour == 1.0 && max_hour == 1.0) {
                val fare = calculateFareUpto(
                    min_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + fare
            } else {

                val far2 = calculateFareUpto(
                    max_hour,
                    currrent_vechile!!,
                    distanceFromGoogle,
                    context!!
                )
                view!!.tv_approximated_fare!!.text = CONSTANTS.CURRENCY + far2

            }

        }
        LaodServies()
    }

    companion object {
        const val REQUEST_CODE_GALLERY = 8000
        const val REQUEST_CODE_CAMERA = 7000
        const val vachile_list_request_code = 89
    }
}