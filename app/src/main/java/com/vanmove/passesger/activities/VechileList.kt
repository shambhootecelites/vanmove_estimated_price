package com.vanmove.passesger.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.Vechile_ListAdaptor
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_vechile_list.*
import kotlinx.android.synthetic.main.titlebar.*

class VechileList : AppCompatActivity(), OnItemClickRecycler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vechile_list)
        Vechile_ListAdaptor(
            Utils.OnlyVansList, this@VechileList,
            this@VechileList
        ).let {
            vechile_list.adapter = it
        }
        call.setOnClickListener({ Utils.Dial_Number(this, CONSTANTS.Support_Number) })


        iv_back.setOnClickListener {
            finish()
        }
    }


    override fun onClickRecycler(view: View?, position: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra("vechile_position", position)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}