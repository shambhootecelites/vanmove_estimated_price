package com.vanmove.passesger.view_holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.vanmove.passesger.R


class NavDrawerViewHolders(v: View) {
    private val iv_drawer_icon: ImageView
    private val tv_drawer_title: TextView
    fun setValues(icon: Int, title: String?) {
        iv_drawer_icon.setImageResource(icon)
        tv_drawer_title.text = title
    }

    init {
        iv_drawer_icon =
            v.findViewById<View>(R.id.iv_drawer_icon) as ImageView
        tv_drawer_title = v.findViewById<View>(R.id.tv_drawer_text) as TextView
    }
}