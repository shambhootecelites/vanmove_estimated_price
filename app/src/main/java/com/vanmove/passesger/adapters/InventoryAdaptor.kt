package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.inventory_item.view.*

class InventoryAdaptor(
    var context: Context,
    var list: ArrayList<String?>,
    var itemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var inflater: LayoutInflater? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.inventory_item, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {


        holder.view!!.run {
            list[position]!!.run {
                if (!this.isEmpty()) {
                    Picasso.get().load(Utils.imageUrl + this).error(R.drawable.error_image)
                        .placeholder(R.drawable.inventory_places_holder).into(holder.view!!.image)
                } else {
                    holder.view!!.image.setImageResource(R.drawable.inventory_places_holder)
                }
                holder.itemView.setOnClickListener { v -> itemClickRecycler.onClickRecycler(v, position) }
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}