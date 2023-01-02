package com.vanmove.passesger.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vanmove.passesger.R
import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.rate_breakdown.*

class FareBreakDown : AppCompatActivity(), View.OnClickListener {

    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rate_breakdown)
        context = this@FareBreakDown
        initialization()
    }

    private fun initialization() {

        val data = intent.getStringExtra("data")
        val vehicles = Gson().fromJson(data, GetVehicles::class.java)
        vehicles.run {
            val rate_per_hour = hourly_rate
            val rate_per_mile =rate_per_mile_fare
            val rate_per_minute = rate_per_hour.toDouble()/60

            val rate_per_helper_hour = rate_helper_hourly
           val rate_per_helper_per_minute = rate_per_helper_hour.toDouble()/60
            val vehicle_name =vehicle_name
            val vehicle_id = vehicle_id
            val helpers =
                Utils.getPreferences(CONSTANTS.helpers, context!!)
            van_peR_hour.setText(CONSTANTS.CURRENCY + rate_per_hour + " per hour")
            // van_peR_min.setText(CONSTANTS.CURRENCY + rate_per_minute + " per min")
            helper_peR_hour.setText(CONSTANTS.CURRENCY + rate_per_helper_hour + " per hour")
           // helper_peR_min.setText(CONSTANTS.CURRENCY + rate_per_helper_per_minute + " per min")
            Vechile_Type.setText(vehicle_name)
            van_name.setText("$vehicle_name Hire : ")
            van_name_2.setText("$vehicle_name Hire : ")
            Service_Type.setText("$helpers Helper(s) assist")
            val Time_ = CONSTANTS.move_duration
            val Distance_ = intent.getStringExtra("move_distance")
            Distance.setText("$Distance_ miles")
            Time.setText(Time_+"(Approx)")
            val helper_total = helpers!!.toDouble() * rate_per_helper_hour.toDouble()
            val helper_miles_fare = Distance_!!.toDouble() * rate_per_mile.toDouble()
            val Fare_BreakDown_msg_ = """${type} Hire ${CONSTANTS.CURRENCY}$rate_per_hour per hour + $helpers X Helpers @ ${CONSTANTS.CURRENCY}$rate_per_helper_hour per hour total ${CONSTANTS.CURRENCY}$helper_total + Mileage charge $Distance_ miles @ ${CONSTANTS.CURRENCY}$rate_per_mile per mile total ${CONSTANTS.CURRENCY}${String.format("%.2f", helper_miles_fare)}"""
         //  val helper_total_minute = helpers.toDouble() * rate_per_helper_per_minute.toDouble()
            // var total_first_hour = 0.0
         /*   total_first_hour = if (vehicle_id != "13") {
                helper_total_minute
            } else {
                rate_per_minute.toDouble() + helper_total_minute
            }*/
            Fare_BreakDown_msg.setText(Fare_BreakDown_msg_)
            val estimated_duartion =
                Utils.getPreferences(CONSTANTS.estimated_duartion, context!!)
            val result = Utils.getMinMax(estimated_duartion!!)
            val second_hour_calulation_double =
                Utils.calculateFareUpto(
                    result[1],
                    this,
                    Distance_,
                    context!!
                )
            var duration = ""
            duration = if (result[0] == 1.00 && result[1] == 1.00) {
                "" + result[0]
            }
            else if (result[0] == 1.00 && result[1] == 2.00) {
                result[0].toString() + "-" + result[1]
            }
            else {
               /* var userInput= result[0]
                var hours = userInput.toInt()
                var minutestmp=0.0

                minutestmp = (userInput.minus(hours.toDouble()) )* 60
               val min= String.format("%.2f", minutestmp)

                val TimeString = "$hours Hours $min Minutes"*/
                "" + result[0]
            }
            fist_hour_calulation_title.setText("FOR " + duration + " hour(s) " + CONSTANTS.CURRENCY + second_hour_calulation_double)

          val after_hour_complete_rate = rate_per_minute +
             helpers.toDouble() * rate_per_helper_per_minute
           /* fist_hour_calulation.setText(
                """Then charged @ ${CONSTANTS.CURRENCY}${String.format(
                    "%.2f",
                    after_hour_complete_rate
                )} 
               per minute thereafter.Van hire per min ${CONSTANTS.CURRENCY}$rate_per_minute
+ $helpers helper hire ${CONSTANTS.CURRENCY}$rate_per_helper_per_minute per min = ${CONSTANTS.CURRENCY}""" + String.format(
                 //   "%.2f",
                   // after_hour_complete_rate
                )
            )*/
            fist_hour_calulation.setText(
                """Then charged @ ${CONSTANTS.CURRENCY}${String.format("%.2f", after_hour_complete_rate)}  per minute thereafter.""")
            back_.setOnClickListener(this@FareBreakDown)
            agree.setOnClickListener(this@FareBreakDown)
        }

    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.back_) {
            finish()
        }
        if (id == R.id.agree) {
            finish()
        }
    }
}