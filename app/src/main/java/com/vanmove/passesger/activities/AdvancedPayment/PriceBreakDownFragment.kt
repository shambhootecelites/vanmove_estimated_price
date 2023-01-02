package com.vanmove.passesger.activities.AdvancedPayment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.PayAdvance
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.price_breakdown.view.*

class PriceBreakDownFragment : Fragment(R.layout.price_breakdown) {

    private var offered_amount_double = 0.0
    private var offered_amount_double_needed_to_reserve = 0.0
    private var offered_amount_double_remaining = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookingDetails


    }


    private val bookingDetails: Unit
        get() {
            val driver_first_name = activity!!.intent.getStringExtra("driver_name")
            val rating_driver = activity!!.intent.getStringExtra("rating")
            val moves = activity!!.intent.getStringExtra("move")
            val picture = activity!!.intent.getStringExtra("picture")
            var offeredPrice = activity!!.intent.getStringExtra("offeredPrice")
            offeredPrice = offeredPrice!!.replace(CONSTANTS.CURRENCY, "")

            view!!.tv_name!!.text = driver_first_name
            Picasso.get()
                .load(Utils.imageUrl + picture)
                .error(R.drawable.ic_profile_no_server_pic)
                .into(view!!.iv_driver)
            view!!.tv_moves!!.text = "Moves :$moves"
            try {
                view!!.ratingBar!!.rating = rating_driver!!.toFloat()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            offered_amount_double = offeredPrice.toDouble()
            offered_amount_double_needed_to_reserve = offered_amount_double * .25
            offered_amount_double_remaining = offered_amount_double * .75
            view!!.tv_offered_amount!!.text = CONSTANTS.CURRENCY + " " + CONSTANTS.precision.format(
                offered_amount_double
            )
            view!!.tv_remaining_payment!!.text =
                CONSTANTS.CURRENCY + " " + CONSTANTS.precision.format(
                    offered_amount_double_needed_to_reserve
                )
            view!!.tv_paid_amount!!.text = CONSTANTS.CURRENCY + " " + CONSTANTS.precision.format(
                offered_amount_double_remaining
            )
            view!!.btn_book_now!!.setOnClickListener {
                val newFragment = PayAdvance()
                val transaction =
                    fragmentManager!!.beginTransaction()
                val bundle = Bundle()
                bundle.putString("is_offer", "is_offer")
                newFragment.arguments = bundle
                transaction.replace(R.id.container_fragments, newFragment)
                transaction.commit()
            }
        }
}