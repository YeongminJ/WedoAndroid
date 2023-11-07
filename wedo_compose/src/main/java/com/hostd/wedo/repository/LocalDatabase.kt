/*
package com.hostd.wedo.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hostd.wedo.data.Constants.DATABASE_NAME
import com.hostd.wedo.data.Wedo

//@Database(entities = [Wedo::class], version = 1)
//abstract class LocalDatabase: RoomDatabase() {
//    abstract fun wedoDao(): LocalWedoDao
//
//    companion object {
//        // For Singleton instantiation
//        @Volatile private var instance: LocalDatabase? = null
//
//        fun getInstance(context: Context): LocalDatabase {
//            return instance ?: synchronized(this) {
//                instance ?: buildDatabase(context).also { instance = it }
//            }
//        }
//
//        private fun buildDatabase(context: Context): LocalDatabase {
//            return Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE_NAME)
//                .addCallback(
//                    object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
////                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
////                            .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
////                            .build()
////                        WorkManager.getInstance(context).enqueue(request)
//                        }
//                    }
//                )
//                .build()
//        }
//    }
//}*/
