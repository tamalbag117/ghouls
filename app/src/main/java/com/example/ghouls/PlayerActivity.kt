package com.example.ghouls

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ghouls.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity() , ServiceConnection ,MediaPlayer.OnCompletionListener {



    companion object{
    lateinit  var musicListPA : ArrayList<Music>

    var songPosition : Int = 0
//        var mediaPlayer : MediaPlayer? = null

        var isPlaying : Boolean = false
        var musicService:MusicService? =null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding

        var repeat : Boolean = false

        var min30 : Boolean = false
        var min60 : Boolean = false
        var min180 : Boolean = false
        var nowPlayingId : String = ""
        var isFavourite : Boolean = false
        var fIndex : Int = -1
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // To start music Service

        val intent = Intent(this,MusicService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)



        initializeLayout()

        // back - button player activity
        binding.backButtonPA.setOnClickListener { finish() }

        // blinding for play- pause button

        binding.playpausebutton.setOnClickListener{
        if(isPlaying)pauseMusic()
            else playMusic()
        }

        binding.previousPlay.setOnClickListener{ palyNextPreviousSong(increment = false) }
            binding.nextplaybutton.setOnClickListener {  palyNextPreviousSong(increment = true)}


        // seekbar function add

        binding.appseekbar.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               if(fromUser){
                   musicService!!.mediaPlayer!!.seekTo(progress)
               }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) =Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })

        // repeat button feature

        binding.repeatbutton.setOnClickListener{
            if(!repeat){
                repeat = true
                binding.repeatbutton.setColorFilter(ContextCompat.getColor(this,R.color.touka_background))

            }else{
                repeat = false
                binding.repeatbutton.setColorFilter(ContextCompat.getColor(this,
                    R.color.purple_200))
            }
        }

        // Equalizer Button

        binding.equalizserbutton.setOnClickListener {

            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent,22)
            }catch (e :Exception){
                Toast.makeText(this,"Equalizer Feature not Supported ",Toast.LENGTH_SHORT).show()
            }

            binding.watchbutton.setOnClickListener {
                val timer = min30 || min60 || min180
                if(!timer){
                    showBottomSheetDialog()
                }else{
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Stop Timer")
                        .setMessage("Do you want to Stop Timer ?")
                        .setPositiveButton("yes"){ _, _ ->
                            min30 = false
                            min180 = false
                            min60 = false
                            binding.watchbutton.setColorFilter(ContextCompat.getColor(this,R.color.touka_background))
                        }
                        .setNegativeButton("No"){dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialogBuilder = builder.create()
                    customDialogBuilder.show()
                    customDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }

            }

        }

        // share file
        binding.sharebutton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Share Music"))



        }
        // binding favourite button

        binding.favouritebuttonPA.setOnClickListener {
            if(isFavourite){
                isFavourite = false
                binding.favouritebuttonPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                ActivityFavourite.favouriteSongs.removeAt(fIndex)
            }else{
                isFavourite = true
                binding.favouritebuttonPA.setImageResource(R.drawable.ic_baseline_favorite_24)
                ActivityFavourite.favouriteSongs.add(musicListPA[songPosition])
            }
        }
    }




    // set music layout

    private fun setLayout(){

        // favourite checker
        fIndex = favouriteChecker(musicListPA[songPosition].id)



        Glide.with(this)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.demo).centerCrop())
            .into(binding.songsImage)
        binding.currentSongname.text = musicListPA[songPosition].title
        if(repeat){
            repeat = true
            binding.repeatbutton.setColorFilter(ContextCompat.getColor(this,R.color.touka_background))
        }
        if(min30 || min60 || min180){
            binding.watchbutton.setColorFilter(ContextCompat.getColor(this, R.color.touka_background))
        }
        if (isFavourite) binding.favouritebuttonPA.setImageResource(R.drawable.ic_baseline_favorite_24)
        else binding.favouritebuttonPA.setImageResource(R.drawable.ic_baseline_favorite_border_24)


    }

    // to run current song
    private fun createMediaPlayer(){
        try {
            if(musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playpausebutton.setBackgroundResource(R.drawable.ic_baseline_pause_circle_filled_24)
            // change notification

            musicService!!.showNotification(R.drawable.ic_baseline_pause_circle_filled_24)

            // seekbar binding
            binding.prevtextseek.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.nexttextseek.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.appseekbar.progress = 0
            binding.appseekbar.max = musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener (this)

        }catch (e : Exception){
            return
        }


    }

    private  fun initializeLayout(){
        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){




//            "MusicAdapterSearch" -> {
//                musicListPA = ArrayList()
//                musicListPA.addAll(MainActivity.MusicListSearch)
//                setLayout()
//            }
            "ActivityFavourite" -> {
                musicListPA = ArrayList()
                musicListPA.addAll(ActivityFavourite.favouriteSongs)
                musicListPA.shuffle()
                setLayout()
            }

             "FavouriteAdapter" -> {
                 val intent = Intent(this,MusicService::class.java)
                 bindService(intent,this, BIND_AUTO_CREATE)
                 startService(intent)
                 musicListPA = ArrayList()
                 musicListPA.addAll(ActivityFavourite.favouriteSongs)
                 setLayout()
             }
            "MusicAdapter"  -> {
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
//                createMediaPlayer()
            }

            "MainActivity" ->{
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
//                createMediaPlayer()

            }
            "PlaylistDetails" ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist
                    .ref[PlaylistDetails.currentPlaylistPosition].playlist)
                setLayout()
            }
            "PlaylistDetailsShuffle" ->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(PlaylistActivity.musicPlaylist
                    .ref[PlaylistDetails.currentPlaylistPosition].playlist)
                musicListPA.shuffle()
                setLayout()

            }

        }
    }

    // playing music button
    private  fun playMusic(){

        binding.playpausebutton.setIconResource(R.drawable.ic_baseline_pause_circle_filled_24)
        musicService!!.showNotification(R.drawable.ic_baseline_pause_circle_filled_24)
        isPlaying =true
        musicService!!.mediaPlayer!!.start()
    }

    // pause music button

    private fun pauseMusic(){


        binding.playpausebutton.setIconResource(R.drawable.ic_baseline_play_circle_outline_24)
        musicService!!.showNotification(R.drawable.ic_baseline_play_circle_outline_24)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }


    // previous - next button
    private fun  palyNextPreviousSong(increment : Boolean ){


        if(increment){
            setSongPosition(increment=true)

         ++songPosition
         setLayout()
         createMediaPlayer()

        }else{
            setSongPosition(increment=false)
        --songPosition
        setLayout()
        createMediaPlayer()
        }




    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaPlayer()
        musicService!!.seekBarSetUp()



    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null

    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        }catch (e : Exception){
            return
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 22 ||  resultCode == RESULT_OK){
            return
        }
    }

    private fun showBottomSheetDialog(){
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop after 30 Minutes",
                Toast.LENGTH_SHORT).show()
            binding.watchbutton.setColorFilter(ContextCompat.getColor(this, R.color.touka_background))
            min30 = true
            Thread{Thread.sleep(30*60000)
            if (min30)
                exitApplication()
            }.start()

        dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop after One Hours",
                Toast.LENGTH_SHORT).show()
            binding.watchbutton.setColorFilter(ContextCompat.getColor(this, R.color.touka_background))
            min60 = true
            Thread{Thread.sleep(60*60000)
                if (min60)
                    exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min180)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop after Three hours",
                Toast.LENGTH_SHORT).show()
            binding.watchbutton.setColorFilter(ContextCompat.getColor(this, R.color.touka_background))
            min180 = true
            Thread{Thread.sleep(180*60000)
                if (min180)
                    exitApplication()
            }.start()
            dialog.dismiss()
        }
    }


}



