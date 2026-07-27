package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ClipEntity
import com.example.data.local.entities.TrackType
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RwandaGold
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface
import java.io.InputStream
import java.lang.Exception

@Composable
fun VideoPreviewPlayer(
    aspectRatio: String,
    isPlaying: Boolean,
    currentPositionMs: Long,
    totalDurationMs: Long,
    clips: List<ClipEntity>,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val aspectValue = when (aspectRatio) {
        "16:9" -> 16f / 9f
        "1:1" -> 1f
        "4:5" -> 4f / 5f
        else -> 9f / 16f // 9:16 default
    }

    // Active clips at current timestamp
    val activeVideoClip = clips.firstOrNull {
        it.trackType == TrackType.VIDEO && currentPositionMs >= it.startTimeMs && currentPositionMs <= it.endTimeMs
    }
    val activeTextClips = clips.filter {
        it.trackType == TrackType.TEXT && currentPositionMs >= it.startTimeMs && currentPositionMs <= it.endTimeMs
    }
    val activeStickerClips = clips.filter {
        it.trackType == TrackType.STICKER && currentPositionMs >= it.startTimeMs && currentPositionMs <= it.endTimeMs
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(aspectValue)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(12.dp))
                .background(SlateDarkCard)
                .clickable { onTogglePlayPause() },
            contentAlignment = Alignment.Center
        ) {
            // Interactive Video Frame & AI Segmentation Canvas Player
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw background depending on whether AI Background Removal is active
                if (activeVideoClip?.isAiBgRemoved == true) {
                    when (activeVideoClip.bgReplacementType) {
                        "transparent" -> {
                            // Render professional dark checkered pattern
                            val numCols = 16
                            val numRows = (canvasHeight / (canvasWidth / numCols)).toInt().coerceAtLeast(16)
                            val sqSize = canvasWidth / numCols
                            for (row in 0 until numRows) {
                                for (col in 0 until numCols) {
                                    val isDark = (row + col) % 2 == 0
                                    drawRect(
                                        color = if (isDark) Color(0xFF1E293B) else Color(0xFF0F172A),
                                        topLeft = androidx.compose.ui.geometry.Offset(col * sqSize, row * sqSize),
                                        size = androidx.compose.ui.geometry.Size(sqSize, sqSize)
                                    )
                                }
                            }
                        }
                        "color" -> {
                            // Render solid color background
                            var col = Color.Transparent
                            try {
                                col = Color(android.graphics.Color.parseColor(activeVideoClip.bgSolidColorHex))
                            } catch (e: Exception) {
                                col = Color.Black
                            }
                            drawRect(color = col)
                        }
                        "image" -> {
                            // Render user-selected background image or nice gradient scene representing custom background
                            drawRect(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(Color(0xFF3B82F6), Color(0xFF1E3A8A))
                                )
                            )
                            // Draw sun or secondary elements representing background image
                            drawCircle(
                                color = Color(0xFFFBBF24).copy(alpha = 0.6f),
                                radius = canvasWidth * 0.15f,
                                center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.8f, canvasHeight * 0.2f)
                            )
                        }
                        else -> {
                            drawRect(color = Color.Black)
                        }
                    }
                } else {
                    // Regular Mode (Background not removed)
                    val filterColor = when (activeVideoClip?.filterName) {
                        "Cinematic" -> Color(0xFF0F172A)
                        "Rwandan Dawn" -> Color(0xFF451A03)
                        "Vibrant" -> Color(0xFF064E3B)
                        "Vintage Sepia" -> Color(0xFF422006)
                        "Noir B&W" -> Color(0xFF18181B)
                        "Warm Sunset" -> Color(0xFF7C2D12)
                        else -> Color(0xFF1E293B)
                    }
                    drawRect(color = filterColor)

                    // Draw the normal background scene (hills) since background is not removed!
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, canvasHeight * 0.7f)
                            quadraticTo(
                                canvasWidth * 0.25f, canvasHeight * 0.55f,
                                canvasWidth * 0.5f, canvasHeight * 0.7f
                            )
                            quadraticTo(
                                canvasWidth * 0.75f, canvasHeight * 0.85f,
                                canvasWidth, canvasHeight * 0.65f
                            )
                            lineTo(canvasWidth, canvasHeight)
                            lineTo(0f, canvasHeight)
                            close()
                        },
                        color = Color(0xFF047857) // Rwandan green hills background
                    )
                }

                // Now, render the foreground isolated subject in the center
                // Draw AI segmentation mask outline if background removal is enabled
                val subjectColor = if (activeVideoClip?.isChromaKeyEnabled == true) Color(0xFF0284C7) else Color(0xFF38BDF8)

                // Draw simulated human silhouette subject in the center
                drawCircle(
                    color = subjectColor,
                    radius = canvasWidth * 0.22f,
                    center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.5f, canvasHeight * 0.42f)
                )
                // Draw shoulders/body for the silhouette
                drawPath(
                    path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(canvasWidth * 0.25f, canvasHeight * 0.75f)
                        quadraticTo(
                            canvasWidth * 0.5f, canvasHeight * 0.52f,
                            canvasWidth * 0.75f, canvasHeight * 0.75f
                        )
                        lineTo(canvasWidth * 0.75f, canvasHeight)
                        lineTo(canvasWidth * 0.25f, canvasHeight)
                        close()
                    },
                    color = subjectColor
                )

                // Render crop guides or rotations if custom edits are applied
                if (activeVideoClip != null) {
                    if (activeVideoClip.isAiBgRemoved) {
                        // AI Cutout selection border / tracking outline
                        drawCircle(
                            color = CyanAccent,
                            radius = canvasWidth * 0.24f,
                            center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.5f, canvasHeight * 0.42f),
                            style = Stroke(width = 3f)
                        )
                    }
                }
            }

            // Text Overlays (High Density Subtitles)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                activeTextClips.forEach { textClip ->
                    Surface(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = textClip.textContent,
                            color = RwandaGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Sticker Overlays
            activeStickerClips.forEach { stickerClip ->
                Text(
                    text = stickerClip.stickerSymbol,
                    fontSize = 36.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp)
                )
            }

            // Play/Pause Overlay Button
            AnimatedVisibility(
                visible = !isPlaying,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onTogglePlayPause() }
                        .testTag("preview_play_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Floating AI Tracking Indicator Badge (High Density Theme)
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CyanAccent)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (activeVideoClip?.isAiBgRemoved == true) "AI BG REMOVED" else "AI TRACKING ACTIVE",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Timecode Indicator (00:12 / 00:30)
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(
                text = "${formatMs(currentPositionMs)} / ${formatMs(totalDurationMs)}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
