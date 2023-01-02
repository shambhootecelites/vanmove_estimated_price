package com.vanmove.passesger.fragments.PromoCode

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.Holder
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.PromoCode
import kotlinx.android.synthetic.main.promo_item.view.*

class PromoAdaptor(
    var context: Context,
    var list: List<PromoCode>,
    var onItemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    var inflater: LayoutInflater
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = inflater.inflate(R.layout.promo_item, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {

        holder.view!!.run {
            list[position].run {
                promo_title.text = title
                promo_code.text = code
                discount_percnatge.text = percentage + "% OFF"
                holder.itemView.setOnClickListener { v -> onItemClickRecycler.onClickRecycler(v, position) }
                share_code.setOnClickListener { v ->
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


    init {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}