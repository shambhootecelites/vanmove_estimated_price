package com.vanmove.passesger.services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vanmove.passesger.R
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.NotificationUtils
import com.vanmove.passesger.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var title: String? = ""
    override fun onNewToken(refreshedToken: String) {
        super.onNewToken(refreshedToken)
        Utils.savePreferences(
            CONSTANTS.RegistrationID,
            refreshedToken,
            applicationContext
        )
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        try {
            val text = data["text"]
            val user_id2 = data["user_id"]
            title = data["title"]
            when (title) {
                CONSTANTS.Otp -> {
                    val intent = Intent("unique_name")
                    intent.putExtra("otp_code", text)
                    applicationContext.sendBroadcast(intent)


                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        "Please send otp $text"
                    )
                }
                CONSTANTS.CashPaid -> {
                    NotificationUtils().showNotification(
                        title!!,
                        text!!
                    )
                }
                CONSTANTS.AmountDue -> {
                    NotificationUtils().showNotification(
                        title!!,
                        text!!
                    )
                }
                "Porter Job Accepted" -> {
                    val fcmStringAray = getConvertedNotificationData(text)
                    val porter_request = fcmStringAray[0]
                    val first_name = fcmStringAray[1]
                    val last_name = fcmStringAray[2]
                    val porterId =fcmStringAray[3]
                    Utils.savePreferences(
                        CONSTANTS.REQUEST_ID,
                        porter_request,
                        applicationContext
                    )

                    Utils.savePreferences(CONSTANTS.PORTER_ID,
                    porterId,
                    applicationContext)

                    Utils.savePreferences(CONSTANTS.PORTER_LAST_NAME, last_name,
                    applicationContext)
Log.d("IDs:fcmRequest", "request Id: $porter_request, porterId: $porterId")


                    val intent = Intent(CONSTANTS.unique_name_accepted_porter)
                    intent.putExtra("porter_not_found", "false")
                    intent.putExtra(CONSTANTS.porter_id, porterId)
                    applicationContext.sendBroadcast(intent)




                    NotificationUtils().showNotification(
                        "Job Accepted",
                        "Porter Name : " +
                                first_name + " " + last_name
                    )
                }
                "Porter Job Arrived" -> {
                    val fcmStringAray = getConvertedNotificationData(text)
                    val porter_request = fcmStringAray[0]
                    val first_name = fcmStringAray[2]
                    val last_name = fcmStringAray[3]
                    Utils.savePreferences(
                        CONSTANTS.REQUEST_ID,
                        porter_request,
                        applicationContext
                    )


                    NotificationUtils().showNotification(
                        "Job Arrived",
                        "Porter Name : " +
                                first_name + " " + last_name
                    )

                }
                "Offer accepted - Pay deposit to confirm booking." -> {
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put(CONSTANTS.offer_request_id, text!!)
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!,
                        CONSTANTS.Offer_accepted,
                        hashMap
                    )
                }
                "Porter Job Started" -> {

                    val fcmStringAray = getConvertedNotificationData(text)
                    val porter_request = fcmStringAray[0]
                    val first_name = fcmStringAray[2]
                    val last_name = fcmStringAray[3]
                    Utils.savePreferences(
                        CONSTANTS.REQUEST_ID,
                        porter_request,
                        applicationContext
                    )


                    NotificationUtils().showNotification(
                        "Porter Booking Started",
                        "Porter Name : $first_name $last_name"
                    )
                }
                "Porter Job Completed" -> {

                    val fcmStringAray = getConvertedNotificationData(text)
                    val porter_request = fcmStringAray[0]
                    var porter_fare = fcmStringAray[1]
                    Utils.savePreferences(
                        CONSTANTS.REQUEST_ID,
                        porter_request,
                        applicationContext
                    )
                    val intent = Intent(CONSTANTS.Porter_Booking_COMPLETED)
                    porter_fare = String.format("%.2f", porter_fare!!.toDouble())
                    intent.putExtra("porter_fare", porter_fare)
                    sendBroadcast(intent)


                    NotificationUtils().showNotification(
                        "Porter Job Completed",
                        "Amount : " + CONSTANTS.CURRENCY + porter_fare
                    )
                }
                "Porter not Found" -> {
                    val intent = Intent(CONSTANTS.unique_name_accepted_porter)
                    intent.putExtra("porter_not_found", "true")
                    applicationContext.sendBroadcast(intent)
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Future Job Accepted" -> {
                    NotificationUtils().showNotification(
                        title!!,
                        text!!
                    )
                }
                "Request Accepted" -> {
                    Utils.savePreferences(
                        "driver_not_found",
                        "false",
                        applicationContext
                    )

                    requestAccepted_Accepted(applicationContext, text)


                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Fixed Request Accepted" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )


                }

                "Porter Job Amount Paid" ->{
                    NotificationUtils().showNotification(
                        title!!,
                        text!!
                    )
                }

                "Fixed Job started" -> {
                    requestAccepted_Arrived(applicationContext, text, "Job started")


                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Job Driver going to pick up" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Driver going to drop off" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )

                }
                "Driver going to pick up" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )

                }
                "Driver arrived at drop off" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )

                }
                "Driver has arrived" -> {
                    requestAccepted_Arrived(applicationContext, text, title)
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )

                }
                "Job started" -> {
                    requestAccepted_Arrived(applicationContext, text, title)
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }

                "Fixed Driver has arrived" -> {
                    requestAccepted_Arrived(applicationContext, text, "Driver has arrived")
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Job completed" -> {
                    requestAccepted_Completed(applicationContext, text)
                    NotificationUtils().showNotification(
                        title!!,
                        text!!
                    )
                }
                "Fixed Job completed" -> {
                    requestAccepted_Completed(applicationContext, text)
                    NotificationUtils().showNotification(
                        title!!,
                        text!!
                    )
                }
                "Fixed Booking GO TO PICK-UP" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Fixed Driver going to drop off" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Fixed Driver arrived at drop off" -> {
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                }
                "Driver not Found" -> {
                    Utils.savePreferences(
                        CONSTANTS.REQUEST_ID,
                        text,
                        applicationContext
                    )
                    val intent = Intent(CONSTANTS.unique_name_accepted)
                    intent.putExtra("reqID", text)
                    intent.putExtra("driver_not_found", "true")
                    applicationContext.sendBroadcast(intent)
                    NotificationUtils().showNotification(
                        getString(R.string.app_name),
                        title!!
                    )
                    val intent1 = Intent(CONSTANTS.unique_name_accepted_fixed)
                    intent1.putExtra("reqID", text)
                    intent1.putExtra("driver_not_found", "true")
                    applicationContext.sendBroadcast(intent1)
                }
                "New Login Detected" -> {
//                    val intent = Intent(applicationContext, Ask::class.java)
                    Utils.savePreferences(
                        "LOGGED",
                        null,
                        applicationContext
                    )
                    Utils.savePreferences(
                        CONSTANTS.login,
                        "false",
                        applicationContext
                    )
//                    intent.action = Intent.ACTION_MAIN
//                    intent.addCategory(Intent.CATEGORY_HOME)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun requestAccepted_Accepted(
        context: Context,
        reqID: String?
    ) {
        Utils.savePreferences(
            CONSTANTS.REQUEST_ID,
            reqID,
            applicationContext
        )
        val intent = Intent(CONSTANTS.unique_name_accepted)
        intent.putExtra("reqID", reqID)
        intent.putExtra("driver_not_found", "false")
        context.sendBroadcast(intent)
    }

    fun requestAccepted_Arrived(
        context: Context,
        reqID: String?,
        title: String?
    ) {
        val intent = Intent(CONSTANTS.broadcat_name)
        intent.putExtra("reqID", reqID)
        intent.putExtra("status", title)
        context.sendBroadcast(intent)
    }

    fun requestAccepted_Completed(
        context: Context,
        reqID: String?
    ) {
        val intent = Intent(CONSTANTS.unique_name_completed)
        intent.putExtra("reqID", reqID)
        context.sendBroadcast(intent)
    }


    private fun getConvertedNotificationData(data: String?): Array<String?> {
        var jsonArray: JSONArray? = null
        try {
            jsonArray = JSONArray(data)
            Log.d("hantash_fcm_message", "Size: " + jsonArray.length())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val converted_data =
            arrayOfNulls<String>(jsonArray!!.length())
        for (i in 0 until jsonArray.length()) {
            try {
                converted_data[i] = jsonArray.getString(i)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return converted_data
    }
}