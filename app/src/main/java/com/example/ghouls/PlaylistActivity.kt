package com.example.ghouls

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ghouls.databinding.ActivityPlaylistBinding
import com.example.ghouls.databinding.AddPlaylistdialogueBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding

    private lateinit var adapter : PlaylistViewAdapter

    companion object{
        var musicPlaylist : MusicPlaylist = MusicPlaylist()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recycleviewAPlaylist.setHasFixedSize(true)
        binding.recycleviewAPlaylist.setItemViewCacheSize(20)
        binding.recycleviewAPlaylist.layoutManager = GridLayoutManager(
            this@PlaylistActivity,2)
        adapter = PlaylistViewAdapter(this, playlistList = musicPlaylist.ref)
        binding.recycleviewAPlaylist.adapter = adapter
        binding.bckbuttonplaylist.setOnClickListener { finish() }
        binding.addplaylistbuttonAP.setOnClickListener {
            customAlertDialogue()
        }
    }

    private fun customAlertDialogue(){
        val customDialogue = LayoutInflater.from(this@PlaylistActivity)
            .inflate(R.layout.add_playlistdialogue,binding.root,false)

        val binder = AddPlaylistdialogueBinding.bind(customDialogue)
        val builder = MaterialAlertDialogBuilder(this)

        builder.setView(customDialogue).setTitle("Playlist Details")
            .setMessage("Do you want to close this application ?")
            .setPositiveButton("Add"){ dialog, _ ->
                val playlistName = binder.playlistName.text
                val createdBy = binder.yourName.text

                if(playlistName!=null && createdBy!=null){
                    if(playlistName.isNotBlank()&& createdBy.isNotEmpty()){
                        addPlaylist(playlistName.toString(),createdBy.toString())
                    }
                }


                dialog.dismiss()
            }.show()


    }

    private fun addPlaylist(name: String, createdBy: String) {
    var playlistExist = false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)){
                playlistExist = true
                break
            }
        }
        if (playlistExist){
            Toast.makeText(this,"Playlist Exists ",Toast.LENGTH_SHORT).show()

        }else {
            val tempPlaylist = PlayList()
            tempPlaylist.name = name
            tempPlaylist.playlist = ArrayList()
            tempPlaylist.cretedBy = createdBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH)

            tempPlaylist.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()
        }
    }
}