package com.jdi.wedo.repository

import com.jdi.wedo.data.Wedo

class LocalRepositoryImpl(val database: LocalDatabase): LocalRepository {

    override fun getAll(): List<Wedo> {
        return database.wedoDao().getAll()
    }

    override fun insertWedo(wedo: Wedo) {
        database.wedoDao().insertWedo(wedo)
    }

    override fun deleteWedo(wedo: Wedo) {
        database.wedoDao().deleteWedo(wedo)
    }
}