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
                    text = "Clip Tools",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speed Control Slider
            if (clip != null) {
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
