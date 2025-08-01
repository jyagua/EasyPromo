package com.ufc.easypromo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ufc.easypromo.util.NotificationHelper

class ScheduledNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Notificação"
        val message = intent?.getStringExtra("message") ?: "Mensagem agendada"
        NotificationHelper.sendCustomNotification(context, title, message)
    }
}

//annotation class ScheduledNotificationReceiver
