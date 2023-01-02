package com.vanmove.passesger.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.security.ProviderInstaller
import com.vanmove.passesger.BuildConfig
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.TimeSlotsAdapter
import com.vanmove.passesger.animationpackage.AnimationForViews
import com.vanmove.passesger.animationpackage.IsAnimationEndedCallback
import com.vanmove.passesger.fragments.SelectVehicle
import com.vanmove.passesger.interfaces.BaseApiService
import com.vanmove.passesger.model.*
import com.vanmove.passesger.universal.AppController
import com.vanmove.passesger.utils.CONSTANTS.Flexible
import com.vanmove.passesger.utils.RetrofitClient.getClient
import kotlinx.android.synthetic.main.make_offer.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.SSLContext
import kotlin.collections.ArrayList

object Utils {
    var userType = "passenger"
    var imageUrl = "https://vanmove.com/uploads/images/"
    private const val server ="https://techelites.in/vanmove/api/"
   // private const val server = "https://vanmove.com/api/"
    const val register_passengerUrl =
        server + "register_passenger.php"
    const val new_offer_request =
        server + "new_offer_request.php"
    const val new_upcoming_move =
        server + "new_upcoming_move.php"
    const val new_booking_request =
        server + "new_booking_request.php"
    const val Logout_Url = server + "logout.php"
    const val update_request_rating_passenger =
        server + "update_request_rating_passenger.php"
    const val porter_update_request_rating_user =
        server + "porter_update_request_rating_user.php"
    const val update_booking_request_status_passenger =
        server + "update_booking_request_status_passenger.php"
    const val load_driver_location =
        server + "load_driver_location.php"
    const val get_booking_detail =
        server + "get_booking_detail.php"
    const val get_active_booking_detail =
        server + "get_active_booking_detail.php"
    const val changePasswordUrl =
        server + "update_profile.php"
    const val forgot_password =
        server + "forgot-password.php"
    const val update_notify_booking =
        server + "update_notify_booking.php"
    const val loginUrl = server + "login_passenger.php"
    const val active_user = server + "active_user.php"
    const val porter_value = server + "porter_value.php"
    const val change_password = server + "change_password.php"
    const val get_passenger_upcomimg_moves =
        server + "get_passenger_upcomimg_moves.php"
    const val get_passenger_upcomimg_offer =
        server + "get_passenger_upcomimg_offer.php"
    const val get_passenger_previous_offer =
        server + "get_passenger_previous_offer.php"
    const val update_booking_payment_passenger =
        server + "update_booking_payment_passenger.php"
    const val update_offer_booking_payment_passenger =
        server + "update_offer_booking_payment_passenger.php"
    const val update_passenger_payment_method =
        server + "update_passenger_payment_method.php"
    const val mobile_check = server + "mobile_check.php"
    const val sendOtpUrl = server + "otp_send.php"
    const val get_vehicle_classes_passenger_all_Url =
        server + "get_vehicle_classes_passenger_all.php"
    const val get_insurance_plans =
        server + "get_insurance_plans.php"
    const val porter_online =
        server + "porter_online.php"
    const val get_passenger_previous_moves =
        server + "get_passenger_previous_moves.php"

    const val get_saved_addresses =
        server + "get_saved_addresses.php"

    const val saved_addresses =
        server + "save_addresses.php"


    const val update_request_inventory_passenger =
        server + "update_request_inventory_passenger.php"
    const val update_booking_location_passenger =
        server + "update_booking_location_passenger.php"
    const val porter_new_booking_request =
        server + "porter_new_booking_request.php"
    const val porter_get_booking_detail =
        server + "porter_get_booking_detail.php"
    const val porter_active_booking_passesger =
        server + "porter_active_booking_passesger.php"
    const val porter_get_users_bookings =
        server + "porter_get_users_bookings.php"
    const val cancel_porter_request =
        server + "cancel_porter_request.php"
    const val get_services = server + "get_services.php"
    const val stripe_customer =
        server + "stripe-customer.php"
    const val API_NAME_CHECK_OTP = server + "otp_check.php"
    var DISTANCE_TO_ROTATE = 20
    var GetMinTime = ArrayList<MinTime>()

    var GetVehicles = ArrayList<GetVehicles>()

    var OnlyVansList = ArrayList<GetVehicles>()
    var DriversArroundList = ArrayList<GetDrivers>()

    var getInventoryImagesList = ArrayList<String>()

    var GetServicesList = ArrayList<GetServices>()

    var deminsionList: ArrayList<DeminsionModel> =
        ArrayList()

    val aPIService: BaseApiService =
        getClient(server, AppController.getAppContext())!!.create(BaseApiService::class.java)


    fun getPreferences(key: String?, context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(
            CONSTANTS.SHARED_PREFERENCE_APP,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(key, "")
    }

    fun savePreferences(
        key: String?, value: String?,
        context: Context
    ): Boolean {
        val sharedPreferences = context.getSharedPreferences(
            CONSTANTS.SHARED_PREFERENCE_APP,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        if (value != null) {
            editor.putString(key, value)
        } else {
            editor.putString(key, "")
        }
        editor.commit()
        return true
    }
    fun compareTwoDate(my_date: String?): Boolean{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val strDate = sdf.parse(my_date)
        if (System.currentTimeMillis() ==strDate.time) {
           return true
        } else {
           return false
        }
    }
    fun compareDateTime(my_date: String?): Boolean{

        System.out.println("my_date date_future_booking_str::"+my_date)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val strDate = sdf.parse(my_date)
        if (System.currentTimeMillis()==strDate.time) {
            return true
        } else {
            return false
        }
    }
    fun changeDateFormat(
        time: String?,
        inputPattern: String?,
        outputPattern: String?
    ): String? {
        @SuppressLint("SimpleDateFormat") val inputFormat =
            SimpleDateFormat(inputPattern)
        @SuppressLint("SimpleDateFormat") val outputFormat =
            SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time!!)
            str = outputFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }


    fun getStringImage(bmp: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        //  return "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="

    }

    fun GetCurrentTimeStamp(): String {
     return   SimpleDateFormat("HH:mm dd-MMMM-yyyy").format(Date())
    }


    fun GetBitmapImage(base64: String?): Bitmap {
        val decodedString =
            Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }


    fun View.hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun closeKeyboard(context: Context) {
        val inputMethodManager: InputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


    fun KmToMile(distance_meter: String): String {
        var mileS_diatcne = "0.0"
        try {
            val dis = distance_meter.toDouble()
            val dis_mile = dis * CONSTANTS.Meter_Miles
            mileS_diatcne = String.format("%.2f", dis_mile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mileS_diatcne
    }


    fun Share_Application(context: Context) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            context.startActivity(Intent.createChooser(shareIntent, "Share Application"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun isValidMobile(phone: String?): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    open fun inPhoneNumberValidate(phone: String): Boolean {
        var result = false
        result = if (!isValidMobile(phone)) {
            false
        } else if (phone.toDouble() > 0) {
            true
        } else {
            false
        }
        return result
    }
    fun Open_Browse(context: Context, link: String?) {
        try {
            val i = Intent("android.intent.action.MAIN")
            i.data = Uri.parse(link)
            i.setPackage("com.android.chrome")
            context.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            // Chrome is not installed
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(i)
        }
    }


    fun Dial_Number(context: Context, number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun Open_Email_Intent(
        context: Context,
        email_address: Array<String?>?
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_EMAIL, email_address)
            intent.type = "text/html"
            intent.setPackage("com.google.android.gm")
            context.startActivity(Intent.createChooser(intent, "Send mail"))
        } catch (e: Exception) {
        }
    }


    fun WifiEnable(context: Context): Boolean {
        var mWifiEnable = false // Assume disabled
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mWifi = connManager
            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (mWifi!!.isConnected) {
            mWifiEnable = true
            return true
        }
        return false
    }


    fun isMobileDataEnable(context: Context): Boolean {
        var mobileDataEnabled = false // Assume disabled
        val cm = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            val cmClass = Class.forName(cm.javaClass.name)
            val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
            method.isAccessible = true // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = method.invoke(cm) as Boolean
        } catch (e: Exception) {
            // Some problem accessible private API and do whatever error
            // handling you want here
        }
        return mobileDataEnabled
    }


    fun showToast(txt: String?) {
        Toast.makeText(AppController.getAppContext(), txt, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(txt: String?) {
        Toast.makeText(AppController.getAppContext(), txt, Toast.LENGTH_LONG).show()
    }

    fun showToastTest(context: Context?, txt: String?) {
        //  Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }


    fun GetRequestBody(postParam: MutableMap<String?, Any?>) = RequestBody.create(
        "application/json; charset=utf-8".toMediaTypeOrNull(),
        JSONObject(postParam as Map<*, *>).toString()
    )


    fun showCustomDialog(context: Context?, message: String?) {
        val builder =
            AlertDialog.Builder(context!!)
        builder.setMessage(message)
        builder.setPositiveButton(
            "ok"
        ) { dialog, which -> }
        val alertDialog = builder.create()
        alertDialog.show()
    }
     fun InSurenaceAdd(total_fare: String,insurance_fare: String): String {
         val fare_ = total_fare.toDouble() + insurance_fare.toDouble()
         return String.format("%.2f", fare_)
    }

    fun updateTLS(context: Context?) {
        try {
            ProviderInstaller.installIfNeeded(context)
            var sslContext: SSLContext? = null
            try {
                sslContext = SSLContext.getInstance("TLSv1.2")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            try {
                assert(sslContext != null)
                sslContext!!.init(null, null, null)
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            val engine = sslContext!!.createSSLEngine()
            engine.enabledProtocols = arrayOf("TLSv1.2")
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }


    fun GetAddressFromLocation(
        context: Context?,
        lat: Double,
        lng: Double
    ): String {
        var address_return = ""
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                lat,
                lng,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address_return = addresses[0]
                .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return address_return
    }


    fun distance_two_point(start_point: LatLng, end_point: LatLng): Float {
        val loc1 = Location("")
        loc1.latitude = start_point.latitude
        loc1.longitude = start_point.longitude
        val loc2 = Location("")
        loc2.latitude = end_point.latitude
        loc2.longitude = end_point.longitude
        return loc1.distanceTo(loc2)
    }


    fun Show_Sms_intent(mobile: String, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.putExtra("sms_body", "")
            intent.data = Uri.parse("sms:$mobile")
            context.startActivity(intent)
        } catch (error: Exception) {
        }
    }


    fun isAppRunning(context: Context, packageName: String): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val procInfos =
            activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun ShowParamter(
        postParam: Map<String?, Any?>?,
        test_name: String
    ) {
        val jsonObject = JSONObject(postParam)
        println("$test_name:    $jsonObject")
    }

 /*   fun calculateFareUpto(
        hour: Double,
        vehicles: GetVehicles,
        distance_in_miles: String,
        context: Context
    ): String {
        return try {
            val rate_per_mile = vehicles.rate_per_mile_fare
            val rate_per_minute = vehicles.rate_per_minute_fare
            val rate_per_hour = vehicles.hourly_rate
            val rate_per_helper_per_hour = vehicles.rate_helper_hourly
            val rate_per_helper_per_minute = vehicles.rate_helper_per_minute
            val helpers = getPreferences(CONSTANTS.helpers, context)
            val ratePerMileFare = rate_per_mile.toDouble()
            val hourlyRate = rate_per_hour.toDouble()
            val rateHelperHourly = rate_per_helper_per_hour.toDouble()
            val ratePerMinuteFare = rate_per_minute.toDouble()
            val ratePerMinuteHelper = rate_per_helper_per_minute.toDouble()
            val helpers_count_ = helpers!!.toDouble()
            val distanceInMiles_ = distance_in_miles.toDouble()
            var rateAfterFirstHour = 0.0
            var uptoOneHourRate = ratePerMileFare * distanceInMiles_ + hourlyRate
            uptoOneHourRate += rateHelperHourly * helpers_count_
            val firstHourRate = uptoOneHourRate
            if (hour > 1) {
                val hourToMinutes = hour - 1.toDouble()
                val minatesAfterFirstHour = hourToMinutes * 60.0
                if (helpers_count_ > 0){
                    rateAfterFirstHour = minatesAfterFirstHour * ratePerMinuteFare +
                            minatesAfterFirstHour * ratePerMinuteHelper * helpers_count_
                }else{
                    rateAfterFirstHour = minatesAfterFirstHour * ratePerMinuteFare
                }
            }
            val FareUpto: Double
            FareUpto = if (vehicles.vehicle_id == "13") {
                var fareUpTodouble: Double =0.0
                if (helpers_count_ > 0) {
                    fareUpTodouble =   hour * helpers_count_ * hourlyRate
                }else{
                    fareUpTodouble =   hour * hourlyRate
                }
                fareUpTodouble
            } else {
                firstHourRate + rateAfterFirstHour
            }


            String.format("%.2f", FareUpto)
        } catch (error: Exception) {
            "0"
        }
    }*/

    fun calculateFareUpto(
        hour: Double,
        vehicles: GetVehicles,
        distance_in_miles: String,
        context: Context
    ): String {
        return try {
            val rate_per_mile = vehicles.rate_per_mile_fare
            val rate_per_minute = vehicles.rate_per_minute_fare
            val rate_per_hour = vehicles.hourly_rate
            val rate_per_helper_per_hour = vehicles.rate_helper_hourly
            val rate_per_helper_per_minute = vehicles.rate_helper_per_minute
            val helpers = getPreferences(CONSTANTS.helpers, context)
            val ratePerMileFare = rate_per_mile.toDouble()
            val vanHourlyRate = rate_per_hour.toDouble()
            val rateHelperHourly = rate_per_helper_per_hour.toDouble()
            val ratePerMinuteFare = rate_per_minute.toDouble()
            val ratePerMinuteHelper = rate_per_helper_per_minute.toDouble()
            val helpers_count_ = helpers!!.toDouble()
            val distanceInMiles_ = distance_in_miles.toDouble()
            val FareUpto: Double
            FareUpto =(vanHourlyRate+rateHelperHourly * helpers_count_)*hour+ratePerMileFare*distanceInMiles_

            String.format("%.2f", FareUpto)
        } catch (error: Exception) {
            "0"
        }
    }

    fun calculateFareUptoOld(
        hour: Double,
        vehicles: GetVehicles,
        distance_in_miles: String,
        context: Context
    ): String {
        return try {
            val rate_per_mile = vehicles.rate_per_mile_fare
            val van_hours_rate_per_hour = vehicles.hourly_rate
            val rate_per_helper_per_hour = vehicles.rate_helper_hourly
            val helpers = getPreferences(CONSTANTS.helpers, context)
            val ratePerMileFare = rate_per_mile.toDouble()
            var vanHourlyRate = van_hours_rate_per_hour.toDouble()
            var rateHelperHourly = rate_per_helper_per_hour.toDouble()

            val helpers_count_ = helpers!!.toDouble()
            val distanceInMiles_ = distance_in_miles.toDouble()

            val date_future_booking_str=getPreferences(CONSTANTS.date_future_booking_str, context!!)
            val time_slots_future_booking_str=getPreferences(CONSTANTS.time_slots_future_booking_str, context!!)
           // System.out.println("3 rate_helper_per_minute::::"+rate_per_helper_per_minute)
            System.out.println("3 hourly_rate::::"+van_hours_rate_per_hour)
            System.out.println("rate_per_mile::::"+rate_per_mile)
            System.out.println("duration hour::::"+hour)
            System.out.println("distanceInMiles_::::"+distanceInMiles_)
            val compare_format = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val input_time_format = SimpleDateFormat("HH:mm")
            var currentDateTime = compare_format.format(Date())
            var currentDD = SimpleDateFormat("yyyy-MM-dd").format(Date())
            var currentDate = SimpleDateFormat("yyyy-MM-dd").parse(currentDD)

            var bookingDate=SimpleDateFormat("yyyy-MM-dd").parse(date_future_booking_str)
            val bookingDDMMTT=time_slots_future_booking_str
            val compareTime6PM=input_time_format.parse("18:00")
            val TotalVanRate: Double
            val TotalHelperRate: Double
            val FareUpto: Double

            if (bookingDate.after(currentDate)){
                if (bookingDDMMTT.equals(Flexible)){
                    vanHourlyRate=vanHourlyRate;
                    rateHelperHourly=rateHelperHourly;
                }
                else
                {
                    val bookingDateTime=input_time_format.parse(bookingDDMMTT)
                    System.out.println("Request As Letter Booking::")
                    if (bookingDateTime.after(compareTime6PM)) {// After 6PM booking
                        System.out.println("compareTime6PM::"+compareTime6PM)
                        if (hour <= 1) {// upto 1 hours
                            vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                            rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;
                        }
                        else{
                            vanHourlyRate=vanHourlyRate+vanHourlyRate*0.4;
                            rateHelperHourly=rateHelperHourly+rateHelperHourly*0.4;
                        }
                    }
                    else
                    {
                        if (hour <= 1) {// upto 1 hours
                            vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                            rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;

                        }
                        else{
                            vanHourlyRate=vanHourlyRate;
                            rateHelperHourly=rateHelperHourly;
                        }
                    }
                }

                FareUpto =(vanHourlyRate+rateHelperHourly * helpers_count_)*hour+ratePerMileFare*distanceInMiles_




            }//Booking Type Request As Letter
            else{//Booking Type Request As SOON AS POSSIBLE
                val bookingDateTime=input_time_format.parse(bookingDDMMTT)
                System.out.println("Request As Soon As PossibleBooking::")
                if (bookingDateTime.after(compareTime6PM)) {// After 6PM booking
                    System.out.println("compareTime6PM::"+compareTime6PM)
                    if (hour <= 1) {// upto 1 hours
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;
                    }
                    else{
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.4;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.4;
                    }
                }
                else{
                    if (hour <= 1) {// upto 1 hours
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;

                    }
                    else{
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.25;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.25;

                    }
                }


                FareUpto =(vanHourlyRate+rateHelperHourly * helpers_count_)*hour+ratePerMileFare*distanceInMiles_

            }
            String.format("%.2f", FareUpto)
        } catch (error: Exception) {
            "0"
        }
    }

    fun increaseTheVehiclesPrice(
        hour: Double,
        vehicles: GetVehicles,
        context: Context
    ): GetVehicles {
        return try {
            val rate_per_mile = vehicles.rate_per_mile_fare
            val van_hours_rate_per_hour = vehicles.hourly_rate
            val rate_per_helper_per_hour = vehicles.rate_helper_hourly
            val helpers = getPreferences(CONSTANTS.helpers, context)
            val ratePerMileFare = rate_per_mile.toDouble()
            var vanHourlyRate = van_hours_rate_per_hour.toDouble()
            var rateHelperHourly = rate_per_helper_per_hour.toDouble()



            val date_future_booking_str=getPreferences(CONSTANTS.date_future_booking_str, context!!)
            val time_slots_future_booking_str=getPreferences(CONSTANTS.time_slots_future_booking_str, context!!)
            // System.out.println("3 rate_helper_per_minute::::"+rate_per_helper_per_minute)
            System.out.println("3 hourly_rate::::"+van_hours_rate_per_hour)
            System.out.println("rate_per_mile::::"+rate_per_mile)
            System.out.println("duration hour::::"+hour)

            val compare_format = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val input_time_format = SimpleDateFormat("HH:mm")
            var currentDateTime = compare_format.format(Date())
            var currentDD = SimpleDateFormat("yyyy-MM-dd").format(Date())
            var currentDate = SimpleDateFormat("yyyy-MM-dd").parse(currentDD)

            var bookingDate=SimpleDateFormat("yyyy-MM-dd").parse(date_future_booking_str)
            val bookingDDMMTT=time_slots_future_booking_str
            val compareTime6PM=input_time_format.parse("18:00")
            val TotalVanRate: Double
            val TotalHelperRate: Double
            val FareUpto: Double

            if (bookingDate.after(currentDate)){
                if (bookingDDMMTT.equals(Flexible)){
                    vanHourlyRate=vanHourlyRate;
                    rateHelperHourly=rateHelperHourly;

                }
                else
                {
                    val bookingDateTime=input_time_format.parse(bookingDDMMTT)
                    System.out.println("Request As Letter Booking::")
                    if (bookingDateTime.after(compareTime6PM)) {// After 6PM booking
                        System.out.println("compareTime6PM::"+compareTime6PM)
                        if (hour <= 1) {// upto 1 hours
                            vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                            rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;
                        }
                        else{
                            vanHourlyRate=vanHourlyRate+vanHourlyRate*0.4;
                            rateHelperHourly=rateHelperHourly+rateHelperHourly*0.4;
                        }
                    }
                    else
                    {
                        if (hour <= 1) {// upto 1 hours
                            vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                            rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;

                        }
                        else{
                            vanHourlyRate=vanHourlyRate;
                            rateHelperHourly=rateHelperHourly;
                        }
                    }
                }
                vehicles.hourly_rate=vanHourlyRate.toString()
                vehicles.rate_helper_hourly=rateHelperHourly.toString()
                vehicles.rate_helper_per_minute=(rateHelperHourly/60).toString()
                vehicles.rate_per_minute_fare=(vanHourlyRate/60).toString()
                return vehicles


            }//Booking Type Request As Letter
            else{//Booking Type Request As SOON AS POSSIBLE
                val bookingDateTime=input_time_format.parse(bookingDDMMTT)
                System.out.println("Request As Soon As PossibleBooking::")
                if (bookingDateTime.after(compareTime6PM)) {// After 6PM booking
                    System.out.println("compareTime6PM::"+compareTime6PM)
                    if (hour <= 1) {// upto 1 hours
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;
                    }
                    else{
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.4;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.4;
                    }
                }
                else{
                    if (hour <= 1) {// upto 1 hours
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.8;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.8;

                    }
                    else{
                        vanHourlyRate=vanHourlyRate+vanHourlyRate*0.25;
                        rateHelperHourly=rateHelperHourly+rateHelperHourly*0.25;

                    }
                }

                vehicles.hourly_rate=vanHourlyRate.toString()
                vehicles.rate_helper_hourly=rateHelperHourly.toString()
                vehicles.rate_helper_per_minute=(rateHelperHourly/60).toString()
                vehicles.rate_per_minute_fare=(vanHourlyRate/60).toString()
                return vehicles
            }

        } catch (error: Exception) {
           return vehicles
        }
    }


    fun getTimeSet(date: String,fromHours:Int,toHous:Int): ArrayList<TimeSlot>{
        val results = ArrayList<TimeSlot>()
        val sdf = SimpleDateFormat("HH:mm")
        for (i in fromHours..toHous) {
            val calendar: Calendar = GregorianCalendar()
            calendar.add(Calendar.HOUR_OF_DAY, i)
            calendar[Calendar.MINUTE] = 30
            val day1 = sdf.format(calendar.time)
           // calendar.add(Calendar.HOUR, 1)
           // val day2 = sdf.format(calendar.time)
            val day = "$day1"
            val timeSlots=TimeSlot(date,day,true)
            results.add(timeSlots)
        }
        return results
    }
    fun getTimeSlotsArray(date: String):ArrayList<TimeSlot>{
        var time_slots_list: ArrayList<TimeSlot>?
        time_slots_list= ArrayList()
        //time_slots_list!!.add(TimeSlot(date,"Flexible",true))
        time_slots_list!!.add(TimeSlot(date,"08:00",true))
        time_slots_list!!.add(TimeSlot(date,"08:30",true))
        time_slots_list!!.add(TimeSlot(date,"09:00",true))
        time_slots_list!!.add(TimeSlot(date,"09:30",true))
        time_slots_list!!.add(TimeSlot(date,"10:00",true))
        time_slots_list!!.add(TimeSlot(date,"10:30",true))
        time_slots_list!!.add(TimeSlot(date,"11:00",true))
        time_slots_list!!.add(TimeSlot(date,"11:30",true))
        time_slots_list!!.add(TimeSlot(date,"12:00",true))
        time_slots_list!!.add(TimeSlot(date,"12:30",true))
        time_slots_list!!.add(TimeSlot(date,"13:00",true))
        time_slots_list!!.add(TimeSlot(date,"13:30",true))
        time_slots_list!!.add(TimeSlot(date,"14:00",true))
        time_slots_list!!.add(TimeSlot(date,"14:30",true))
        time_slots_list!!.add(TimeSlot(date,"15:00",true))
        time_slots_list!!.add(TimeSlot(date,"15:30",true))
        time_slots_list!!.add(TimeSlot(date,"16:00",true))
        time_slots_list!!.add(TimeSlot(date,"16:30",true))
        time_slots_list!!.add(TimeSlot(date,"17:00",true))
        time_slots_list!!.add(TimeSlot(date,"17:30",true))
        time_slots_list!!.add(TimeSlot(date,"18:00",true))
        time_slots_list!!.add(TimeSlot(date,"18:30",true))
        time_slots_list!!.add(TimeSlot(date,"19:00",true))
        time_slots_list!!.add(TimeSlot(date,"19:30",true))
        time_slots_list!!.add(TimeSlot(date,"20:00",true))

        return time_slots_list!!
    }
    fun getTimeSlots(date: String,req_type:REQUEST_TYPE,time_slots_list:ArrayList<TimeSlot>):ArrayList<TimeSlot>{
            var fil_time_slots_list: ArrayList<TimeSlot>?
                fil_time_slots_list= ArrayList()
            val inputformat = SimpleDateFormat("HH:mm")
           // val mDate= inputformat.parse(listItem!!.time);
            val outputformat = SimpleDateFormat("hh:mm aa")
           // val strDate  = outputformat.format(mDate);

            when(req_type) {

                REQUEST_TYPE.REQUEST_ASAP ->
                {

                    val input_time_format = SimpleDateFormat("HH:mm")
                    var currentHours = SimpleDateFormat("HH:mm").format(Date())
                    val toCompareCurentHoursSession = input_time_format.parse(currentHours)

                    val toCompareTimeMorningSession = input_time_format.parse("10:00")
                    val toCompareTimeEveningSession = input_time_format.parse("16:00")
                    val toCompareTimeMorningSession10AM = input_time_format.parse("10:00")
                    val incrementedTime = getIncrementedTime(1, currentHours)
                    val toCompareIncrementTime = input_time_format.parse(incrementedTime)

                    if (toCompareCurentHoursSession.after(toCompareTimeEveningSession))
                    {

                    }
                    else if (toCompareCurentHoursSession.before(toCompareTimeMorningSession))
                    {
                        currentHours = "10:00"
                        val incrementedTime = currentHours
                        val  toCompareIncrementTime = input_time_format.parse(incrementedTime)
                        val  toShowIncrementTime = outputformat.format(toCompareIncrementTime)

                        val incremented3HoursDateTime = Utils.getIncrementedTime(3,incrementedTime)
                        val  toCompareIncrement3HoursDateTime = input_time_format.parse(incremented3HoursDateTime)
                        val  toShowIncrement3HoursDateTime = outputformat.format(toCompareIncrement3HoursDateTime)
                        val firstSlotsTime= "$toShowIncrementTime to $toShowIncrement3HoursDateTime"

                        fil_time_slots_list!!.add(TimeSlot(date, firstSlotsTime, true))

                        for (listItem in time_slots_list) {
                            val toCompareAdapterTimeSlots = input_time_format.parse(listItem!!.time)
                            if (toCompareAdapterTimeSlots.before(toCompareIncrement3HoursDateTime)) {
                            } else {
                                fil_time_slots_list.add(listItem)
                            }

                        }


                    }
                    else
                    { // booking for condition before 10:00AM
                        val incrementedTime = getIncrementedTime(1, currentHours)
                        val toCompareIncrementTime = input_time_format.parse(incrementedTime)
                        val incremented3HoursDateTime = getIncrementedTime(3, incrementedTime)
                        val toCompareIncrement3HoursDateTime =
                            input_time_format.parse(incremented3HoursDateTime)
                        var firstSlotsFrom = ""
                        val toCompareFromHours =
                            SimpleDateFormat("HH").format(toCompareIncrementTime)
                        val toCompareFromMinutes =
                            SimpleDateFormat("mm").format(toCompareIncrementTime)
                        firstSlotsFrom = if (toCompareFromMinutes.toInt() < 30) {
                            "$toCompareFromHours:30"
                        } else {
                            "$toCompareFromHours:00"
                        }
                        var firstSlotsTo = ""
                        val toCompareToHours =
                            SimpleDateFormat("HH").format(toCompareIncrement3HoursDateTime)
                        val toCompareToMinutes =
                            SimpleDateFormat("mm").format(toCompareIncrement3HoursDateTime)
                        firstSlotsTo = if (toCompareToMinutes.toInt() < 30) {
                            "$toCompareToHours:30"
                        } else {
                            "$toCompareToHours:00"
                        }

                        val firstSlotsTime =
                            "" + outputformat.format(input_time_format.parse(firstSlotsFrom)) + " to " + outputformat.format(
                                input_time_format.parse(firstSlotsTo))

                        fil_time_slots_list!!.add(TimeSlot(date, firstSlotsTime, true))

                        for (listItem in time_slots_list) {
                            val toCompareAdapterTimeSlots = input_time_format.parse(listItem!!.time)
                            if (toCompareAdapterTimeSlots.before(toCompareIncrement3HoursDateTime)) {
                            } else {
                                fil_time_slots_list.add(listItem)
                            }

                        }


                    }
                }
                REQUEST_TYPE.REQUEST_AS_LETTER ->
                    {
                        val input_time_format = SimpleDateFormat("HH:mm")
                        var currentHours = SimpleDateFormat("HH:mm").format(Date())
                        val toCompareCurentHoursSession = input_time_format.parse(currentHours)


                        val toCompareTimeEveningSession = input_time_format.parse("20:00")
                        val toCompareTimeMidNightSession = input_time_format.parse("24:00")
                        val incrementedTime = getIncrementedTime(1, currentHours)

                        val toCompareIncrementTime = input_time_format.parse(incrementedTime)


                        fil_time_slots_list!!.add(TimeSlot(date, Flexible, true))

                        for (listItem in time_slots_list){
                            val toCompareAdapterTimeSlots = input_time_format.parse(listItem!!.time)
                            val toCompareTimeMorningSession10AM = input_time_format.parse("10:00")
                            if (toCompareCurentHoursSession.after(toCompareTimeEveningSession)&&toCompareCurentHoursSession.before(toCompareTimeMidNightSession))
                            {
                                val calendar = Calendar.getInstance()
                                calendar.add(Calendar.DAY_OF_YEAR, 1)
                                val tomorrow = calendar.time

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")

                                val selectedDate=dateFormat.parse(date)
                                val tomorrowAsString: String = dateFormat.format(tomorrow)

                                val tomorrowAsDateObj = dateFormat.parse(tomorrowAsString)
                                if (selectedDate.after(tomorrowAsDateObj)){
                                    fil_time_slots_list.add(listItem)
                                }
                                else{
                                    if (toCompareAdapterTimeSlots.before(toCompareTimeMorningSession10AM)) {// matching condition adapter time slots with 10:00AM
                                    }
                                    else {
                                        fil_time_slots_list.add(listItem)
                                    }
                                }
                            }
                            else{
                                fil_time_slots_list.add(listItem)
                            }
                        }
                    }

            }

        return fil_time_slots_list
    }

    fun getEstimationDurationVehicles(
        time: String?,
        vehicles: GetVehicles,
        context: Context
    ): GetVehicles {
        if (time == CONSTANTS.estimated_duration_list[1]) {
            return increaseTheVehiclesPrice(
                1.0,
                 vehicles,
                 context
            )


        }
        else if (time == CONSTANTS.estimated_duration_list[2]) {

            return increaseTheVehiclesPrice(
                2.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[3]) {

            return increaseTheVehiclesPrice(
                2.50,
                vehicles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[4]) {

            return increaseTheVehiclesPrice(
                3.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[5]) {

            return increaseTheVehiclesPrice(
                3.50,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[6]) {

            return increaseTheVehiclesPrice(
                4.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[7]) {

            return increaseTheVehiclesPrice(
                4.50,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[8]) {

            return increaseTheVehiclesPrice(
                5.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[9]) {

            return increaseTheVehiclesPrice(
                5.50,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[10]) {

            return increaseTheVehiclesPrice(
                6.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[11]) {

            return increaseTheVehiclesPrice(
                6.50,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[12]) {

            return increaseTheVehiclesPrice(
                7.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[13]) {

            return increaseTheVehiclesPrice(
                7.50,
                vehicles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[14]) {

            return increaseTheVehiclesPrice(
                8.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[15]) {

            return increaseTheVehiclesPrice(
                8.50,
                vehicles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[16]) {

            return increaseTheVehiclesPrice(
                9.0,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[17]) {

            return increaseTheVehiclesPrice(
                9.50,
                vehicles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[18]) {

            return increaseTheVehiclesPrice(
                10.0,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[19]) {

            return increaseTheVehiclesPrice(
                10.50,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[20]) {

            return increaseTheVehiclesPrice(
                11.0,
                vehicles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[21]) {

            return increaseTheVehiclesPrice(
                11.50,
                vehicles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[22]) {

            return increaseTheVehiclesPrice(
                12.0,
                vehicles,
                context
            )

        }
        else {
            return vehicles

        }
    }
    fun getMinMax(time: String): DoubleArray {
        val intArray = DoubleArray(2)
        if (time == CONSTANTS.estimated_duration_list[1]) {
            intArray[0] = 1.00
            intArray[1] = 1.00
        } else if (time == CONSTANTS.estimated_duration_list[2]) {
            intArray[0] = 1.00
            intArray[1] = 2.00
        }
        else if (time == CONSTANTS.estimated_duration_list[3]) {
            intArray[0] = 2.50
            intArray[1] = 2.50
        }
        else if (time == CONSTANTS.estimated_duration_list[4]) {
            intArray[0] = 3.00
            intArray[1] = 3.00
        }
        else if (time == CONSTANTS.estimated_duration_list[5]) {
            intArray[0] = 3.50
            intArray[1] = 3.50
        }

        else if (time == CONSTANTS.estimated_duration_list[6]) {
            intArray[0] = 4.00
            intArray[1] = 4.00
        }
        else if (time == CONSTANTS.estimated_duration_list[7]) {
            intArray[0] = 4.50
            intArray[1] = 4.50
        }
        else if (time == CONSTANTS.estimated_duration_list[8]) {
            intArray[0] = 5.00
            intArray[1] = 5.00
        }
        else if (time == CONSTANTS.estimated_duration_list[9]) {
            intArray[0] = 5.50
            intArray[1] = 5.50
        }
        else if (time == CONSTANTS.estimated_duration_list[10]) {
            intArray[0] = 6.00
            intArray[1] = 6.00
        }
        else if (time == CONSTANTS.estimated_duration_list[11]) {
            intArray[0] = 6.50
            intArray[1] = 6.50
        }
        else if (time == CONSTANTS.estimated_duration_list[12]) {
            intArray[0] = 7.00
            intArray[1] = 7.00
        }
        else if (time == CONSTANTS.estimated_duration_list[13]) {
            intArray[0] = 7.50
            intArray[1] = 7.30
        }
        else if (time == CONSTANTS.estimated_duration_list[14]) {
            intArray[0] = 8.00
            intArray[1] = 8.00
        }
        else if (time == CONSTANTS.estimated_duration_list[15]) {
            intArray[0] = 8.50
            intArray[1] = 8.50
        }
        else if (time == CONSTANTS.estimated_duration_list[16]) {
            intArray[0] = 9.00
            intArray[1] = 9.00
        }
        else if (time == CONSTANTS.estimated_duration_list[17]) {
            intArray[0] = 9.50
            intArray[1] = 9.50
        }
        else if (time == CONSTANTS.estimated_duration_list[18]) {
            intArray[0] = 10.00
            intArray[1] = 10.00
        }
        else if (time == CONSTANTS.estimated_duration_list[19]) {
            intArray[0] = 10.50
            intArray[1] = 10.50
        }
        else if (time == CONSTANTS.estimated_duration_list[20]) {
            intArray[0] = 11.00
            intArray[1] = 11.00
        }
        else if (time == CONSTANTS.estimated_duration_list[21]) {
            intArray[0] = 11.50
            intArray[1] = 11.50
        }
        else if (time == CONSTANTS.estimated_duration_list[22]) {
            intArray[0] = 12.00
            intArray[1] = 12.00
        }

        return intArray
    }
    fun dialog(context: Context,layout: Int, isHide: Boolean): Dialog {
        var commonDialog = Dialog(context);
        commonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        commonDialog = Dialog(context)
        commonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        commonDialog.setContentView(layout)
        val lp = WindowManager.LayoutParams()
        val window = commonDialog.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window!!.attributes = lp
        commonDialog.setCancelable(isHide)
        commonDialog.window!!.setBackgroundDrawableResource(R.color.transuleant_black)
        commonDialog.window!!.setDimAmount(0f)
        commonDialog.show()

        return commonDialog;

    }

    fun getIncrementedTime(value:Int,time: String) : String{
        val sdf = SimpleDateFormat("HH:mm")
        val  currentDate = sdf.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time=currentDate
        currentDate.time = calendar.timeInMillis + value*60 * 60 * 1000
        val currentDateTime = sdf.format(currentDate)
        System.out.println("Time here "+currentDateTime);
        return currentDateTime
    }

    lateinit var animationForViews: AnimationForViews;
    var screenWidth: Int = 0
    var screenHeight: Int = 0
    fun downSourceDestinationView(context: AppCompatActivity,sourcedestinationcontainer: View?, dialog: Dialog?) {
        animationForViews = AnimationForViews()
        animationForViews.handleAnimation(context,
            sourcedestinationcontainer,
            300,
            0,
            screenHeight,
            IsAnimationEndedCallback.translationY,
            IsAnimationEndedCallback { status ->
                when (status) {
                    IsAnimationEndedCallback.animationCancel -> {
                    }
                    IsAnimationEndedCallback.animationEnd -> {
                        dialog?.dismiss()
                    }
                    IsAnimationEndedCallback.animationRepeat -> {
                    }
                    IsAnimationEndedCallback.animationStart -> {
                    }
                }
            })
    }
    fun getScreenHeight(context: AppCompatActivity) {
        val displayMetrics = DisplayMetrics()
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        screenHeight = displayMetrics.heightPixels
        screenWidth = displayMetrics.widthPixels
    }
    fun animateUp(context: AppCompatActivity,sourcedestinationcontainer: View?) {
        getScreenHeight(context)
        animationForViews = AnimationForViews()
        animationForViews.handleAnimation(context,
            sourcedestinationcontainer,
            300,
            screenHeight,
            0,
            IsAnimationEndedCallback.translationY,
            IsAnimationEndedCallback { status ->
                when (status) {
                    IsAnimationEndedCallback.animationCancel -> {
                    }
                    IsAnimationEndedCallback.animationEnd -> {
                    }
                    IsAnimationEndedCallback.animationRepeat -> {
                    }
                    IsAnimationEndedCallback.animationStart -> {
                    }
                }
            })
    }

}