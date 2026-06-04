package com.mycelengan

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

private const val TARGET_REMINDER_CHANNEL_ID = "target_reminder_channel"
private const val TARGET_REMINDER_NOTIFICATION_ID = 1201
private const val TARGET_REMINDER_REQUEST_CODE = 1201

class TargetReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        TargetReminderScheduler.showReminder(context)
    }
}

object TargetReminderScheduler {
    fun scheduleDaily(context: Context) {
        createChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextReminderMillis(),
            AlarmManager.INTERVAL_DAY,
            reminderIntent(context)
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(reminderIntent(context))
    }

    fun showReminder(context: Context) {
        createChannel(context)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(context, TARGET_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentTitle("kamu lupa menabung")
            .setContentText("jangan lupa menabung")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("jangan lupa menabung")
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context)
            .notify(TARGET_REMINDER_NOTIFICATION_ID, notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            TARGET_REMINDER_CHANNEL_ID,
            "Pengingat Target",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Pengingat agar tidak lupa menabung ke target."
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun reminderIntent(context: Context): PendingIntent {
        val intent = Intent(context, TargetReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            TARGET_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextReminderMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis
    }
}
