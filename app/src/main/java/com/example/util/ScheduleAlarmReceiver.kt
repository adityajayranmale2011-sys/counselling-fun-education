package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Scheduled Block"
        val description = intent.getStringExtra("EXTRA_DESCRIPTION") ?: "Time to begin your scheduled activity."
        val metaHeader = intent.getStringExtra("EXTRA_META_HEADER") ?: "STUDY TIME"
        val audioSource = intent.getStringExtra("EXTRA_AUDIO_SOURCE") ?: "SYSTEM_ALARM"
        val customUri = intent.getStringExtra("EXTRA_CUSTOM_URI") ?: ""

        when (action) {
            AlarmAndPdfEngine.ACTION_STAGE_1_PUSH -> {
                AlarmAndPdfEngine.postTargetedPushNotification(
                    context = context,
                    metaHeader = metaHeader,
                    title = title,
                    description = description
                )
            }
            AlarmAndPdfEngine.ACTION_STAGE_2_HARDWARE_ALARM -> {
                AlarmAndPdfEngine.firePersistentHardwareAlarm(
                    context = context,
                    metaHeader = metaHeader,
                    title = title,
                    description = description,
                    audioSource = audioSource,
                    customAudioUri = customUri
                )
            }
            AlarmAndPdfEngine.ACTION_DISMISS_HARDWARE_ALARM -> {
                AlarmAndPdfEngine.stopPersistentHardwareAlarm(context)
            }
        }
    }
}
