package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.TemplateEntity
import com.example.data.local.entities.ClipEntity
import com.example.data.local.entities.TrackType
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: VideoProjectRepository
) : ViewModel() {

    val recentProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val templates: StateFlow<List<TemplateEntity>> = repository.allTemplates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.populateDefaultTemplatesIfEmpty()
        }
    }

    fun createProject(
        title: String,
        aspectRatio: String = "9:16",
        mediaItems: List<com.example.ui.screens.import.ImportMediaItem> = emptyList(),
        onCreated: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val projectId = repository.createNewProject(title = title, aspectRatio = aspectRatio)
            if (mediaItems.isNotEmpty()) {
                // Get the default sample clip and delete it
                try {
                    val defaultClips = repository.getClipsForProject(projectId).first()
                    for (clip in defaultClips) {
                        repository.deleteClip(clip.id)
                    }
                } catch (e: Exception) {
                    // Ignore flow exceptions
                }
                var currentStartTime = 0L
                for (item in mediaItems) {
                    val isPhoto = item.uri.endsWith(".png") || item.uri.endsWith(".jpg") || item.uri.endsWith(".jpeg") || item.title.lowercase().contains("photo") || item.title.lowercase().contains("image") || item.uri.contains("image")
                    val duration = if (isPhoto) 4000L else 5000L

                    val clip = ClipEntity(
                        projectId = projectId,
                        trackType = TrackType.VIDEO,
                        trackIndex = 0,
                        mediaUri = item.uri,
                        title = item.title,
                        startTimeMs = currentStartTime,
                        endTimeMs = currentStartTime + duration,
                        sourceTrimStartMs = 0,
                        sourceTrimEndMs = duration
                    )
                    repository.addClip(clip)
                    currentStartTime += duration
                }
            }
            onCreated(projectId)
        }
    }

    fun deleteProject(projectId: Long) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
        }
    }

    class Factory(private val repository: VideoProjectRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
