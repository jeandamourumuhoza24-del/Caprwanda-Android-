package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.ClipDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.TemplateDao
import com.example.data.local.entities.ClipEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.TemplateEntity

@Database(
    entities = [ProjectEntity::class, ClipEntity::class, TemplateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CapRwandaDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun clipDao(): ClipDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var INSTANCE: CapRwandaDatabase? = null

        fun getDatabase(context: Context): CapRwandaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CapRwandaDatabase::class.java,
                    "caprwanda_video_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
