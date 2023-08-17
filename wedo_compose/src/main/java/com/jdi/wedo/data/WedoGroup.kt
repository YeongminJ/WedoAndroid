package com.jdi.wedo.data

data class WedoGroup(
    var member: List<String> = emptyList(),
    var wedos: List<Wedo> = emptyList(),
    var groupname: String = "",
)

data class Wedo(
    var todo: String = ""
)
