package com.jdi.wedo

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jdi.wedo.repository.LocalDatabase

class WedoApplication: Application() {
    companion object {
        var applicationContext: Context? = null
        var db: LocalDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        WedoApplication.applicationContext = this
        db = LocalDatabase.getInstance(this)
    }
}