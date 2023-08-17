package com.jdi.wedo.util

import androidx.preference.PreferenceManager
import com.jdi.wedo.WedoApplication

object PreferenceUtils {
    fun getString(key: String, defaultValue: String = ""): String {
        WedoApplication.getContext().let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
        }

        return defaultValue
    }

    fun set(key: String, value: Any) {
        if (value is String) {
            setString(key, value)
        }
    }
    private fun setString(key: String, value: String) {
        WedoApplication.getContext().let { context->
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply()
        }
    }
}