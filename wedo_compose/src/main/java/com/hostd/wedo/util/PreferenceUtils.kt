package com.hostd.wedo.util

import androidx.preference.PreferenceManager
import com.hostd.wedo.WedoApplication
import com.hostd.wedo.data.Constants

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
        WedoApplication.instance.appContext.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defaultValue) ?: defaultValue
        }

        return defaultValue
    }

    fun set(key: String, str: String) {
        WedoApplication.instance.appContext.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, str).apply()
        }
    }

    fun hasKey(key: String): Boolean {
        WedoApplication.instance.appContext.let { context->
            return PreferenceManager.getDefaultSharedPreferences(context).contains(key)
        }
        return false
    }
}