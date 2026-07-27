package com.example.data.repository

import com.example.data.local.dao.ClipDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.dao.TemplateDao
import com.example.data.local.entities.ClipEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.TemplateEntity
import com.example.data.local.entities.TrackType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VideoProjectRepository(
    private val projectDao: ProjectDao,
    private val clipDao: ClipDao,
    private val templateDao: TemplateDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val allTemplates: Flow<List<TemplateEntity>> = templateDao.getAllTemplates()

    suspend fun createNewProject(title: String = "Rwanda Reel", aspectRatio: String = "9:16"): Long {
        val project = ProjectEntity(
            title = title,
            aspectRatio = aspectRatio,
            lastModified = System.currentTimeMillis()
        )
        val projectId = projectDao.insertProject(project)
        
        // Add an initial sample video clip
        val sampleClip = ClipEntity(
            projectId = projectId,
            trackType = TrackType.VIDEO,
            trackIndex = 0,
            mediaUri = "sample_rwanda_landscape.mp4",
            title = "Intro Landscape",
            startTimeMs = 0,
            endTimeMs = 5000,
            sourceTrimStartMs = 0,
            sourceTrimEndMs = 5000,
            filterName = "Rwandan Dawn"
        )
        clipDao.insertClip(sampleClip)
        return projectId
    }

    suspend fun getProject(id: Long): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    fun getProjectFlow(id: Long): Flow<ProjectEntity?> {
        return projectDao.getProjectByIdFlow(id)
    }

    fun getClipsForProject(projectId: Long): Flow<List<ClipEntity>> {
        return clipDao.getClipsForProject(projectId)
    }

    suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project.copy(lastModified = System.currentTimeMillis()))
    }

    suspend fun deleteProject(projectId: Long) {
        clipDao.deleteClipsForProject(projectId)
        projectDao.deleteProjectById(projectId)
    }

    suspend fun addClip(clip: ClipEntity): Long {
        return clipDao.insertClip(clip)
    }

    suspend fun updateClip(clip: ClipEntity) {
        clipDao.updateClip(clip)
    }

    suspend fun deleteClip(clipId: Long) {
        clipDao.deleteClipById(clipId)
    }

    suspend fun duplicateClip(clip: ClipEntity) {
        val duplicated = clip.copy(
            id = 0, // Auto-generate
            startTimeMs = clip.endTimeMs,
            endTimeMs = clip.endTimeMs + (clip.endTimeMs - clip.startTimeMs)
        )
        clipDao.insertClip(duplicated)
    }

    suspend fun splitClipAtTime(clip: ClipEntity, splitTimeMs: Long) {
        if (splitTimeMs <= clip.startTimeMs || splitTimeMs >= clip.endTimeMs) return
        
        val duration1 = splitTimeMs - clip.startTimeMs
        val duration2 = clip.endTimeMs - splitTimeMs
        
        val clip1 = clip.copy(
            endTimeMs = splitTimeMs,
            sourceTrimEndMs = clip.sourceTrimStartMs + duration1
        )
        val clip2 = clip.copy(
            id = 0,
            startTimeMs = splitTimeMs,
            endTimeMs = clip.endTimeMs,
            sourceTrimStartMs = clip.sourceTrimStartMs + duration1,
            sourceTrimEndMs = clip.sourceTrimStartMs + duration1 + duration2
        )
        
        clipDao.updateClip(clip1)
        clipDao.insertClip(clip2)
    }

    suspend fun populateDefaultTemplatesIfEmpty() {
        val currentTemplates = templateDao.getAllTemplates().first()
        if (currentTemplates.isEmpty()) {
            val defaultTemplates = listOf(
                TemplateEntity(
                    id = "tpl_kigali_sunset",
                    title = "Kigali Sunset Vlog",
                    category = "Vlogs",
                    description = "Warm golden hour aesthetic with smooth cinematic beats.",
                    durationText = "0:15",
                    aspectRatio = "9:16",
                    filterName = "Warm Sunset",
                    musicTrackName = "Kigali Sunset Beats",
                    bgGradientHex1 = "#F59E0B",
                    bgGradientHex2 = "#D97706",
                    usesCount = 4200
                ),
                TemplateEntity(
                    id = "tpl_intore_rhythm",
                    title = "Intore Rhythm Beats",
                    category = "Beats",
                    description = "Fast beat-sync transitions for traditional and modern dance clips.",
                    durationText = "0:12",
                    aspectRatio = "9:16",
                    filterName = "Vibrant",
                    musicTrackName = "Intore Drum Afrobeat",
                    bgGradientHex1 = "#06B6D4",
                    bgGradientHex2 = "#0284C7",
                    usesCount = 8900
                ),
                TemplateEntity(
                    id = "tpl_gorilla_trek",
                    title = "Volcanoes Park Trek",
                    category = "Travel",
                    description = "Nature green color grade with ambient forest sounds and title cards.",
                    durationText = "0:30",
                    aspectRatio = "16:9",
                    filterName = "Rwandan Dawn",
                    musicTrackName = "Mountain Ambient Flow",
                    bgGradientHex1 = "#10B981",
                    bgGradientHex2 = "#059669",
                    usesCount = 3100
                ),
                TemplateEntity(
                    id = "tpl_kivu_breeze",
                    title = "Lake Kivu Breeze",
                    category = "Reels",
                    description = "Soft blue LUT filter with auto-caption title style for travel reels.",
                    durationText = "0:18",
                    aspectRatio = "9:16",
                    filterName = "Cinematic",
                    musicTrackName = "Kivu Chill Vibes",
                    bgGradientHex1 = "#3B82F6",
                    bgGradientHex2 = "#1D4ED8",
                    usesCount = 5600
                )
            )
            templateDao.insertTemplates(defaultTemplates)
        }
    }
}
