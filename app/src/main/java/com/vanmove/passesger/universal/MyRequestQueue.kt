package com.vanmove.passesger.universal

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.vanmove.passesger.R
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage
import com.vanmove.passesger.utils.ShowProgressDialog

class MyRequestQueue private constructor(private val context: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context)
        }
        return requestQueue
    }

    fun <T> addRequest(request: Request<T>?) {
        if (NetworkConnectivity.isInternetConnectivityAvailable(context)) {
            requestQueue!!.add(request)
        } else {
            ShowProgressDialog.closeDialog()
            showAlertMessage(
                context,
                context.getString(R.string.no_internet)
            )
        }


    }
    companion object {
        var request_instance: MyRequestQueue? = null

        @Synchronized
        fun getRequestInstance(context: Context?): MyRequestQueue? {
            request_instance = MyRequestQueue(context!!)

            return request_instance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}