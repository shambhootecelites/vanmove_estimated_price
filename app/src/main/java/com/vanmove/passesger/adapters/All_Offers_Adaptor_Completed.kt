package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageLoader
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.AllOffers
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils.changeDateFormat
import kotlinx.android.synthetic.main.item_all_offers_completed.view.*
import java.util.*

class All_Offers_Adaptor_Completed(
    var context: Context, list: ArrayList<AllOffers>,
    onItemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    private val list: List<AllOffers>
    var inflater: LayoutInflater? = null
    private val imageLoader: ImageLoader? = null
    var onItemClickRecycler: OnItemClickRecycler
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.item_all_offers_completed, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        viewHolder: Holder,
        position: Int
    ) {
        viewHolder.view!!.run {

            list[position].run {

                val format_time = changeDateFormat(
                    timse_stamp,
                    "yyyy-MM-dd HH:mm:ss",
                    "dd-MMM-yyyy h:mm a"
                )
                tv_date_time.text = format_time
                tv_price.text =
                    CONSTANTS.CURRENCY + String.format("%.2f", offered_price!!.toDouble())
                tv_drop_off_name.text = drop_off_name
                tv_pick_up_name.text = pick_up_name


            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


    init {
        this.list = list
        this.onItemClickRecycler = onItemClickRecycler
    }
}