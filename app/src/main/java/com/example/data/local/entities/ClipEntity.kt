package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TrackType {
    VIDEO,
    AUDIO,
    TEXT,
    STICKER
}

@Entity(tableName = "clips")
data class ClipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val trackType: TrackType = TrackType.VIDEO,
    val trackIndex: Int = 0, // 0 = main video, 1 = overlay, 2 = audio, 3 = subtitle, 4 = sticker
    val mediaUri: String,
    val title: String = "Clip",
    val startTimeMs: Long = 0,
    val endTimeMs: Long = 5000,
    val sourceTrimStartMs: Long = 0,
    val sourceTrimEndMs: Long = 5000,
    
    // Video editing properties
    val speed: Float = 1.0f, // 0.25x to 4.0x
    val isReversed: Boolean = false,
    val rotationDegrees: Int = 0, // 0, 90, 180, 270
    val isFlippedHorizontal: Boolean = false,
    val isFlippedVertical: Boolean = false,
    val cropX: Float = 0f,
    val cropY: Float = 0f,
    val cropWidth: Float = 1f,
    val cropHeight: Float = 1f,
    
    // Effects & Filters
    val filterName: String = "Normal", // Normal, Cinematic, Rwandan Dawn, Vibrant, Vintage, B&W, Warm Sunset
    val lutPreset: String = "None",
    val brightness: Float = 0.0f, // -1.0 to 1.0
    val contrast: Float = 1.0f,   // 0.0 to 2.0
    val saturation: Float = 1.0f, // 0.0 to 2.0
    val blurRadius: Float = 0.0f, // 0 to 20
    val sharpenAmount: Float = 0.0f,
    
    // Transitions
    val transitionIn: String = "None", // None, Fade, Slide, Zoom, Dissolve
    val transitionInDurationMs: Long = 500,
    val transitionOut: String = "None",
    
    // Text / Subtitle properties
    val textContent: String = "",
    val textStyleFont: String = "Default",
    val textColorHex: String = "#FFFFFF",
    val textBgColorHex: String = "#00000000",
    val isTextAnimated: Boolean = false,
    
    // Audio properties
    val volume: Float = 1.0f, // 0.0 to 2.0
    val fadeInMs: Long = 0,
    val fadeOutMs: Long = 0,
    val isVoiceOver: Boolean = false,
    
    // Sticker properties
    val stickerSymbol: String = "", // Emoji or asset name
    
    // AI Features
    val isAiBgRemoved: Boolean = false,
    val isChromaKeyEnabled: Boolean = false,
    val chromaKeyColorHex: String = "#00FF00", // Green screen
    val chromaKeyTolerance: Float = 0.3f,
    val isAiAutoCaption: Boolean = false,
    val objectRemovalMask: String = "" // Serialized points for object removal
)
