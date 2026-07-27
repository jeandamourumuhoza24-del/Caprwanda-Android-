package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val durationMs: Long = 15000,
    val thumbnailUri: String? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val resolution: String = "1080p", // 720p, 1080p, 4K
    val fps: Int = 30,
    val aspectRatio: String = "9:16", // 9:16, 16:9, 1:1, 4:5
    val isExported: Boolean = false
)
