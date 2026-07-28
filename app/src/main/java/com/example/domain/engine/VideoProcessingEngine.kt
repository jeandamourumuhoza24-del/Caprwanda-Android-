package com.example.domain.engine

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class FilterPreset(
    val id: String,
    val name: String,
    val colorOverlay: Color,
    val brightnessBoost: Float = 0f,
    val contrastBoost: Float = 1f,
    val saturationBoost: Float = 1f
)

object VideoProcessingEngine {

    val availableFilters = listOf(
        FilterPreset("norm", "Normal", Color.Transparent, 0f, 1f, 1f),
        FilterPreset("cinematic", "Cinematic", Color(0x330284C7), -0.05f, 1.2f, 0.9f),
        FilterPreset("rwandan_dawn", "Rwandan Dawn", Color(0x33F59E0B), 0.1f, 1.1f, 1.2f),
        FilterPreset("vibrant", "Vibrant", Color(0x2210B981), 0.05f, 1.15f, 1.4f),
        FilterPreset("vintage", "Vintage Sepia", Color(0x4478350F), -0.1f, 0.95f, 0.7f),
        FilterPreset("bw", "Noir B&W", Color(0x66000000), 0f, 1.3f, 0f),
        FilterPreset("warm_sunset", "Warm Sunset", Color(0x33EA580C), 0.08f, 1.1f, 1.3f)
    )

    data class ExportProgressState(
        val isExporting: Boolean = false,
        val progressPercent: Float = 0f,
        val currentFrame: Long = 0,
        val totalFrames: Long = 450,
        val timeRemainingSeconds: Int = 15,
        val exportedFileUri: String? = null,
        val isCompleted: Boolean = false,
        val errorMsg: String? = null
    )

    fun startVideoExport(
        projectId: Long,
        title: String,
        resolution: String = "1080p",
        fps: Int = 30,
        totalDurationMs: Long = 15000
    ): Flow<ExportProgressState> = flow {
        val totalFrames = ((totalDurationMs / 1000f) * fps).toLong()
        emit(ExportProgressState(isExporting = true, progressPercent = 0f, currentFrame = 0, totalFrames = totalFrames, timeRemainingSeconds = 12))

        val steps = 20
        for (i in 1..steps) {
            delay(350) // Simulation step
            val progress = i / steps.toFloat()
            val currentFrame = (progress * totalFrames).toLong()
            val timeRemaining = ((1f - progress) * 12).toInt()

            emit(
                ExportProgressState(
                    isExporting = true,
                    progressPercent = progress,
                    currentFrame = currentFrame,
                    totalFrames = totalFrames,
                    timeRemainingSeconds = timeRemaining
                )
            )
        }

        val outputFilename = "CapRwanda_${title.lowercase().replace(" ", "_")}_$resolution.mp4"
        val path = "/storage/emulated/0/Movies"
        try {
            val dir = java.io.File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = java.io.File(dir, outputFilename)
            file.writeText("Simulated CapRwanda MP4 Video Content at $resolution resolution")
        } catch (e: Exception) {
            // Gracefully ignore since it's sandbox / simulated
        }

        emit(
            ExportProgressState(
                isExporting = false,
                progressPercent = 1.0f,
                currentFrame = totalFrames,
                totalFrames = totalFrames,
                timeRemainingSeconds = 0,
                exportedFileUri = "$path/$outputFilename",
                isCompleted = true
            )
        )
    }
}
