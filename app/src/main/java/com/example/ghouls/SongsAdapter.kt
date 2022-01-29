package com.example.ghouls

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ghouls.databinding.FrontSongViewBinding

class SongsAdapter(private val context: Context, private val musicList: ArrayList<Music>):RecyclerView.Adapter<SongsAdapter.MyHolder>() {
    class MyHolder (binding: FrontSongViewBinding):RecyclerView.ViewHolder(binding.root){
        val title = binding.songName
        val album = binding.songAlbum
        val image = binding.imageview
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsAdapter.MyHolder {
        return MyHolder(FrontSongViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: SongsAdapter.MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = musicList[position].duration.toString()
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


}