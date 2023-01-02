package com.vanmove.passesger.adapters

import android.content.Context
import android.graphics.Color
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
import com.vanmove.passesger.utils.Utils.savePreferences
import kotlinx.android.synthetic.main.single_view_2.view.*


class VanBookingAdapter2(
    private val context: Context,
    var data: List<GetVehicles>,
    private val mVanListner: VanSelectionInterface,
    var itemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var holder: Holder? = null
    private val inflater: LayoutInflater
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = inflater.inflate(R.layout.single_view_2, parent, false)
        holder = Holder(view)
        return holder!!
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {

        holder.view!!.run {
            data[position].run {
                try {
                    if (position == globalPosition) {
                        selector_view.setBackgroundColor(Color.parseColor("#1F9EED"))
                        savePreferences(
                            CONSTANTS.vehicle_name,
                            vehicle_name,
                            context
                        )
                        mVanListner.onVanImageClicking(vehicle_id, this, null, position)
                    } else {
                        selector_view.setBackgroundColor(Color.parseColor("#ffffff"))
                    }
                    tv_cabbii_type.text = vehicle_name
                    Picasso.get().load(Utils.imageUrl + picture)
                        .into(iv_vehicles)
                    iv_vehicle_info.setOnClickListener { v ->
                        itemClickRecycler.onClickRecycler(
                            v,
                            position
                        )
                    }
                    holder.itemView.setOnClickListener {
                        globalPosition = position
                        notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


    companion object {
        private var globalPosition = 0
    }

    init {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}