package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.model.Porters_History
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils.changeDateFormat
import kotlinx.android.synthetic.main.single_view_porters_job_previous.view.*
import java.util.*

class Porters_Previous_Adpater(
    var context: Context,
    list: ArrayList<Porters_History>
) : RecyclerView.Adapter<Holder>() {
    private val list: List<Porters_History>
    var inflater: LayoutInflater
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view =
            inflater.inflate(R.layout.single_view_porters_job_previous, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        viewHolder: Holder,
        position: Int
    ) {

        viewHolder.view!!.run {
            list[position].run {
                viewHolder.view!!.tv_date_time.text = changeDateFormat(
                    request_trip_start_time, "yyyy-MM-dd HH:mm:ss",
                    "dd-MMM-yyyy h:mm a"
                )
                viewHolder.view!!.tv_price.text = CONSTANTS.CURRENCY + rate_grand_total
                viewHolder.view!!.job_id.text = "Job Id: $request_id"
                viewHolder.view!!.text_pick_up_loc.text = pick_up_name
            }

        }


    }

    override fun getItemCount(): Int {
        return list.size
    }


    init {
        this.list = list
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}