package com.example.ghouls

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ghouls.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {


    private  lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter : MusicAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<Music>
        var search: Boolean = false

    }




    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        requestRunTimePermissionFromUser()
        setTheme(R.style.Theme_Ghouls)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Drawer
        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open_va,R.string.close_va)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRunTimePermissionFromUser()) {
            initializeLayout()

            // for retrieve favourite song data from json file
            ActivityFavourite.favouriteSongs = ArrayList()
            val editor =getSharedPreferences("FAVOURITE", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs",null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
           if(jsonString!=null){
               val dataFetch : ArrayList<Music> = GsonBuilder().create().fromJson(jsonString,typeToken)
               ActivityFavourite.favouriteSongs.addAll(dataFetch)
           }



        }


        // Set Music Adapter Functionality
//        val musicList = ArrayList<String>()
//        musicList.add("First Song")
//        musicList.add("Second Song")
//        musicList.add("Third Song")
//        musicList.add("Fourth Song")
//        musicList.add("Fifth Song")
//        musicList.add("Sixth Song")
//        musicList.add("Seventh Song")
//        musicList.add("Eight Song")
//        musicList.add("Nine Song")








        // main screen buttons

        binding.shuffleButton.setOnClickListener {
            val intent = Intent(this@MainActivity,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)
        }
        binding.favouriteButton.setOnClickListener {
            val intent = Intent(this@MainActivity,ActivityFavourite::class.java)
            startActivity(intent)
        }
        binding.playlistButton.setOnClickListener {
            val intent = Intent(this@MainActivity,PlaylistActivity::class.java)
            startActivity(intent)
        }

        // initialize navigation bar



        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_feedback -> Toast.makeText(baseContext,"feedback",Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(baseContext,"settings",Toast.LENGTH_SHORT).show()
                R.id.nav_about -> Toast.makeText(baseContext,"about",Toast.LENGTH_SHORT).show()
                R.id.nav_exitButton->
                {
                   val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close this application ?")
                        .setPositiveButton("yes"){ _, _ ->
                           exitApplication()
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
            true
        }
    }



    // requesting permission

    private  fun requestRunTimePermissionFromUser():Boolean{
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),14)
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==14 ){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this,"permission Granted",Toast.LENGTH_SHORT).show()
                initializeLayout()

            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),14)
            }
            }
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

//    @SuppressLint("Range")
//


    // return all audio file from storage to music player
//    @SuppressLint("Range")
//    private fun getAllAudioFile():ArrayList<Music>{
//    val tempList = ArrayList<Music> ()
//
//    val selection  = MediaStore.Audio.Media.IS_MUSIC+ "!=0"
//        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
//            MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
//            MediaStore.Audio.Media.DATA)
//        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//            projection,selection,null,MediaStore.Audio.Media.DATE_ADDED+"DESC",
//            null)
//
//        if(cursor!=null){
//            if(cursor.moveToFirst()){
//                do {
//                 val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
//                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
//                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
//                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
//                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
//
//                    // custom object
//                    val music = Music(id = idC,title=titleC, album = albumC,
//                        artist = artistC, path = pathC, duration = durationC)
//
//                    val file = File(music.path)
//                    if(file.exists()){
//                        tempList.add(music)
//                    }
//                }while (cursor.moveToNext())
//                cursor.close()
//            }
//        }
//
//        return tempList
//    }


    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio():ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection  = MediaStore.Audio.Media.IS_MUSIC +  " !=0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,selection,null,MediaStore.Audio.Media.DATE_ADDED + " DESC",
            null)
       // is cursor null !?
        if(cursor!=null){
            if(cursor.moveToFirst()){
                do {
                 val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    // custom object
                    val music = Music(id = idC,title=titleC, album = albumC,
                        artist = artistC, path = pathC, duration = durationC,
                    artUri = artUriC)

                    val file = File(music.path)
                    if(file.exists()){
                        tempList.add(music)
                    }
                }while (cursor.moveToNext())
                cursor.close()
            }
        }





        return tempList

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeLayout(){


        MusicListMA = getAllAudio()


        binding.musicRecycleView.setHasFixedSize(true)
        binding.musicRecycleView.setItemViewCacheSize(20)
        binding.musicRecycleView.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRecycleView.adapter = musicAdapter

        // total; songs count
        binding.totalsongsMain.text = "Total Songs :"+musicAdapter.itemCount
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService!= null){
            // close application !!!

          exitApplication()
        }


    }

    override fun onResume() {
        super.onResume()

            // For storing favourite data with reference in json file
            val editor =getSharedPreferences("FAVOURITE", MODE_PRIVATE).edit()
            val jsonString = GsonBuilder().create().toJson(ActivityFavourite.favouriteSongs)
            editor.putString("FavouriteSongs",jsonString)
            editor.apply()
    }

    }