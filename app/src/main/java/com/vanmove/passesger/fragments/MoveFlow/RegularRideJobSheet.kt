package com.vanmove.passesger.fragments.MoveFlow

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.AdvancedPayment.AdvancedPaymentActivity
import com.vanmove.passesger.activities.Dimension.AddDeminsion
import com.vanmove.passesger.activities.FullImage.ZoomImage2
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.adapters.InventoryAdaptor2
import com.vanmove.passesger.fragments.MoveHistory.BookedMovesMain
import com.vanmove.passesger.fragments.OfferFlow.MakeOfferFragment
import com.vanmove.passesger.interfaces.OnAlertDialogButtonClicks
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.DeminsionModel
import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.*
import com.vanmove.passesger.utils.Utils.calculateFareUpto
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getMinMax
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.hideKeyboard
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.activity_vechile_all.view.*
import kotlinx.android.synthetic.main.add_inventory.*
import kotlinx.android.synthetic.main.add_photo.*
import kotlinx.android.synthetic.main.alert_insurance.*
import kotlinx.android.synthetic.main.alert_property_type.*
import kotlinx.android.synthetic.main.fragment_job_sheet.*
import kotlinx.android.synthetic.main.fragment_job_sheet.view.*
import kotlinx.android.synthetic.main.inventoy_option.view.*
import kotlinx.android.synthetic.main.location_alert.*
import kotlinx.android.synthetic.main.location_alert.cancel
import kotlinx.android.synthetic.main.selected_images_layout.view.*
import kotlinx.android.synthetic.main.sepcial_instuction_alert.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegularRideJobSheet : Fragment(R.layout.fragment_job_sheet),
    View.OnClickListener, OnItemSelectedListener, OnTouchListener,
    OnClickTwoButtonsAlertDialog2, OnAlertDialogButtonClicks, OnClickTwoButtonsAlertDialog,
    OnItemClickRecycler {
    var pickup_address: String? = null
    var insurance: String? = null
    var insurance_charge = "0.0"
    var vehicle_name: String? = null
    var rate_per_mile: String? = null
    var rate_per_minute: String? = null
    var rate_per_hour: String? = null
    var currency: String? = null
    var helpers: String? = null
    var passenger_id: String? = null
    var destination_address: String? = null
    var rate_per_helper_hour: String? = null
    var rate_per_helper_per_minute: String? = null
    private var str_property_pickup: String? = null
    private var property_type_droppoff: String? = null
    private var pickup_floor: String? = null
    private var congestion_charge: String? = null
    private var ulez_charge: String? = null
    var special_instructions: String? = ""
    var pickup_lat: String? = null
    var pickup_lon: String? = null
    var destination_lat: String? = null
    var destination_lon: String? = null
    var RegistrationID: String? = null
    private var vehicle_class_id: String? = null
    private var date_future_booking_str: String? = null
    private var time_slots_future_booking_str: String? = null
    private var other_Stops: String? = ""
    private var latitude = 0.0
    private var longitude = 0.0
    private var bookingRequestId = ""

    var alertDialog: AlertDialog? = null
    var ispickup = false

    private var Upto_hour_double = 0.0
    private var fname: String? = null
    private var lname: String? = null

    private var dropoff_floor: String? = ""
    private var pickup_lift: String? = ""
    private var dropoff_lift: String? = ""
    var estimated_duartion: String? = ""
    var estimated_payment: String? = ""

    var total_estimated_payment: Double = 0.0
    var total_pickup_floor_estimated_payment: Double = 0.0
    var total_drop_floore_estimated_payment: Double = 0.0
    var total_congnigetion_stimated_payment: Double = 0.0
    var total_ulez_stimated_payment: Double = 0.0




    var pay_by: String? = ""
    var porter_count = ""
    var onlinePorterCount = ""
    private var service_id: String? = null
    private var date_current: Date? = null
    var is_pickup_property = false
    private var progressDialog: ProgressDialog? = null
    private var what_moving_: String = ""
    private var Quick_Inventory_: String = ""

    var alertDialog_Picture: AlertDialog? = null
    var dialog: BottomSheetDialog? = null
    var select_image_position = 0
    private var bitmap_img: Bitmap? = null

    var send_furture_request: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_address.setOnClickListener(this)
        edit_address2.setOnClickListener(this)



        linkViews()
    }


    @SuppressLint("SetTextI18n")
    private fun linkViews() {

        Utils.deminsionList.clear()
        Utils.deminsionList.add(DeminsionModel("", "", "", "", "", ""))

        Utils.getInventoryImagesList.clear()
        for (i in 0..5) {
            Utils.getInventoryImagesList.add("")
        }

        val gps = GPSTracker(activity, context)
        if (gps.canGetLocation()) {
            latitude = gps.latitude
            longitude = gps.longitude
        } else {
            gps.showSettingsAlert()
        }
        date_future_booking_str = getPreferences(
            CONSTANTS.date_future_booking_str,
            context!!
        )
        time_slots_future_booking_str= getPreferences(CONSTANTS.time_slots_future_booking_str,context!!)
        val str_phone = getPreferences(CONSTANTS.mobile_passenger, context!!)
        val country_code = getPreferences(CONSTANTS.country_code, context!!)
        fname = getPreferences(CONSTANTS.first_name, context!!)
        lname = getPreferences(CONSTANTS.last_name, context!!)
        passenger_id = getPreferences(CONSTANTS.passenger_id, context!!)
        RegistrationID = getPreferences(CONSTANTS.RegistrationID, context!!)

        pickup_address = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            context!!
        )
        destination_address = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
            context!!
        )
        insurance = getPreferences(CONSTANTS.insurance, context!!)
        insurance_charge= getPreferences(CONSTANTS.insurance_fare, context!!)!!
        helpers = getPreferences(CONSTANTS.helpers, context!!)
        vehicle_name =
            getPreferences(CONSTANTS.vehicle_name, context!!)
        rate_per_mile =
            getPreferences(CONSTANTS.rate_per_mile, context!!)
        rate_per_minute =
            getPreferences(CONSTANTS.rate_per_minute, context!!)
        rate_per_hour =
            getPreferences(CONSTANTS.rate_per_hour, context!!)
        rate_per_helper_hour = getPreferences(
            CONSTANTS.rate_per_helper_hour,
            context!!
        )
        rate_per_helper_per_minute = getPreferences(
            CONSTANTS.rate_per_helper_per_minute,
            context!!
        )
        currency = CONSTANTS.CURRENCY

        tv_requestVan.setOnClickListener(this)
        tv_additional_info_value.setOnClickListener(this)
        tv_proprty_type_pickup.setOnClickListener(this)
        tv_proprty_type_dropoff.setOnClickListener(this)
        pickup_floor_spinner.setOnClickListener(this)
        dropoff_floor_spinner.setOnClickListener(this)
        pickup_lift_spinner.setOnClickListener(this)
        dropoff_lift_spinner.setOnClickListener(this)

        congestion_charge_tv.setOnClickListener(this)
        ulez_tv.setOnClickListener(this)


        view!!.add_inventory.setOnClickListener(this)
        view!!.iv_camera.setOnClickListener(this)

        Need_dismantling_spinner.setOnClickListener(this)
       val insurance_plan = getPreferences(CONSTANTS.insurance_plan, context!!)
        tv_additional_info_value.setText(insurance_plan)
        estimated_duartion = getPreferences(CONSTANTS.estimated_duartion, context!!)
        estimated_payment = Fare_Estimation(estimated_duartion, CONSTANTS.vehicles!!)

        total_estimated_payment=(estimated_payment!!.replace(CONSTANTS.CURRENCY,""))!!.toDouble()

        Estimated.setText("$estimated_duartion $estimated_payment")
        val result = getMinMax(estimated_duartion!!)
        if (result[1] == 1.00) {
            Upto_hour_title.setText("Price per hour after 1st hour -")
            After_Hour_title.setText("After 1st hour -")
        } else if (result[1] == 2.00) {
            Upto_hour_title.setText("Price per hour after 2nd hour -")
            After_Hour_title.setText("After 2nd hour -")
        } else if (result[1] == 3.00) {
            Upto_hour_title.setText("Price per hour after 3rd hour -")
            After_Hour_title.setText("After 3rd hour -")
        } else {
            Upto_hour_title.setText("Price per hour after " + result[1] + "th hour -")
            After_Hour_title.setText("After " + result[1] + "th hour -")
        }
        val after_first_hour_ =
            rate_per_minute!!.toDouble() + helpers!!.toDouble() * rate_per_helper_per_minute!!.toDouble()
        After_Hour.setText(currency + CONSTANTS.decimalFormat(after_first_hour_) + "/min")
        Upto_hour_double =
            rate_per_hour!!.toDouble() + helpers!!.toDouble() * rate_per_helper_hour!!.toDouble()
        Upto_hour.setText(currency + String.format("%.2f", Upto_hour_double))
   /*     tv_Agreed_price.setText(
            "$currency$rate_per_hour/h or $currency$rate_per_minute/ min + $currency$rate_per_mile per mile"
        )*/
        tv_Agreed_price.setText(
            "$currency$rate_per_hour/h + $currency$rate_per_mile per mile"
        )
     /*   Helper_Rate.setText(
            currency + rate_per_helper_hour + "/h or "
                    + currency + rate_per_helper_per_minute + "/m"
        )*/
        Helper_Rate.setText(currency + rate_per_helper_hour + "/h")
        tv_requestVan.setText("Request $vehicle_name")
        if (helpers.equals("0", ignoreCase = true)) {
            tv_service_type.text = "$vehicle_name - $helpers Helper"
        } else if (helpers.equals("1", ignoreCase = true)) {
            tv_service_type.text = "$vehicle_name - $helpers Helper"
        } else if (helpers.equals("2", ignoreCase = true)) {
            tv_service_type.text = "$vehicle_name - $helpers Helpers"
        } else if (helpers.equals("3", ignoreCase = true)) {
            tv_service_type.text = "$vehicle_name - $helpers Helpers"
        }
        tv_client_name.text = "$fname $lname"
        tv_client_telephone.setText(country_code + str_phone)
        tv_drop_off.setText(destination_address)
        tv_pickup.setText(pickup_address)
        val currentDate = SimpleDateFormat("dd/MM/yyyy")
        val todayDate = Date()
        val thisDate = currentDate.format(todayDate)
        val cal =
            Calendar.getInstance(TimeZone.getTimeZone("GMT+5:00"))
        val currentLocalTime = cal.time
        val date: DateFormat = SimpleDateFormat("HH:mm a")
        date.timeZone = TimeZone.getTimeZone("GMT+5:00")
        val localTime = date.format(currentLocalTime)
        if (!TextUtils.isEmpty(date_future_booking_str)&&!TextUtils.isEmpty(time_slots_future_booking_str)) {
            val format_time = changeDateFormat(
                date_future_booking_str+" "+time_slots_future_booking_str,
                "yyyy-MM-dd HH:mm",
                "dd-MMM-yyyy hh:mm a"
            )
            tv_job_date.setText(format_time)
         //   val img = getContext()!!.resources.getDrawable(R.drawable.ic_down_arrow)
         //   tv_job_date.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
            tv_job_date.setOnClickListener(this@RegularRideJobSheet)
            val format =
                SimpleDateFormat("yyyy-MM-dd HH:mm")
            try {
                date_current = format.parse(date_future_booking_str+" "+time_slots_future_booking_str)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            tv_job_date.setText("Right Away")
            tv_job_date.setOnClickListener(null)
        }
        add_instruction.setOnClickListener {
            Special_Instruction_Dialog()
        }
        tv_stops2.setOnClickListener {
            aditionalStopsAlert()
        }
        AlertDialogManager.showAlertMessage(context, getString(R.string.edit_address_alert))


    }

    private fun Special_Instruction_Dialog() {

        val builder = AlertDialog.Builder(context!!)
        builder.setView(R.layout.sepcial_instuction_alert)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()

        alertDialog!!.special_instructions_tv!!.setText(special_instructions)
        alertDialog!!.Submit_instruction!!.setOnClickListener {
            special_instructions = alertDialog!!.special_instructions_tv!!.text.toString()
            add_instruction.text=special_instructions
            alertDialog!!.special_instructions_tv!!.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.cancel!!.setOnClickListener {
            alertDialog!!.special_instructions_tv!!.hideKeyboard()
            alertDialog!!.dismiss()
        }

    }

    private fun setdimatchingVales(v: View) {

        val popup = PopupMenu(context, v)
        for (title in CONSTANTS.Need_dismantling_List) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            val title = item.title.toString()
            view!!.Need_dismantling_spinner!!.text = title
            when (title) {
                CONSTANTS.Need_dismantling_List[0] -> {
                    showToast("Please mention if you require assembly/dismantling services or not")
                }
                CONSTANTS.Need_dismantling_List[1] -> {
                    AlertDialogManager.showAlertMessage(
                        context!!,
                        "Please provide full details of what needs to be dismantled or reassembled in the special instructions box below."
                    )
                }
                CONSTANTS.Need_dismantling_List[2] -> {
                }
            }
            true
        }
        popup.show()


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

    private fun Open_Picture_Dialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setCancelable(false)
        builder.setView(R.layout.add_photo)
        alertDialog_Picture = builder.create()

        alertDialog_Picture!!.run {
            show()
            Cancel_Picture!!.setOnClickListener {
                dismiss()
            }
            inventory_rcv!!.adapter = InventoryAdaptor2(
                context, Utils.getInventoryImagesList,
                this@RegularRideJobSheet
            )
        }


    }


    private fun open_botton_sheet() {
        val view =
            layoutInflater.inflate(R.layout.selected_images_layout, null)
        dialog = BottomSheetDialog(context!!)
        dialog!!.setContentView(view)
        dialog!!.show()
        view.gallery.setOnClickListener(this)
        view.camera.setOnClickListener(this)
        view.Cancel_2.setOnClickListener { dialog!!.dismiss() }
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
        startActivityForResult(intent, MakeOfferFragment.REQUEST_CODE_GALLERY)
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
        startActivityForResult(camera_intent, MakeOfferFragment.REQUEST_CODE_CAMERA)
    }


    private fun Check_WRITE_EXTERNAL_STORAGE(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
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

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                if (requestCode == MakeOfferFragment.REQUEST_CODE_GALLERY) {
                    val image_path = intent.data
                    try {
                        bitmap_img = MediaStore.Images.Media.getBitmap(
                            activity!!.contentResolver,
                            image_path
                        )
                        val base_64_image = Utils.getStringImage(bitmap_img!!)
                        Calulating_Image_Selected()
                        Utils.getInventoryImagesList[select_image_position] = base_64_image
                        alertDialog_Picture!!.inventory_rcv!!.adapter!!.notifyDataSetChanged()

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (requestCode == MakeOfferFragment.REQUEST_CODE_CAMERA) {
                    bitmap_img = intent.extras!!["data"] as Bitmap?
                    val base_64_image =
                        Utils.getStringImage(bitmap_img!!)
                    Calulating_Image_Selected()
                    Utils.getInventoryImagesList[select_image_position] = base_64_image
                    alertDialog_Picture!!.inventory_rcv!!.adapter!!.notifyDataSetChanged()


                }
            }
        }
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
                        this@RegularRideJobSheet
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


    override fun onClick(v: View) {
        when (v.id) {

            R.id.iv_camera -> Open_Picture_Dialog()

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

            R.id.add_inventory -> add_inventory_Dialog()

            R.id.what_moving -> {
                ShowServiceList(v)
            }
            R.id.add_Demensions -> startActivity(Intent(context, AddDeminsion::class.java))
            R.id.add_photo -> Open_Picture_Dialog()


            R.id.Need_dismantling_spinner -> {
                setdimatchingVales(v)
            }
            R.id.edit_address -> {
                ispickup = true
                AlertDialogManager.showAlertMessage_fixed(
                    context, getString(R.string.edit_address_msg),
                    "edit_address", this@RegularRideJobSheet
                )
            }
            R.id.edit_address2 -> {
                ispickup = false
                AlertDialogManager.showAlertMessage_fixed(
                    context, getString(R.string.edit_address_msg),
                    "edit_address", this@RegularRideJobSheet
                )
            }
            R.id.detail -> {
                alertDialog!!.dismiss()
                val transaction =
                    fragmentManager!!.beginTransaction()
                val fragment = BookedMovesMain()
                val bundle = Bundle()
                bundle.putString(CONSTANTS.move_to_move_upcoming, CONSTANTS.move_to_move_upcoming)
                fragment.arguments = bundle
                transaction.replace(R.id.container_fragments, fragment)
                transaction.commit()
            }
            R.id.tv_requestVan -> CheckValidition()
            R.id.tv_proprty_type_pickup -> {
                is_pickup_property = true
                showProprtyTypesAlert()
            }
            R.id.tv_proprty_type_dropoff -> {
                is_pickup_property = false
                showProprtyTypesAlert()
            }

            R.id.cancel -> alertDialog!!.dismiss()
/*
            R.id.tv_additional_info_value -> showInsuranceAlert()
*/
            R.id.tv_one_bed_house -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_one_bed_house.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.tv_one_bed_house.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_two_bed_house -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_two_bed_house.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.tv_two_bed_house.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.Basement -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.Basement.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.Basement.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_three_bed_house -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_three_bed_house.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text =
                        alertDialog!!.tv_three_bed_house.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_four_plus_bed_house -> {
                if (is_pickup_property) {

                    tv_proprty_type_pickup!!.text =
                        alertDialog!!.tv_four_plus_bed_house.text.toString()
                } else {

                    tv_proprty_type_dropoff!!.text =
                        alertDialog!!.tv_four_plus_bed_house.text.toString()
                }
                alertDialog!!.dismiss()
            }
            R.id.tv_storage -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_storage.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.tv_storage.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.recycling -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.recycling.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.recycling.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_flat_share -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_flat_share.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.tv_flat_share.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_flat_one_bed -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_flat_one_bed.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.tv_flat_one_bed.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_flat_two_bed -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_flat_two_bed.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.tv_flat_two_bed.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_three__bed_flat -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.tv_three__bed_flat.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text =
                        alertDialog!!.tv_three__bed_flat.text.toString()

                }
                alertDialog!!.dismiss()
            }
            R.id.tv_flat_four_plus_bed -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text =
                        alertDialog!!.tv_flat_four_plus_bed.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text =
                        alertDialog!!.tv_flat_four_plus_bed.text.toString()

                }
                alertDialog!!.dismiss()
            }
     /*       R.id.rel_free -> {
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
            R.id.btn_continue -> {
                val insurance_plan = getPreferences(CONSTANTS.insurance_plan, context!!)
                tv_additional_info_value!!.text=insurance_plan
               /* if (insurance == CONSTANTS.Standard_insurance_) {
                    tv_additional_info_value!!.text = CONSTANTS.Standard_insurance_
                } else {
                    tv_additional_info_value!!.text = CONSTANTS.Costs_insurance
                }
*/
                // CHange Estaimetd Duration when sure change insuarnce
                estimated_payment = Fare_Estimation(estimated_duartion, CONSTANTS.vehicles!!)
                Estimated!!.text = "$estimated_duartion($estimated_payment)"
                alertDialog!!.dismiss()
            }
           /* R.id.tv_job_date -> {
                send_furture_request = false
                DateTimePicker.ShowDatePicker(
                    activity!!.supportFragmentManager,
                    listener
                )
                Utils.showToastLong(getString(R.string.job_time))
            }*/
            R.id.pickup_floor_spinner -> PickupFloor(v)
            R.id.dropoff_floor_spinner -> DrofoffFloor(v)
            R.id.pickup_lift_spinner -> PickupLift(v)
            R.id.dropoff_lift_spinner -> DrofoffLift(v)
            R.id.congestion_charge_tv -> PickCongestionCharge(v)
            R.id.ulez_tv -> PickULEZCharge(v)
        }
    }

    private fun add_inventory_Dialog() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.add_inventory)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()


        alertDialog!!.Quick_Inventory!!.setText(Quick_Inventory_)


        alertDialog!!.what_moving!!.setText(what_moving_)

        alertDialog!!.what_moving!!.setOnClickListener(this)
        alertDialog!!.close_inventory!!.setOnClickListener {
            alertDialog!!.Quick_Inventory!!.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.done_invenotry!!.setOnClickListener {

            what_moving_ = alertDialog!!.what_moving!!.text.toString()
            Quick_Inventory_ = alertDialog!!.Quick_Inventory!!.text.toString()
            add_inventory.text=Quick_Inventory_
            alertDialog!!.Quick_Inventory!!.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.add_Demensions!!.setOnClickListener(this)
        alertDialog!!.add_photo!!.setOnClickListener(this)

    }

    private fun CheckValidition() {

        str_property_pickup = view!!.tv_proprty_type_pickup.text.toString()
        property_type_droppoff = view!!.tv_proprty_type_dropoff.text.toString()
        pickup_floor = pickup_floor_spinner!!.text.toString()
        dropoff_floor = dropoff_floor_spinner!!.text.toString()
        pickup_lift = pickup_lift_spinner!!.text.toString()
        dropoff_lift = dropoff_lift_spinner!!.text.toString()

        congestion_charge = congestion_charge_tv!!.text.toString()
        ulez_charge = ulez_tv!!.text.toString()

        if (pickup_floor!!.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select pick up floor.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (dropoff_floor!!.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select drop off floor.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (pickup_floor != CONSTANTS.Ground && pickup_lift!!.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select pick up lift.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (dropoff_floor != CONSTANTS.Ground && dropoff_lift!!.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select drop off lift.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (TextUtils.isEmpty(str_property_pickup)) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select pick up property type.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (TextUtils.isEmpty(property_type_droppoff)) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select drop off property type.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        }
//        else if (tv_stops_data.text == "No Stop Selected") {
//            val snackbar = Snackbar
//                .make(
//                    coordinatorLayout!!,
//                    "Please select additional stops", Snackbar.LENGTH_LONG
//                )
//            snackbar.show()
//        }
        else if (Need_dismantling_spinner.text.contains("Need dismantling or assembling ?")) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Please mention if you require assembly/dismantling services or not", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (Quick_Inventory_ == "") {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Please add inventory", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (special_instructions == "") {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Please add special instructions if any", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else {
            MakeRequest()
        }
    }

    private fun showConfirmDialog(requestId: String) {
        bookingRequestId = requestId
        val builder = AlertDialog.Builder(activity!!)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Please select where you want to move")
//        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Pay a deposit now to book") { dialogInterface, which ->
            moveToAdvancePaymentScreen(requestId)
        }
        //performing cancel action
        builder.setNeutralButton("Cancel Booking") { dialogInterface, which ->
            showConfirmCancelDialog(requestId)
        }
        //performing negative action
        builder.setNegativeButton("Visit dashboard for changes") { dialogInterface, which ->
            moveToDashboard()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun showConfirmCancelDialog(requestId: String) {
        bookingRequestId = requestId
        val builder = AlertDialog.Builder(activity!!)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to cancel your booking?")
//        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            cancelRequest(
                passenger_id, RegistrationID,
                bookingRequestId
            )        }
        //performing cancel action
        builder.setNeutralButton("No") { dialogInterface, which ->
            showConfirmDialog(requestId)
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun moveToDashboard() {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_fragments, BookedMovesMain())
        transaction.commit()
    }

    private fun Confirm_Dialog_Cancel(requestId: String) {
        val first_name =
            getPreferences(CONSTANTS.first_name, context!!)
        val last_name =
            getPreferences(CONSTANTS.last_name, context!!)
        val title = "Dear $first_name $last_name"

        var msg = ""
        msg = getString(R.string.cancel_booking_msg)
        AlertDialogManager.showAlertMessageWithTwoButtons(
            context,
            this,
            "Confirm_Dialog_Cancel",
            title,
            msg,
            "Yes- Cancel",
            "No"
        )
    }

    private fun moveToAdvancePaymentScreen(requestId: String) {
        startActivity(
            Intent(context, AdvancedPaymentActivity::class.java)
                .putExtra("request_id", requestId)

        )
        activity!!.finish()
    }

    private fun MakeRequest() {

        //TODO:

        estimated_duartion = getPreferences(
            CONSTANTS.estimated_duartion,
            context!!
        )

        if (pickup_floor == CONSTANTS.Ground) {
            pickup_lift = "No"
        }
        if (dropoff_floor == CONSTANTS.Ground) {
            dropoff_lift = "No"
        }

        vehicle_class_id = getPreferences(CONSTANTS.vehicle_class_id, context!!)
        date_future_booking_str = getPreferences(
            CONSTANTS.date_future_booking_str,
            context!!
        )
        time_slots_future_booking_str = getPreferences(
            CONSTANTS.time_slots_future_booking_str,
            context!!
        )
        pickup_lat = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )
        pickup_lon = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            context!!
        )

        destination_lat = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
            context!!
        )
        destination_lon = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
            context!!
        )
        destination_lon = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
            context!!
        )
        destination_lon = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
            context!!
        )
        insurance = getPreferences(CONSTANTS.insurance, context!!)

        insurance_charge = getPreferences(CONSTANTS.insurance_fare, context!!)!!

        insurance = if (insurance_charge!!.toDouble()>0.0) {
            "1"
        } else {
            "0"
        }

        vehicle_name = getPreferences(CONSTANTS.vehicle_name, context!!)








/*
        estimated_payment = getPreferences(
            CONSTANTS.estimated_payment,
            context!!
        )*/

       // estimated_payment=   totalEstimatedPayment.toString()

        pay_by = getPreferences(CONSTANTS.PAY_BY, context!!)


        // saved last destination
        savePreferences(
            CONSTANTS.PREFERENCE_LAST_RIDE_ADDRESS_EXTRA,
            destination_address,
            context!!
        )
        savePreferences(
            CONSTANTS.PREFERENCE_LAST_RIDE_LATITUDE_EXTRA,
            destination_lat,
            context!!
        )
        savePreferences(
            CONSTANTS.PREFERENCE_LAST_RIDE_LONGITUDE_EXTRA,
            destination_lon,
            context!!
        )
        if (TextUtils.isEmpty(date_future_booking_str)) {
            newBookingRequest(
                "" + latitude,
                "" + longitude, pickup_lat,
                pickup_lon, destination_lat, destination_lon,
                pay_by, vehicle_class_id,
                destination_address,
                pickup_address, special_instructions,
                helpers, insurance,insurance_charge, passenger_id, RegistrationID
            )
        } else {
            newBookingRequestFuture(
                "" + latitude,
                "" + longitude,
                pickup_lat,
                pickup_lon,
                destination_lat,
                destination_lon,
                pay_by,
                vehicle_class_id,
                destination_address,
                pickup_address,
                special_instructions,
                helpers,
                insurance,
                insurance_charge,
                passenger_id,
                RegistrationID
            )
        }
    }

//TODO:Future Booking Request
    private fun newBookingRequestFuture(
        passenger_latitude: String,
        passenger_longitude: String,
        pickup_latitude: String?,
        pickup_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        payment_type: String?,
        vehicle_class_id: String?,
        destination: String?,
        pickup: String?,
        special_instructions: String?,
        helpers_count: String?,
        is_insurance: String?,
        insurance_charge: String?,
        passenger_id: String?,
        registrationID: String?
    ) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.show()
        progressDialog!!.setMessage("Please wait...")
        var service_id =
            getPreferences(CONSTANTS.SERVICE_ID, context!!)
        val url = Utils.new_upcoming_move
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, Any?> =
            HashMap()

        postParam["special_instructions"] = special_instructions
        postParam["inventory_details"] = Quick_Inventory_
        postParam["inventory_items"] = what_moving_
        postParam["details"] = ""

        postParam["passenger_latitude"] = passenger_latitude
        postParam["passenger_longitude"] = passenger_longitude
        postParam["pickup_latitude"] = pickup_latitude
        postParam["pickup_longitude"] = pickup_longitude
        postParam["destination_latitude"] = destination_latitude
        postParam["destination_longitude"] = destination_longitude
        postParam["payment_type"] = payment_type
        postParam["vehicle_class_id"] = vehicle_class_id



        postParam["destination"] = destination
        postParam["pickup"] = pickup
        postParam["pickup_door"] = ""
        postParam["dropoff_door"] = ""


        postParam["ulezzone"] = total_ulez_stimated_payment
        postParam["congestionzone"] = total_congnigetion_stimated_payment
        postParam["pickup_floor_charge"] = total_pickup_floor_estimated_payment
        postParam["dropoff_floorcharge"] = total_drop_floore_estimated_payment

        postParam["helpers_count"] = helpers_count
        postParam["is_insurance"] = is_insurance
        postParam["insurance_charge"] = insurance_charge
        postParam["timestamp"] = date_future_booking_str
        postParam["arrival_time"] = time_slots_future_booking_str
        postParam["offered_price"] = "0"
        postParam["is_flexible"] = "No"
        postParam["other_Stops"] = other_Stops
        postParam["pickup_floor"] = pickup_floor
        postParam["pickup_lift"] = pickup_lift
        postParam["dropoff_floor"] = dropoff_floor
        postParam["dropoff_lift"] = dropoff_lift
        postParam["pickup_property"] = str_property_pickup
        postParam["dropoff_property"] = property_type_droppoff
        postParam["upto_first_hour_rate"] = Upto_hour_double
        postParam["estimated_duartion"] = estimated_duartion
      //  estimated_payment = estimated_payment!!.replace(CONSTANTS.CURRENCY,"")

        postParam["estimated_payment"] = estimated_payment!!.replace(CONSTANTS.CURRENCY,"")
        if (TextUtils.isEmpty(service_id)) {
            service_id = "1"
        }
        postParam["service_id"] = service_id
        if (CONSTANTS.isPromoCodeApply) {
            postParam["promo_code_percentage"] = CONSTANTS.promoCode!!.percentage
            postParam["promo_code_id"] = CONSTANTS.promoCode!!.id
        }


        // UPLAOD  dimensions
        try {
            val gson = Gson()
            val deminsions =
                ArrayList<DeminsionModel>()
            for (i in Utils.deminsionList.indices) {
                if (!Utils.deminsionList[i].name.isEmpty()) {
                    deminsions.add(Utils.deminsionList[i])
                }
            }
            val listString = gson.toJson(
                deminsions,
                object : TypeToken<List<DeminsionModel?>?>() {}.type
            )
            val jsonArray = JSONArray(listString)
            if (jsonArray.length() > 0) {
                postParam["dimensions"] = jsonArray
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {

            // UPLAOD  picture
            // Remove Empty List
            val ImagesList = ArrayList<String>()
            for (i in Utils.getInventoryImagesList.indices) {
                if (!Utils.getInventoryImagesList[i].isEmpty()) {
                    ImagesList.add(Utils.getInventoryImagesList[i])
                }
            }


            var jsonArray: JSONArray? = JSONArray()
            val listString = Gson().toJson(
                ImagesList,
                object : TypeToken<ArrayList<String?>?>() {}.type
            )
            try {
                jsonArray = JSONArray(listString)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (jsonArray!!.length() > 0) {
                postParam["picture"] = jsonArray
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val testing = Gson().toJson(postParam)
        println("TESTIG: " + JSONObject(postParam as Map<*, *>))
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            savePreferences(
                                CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                                "",
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                                "",
                                context!!
                            )


                            savePreferences(
                                CONSTANTS.date_future_booking_str,
                                "",
                                context!!
                            )

                            savePreferences(
                                CONSTANTS.time_slots_future_booking_str,
                                "",
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.estimated_payment,
                                estimated_payment,
                                context!!
                            )
                            NotificationUtils().showNotification(
                                getString(R.string.app_name),
                                "New Upcoming job created"
                            )

                            val request_id = jsonObject.getString("request_id")
                            showConfirmDialog(request_id)
                        } else {
                            Toast.makeText(
                                context,
                                "" + jsonStatus.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }, Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    if (progressDialog!!.isShowing) {
                        progressDialog!!.dismiss()
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["passenger_id"] = passenger_id!!
                    headers["registration_id"] = registrationID!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
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
                            Utils.showToast(
                                "Booking Cancelled"
                            )
                            startActivity(Intent(context, MainScreenActivity::class.java))
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

    private fun DrofoffFloor(v: View) {
        val popup =
            PopupMenu(getContext()!!, v)
        for (category in CONSTANTS.category) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            if (item_.equals(CONSTANTS.category.get(0))) {
                dropoff_lift_spinner.gone()
            } else {
                dropoff_lift_spinner.visible()
            }
            dropoff_floor_spinner!!.text = item_

            updateDropFloorEstimationPrice()

            true
        }
        popup.show()
    }
    private fun updateEstimatedPrice(){
        val totalEstimatedPrice=total_estimated_payment+
                total_pickup_floor_estimated_payment+
                total_drop_floore_estimated_payment+
                total_ulez_stimated_payment+
                total_congnigetion_stimated_payment
        estimated_payment=CONSTANTS.CURRENCY+totalEstimatedPrice.toString()
        Estimated.setText("$estimated_duartion $estimated_payment")
    }

    private fun updatePickupFloorEstimationPrice(){

        val  helpers = getPreferences(CONSTANTS.helpers, context!!)
        val helpers_count_ = helpers!!.toDouble()

        System.out.println("total_pickup_floor_estimated_payment:"+total_pickup_floor_estimated_payment)
        System.out.println("helpers_count_:"+helpers_count_)
        pickup_floor=pickup_floor_spinner!!.text.toString()
        System.out.println("pickup_lift_spinner!!.text.toString()::"+pickup_lift_spinner!!.text.toString())
        System.out.println("pickup_floor:"+pickup_floor)
        if (pickup_lift_spinner!!.text.toString().equals("No")){

            if (pickup_floor == CONSTANTS.First_Floor_) {
                pickup_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.first_floor_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_pickup_floor_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
            if (pickup_floor == CONSTANTS.Second_Floor_) {
                pickup_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.second_floor_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_pickup_floor_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
            if (pickup_floor == CONSTANTS.Third_Floor_) {
                pickup_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.third_floor_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_pickup_floor_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
            if (pickup_floor == CONSTANTS.Fourth_Floor_) {
                pickup_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.fourth_above_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_pickup_floor_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
        }
        else{
            pickup_lift = "Yes"
            total_pickup_floor_estimated_payment=0.0
        }
        updateEstimatedPrice()
    }
    private fun updateDropFloorEstimationPrice(){

        val  helpers = getPreferences(CONSTANTS.helpers, context!!)
        val helpers_count_ = helpers!!.toDouble()

        System.out.println("totalEstimatedPayment:"+total_drop_floore_estimated_payment)
        System.out.println("helpers_count_:"+helpers_count_)

        System.out.println("pickup_floor:"+pickup_floor)
        dropoff_floor=dropoff_floor_spinner!!.text.toString()
        System.out.println("dropoff_floor:"+dropoff_floor)
        if (dropoff_lift_spinner!!.text.toString().equals("No")){
            if (dropoff_floor == CONSTANTS.First_Floor_) {
                dropoff_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.first_floor_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_drop_floore_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
            if (dropoff_floor == CONSTANTS.Second_Floor_) {
                dropoff_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.second_floor_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_drop_floore_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
            if (dropoff_floor == CONSTANTS.Third_Floor_) {
                dropoff_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.third_floor_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_drop_floore_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
            if (dropoff_floor == CONSTANTS.Fourth_Floor_) {
                dropoff_lift = "No"
                val  floor_charge = getPreferences(
                    CONSTANTS.fourth_above_charge,
                    context!!
                )
                System.out.println("floor_charge:"+floor_charge)
                total_drop_floore_estimated_payment=helpers_count_*floor_charge!!.toDouble()
            }
        }
        else{
            dropoff_lift = "Yes"
            total_drop_floore_estimated_payment=0.0
        }
       updateEstimatedPrice()

    }
    private fun updateCongigetionEstimationPrice(){

        if (congestion_charge.equals("Yes")){
            total_congnigetion_stimated_payment=15.00
        }
        else{
            total_congnigetion_stimated_payment=0.0
        }
       updateEstimatedPrice()
    }
    private fun updateUlezEstimationPrice(){
        if (ulez_charge.equals("Yes")){
            total_ulez_stimated_payment=12.50
        }
        else{
            total_ulez_stimated_payment=0.0
        }
        updateEstimatedPrice()
    }
    private fun PickupLift(v: View) {
        val popup =
            PopupMenu(getContext()!!, v)
        for (category in CONSTANTS.categorylift) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            pickup_lift_spinner!!.text = item_

            updatePickupFloorEstimationPrice()
            true
        }
        popup.show()
    }
    private fun PickCongestionCharge(v: View) {
        val popup =
            PopupMenu(getContext()!!, v)
        for (category in CONSTANTS.categorylift) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            congestion_charge_tv!!.text = item_
            congestion_charge=item_
            updateCongigetionEstimationPrice()
            true
        }
        popup.show()
    }
    private fun PickULEZCharge(v: View) {
        val popup =
            PopupMenu(getContext()!!, v)
        for (category in CONSTANTS.categorylift) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            ulez_tv!!.text = item_
            ulez_charge=item_
            updateUlezEstimationPrice()
            true


        }
        popup.show()
    }

    private fun DrofoffLift(v: View) {
        val popup =
            PopupMenu(getContext()!!, v)
        for (category in CONSTANTS.categorylift) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            dropoff_lift_spinner!!.text = item_
            updateDropFloorEstimationPrice()
            true
        }
        popup.show()
    }

    private fun PickupFloor(v: View) {
        val popup =
            PopupMenu(getContext()!!, v)
        for (category in CONSTANTS.category) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            if (item_.equals(CONSTANTS.category.get(0))) {
                pickup_lift_spinner.gone()
            } else {
                pickup_lift_spinner.visible()
            }
            pickup_floor_spinner!!.text = item_
            updatePickupFloorEstimationPrice()
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

    private fun showInsuranceAlert() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.alert_insurance)
        alertDialog = builder.create()
        alertDialog!!.show()

        alertDialog!!.term_condition_insurance!!.setOnClickListener { Open_Chrome(CONSTANTS.insurance_link) }
      //  alertDialog!!.rel_free!!.setOnClickListener(this)
       // alertDialog!!.rel_ten!!.setOnClickListener(this)
        alertDialog!!.btn_continue!!.setOnClickListener(this)
        alertDialog!!.close_insurance!!.setOnClickListener {
            alertDialog!!.dismiss()
        }
        if (insurance == CONSTANTS.Standard_insurance_) {
           // alertDialog!!.rel_free!!.performClick()
        } else {
          //  alertDialog!!.rel_ten!!.performClick()
        }
    }

    private fun showProprtyTypesAlert() {
        val builder =
            AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        builder.setView(inflater.inflate(R.layout.alert_property_type, null))
        alertDialog = builder.create()
        alertDialog!!.show()
        alertDialog!!.setCanceledOnTouchOutside(true)

        alertDialog!!.tv_one_bed_house!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_two_bed_house!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_three_bed_house!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_four_plus_bed_house!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_storage!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_flat_share!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_flat_one_bed!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_flat_two_bed!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_three__bed_flat!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.tv_flat_four_plus_bed!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.Basement!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.recycling!!.setOnClickListener(this@RegularRideJobSheet)
        alertDialog!!.Cancel!!.setOnClickListener { alertDialog!!.dismiss() }
    }

    private fun aditionalStopsAlert() {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setView(R.layout.location_alert)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()

        alertDialog!!.extra_steps!!.setText(other_Stops)
        alertDialog!!.Submit!!.setOnClickListener {
//            if (alertDialog!!.extra_steps.text.isNotEmpty()) {
                alertDialog!!.extra_steps!!.text.toString().let {
                    view!!.tv_stops_data!!.text = it
                    other_Stops = it
                    alertDialog!!.extra_steps!!.hideKeyboard()
                    alertDialog!!.dismiss()
                }
//            }else{
//                showToast("Please select additional stops")
//            }

        }
        alertDialog!!.cancel!!.setOnClickListener {
            alertDialog!!.extra_steps!!.hideKeyboard()
            alertDialog!!.dismiss()
        }
    }

    override fun onItemSelected(
        adapterView: AdapterView<*>?,
        view: View,
        i: Int,
        l: Long
    ) {
        service_id = Utils.GetServicesList[i].id
        savePreferences(CONSTANTS.SERVICE_ID, service_id, context!!)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN ->                 // Disallow ScrollView to intercept touch events.
                v.parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP ->                 // Allow ScrollView to intercept touch events.
                v.parent.requestDisallowInterceptTouchEvent(false)
        }
        v.onTouchEvent(event)
        return true
    }

    private val listener2: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            val compare_format =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date_future_booking_str = compare_format.format(date)


            savePreferences(
                CONSTANTS.date_future_booking_str,
                date_future_booking_str,
                context!!
            )
            val format_time = changeDateFormat(
                date_future_booking_str,
                "yyyy-MM-dd HH:mm:ss",
                "dd-MMM-yyyy h:mm a"
            )


            view!!.tv_job_date!!.text = format_time
        }

        override fun onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    }
    private val listener: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            val compare_format =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val format_time = compare_format.format(date)

            date_future_booking_str = compare_format.format(date)



            val compare_date_format =
                SimpleDateFormat("yyyy-MM-dd")

            val new_date_future_booking_str = compare_date_format.format(date)
            savePreferences(
                CONSTANTS.date_future_booking_str,
                new_date_future_booking_str,
                context!!
            )
            val compare_time_slots_format = SimpleDateFormat("HH:mm")
            time_slots_future_booking_str = compare_time_slots_format.format(date)

            savePreferences(
                CONSTANTS.time_slots_future_booking_str,
                time_slots_future_booking_str,
                context!!
            )
            if (send_furture_request) {
                newBookingRequestFuture(
                    "" + latitude,
                    "" + longitude,
                    pickup_lat,
                    pickup_lon,
                    destination_lat,
                    destination_lon,
                    pay_by,
                    vehicle_class_id,
                    destination_address,
                    pickup_address,
                    special_instructions,
                    helpers,
                    insurance,
                    insurance_charge,
                    passenger_id,
                    RegistrationID
                )
            }


            val currentHours = SimpleDateFormat("HH").format(Date())
            val selectedHours = SimpleDateFormat("HH").format(date)
            val currentMin = SimpleDateFormat("mm").format(Date())
            val selectedMin = SimpleDateFormat("mm").format(date)
            val selectedYear = SimpleDateFormat("yyyy").format(date)
            val currentYear = SimpleDateFormat("yyyy").format(Date())
            val selectedMonth = SimpleDateFormat("MM").format(date)
            val currentMonth = SimpleDateFormat("MM").format(Date())
            val selectedDay = SimpleDateFormat("dd").format(date)
            val currentDay = SimpleDateFormat("dd").format(Date())
            val currentHoursInt = currentHours.toInt() + 2

            if (currentYear.toInt() == selectedYear.toInt()) {
                if (currentMonth.toInt() == selectedMonth.toInt()) {
                    if (currentDay.toInt() == selectedDay.toInt()) {
                        if (currentHoursInt > selectedHours.toInt()) {
                            Utils.showToastLong(getString(R.string.job_time))
                        } else if (currentHoursInt < selectedHours.toInt()) {
                            view!!.tv_job_date!!.text = format_time
                        } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() == selectedMin.toInt()) {
                            view!!.tv_job_date!!.text = format_time
                        } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() < selectedMin.toInt()) {
                            view!!.tv_job_date!!.text = format_time
                        } else {
                            Utils.showToastLong(getString(R.string.job_time))
                        }
                    } else {
                        view!!.tv_job_date!!.text = format_time
                    }
                } else {
                    view!!.tv_job_date!!.text = format_time
                }
            } else {
                view!!.tv_job_date!!.text = format_time
            }

        }

        override fun onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    }

    private fun newBookingRequest(

        passenger_latitude: String,
        passenger_longitude: String,
        pickup_latitude: String?,
        pickup_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        payment_type: String?,
        vehicle_class_id: String?,
        destination: String?,
        pickup: String?,
        special_instructions: String?,
        helpers_count: String?,
        is_insurance: String?,
        insurance_charge: String?,
        passenger_id: String?,
        registrationID: String?
    ) {
        var service_id =
            getPreferences(CONSTANTS.SERVICE_ID, context!!)
        val url = Utils.new_booking_request
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, Any?> = HashMap()
        progress.visibility = View.VISIBLE
        postParam["passenger_latitude"] = passenger_latitude
        postParam["passenger_longitude"] = passenger_longitude
        postParam["pickup_latitude"] = pickup_latitude
        postParam["pickup_longitude"] = pickup_longitude
        postParam["destination_latitude"] = destination_latitude
        postParam["destination_longitude"] = destination_longitude
        postParam["payment_type"] = payment_type
        postParam["vehicle_class_id"] = vehicle_class_id
        postParam["details"] = ""
        postParam["inventory_details"] = Quick_Inventory_
        postParam["inventory_items"] = what_moving_
        postParam["special_instructions"] = special_instructions
        postParam["destination"] = destination
        postParam["pickup"] = pickup
        postParam["pickup_floor"] = pickup_floor
        postParam["pickup_lift"] = pickup_lift
        postParam["dropoff_floor"] = dropoff_floor
        postParam["dropoff_lift"] = dropoff_lift
        postParam["pickup_door"] = ""
        postParam["dropoff_door"] = ""
        postParam["pickup_property"] = str_property_pickup
        postParam["dropoff_property"] = property_type_droppoff
        postParam["helpers_count"] = helpers_count
        postParam["is_insurance"] = is_insurance
        postParam["insurance_charge"] = insurance_charge

        postParam["is_flexible"] = "NO"
        postParam["other_Stops"] = other_Stops
        postParam["estimated_duartion"] = estimated_duartion
        estimated_payment =  Fare_Estimation(estimated_duartion, CONSTANTS.vehicles!!)
        postParam["estimated_payment"] = estimated_payment
        postParam["upto_first_hour_rate"] = Upto_hour_double
        if (TextUtils.isEmpty(service_id)) {
            service_id = "1"
        }
        postParam["service_id"] = service_id
        if (CONSTANTS.isPromoCodeApply) {
            postParam["promo_code_percentage"] = CONSTANTS.promoCode!!.percentage
            postParam["promo_code_id"] = CONSTANTS.promoCode!!.id
        }


        // UPLAOD  dimensions
        try {
            var jsonArray = JSONArray()
            val gson = Gson()
            val deminsions =
                ArrayList<DeminsionModel>()
            for (i in Utils.deminsionList.indices) {
                if (!Utils.deminsionList[i].name.isEmpty()) {
                    deminsions.add(Utils.deminsionList[i])
                }
            }
            val listString = gson.toJson(
                deminsions,
                object : TypeToken<List<DeminsionModel?>?>() {}.type
            )
            jsonArray = JSONArray(listString)
            if (jsonArray.length() > 0) {
                postParam["dimensions"] = jsonArray
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        try {

            // UPLAOD  picture
            // Remove Empty List
            val ImagesList =
                ArrayList<String>()
            for (i in Utils.getInventoryImagesList.indices) {
                if (!Utils.getInventoryImagesList[i].isEmpty()) {
                    ImagesList.add(Utils.getInventoryImagesList[i])
                }
            }
            var jsonArray = JSONArray()
            val gson = Gson()
            val listString = gson.toJson(
                ImagesList,
                object : TypeToken<ArrayList<String?>?>() {}.type
            )
            jsonArray = JSONArray(listString)
            if (jsonArray.length() > 0) {
                postParam["picture"] = jsonArray
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val testing = Gson().toJson(postParam)
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        when (jsonStatus.getString("code")) {
                            "1000" -> {
                                val jsonRequest = jsonObject.getJSONObject("request")
                                val request_id = jsonRequest.getString("request_id")
                                savePreferences(
                                    CONSTANTS.REQUEST_ID,
                                    request_id,
                                    context!!
                                )



                                NotificationUtils().showNotification(
                                    getString(R.string.app_name),
                                    "New Regular Move Created"
                                )


                                CONSTANTS.current_ride_data = postParam
                                val i =
                                    Intent(activity, MainScreenActivity::class.java)
                                i.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                i.putExtra(
                                    CONSTANTS.move_to_connecting_screen,
                                    CONSTANTS.move_to_connecting_screen
                                )
                                startActivity(i)
                            }
                            "1001" -> {
                                val message =
                                    "We’re sorry your selected van has just been booked by another nearby customer"


                                ShowAlertMessage.showAlertMessageThreeButtons(
                                    context, "Booking Message",
                                    message,
                                    "Request for another ASAP van",
                                    "Request for later time",
                                    "Cancel Request", "not_found",
                                    this@RegularRideJobSheet
                                )


                            }
                            "1003" -> {
                                val jsonRequest = jsonObject.getJSONObject("request")
                                var driverDistance =
                                    jsonRequest.getString("driver_distance")
                                driverDistance =
                                    "" + CONSTANTS.decimalFormat(driverDistance.toDouble() * CONSTANTS.Meter_Miles) + " mile"
                                porter_count = jsonRequest.getString("porter_count")
                                onlinePorterCount = getPreferences(CONSTANTS.helpers, context!!)!!
                                savePreferences(CONSTANTS.helpers, porter_count, context!!)
                                val message: String
                                message = if (porter_count == "1") {
                                    "The nearest van you have selected is only " + driverDistance +
                                            " , but it only has driver who will help, would you like to proceed?"
                                } else {
                                    val new_porter = porter_count.toInt() - 1
                                    "The nearest van you have selected is only " + driverDistance + " away, but it only has driver who will help and " + new_porter +
                                            " extra helper(s) on board, would you like to proceed?"
                                }
                                ShowAlertMessage.showAlertMessageThreeButtons(
                                    context, "Booking Message", message,
                                    "Proceed", "Request for later time",
                                    "Cancel",
                                    "less_porter",
                                    this@RegularRideJobSheet
                                )
                            }
                            "3002" -> {
                                val message =
                                    "We’re sorry your selected van has just been booked by another nearby customer"


                                ShowAlertMessage.showAlertMessageThreeButtons(
                                    context, "Booking Message",
                                    message,
                                    "Request for another ASAP van",
                                    "Request for later time today or another day",
                                    "Cancel Request", "not_found",
                                    this@RegularRideJobSheet
                                )

                            }
                            else -> {
                                ShowAlertMessage.setOnAlertDialogButtonClicks(this@RegularRideJobSheet)
                                ShowAlertMessage.showAlertMessageOneButtons(
                                    context, "Booking Message", jsonStatus.getString("message"),
                                    "OK"
                                )
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    progress.visibility = View.GONE

                }, Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    progress.visibility = View.GONE

                    Toast.makeText(getContext(), "Error: " + error.message, Toast.LENGTH_SHORT)
                        .show()
                }) {
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["passenger_id"] = passenger_id!!
                    headers["registration_id"] = registrationID!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }

    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        dialogInterface!!.dismiss()
        when (dialog_name) {
            "not_found" -> {
                activity!!.finish()
            }
            "less_porter" -> {
                estimated_duartion = getPreferences(
                    CONSTANTS.estimated_duartion,
                    context!!
                )
                estimated_payment = Fare_Estimation(estimated_duartion, CONSTANTS.vehicles!!)
                val new_price = "$estimated_payment"
                var message = ""
                message = if (porter_count == "0" || porter_count == "1") {
                    "The new estimated price with  " + porter_count + " helper and $estimated_duartion" +
                            " is " + new_price
                } else {
                    "The new estimated price with  " + porter_count + " helpers and" +
                            "$estimated_duartion is " + new_price
                }
                ShowAlertMessage.showAlertMessageTwoButtons(
                    context, "Alert", message,
                    "Proceed", "Cancel",
                    "new_price_show",
                    this@RegularRideJobSheet
                )
            }
            "new_price_show" -> newBookingRequest(
                "" + latitude,
                "" + longitude, pickup_lat,
                pickup_lon, destination_lat, destination_lon,
                pay_by, vehicle_class_id,
                destination_address,
                pickup_address, special_instructions,
                porter_count, insurance,insurance_charge, passenger_id, RegistrationID
            )
            "Delete_Inventory" -> {
                Utils.getInventoryImagesList[select_image_position] = ""
                alertDialog_Picture!!.inventory_rcv!!.adapter!!.notifyDataSetChanged()

            }

            "Confirm_Dialog_Cancel" -> {
                cancelRequest(
                    passenger_id, RegistrationID,
                    bookingRequestId
                )
            }


        }
    }

    override fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        dialogInterface!!.dismiss()
        when (dialog_name) {
            "not_found" -> {
               /* DateTimePicker.ShowDatePicker(
                    activity!!.supportFragmentManager,
                    listener
                )
                Utils.showToastLong(getString(R.string.job_time))*/
            }
            "less_porter" -> {
             /*   DateTimePicker.ShowDatePicker(
                    activity!!.supportFragmentManager,
                    listener
                )
                Utils.showToastLong(getString(R.string.job_time))*/
            }
            "new_price_show" -> {
                savePreferences(CONSTANTS.helpers, onlinePorterCount, context!!)
            }
            "Confirm_Dialog_Cancel" ->{

            }
        }
    }

    override fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        dialogInterface!!.dismiss()

        when (dialog_name) {
            "not_found" -> Home()

        }
    }

    private fun Home() {
        startActivity(Intent(activity, MainScreenActivity::class.java))
        activity!!.finish()
    }
//TODO
    private fun Fare_Estimation(time: String?, vehicles: GetVehicles): String {
        insurance = getPreferences(CONSTANTS.insurance, context!!)
        date_future_booking_str = getPreferences(
            CONSTANTS.date_future_booking_str,
            context!!
        )
    time_slots_future_booking_str = getPreferences(
        CONSTANTS.time_slots_future_booking_str,
        context!!
    )
        val distance_in_miles = getPreferences(CONSTANTS.distance_in_miles, context!!)
        return if (time == CONSTANTS.estimated_duration_list[1]) {
            var fare = calculateFareUpto(
                1.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        } else if (time == CONSTANTS.estimated_duration_list[2]) {

            var fare = calculateFareUpto(
                2.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[3]) {

            var fare = calculateFareUpto(
                2.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }


        else if (time == CONSTANTS.estimated_duration_list[4]) {
            var fare = calculateFareUpto(
                3.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[5]) {

            var fare = calculateFareUpto(
                3.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }




        else if (time == CONSTANTS.estimated_duration_list[6]) {
            var fare = calculateFareUpto(
                4.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }

        else if (time == CONSTANTS.estimated_duration_list[7]) {

            var fare = calculateFareUpto(
                4.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }

        else if (time == CONSTANTS.estimated_duration_list[8]) {
            var fare = calculateFareUpto(
                5.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[9]) {

            var fare = calculateFareUpto(
                5.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }


        else if (time == CONSTANTS.estimated_duration_list[10]) {
            var fare = calculateFareUpto(
                6.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[11]) {

            var fare = calculateFareUpto(
                6.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }


        else if (time == CONSTANTS.estimated_duration_list[12]) {
            var fare = calculateFareUpto(
                7.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )

            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[13]) {

            var fare = calculateFareUpto(
                7.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }


        else if (time == CONSTANTS.estimated_duration_list[14]) {
            var fare = calculateFareUpto(
                8.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[15]) {

            var fare = calculateFareUpto(
                8.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }


        else if (time == CONSTANTS.estimated_duration_list[16]) {
            var fare = calculateFareUpto(
                9.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[17]) {

            var fare = calculateFareUpto(
                9.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }


        else if (time == CONSTANTS.estimated_duration_list[18]) {
            var fare = calculateFareUpto(
                10.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[19]) {

            var fare = calculateFareUpto(
                10.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }

        else if (time == CONSTANTS.estimated_duration_list[20]) {
            var fare = calculateFareUpto(
                11.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else if (time == CONSTANTS.estimated_duration_list[21]) {

            var fare = calculateFareUpto(
                11.50,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }

        else if (time == CONSTANTS.estimated_duration_list[22]) {
            var fare = calculateFareUpto(
                12.00,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
        else {
            var fare = calculateFareUpto(
                1.0,
                vehicles,
                distance_in_miles!!,
                context!!
            )


            fare = InDiscountAdd(fare)
            fare = InSurenaceAdd(fare)
            CONSTANTS.CURRENCY + fare
        }
    }

    private fun InSurenaceAdd(fare: String): String {
        val  insurance_fare = getPreferences(CONSTANTS.insurance_fare, context!!)!!.toDouble()
        val fare_ = fare.toDouble() + insurance_fare
        return  String.format("%.2f", fare_)

      /*  return if (insurance == CONSTANTS.Standard_insurance_) {
            val fare_ = fare.toDouble() + CONSTANTS.Standard_insurance_amount
            String.format("%.2f", fare_)
        } else {
            val fare_ = fare.toDouble() + CONSTANTS.Costs_insurance_amount
            String.format("%.2f", fare_)
        }*/
    }

    private fun InDiscountAdd(fare: String): String {
        return if (CONSTANTS.isPromoCodeApply) {
            val Discount_Price = (fare.toDouble()
                    - ((fare.toDouble()
                    * CONSTANTS.promoCode!!.percentage!!.toDouble()) / 100)).toString()
            String.format("%.2f", Discount_Price.toDouble())
        } else {
            fare
        }


    }

    override fun onDialogPositiveButtonPressed() {}
    override fun onDialogNegativeButtonPressed() {}
    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "edit_address") {
            ShowEditDialog()
        } else if (dialog_name == "Confirm_Dialog_Cancel") {
            cancelRequest(
                passenger_id, RegistrationID,
                bookingRequestId
            )
        }
    }

    private fun ShowEditDialog() {
        val alertDialog =
            AlertDialog.Builder(context!!)
                .setTitle("Edit Address")
        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        alertDialog.setView(input)
        if (ispickup) {
            input.setText(tv_pickup!!.text.toString())
        } else {
            input.setText(tv_drop_off!!.text.toString())
        }
        alertDialog.setPositiveButton("Save") { dialogInterface, i ->
            val question = input.text.toString()
            if (question.isEmpty()) {
                Toast.makeText(context, "Address cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                input.hideKeyboard()
                dialogInterface.dismiss()
                ChnageAddress(question)
            }
        }
            .setNegativeButton("Cancel") { dialogInterface, i ->
                input.hideKeyboard()
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun ChnageAddress(address: String) {
        if (ispickup) {
            pickup_address = address
            savePreferences(
                CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                address,
                context!!
            )
            tv_pickup!!.text = address
        } else {
            destination_address = address
            savePreferences(
                CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                address,
                context!!
            )
            tv_drop_off!!.text = address
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}
    override fun onClickRecycler(view: View?, position: Int) {

        select_image_position = position
        if (Utils.getInventoryImagesList[select_image_position].isEmpty()) {
            open_botton_sheet()
        } else {
            inventoy_option()
        }
    }
}