package com.vanmove.passesger.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.vanmove.passesger.R
import com.vanmove.passesger.model.APIModel.BookingDetailRespone
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.CONSTANTS.headers
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.changeDateFormat
import github.nisrulz.screenshott.ScreenShott
import kotlinx.android.synthetic.main.activity_trip_detail.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MoveInvoice : AppCompatActivity(), View.OnClickListener {


    var precision = DecimalFormat("0.00")
    var totalFareDuration = 0.0
    var totalHelperFare = 0.0
    var insurance = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_detail)

        iv_back.setOnClickListener(this)
        Support.setOnClickListener(this)
        share_invoice.setOnClickListener(this)
        val reqID = intent.getStringExtra("reqID")
        getbookingdetail(reqID!!)
    }

    private fun getbookingdetail(reqID: String) {
        progress_bar!!.visibility = View.VISIBLE
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = reqID
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam as Map<*, *>).toString()
        )
        CONSTANTS.mApiService.get_booking_detail(body, headers)!!
            .enqueue(object : Callback<BookingDetailRespone?> {
                override fun onResponse(
                    call: Call<BookingDetailRespone?>,
                    response: Response<BookingDetailRespone?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.status!!.code == "1000") {
                            val bookingDetailRespone = response.body()
                            val request =
                                bookingDetailRespone!!.request

                            request!!.  run {
                                if (isInsurance != 0){
                                    insurance = 20.0
                                }
                                reference!!.text =requestId
                                Pickup!!.text = pickup
                                Dropoff!!.text = destination
                                val completd_time = requestTripStartTime
                                val time =
                                    changeDateFormat(
                                        completd_time, "yyyy-MM-dd HH:mm:ss",
                                        "dd-MMM-yyyy h:mm a"
                                    )
                                Status!!.text = "Move completed $time"
                                var servies_ = vehicleClassName
                                servies_ = if (helpersCount!! < 2) {
                                    servies_ + " - " + helpersCount + " helper"
                                } else {
                                    servies_ + " - " + helpersCount + " helpers"
                                }
                                Service!!.text = servies_
                                val simpleDateFormat =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val requestDriveStartTime =
                                    requestDriveStartTime
                                val requestTripEndTime = requestTripEndTime
                                try {
                                    val date1 =
                                        simpleDateFormat.parse(requestDriveStartTime)
                                    val date2 =
                                        simpleDateFormat.parse(requestTripEndTime)
                                    val difference = date2.time - date1.time
                                    val days = (difference / (1000 * 60 * 60 * 24)).toInt()
                                    var hours =
                                        ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                                    val min =
                                        (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
                                    hours = if (hours < 0) -hours else hours
                                    print(" UMER SHERAZ: min $min")
                                    print(" UMER SHERAZ: hours $hours")
                                    var duration_: String? = ""
                                    var helper_ = ""
                                    if (hours == 0) {
                                        totalFareDuration = (ratePerMinuteFare!!.toDouble() * distanceTimeTotal!!.toDouble())
                                        totalHelperFare = (rateHelperPerMinute!!.toDouble() * distanceTimeTotal!!.toDouble() * helpersCount!!)
                                       // var totalFare =  distanceTimeTotal + totalHelperFare
                                        duration_ = hourlyRate
                                        helper_ = "" + helpersCount!! *
                                                rateHelperHourlyTotal!!.toDouble()
                                    } else {

                                        totalFareDuration = (ratePerMinuteFare!!.toDouble() * distanceTimeTotal!!.toDouble())
                                        totalHelperFare = (rateHelperPerMinute!!.toDouble() * distanceTimeTotal!!.toDouble() * helpersCount!!)

                                        // 1 hour 3 minute
                                        val total_hour_min = hours * 60
                                        var total_min = total_hour_min + min
                                        total_min = total_min - 60 // minius first hour minute

                                        // GT DUARTION
                                        val first_hour_price =
                                            hourlyRate!!.toDouble()
                                        val remaining_min_price =
                                            total_min * ratePerMileFare!!.toDouble()
                                        val total =
                                            first_hour_price + remaining_min_price
                                        duration_ = "" + total

                                        // GET HELPER PRICE
                                        val first_hour_price_helper =
                                            rateHelperHourlyTotal!!.toDouble()
                                        val remaining_min_price_helper =
                                            total_min * rateHelperPerMinute!!.toDouble()
                                        val total_helper =
                                            first_hour_price_helper + remaining_min_price_helper
                                        helper_ = "" + helpersCount!! *
                                                total_helper
                                    }
                                    if (distanceTimeTotal!!.toInt() < 60) {
                                        totalFareDuration = (hourlyRate!!.toDouble())
                                        totalHelperFare =  (rateHelperHourly!!.toDouble() * helpersCount!!)
                                    }else{
                                        totalFareDuration =  (hourlyRate!!.toDouble())
                                        totalFareDuration += (ratePerMinuteFare!!.toDouble() * (distanceTimeTotal!!.toDouble()-60))
                                        totalHelperFare =  (rateHelperHourly!!.toDouble() * helpersCount!!)
                                        totalHelperFare += (rateHelperPerMinute!!.toDouble() * (distanceTimeTotal!!.toDouble()-60) * helpersCount!!)


                                    }
                                  //  totalFareDuration = (ratePerMinuteFare!!.toDouble() * distanceTimeTotal!!.toDouble())
                                  //  totalHelperFare = (rateHelperPerMinute!!.toDouble() * distanceTimeTotal!!.toDouble() * helpersCount!!)

                                    Duration!!.text =
                                        CONSTANTS.CURRENCY + precision.format(totalFareDuration!!.toDouble())
                                    Helpers!!.text =
                                        CONSTANTS.CURRENCY + precision.format(totalHelperFare)
                                    Mileage!!.text =
                                        CONSTANTS.CURRENCY + precision.format(rateDistanceTotal!!.toDouble())
                                    val sub_total = totalHelperFare!!.toDouble() + totalFareDuration!!.toDouble() + rateDistanceTotal!!.toDouble()
                                    Subtotal!!.text = CONSTANTS.CURRENCY + precision.format(sub_total)
                                    Insurance!!.text =
                                        CONSTANTS.CURRENCY + precision.format(insurance)
                                    val grand_total = sub_total + insurance!!
                                    if (isType == "Regular") {
                                        if (is_future == 1) {
                                            estimated_price!!.visibility = View.VISIBLE
                                            balance_amount_layout!!.visibility = View.VISIBLE
                                            deposit_amount_layout!!.visibility = View.VISIBLE
//                                            val total =
//                                                rateGrandTotal!!.toDouble() +
//                                                        offeredAdvance

                                            val total = rateGrandTotal!!.toDouble()
                                            var rateGrandTotal_ = 0.0
                                            var rateGrandTotalCalc =  totalHelperFare.toDouble() + totalFareDuration.toDouble() + rateDistanceTotal!!.toDouble() + insurance
                                            if (is_offer_advance_paid == 1){
                                                rateGrandTotal_ = rateGrandTotal!!.toDouble() + offeredAdvance
                                            }else{
                                                rateGrandTotal_ = rateGrandTotal!!.toDouble()
                                            }

//                                            paymnet_type!!.text = "Paid by " + paymentType
//                                            Total!!.text = CONSTANTS.CURRENCY + precision.format(totalHelperFare + totalFareDuration + insurance!!)
                                            if (rateGrandTotalCalc >= rateGrandTotal_) {
                                                Total!!.text = CONSTANTS.CURRENCY + rateGrandTotalCalc
                                            }else{
                                                Total!!.text = CONSTANTS.CURRENCY + rateGrandTotal_
                                            }
                                            exact_amount!!.text = CONSTANTS.CURRENCY + precision.format(total)
                                            val remaining_amount =
                                                total - offeredAdvance
                                            Balance_Due!!.text = CONSTANTS.CURRENCY +precision.format(remaining_amount)
                                            depsoit_amount!!.text =
                                                CONSTANTS.CURRENCY + offeredAdvance
                                        } else {
                                            estimated_price!!.visibility = View.VISIBLE
                                            balance_amount_layout!!.visibility = View.GONE
                                            deposit_amount_layout!!.visibility = View.GONE
                                            if (paymentType == "Stripe") {
                                                paymnet_type!!.text = "Paid by card"
                                            }else{
                                                paymnet_type!!.text = "Paid by cash"
                                            }
                                            Total!!.text = CONSTANTS.CURRENCY + rateGrandTotal


                                            exact_amount!!.text = CONSTANTS.CURRENCY + precision.format(
                                                grand_total
                                            )
                                        }
                                    } else {
                                        estimated_price!!.visibility = View.GONE
                                        balance_amount_layout!!.visibility = View.VISIBLE
                                        deposit_amount_layout!!.visibility = View.VISIBLE
                                        val remaining_amount =
                                            offeredPrice - offeredAdvance
                                        Balance_Due!!.text = CONSTANTS.CURRENCY + remaining_amount
                                        depsoit_amount!!.text = CONSTANTS.CURRENCY + offeredAdvance
//                                        paymnet_type!!.text = "Paid by " + paymentType
                                        Total!!.text = CONSTANTS.CURRENCY + rateGrandTotal


                                        exact_amount!!.text =
                                            CONSTANTS.CURRENCY + precision.format(grand_total)
                                    }
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                    Toast.makeText(this@MoveInvoice, e.message, Toast.LENGTH_SHORT)
                                        .show()
                                } 
                            }
                            
                        } else {
                            Toast.makeText(
                                this@MoveInvoice,
                                response.body()!!.status!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this@MoveInvoice, response.raw().message, Toast.LENGTH_SHORT).show()
                    }
                    progress_bar!!.visibility = View.GONE
                }

                override fun onFailure(
                    call: Call<BookingDetailRespone?>,
                    t: Throwable
                ) {
                    progress_bar!!.visibility = View.GONE
                    Utils.showToast(t.message)
                }
            })
    }

    override fun onClick(v: View) {
        if (v.id == R.id.iv_back) {
            finish()
        } else if (v.id == R.id.Support) {
            startActivity(Intent(this@MoveInvoice, ContactFragment::class.java))
        } else if (v.id == R.id.share_invoice) {
            val bitmap_view = ScreenShott.getInstance().takeScreenShotOfView(view)


            // save bitmap to cache directory
            try {
                val cachePath = File(this.cacheDir, "images")
                cachePath.mkdirs() // don't forget to make the directory
                val stream =
                    FileOutputStream("$cachePath/image.png") // overwrites this image every time
                bitmap_view.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()
                val imagePath = File(this.cacheDir, "images")
                val newFile = File(imagePath, "image.png")
                val contentUri =
                    FileProvider.getUriForFile(this, "com.example.myapp.fileprovider", newFile)
                if (contentUri != null) {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
                    shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri))
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    startActivity(Intent.createChooser(shareIntent, "Choose an app"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}