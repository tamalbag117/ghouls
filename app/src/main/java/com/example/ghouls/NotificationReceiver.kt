package com.example.ghouls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       when(intent?.action){
           ApplicationClass.PREVIOUS ->{
//               Toast.makeText(context,"previous click",Toast.LENGTH_SHORT).show()
               prevNextSong(increment = false,context= context!!)
           }
           ApplicationClass.NEXT ->{
//               Toast.makeText(context,"next click",Toast.LENGTH_SHORT).show()
               prevNextSong(increment = true,context=context!!)
           }
           ApplicationClass.EXIT ->{
               exitApplication()

           }
           ApplicationClass.PLAY ->{
//               Toast.makeText(context,"play click",Toast.LENGTH_SHORT).show()
               if(PlayerActivity.isPlaying){
                   pauseMusic()
               }else{
                   playMusic()
               }
           }
       }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.
        showNotification(R.drawable.ic_baseline_pause_circle_filled_24)
        PlayerActivity.binding.playpausebutton.setIconResource(
            R.drawable.ic_baseline_pause_circle_filled_24
        )

    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.
        showNotification(R.drawable.ic_baseline_play_circle_outline_24)
        PlayerActivity.binding.playpausebutton.setIconResource(
            R.drawable.ic_baseline_play_circle_outline_24
        )

    }


    private fun prevNextSong(increment : Boolean,context : Context){
     setSongPosition(increment = increment)
       PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.demo).centerCrop())
            .into(PlayerActivity.binding.songsImage)
        PlayerActivity.binding.currentSongname.text = PlayerActivity.musicListPA[PlayerActivity
            .songPosition].title

        playMusic()
        PlayerActivity.fIndex = favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFavourite ){
            PlayerActivity.binding.favouritebuttonPA.setImageResource(R.drawable.ic_baseline_favorite_24)
        }else{
            PlayerActivity.binding.favouritebuttonPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }


    }

}