package com.hostd.wedo.data

import com.hostd.wedo.util.Utils


data class WedoGroup(
    var groupId: String = "",
    var member: List<String> = emptyList(),
    var wedos: List<Wedo> = emptyList(),
    var groupname: String = "",
) {
    companion object {
        fun getGeneratorGroup(): String = Utils.generateRandomAlphanumericString(8).apply {
            return this
        }
    }

    constructor() : this("")
}

data class Wedo(
    var todo: String,
    // uid + - + index? => 불필요해 보임 text 로 구분된다고 봄
//    var id: String,
    var createDate: Long = System.currentTimeMillis()
)

data class User(
    var uid: String = "",
    var email: String = "",
    var groups: List<String> = emptyList()
) {
    constructor(): this("")
}