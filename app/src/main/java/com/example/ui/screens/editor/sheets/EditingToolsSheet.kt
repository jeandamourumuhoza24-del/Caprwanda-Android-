package com.example.ui.screens.editor.sheets

import androidx.compose.foundation.background
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
import com.example.data.local.entities.ClipEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RwandaGold
import com.example.ui.theme.SlateDarkCard

@Composable
fun EditingToolsSheet(
    clip: ClipEntity?,
    onSplit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onRotate: () -> Unit,
    onFlip: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onTrimChange: (startMs: Long, endMs: Long) -> Unit,
    onCropChange: (x: Float, y: Float, w: Float, h: Float) -> Unit,
    onReverseToggle: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        color = SlateDarkCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("editing_tools_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Clip Studio Tools",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (clip != null) {
                // Scrollable container for editing controls (Trim, Speed, Crop)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Playback Speed Control Slider
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Playback Speed", fontSize = 13.sp, color = Color.White)
                            Text("${String.format("%.2f", clip.speed)}x", fontSize = 13.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = clip.speed,
                            onValueChange = onSpeedChange,
                            valueRange = 0.25f..4.0f,
                            colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                        )
                    }

                    // 2. Trim Control Sliders (Adjust clip duration on timeline)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Timeline Trim (Duration)", fontSize = 13.sp, color = Color.White)
                            Text(
                                "Start: ${(clip.startTimeMs / 1000f)}s • End: ${(clip.endTimeMs / 1000f)}s",
                                fontSize = 12.sp,
                                color = RwandaGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Trim Start Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Start Ms: ", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(60.dp))
                            Slider(
                                value = clip.startTimeMs.toFloat(),
                                onValueChange = { newStart ->
                                    val currentEnd = clip.endTimeMs
                                    if (newStart < currentEnd - 500L) {
                                        onTrimChange(newStart.toLong(), currentEnd)
                                    }
                                },
                                valueRange = 0f..20000f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = RwandaGold, activeTrackColor = RwandaGold)
                            )
                        }

                        // Trim End Slider
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("End Ms: ", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.width(60.dp))
                            Slider(
                                value = clip.endTimeMs.toFloat(),
                                onValueChange = { newEnd ->
                                    val currentStart = clip.startTimeMs
                                    if (newEnd > currentStart + 500L) {
                                        onTrimChange(currentStart, newEnd.toLong())
                                    }
                                },
                                valueRange = 500f..30000f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = RwandaGold, activeTrackColor = RwandaGold)
                            )
                        }
                    }

                    // 3. Crop Controls (Adjust Crop X, Y, Width, Height)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Crop Frame Settings", fontSize = 13.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Crop X & Y sliders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Crop X: ${String.format("%.2f", clip.cropX)}", fontSize = 11.sp, color = Color.Gray)
                                Slider(
                                    value = clip.cropX,
                                    onValueChange = { onCropChange(it, clip.cropY, clip.cropWidth, clip.cropHeight) },
                                    valueRange = 0f..0.9f,
                                    colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Crop Y: ${String.format("%.2f", clip.cropY)}", fontSize = 11.sp, color = Color.Gray)
                                Slider(
                                    value = clip.cropY,
                                    onValueChange = { onCropChange(clip.cropX, it, clip.cropWidth, clip.cropHeight) },
                                    valueRange = 0f..0.9f,
                                    colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                                )
                            }
                        }

                        // Crop Width & Height sliders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Crop Width: ${String.format("%.2f", clip.cropWidth)}", fontSize = 11.sp, color = Color.Gray)
                                Slider(
                                    value = clip.cropWidth,
                                    onValueChange = { onCropChange(clip.cropX, clip.cropY, it, clip.cropHeight) },
                                    valueRange = 0.1f..1.0f,
                                    colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Crop Height: ${String.format("%.2f", clip.cropHeight)}", fontSize = 11.sp, color = Color.Gray)
                                Slider(
                                    value = clip.cropHeight,
                                    onValueChange = { onCropChange(clip.cropX, clip.cropY, clip.cropWidth, it) },
                                    valueRange = 0.1f..1.0f,
                                    colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ToolActionButton(
                        title = "Split",
                        icon = Icons.Default.CallSplit,
                        color = CyanAccent,
                        onClick = onSplit
                    )
                }
                item {
                    ToolActionButton(
                        title = "Duplicate",
                        icon = Icons.Default.ContentCopy,
                        color = RwandaGold,
                        onClick = onDuplicate
                    )
                }
                item {
                    ToolActionButton(
                        title = "Rotate",
                        icon = Icons.Default.RotateRight,
                        color = Color.White,
                        onClick = onRotate
                    )
                }
                item {
                    ToolActionButton(
                        title = "Flip",
                        icon = Icons.Default.Flip,
                        color = Color.White,
                        onClick = onFlip
                    )
                }
                item {
                    ToolActionButton(
                        title = if (clip?.isReversed == true) "Normal" else "Reverse",
                        icon = Icons.Default.FastRewind,
                        color = if (clip?.isReversed == true) CyanAccent else Color.White,
                        onClick = onReverseToggle
                    )
                }
                item {
                    ToolActionButton(
                        title = "Delete",
                        icon = Icons.Default.Delete,
                        color = MaterialTheme.colorScheme.error,
                        onClick = onDelete
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = color.copy(alpha = 0.2f)),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(icon, contentDescription = title, tint = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}
