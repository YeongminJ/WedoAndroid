package com.jdi.wedo.util

import androidx.preference.PreferenceManager
import com.jdi.wedo.WedoApplication
import com.jdi.wedo.data.Constants
import com.jdi.wedo.data.Wedo

object PreferenceUtils {
    fun getDefaultUid(): String {
        if (hasKey(Constants.DEFAULT_UID)) {
            return getString(Constants.DEFAULT_UID, Utils.generateRandomAlphanumericString(16))
        }
        else {
            Utils.generateRandomAlphanumericString(16).apply {
                setString(Constants.DEFAULT_UID, this)
                return this
            }

        }
    }
    fun getString(key: String, defaultValue: String = ""): String {
        WedoApplication.getContext().let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
        }

        return defaultValue
    }

    fun setString(key: String, str: String) {
        WedoApplication.applicationContext?.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, str).apply()
        }
    }

    fun hasKey(key: String): Boolean {
        WedoApplication.applicationContext?.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).contains(key)
        }
        return false
    }
}