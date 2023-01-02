package com.vanmove.passesger.universal

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by Linez-001 on 7/3/2017.
 */
object NetworkConnectivity {
    fun isInternetConnectivityAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}