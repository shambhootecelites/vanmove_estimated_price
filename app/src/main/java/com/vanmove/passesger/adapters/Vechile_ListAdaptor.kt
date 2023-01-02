package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.GetVehicles
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.vechile_list_item.view.*

class Vechile_ListAdaptor(
    var data: List<GetVehicles>, var context: Context,
    var itemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var holder: Holder? = null
    private val inflater: LayoutInflater
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = inflater.inflate(R.layout.vechile_list_item, parent, false)
        holder = Holder(view)
        return holder!!
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {

        holder.view!!.run {
            data[position].run {
                tv_vehicle_name.text = vehicle_name
                Picasso.get().load(Utils.imageUrl + picture)
                    .into(iv_vehicles)
                Usage.text = "Usage: " + info
                Capacity.text = "Max Load: " + capacity
                Max_Load.text = "Capacity: " + weight
                External_DIMESIONS.text = "External: " + external_dimension
                Internal_DIMESIONS.text = "Internal: " + internal_dimension
                select.setOnClickListener { v ->
                    itemClickRecycler.onClickRecycler(
                        v,
                        position
                    )
                }
            }

        }

    }


    override fun getItemCount(): Int {
        return data.size
    }


    init {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}