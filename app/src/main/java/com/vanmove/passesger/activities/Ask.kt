package com.vanmove.passesger.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import kotlinx.android.synthetic.main.activity_welcome.*
class Ask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        btnLogin!!.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }

        btnSignUp!!.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

    }


}