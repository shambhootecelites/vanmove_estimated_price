package com.vanmove.passesger.fragments.PorterNavigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.ContactFragment
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.utils.CONSTANTS
import kotlinx.android.synthetic.main.fragment_payment_porter.view.*
import kotlinx.android.synthetic.main.support.*


class PaymentPorter : Fragment(R.layout.fragment_payment_porter),
    View.OnClickListener {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linkViews()
        if (arguments != null) {
            val porter_fare = arguments!!.getString("porter_fare")
            view.job_amount!!.text = "Pay " + CONSTANTS.CURRENCY + porter_fare + " to Porter"
        }

        contact_Number.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    ContactFragment::class.java
                )
            )
        }
    }


    private fun linkViews() {

        view!!.btn_book_new_vanmove!!.setOnClickListener(this)
        view!!.btn_rate!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_rate -> {
                val transaction =
                    activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragments, RatingPorter())
                transaction.commit()
            }
            R.id.btn_book_new_vanmove -> {
                val intent = Intent(context, MainScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                activity!!.finish()
            }
        }
    }
}