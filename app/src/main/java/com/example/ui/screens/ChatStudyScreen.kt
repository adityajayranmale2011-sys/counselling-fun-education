package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.ChatHistoryEntity
import com.example.network.DualPerspectiveAiResult
import com.example.network.MultimodalAttachment
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldRecharge

@Composable
fun ChatStudyScreen(
    attachments: List<MultimodalAttachment>,
    isAnalyzing: Boolean,
    latestResult: DualPerspectiveAiResult?,
    chatHistory: List<ChatHistoryEntity>,
    onAddUriAttachment: (Uri, String, String?) -> Unit,
    onAddCameraBitmap: (Bitmap) -> Unit,
    onRemoveAttachment: (String) -> Unit,
    onSubmitPrompt: (String) -> Unit,
    onDeleteHistoryItem: (Int) -> Unit
) {
    val context = LocalContext.current
    var promptText by remember { mutableStateOf("") }

    // 1. Multiple Images Picker (Zero-permission Android Photo Picker)
    val multipleImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        uris.forEachIndexed { idx, uri ->
            onAddUriAttachment(uri, "Study_Image_${idx + 1}.jpg", "image/jpeg")
        }
    }

    // 2. Multiple Complex PDFs Picker
    val multiplePdfsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEachIndexed { idx, uri ->
            onAddUriAttachment(uri, "Textbook_Document_${idx + 1}.pdf", "application/pdf")
        }
    }

    // 3. Native Camera Capture + Runtime CAMERA Permission Launcher
    val takePicturePreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            onAddCameraBitmap(bitmap)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takePicturePreviewLauncher.launch(null)
        }
    }

    val samplePrompts = listOf(
        "Explain Newton's Second Law of Motion (F = ma) with derivation",
        "Solve quadratic equation x² - 7x + 12 = 0 and explain roots",
        "Explain Photosynthesis light & dark reactions for 10th Grade",
        "Summarize causes of the French Revolution for board exams"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ElectricCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "CHATSTUDY AI • MULTIMODAL WORKSPACE",
                                fontFamily = CinzelFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = ElectricCyan,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        Text(
                            text = "Small & Exact",
                            style = MaterialTheme.typography.labelSmall,
                            color = AcademicGold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Small, Crisp & 100% Correct Answers • OCR & Summary",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Upload images or PDFs, capture textbook pages via camera, and get short, crystal-clear, fully accurate Dual-Perspective answers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Multimodal Input Console
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Attach Multimodal Study Materials (Images, PDFs, Camera)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                multipleImagesLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("chat_upload_images_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Images", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                multiplePdfsLauncher.launch(arrayOf("application/pdf"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("chat_upload_pdfs_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PDFs", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val hasCamPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasCamPerm) {
                                    takePicturePreviewLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("chat_camera_capture_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera", fontSize = 12.sp)
                        }
                    }

                    // Active Attachments Queue
                    if (attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            attachments.forEach { att ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (att.mimeType == "application/pdf")
                                                Icons.Default.PictureAsPdf else Icons.Default.Image,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = att.displayName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        IconButton(
                                            onClick = { onRemoveAttachment(att.id) },
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove Attachment",
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Sample Prompts
                    Text(
                        text = "Quick Academic Question Starters:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        samplePrompts.forEach { sample ->
                            AssistChip(
                                onClick = { promptText = sample },
                                label = { Text(sample, maxLines = 1) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        label = { Text("Ask any textbook question or request OCR & summary…") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_study_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onSubmitPrompt(promptText)
                            promptText = ""
                        },
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("chat_study_submit_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Performing OCR & Generating Dual-Perspective Answers…")
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate Dual-Perspective AI Solution",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Latest Dual-Perspective Output Card
        if (latestResult != null) {
            item {
                DualPerspectiveResponseView(
                    title = "Latest Multimodal Dual-Perspective Output",
                    ocrSummary = latestResult.extractedOcrAndSummary,
                    simplifiedAnswer = latestResult.simplifiedConceptualAnswer,
                    academicAnswer = latestResult.detailedAcademicAnswer
                )
            }
        }

        // Previous ChatStudy History Database Section
        if (chatHistory.isNotEmpty()) {
            item {
                Text(
                    text = "ChatStudy AI History Database (${chatHistory.size} Saved Solutions)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(chatHistory, key = { it.id }) { entry ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Q: ${entry.promptText}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Attachments (${entry.attachmentCount}): ${entry.attachmentSummary}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            IconButton(onClick = { onDeleteHistoryItem(entry.id) }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete History Item",
                                    tint = CoralAlert
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        DualPerspectiveResponseView(
                            title = null,
                            ocrSummary = entry.extractedOcrAndSummary,
                            simplifiedAnswer = entry.simplifiedAnswer,
                            academicAnswer = entry.academicAnswer
                        )
                    }
                }
            }
        }

        // Creator Name Footer at the bottom of ChatStudy AI Page
        item {
            ArCreatorPageFooter(
                pageSubtitle = "Counseling : fun education • Small & Exact ChatStudy AI"
            )
        }
    }
}

@Composable
fun DualPerspectiveResponseView(
    title: String?,
    ocrSummary: String,
    simplifiedAnswer: String,
    academicAnswer: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // OCR & Document Summary Box
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "OCR Text Extraction & Document Summary",
                        style = MaterialTheme.typography.labelLarge,
                        color = ElectricCyan
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ocrSummary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Answer 1: Simplified Conceptual Perspective
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = EmeraldRecharge.copy(alpha = 0.1f),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EmeraldRecharge.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .testTag("perspective_1_simplified_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = EmeraldRecharge,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PERSPECTIVE 1: SIMPLIFIED CONCEPTUAL ANSWER",
                        style = MaterialTheme.typography.titleSmall,
                        color = EmeraldRecharge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = simplifiedAnswer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Answer 2: Detailed Academic Perspective
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AcademicGold.copy(alpha = 0.1f),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AcademicGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .testTag("perspective_2_academic_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = AcademicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PERSPECTIVE 2: DETAILED ACADEMIC ANSWER",
                        style = MaterialTheme.typography.titleSmall,
                        color = AcademicGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = academicAnswer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
