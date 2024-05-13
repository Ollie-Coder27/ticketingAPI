package com.example.homework4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class randomNotificationReciever : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            notificationService(context).createChannel()
            val event = eventViewModel().generateNotification(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                notificationService(context).showNotification(event[0], event[1])
            }
        }


    }


}