package com.vanmove.passesger.activities.FullImage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_zoom_image2.*
import kotlinx.android.synthetic.main.titlebar.*

class ZoomImage2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image2)

        iv_back.setOnClickListener {
            finish()
        }

        val position = intent.getIntExtra(
            "select_image_position", 0
        )

        myZoomageView.setImageBitmap(
            Utils.GetBitmapImage(
                Utils.getInventoryImagesList[position]
            )
        )

    }
}