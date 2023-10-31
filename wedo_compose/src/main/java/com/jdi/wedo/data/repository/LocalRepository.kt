package com.jdi.wedo.data.repository

import androidx.room.Room
import com.jdi.wedo.WedoApplication
import com.jdi.wedo.data.Wedo

interface LocalRepository {
    fun getAll(): List<Wedo>
    fun insertWedo(wedo: Wedo)
    fun deleteWedo(wedo: Wedo)
}