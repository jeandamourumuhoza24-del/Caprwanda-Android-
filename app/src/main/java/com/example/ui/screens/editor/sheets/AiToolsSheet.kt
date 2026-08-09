package com.example.ui.screens.editor.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Subtitles
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
    onUpdateBackground: (bgType: String, bgColorHex: String, bgImageUri: String) -> Unit,
    onClose: () -> Unit
) {
    var selectedLang by remember { mutableStateOf(AiFeaturesEngine.SubtitleLanguage.KINYARWANDA) }

    val colorsPalette = listOf(
        "#000000" to "Black",
        "#1E3A8A" to "Blue",
        "#047857" to "Green",
        "#B91C1C" to "Red",
        "#6D28D9" to "Purple",
        "#F59E0B" to "Amber"
    )

    val imagesPalette = listOf(
        "kigali_sunset_bg.jpg" to "Kigali Sunset",
        "lake_kivu_bg.jpg" to "Lake Kivu",
        "nyungwe_forest_bg.jpg" to "Nyungwe",
        "volcanoes_park_bg.jpg" to "Volcanoes"
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
                .verticalScroll(rememberScrollState())
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

            Spacer(modifier = Modifier.height(16.dp))

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
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
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
                        Text("Remove Background", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onToggleBgRemoval,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Text(
                                text = if (clip?.isAiBgRemoved == true) "Disable" else "Apply",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

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
                        Text("Green Screen Keying", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onToggleChromaKey("#00FF00") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text(
                                text = if (clip?.isChromaKeyEnabled == true) "Disable" else "Green Key",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Replace Background Section (Image or Color)
            if (clip != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = CyanAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Replace Background (AI Cutout Required)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle Mode: Color vs Image
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = clip.bgType == "color",
                                onClick = { onUpdateBackground("color", clip.bgColorHex, clip.bgImageUri) },
                                label = { Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Solid Color")
                                }}
                            )
                            FilterChip(
                                selected = clip.bgType == "image",
                                onClick = { onUpdateBackground("image", clip.bgColorHex, clip.bgImageUri.ifEmpty { "kigali_sunset_bg.jpg" }) },
                                label = { Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Image Backdrop")
                                }}
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (clip.bgType == "color") {
                            // Render solid color options
                            Text("Select Backdrop Color", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(colorsPalette) { (hex, name) ->
                                    val isSelected = clip.bgColorHex.lowercase() == hex.lowercase()
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onUpdateBackground("color", hex, clip.bgImageUri) },
                                        label = { Text(name) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyanAccent,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        } else {
                            // Render image backdrop options
                            Text("Select Backdrop Image", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(imagesPalette) { (uri, name) ->
                                    val isSelected = clip.bgImageUri == uri
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onUpdateBackground("image", clip.bgColorHex, uri) },
                                        label = { Text(name) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = RwandaGold,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
