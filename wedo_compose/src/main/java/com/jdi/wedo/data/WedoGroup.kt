package com.jdi.wedo.data

data class WedoGroup(
    var member: List<String>,
    var wedos: List<Wedo>,
    var groupname: String,
)

data class Wedo(
    var todo: String
)
