package com.example.ui.screens.editor.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.SlateDarkSurface

@Composable
fun EditingToolsSheet(
    clip: ClipEntity?,
    onSplit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onRotate: () -> Unit,
    onFlip: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onReverseToggle: () -> Unit,
    onTrimChange: (startMs: Long, endMs: Long) -> Unit,
    onCropChange: (cropWidth: Float, cropHeight: Float) -> Unit,
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
                    text = "Clip Studio Editor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (clip != null) {
                // 1. Playback Speed Control Slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Playback Speed", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                        Text("${String.format("%.2f", clip.speed)}x", fontSize = 12.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = clip.speed,
                        onValueChange = onSpeedChange,
                        valueRange = 0.25f..4.0f,
                        colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent),
                        modifier = Modifier.height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Interactive Video/Image Trim Control (Trim start & end)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Trim Range", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Trim Start Slider Column
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Trim Start", fontSize = 11.sp, color = Color.Gray)
                                Text("${String.format("%.1f", clip.sourceTrimStartMs / 1000f)}s", fontSize = 11.sp, color = RwandaGold, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = clip.sourceTrimStartMs.toFloat(),
                                onValueChange = { newVal ->
                                    val safeStart = newVal.toLong().coerceIn(0L, clip.sourceTrimEndMs - 500L)
                                    onTrimChange(safeStart, clip.sourceTrimEndMs)
                                },
                                valueRange = 0f..15000f,
                                colors = SliderDefaults.colors(thumbColor = RwandaGold, activeTrackColor = RwandaGold),
                                modifier = Modifier.height(24.dp)
                            )
                        }

                        // Trim End Slider Column
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Trim End", fontSize = 11.sp, color = Color.Gray)
                                Text("${String.format("%.1f", clip.sourceTrimEndMs / 1000f)}s", fontSize = 11.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                            }
                            Slider(
                                value = clip.sourceTrimEndMs.toFloat(),
                                onValueChange = { newVal ->
                                    val safeEnd = newVal.toLong().coerceIn(clip.sourceTrimStartMs + 500L, 15000L)
                                    onTrimChange(clip.sourceTrimStartMs, safeEnd)
                                },
                                valueRange = 0f..15000f,
                                colors = SliderDefaults.colors(thumbColor = CyanAccent, activeTrackColor = CyanAccent),
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Clip Aspect Crop Controls (Trim, split, crop and rotate)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Crop Aspect Ratio", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val is1_1 = clip.cropWidth == 1f && clip.cropHeight == 1f
                        val is16_9 = clip.cropWidth == 1.77f
                        val is9_16 = clip.cropWidth == 0.56f
                        val is4_5 = clip.cropWidth == 0.8f

                        FilterChip(
                            selected = is1_1,
                            onClick = { onCropChange(1f, 1f) },
                            label = { Text("1:1 Square") }
                        )
                        FilterChip(
                            selected = is16_9,
                            onClick = { onCropChange(1.77f, 1f) },
                            label = { Text("16:9 Landscape") }
                        )
                        FilterChip(
                            selected = is9_16,
                            onClick = { onCropChange(0.56f, 1f) },
                            label = { Text("9:16 Portrait") }
                        )
                        FilterChip(
                            selected = is4_5,
                            onClick = { onCropChange(0.8f, 1f) },
                            label = { Text("4:5 Post") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
