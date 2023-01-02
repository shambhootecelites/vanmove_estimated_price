package com.vanmove.passesger.activities.AdvancedPayment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.PayAdvance
import com.vanmove.passesger.model.APIModel.BookingDetailRespone
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.ShowProgressDialog
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.GetRequestBody
import kotlinx.android.synthetic.main.titlebar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AdvancedPaymentActivity : AppCompatActivity(R.layout.activity_advanced_payment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iv_back.visibility = View.GONE

        intent.getStringExtra("request_id").let {
            getbookingdetail(it!!)
        }

    }


    override fun onBackPressed() {
//        val alert_msg: String = "You need to pay a deposit to confirm your booking. Pay deposit now."
//        showAlertMessage_fixed(this, alert_msg, "confirm_successfull", this)
//        if (intent.hasExtra(CONSTANTS.from_home)) {
//            startActivity(
//                Intent(this, MainScreenActivity::class.java)
//            )
//
//        }
//        finish()

    }

    private fun getbookingdetail(reqID: String) {
        ShowProgressDialog.showDialog2(this)
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["request_id"] = reqID

        CONSTANTS.mApiService.get_booking_detail(GetRequestBody(postParam), CONSTANTS.headers)!!
            .enqueue(object : Callback<BookingDetailRespone?> {
                override fun onResponse(
                    call: Call<BookingDetailRespone?>,
                    response: Response<BookingDetailRespone?>
                ) {
                    if (response.body() != null) {
                        if (response.body()!!.status!!.code == "1000") {
                            val bookingDetailRespone = response.body()
                            val request = bookingDetailRespone!!.request
                            val driver = bookingDetailRespone.driver
                            val rating = bookingDetailRespone.rating

                            val transaction2 = supportFragmentManager.beginTransaction()
                            rating!!.run {
                                driver!!.run {
                                    request!!.run {
                                        intent.apply {
                                            putExtra("request_id", requestId)
                                            putExtra("offered_advance", offeredAdvance.toString())
                                            putExtra("vehicle_name", vehicleClassName)
                                            putExtra("helpers_count", helpersCount.toString())
                                            putExtra("date_future_booking_str", timestamp)
                                            putExtra(
                                                "estimated_payment",
                                                estimated_payment.toString()
                                            )
                                            putExtra("offeredPrice", offeredPrice.toString())
                                            putExtra("type", isType)
                                            putExtra(
                                                "driver_name", driver_first_name + " " +
                                                        driver_last_name
                                            )
                                            putExtra("rating", rating_driver)
                                            putExtra("move", moves)
                                            putExtra("picture", picture)
                                        }
                                        if (isType == "Fixed") {
                                            transaction2.replace(
                                                R.id.container_fragments,
                                                PriceBreakDownFragment()
                                            )
                                        } else {
                                            transaction2.replace(
                                                R.id.container_fragments,
                                                PayAdvance()
                                            )
                                        }
                                        transaction2.commit()
                                    }
                                }
                            }
                        } else {
                            Utils.showToast(
                                response.body()!!.status!!.message
                            )

                        }
                    } else {
                        Utils.showToast(response.raw().message)


                    }
                    ShowProgressDialog.closeDialog()
                }

                override fun onFailure(
                    call: Call<BookingDetailRespone?>,
                    t: Throwable
                ) {
                    ShowProgressDialog.closeDialog()
                    Utils.showToast(t.message)
                }
            })
    }

//    override fun clickPositiveDialogButton(dialog_name: String?) {
//        if (dialog_name == "confirm_successfull") {
//            val transactionPay = supportFragmentManager.beginTransaction()
//
//            transactionPay.replace(
//                R.id.container_fragments,
//                PayAdvance()
//            )
//            transactionPay.commit()
//        }
//    }
//
//    override fun clickNegativeDialogButton(dialog_name: String?) {
//
//    }


}