package com.example.ui.screens.import

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.CapRwandaTopBar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.RwandaGold
import com.example.ui.theme.SlateDarkCard
import com.example.ui.theme.SlateDarkSurface
import kotlinx.coroutines.delay
import java.util.UUID

data class ImportMediaItem(
    val id: String,
    val title: String,
    val durationText: String,
    val uri: String
)

@Composable
fun VideoImportScreen(
    onProjectCreated: (Long) -> Unit,
    onBackClick: () -> Unit,
    onCreateProject: (title: String, aspectRatio: String, selectedItems: List<ImportMediaItem>) -> Unit
) {
    val context = LocalContext.current
    var projectTitle by remember { mutableStateOf("Rwanda Vlog Video") }
    var selectedAspectRatio by remember { mutableStateOf("9:16") }

    // Media gallery list including default items
    var mediaGallery by remember {
        mutableStateOf(
            listOf(
                ImportMediaItem("v1", "Kigali Horizon Drive (Sample)", "0:15", "media_rw_1.mp4"),
                ImportMediaItem("v2", "Nyungwe Canopy Walk (Sample)", "0:25", "media_rw_2.mp4"),
                ImportMediaItem("v3", "Musanze Cave Trek (Sample)", "0:18", "media_rw_3.mp4"),
                ImportMediaItem("v4", "Gisenyi Beach Sunset (Sample)", "0:30", "media_rw_4.mp4")
            )
        )
    }

    var selectedItems by remember { mutableStateOf(setOf("v1")) }
    val aspectRatios = listOf("9:16", "16:9", "1:1", "4:5")

    // State for Custom Camera Recorder Overlay
    var isCameraOverlayVisible by remember { mutableStateOf(false) }
    var isRecordingVideo by remember { mutableStateOf(false) }
    var recordingTimerSeconds by remember { mutableStateOf(0) }

    // Screen dimension checks for responsiveness
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val columns = if (screenWidth > 600) 3 else 2
    val galleryChunks = remember(mediaGallery, columns) {
        mediaGallery.chunked(columns)
    }

    // Handlers for Permissions
    val requiredPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val cameraGranted = results[Manifest.permission.CAMERA] ?: false
        val audioGranted = results[Manifest.permission.RECORD_AUDIO] ?: false
        if (cameraGranted && audioGranted) {
            Toast.makeText(context, "Permissions granted! Ready for Camera & Studio.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permissions partially approved. Some features might be limited.", Toast.LENGTH_LONG).show()
        }
    }

    // Media picking launchers
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val id = UUID.randomUUID().toString()
            val newItem = ImportMediaItem(
                id = id,
                title = "Gallery Video ${mediaGallery.size + 1}",
                durationText = "0:10",
                uri = it.toString()
            )
            mediaGallery = mediaGallery + newItem
            selectedItems = selectedItems + id
            Toast.makeText(context, "Video imported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val id = UUID.randomUUID().toString()
            val newItem = ImportMediaItem(
                id = id,
                title = "Gallery Photo ${mediaGallery.size + 1}",
                durationText = "Photo",
                uri = it.toString()
            )
            mediaGallery = mediaGallery + newItem
            selectedItems = selectedItems + id
            Toast.makeText(context, "Photo imported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val id = UUID.randomUUID().toString()
            val newItem = ImportMediaItem(
                id = id,
                title = "Phone Audio ${mediaGallery.size + 1}",
                durationText = "Audio",
                uri = it.toString()
            )
            mediaGallery = mediaGallery + newItem
            selectedItems = selectedItems + id
            Toast.makeText(context, "Audio imported successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check and request permissions
    fun checkAndRequestPermissions(onSuccess: () -> Unit) {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(requiredPermissions)
        } else {
            onSuccess()
        }
    }

    // Timer Effect for Camera Recording Simulation
    LaunchedEffect(isRecordingVideo) {
        if (isRecordingVideo) {
            recordingTimerSeconds = 0
            while (isRecordingVideo) {
                delay(1000)
                recordingTimerSeconds++
            }
        }
    }

    Scaffold(
        topBar = {
            CapRwandaTopBar(
                title = "Import Media",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Project Title Textfield
                item {
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { projectTitle = it },
                        label = { Text("Project Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            focusedLabelColor = CyanAccent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("project_title_input")
                    )
                }

                // Canvas Aspect Ratio chips
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Canvas Aspect Ratio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Scrollable chip row for small screen safety
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(aspectRatios) { ratio ->
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
                    }
                }

                // Select Media Source button row / grid (responsively scaled)
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Select Media Source", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontally scrollable row on very narrow screens, otherwise dynamic spacing
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Button(
                                    onClick = {
                                        checkAndRequestPermissions {
                                            videoPickerLauncher.launch("video/*")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.widthIn(min = 72.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = CyanAccent)
                                        Text("Video", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        checkAndRequestPermissions {
                                            photoPickerLauncher.launch("image/*")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.widthIn(min = 72.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Photo, contentDescription = null, tint = RwandaGold)
                                        Text("Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        checkAndRequestPermissions {
                                            audioPickerLauncher.launch("audio/*")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.widthIn(min = 72.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Audiotrack, contentDescription = null, tint = Color(0xFF10B981))
                                        Text("Audio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        checkAndRequestPermissions {
                                            isCameraOverlayVisible = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.widthIn(min = 100.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                        Text("Camera Rec", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                // Import selection header
                item {
                    Text(
                        text = "Import Selection (${selectedItems.size} selected)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Responsive chunk-based grid gallery
                items(galleryChunks) { chunk ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        chunk.forEach { item ->
                            val isSelected = selectedItems.contains(item.id)
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SlateDarkCard),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
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
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = Color.Black.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = item.durationText,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            if (isSelected) {
                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = CyanAccent,
                                                    modifier = Modifier.size(20.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = item.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                        // Fill extra spaces if the last chunk is not full
                        if (chunk.size < columns) {
                            repeat(columns - chunk.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Start Editing floating action bar at bottom of import view
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val selectedMediaList = mediaGallery.filter { selectedItems.contains(it.id) }
                        onCreateProject(
                            projectTitle.ifBlank { "CapRwanda Project" },
                            selectedAspectRatio,
                            selectedMediaList
                        )
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

            // Beautiful Custom Simulated Camera Recorder Overlay (Responsively Wrapped)
            AnimatedVisibility(
                visible = isCameraOverlayVisible,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Fully scrollable layout to handle extremely small/landscape screen heights perfectly
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Top Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIVING STREAMING PREVIEW",
                                color = RwandaGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            IconButton(
                                onClick = {
                                    isRecordingVideo = false
                                    isCameraOverlayVisible = false
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close Camera", tint = Color.White)
                            }
                        }

                        // Simulated Live Stream Viewport (Height-scaled responsibly)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp, max = 320.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SlateDarkSurface)
                                .border(2.dp, if (isRecordingVideo) Color.Red else Color.Gray, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = if (isRecordingVideo) Color.Red else Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isRecordingVideo) "RECORDING: ${recordingTimerSeconds}s" else "READY TO RECORD",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (isRecordingVideo) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(Color.Red)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("REC", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Control Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isRecordingVideo) {
                                Button(
                                    onClick = { isRecordingVideo = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(32.dp),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(2.dp, Color.White, RoundedCornerShape(32.dp))
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Start Recording", tint = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        isRecordingVideo = false
                                        isCameraOverlayVisible = false
                                        val id = UUID.randomUUID().toString()
                                        val newItem = ImportMediaItem(
                                            id = id,
                                            title = "Camera Capture ${mediaGallery.size + 1}",
                                            durationText = "0:08",
                                            uri = "camera_capture_${UUID.randomUUID().toString().take(6)}.mp4"
                                        )
                                        mediaGallery = mediaGallery + newItem
                                        selectedItems = selectedItems + id
                                        Toast.makeText(context, "Recorded video saved successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(32.dp),
                                    modifier = Modifier
                                        .size(64.dp)
                                        .border(2.dp, Color.Red, RoundedCornerShape(32.dp))
                                ) {
                                    Icon(Icons.Default.Stop, contentDescription = "Stop Recording", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
