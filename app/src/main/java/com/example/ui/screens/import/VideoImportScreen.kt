package com.example.ui.screens.import

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CapRwandaTopBar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RwandaGold
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface

data class ImportMediaItem(
    val id: String,
    val title: String,
    val durationText: String,
    val uri: String,
    val isImage: Boolean = false
)

@Composable
fun VideoImportScreen(
    onProjectCreated: (Long) -> Unit,
    onBackClick: () -> Unit,
    onCreateProject: (title: String, aspectRatio: String, mediaItems: List<Triple<String, String, Boolean>>) -> Unit
) {
    val context = LocalContext.current
    var projectTitle by remember { mutableStateOf("Rwanda Vlog Video") }
    var selectedAspectRatio by remember { mutableStateOf("9:16") }

    // Initial gallery with simulated items + custom picked items
    var mediaGallery by remember {
        mutableStateOf(
            listOf(
                ImportMediaItem("v1", "Kigali Horizon Drive", "0:15", "media_rw_1.mp4"),
                ImportMediaItem("v2", "Nyungwe Canopy Walk", "0:25", "media_rw_2.mp4"),
                ImportMediaItem("v3", "Musanze Cave Trek", "0:18", "media_rw_3.mp4"),
                ImportMediaItem("v4", "Gisenyi Beach Sunset", "0:30", "media_rw_4.mp4")
            )
        )
    }

    var selectedItems by remember { mutableStateOf(setOf("v1", "v2")) }

    // Activity launcher for Android Photo Picker (supports multiple images and videos)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                val newItems = uris.mapIndexed { index, uri ->
                    val uriStr = uri.toString()
                    val isImage = uriStr.contains("image") ||
                                  uriStr.endsWith(".jpg") ||
                                  uriStr.endsWith(".png") ||
                                  uriStr.endsWith(".jpeg") ||
                                  uriStr.endsWith(".webp") ||
                                  (context.contentResolver.getType(uri)?.startsWith("image/") == true)

                    val typePrefix = if (isImage) "IMG" else "VID"
                    val id = "picked_${System.currentTimeMillis()}_$index"
                    ImportMediaItem(
                        id = id,
                        title = "Gallery $typePrefix ${index + 1}",
                        durationText = if (isImage) "0:05" else "0:05",
                        uri = uriStr,
                        isImage = isImage
                    )
                }
                mediaGallery = mediaGallery + newItems
                selectedItems = selectedItems + newItems.map { it.id }.toSet()
            }
        }
    )

    val aspectRatios = listOf("9:16", "16:9", "1:1", "4:5")

    Scaffold(
        topBar = {
            CapRwandaTopBar(
                title = "Import Media",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = projectTitle,
                onValueChange = { projectTitle = it },
                label = { Text("Project Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_title_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Canvas Aspect Ratio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                aspectRatios.forEach { ratio ->
                    val isSelected = selectedAspectRatio == ratio
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedAspectRatio = ratio },
                        label = { Text(ratio, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Source Mode Selector (Gallery vs Camera)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            // Launch Photo Picker to choose videos and images
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Gallery", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = RwandaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Camera Rec", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Video & Image Clips (${selectedItems.size} selected)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(mediaGallery, key = { it.id }) { item ->
                    val isSelected = selectedItems.contains(item.id)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(110.dp)
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) CyanAccent else Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedItems = if (isSelected) {
                                    selectedItems - item.id
                                } else {
                                    selectedItems + item.id
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(item.durationText, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }

                                    if (isSelected) {
                                        Surface(shape = RoundedCornerShape(12.dp), color = CyanAccent, modifier = Modifier.size(20.dp)) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val selectedList = mediaGallery.filter { selectedItems.contains(it.id) }
                    val itemsToSend = selectedList.map { Triple(it.uri, it.title, it.isImage) }
                    onCreateProject(projectTitle.ifBlank { "CapRwanda Project" }, selectedAspectRatio, itemsToSend)
                },
                enabled = selectedItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_editing_button")
            ) {
                Text("Start Editing Project", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
