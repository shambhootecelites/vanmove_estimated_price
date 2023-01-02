package com.vanmove.passesger.fragments.PorterHistory

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.adapters.Porter_History_Tab_Adapter
import com.vanmove.passesger.fragments.HomeFrragment
import com.vanmove.passesger.utils.CONSTANTS
import kotlinx.android.synthetic.main.fragment_booked_moves.view.*

class PorterHistory_Tab_Fragment : Fragment(R.layout.fragment_booked_porter) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.sliding_tab!!.setupWithViewPager(view.view_pager)
        setupViewPager(view.view_pager)
    }


    private fun setupViewPager(view_pager: ViewPager?) {
        val adapter =
            Porter_History_Tab_Adapter(activity!!.supportFragmentManager)
        adapter.addFragment(Current_Porters_Fragment(), "Current")
        adapter.addFragment(Porter_Previous_History_Fragment(), "Previous")
        view_pager!!.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        goBackMethod()
    }

    private fun goBackMethod() {
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (arguments != null) {
                    if (arguments!!.containsKey(CONSTANTS.from_circulation_screen)) {
                        val intent = Intent(context, MainScreenActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        activity!!.finish()
                    }
                } else {
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.replace(R.id.container_fragments, HomeFrragment())
                    transaction.commit()
                }

                return@OnKeyListener true
            }
            false
        })
    }
}