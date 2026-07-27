package com.example.ui.screens.editor.sheets

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Subtitles
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
import com.example.domain.engine.AiFeaturesEngine
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RwandaGold
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface

@Composable
fun AiToolsSheet(
    clip: ClipEntity?,
    isAiLoading: Boolean,
    aiStatusMessage: String,
    onRunAutoCaptions: (AiFeaturesEngine.SubtitleLanguage) -> Unit,
    onToggleBgRemoval: () -> Unit,
    onToggleChromaKey: (String) -> Unit,
    onUpdateBgReplacement: (type: String, colorHex: String, imageUri: String) -> Unit,
    onClose: () -> Unit
) {
    var selectedLang by remember { mutableStateOf(AiFeaturesEngine.SubtitleLanguage.KINYARWANDA) }

    // Color Palette presets for solid background
    val colorPresets = listOf(
        "#000000" to Color.Black,
        "#EF4444" to Color(0xFFEF4444), // Red
        "#10B981" to Color(0xFF10B981), // Green
        "#3B82F6" to Color(0xFF3B82F6), // Blue
        "#F59E0B" to Color(0xFFF59E0B), // Yellow/Orange
        "#8B5CF6" to Color(0xFF8B5CF6), // Purple
        "#EC4899" to Color(0xFFEC4899), // Pink
        "#FFFFFF" to Color.White
    )

    // Image Picker for custom background image
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                onUpdateBgReplacement("image", "#00000000", uri.toString())
            }
        }
    )

    Surface(
        color = SlateDarkCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_tools_sheet")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Smart Studio",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            if (isAiLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(color = CyanAccent, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Text(aiStatusMessage, color = CyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. AI Auto Subtitles
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = RwandaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Auto Captions Generator", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedLang == AiFeaturesEngine.SubtitleLanguage.KINYARWANDA,
                            onClick = { selectedLang = AiFeaturesEngine.SubtitleLanguage.KINYARWANDA },
                            label = { Text("Kinyarwanda") }
                        )
                        FilterChip(
                            selected = selectedLang == AiFeaturesEngine.SubtitleLanguage.ENGLISH,
                            onClick = { selectedLang = AiFeaturesEngine.SubtitleLanguage.ENGLISH },
                            label = { Text("English") }
                        )
                        FilterChip(
                            selected = selectedLang == AiFeaturesEngine.SubtitleLanguage.FRENCH,
                            onClick = { selectedLang = AiFeaturesEngine.SubtitleLanguage.FRENCH },
                            label = { Text("Français") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onRunAutoCaptions(selectedLang) },
                        colors = ButtonDefaults.buttonColors(containerColor = RwandaGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Subtitles", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. AI Background Removal & Chroma Key
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val cutoutActive = clip?.isAiBgRemoved == true
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (cutoutActive) SlateDarkSurface.copy(alpha = 0.5f) else SlateDarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PersonRemove, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("AI Cutout", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(if (cutoutActive) "Active" else "Remove Background", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onToggleBgRemoval,
                            colors = ButtonDefaults.buttonColors(containerColor = if (cutoutActive) RwandaGold else CyanAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (cutoutActive) "Restore" else "Apply", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                val chromaActive = clip?.isChromaKeyEnabled == true
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Chroma Key", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(if (chromaActive) "Active (#00FF00)" else "Green Screen Keying", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onToggleChromaKey("#00FF00") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (chromaActive) "Disable" else "Green Key", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Background Replacement Options (only shown or highlighted if background is removed)
            if (clip?.isAiBgRemoved == true) {
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Replace Background With",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Type Choice Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val currentType = clip.bgReplacementType
                            FilterChip(
                                selected = currentType == "transparent",
                                onClick = { onUpdateBgReplacement("transparent", "#00000000", "") },
                                label = { Text("Transparent") }
                            )
                            FilterChip(
                                selected = currentType == "color",
                                onClick = { onUpdateBgReplacement("color", "#EF4444", "") },
                                label = { Text("Solid Color") }
                            )
                            FilterChip(
                                selected = currentType == "image",
                                onClick = { onUpdateBgReplacement("image", "#00000000", clip.bgImageUri.ifEmpty { "preset_hills" }) },
                                label = { Text("Custom Image") }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Context-specific settings for background type
                        when (clip.bgReplacementType) {
                            "color" -> {
                                Text("Select Solid Color", color = Color.Gray, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(colorPresets) { (hex, col) ->
                                        val isSelected = clip.bgSolidColorHex.equals(hex, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(col)
                                                .border(
                                                    width = if (isSelected) 3.dp else 0.dp,
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    onUpdateBgReplacement("color", hex, "")
                                                }
                                        )
                                    }
                                }
                            }
                            "image" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (clip.bgImageUri.isEmpty() || clip.bgImageUri == "preset_hills") "Preset: Rwanda Hills" else "Custom Image Loaded",
                                            color = CyanAccent,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Uri: ${clip.bgImageUri.take(30)}...",
                                            color = Color.Gray,
                                            fontSize = 9.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            imagePickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Pick Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                            else -> {
                                Text("Background is transparent. Rendered with dark checkered pattern.", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
