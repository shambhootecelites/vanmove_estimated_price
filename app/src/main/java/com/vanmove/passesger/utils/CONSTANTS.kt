package com.vanmove.passesger.utils

import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.model.PromoCode
import com.vanmove.passesger.utils.Utils.aPIService
import java.text.DecimalFormat
import java.util.*

object CONSTANTS {

    var current_ride_data: MutableMap<String?, Any?> = HashMap()


    private const val PACKAGE_NAME = "com.vanmove.passesger"


    fun decimalFormat(value:Double): String=String.format("%.2f", value)


    val mApiService = aPIService

    val headers = HashMap<String, String>()

    const val user_type = "passenger"
    const val Support_Number = "0203 168 1001"
    const val Booking_Team = "0203 168 1001"
    const val Support_Email = "support@vanmove.com"
    const val Help_Link = "https://vanmove.com/help.php"
    const val FBLink = "https://www.facebook.com/move.van.94"
    const val privacy = "https://vanmove.com/terms-and-conditions.php?page=2"
    const val term_condition = "https://vanmove.com/terms-and-conditions.php"
    const val twitterlink = "https://twitter.com/move_van"
    const val insurance_link = "https://vanmove.com/insurance.html"
    var dismantlingRate = 15.00
    const val dismantlingRate2 = 15.00

    var promoCode: PromoCode? = null
    var isPromoCodeApply = false

    const val SERVICE_ID = "service_id"
    const val REQUEST_ID = "request_id"
    const val CURRENCY = "£"
    const val Meter_Miles = 0.00062137
    const val KEY_LICENCE_NUMBER = "11953647"
    const val Ground = "Ground Floor"
    const val First_Floor_ = "1st Floor"
    const val Second_Floor_ = "2nd Floor"
    const val Third_Floor_ = "3rd Floor"
    const val Fourth_Floor_ = "4th & Above"


    val helper_list = arrayOf(
        "Need extra help with loading ?",
        "No I will self load",
        "I need just the driver",
        "I need 2 people to help me",
        "I need 3 people to help me"
    )

   val estimated_duration_list = arrayOf(
        "Select estimated duration",
        "Less Than 1 hour",
        "1 to 2 Hours",
        "02 Hours 30 Minutes",
        "03 Hours 00 Minutes",

        "03 Hours 30 Minutes",
        "04 Hours 00 Minutes",

        "04 Hours 30 Minutes",
        "05 Hours 00 Minutes",

        "05 Hours 30 Minutes",
        "06 Hours 00 Minutes",

        "06 Hours 30 Minutes",
        "07 Hours 00 Minutes",

        "07 Hours 30 Minutes",
        "08 Hours 00 Minutes",

        "08 Hours 30 Minutes",
        "09 Hours 00 Minutes",

        "09 Hours 30 Minutes",
        "10 Hours 00 Minutes",

        "10 Hours 30 Minutes",
        "11 Hours 00 Minutes",

        "11 Hours 30 Minutes",
        "12 Hours 00 Minutes"
    )

 /* val estimated_duration_list = arrayOf(
      "Select estimated duration",
      "Upto 1 hour",
      "1-2 hours",
      "2-3 hours",
      "3-4 hours",
      "4-5 hours",
      "5-6 hours",
      "6-7 hours",
      "7-8 hours",
      "8-9 hours",
      "9-10 hours"
  )*/
    val estimated_duration_list2 = arrayOf(
        "1 hour",
        "2 hours",
        "3 hours",
        "4 hours",
        "5 hours",
        "6 hours",
        "7 hours",
        "8 hours",
        "9 hours",
        "10 hours"
    )
    val Need_dismantling_List = arrayOf(
        "Need dismantling or assembling?",
        "Yes",
        "No"
    )

    val category = arrayOf(
        Ground,
        First_Floor_, Second_Floor_,
        Third_Floor_,
        Fourth_Floor_
    )

    private const val Yes = "Yes"
    const val No = "No"

    val categorylift = arrayOf(
        Yes,
        No
    )

    var vehicles: GetVehicles? = null
    const val login = "login"
    const val Flexible = "Flexible (10:00 am - 08:00 pm)"
    const val callfrom = "callfrom"
    const val where_to_loc = "where_to_loc"
    const val pickup_from_loc = "pickup_from_loc"
    const val Standard_insurance_ = "Standard £500*"
    const val Costs_insurance = "Costs £20.00"
    const val costs_insurance_extra = "Yes coverage upto £10,000 costs £20.00"
    const val Standard_insurance_amount = 0.0
    const val Costs_insurance_amount = 20.00
    val precision = DecimalFormat("##,##,###0.00")
    const val PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA = "pick_up_location_name_extra"
    const val PREFERENCE_PICK_UP_LATITUDE_EXTRA = "pick_up_latitude"
    const val PREFERENCE_PICK_UP_LONGITUDE_EXTRA = "pick_up_longitude"
    const val PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA = "destination_location_name_extra"
    const val PREFERENCE_DESTINATION_LATITUDE_EXTRA = "destination_latitude"
    const val PREFERENCE_DESTINATION_LONGITUDE_EXTRA = "destination_longitude"
    const val PREFERENCE_ADD_HOME_LOCATION_NAME = "home_location_name"
    const val PREFERENCE_ADD_HOME_LATITUDE_EXTRA = "home_latitude_extra"
    const val PREFERENCE_ADD_HOME_LONGITUDE_EXTRA = "home_longitude_extra"
    const val PREFERENCE_ADD_WORK_LOCATION_NAME = "work_location_name"
    const val PREFERENCE_ADD_WORK_LATITUDE_EXTRA = "work_latitude_extra"
    const val PREFERENCE_ADD_WORK_LONGITUDE_EXTRA = "work_longitude_extra"

    const val PREFERENCE_LAST_RIDE_ADDRESS_EXTRA = "PREFERENCE_LAST_RIDE_ADDRESS_EXTRA"
    const val PREFERENCE_LAST_RIDE_LATITUDE_EXTRA = "PREFERENCE_LAST_RIDE_LATITUDE_EXTRA"
    const val PREFERENCE_LAST_RIDE_LONGITUDE_EXTRA = "PREFERENCE_LAST_RIDE_LONGITUDE_EXTRA"


    const val distance_in_miles = "distance_in_miles"
    const val estimated_duartion = "estimated_duartion"
    const val estimated_payment = "estimated_payment"
    const val is_flexible = "is_flexible"
    const val date_booking = "date_booking"
    const val str_inventry = "str_inventry"
    const val str_inventry_detail = "str_inventry_detail"
    const val str_aditional_info = "str_aditional_info"
    const val offered_amount = "offered_amount"
    const val second_date = "second_date"
    const val mobile_passenger = "mobile_passenger"
    const val RegistrationID = "RegistrationID"
    const val passenger_id = "passenger_id"
    const val first_name = "first_name"
    const val last_name = "last_name"
    const val country_code = "country_code"
    const val vehicle_class_id = "vehicle_class_id"
    const val insurance = "insurance"
    const val insurance_fare = "insurance_fare"
    const val insurance_plan = "insurance_plan"
    const val helpers = "helpers"
    const val vehicle_name = "vehicle_name"
    const val rate_per_mile = "rate_per_mile"
    const val rate_per_minute = "rate_per_minute"
    const val rate_per_hour = "rate_per_hour"
    const val rate_per_helper_hour = "rate_per_helper_hour"
    const val rate_per_helper_per_minute = "rate_per_helper_per_minute"
    const val card_number = "card_number"
    const val BOOKING_PORTER = "booking_porter"
    const val date_future_booking_str = "date_future_booking_str"
    const val time_slots_future_booking_str = "time_slots_future_booking_str"

    const val first_floor_charge = "first_floor_charge"
    const val second_floor_charge = "second_floor_charge"
    const val third_floor_charge = "third_floor_charge"
    const val fourth_above_charge = "fourth_above_charge"

    const val first_floor_price = "1st Floor"
    const val second_floor_price = "second_floor_charge"
    const val third_floor_price = "third_floor_charge"
    const val fourth_above_price = "fourth_above_charge"



    const val dismantling = "dismantling"
    const val Loading = "Loading..."
    const val from_circulation_screen = "from_circulation_screen"
    const val INTENT_EXTRA_NOTIFICATION_NAME = "notification_name"
    const val INTENT_EXTRA_IS_PUSH_NOTIFICATION_AVAILABLE = "INTENT_EXTRA_IS_PUSH_NOTIFICATION_AVAILABLE"
    const val from_home = "from_home"
    const val stripe_customer_id = "stripe_customer_id"
    const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
    const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"
    const val SUCCESS_RESULT = 0
    const val FAILURE_RESULT = 1
    const val DISTANCE_TO_ROTATE = 10
    var move_duration = "0 min"
    const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"

    // Borad Cast Receiever Name
    const val unique_name_accepted = "unique_name_accepted"
    const val unique_name_accepted_fixed = "unique_name_accepted_fixed"

    // EXTRA
    const val PAY_BY = "pay_by"
    const val CASH = "CASH"
    const val CARD = "Stripe"
    const val image_link = "image_link"
    const val broadcat_name = "muhammad_umer_sheraz"
    const val unique_name_completed = "unique_name_completed"
    const val move_to_connecting_screen = "move_to_connecting_screen"
    const val move_to_offer_upcoming = "move_to_offer_upcoming"
    const val move_to_move_upcoming = "move_to_move_upcoming"
    const val unique_name_accepted_porter = "unique_name_accepted_porter"
    const val PLAY_SERVICES_RESOLUTION_REQUEST = 7172
    const val UPDATE_INTERVAL = 10000 // SEC
    const val FATEST_INTERVAL = 5000 // SEC
    const val DISPLACEMENT = 10 // METERS
    const val REQUEST_GPS_SETTINGS = 107
    const val SHARED_PREFERENCE_APP = "app_shared_preference"
    const val PREFERENCE_EXTRA_REGISTRATION_ID = "RegistrationID"
    const val Work_Destination = "Work_Destination"
    const val Home_Destination = "Home_Destination"
    const val Last_Ride = "Last_Ride"
    const val MOBILE_CODE_SELECTION = "mobile_code"
    const val LANGUAGE_SELECTION = "language_selection"
    const val COUNTRY_NAME_SELECTION = "country_name"
    const val PORTER_ID = "porter_id"
    const val PORTER_LAST_NAME = "porter_last_name"
    // Events Bus Tag
    const val edit_move = "edit_move"
    const val edit_offer = "edit_offer"

    // FCM Name
    const val Otp = "Otp"
    const val CashPaid = "Cash Paid"
    const val AmountDue = "Amount Due"


    const val Offer_accepted = "Offer accepted"
    const val offer_request_id = "offer_request_id"

    var current_country = "UK"
    const val pickup_latitude = "pickup_latitude"
    const val pickup_longitude = "pickup_longitude"
    const val destination_latitude = "destination_latitude"
    const val destination_longitude = "destination_longitude"
    const val vehicle_class_name = "vehicle_class_name"
    const val driver_id = "driver_id"
    const val driver_first_name = "driver_first_name"
    const val driver_latitude = "driver_latitude"
    const val driver_longitude = "driver_longitude"
    const val email = "email"
    const val mobile = "mobile"
    const val picture = "picture"
    const val polyline = "polyline"
    const val onResume = "onResume"
    const val username = "username"
    const val login_email = "login_email"
    const val login_pwd = "login_pwd"
    const val picture_user = "picture_user"
    const val mobile2 = "mobile2"
    const val reqID = "reqID"
    const val Regular = "Regular"
    const val Fixed = "Fixed"
    const val str_phone = "str_phone"
    const val str_password = "str_password"
    const val is_type = "is_type"
    const val sub_total = "sub_total"
    const val porter_id = "porter_id"
    const val PORTER_REQUEST_ID = "porter_request_id"
    const val sendNotificationForFixedBooking = "sendNotificationForFixedBooking"
    const val Offer_AcceptedFragment_On_Resumed_State =
        "Offer_AcceptedFragment_On_Resumed_State"
    const val Porter_Booking_COMPLETED = "Porter_Booking_COMPLETED"
}