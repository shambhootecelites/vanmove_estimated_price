package com.vanmove.passesger.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.ContactFragment
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils.getPreferences
import kotlinx.android.synthetic.main.fragment_pyment_confirmation.*

class PaymentSummary : Fragment(R.layout.fragment_pyment_confirmation),
    View.OnClickListener {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linkViews()
        if (arguments != null) {
            val rate_grand_total = arguments!!.getString("rate_grand_total")
            val payment_type = arguments!!.getString("payment_type")
            val charge_id = arguments!!.getString("charge_id")
            val card_number =
                getPreferences(CONSTANTS.card_number, context!!)
            if (charge_id!!.isEmpty() || charge_id == "null") {
                if (payment_type == CONSTANTS.CASH) {
                    tv_payment!!.text = "Payment Successful"
                    tv_payment_message!!.text =
                        "Thank you for the cash payment of " + CONSTANTS.CURRENCY + rate_grand_total
                } else if (payment_type == CONSTANTS.CARD) {
                    tv_payment!!.text = "Payment Pending"
                    tv_payment_message!!.text =
                        "Some Issue in your Card.Pay " + CONSTANTS.CURRENCY + rate_grand_total +
                                " to driver by Cash"
                }
            } else {
                tv_payment!!.text = "Payment Successful"
                tv_payment_message!!.text =
                    "Thank you, Payment of ${CONSTANTS.CURRENCY}${rate_grand_total}  has been debited from your card ending with number ${card_number}"
            }
        }
    }


    private fun linkViews() {

        btn_book_new_vanmove.setOnClickListener(this)
        btn_rate.setOnClickListener(this)

        contact_Number.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    ContactFragment::class.java
                )
            )
        }
        book_new.setOnClickListener {
            new_booking()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_rate -> {
                val transaction =
                    activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_fragments, RatingFragment())
                transaction.commit()
            }
            R.id.btn_book_new_vanmove -> new_booking()
        }
    }

    private fun new_booking() {
        val intent = Intent(context, MainScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        activity!!.finish()
    }
}