package com.example.ghouls

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {
    companion object{
        const val CHANNEL_ID = "channel6"
        const val PLAY = "play6"
        const val NEXT = "next6"
        const val PREVIOUS = "previous6"
        const val EXIT = "exit6"
    }
    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

        val notificationChannel = NotificationChannel(CHANNEL_ID,"Now Playing Song",
            NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "For showing Notification"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

}