package com.hostd.wedo.gallery

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hostd.wedo.WedoApplication
import com.hostd.wedo.util.Log

@Database(entities = [GalleryData::class], version = 1, exportSchema = false)
abstract class GalleryDatabase: RoomDatabase() {
    abstract fun galleryDao(): GalleryDao

    companion object {
        @Volatile private var instance: GalleryDatabase? = null

        @Synchronized
        fun getInstance(): GalleryDatabase {
            if (instance == null) {
                synchronized(GalleryDatabase::class.java) {
                    instance = Room.databaseBuilder(
                        WedoApplication.instance.appContext,
                        GalleryDatabase::class.java,
                        "wedo-bg.db"
                    )
//                        .addMigrations(migration_1_2, migration_2_3)  //TODO Migration
                        .build()

                    Log.d("database : ${instance!!.isOpen}")
                }
            }
            return instance!!
        }

        fun close() {
            instance?.close()
            instance = null
        }
    }
}