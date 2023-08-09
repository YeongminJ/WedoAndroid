package com.jdi.wedo.util

import androidx.preference.PreferenceManager
import com.jdi.wedo.WedoApplication

object PreferenceUtils {
    fun getString(key: String, defaultValue: String = ""): String {
        WedoApplication.applicationContext?.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
        }

        return defaultValue
    }
}