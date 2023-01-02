package com.vanmove.passesger.utils

import android.content.Context
import com.vanmove.passesger.universal.NetworkConnectivity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class NoConnectivityException(context: Context) : IOException() {
    override val message: String = context.getString(com.vanmove.passesger.R.string.no_internet)
}


class NetworkConnectionInterceptor(context: Context) : Interceptor {
    private val mContext: Context

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (!NetworkConnectivity.isInternetConnectivityAvailable(mContext)) {
            throw NoConnectivityException(mContext)
        }

        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    init {
        mContext = context
    }
}