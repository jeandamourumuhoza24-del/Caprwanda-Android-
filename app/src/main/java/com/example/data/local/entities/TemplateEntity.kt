package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String, // Vlogs, Beats, Culture, Travel, Reels
    val description: String,
    val durationText: String,
    val aspectRatio: String,
    val filterName: String,
    val musicTrackName: String,
    val bgGradientHex1: String,
    val bgGradientHex2: String,
    val usesCount: Int = 1200
)
