package com.vanmove.passesger.fragments.MoveHistory

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.BookedMovePagerAdapter
import com.vanmove.passesger.fragments.HomeFrragment
import com.vanmove.passesger.utils.CONSTANTS
import kotlinx.android.synthetic.main.fragment_booked_moves.*
import kotlinx.android.synthetic.main.fragment_booked_moves.view.*

class BookedMovesMain : Fragment(R.layout.fragment_booked_moves) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.sliding_tab!!.setupWithViewPager(view_pager)
        setupViewPager()
        if (arguments != null) {
            val move_to_move_upcoming =
                arguments!!.getString(CONSTANTS.move_to_move_upcoming, "")
            if (!move_to_move_upcoming.isEmpty()) {
                view_pager!!.setCurrentItem(1, true)
            }
        }
        goBackMethod()
    }





    private fun setupViewPager() {
        BookedMovePagerAdapter(activity!!.supportFragmentManager).let {
            it.addFragment(PreviousBookedFragment(), "Previous")
            it.addFragment(UpcomingBookedFragment(), "Upcoming")
            view!!.view_pager!!.adapter = it
        }

    }


    private fun goBackMethod() {
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                val transaction =
                    fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_fragments, HomeFrragment())
                transaction.commit()
                return@OnKeyListener true
            }
            false
        })
    }
}