package com.vanmove.passesger.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.Contact_Number
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.Open_Browse
import kotlinx.android.synthetic.main.activity_contact__number.*
import kotlinx.android.synthetic.main.titlebar.*

class Contact_Number : AppCompatActivity(), View.OnClickListener {
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact__number)
        context = this@Contact_Number
        linkViews()
    }

    private fun linkViews() {


        twitter_page_link.setOnClickListener(this)
        fb_page_link.setOnClickListener(this)
        cutomer_suppport.setOnClickListener(this)
        Booking_Team.setOnClickListener(this)
        cutomer_suppport.setText("0333 444 1002")
        Booking_Team.setText(CONSTANTS.Booking_Team)

        iv_back.setOnClickListener {
            finish()
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.twitter_page_link) {
            Open_Browse(context!!, CONSTANTS.twitterlink)
        } else if (v.id == R.id.fb_page_link) {
            Open_Browse(context!!, CONSTANTS.FBLink)
        } else if (v.id == R.id.cutomer_suppport) {
            Dial_Number(
                context!!,
                cutomer_suppport!!.text.toString()
            )
        } else if (v.id == R.id.Booking_Team) {
            Dial_Number(
                context!!,
                Booking_Team!!.text.toString()
            )
        }
    }
}