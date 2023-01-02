package com.vanmove.passesger.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.SelectVehicle

class SelectVechileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_vechile)

        val distance_in_miles = intent.getStringExtra("distance_in_miles")

        val transaction1 =
            supportFragmentManager.beginTransaction()
        val fragment = SelectVehicle()

        val bundle = Bundle()
        bundle.putString("distance_in_miles", distance_in_miles)
        fragment.arguments = bundle

        transaction1.replace(R.id.container_fragments,fragment)
        transaction1.commit()
    }
}