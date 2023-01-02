package com.vanmove.passesger.adapters



import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.InsurancePlan
import com.vanmove.passesger.model.TimeSlot
import com.vanmove.passesger.utils.CONSTANTS.Flexible
import com.vanmove.passesger.utils.REQUEST_TYPE
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.time_slots_row_items.view.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class InsuranceAdapter(var context: Context, var list: List<InsurancePlan>, var onItemClickRecycler: OnItemClickRecycler) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_LIST : Int = 1
    var inflater: LayoutInflater? = null
    private var selected_pos=-1;

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

        val header = LayoutInflater.from(parent.context).inflate(R.layout.insurance_row_item,parent,false)
        return ViewHolder(header)
    }


    override fun getItemCount(): Int {
        System.out.println("List Size::"+list.size)
        return list.size
    }
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        val tv_insurance_name = itemView.findViewById(R.id.tv_insurance_name) as TextView
        val isSelectedCheck = itemView.findViewById(R.id.isSelected) as ImageView
        val rel_free = itemView.findViewById(R.id.rel_free) as LinearLayout
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        try {
            var listItem : InsurancePlan ? = null

            listItem  = list[position]
            //{"plan_name":"I have home content insurance","plan_price":"0"} is_select
            System.out.println("listItem!!.time::"+listItem!!.plan_name)


            if(holder is ViewHolder) {
                if (selected_pos==position){
                    holder.isSelectedCheck.visibility=View.VISIBLE
                    holder.isSelectedCheck.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_box_24))
                    holder.tv_insurance_name!!.setTextColor(Color.BLACK)
                    holder.rel_free!!.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    list[position].isSelect=true
                }
                else{
                    holder.isSelectedCheck.visibility=View.VISIBLE
                    holder.tv_insurance_name!!.setTextColor(Color.BLACK)
                    holder.isSelectedCheck.setImageDrawable(context.getDrawable(R.drawable.ic_not_select_check_box))

                    holder.rel_free!!.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    list[position].isSelect=false
                }
                holder.tv_insurance_name.text=listItem.plan_name
                holder.rel_free.setOnClickListener {
                    if (selected_pos==position){
                        selected_pos=-1
                        list[position].isSelect=false
                    }
                    else{
                        selected_pos=position
                        list[position].isSelect=true
                    }
                    notifyDataSetChanged()
                    onItemClickRecycler.onClickRecycler(it, position)

                }

            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }

    }
}