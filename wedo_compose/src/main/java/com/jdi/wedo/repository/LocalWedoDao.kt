package com.jdi.wedo.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jdi.wedo.data.Wedo

@Dao
interface LocalWedoDao {
    @Query("SELECT * FROM Wedo")
    fun getAll(): List<Wedo>

    @Insert
    fun insertWedo(wedo: Wedo)

    @Delete
    fun deleteWedo(wedo: Wedo)
}