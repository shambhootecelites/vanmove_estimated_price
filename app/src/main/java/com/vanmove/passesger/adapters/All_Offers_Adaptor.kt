package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.UpcomingBookings
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.item_all_offers.view.*

class All_Offers_Adaptor
    (
    var context: Context, var list: List<UpcomingBookings>,
    var onItemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var inflater: LayoutInflater? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.item_all_offers, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        viewHolder: Holder,
        position: Int
    ) {

        viewHolder.view!!.run {

            list[position].run {
                tv_pickup.text = pickup
                tv_destination.text = destination


                when (is_flexible) {
                    "No" -> {
                        date.text = Utils.changeDateFormat(
                            timestamp, "yyyy-MM-dd HH:mm:ss",
                            "dd-MMM-yyyy h:mm a"
                        )
                    }
                    "Yes" -> {
                        date.text = "Flexible"
                    }
                }
                when (isStatus.toString()) {
                    "0" -> btn_status.text = "Driver not yet assigned"
                    "1" -> btn_status.text = "Driver Assigned"
                    "2" -> btn_status.text = "Driver Accepted"
                    "12" -> btn_status.text = "Advance Paid"
                }
                detail_btn.setOnClickListener { v ->
                    onItemClickRecycler.onClickRecycler(
                        v,
                        position
                    )
                }
            }


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}