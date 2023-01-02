package com.vanmove.passesger.activities.FullImage

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vanmove.passesger.R
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_zoom_image.*
import kotlinx.android.synthetic.main.titlebar.*

class ZoomImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_image)
        iv_back.setOnClickListener {
            finish()
        }
        if (intent != null) {
            if (intent.hasExtra(CONSTANTS.image_link)) {
                val link =
                    Utils.imageUrl + intent.getStringExtra(
                        CONSTANTS.image_link
                    )
                Picasso.get().load(link).error(R.drawable.error_image)
                    .into(myZoomageView, object : Callback {
                        override fun onSuccess() {
                            progess_bar.setVisibility(View.GONE)
                        }

                        override fun onError(e: Exception) {
                            progess_bar.setVisibility(View.GONE)
                        }
                    })
            }
        }
    }
}