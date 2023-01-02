package com.vanmove.passesger.activities.OfferConfirm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kaopiz.kprogresshud.KProgressHUD
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.fragments.OfferHistory.BookedOffersMain
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.model.APIModel.BookingDetailRespone
import com.vanmove.passesger.model.DeminsionModel
import com.vanmove.passesger.utils.*
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage_fixed
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.hideKeyboard
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.alert_property_type.*
import kotlinx.android.synthetic.main.future_request_summary.*
import kotlinx.android.synthetic.main.future_request_summary.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.util.*

class OfferConfimrationFragmence : Fragment(R.layout.future_request_summary),
    View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
    OnClickTwoButtonsAlertDialog {
    var pickup_address: String? = null
    var pickup_lat: String? = null
    var pickup_lon: String? = null
    var destination_address: String? = null
    var destination_lat: String? = null
    var destination_lon: String? = null
    var insurance: String? = "0"
    var vehicle_name: String? = null
    var rate_per_mile: String? = null
    var rate_per_minute: String? = null
    var rate_per_hour: String? = null
    var helpers: String? = null
    var passenger_id: String? = null

    var estimated_duartion: String? = ""
    var estimated_payment: String? = ""
    private var str_phone: String? = null
    private var RegistrationID: String? = null
    private var fname: String? = null
    private var lname: String? = null
    private var vehicle_class_id: String? = null
    private var progressDialog: KProgressHUD? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var gps: GPSTracker? = null
    var second_date: String? = null
    private var str_inventry: String? = ""
    private var quick_inventory_: String? = ""
    private var str_aditional_info: String? = ""
    private var offered_amount: String? = null
    private var date_booking: String? = null
    private var is_flexible: String? = null

    var alertDialog: AlertDialog? = null
    private var pickup_floor = ""
    private var dropoff_floor = ""
    private var pickup_lift = ""
    private var dropoff_lift = ""

    var is_pickup_property = false
    var request_id = ""
    var ispickup = false
    var sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
    var c = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = DialogUtils.showProgressDialog(context!!, cancelable = false)

        edit_address.setOnClickListener(this)
        edit_address2.setOnClickListener(this)
        linkViews()
        showAlertMessage(context, getString(R.string.edit_address_alert))
    }


    private fun linkViews() {
        gps = GPSTracker(activity, context)
        if (gps!!.canGetLocation()) {
            latitude = gps!!.latitude
            longitude = gps!!.longitude
        } else {
            gps!!.showSettingsAlert()
        }
        str_phone =
            getPreferences(CONSTANTS.mobile_passenger, context!!)
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        fname = getPreferences(CONSTANTS.first_name, context!!)
        lname = getPreferences(CONSTANTS.last_name, context!!)
        vehicle_class_id =
            getPreferences(CONSTANTS.vehicle_class_id, context!!)
        pickup_address = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
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
        destination_address = getPreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
            context!!
        )
        insurance = getPreferences(CONSTANTS.insurance, context!!)
        helpers = getPreferences(CONSTANTS.helpers, context!!)
        vehicle_name = getPreferences(CONSTANTS.vehicle_name, context!!)
        rate_per_mile =
            getPreferences(CONSTANTS.rate_per_mile, context!!)
        rate_per_minute =
            getPreferences(CONSTANTS.rate_per_minute, context!!)
        rate_per_hour =
            getPreferences(CONSTANTS.rate_per_hour, context!!)
        is_flexible = getPreferences(CONSTANTS.is_flexible, context!!)
        date_booking =
            getPreferences(CONSTANTS.date_booking, context!!)
        second_date =
            getPreferences(CONSTANTS.second_date, context!!)
        str_inventry =
            getPreferences(CONSTANTS.str_inventry, context!!)
        quick_inventory_ =
            getPreferences(CONSTANTS.str_inventry_detail, context!!)
        str_aditional_info =
            getPreferences(CONSTANTS.str_aditional_info, context!!)
        offered_amount =
            getPreferences(CONSTANTS.offered_amount, context!!)

        view!!.tv_client_name!!.text = "$fname $lname"
        view!!.tv_client_telephone!!.text = str_phone
        view!!.tv_drop_off!!.text = destination_address
        view!!.tv_pickup!!.text = pickup_address

        if (is_flexible.equals("Yes")) {
            c.time = Date()
            c.add(Calendar.DATE, 7)
            date_booking = sdf.format(c.time)
            view!!.tv_job_date!!.text = "It is a Flexible Booking"
        } else {

            if (second_date!!.isEmpty()) {
                view!!.tv_job_date!!.text = date_booking
            } else {
                view!!.tv_job_date!!.text = date_booking + "\n" +
                        second_date
            }
        }

        view!!.tv_helpers!!.text = "$vehicle_name, $helpers helper(s)"
        view!!.tv_amount!!.text = CONSTANTS.CURRENCY + offered_amount

        view!!.tv_info_data!!.text = str_aditional_info
        view!!.quick_inventory!!.text = "${str_inventry}\n${quick_inventory_}"
        view!!.tv_proprty_type_pickup!!.setOnClickListener(this)
        view!!.tv_proprty_type_dropoff!!.setOnClickListener(this)
        view!!.tv_requestVan.setOnClickListener(this)
        view!!.pickup_floor_spinner.setOnClickListener(this)
        view!!.dropoff_floor_spinner.setOnClickListener(this)
        view!!.pickup_lift_spinner.setOnClickListener(this)
        view!!.dropoff_lift_spinner.setOnClickListener(this)

        if (insurance == CONSTANTS.Standard_insurance_) {
            Insurance_Cover!!.text = CONSTANTS.Standard_insurance_
        } else {
            Insurance_Cover!!.text = CONSTANTS.costs_insurance_extra
        }
        tv_requestVan!!.text = "Request ${vehicle_name}"

    }

    private fun MakeOffer() {
        pickup_floor = pickup_floor_spinner!!.text.toString()
        dropoff_floor = dropoff_floor_spinner!!.text.toString()
        pickup_lift = pickup_lift_spinner!!.text.toString()
        dropoff_lift = dropoff_lift_spinner!!.text.toString()
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
        estimated_duartion = getPreferences(CONSTANTS.estimated_duartion, context!!)
        estimated_payment = getPreferences(
            CONSTANTS.estimated_payment,
            context!!
        )
        destination_address = tv_drop_off!!.text.toString()
        pickup_address = tv_pickup!!.text.toString()
        val property_type_pickup = tv_proprty_type_pickup!!.text.toString()
        val property_type_droppoff = tv_proprty_type_dropoff!!.text.toString()
        if (pickup_floor.isEmpty()) {
            Toast.makeText(context, "Select pick up floor", Toast.LENGTH_SHORT).show()
        } else if (dropoff_floor.isEmpty()) {
            Toast.makeText(context, "Select drop off floor.", Toast.LENGTH_SHORT).show()
        } else if (pickup_floor != CONSTANTS.Ground && pickup_lift.isEmpty()) {
            Toast.makeText(context, "Select pick up lift.", Toast.LENGTH_SHORT).show()
        } else if (dropoff_floor != CONSTANTS.Ground && dropoff_lift.isEmpty()) {
            Toast.makeText(context, "Select drop off lift.", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(property_type_pickup)) {
            Toast.makeText(context, "Select pick up property type", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(property_type_droppoff)) {
            Toast.makeText(context, "Select drop off property type.", Toast.LENGTH_SHORT).show()
        } else {
            val pay_by =
                getPreferences(CONSTANTS.PAY_BY, context!!)
            if (pickup_floor == CONSTANTS.Ground) {
                pickup_lift = "No"
            }
            if (dropoff_floor == CONSTANTS.Ground) {
                dropoff_lift = "No"
            }
            new_offer_request(
                "" + latitude, "" + longitude, pickup_lat,
                pickup_lon, destination_lat, destination_lon, pay_by, vehicle_class_id,
                quick_inventory!!.text.toString(),
                destination_address!!, pickup_address!!,
                str_aditional_info, str_inventry, helpers,
                insurance!!, date_booking, offered_amount,
                second_date
            )
        }
    }

    private fun new_offer_request(
        passenger_latitude: String,
        passenger_longitude: String,
        pickup_latitude: String?,
        pickup_longitude: String?,
        destination_latitude: String?,
        destination_longitude: String?,
        payment_type: String?,
        vehicle_class_id: String?,
        details: String,
        destination: String,
        pickup: String,
        special_instructions: String?,
        inventory_items: String?,
        helpers_count: String?,
        is_insurance: String,
        date_booking: String?,
        offered_amount: String?,
        second_date: String?
    ) {
        progressDialog!!.show()

        var service_id =
            getPreferences(CONSTANTS.SERVICE_ID, context!!)
        val url = Utils.new_offer_request
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, Any?> =
            HashMap()

        postParam["inventory_details"] = details
        postParam["inventory_items"] = inventory_items
        postParam["special_instructions"] = special_instructions
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
        postParam["helpers_count"] = helpers_count
        postParam["is_insurance"] = is_insurance
        postParam["timestamp"] = date_booking
        postParam["pickup_door"] = ""
        postParam["dropoff_door"] = ""
        postParam["pickup_floor"] = pickup_floor
        postParam["dropoff_floor"] = dropoff_floor
        postParam["pickup_lift"] = pickup_lift
        postParam["dropoff_lift"] = dropoff_lift
        postParam["pickup_property"] = tv_proprty_type_pickup!!.text.toString()
        postParam["dropoff_property"] = tv_proprty_type_dropoff!!.text.toString()
        postParam["start_offer_date"] = second_date
        postParam["offered_price"] = offered_amount
        postParam["is_flexible"] = is_flexible
        postParam["estimated_duartion"] = estimated_duartion
        postParam["estimated_payment"] = estimated_payment
        val dismantling = getPreferences(CONSTANTS.dismantling, context!!)
        postParam["is_assembling"] = dismantling
        if (TextUtils.isEmpty(service_id)) {
            service_id = "1"
        }
        postParam["service_id"] = service_id


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
            val ImagesList = ArrayList<String>()
            for (i in Utils.getInventoryImagesList.indices) {
                if (!Utils.getInventoryImagesList[i].isEmpty()) {
                    ImagesList.add(Utils.getInventoryImagesList[i])
                }
            }
            val listString = Gson().toJson(
                ImagesList,
                object : TypeToken<ArrayList<String?>?>() {}.type
            )
            val jsonArray = JSONArray(listString)
            if (jsonArray.length() > 0) {
                postParam["picture"] = jsonArray
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val test = Gson().toJson(postParam)
        print("TESTING UMER SHEARZ  " + test)
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.new_offer_request(body, CONSTANTS.headers)!!
            .enqueue(object : Callback<BookingDetailRespone?> {
                override fun onResponse(
                    call: Call<BookingDetailRespone?>,
                    response: retrofit2.Response<BookingDetailRespone?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.status!!.code == "1000") {


                            request_id = response.body()!!.request!!.requestId!!
                            showAlertMessageWithTwoButtons(
                                context,
                                this@OfferConfimrationFragmence,
                                "Offer_Alert", "Offer Alert",
                                """
                                    Thank you for your offer.Your offer is now being circulated to all matching drivers in your area.Once a local driver in your area has accepted your offer, you will be notified via the app and email.
                                    In the mean time sit back and relax.
                                    """.trimIndent(),
                                "View offer", "OK"
                            )

                            NotificationUtils().showNotification(
                                getString(R.string.app_name),
                                "New Upcoming job created"
                            )


                            // Clear dATA
                            Utils.deminsionList.clear()
                            Utils.getInventoryImagesList.clear()
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
                    progressDialog!!.dismiss()
                }

                override fun onFailure(
                    call: Call<BookingDetailRespone?>,
                    t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    Utils.showToast(t.message)
                }
            })


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_address -> {
                ispickup = true
                val msg =
                    getString(R.string.edit_address_msg)
                showAlertMessage_fixed(
                    context, msg,
                    "edit_address", this@OfferConfimrationFragmence
                )
            }
            R.id.edit_address2 -> {
                ispickup = false
                val msg2 = getString(R.string.edit_address_msg)
                showAlertMessage_fixed(
                    context, msg2,
                    "edit_address", this@OfferConfimrationFragmence
                )
            }
            R.id.tv_requestVan -> MakeOffer()
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
            R.id.pickup_floor_spinner -> PickupFloor(v)
            R.id.dropoff_floor_spinner -> DrofoffFloor(v)
            R.id.pickup_lift_spinner -> PickupLift(v)
            R.id.dropoff_lift_spinner -> DrofoffLift(v)
        }
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
            true
        }
        popup.show()
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
            true
        }
        popup.show()
    }

    private fun showProprtyTypesAlert() {
        val builder =
            AlertDialog.Builder(context!!)
        val inflater = this.layoutInflater
        builder.setView(inflater.inflate(R.layout.alert_property_type, null))
        alertDialog = builder.create()
        alertDialog!!.show()
        alertDialog!!.setCanceledOnTouchOutside(true)


        alertDialog!!.tv_one_bed_house!!.setOnClickListener(this)
        alertDialog!!.tv_two_bed_house!!.setOnClickListener(this)
        alertDialog!!.tv_three_bed_house!!.setOnClickListener(this)
        alertDialog!!.tv_four_plus_bed_house!!.setOnClickListener(this)
        alertDialog!!.tv_storage!!.setOnClickListener(this)
        alertDialog!!.tv_flat_share!!.setOnClickListener(this)
        alertDialog!!.tv_flat_one_bed!!.setOnClickListener(this)
        alertDialog!!.tv_flat_two_bed!!.setOnClickListener(this)
        alertDialog!!.tv_three__bed_flat!!.setOnClickListener(this)
        alertDialog!!.tv_flat_four_plus_bed!!.setOnClickListener(this)
        alertDialog!!.Basement!!.setOnClickListener(this)
        alertDialog!!.recycling!!.setOnClickListener(this)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        showToast(connectionResult.errorMessage)
    }

    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "Offer_Alert") {
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            val fragment: Fragment
            fragment = BookedOffersMain()
            val bundle = Bundle()
            bundle.putString(CONSTANTS.move_to_offer_upcoming, CONSTANTS.move_to_offer_upcoming)
            fragment.setArguments(bundle)
            transaction.replace(R.id.container_fragments, fragment)
            transaction.commit()
            activity.let {
                startActivity(Intent(it, MainScreenActivity::class.java).apply {
                    putExtra(CONSTANTS.move_to_offer_upcoming, CONSTANTS.move_to_offer_upcoming)
                })
                it!!.finish()
            }

        } else if (dialog_name == "edit_address") {
            ShoeEditDialog()
        }
    }

    private fun ShoeEditDialog() {
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

    override fun clickNegativeDialogButton(dialog_name: String?) {
        if (dialog_name == "Offer_Alert") {
            val intent = Intent(activity, MainScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity!!.finish()
        }
    }

    companion object {
        private var fragment_view: View? = null
    }
}