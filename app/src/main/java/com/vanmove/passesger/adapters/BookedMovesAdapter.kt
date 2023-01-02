package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.Booking
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils.changeDateFormat
import kotlinx.android.synthetic.main.single_view_previous_booked.view.*
import java.util.*

class BookedMovesAdapter(
    private val context: Context, private val previous_booked_list: ArrayList<Booking>,
    var onItemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var inflater: LayoutInflater
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = inflater.inflate(R.layout.single_view_previous_booked, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {
        holder.view!!.run{
            previous_booked_list[position].run {
                tv_drop_off_name.text = drop_off_name
                tv_pick_up_name.text = pick_up_name
                val format_time = changeDateFormat(
                    trip_end_time,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd-MMM-yyyy h:mm a"
                )
                tv_date_time.text = format_time
                if (is_future == 1) {
                    val total = rate_grand_total!!.toDouble() +
                            offered_advance
                    tv_price.text = CONSTANTS.CURRENCY + CONSTANTS.precision.format(total)
                } else {
                    tv_price.text =
                        CONSTANTS.CURRENCY + CONSTANTS.precision.format(rate_grand_total!!.toDouble())
                }
                view_detail.setOnClickListener { v ->
                    onItemClickRecycler.onClickRecycler(
                        v,
                        position
                    )
                }
            }
        }
       

    }

    override fun getItemCount(): Int {
        return previous_booked_list.size
    }

    init {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}