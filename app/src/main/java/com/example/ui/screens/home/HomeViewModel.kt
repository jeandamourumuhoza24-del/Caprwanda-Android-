package com.example.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.TemplateEntity
import com.example.data.repository.VideoProjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
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

    fun createProject(title: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val projectId = repository.createNewProject(title = title)
            onCreated(projectId)
        }
    }

    fun createProjectWithClips(
        title: String,
        aspectRatio: String,
        mediaItems: List<Triple<String, String, Boolean>>,
        onCreated: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val projectId = repository.createNewProjectWithClips(title, aspectRatio, mediaItems)
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
