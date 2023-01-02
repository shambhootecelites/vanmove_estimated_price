package com.vanmove.passesger.fragments.MoveFlow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import kotlinx.android.synthetic.main.titlebar.*

class RegularRideJobSheetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regular_ride_job_sheet)
        val transaction1 =
            supportFragmentManager.beginTransaction()
        transaction1.replace(R.id.container_fragments, RegularRideJobSheet())
        transaction1.commit()

        iv_back.setOnClickListener {
            finish()
        }
    }
}