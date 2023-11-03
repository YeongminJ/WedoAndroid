package com.jdi.wedo.data

import com.jdi.wedo.util.PreferenceUtils
import com.jdi.wedo.util.Utils


data class WedoGroup(
    var goupId: String,
    var member: List<String> = emptyList(),
    var wedos: List<Wedo> = emptyList(),
    var groupname: String = "",
) {
    companion object {
        fun getGeneratorGroup(): String = Utils.generateRandomAlphanumericString(8).apply {
            return this
        }
    }
}

data class Wedo(
    var todo: String,
    // uid + - + index?
    var id: String,
    var createDate: Long = System.currentTimeMillis()
)

data class User(
    var uid: String,
    var email: String = "",
    var groups: List<String>
)