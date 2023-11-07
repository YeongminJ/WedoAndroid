package com.hostd.wedo

import android.app.Application
import android.content.Context

class WedoApplication: Application() {

    init{
        instance = this
    }

    lateinit var appContext: Context
    companion object {
        lateinit var instance: WedoApplication
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}