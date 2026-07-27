package com.example

import android.app.Application
import com.example.data.local.database.CapRwandaDatabase
import com.example.data.repository.VideoProjectRepository

class CapRwandaApplication : Application() {

    lateinit var repository: VideoProjectRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = CapRwandaDatabase.getDatabase(this)
        repository = VideoProjectRepository(
            projectDao = database.projectDao(),
            clipDao = database.clipDao(),
            templateDao = database.templateDao()
        )
    }
}
