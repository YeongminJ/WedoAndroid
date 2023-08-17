package com.jdi.wedo

import android.app.Application
import android.content.Context

class WedoApplication: Application() {

    init{
        instance = this
    }

    companion object {
        lateinit var instance: WedoApplication
        fun getContext() : Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}