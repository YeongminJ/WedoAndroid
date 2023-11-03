package com.jdi.wedo.util

import androidx.preference.PreferenceManager
import com.jdi.wedo.WedoApplication
import com.jdi.wedo.data.Constants

object PreferenceUtils {
    fun getDefaultUid(): String {
        if (hasKey(Constants.DEFAULT_UID)) {
            return get(Constants.DEFAULT_UID, Utils.generateRandomAlphanumericString(8))
        }
        else {
            Utils.generateRandomAlphanumericString(8).apply {
                set(Constants.DEFAULT_UID, this)
                return this
            }

        }
    }
    fun get(key: String, defaultValue: String = ""): String {
        WedoApplication.instance.applicationContext.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
        }

        return defaultValue
    }

    fun set(key: String, str: String) {
        WedoApplication.instance.applicationContext.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, str).apply()
        }
    }

    fun hasKey(key: String): Boolean {
        WedoApplication.instance.applicationContext.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).contains(key)
        }
        return false
    }
}