package com.jdi.wedo

import android.app.Application
import android.content.Context

class WedoApplication: Application() {
    companion object {
        var applicationContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        WedoApplication.applicationContext = this
    }
}