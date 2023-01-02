package com.vanmove.passesger.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.vanmove.passesger.R
import com.vanmove.passesger.model.NavDrawerItem
import com.vanmove.passesger.view_holders.NavDrawerViewHolders
import java.util.*


class NavDrawerAdapter(
    private val nav_items: ArrayList<NavDrawerItem>,
    private val context: Context
) : BaseAdapter() {
    override fun getCount(): Int {
        return nav_items.size
    }

    override fun getItem(position: Int): Any {
        return nav_items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var single_view = convertView
        val view_holder: NavDrawerViewHolders
        if (single_view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            single_view = inflater.inflate(R.layout.single_view_drawer_list, parent, false)
            view_holder = NavDrawerViewHolders(single_view)
            single_view.tag = view_holder
        } else {
            view_holder = single_view.tag as NavDrawerViewHolders
        }
        val drawer_item = nav_items[position]
        view_holder.setValues(drawer_item.icon, drawer_item.title_name)
        return single_view
    }



}