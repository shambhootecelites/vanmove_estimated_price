package com.vanmove.passesger.fragments.OfferFlow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.vanmove.passesger.R
import kotlinx.android.synthetic.main.offer_fragment.view.*

class OfferSlogan : Fragment(R.layout.offer_fragment) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.ib_offer_price.setOnClickListener {
            val transaction2 =
                activity!!.supportFragmentManager.beginTransaction()
            transaction2.replace(R.id.container_fragments, MakeOfferFragment())
            transaction2.commit()
        }
    }

}