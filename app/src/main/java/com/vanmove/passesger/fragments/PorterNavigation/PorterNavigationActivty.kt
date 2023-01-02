package com.vanmove.passesger.fragments.PorterNavigation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MainScreenActivity
import kotlinx.android.synthetic.main.titlebar.*

class PorterNavigationActivty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_porter_navigation_activty)

        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.container_fragments,
                Porter_On_Way()
            )
            commitAllowingStateLoss()
        }


        iv_back.setOnClickListener {
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainScreenActivity::class.java))
        finish()
    }
}