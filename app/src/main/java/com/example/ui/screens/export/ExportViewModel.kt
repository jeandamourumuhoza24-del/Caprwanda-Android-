package com.example.ui.screens.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ProjectEntity
import com.example.data.repository.VideoProjectRepository
import com.example.domain.engine.VideoProcessingEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExportViewModel(
    private val repository: VideoProjectRepository,
    private val projectId: Long
) : ViewModel() {

    val project: StateFlow<ProjectEntity?> = repository.getProjectFlow(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedResolution = MutableStateFlow("1080p")
    val selectedResolution: StateFlow<String> = _selectedResolution.asStateFlow()

    private val _selectedFps = MutableStateFlow(30)
    val selectedFps: StateFlow<Int> = _selectedFps.asStateFlow()

    private val _selectedFormat = MutableStateFlow("MP4")
    val selectedFormat: StateFlow<String> = _selectedFormat.asStateFlow()

    private val _exportState = MutableStateFlow(VideoProcessingEngine.ExportProgressState())
    val exportState: StateFlow<VideoProcessingEngine.ExportProgressState> = _exportState.asStateFlow()

    fun setResolution(res: String) {
        _selectedResolution.value = res
    }

    fun setFps(fps: Int) {
        _selectedFps.value = fps
    }

    fun setFormat(format: String) {
        _selectedFormat.value = format
    }

    fun startExporting() {
        val proj = project.value ?: return
        viewModelScope.launch {
            VideoProcessingEngine.startVideoExport(
                projectId = proj.id,
                title = proj.title,
                resolution = _selectedResolution.value,
                fps = _selectedFps.value,
                totalDurationMs = proj.durationMs
            ).collect { state ->
                _exportState.value = state
                if (state.isCompleted) {
                    repository.updateProject(proj.copy(isExported = true, resolution = _selectedResolution.value))
                }
            }
        }
    }

    class Factory(
        private val repository: VideoProjectRepository,
        private val projectId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExportViewModel(repository, projectId) as T
        }
    }
}
