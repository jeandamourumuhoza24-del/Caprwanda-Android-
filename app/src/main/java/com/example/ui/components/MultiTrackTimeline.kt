package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ClipEntity
import com.example.data.local.entities.TrackType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepPurpleContainer
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RoseContainer
import com.example.ui.theme.RoseText
import com.example.ui.theme.SlateDarkBorder
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface

@Composable
fun MultiTrackTimeline(
    clips: List<ClipEntity>,
    currentPositionMs: Long,
    totalDurationMs: Long,
    selectedClipId: Long?,
    zoomLevel: Float,
    onClipSelect: (Long) -> Unit,
    onSeekTo: (Long) -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val videoClips = clips.filter { it.trackType == TrackType.VIDEO }
    val audioClips = clips.filter { it.trackType == TrackType.AUDIO }
    val textClips = clips.filter { it.trackType == TrackType.TEXT }
    val stickerClips = clips.filter { it.trackType == TrackType.STICKER }

    val selectedClip = clips.find { it.id == selectedClipId }

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateDarkBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("multi_track_timeline")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // High Density Timeline Context Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatMs(currentPositionMs)} / ${formatMs(totalDurationMs)}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (selectedClip != null) "CLIP_${selectedClip.id.toString().padStart(2, '0')}" else "TIMELINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleAccent
                    )
                    Text(
                        text = "ZOOM: ${String.format("%.1f", zoomLevel)}x",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onZoomChange((zoomLevel - 0.25f).coerceAtLeast(0.5f)) },
                            modifier = Modifier
                                .size(26.dp)
                                .testTag("zoom_out_button")
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = PurpleAccent, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = { onZoomChange((zoomLevel + 0.25f).coerceAtMost(3.0f)) },
                            modifier = Modifier
                                .size(26.dp)
                                .testTag("zoom_in_button")
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = PurpleAccent, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // High Density Time Ruler Line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateDarkSurface, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("00:00", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("00:15", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("00:30", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("00:45", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("01:00", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Time Scrubber Seek Bar
            Slider(
                value = currentPositionMs.toFloat(),
                onValueChange = { onSeekTo(it.toLong()) },
                valueRange = 0f..totalDurationMs.coerceAtLeast(1000L).toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = PurpleAccent,
                    activeTrackColor = PurpleAccent,
                    inactiveTrackColor = SlateDarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .testTag("timeline_time_slider")
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Track Rows Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaMs = (dragAmount * 50 / zoomLevel).toLong()
                            onSeekTo((currentPositionMs + deltaMs).coerceIn(0L, totalDurationMs))
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Track 1: Text Track (High Density Rose Accent)
                TrackRow(
                    trackTitle = "Text",
                    icon = Icons.Default.TextFields,
                    trackColor = RoseContainer,
                    textColor = RoseText,
                    clips = textClips,
                    selectedClipId = selectedClipId,
                    zoomLevel = zoomLevel,
                    onClipSelect = onClipSelect
                )

                // Track 2: Video Track (High Density M3 Purple Accent)
                TrackRow(
                    trackTitle = "Video",
                    icon = Icons.Default.Movie,
                    trackColor = PurpleAccent,
                    textColor = DeepPurpleContainer,
                    clips = videoClips,
                    selectedClipId = selectedClipId,
                    zoomLevel = zoomLevel,
                    onClipSelect = onClipSelect
                )

                // Track 3: Audio Track (High Density Deep Purple Container)
                TrackRow(
                    trackTitle = "Audio",
                    icon = Icons.Default.Audiotrack,
                    trackColor = DeepPurpleContainer,
                    textColor = PurpleAccent,
                    clips = audioClips,
                    selectedClipId = selectedClipId,
                    zoomLevel = zoomLevel,
                    onClipSelect = onClipSelect
                )

                // Track 4: Sticker Track (High Density Cyan)
                TrackRow(
                    trackTitle = "Sticker",
                    icon = Icons.Default.EmojiEmotions,
                    trackColor = CyanAccent,
                    textColor = Color.Black,
                    clips = stickerClips,
                    selectedClipId = selectedClipId,
                    zoomLevel = zoomLevel,
                    onClipSelect = onClipSelect
                )
            }
        }
    }
}

@Composable
private fun TrackRow(
    trackTitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trackColor: Color,
    textColor: Color,
    clips: List<ClipEntity>,
    selectedClipId: Long?,
    zoomLevel: Float,
    onClipSelect: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(SlateDarkSurface, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.width(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = trackTitle,
                tint = trackColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = trackTitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(clips, key = { it.id }) { clip ->
                val durationSec = (clip.endTimeMs - clip.startTimeMs) / 1000f
                val clipWidthDp = (durationSec * 35f * zoomLevel).coerceAtLeast(60f).dp
                val isSelected = clip.id == selectedClipId

                Box(
                    modifier = Modifier
                        .width(clipWidthDp)
                        .fillMaxHeight(0.85f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) trackColor else trackColor.copy(alpha = 0.5f))
                        .border(
                            width = if (isSelected) 2.dp else 0.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onClipSelect(clip.id) }
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (clip.textContent.isNotEmpty()) clip.textContent else clip.title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
