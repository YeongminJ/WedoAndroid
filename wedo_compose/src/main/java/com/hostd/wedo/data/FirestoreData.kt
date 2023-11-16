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
    var todo: String = "",
    // uid + - + index? => 불필요해 보임 text 로 구분된다고 봄
//    var id: String,
    var groupId: String = "", //그룹 아이디를 여기에 넣어서 Flow 처리할때, Wedo List 로만 관리하기 편하게
    var starCount: Int = 0,
    var createDate: Long = System.currentTimeMillis()
) {
    constructor(): this("")
}

//유저가 가입된 그룹정보를 가지고 있는 유저정보
data class User(
    var uid: String = "",
    var email: String = "",
    var thumbnail: String = "",
    var groups: List<String> = emptyList()
) {
    constructor(): this("")
}