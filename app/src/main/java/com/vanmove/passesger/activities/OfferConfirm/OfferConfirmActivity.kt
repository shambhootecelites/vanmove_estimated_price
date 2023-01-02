package com.vanmove.passesger.activities.OfferConfirm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import kotlinx.android.synthetic.main.titlebar.*

class OfferConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_confirm)
        val transaction2 =
            supportFragmentManager.beginTransaction()
        transaction2.replace(R.id.container_fragments, OfferConfimrationFragmence())
        transaction2.commit()

        iv_back.setOnClickListener {
            finish()
        }
    }
}