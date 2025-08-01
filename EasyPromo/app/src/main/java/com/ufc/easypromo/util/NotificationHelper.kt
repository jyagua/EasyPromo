package com.ufc.easypromo.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ufc.easypromo.R
import com.ufc.easypromo.models.Product
import com.ufc.easypromo.receiver.ScheduledNotificationReceiver

object NotificationHelper {
    const val HIGH_PRIORITY_CHANNEL_ID = "high_priority_channel"
    const val MEDIUM_PRIORITY_CHANNEL_ID = "medium_priority_channel"
    const val LOW_PRIORITY_CHANNEL_ID = "low_priority_channel"

    fun createNotificationChannels(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .build()

        val highPriorityChannel = NotificationChannel(
            HIGH_PRIORITY_CHANNEL_ID,
            "Alertas de Desconto",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações para descontos importantes."
            enableLights(true)
            lightColor = ContextCompat.getColor(context, R.color.purple_500)
            enableVibration(true)
            setSound(
                android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                audioAttributes
            )
        }
        notificationManager.createNotificationChannel(highPriorityChannel)

        val mediumPriorityChannel = NotificationChannel(
            MEDIUM_PRIORITY_CHANNEL_ID,
            "Notificações Gerais",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificações gerais do aplicativo."
            enableVibration(true)
            setSound(
                android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                audioAttributes
            )
        }
        notificationManager.createNotificationChannel(mediumPriorityChannel)

        val lowPriorityChannel = NotificationChannel(
            LOW_PRIORITY_CHANNEL_ID,
            "Notificações Opcionais",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificações menos importantes."
            setSound(null, null)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(lowPriorityChannel)
    }

    fun sendPriceDropNotification(context: Context, product: Product) {
        createNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, HIGH_PRIORITY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Desconto em favorito!")
            .setContentText("${product.name} está com desconto: R$%.2f".format(product.price))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify(product.id.toInt(), builder.build()) // Cast Long to Int
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleCustomNotification(
        context: Context,
        title: String,
        message: String,
        triggerAtMillis: Long,
        requestCode: Int = 12345
    ) {
        createNotificationChannels(context)
        val intent = Intent(context, ScheduledNotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun sendCustomNotification(context: Context, title: String, message: String) {
        createNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, HIGH_PRIORITY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun schedulePriceDropAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduledNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val intervalMillis = 6 * 60 * 60 * 1000L // 6 hours
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            intervalMillis,
            pendingIntent
        )
    }
}