package com.example.ui.screens.editor.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ClipEntity
import com.example.domain.engine.VideoProcessingEngine
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface

@Composable
fun EffectsFilterSheet(
    clip: ClipEntity?,
    onApplyFilter: (String) -> Unit,
    onColorAdjustments: (Float, Float, Float) -> Unit,
    onClose: () -> Unit
) {
    var brightness by remember(clip) { mutableFloatStateOf(clip?.brightness ?: 0f) }
    var contrast by remember(clip) { mutableFloatStateOf(clip?.contrast ?: 1f) }
    var saturation by remember(clip) { mutableFloatStateOf(clip?.saturation ?: 1f) }

    Surface(
        color = SlateDarkCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("effects_filter_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Effects & Filters",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Color Presets", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(VideoProcessingEngine.availableFilters) { filter ->
                    val isSelected = clip?.filterName == filter.name
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(72.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(filter.colorOverlay)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) CyanAccent else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onApplyFilter(filter.name) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(filter.name.take(2), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = filter.name,
                            fontSize = 11.sp,
                            color = if (isSelected) CyanAccent else Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Manual Adjustments", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)

            // Brightness
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Brightness", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(80.dp))
                Slider(
                    value = brightness,
                    onValueChange = {
                        brightness = it
                        onColorAdjustments(brightness, contrast, saturation)
                    },
                    valueRange = -0.5f..0.5f,
                    modifier = Modifier.weight(1f)
                )
            }

            // Contrast
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Contrast", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(80.dp))
                Slider(
                    value = contrast,
                    onValueChange = {
                        contrast = it
                        onColorAdjustments(brightness, contrast, saturation)
                    },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.weight(1f)
                )
            }

            // Saturation
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Saturation", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.width(80.dp))
                Slider(
                    value = saturation,
                    onValueChange = {
                        saturation = it
                        onColorAdjustments(brightness, contrast, saturation)
                    },
                    valueRange = 0.0f..2.0f,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
