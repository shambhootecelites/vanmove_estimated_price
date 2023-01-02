package com.vanmove.passesger.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.Dimension.ViewDeminsion
import com.vanmove.passesger.activities.FullImage.ZoomImage
import com.vanmove.passesger.adapters.InventoryAdaptor
import com.vanmove.passesger.interfaces.OnAlertDialogButtonClicks
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.APIModel.GenenalModel
import com.vanmove.passesger.model.APIModel.InventoryModel
import com.vanmove.passesger.model.Inventories
import com.vanmove.passesger.model.MessageEvent
import com.vanmove.passesger.model.UpcomingBookings
import com.vanmove.passesger.utils.AlertDialogManager
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage_fixed
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DateTimePicker
import com.vanmove.passesger.utils.ShowAlertMessage.setOnAlertDialogButtonClicks
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageTwoButtons
import com.vanmove.passesger.utils.ShowProgressDialog.closeDialog
import com.vanmove.passesger.utils.ShowProgressDialog.showDialog2
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.ShowParamter
import com.vanmove.passesger.utils.Utils.changeDateFormat
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.getStringImage
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.hideKeyboard
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.activity_edit_offer.*
import kotlinx.android.synthetic.main.add_inventory.*
import kotlinx.android.synthetic.main.add_photo2.*
import kotlinx.android.synthetic.main.alert_insurance.*
import kotlinx.android.synthetic.main.alert_property_type.*
import kotlinx.android.synthetic.main.inventoy_option.view.*
import kotlinx.android.synthetic.main.selected_images_layout.view.*
import kotlinx.android.synthetic.main.titlebar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditOffer : AppCompatActivity(), View.OnClickListener,
    OnTouchListener, OnItemClickRecycler, OnAlertDialogButtonClicks, OnClickTwoButtonsAlertDialog {

    val REQUEST_CODE_GALLERY = 8000
    val REQUEST_CODE_CAMERA = 7000
    var inventories: List<Inventories>? = ArrayList()
    var add_or_edit = ""
    var dialog: BottomSheetDialog? = null

    var request_id: String? = null
    var pickupProperty: String? = null
    var dropoffProperty: String? = null
    private var bitmap_img: Bitmap? = null

    var current_position = 0
    var preferences: SharedPreferences? = null
    var RegistrationID: String? = null
    var passenger_id: String? = null
    private var pickup_floor = ""
    private var dropoff_floor = ""
    private var pickup_lift = ""
    private var dropoff_lift = ""

    private var date_current: Date? = null


    var alertDialog: AlertDialog? = null
    var alertDialog_Picture: AlertDialog? = null


    var et_special_instructions_: String? = ""
    var Quick_Inventory_: String? = ""
    var what_moving_: String? = ""
    var getInventoryImagesList = ArrayList<String?>()
    var pickup_: String? = ""
    var destination_: String? = ""
    var timestamp_: String? = ""
    var isInsurance_ = ""
    var otherStops_: String? = ""
    var destination_longitude: String? = ""
    var destination_latitude: String? = ""
    var pickup_longitude: String? = ""
    var pickup_latitude: String? = ""
    var bookings = UpcomingBookings()
    var is_pickup_property = false
    var ispickup = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_offer)
        iv_back.setOnClickListener {
            finish()
        }

        progress_bar!!.setVisibility(View.GONE)
        preferences = getSharedPreferences(
            CONSTANTS.SHARED_PREFERENCE_APP,
            Context.MODE_PRIVATE
        )
        RegistrationID = getPreferences(CONSTANTS.RegistrationID, this@EditOffer)
        passenger_id = getPreferences(CONSTANTS.passenger_id, this@EditOffer)
        edit_address.setOnClickListener(this@EditOffer)
        edit_address2.setOnClickListener(this@EditOffer)
        insurance_!!.setOnClickListener(this@EditOffer)
        tv_stops_data!!.setOnClickListener(this@EditOffer)
        UPADTE_OFFER.setOnClickListener(this@EditOffer)
        tv_job_date!!.setOnClickListener(this@EditOffer)
        tv_pickup!!.setOnClickListener(this@EditOffer)
        tv_drop_off!!.setOnClickListener(this@EditOffer)
        tv_proprty_type_pickup!!.setOnClickListener(this@EditOffer)
        tv_proprty_type_dropoff!!.setOnClickListener(this@EditOffer)
        pickup_floor_spinner.setOnClickListener(this@EditOffer)
        dropoff_floor_spinner.setOnClickListener(this@EditOffer)
        pickup_lift_spinner.setOnClickListener(this@EditOffer)
        dropoff_lift_spinner.setOnClickListener(this@EditOffer)
        view_inventory.setOnClickListener(this@EditOffer)
        et_special_instructions!!.setOnTouchListener(this@EditOffer)
        val data = intent.getStringExtra("data")
        bookings = Gson().fromJson(
            data,
            object : TypeToken<UpcomingBookings?>() {}.type
        )
        timestamp_ = bookings.timestamp
        isInsurance_ = "" + bookings.isInsurance
        pickup_latitude = bookings.pickupLatitude
        pickup_longitude = bookings.pickupLongitude
        destination_latitude = bookings.destinationLatitude
        destination_longitude = bookings.destinationLongitude
        what_moving_ = bookings.inventoryItems
        Quick_Inventory_ = bookings.inventory_details
        et_special_instructions_ = bookings.specialInstructions
        pickup_floor_spinner.setText(bookings.pickupFloor)
        dropoff_floor_spinner.setText(bookings.dropoffFloor)

        if (bookings.pickupFloor.equals(CONSTANTS.category.get(0))) {
            pickup_lift_spinner.gone()
        } else {
            pickup_lift_spinner.visible()
        }


        if (bookings.dropoffFloor.equals(CONSTANTS.category.get(0))) {
            dropoff_lift_spinner.gone()
        } else {
            dropoff_lift_spinner.visible()
        }
        pickup_lift_spinner.setText(bookings.pickupLift)
        dropoff_lift_spinner.setText(bookings.dropoffLift)
        request_id = bookings.requestId
        pickupProperty = bookings.pickupProperty
        dropoffProperty = bookings.dropoffProperty
        pickup_ = bookings.pickup
        destination_ = bookings.destination
        val format_time = changeDateFormat(
            timestamp_, "yyyy-MM-dd HH:mm:ss",
            "dd-MMM-yyyy h:mm a"
        )
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            date_current = format.parse(timestamp_)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        et_special_instructions!!.setText(et_special_instructions_)
        tv_job_date!!.text = format_time
        tv_pickup!!.text = pickup_
        tv_drop_off!!.text = destination_
        tv_stops_data!!.setText(bookings.otherStops)
        tv_proprty_type_pickup!!.text = pickupProperty
        tv_proprty_type_dropoff!!.text = dropoffProperty

        view_inventory!!.text = "${what_moving_} - ${Quick_Inventory_!!}"


        if (isInsurance_ == "0") {
            insurance_!!.text = CONSTANTS.Standard_insurance_
        } else {
            insurance_!!.text = CONSTANTS.costs_insurance_extra
        }
        getInventoryImagesList.clear()
        for (i in 0..5) {
            getInventoryImagesList.add("")
        }
        AlertDialogManager.showAlertMessage(this, getString(R.string.edit_address_alert))

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_address -> {
                ispickup = true
                showAlertMessage_fixed(
                    this@EditOffer, getString(R.string.edit_address_msg2),
                    "edit_address", this@EditOffer
                )
            }
            R.id.edit_address2 -> {
                ispickup = false
                showAlertMessage_fixed(
                    this@EditOffer, getString(R.string.edit_address_msg2),
                    "edit_address", this@EditOffer
                )
            }


            R.id.insurance_ -> showInsuranceAlert()
  /*          R.id.rel_free -> {
                alertDialog!!.tv_free!!.setTextColor(Color.WHITE)
                alertDialog!!.tv_ten!!.setTextColor(Color.BLACK)
                alertDialog!!.rel_free!!.setBackgroundColor(Color.parseColor("#1E71FD"))
                alertDialog!!.rel_ten!!.setBackgroundColor(Color.parseColor("#ffffff"))
                insurance_!!.text = CONSTANTS.Standard_insurance_
                isInsurance_ = "0"
            }
            R.id.rel_ten -> {
                alertDialog!!.tv_free!!.setTextColor(Color.BLACK)
                alertDialog!!.tv_ten!!.setTextColor(Color.WHITE)
                alertDialog!!.rel_ten!!.setBackgroundColor(Color.parseColor("#1E71FD"))
                alertDialog!!.rel_free!!.setBackgroundColor(Color.parseColor("#ffffff"))
                isInsurance_ = "1"
                insurance_!!.text = CONSTANTS.costs_insurance_extra
            }*/
            R.id.btn_continue -> alertDialog!!.dismiss()
            R.id.UPADTE_OFFER -> {
                Check_Validation()
            }
            R.id.tv_job_date ->{
                DateTimePicker.ShowDatePicker(
                    supportFragmentManager,
                    listener
                )
                Utils.showToastLong(getString(R.string.job_time))
            }

            R.id.cancel -> alertDialog!!.dismiss()
            R.id.tv_proprty_type_pickup -> {
                is_pickup_property = true
                showProprtyTypesAlert()
            }
            R.id.tv_proprty_type_dropoff -> {
                is_pickup_property = false
                showProprtyTypesAlert()
            }
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
            R.id.recycling -> {
                if (is_pickup_property) {
                    tv_proprty_type_pickup!!.text = alertDialog!!.recycling.text.toString()

                } else {
                    tv_proprty_type_dropoff!!.text = alertDialog!!.recycling.text.toString()

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
            R.id.view_inventory -> add_inventory_Dialog()

            R.id.add_Demensions -> startActivity(
                Intent(this@EditOffer, ViewDeminsion::class.java)
                    .putExtra("requestid", "" + request_id)
            )
            R.id.add_photo -> Open_Picture_Dialog()
            R.id.Cancel_Picture -> alertDialog_Picture!!.dismiss()
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
            R.id.edit_inventory -> {
                dialog!!.dismiss()
                add_or_edit = "edit"
                open_botton_sheet()
            }
            R.id.Cancel -> alertDialog!!.dismiss()
            R.id.delete_inventory -> {
                dialog!!.dismiss()
                setOnAlertDialogButtonClicks(this@EditOffer)
                showAlertMessageTwoButtons(
                    this@EditOffer,
                    "Delete Inventory",
                    "Are you sure you want to delete this inventory?", "Yes", "No"
                )
            }
            R.id.View_inventory -> {
                dialog!!.dismiss()
                val link = inventories!![current_position].picture
                startActivity(
                    Intent(this@EditOffer, ZoomImage::class.java)
                        .putExtra(CONSTANTS.image_link, link)
                )
            }
            R.id.pickup_floor_spinner -> PickupFloor(v)
            R.id.dropoff_floor_spinner -> DrofoffFloor(v)
            R.id.pickup_lift_spinner -> PickupLift(v)
            R.id.dropoff_lift_spinner -> DrofoffLift(v)
        }
    }

    private fun DrofoffFloor(v: View) {
        val popup =
            PopupMenu(this@EditOffer, v)
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
            true
        }
        popup.show()
    }

    private fun PickupLift(v: View) {
        val popup =
            PopupMenu(this@EditOffer, v)
        for (category in CONSTANTS.categorylift) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            pickup_lift_spinner!!.text = item_
            true
        }
        popup.show()
    }

    private fun DrofoffLift(v: View) {
        val popup =
            PopupMenu(this@EditOffer, v)
        for (category in CONSTANTS.categorylift) {
            popup.menu.add(category)
        }
        popup.setOnMenuItemClickListener { item ->
            val item_ = item.title.toString()
            dropoff_lift_spinner!!.text = item_
            true
        }
        popup.show()
    }

    private fun PickupFloor(v: View) {
        val popup =
            PopupMenu(this@EditOffer, v)
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
            true
        }
        popup.show()
    }

    private fun check_camera_permission() {
        val Camera = Check_Camera()
        val READ = Check_READ_EXTERNAL_STORAGE()
        val WRITE = Check_WRITE_EXTERNAL_STORAGE()
        if (!Camera
            || !READ || !WRITE
        ) {
            ActivityCompat.requestPermissions(
                this@EditOffer, arrayOf(
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
            this@EditOffer,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Check_READ_EXTERNAL_STORAGE(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@EditOffer,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun Check_Camera(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@EditOffer,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun check_gallery_permission() {
        if (ContextCompat.checkSelfPermission(
                this@EditOffer,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this@EditOffer,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@EditOffer, arrayOf(
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
                Toast.makeText(this@EditOffer, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            5454 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                opne_gallery()
            } else {
                Toast.makeText(this@EditOffer, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun open_camera_intent() {
        val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(camera_intent, REQUEST_CODE_CAMERA)
    }

    private fun Open_Picture_Dialog() {
        val builder =
            AlertDialog.Builder(this@EditOffer)
        builder.setView(R.layout.add_photo2)
        alertDialog_Picture = builder.create()
        alertDialog_Picture!!.show()
        alertDialog_Picture!!.Cancel_Picture!!.setOnClickListener(this@EditOffer)
        Load_Inventory()
    }

    private fun Load_Inventory() {
        showDialog2(this)
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = request_id
        postParam["operation"] = "read"
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam as Map<*, *>).toString()
        )
        CONSTANTS.mApiService.read_inventory(body, CONSTANTS.headers)!!
            .enqueue(object : Callback<InventoryModel?> {
                override fun onResponse(
                    call: Call<InventoryModel?>,
                    response: Response<InventoryModel?>
                ) {
                    if (response.body() != null) {
                        closeDialog()
                        if (response.body()!!.status!!.code == "1000") {
                            inventories = response.body()!!.inventories
                            if (inventories!!.size > 0) {
                                for (i in inventories!!.indices) {
                                    getInventoryImagesList[i] = inventories!![i].picture
                                }
                            } else {
                                getInventoryImagesList.clear()
                                for (i in 0..5) {
                                    getInventoryImagesList.add("")
                                }
                            }
                            alertDialog_Picture!!.inventory_rcv!!.adapter = InventoryAdaptor(
                                this@EditOffer, getInventoryImagesList,
                                this@EditOffer
                            )
                            closeDialog()
                        } else {
                            closeDialog()
                            Toast.makeText(
                                this@EditOffer,
                                response.body()!!.status!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        closeDialog()
                        Toast.makeText(this@EditOffer, response.raw().message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<InventoryModel?>,
                    e: Throwable
                ) {
                    Utils.showToast(e.message)
                    closeDialog()
                }
            })
    }

    private fun add_inventory_Dialog() {
        val builder =
            AlertDialog.Builder(this)
        builder.setView(R.layout.add_inventory)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.show()
        if (!TextUtils.isEmpty(Quick_Inventory_)) {
            alertDialog!!.Quick_Inventory!!.setText(Quick_Inventory_)
        }
        if (!TextUtils.isEmpty(what_moving_)) {
            alertDialog!!.what_moving!!.text = what_moving_
        }
        alertDialog!!.close_inventory!!.setOnClickListener {
            alertDialog!!.Quick_Inventory!!.hideKeyboard()
            alertDialog!!.dismiss()

        }
        alertDialog!!.what_moving!!.setOnClickListener {
            ShowServiceList(it)
        }

        alertDialog!!.done_invenotry!!.setOnClickListener {
            what_moving_ = alertDialog!!.what_moving!!.text.toString()
            Quick_Inventory_ = alertDialog!!.Quick_Inventory!!.text.toString()

            view_inventory!!.text = "${what_moving_} - ${Quick_Inventory_!!}"

            alertDialog!!.Quick_Inventory!!.hideKeyboard()
            alertDialog!!.dismiss()
        }
        alertDialog!!.add_Demensions!!.setOnClickListener(this@EditOffer)
        alertDialog!!.add_photo!!.setOnClickListener(this@EditOffer)
    }


    private fun ShowServiceList(v: View) {
        val popup = android.widget.PopupMenu(this, v)
        for (title in Utils.GetServicesList) {
            popup.menu.add(title.name)
        }
        popup.setOnMenuItemClickListener { item ->
            alertDialog!!.what_moving!!.text = item.title.toString()
            true
        }
        popup.show()
    }

    private fun Check_Validation() {
        pickup_floor = pickup_floor_spinner!!.text.toString()
        dropoff_floor = dropoff_floor_spinner!!.text.toString()
        pickup_lift = pickup_lift_spinner!!.text.toString()
        dropoff_lift = dropoff_lift_spinner!!.text.toString()
        if (pickup_floor.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select pick up floor.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (dropoff_floor.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select drop off floor.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (pickup_floor != CONSTANTS.Ground && pickup_lift.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select pick up lift.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (dropoff_floor != CONSTANTS.Ground && dropoff_lift.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select drop off lift.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (TextUtils.isEmpty(tv_proprty_type_pickup!!.text.toString())) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select pick up property type.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else if (TextUtils.isEmpty(tv_proprty_type_dropoff!!.text.toString())) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Select drop off property type.", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        }
//        else if (TextUtils.isEmpty(tv_stops_data!!.text.toString())) {
//            val snackbar = Snackbar
//                .make(
//                    coordinatorLayout!!,
//                    "Please select additional stops", Snackbar.LENGTH_LONG
//                )
//            snackbar.show()
//        }
        else if (view_inventory!!.text == " - ") {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Please add inventory", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        }  else if (et_special_instructions!!.text.isEmpty()) {
            val snackbar = Snackbar
                .make(
                    coordinatorLayout!!,
                    "Please add special instructions if any", Snackbar.LENGTH_LONG
                )
            snackbar.show()
        } else {
            if (pickup_floor == CONSTANTS.Ground) {
                pickup_lift = "No"
            }
            if (dropoff_floor == CONSTANTS.Ground) {
                dropoff_lift = "No"
            }
            Update_Offer()
        }
    }

    private fun showProprtyTypesAlert() {
        val builder =
            AlertDialog.Builder(this@EditOffer)
        val inflater = this@EditOffer.layoutInflater
        builder.setView(inflater.inflate(R.layout.alert_property_type, null))
        alertDialog = builder.create()
        alertDialog!!.show()
        alertDialog!!.setCanceledOnTouchOutside(true)


        alertDialog!!.tv_one_bed_house!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_two_bed_house!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_three_bed_house!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_four_plus_bed_house!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_storage!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_flat_share!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_flat_one_bed!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_flat_two_bed!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_three__bed_flat!!.setOnClickListener(this@EditOffer)
        alertDialog!!.tv_flat_four_plus_bed!!.setOnClickListener(this@EditOffer)
        alertDialog!!.Cancel!!.setOnClickListener(this@EditOffer)
        alertDialog!!.Basement!!.setOnClickListener(this@EditOffer)
        alertDialog!!.recycling!!.setOnClickListener(this@EditOffer)
    }

    private fun Update_Offer() {
        progress_bar!!.visibility = View.VISIBLE
        val postParam =
            HashMap<String?, String?>()
        postParam["pickup_door"] = ""
        postParam["dropoff_door"] = ""
        postParam["pickup_latitude"] = pickup_latitude
        postParam["pickup_longitude"] = pickup_longitude
        postParam["destination_latitude"] = destination_latitude
        postParam["destination_longitude"] = destination_longitude
        postParam["destination"] = destination_
        postParam["pickup"] = pickup_
        postParam["request_id"] = request_id
        postParam["is_insurance"] = isInsurance_
        postParam["timestamp"] = timestamp_
        postParam["other_Stops"] = tv_stops_data.text.toString()
        postParam["inventory_items"] = what_moving_
        postParam["inventory_details"] = Quick_Inventory_
        postParam["special_instructions"] = et_special_instructions!!.text.toString()
        postParam["pickup_floor"] = pickup_floor
        postParam["dropoff_floor"] = dropoff_floor
        postParam["pickup_lift"] = pickup_lift
        postParam["dropoff_lift"] = dropoff_lift
        postParam["dropoff_property"] = tv_proprty_type_dropoff!!.text.toString()
        postParam["pickup_property"] = tv_proprty_type_pickup!!.text.toString()
        val `object` = JSONObject(postParam as Map<*, *>)
        println("Testing:$`object`")
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam as Map<*, *>).toString()
        )
        CONSTANTS.mApiService.move_edit(body, CONSTANTS.headers)!!
            .enqueue(object : Callback<GenenalModel?> {
                override fun onResponse(
                    call: Call<GenenalModel?>,
                    response: Response<GenenalModel?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.status!!.code == "1000") {


                            val type = intent.getStringExtra("type").let {
                                if (it!!.equals(CONSTANTS.Regular)) {
                                    EventBus.getDefault().post(
                                        MessageEvent(
                                            CONSTANTS.edit_move,
                                            ""
                                        )
                                    )
                                    Toast.makeText(
                                        this@EditOffer,
                                        "Update Move Successful",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else if (it.equals(CONSTANTS.Fixed)) {
                                    EventBus.getDefault().post(
                                        MessageEvent(
                                            CONSTANTS.edit_offer,
                                            ""
                                        )
                                    )
                                    Toast.makeText(
                                        this@EditOffer,
                                        "Update Offer Successful.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }

                            finish()

                        } else {
                            Toast.makeText(
                                this@EditOffer,
                                response.body()!!.status!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@EditOffer, response.raw().message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    progress_bar!!.visibility = View.GONE
                }

                override fun onFailure(
                    call: Call<GenenalModel?>,
                    t: Throwable
                ) {
                    progress_bar!!.visibility = View.GONE
                    Utils.showToast(t.message)
                }
            })
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                if (requestCode == REQUEST_CODE_GALLERY) {
                    val image_path = intent.data
                    try {
                        bitmap_img =
                            MediaStore.Images.Media.getBitmap(contentResolver, image_path)
                        val base_64_image = getStringImage(bitmap_img!!)
                        if (add_or_edit == "edit") {
                            EditInventory(base_64_image)
                        } else {
                            uploadInventoryPicture(base_64_image)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (requestCode == REQUEST_CODE_CAMERA) {
                    bitmap_img = intent.extras!!["data"] as Bitmap?
                    val base_64_image =
                        getStringImage(bitmap_img!!)
                    if (add_or_edit == "edit") {
                        EditInventory(base_64_image)
                    } else {
                        uploadInventoryPicture(base_64_image)
                    }
                }
            }
        }
    }

    private fun EditInventory(picture_string: String) {
        val list: MutableList<String> =
            ArrayList()
        list.add(picture_string)
        var jsonArray: JSONArray? = JSONArray()
        val listString = Gson().toJson(
            list,
            object : TypeToken<ArrayList<String?>?>() {}.type
        )
        try {
            jsonArray = JSONArray(listString)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        showDialog2(this)
        postParam["request_id"] = request_id
        postParam["picture"] = jsonArray
        postParam["operation"] = "edit"
        postParam["inventory_id"] = inventories!![current_position].invetory_id
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        ShowParamter(postParam, "UmerSheraz_test")
        CONSTANTS.mApiService.update_request_inventory_passenger(body, CONSTANTS.headers)!!
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.body() != null) {
                        closeDialog()
                        Load_Inventory()
                    } else {
                        closeDialog()
                        Toast.makeText(this@EditOffer, response.raw().message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    e: Throwable
                ) {
                    Utils.showToast(e.message)
                    closeDialog()
                }
            })
    }

    private fun Open_Chrome(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            startActivity(intent)
        } catch (ex: Exception) {
            Toast.makeText(this@EditOffer, ex.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showInsuranceAlert() {
        val builder =
            AlertDialog.Builder(this@EditOffer)
        builder.setView(R.layout.alert_insurance)
        alertDialog = builder.create()
        alertDialog!!.show()

        alertDialog!!.term_condition_insurance!!.setOnClickListener { Open_Chrome(CONSTANTS.insurance_link) }
       // alertDialog!!.rel_free!!.setOnClickListener(this@EditOffer)
      //  alertDialog!!.rel_ten!!.setOnClickListener(this@EditOffer)
        alertDialog!!.btn_continue!!.setOnClickListener(this@EditOffer)
        alertDialog!!.close_insurance!!.setOnClickListener {
            alertDialog!!.dismiss()
        }
      /*  if (isInsurance_ == "0") {
            alertDialog!!.rel_free!!.performClick()
        } else {
            alertDialog!!.rel_ten!!.performClick()
        }*/
    }

    private val listener: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            val compare_format =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            timestamp_ = compare_format.format(date)
            val format_time = changeDateFormat(
                timestamp_,
                "yyyy-MM-dd HH:mm:ss",
                "dd-MMM-yyyy h:mm a"
            )
            tv_job_date!!.text = format_time
        }

        override fun onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    }

    private fun uploadInventoryPicture(picture_string: String) {
        val list: MutableList<String> =
            ArrayList()
        list.add(picture_string)
        var jsonArray: JSONArray? = JSONArray()
        val gson = Gson()
        val listString = gson.toJson(
            list,
            object : TypeToken<ArrayList<String?>?>() {}.type
        )
        try {
            jsonArray = JSONArray(listString)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        showDialog2(this)

        postParam["request_id"] = request_id
        postParam["picture"] = jsonArray
        postParam["operation"] = "add"
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.update_request_inventory_passenger(body, CONSTANTS.headers)!!
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.body() != null) {
                        closeDialog()
                        Load_Inventory()
                    } else {
                        closeDialog()
                        Toast.makeText(this@EditOffer, response.raw().message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    e: Throwable
                ) {
                    Utils.showToast(e.message)
                    closeDialog()
                }
            })
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
        }
        v.onTouchEvent(event)
        return true
    }

    override fun onClickRecycler(view: View?, position: Int) {
        current_position = position
        if (getInventoryImagesList[position]!!.isEmpty()) {
            add_or_edit = "add"
            open_botton_sheet()
        } else {
            inventoy_option()
        }
    }

    private fun inventoy_option() {
        val view = layoutInflater.inflate(R.layout.inventoy_option, null)
        dialog = BottomSheetDialog(this@EditOffer)
        dialog!!.setContentView(view)
        dialog!!.show()
        view.edit_inventory.setOnClickListener(this@EditOffer)
        view.delete_inventory.setOnClickListener(this@EditOffer)
        view.View_inventory.setOnClickListener(this@EditOffer)
        view.Cancel.setOnClickListener { dialog!!.dismiss() }
    }

    private fun open_botton_sheet() {
        val view1 = layoutInflater.inflate(R.layout.selected_images_layout, null)
        dialog = BottomSheetDialog(this@EditOffer)
        dialog!!.setContentView(view1)
        dialog!!.show()
        view1.gallery.setOnClickListener(this@EditOffer)
        view1.camera.setOnClickListener(this@EditOffer)
        view1.Cancel_2.setOnClickListener { dialog!!.dismiss() }
    }

    private fun Delete_Inventory(inventory_id: String?, current_position: Int) {
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        showDialog2(this)
        postParam["request_id"] = request_id
        postParam["inventory_id"] = inventory_id
        postParam["operation"] = "delete"
        ShowParamter(postParam, "abc")
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.read_inventory(body, CONSTANTS.headers)!!
            .enqueue(object : Callback<InventoryModel?> {
                override fun onResponse(
                    call: Call<InventoryModel?>,
                    response: Response<InventoryModel?>
                ) {
                    if (response.body() != null) {
                        closeDialog()
                        if (response.body()!!.status!!.code == "1000") {
                            getInventoryImagesList.clear()
                            for (i in 0..5) {
                                getInventoryImagesList.add("")
                            }
                            Load_Inventory()
                        } else {
                            Toast.makeText(
                                this@EditOffer,
                                response.body()!!.status!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        closeDialog()
                        Toast.makeText(this@EditOffer, response.raw().message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<InventoryModel?>,
                    e: Throwable
                ) {
                    Utils.showToast(e.message)
                    closeDialog()
                }
            })
    }

    override fun onDialogPositiveButtonPressed() {
        Delete_Inventory(inventories!![current_position].invetory_id, current_position)
    }

    override fun onDialogNegativeButtonPressed() {}
    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "edit_address") {
            ShoeEditDialog()
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {}
    private fun ShoeEditDialog() {
        val alertDialog =
            AlertDialog.Builder(this@EditOffer!!)
                .setTitle("Edit Address")
        val input = EditText(this@EditOffer)
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
                Toast.makeText(this@EditOffer, "Address cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                input.hideKeyboard()
                dialogInterface.dismiss()
                ChnageAddress(question)
            }
        }.setNegativeButton("Cancel") { dialogInterface, i ->
            input.hideKeyboard()
            dialogInterface.dismiss()
        }.show()
    }

    private fun ChnageAddress(address: String) {
        if (ispickup) {
            pickup_ = address
            tv_pickup!!.text = address
        } else {
            destination_ = address
            tv_drop_off!!.text = address
        }
    }
}