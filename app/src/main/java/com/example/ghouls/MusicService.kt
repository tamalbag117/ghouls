package com.example.ghouls

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import java.lang.Exception

class MusicService : Service() {

    private  var myBinder = MyBinder()
    var mediaPlayer :MediaPlayer?=null
    private  lateinit var  mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable



    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"ish")
        return myBinder

    }
    inner class MyBinder : Binder(){
        fun currentService(): MusicService {
            return this@MusicService

        }
    }


    @SuppressLint("UnspecifiedImmutableFlag", "PrivateResource")
    fun showNotification(PlayPauseButton:Int){

        // handle app form notification or to reach application

        val intent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, 0)


        // for previous intent
        val prevIntent = Intent(baseContext,
            NotificationReceiver::class.java).setAction((ApplicationClass.PREVIOUS))

        val prevPendingIntent = PendingIntent.
        getBroadcast(baseContext,0,prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // for playintent
        val playIntent = Intent(baseContext,
            NotificationReceiver::class.java).setAction((ApplicationClass.PLAY))

        val playPendingIntent = PendingIntent.
        getBroadcast(baseContext,0,playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // for next intent

        val nextIntent = Intent(baseContext,
            NotificationReceiver::class.java).setAction((ApplicationClass.NEXT))

        val nextPendingIntent = PendingIntent.
        getBroadcast(baseContext,0,nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // for exit intent

        val exitIntent = Intent(baseContext,
            NotificationReceiver::class.java).setAction((ApplicationClass.NEXT))

        val exitPendingIntent = PendingIntent.
        getBroadcast(baseContext,0,exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)


       // add image to notification bar


        val imageArt = getImageArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)

        val image = if(imageArt != null){
            BitmapFactory.decodeByteArray(imageArt,0,imageArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_screen_icon)
        }


        // for notification
        val notification = NotificationCompat.Builder(baseContext,
        ApplicationClass.CHANNEL_ID).setContentIntent(contentIntent)
        .setContentTitle((PlayerActivity.musicListPA[PlayerActivity.songPosition].title))
        .setContentText((PlayerActivity.musicListPA[PlayerActivity.songPosition].artist))
        .setSmallIcon(R.drawable.music_screen_icon)
        .setLargeIcon(image)
        .setStyle(androidx.media.app.NotificationCompat.MediaStyle().
        setMediaSession(mediaSession.sessionToken))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setOnlyAlertOnce(true)
        .addAction(com.google.android.material.R.drawable.material_ic_keyboard_arrow_previous_black_24dp,
            "previous",prevPendingIntent)
        .addAction(PlayPauseButton,"play",playPendingIntent)
        .addAction(com.google.android.material.R.drawable.material_ic_keyboard_arrow_next_black_24dp,
            "next",nextPendingIntent)
        .addAction(R.drawable.ic_baseline_exit_to_app_24,"exit",exitPendingIntent)
        .build()


        startForeground(13,notification)

    }

    fun createMediaPlayer(){
        try {
            if(PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.playpausebutton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)
            // change notification

            PlayerActivity.musicService!!.showNotification(R.drawable.ic_baseline_pause_circle_filled_24)
            PlayerActivity.binding.prevtextseek.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.nexttextseek.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.appseekbar.progress = 0
            PlayerActivity.binding.appseekbar.max = PlayerActivity.musicService!!.mediaPlayer!!.duration
        }catch (e : Exception){
            return
        }


    }

    fun seekBarSetUp(){
     runnable = Runnable {
         PlayerActivity.binding.prevtextseek.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
         PlayerActivity.binding.appseekbar.progress = mediaPlayer!!.currentPosition
         Handler(Looper.getMainLooper()).postDelayed(runnable,200)
     }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }
}

