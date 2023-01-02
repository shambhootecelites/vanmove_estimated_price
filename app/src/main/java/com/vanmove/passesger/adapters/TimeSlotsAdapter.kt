package com.vanmove.passesger.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.TimeSlot
import com.vanmove.passesger.utils.CONSTANTS.Flexible
import com.vanmove.passesger.utils.REQUEST_TYPE
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.time_slots_row_items.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotsAdapter(var context: Context, var req_type: REQUEST_TYPE, var list: List<TimeSlot>, var onItemClickRecycler: OnItemClickRecycler) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_LIST : Int = 1
    var inflater: LayoutInflater? = null

    override fun getItemViewType(position: Int): Int {

       /* if(position == 0)
        {
            return TYPE_HEADER
        }*/
        return TYPE_LIST
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      /*  if(viewType == TYPE_HEADER)
        {
            val header = LayoutInflater.from(parent.context).inflate(R.layout.flexible_time_slots_row_items,parent,false)
            return ViewHolderHeader(header)
        }*/

        val header = LayoutInflater.from(parent.context).inflate(R.layout.time_slots_row_items,parent,false)
        return ViewHolder(header)
    }


    override fun getItemCount(): Int {
      System.out.println("List Size::"+list.size)
        return list.size
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val time_txt = itemView.findViewById(R.id.time_txt) as TextView
        val timeSlotsLL = itemView.findViewById(R.id.timeSlotsLL) as LinearLayout
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var listItem : TimeSlot ? = null

        listItem  = list[position]
        System.out.println("listItem!!.time::"+listItem!!.time)
        if(holder is ViewHolder) {
            if (position == 0) {

                when(req_type){
                    REQUEST_TYPE.REQUEST_ASAP -> {
                        holder.time_txt.text = listItem.time
                    }
                    REQUEST_TYPE.REQUEST_AS_LETTER -> {
                        holder.time_txt.text = Flexible
                    }

                }

                holder.timeSlotsLL.setTag(listItem!!.time)


            }
            else {
                val inputformat = SimpleDateFormat("HH:mm")
                val mDate = inputformat.parse(listItem!!.time);
                val outputformat = SimpleDateFormat("hh:mm aa")
                val strDate = outputformat.format(mDate);
                holder.time_txt.text = strDate.toString()
                holder.timeSlotsLL.setTag("")
            }
            holder.timeSlotsLL.setOnClickListener { v ->
                onItemClickRecycler.onClickRecycler(
                    v,
                    position
                )
            }

        }
    }
}