package com.example.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.ClipEntity
import com.example.data.local.entities.ProjectEntity
import com.example.data.local.entities.TrackType
import com.example.data.repository.VideoProjectRepository
import com.example.domain.engine.AiFeaturesEngine
import com.example.domain.engine.VideoProcessingEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class BottomSheetTool {
    NONE,
    EDIT_TOOLS,
    FILTERS_EFFECTS,
    TRANSITIONS,
    TEXT_TITLES,
    AUDIO_STUDIO,
    STICKERS,
    AI_TOOLS
}

class TimelineViewModel(
    private val repository: VideoProjectRepository,
    private val projectId: Long
) : ViewModel() {

    val project: StateFlow<ProjectEntity?> = repository.getProjectFlow(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val clips: StateFlow<List<ClipEntity>> = repository.getClipsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    private val _selectedClipId = MutableStateFlow<Long?>(null)
    val selectedClipId: StateFlow<Long?> = _selectedClipId.asStateFlow()

    private val _activeSheetTool = MutableStateFlow(BottomSheetTool.NONE)
    val activeSheetTool: StateFlow<BottomSheetTool> = _activeSheetTool.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5x to 3.0x
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow("")
    val aiStatusMessage: StateFlow<String> = _aiStatusMessage.asStateFlow()

    private var playbackJob: Job? = null

    init {
        viewModelScope.launch {
            clips.collect { list ->
                if (list.isNotEmpty() && _selectedClipId.value == null) {
                    _selectedClipId.value = list.first().id
                }
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    private fun play() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val totalDuration = calculateTotalDurationMs()
            while (_isPlaying.value) {
                delay(50)
                var nextPos = _currentPositionMs.value + 50
                if (nextPos >= totalDuration) {
                    nextPos = 0L
                    _isPlaying.value = false
                }
                _currentPositionMs.value = nextPos
            }
        }
    }

    private fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(positionMs: Long) {
        val totalDuration = calculateTotalDurationMs()
        _currentPositionMs.value = positionMs.coerceIn(0L, totalDuration)
    }

    fun setTimelineZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.0f)
    }

    fun selectClip(clipId: Long) {
        _selectedClipId.value = clipId
    }

    fun openSheetTool(tool: BottomSheetTool) {
        _activeSheetTool.value = tool
    }

    fun closeSheetTool() {
        _activeSheetTool.value = BottomSheetTool.NONE
    }

    // Editing tools
    fun updateClipSpeed(clip: ClipEntity, speed: Float) {
        viewModelScope.launch {
            repository.updateClip(clip.copy(speed = speed))
        }
    }

    fun toggleReverseClip(clip: ClipEntity) {
        viewModelScope.launch {
            repository.updateClip(clip.copy(isReversed = !clip.isReversed))
        }
    }

    fun rotateClip(clip: ClipEntity) {
        val newDegrees = (clip.rotationDegrees + 90) % 360
        viewModelScope.launch {
            repository.updateClip(clip.copy(rotationDegrees = newDegrees))
        }
    }

    fun flipClipHorizontal(clip: ClipEntity) {
        viewModelScope.launch {
            repository.updateClip(clip.copy(isFlippedHorizontal = !clip.isFlippedHorizontal))
        }
    }

    fun duplicateSelectedClip() {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            val currentClips = clips.value
            val target = currentClips.find { it.id == clipId } ?: return@launch
            repository.duplicateClip(target)
        }
    }

    fun splitClipAtCurrentTime() {
        val clipId = _selectedClipId.value ?: return
        val currentMs = _currentPositionMs.value
        viewModelScope.launch {
            val target = clips.value.find { it.id == clipId } ?: return@launch
            repository.splitClipAtTime(target, currentMs)
        }
    }

    fun deleteSelectedClip() {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            repository.deleteClip(clipId)
            _selectedClipId.value = clips.value.firstOrNull { it.id != clipId }?.id
        }
    }

    // Effects & Filters
    fun applyFilterToClip(filterName: String) {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            val target = clips.value.find { it.id == clipId } ?: return@launch
            repository.updateClip(target.copy(filterName = filterName))
        }
    }

    fun updateColorAdjustments(brightness: Float, contrast: Float, saturation: Float) {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            val target = clips.value.find { it.id == clipId } ?: return@launch
            repository.updateClip(target.copy(brightness = brightness, contrast = contrast, saturation = saturation))
        }
    }

    // Transitions
    fun applyTransition(transitionIn: String) {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            val target = clips.value.find { it.id == clipId } ?: return@launch
            repository.updateClip(target.copy(transitionIn = transitionIn))
        }
    }

    // Text & Subtitles
    fun addTextOverlay(text: String, font: String, colorHex: String, isAnimated: Boolean) {
        viewModelScope.launch {
            val startTime = _currentPositionMs.value
            val textClip = ClipEntity(
                projectId = projectId,
                trackType = TrackType.TEXT,
                trackIndex = 3,
                mediaUri = "text_overlay",
                title = text,
                textContent = text,
                textStyleFont = font,
                textColorHex = colorHex,
                isTextAnimated = isAnimated,
                startTimeMs = startTime,
                endTimeMs = startTime + 4000
            )
            repository.addClip(textClip)
        }
    }

    // Audio & Voice
    fun addMusicTrack(musicName: String) {
        viewModelScope.launch {
            val audioClip = ClipEntity(
                projectId = projectId,
                trackType = TrackType.AUDIO,
                trackIndex = 2,
                mediaUri = "audio_track",
                title = musicName,
                startTimeMs = 0,
                endTimeMs = calculateTotalDurationMs()
            )
            repository.addClip(audioClip)
        }
    }

    fun addVoiceRecording(recordingTitle: String) {
        viewModelScope.launch {
            val startTime = _currentPositionMs.value
            val voiceClip = ClipEntity(
                projectId = projectId,
                trackType = TrackType.AUDIO,
                trackIndex = 2,
                mediaUri = "mic_recording.wav",
                title = recordingTitle,
                isVoiceOver = true,
                startTimeMs = startTime,
                endTimeMs = startTime + 5000
            )
            repository.addClip(voiceClip)
        }
    }

    // Stickers
    fun addSticker(symbol: String) {
        viewModelScope.launch {
            val startTime = _currentPositionMs.value
            val stickerClip = ClipEntity(
                projectId = projectId,
                trackType = TrackType.STICKER,
                trackIndex = 4,
                mediaUri = "sticker",
                title = "Sticker $symbol",
                stickerSymbol = symbol,
                startTimeMs = startTime,
                endTimeMs = startTime + 3000
            )
            repository.addClip(stickerClip)
        }
    }

    // AI Features
    fun runAutoCaptions(lang: AiFeaturesEngine.SubtitleLanguage) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "AI analyzing audio waveform & generating subtitles..."
            
            val totalDuration = calculateTotalDurationMs()
            val captions = AiFeaturesEngine.generateAutoCaptions(lang, totalDuration)
            
            captions.forEach { cap ->
                val captionClip = ClipEntity(
                    projectId = projectId,
                    trackType = TrackType.TEXT,
                    trackIndex = 3,
                    mediaUri = "auto_caption",
                    title = "Caption",
                    textContent = cap.text,
                    textColorHex = "#FACC15",
                    textBgColorHex = "#CC000000",
                    isAiAutoCaption = true,
                    startTimeMs = cap.startTimeMs,
                    endTimeMs = cap.endTimeMs
                )
                repository.addClip(captionClip)
            }

            _isAiLoading.value = false
            _aiStatusMessage.value = "Generated ${captions.size} auto captions!"
        }
    }

    fun toggleAiBackgroundRemoval() {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            val target = clips.value.find { it.id == clipId } ?: return@launch
            _isAiLoading.value = true
            _aiStatusMessage.value = "Segmenting video subject from background..."
            delay(1000)
            
            repository.updateClip(target.copy(isAiBgRemoved = !target.isAiBgRemoved))
            _isAiLoading.value = false
            _aiStatusMessage.value = if (!target.isAiBgRemoved) "AI Subject Segmentation Applied!" else "Background restored"
        }
    }

    fun toggleChromaKey(colorHex: String) {
        val clipId = _selectedClipId.value ?: return
        viewModelScope.launch {
            val target = clips.value.find { it.id == clipId } ?: return@launch
            val newState = !target.isChromaKeyEnabled
            repository.updateClip(target.copy(isChromaKeyEnabled = newState, chromaKeyColorHex = colorHex))
        }
    }

    fun addVideoClipFromImport(mediaUri: String, title: String) {
        viewModelScope.launch {
            val totalDur = calculateTotalDurationMs()
            val newClip = ClipEntity(
                projectId = projectId,
                trackType = TrackType.VIDEO,
                trackIndex = 0,
                mediaUri = mediaUri,
                title = title,
                startTimeMs = totalDur,
                endTimeMs = totalDur + 5000,
                sourceTrimStartMs = 0,
                sourceTrimEndMs = 5000
            )
            repository.addClip(newClip)
        }
    }

    private fun calculateTotalDurationMs(): Long {
        val list = clips.value
        if (list.isEmpty()) return 15000L
        return list.maxOfOrNull { it.endTimeMs }?.coerceAtLeast(10000L) ?: 15000L
    }

    class Factory(
        private val repository: VideoProjectRepository,
        private val projectId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TimelineViewModel(repository, projectId) as T
        }
    }
}
