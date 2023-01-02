package com.vanmove.passesger.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

/**
 * Created by Linez-001 on 3/22/2017.
 */
class BookedMovePagerAdapter(fm: FragmentManager?) :
    FragmentStatePagerAdapter(fm!!) {
    private val fragment_list =
        ArrayList<Fragment>()
    private val title_list =
        ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return fragment_list[position]
    }

    override fun getCount(): Int {
        return fragment_list.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragment_list.add(fragment)
        title_list.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title_list[position]
    }
}