package com.vanmove.passesger.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.Request_Porter_Fragment
import kotlinx.android.synthetic.main.titlebar.*

class PorterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regular_ride_job_sheet)
        val transaction1 = supportFragmentManager
            .beginTransaction()
        val fragment = Request_Porter_Fragment()
        transaction1.replace(R.id.container_fragments, fragment)
        transaction1.commit()

        iv_back.setOnClickListener {
            finish()
        }
    }
}