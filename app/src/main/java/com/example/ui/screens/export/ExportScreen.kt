package com.example.ui.screens.export

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CapRwandaTopBar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RwandaGold
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface

@Composable
fun ExportScreen(
    viewModel: ExportViewModel,
    onBackClick: () -> Unit
) {
    val project by viewModel.project.collectAsStateWithLifecycle()
    val resolution by viewModel.selectedResolution.collectAsStateWithLifecycle()
    val fps by viewModel.selectedFps.collectAsStateWithLifecycle()
    val format by viewModel.selectedFormat.collectAsStateWithLifecycle()
    val exportState by viewModel.exportState.collectAsStateWithLifecycle()
    val imageSaveMessage by viewModel.imageSaveMessage.collectAsStateWithLifecycle()

    val resolutions = listOf("720p", "1080p", "4K")
    val fpsOptions = listOf(30, 60)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(imageSaveMessage) {
        imageSaveMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearImageSaveMessage()
        }
    }

    Scaffold(
        topBar = {
            CapRwandaTopBar(
                title = "Export Media Studio",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = project?.title ?: "Video Project",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Duration: 00:15 • Aspect: ${project?.aspectRatio ?: "9:16"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Resolution Quality", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        resolutions.forEach { res ->
                            FilterChip(
                                selected = resolution == res,
                                onClick = { viewModel.setResolution(res) },
                                label = { Text(res, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanAccent,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Frame Rate (FPS)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        fpsOptions.forEach { option ->
                            FilterChip(
                                selected = fps == option,
                                onClick = { viewModel.setFps(option) },
                                label = { Text("${option} fps", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RwandaGold,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exporting Progress Box
            if (exportState.isExporting || exportState.isCompleted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (exportState.isExporting) {
                            Text("Rendering Video Frames...", fontWeight = FontWeight.Bold, color = CyanAccent, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { exportState.progressPercent },
                                color = CyanAccent,
                                trackColor = SlateDarkCard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${(exportState.progressPercent * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Frame: ${exportState.currentFrame}/${exportState.totalFrames}", color = Color.Gray, fontSize = 12.sp)
                                Text("${exportState.timeRemainingSeconds}s left", color = RwandaGold, fontSize = 12.sp)
                            }
                        } else if (exportState.isCompleted) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Export Completed Successfully!", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(exportState.exportedFileUri ?: "", color = Color.Gray, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* Share action */ },
                                colors = ButtonDefaults.buttonColors(containerColor = RwandaGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share MP4 Video", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!exportState.isExporting && !exportState.isCompleted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Export as Video (MP4)
                    Button(
                        onClick = { viewModel.startExporting() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_export_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export MP4 Video", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    // 2. Save Edited Image / Current Frame (Save edited images)
                    OutlinedButton(
                        onClick = { viewModel.saveEditedImage() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RwandaGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RwandaGold),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_image_button")
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = RwandaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Edited Image Frame", color = RwandaGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
