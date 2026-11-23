package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.AlarmClock
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.MainActivity
import com.example.data.local.ScheduleTimeBlock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ActiveHardwareAlarmState(
    val isRinging: Boolean = false,
    val stageLabel: String = "",
    val metaHeader: String = "STUDY TIME",
    val blockTitle: String = "",
    val blockDescription: String = "",
    val audioSourceUsed: String = "SYSTEM_ALARM",
    val countdownSecondsToStage2: Int = 0
)

data class GeneratedPdfInfo(
    val file: File,
    val pageCount: Int,
    val imageCount: Int
)

object AlarmAndPdfEngine {
    const val ACTION_STAGE_1_PUSH = "com.example.ACTION_STAGE_1_PUSH"
    const val ACTION_STAGE_2_HARDWARE_ALARM = "com.example.ACTION_STAGE_2_HARDWARE_ALARM"
    const val ACTION_DISMISS_HARDWARE_ALARM = "com.example.ACTION_DISMISS_HARDWARE_ALARM"

    private const val CHANNEL_PUSH_ID = "ar_creator_t0_push_channel"
    private const val CHANNEL_ALARM_ID = "ar_creator_t5_hardware_alarm_channel"
    private const val NOTIFICATION_PUSH_ID = 4101
    private const val NOTIFICATION_ALARM_ID = 4102

    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var activeRingtone: Ringtone? = null
    private var activeMediaPlayer: MediaPlayer? = null
    private var activeToneJob: Job? = null
    private var stageSequenceJob: Job? = null

    private val _alarmState = MutableStateFlow(ActiveHardwareAlarmState())
    val alarmState: StateFlow<ActiveHardwareAlarmState> = _alarmState.asStateFlow()

    fun ensureNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val pushChannel = NotificationChannel(
                CHANNEL_PUSH_ID,
                "T-0s Schedule Push Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Stage 1 (T-0s) targeted push notifications for scheduled blocks"
                enableVibration(true)
            }
            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM_ID,
                "T+5s Hardware System Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Stage 2 (T+5s) persistent hardware alarms with dynamic meta headers"
                enableVibration(true)
            }
            nm.createNotificationChannel(pushChannel)
            nm.createNotificationChannel(alarmChannel)
        }
    }

    /**
     * Triggers the exact Multi-Stage Alert Logic:
     * 1. T-0 Seconds: Targeted push notification displaying item description text + Alarm Meta Header
     * 2. T+5 Seconds: Persistent hardware system alarm respecting user's Audio Customization
     */
    fun triggerTwoStageAlertSequence(
        context: Context,
        block: ScheduleTimeBlock,
        audioSource: String,
        customAudioUri: String,
        pushEnabled: Boolean = true,
        hardwareAlarmEnabled: Boolean = true,
        speakersEnabled: Boolean = true
    ) {
        ensureNotificationChannels(context)
        stageSequenceJob?.cancel()

        stageSequenceJob = engineScope.launch {
            // Stage 1: T-0 Seconds Targeted Push Notification
            if (pushEnabled) {
                postTargetedPushNotification(
                    context = context,
                    metaHeader = block.metaHeader,
                    title = "${block.timeRange} • ${block.title}",
                    description = block.subjectOrDetail
                )
            }

            // Schedule Stage 2 via AlarmManager at T+5 seconds AND track live countdown in UI
            scheduleHardwareAlarmAtPlus5Seconds(
                context = context,
                block = block,
                audioSource = audioSource,
                customAudioUri = customAudioUri
            )

            for (remaining in 5 downTo 1) {
                _alarmState.value = ActiveHardwareAlarmState(
                    isRinging = false,
                    stageLabel = "STAGE 1 (T-0s) PUSH SENT • STAGE 2 IN ${remaining}s",
                    metaHeader = block.metaHeader,
                    blockTitle = block.title,
                    blockDescription = block.subjectOrDetail,
                    audioSourceUsed = audioSource,
                    countdownSecondsToStage2 = remaining
                )
                delay(1000L)
            }

            // Stage 2: T+5 Seconds Persistent Hardware System Alarm
            if (hardwareAlarmEnabled) {
                firePersistentHardwareAlarm(
                    context = context,
                    metaHeader = block.metaHeader,
                    title = "${block.timeRange} • ${block.title}",
                    description = block.subjectOrDetail,
                    audioSource = if (speakersEnabled) audioSource else "VIBRATE_ONLY",
                    customAudioUri = customAudioUri
                )
            } else {
                _alarmState.value = ActiveHardwareAlarmState()
            }
        }
    }

    private fun scheduleHardwareAlarmAtPlus5Seconds(
        context: Context,
        block: ScheduleTimeBlock,
        audioSource: String,
        customAudioUri: String
    ) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
                action = ACTION_STAGE_2_HARDWARE_ALARM
                putExtra("EXTRA_TITLE", "${block.timeRange} • ${block.title}")
                putExtra("EXTRA_DESCRIPTION", block.subjectOrDetail)
                putExtra("EXTRA_META_HEADER", block.metaHeader)
                putExtra("EXTRA_AUDIO_SOURCE", audioSource)
                putExtra("EXTRA_CUSTOM_URI", customAudioUri)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                block.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val triggerAtMillis = System.currentTimeMillis() + 5000L
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } catch (_: Exception) {
            // Handled by coroutine timer as primary/backup
        }
    }

    /**
     * Also allows setting a native Android system clock alarm for a block's start time (HH:mm)
     */
    fun launchNativeSystemAlarmClock(context: Context, block: ScheduleTimeBlock) {
        try {
            val timePart = block.timeRange.split("-").firstOrNull()?.trim() ?: "04:00"
            val hm = timePart.split(":")
            val hour = hm.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: 4
            val minute = hm.getOrNull(1)?.filter { it.isDigit() }?.toIntOrNull() ?: 0

            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour.coerceIn(0, 23))
                putExtra(AlarmClock.EXTRA_MINUTES, minute.coerceIn(0, 59))
                putExtra(AlarmClock.EXTRA_MESSAGE, "[${block.metaHeader}] ${block.title} - ${block.subjectOrDetail}")
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // If emulator lacks Clock app handler, trigger the built-in 2-stage alarm sequence
            triggerTwoStageAlertSequence(
                context = context,
                block = block,
                audioSource = "SYSTEM_ALARM",
                customAudioUri = ""
            )
        }
    }

    fun postTargetedPushNotification(
        context: Context,
        metaHeader: String,
        title: String,
        description: String
    ) {
        ensureNotificationChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingOpen = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PUSH_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("[$metaHeader] $title")
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$description\n\nStage 2 Hardware Alarm triggers in T+5 seconds."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingOpen)
            .build()

        try {
            nm.notify(NOTIFICATION_PUSH_ID, notification)
        } catch (_: SecurityException) {
            // Notification permission not granted yet
        }
    }

    fun firePersistentHardwareAlarm(
        context: Context,
        metaHeader: String,
        title: String,
        description: String,
        audioSource: String,
        customAudioUri: String
    ) {
        stopAudioPlayersOnly()
        ensureNotificationChannels(context)

        _alarmState.value = ActiveHardwareAlarmState(
            isRinging = true,
            stageLabel = "STAGE 2 (T+5s) HARDWARE SYSTEM ALARM ACTIVE",
            metaHeader = metaHeader,
            blockTitle = title,
            blockDescription = description,
            audioSourceUsed = audioSource,
            countdownSecondsToStage2 = 0
        )

        // Trigger hardware vibration pulse
        triggerHardwareVibration(context)

        // Play persistent audio based on Audio Customization setting
        when (audioSource) {
            "CUSTOM_LOCAL_AUDIO" -> {
                if (customAudioUri.isNotBlank()) {
                    try {
                        val mp = MediaPlayer().apply {
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_ALARM)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .build()
                            )
                            setDataSource(context, Uri.parse(customAudioUri))
                            isLooping = true
                            prepare()
                            start()
                        }
                        activeMediaPlayer = mp
                    } catch (_: Exception) {
                        playNativeSystemAlarmRingtone(context)
                    }
                } else {
                    playNativeSystemAlarmRingtone(context)
                }
            }
            "APP_SOUND_PACK" -> {
                playArCreatorAppSoundPack()
            }
            "VIBRATE_ONLY" -> {
                // Hardware vibration only
            }
            else -> {
                playNativeSystemAlarmRingtone(context)
            }
        }

        // Post persistent notification with Dismiss Action
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val dismissIntent = Intent(context, ScheduleAlarmReceiver::class.java).apply {
            action = ACTION_DISMISS_HARDWARE_ALARM
        }
        val pendingDismiss = PendingIntent.getBroadcast(
            context,
            99,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmNotification = NotificationCompat.Builder(context, CHANNEL_ALARM_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("ALARM: [$metaHeader] $title")
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss Alarm", pendingDismiss)
            .build()

        try {
            nm.notify(NOTIFICATION_ALARM_ID, alarmNotification)
        } catch (_: SecurityException) {
            // Ignore if POST_NOTIFICATIONS denied
        }
    }

    private fun playNativeSystemAlarmRingtone(context: Context) {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = true
            }
            ringtone?.play()
            activeRingtone = ringtone
        } catch (_: Exception) {
            playArCreatorAppSoundPack()
        }
    }

    private fun playArCreatorAppSoundPack() {
        activeToneJob?.cancel()
        activeToneJob = engineScope.launch(Dispatchers.Default) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 90)
                repeat(8) {
                    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 350)
                    delay(500L)
                    toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 250)
                    delay(450L)
                }
                toneGen.release()
            } catch (_: Exception) {
                // Ignore audio hardware error
            }
        }
    }

    private fun triggerHardwareVibration(context: Context) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 500, 200, 600), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(1200)
            }
        } catch (_: Exception) {
            // Ignore if vibrator unavailable
        }
    }

    private fun stopAudioPlayersOnly() {
        try {
            activeRingtone?.stop()
        } catch (_: Exception) {}
        activeRingtone = null

        try {
            activeMediaPlayer?.stop()
            activeMediaPlayer?.release()
        } catch (_: Exception) {}
        activeMediaPlayer = null

        activeToneJob?.cancel()
        activeToneJob = null
    }

    fun stopPersistentHardwareAlarm(context: Context) {
        stageSequenceJob?.cancel()
        stopAudioPlayersOnly()
        _alarmState.value = ActiveHardwareAlarmState(isRinging = false)
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(NOTIFICATION_ALARM_ID)
        } catch (_: Exception) {}
    }

    /**
     * TAB 3: PDF Maker Engine
     * Compiles raw text assets, structured notes, and gallery/camera roll images into a
     * highly structured, formatted multi-page PDF document saved to Internal Device Storage.
     */
    suspend fun generateFormattedPdfDocument(
        context: Context,
        documentTitle: String,
        studentAuthor: String,
        academicCategory: String,
        rawTextContent: String,
        imageUris: List<Uri>,
        capturedBitmaps: List<Bitmap>
    ): Result<GeneratedPdfInfo> = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // Standard A4 width in PostScript points
            val pageHeight = 842 // Standard A4 height in PostScript points
            val margin = 40f
            val contentWidth = pageWidth - (margin * 2)

            val headerBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#0F172A")
            }
            val goldAccentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#F5B041")
            }
            val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#F5B041")
                textSize = 11f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            }
            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#CBD5E1")
                textSize = 10f
            }
            val sectionHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#1E3A8A")
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#1E293B")
                textSize = 11.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#64748B")
                textSize = 9.5f
            }
            val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#F8FAFC")
                style = Paint.Style.FILL
            }
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#CBD5E1")
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            var currentPageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            var currentPage = pdfDocument.startPage(pageInfo)
            var canvas: Canvas = currentPage.canvas
            var currentY = margin

            val timestampStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())

            fun drawPageHeaderAndFooter(c: Canvas, pageNum: Int, isFirstPage: Boolean) {
                if (isFirstPage) {
                    c.drawRoundRect(
                        RectF(margin, margin, pageWidth - margin, margin + 88f),
                        10f,
                        10f,
                        headerBgPaint
                    )
                    c.drawRect(margin, margin + 84f, pageWidth - margin, margin + 88f, goldAccentPaint)
                    c.drawText("AR CREATOR • COUNSELING : FUN EDUCATION", margin + 16f, margin + 24f, brandPaint)
                    val safeTitle = if (documentTitle.length > 42) documentTitle.take(42) + "..." else documentTitle
                    c.drawText(safeTitle.ifBlank { "Academic Study Portfolio" }, margin + 16f, margin + 50f, titlePaint)
                    c.drawText(
                        "Author: ${studentAuthor.ifBlank { "Student" }}  |  Category: $academicCategory  |  $timestampStr",
                        margin + 16f,
                        margin + 72f,
                        metaPaint
                    )
                } else {
                    c.drawText(
                        "AR CREATOR • ${documentTitle.ifBlank { "Study Document" }}",
                        margin,
                        margin + 12f,
                        brandPaint
                    )
                    c.drawLine(margin, margin + 18f, pageWidth - margin, margin + 18f, borderPaint)
                }

                // Footer
                c.drawLine(margin, pageHeight - 38f, pageWidth - margin, pageHeight - 38f, borderPaint)
                c.drawText(
                    "Generated via Counseling : fun education • Created by Aditya Ranmale (AR CREATOR)",
                    margin,
                    pageHeight - 22f,
                    footerPaint
                )
                c.drawText(
                    "Page $pageNum",
                    pageWidth - margin - 45f,
                    pageHeight - 22f,
                    footerPaint
                )
            }

            drawPageHeaderAndFooter(canvas, currentPageNumber, isFirstPage = true)
            currentY = margin + 108f

            fun startNewPage() {
                pdfDocument.finishPage(currentPage)
                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                currentPage = pdfDocument.startPage(pageInfo)
                canvas = currentPage.canvas
                drawPageHeaderAndFooter(canvas, currentPageNumber, isFirstPage = false)
                currentY = margin + 36f
            }

            // Section 1: Structured Text Content
            canvas.drawText("I. STRUCTURED NOTES & TEXT ASSETS", margin, currentY, sectionHeaderPaint)
            currentY += 18f

            val paragraphs = rawTextContent.ifBlank {
                "No text content entered. Attached visual study assets follow below."
            }.split("\n")

            for (paragraph in paragraphs) {
                if (paragraph.isBlank()) {
                    currentY += 10f
                    continue
                }
                val words = paragraph.split(" ")
                var currentLine = StringBuilder()
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val measured = bodyPaint.measureText(testLine)
                    if (measured > contentWidth - 16f) {
                        if (currentY > pageHeight - 65f) {
                            startNewPage()
                        }
                        canvas.drawText(currentLine.toString(), margin + 6f, currentY, bodyPaint)
                        currentY += 16f
                        currentLine = StringBuilder(word)
                    } else {
                        currentLine = StringBuilder(testLine)
                    }
                }
                if (currentLine.isNotEmpty()) {
                    if (currentY > pageHeight - 65f) {
                        startNewPage()
                    }
                    canvas.drawText(currentLine.toString(), margin + 6f, currentY, bodyPaint)
                    currentY += 17f
                }
            }

            // Collect all bitmaps from Gallery URIs + Camera Captures
            val allBitmaps = mutableListOf<Bitmap>()
            allBitmaps.addAll(capturedBitmaps)
            for (uri in imageUris) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)?.let { bmp ->
                            allBitmaps.add(bmp)
                        }
                    }
                } catch (_: Exception) {}
            }

            if (allBitmaps.isNotEmpty()) {
                currentY += 16f
                if (currentY > pageHeight - 240f) {
                    startNewPage()
                }
                canvas.drawText(
                    "II. VISUAL STUDY ASSETS (${allBitmaps.size} ATTACHED IMAGE(S))",
                    margin,
                    currentY,
                    sectionHeaderPaint
                )
                currentY += 16f

                allBitmaps.forEachIndexed { idx, bmp ->
                    val maxImgWidth = contentWidth - 24f
                    val maxImgHeight = 220f
                    val ratio = minOf(
                        maxImgWidth / bmp.width.toFloat().coerceAtLeast(1f),
                        maxImgHeight / bmp.height.toFloat().coerceAtLeast(1f)
                    )
                    val drawW = bmp.width * ratio
                    val drawH = bmp.height * ratio

                    if (currentY + drawH + 40f > pageHeight - 50f) {
                        startNewPage()
                    }

                    val boxRect = RectF(margin, currentY, pageWidth - margin, currentY + drawH + 28f)
                    canvas.drawRoundRect(boxRect, 8f, 8f, boxPaint)
                    canvas.drawRoundRect(boxRect, 8f, 8f, borderPaint)

                    val leftOffset = margin + (contentWidth - drawW) / 2f
                    val destRect = RectF(leftOffset, currentY + 8f, leftOffset + drawW, currentY + 8f + drawH)
                    canvas.drawBitmap(bmp, null, destRect, null)
                    canvas.drawText(
                        "Figure ${idx + 1}: Embedded Visual Study Asset",
                        margin + 12f,
                        currentY + drawH + 22f,
                        footerPaint
                    )
                    currentY += drawH + 40f
                }
            }

            pdfDocument.finishPage(currentPage)

            // Export directly to Internal Device Storage (App Documents directory & filesDir fallback)
            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: File(context.filesDir, "ar_creator_pdfs")
            if (!baseDir.exists()) {
                baseDir.mkdirs()
            }
            val sanitizedTitle = documentTitle
                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                .take(30)
                .ifBlank { "AR_Creator_Study_Doc" }
            val fileName = "${sanitizedTitle}_${Calendar.getInstance().timeInMillis}.pdf"
            val outputFile = File(baseDir, fileName)

            FileOutputStream(outputFile).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            Result.success(
                GeneratedPdfInfo(
                    file = outputFile,
                    pageCount = currentPageNumber,
                    imageCount = allBitmaps.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun openOrSharePdf(context: Context, filePath: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) return
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(viewIntent, "Open or Share PDF Document").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (_: Exception) {
            // Fallback to share intent
            try {
                val file = File(filePath)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(
                    Intent.createChooser(shareIntent, "Share PDF").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } catch (_: Exception) {}
        }
    }

    fun copyPdfToUserUri(context: Context, sourceFilePath: String, targetUri: Uri): Boolean {
        return try {
            val src = File(sourceFilePath)
            if (!src.exists()) return false
            context.contentResolver.openOutputStream(targetUri)?.use { out ->
                src.inputStream().use { input ->
                    input.copyTo(out)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
