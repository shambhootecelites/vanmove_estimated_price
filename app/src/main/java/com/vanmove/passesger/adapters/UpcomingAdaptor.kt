package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.UpcomingBookings
import com.vanmove.passesger.utils.Utils.changeDateFormat
import kotlinx.android.synthetic.main.item_upcoming_bookings_new.view.*

class UpcomingAdaptor(
    var context: Context, var list: List<UpcomingBookings>,
    var onItemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var inflater: LayoutInflater
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = inflater.inflate(R.layout.item_upcoming_bookings_new, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        viewHolder: Holder,
        position: Int
    ) {
        try {

            viewHolder.view!!.run {
                list[position].run {
                    tv_pickup.text = pickup
                    tv_destination.text = destination
                    isStatus.toString().let {
                        when (it) {
                            "0" -> btn_status.text = "Driver not yet assigned"
                            "1" -> btn_status.text = "Driver Assigned"
                            "2" -> btn_status.text = "Driver Accepted"
                            "12" -> btn_status.text = "Advance Paid"
                        }
                    }

                    tv_date_time.text = changeDateFormat(
                        timestamp, "yyyy-MM-dd HH:mm:ss",
                        "dd-MMM-yyyy h:mm a"
                    )

                }

                action.setOnClickListener { v ->
                    onItemClickRecycler.onClickRecycler(
                        v,
                        position
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    init {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}