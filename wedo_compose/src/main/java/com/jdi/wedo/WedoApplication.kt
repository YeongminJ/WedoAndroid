package com.jdi.wedo

import android.app.Application
import android.content.Context
import com.jdi.wedo.repository.LocalDatabase

class WedoApplication: Application() {

    init{
        instance = this
    }

    lateinit var applicationContext: Context
    companion object {
        lateinit var instance: WedoApplication
    }

    override fun onCreate() {
        super.onCreate()
        applicationContext = this
    }
}