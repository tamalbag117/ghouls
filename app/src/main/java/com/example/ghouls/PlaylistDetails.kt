package com.example.ghouls

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ghouls.databinding.ActivityPlaylistDetailsBinding

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding : ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlaylistPosition : Int = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =  ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPosition = intent.extras?.get("Index") as Int
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        PlaylistActivity.musicPlaylist
            .ref[currentPlaylistPosition].playlist.addAll(MainActivity.MusicListMA)
        PlaylistActivity.musicPlaylist
            .ref[currentPlaylistPosition].playlist.shuffle()
        adapter = MusicAdapter(this,PlaylistActivity.musicPlaylist
            .ref[currentPlaylistPosition].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter

        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","PlaylistDetailsShuffle")
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = PlaylistActivity.musicPlaylist.ref[currentPlaylistPosition].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Songs.\n\n" +
                "Created On :\n ${PlaylistActivity.musicPlaylist
                    .ref[currentPlaylistPosition].createdOn}\n\n" +
                " - > ${PlaylistActivity.musicPlaylist
                    .ref[currentPlaylistPosition].cretedBy}"

        if(adapter.itemCount>0){
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPosition].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.catdog).centerCrop())
                .into(binding.playlistImgPD)

            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
    }
}