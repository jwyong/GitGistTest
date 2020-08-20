package com.example.newswavtest.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.newswavtest.db.dao.GistDetailDao
import com.example.newswavtest.db.dao.GistListDao
import com.example.newswavtest.db.dao.OfflineSyncDao
import com.example.newswavtest.db.entity.GistDetailEntity
import com.example.newswavtest.db.entity.GistListEntity
import com.example.newswavtest.db.entity.OfflineSyncEntity
import com.example.newswavtest.utils.DATABASE_NAME
import com.example.newswavtest.utils.DATABASE_VERSION

@Database(entities = [GistListEntity::class, GistDetailEntity::class, OfflineSyncEntity::class], version = DATABASE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gistListDao(): GistListDao
    abstract fun gistDetailDao(): GistDetailDao
    abstract fun offlineSyncDao(): OfflineSyncDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}