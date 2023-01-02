package com.vanmove.passesger.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.interfaces.VanSelectionInterface
import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.calculateFareUpto
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.single_view_taxi_new.view.*

class VanBookingAdapter(
    private val context: Context,
    var data: List<GetVehicles>,
    private val mVanListner: VanSelectionInterface,
    var itemClickRecycler: OnItemClickRecycler,
    var distance_in_miles: String
) : RecyclerView.Adapter<Holder?>() {
    var holder: Holder? = null
    private val inflater: LayoutInflater
    var estimated_duartion: String?
    var date_future_booking_str: String?
    var time_slots_future_booking: String?

    var helpers: String?
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = inflater.inflate(R.layout.single_view_taxi_new, parent, false)
        holder = Holder(view)
        return holder as Holder
    }

    override fun onBindViewHolder(holder: Holder, @SuppressLint("RecyclerView") position: Int) {

        holder.view!!.run {

            data[position].run {
                var price = Fare_Estimation(estimated_duartion, this)


                price = String.format("%.2f", price.toDouble())
                if (CONSTANTS.isPromoCodeApply) {
                    var Discount_Price =
                        "<del>" + CONSTANTS.CURRENCY + price + "</del><br>"
                    price = (price.toDouble()
                            - ((price.toDouble()
                            * CONSTANTS.promoCode!!.percentage!!.toDouble()) / 100)).toString()
                    Discount_Price += CONSTANTS.CURRENCY + String.format("%.2f", price.toDouble())

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fare_estimated.setText(
                            Html.fromHtml(
                                Discount_Price,
                                Html.FROM_HTML_MODE_COMPACT
                            )
                        )
                    } else {
                        fare_estimated.setText(Html.fromHtml(Discount_Price))
                    }


                } else {
                    fare_estimated.text = CONSTANTS.CURRENCY + String.format("%.2f", price.toDouble())

                }

                if (position == globalPosition) {
                    selector_view.setBackgroundColor(Color.parseColor("#D3D3D3"))
                    mVanListner.onVanImageClicking(vehicle_id, this, holder, position)
                } else {
                    selector_view.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                Estimated_Duartion.text = "Estimated duration: $estimated_duartion"
                tv_cabbii_type.text = type + " INFO"
                tv_vehicle_name2.text = vehicle_name
                Picasso.get().load(Utils.imageUrl + picture)
                    .into(iv_vehicles)
                if (vehicle_id == "13") {
                    book_later.gone()
                } else {
                    if (driver_time == null) {
                        book_later.visible()
                    } else if (driver_time!! > 0) {
                        book_later.gone()
                    } else if (driver_time == 0) {
                        book_later.gone()
                    } else {
                        book_later.visible()
                    }
                }
                if (!date_future_booking_str!!.isEmpty()) {
                    tv_distance_min.gone()
                    tv_distance_min2.gone()
                    book_later.gone()
                } else {
                    tv_distance_min.visible()
                    if (driver_time == null) {
                        tv_distance_min.text = "N/A for ASAP"
                        tv_distance_min2.gone()
                    } else if (driver_time!! > 0) {
                        tv_distance_min.gone()
                        tv_distance_min2.visible()
                        tv_distance_min2.text = "" + driver_time + "\nMIN\nAWAY"

                    } else if (driver_time == 0) {
                        tv_distance_min.gone()
                        tv_distance_min2.visible()
                        tv_distance_min2.text = "" + driver_time + 1 + "\nMIN\nAWAY"

                    } else {
                        tv_distance_min.text = "N/A for ASAP"
                        tv_distance_min2.gone()
                    }
                }
                when (helpers) {
                    "0" -> help1.gone()
                    "1" -> help1.setImageResource(R.drawable.help1)
                    "2" -> help1.setImageResource(R.drawable.help2)
                    "3" -> help1.setImageResource(R.drawable.help3)
                }

                tv_cabbii_type.setOnClickListener { v ->
                    itemClickRecycler.onClickRecycler(
                        v,
                        position
                    )
                }
                holder.itemView.setOnClickListener {
                    globalPosition = position
                    notifyDataSetChanged()
                }
                fare_breakdown.setOnClickListener { v ->
                    itemClickRecycler.onClickRecycler(
                        v,
                        position
                    )
                }
                book_later.setOnClickListener { v ->
                    globalPosition = position
                    notifyDataSetChanged()
                    itemClickRecycler.onClickRecycler(v, position)
                }
            }


        }


    }

    override fun getItemCount(): Int {
        return data.size
    }


    private fun Fare_Estimation(
        time: String?,
        vehicles: GetVehicles
    ): String {
        if (time == CONSTANTS.estimated_duration_list[1]) {
            return calculateFareUpto(
                1.0,
                vehicles,
                distance_in_miles,
                context
            )


        }
        else if (time == CONSTANTS.estimated_duration_list[2]) {

            return calculateFareUpto(
                2.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[3]) {

            return calculateFareUpto(
                2.50,
                vehicles,
                distance_in_miles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[4]) {

            return calculateFareUpto(
                3.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[5]) {

            return calculateFareUpto(
                3.50,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[6]) {

            return calculateFareUpto(
                4.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[7]) {

            return calculateFareUpto(
                4.50,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[8]) {

            return calculateFareUpto(
                5.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[9]) {

            return calculateFareUpto(
                5.50,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[10]) {

            return calculateFareUpto(
                6.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[11]) {

            return calculateFareUpto(
                6.50,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[12]) {

            return calculateFareUpto(
                7.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[13]) {

            return calculateFareUpto(
                7.50,
                vehicles,
                distance_in_miles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[14]) {

            return calculateFareUpto(
                8.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[15]) {

            return calculateFareUpto(
                8.50,
                vehicles,
                distance_in_miles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[16]) {

            return calculateFareUpto(
                9.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[17]) {

            return calculateFareUpto(
                9.50,
                vehicles,
                distance_in_miles,
                context
            )

        }


        else if (time == CONSTANTS.estimated_duration_list[18]) {

            return calculateFareUpto(
                10.0,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[19]) {

            return calculateFareUpto(
                10.50,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[20]) {

            return calculateFareUpto(
                11.0,
                vehicles,
                distance_in_miles,
                context
            )

        }

        else if (time == CONSTANTS.estimated_duration_list[21]) {

            return calculateFareUpto(
                11.50,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else if (time == CONSTANTS.estimated_duration_list[22]) {

            return calculateFareUpto(
                12.0,
                vehicles,
                distance_in_miles,
                context
            )

        }
        else {
            return "0"

        }
    }

    companion object {
        private var globalPosition = 0
    }

    init {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        estimated_duartion =
            getPreferences(CONSTANTS.estimated_duartion, context)
        date_future_booking_str = getPreferences(
            CONSTANTS.date_future_booking_str,
            context
        )
        time_slots_future_booking= getPreferences(CONSTANTS.time_slots_future_booking_str,context)
        helpers = getPreferences(CONSTANTS.helpers, context)
    }


}