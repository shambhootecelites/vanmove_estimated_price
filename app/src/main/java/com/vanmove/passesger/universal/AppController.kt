package com.vanmove.passesger.universal

import android.app.Application
import android.content.Context
import io.paperdb.Paper


class AppController : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        Paper.init(this)
    }

    companion object {
        var instance: AppController? = null
        fun getAppContext(): Context {
            return instance as Context
        }
    }
}