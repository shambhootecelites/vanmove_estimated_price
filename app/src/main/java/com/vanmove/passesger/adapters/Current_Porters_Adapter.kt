package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.APIModel.PorterHistoryModel
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.single_view_porters_job_current.view.*

class Current_Porters_Adapter(
    private val context: Context, private val portersArrayList: List<PorterHistoryModel>,
    private val itemClickRecycler: OnItemClickRecycler
) : RecyclerView.Adapter<Holder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view =
            inflater.inflate(R.layout.single_view_porters_job_current, null)
        return Holder(view)
    }

    override fun onBindViewHolder(
        viewHolder: Holder,
        position: Int
    ) {

        viewHolder.view!!.run {
            portersArrayList[position].run {

                tv_porter_name.text = firstName + " " + last_name
                tv_porter_email.text = email
                job_id.text = "Job Id: " + requestId
                when (isStatus) {
                    2 -> {
                        tv_status.text = "Job Accepted"
                    }
                    9 -> {
                        tv_status.text = "Job Started"
                        btn_cancel_porter_request.visibility = View.INVISIBLE
                    }
                    else -> {
                        tv_status.text = "In Progress"
                    }
                }
                Picasso.get()
                    .load(Utils.imageUrl + picture)
                    .error(R.drawable.ic_profile_no_server_pic)
                    .into(iv_porter_pic)
                iv_btn_call_porter.setOnClickListener {
                    itemClickRecycler.onClickRecycler(
                        it,
                        position
                    )
                }
                btn_cancel_porter_request.setOnClickListener {
                    itemClickRecycler.onClickRecycler(
                        it,
                        position
                    )
                }
                btn_track_porter.setOnClickListener { view ->
                    itemClickRecycler.onClickRecycler(
                        view,
                        position
                    )
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return portersArrayList.size
    }


}