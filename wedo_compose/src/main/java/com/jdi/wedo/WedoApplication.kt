package com.jdi.wedo

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jdi.wedo.repository.LocalDatabase

class WedoApplication: Application() {

    init{
        instance = this
    }

    var applicationContext: Context? = null
    var db: LocalDatabase? = null
    companion object {
        lateinit var instance: WedoApplication
    }

    override fun onCreate() {
        super.onCreate()
        applicationContext = this
        db = LocalDatabase.getInstance(this)
    }
}