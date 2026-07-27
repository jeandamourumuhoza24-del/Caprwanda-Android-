package com.example.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.engine.AiFeaturesEngine
import com.example.ui.components.CapRwandaTopBar
import com.example.ui.components.MultiTrackTimeline
import com.example.ui.components.VideoPreviewPlayer
import com.example.ui.screens.editor.sheets.*
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepPurpleContainer
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SlateDarkBorder
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineEditorScreen(
    viewModel: TimelineViewModel,
    onNavigateToExport: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val project by viewModel.project.collectAsStateWithLifecycle()
    val clips by viewModel.clips.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentPosMs by viewModel.currentPositionMs.collectAsStateWithLifecycle()
    val selectedClipId by viewModel.selectedClipId.collectAsStateWithLifecycle()
    val activeTool by viewModel.activeSheetTool.collectAsStateWithLifecycle()
    val timelineZoom by viewModel.timelineZoom.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()

    val selectedClip = clips.find { it.id == selectedClipId }
    val totalDurationMs = clips.maxOfOrNull { it.endTimeMs }?.coerceAtLeast(15000L) ?: 15000L

    Scaffold(
        topBar = {
            CapRwandaTopBar(
                title = project?.title ?: "Kigali_Travel_Vlog",
                subtitle = "1080p • 24fps • 01:24",
                onBackClick = onBackClick,
                actions = {
                    Button(
                        onClick = {
                            val projId = project?.id
                            if (projId != null) {
                                onNavigateToExport(projId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("export_button")
                    ) {
                        Text(
                            "EXPORT",
                            color = DeepPurpleContainer,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            )
        },
        bottomBar = {
            // High Density Bottom Toolbar for Tools Selection
            Surface(
                color = SlateDarkCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = SlateDarkBorder)
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        QuickToolChip(
                            title = "Split",
                            icon = Icons.Default.ContentCut,
                            color = PurpleAccent,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.EDIT_TOOLS) }
                        )
                    }
                    item {
                        QuickToolChip(
                            title = "Speed",
                            icon = Icons.Default.Speed,
                            color = PurpleAccent,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.EDIT_TOOLS) }
                        )
                    }
                    item {
                        QuickToolChip(
                            title = "AI Tools",
                            icon = Icons.Default.AutoAwesome,
                            color = CyanAccent,
                            isHighlighted = true,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.AI_TOOLS) }
                        )
                    }
                    item {
                        QuickToolChip(
                            title = "Filters",
                            icon = Icons.Default.AutoFixHigh,
                            color = PurpleAccent,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.FILTERS_EFFECTS) }
                        )
                    }
                    item {
                        QuickToolChip(
                            title = "Text",
                            icon = Icons.Default.Title,
                            color = PurpleAccent,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.TEXT_TITLES) }
                        )
                    }
                    item {
                        QuickToolChip(
                            title = "Audio",
                            icon = Icons.Default.Mic,
                            color = PurpleAccent,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.AUDIO_STUDIO) }
                        )
                    }
                    item {
                        QuickToolChip(
                            title = "Stickers",
                            icon = Icons.Default.EmojiEmotions,
                            color = PurpleAccent,
                            onClick = { viewModel.openSheetTool(BottomSheetTool.STICKERS) }
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Video Preview Canvas Player
                VideoPreviewPlayer(
                    aspectRatio = project?.aspectRatio ?: "9:16",
                    isPlaying = isPlaying,
                    currentPositionMs = currentPosMs,
                    totalDurationMs = totalDurationMs,
                    clips = clips,
                    onTogglePlayPause = { viewModel.togglePlayPause() },
                    modifier = Modifier.weight(1.2f)
                )

                // Multi-Track Timeline Controller
                MultiTrackTimeline(
                    clips = clips,
                    currentPositionMs = currentPosMs,
                    totalDurationMs = totalDurationMs,
                    selectedClipId = selectedClipId,
                    zoomLevel = timelineZoom,
                    onClipSelect = { viewModel.selectClip(it) },
                    onSeekTo = { viewModel.seekTo(it) },
                    onZoomChange = { viewModel.setTimelineZoom(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Bottom Sheets for active tool overlay
            if (activeTool != BottomSheetTool.NONE) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { viewModel.closeSheetTool() },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(modifier = Modifier.clickable(enabled = false) {}) {
                        when (activeTool) {
                            BottomSheetTool.EDIT_TOOLS -> {
                                EditingToolsSheet(
                                    clip = selectedClip,
                                    onSplit = { viewModel.splitClipAtCurrentTime() },
                                    onDuplicate = { viewModel.duplicateSelectedClip() },
                                    onDelete = { viewModel.deleteSelectedClip() },
                                    onRotate = { selectedClip?.let { viewModel.rotateClip(it) } },
                                    onFlip = { selectedClip?.let { viewModel.flipClipHorizontal(it) } },
                                    onSpeedChange = { speed -> selectedClip?.let { viewModel.updateClipSpeed(it, speed) } },
                                    onReverseToggle = { selectedClip?.let { viewModel.toggleReverseClip(it) } },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            BottomSheetTool.FILTERS_EFFECTS -> {
                                EffectsFilterSheet(
                                    clip = selectedClip,
                                    onApplyFilter = { viewModel.applyFilterToClip(it) },
                                    onColorAdjustments = { b, c, s -> viewModel.updateColorAdjustments(b, c, s) },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            BottomSheetTool.TRANSITIONS -> {
                                TransitionsSheet(
                                    clip = selectedClip,
                                    onApplyTransition = { viewModel.applyTransition(it) },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            BottomSheetTool.TEXT_TITLES -> {
                                TextEditorSheet(
                                    onAddText = { txt, font, color, anim -> viewModel.addTextOverlay(txt, font, color, anim) },
                                    onOpenAiCaptions = { viewModel.openSheetTool(BottomSheetTool.AI_TOOLS) },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            BottomSheetTool.AUDIO_STUDIO -> {
                                AudioStudioSheet(
                                    onAddMusicTrack = { viewModel.addMusicTrack(it) },
                                    onAddVoiceRecording = { viewModel.addVoiceRecording(it) },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            BottomSheetTool.STICKERS -> {
                                StickersSheet(
                                    onSelectSticker = { viewModel.addSticker(it) },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            BottomSheetTool.AI_TOOLS -> {
                                AiToolsSheet(
                                    isAiLoading = isAiLoading,
                                    aiStatusMessage = aiMessage,
                                    onRunAutoCaptions = { viewModel.runAutoCaptions(it) },
                                    onToggleBgRemoval = { viewModel.toggleAiBackgroundRemoval() },
                                    onToggleChromaKey = { viewModel.toggleChromaKey(it) },
                                    onClose = { viewModel.closeSheetTool() }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickToolChip(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isHighlighted) SlateDarkBorder else color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp),
        border = if (isHighlighted) androidx.compose.foundation.BorderStroke(1.dp, color) else null
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
        }
    }
}
