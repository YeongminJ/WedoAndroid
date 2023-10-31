package com.jdi.wedo.data

import androidx.room.Entity

data class WedoGroup(
    var member: List<String>,
    var wedos: List<Wedo>,
    var groupname: String,
)

@Entity
data class Wedo(
    var id: String,     // generate by random string + index
    var todo: String,
    var createDate: Long
)
