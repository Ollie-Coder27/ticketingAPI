package com.example.homework4

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log.i
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import java.util.Calendar
import java.util.Random

class notificationService(context: Context){

    private val con = context
    private val CHANNEL_ID = "R.T.M.N.C"
    private val notificationName = "Random TicketMaster Notification"
    private val descriptionText = "TicketMaster Knock-off Notification"

    @RequiresApi(Build.VERSION_CODES.S)
    fun showNotification(title: String, text: String){
        val notificationManager = con.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(con.applicationContext, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(con.applicationContext, 0, notificationIntent, flags)
        val notificationBuilder =
            NotificationCompat.Builder(con,CHANNEL_ID)
                .setSmallIcon(R.drawable.tmlogo)
                .setContentTitle(title)
                .setContentText("Check out your latest views!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setStyle(NotificationCompat.BigTextStyle()
                          .bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val notificationId = Random().nextInt()
        notificationManager.notify(notificationId, notificationBuilder.build())

        // automatic random notification call
        scheduleRandomNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(){
        val channel = NotificationChannel(CHANNEL_ID, notificationName, NotificationManager.IMPORTANCE_DEFAULT).apply { description = descriptionText }
        val notificationManager = con.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    //Reference : https://www.geeksforgeeks.org/how-to-build-a-simple-alarm-setter-app-in-android/ - alarm manager ideas and basic needs
    /**
     * TODO - done 05.01.2024
     * Get a random hour and minute that will be when the notification is sent - done
     * create a calendar instance to hold the time - done
     * create an intent that opens the reciever class (which will then then the notification) - done
     * create a pending intent that will hold regular intent until the alarm is triggered - done
     * set an inexact alarm that will wake up the intent to then call the notification items - done
     */
    private fun scheduleRandomNotification(){
        //set up alarm service to get random notification
        val alarmManager = con.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val random = Random()
        val calendar = Calendar.getInstance()
        val numNotifications = random.nextInt(5)

        //through out the day get this number of notifications
        for (i in 0 until numNotifications){

            //Set time to random hour and also make the minutes at every :10 so 00, 10, 20, 30, 40, 50
            val hour = random.nextInt(12) + 1
            val min = random.nextInt(5) * 10

            //set that time in a calendar instance
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)
            calendar.set(Calendar.SECOND, 0)

            i("NOTIFICATION TIME", "$hour : $min")

            //create an intent calling the notification reciever to then create a new notification
            val intent = Intent(con, randomNotificationReciever::class.java)

            //pending intent with 2 flags
            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            val pendingIntent = PendingIntent.getBroadcast(con, i,intent, flags)

            //set a repeating, inexact alarm that wakes the program, has a range between these times, and triggers this pending intent
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,calendar.timeInMillis.plus(10), pendingIntent)
        }

    }

}
