package com.example.ghouls

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ghouls.databinding.FavouriteViewBinding
import com.example.ghouls.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PlaylistViewAdapter (private val context : Context, private var playlistList: ArrayList<PlayList>):
    RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>(){
    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root){
//        val title = binding.songName
//        val album =binding.songAlbum
//        val image = binding.imageview
//        val duration = binding.songDuration
//        val root = binding.root

        val image = binding.playlistImagePV
        val name = binding.playlistNamePV
        val root = binding.root
        val delete = binding.playlistDELbuttonPV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to delete this playlist ?")
                .setPositiveButton("yes"){ dialog, _ ->
                    PlaylistActivity.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialogBuilder = builder.create()
            customDialogBuilder.show()
            customDialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
        holder.root.setOnClickListener {
        val intent = Intent(context,PlaylistDetails::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)

        }
        if(PlaylistActivity.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistActivity.musicPlaylist.ref[position].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.catdog).centerCrop())
                .into(holder.image)
        }


    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun updateMusicList(searchList : ArrayList<Music>){
//        musicList = ArrayList()
//        musicList.addAll(searchList)
//        notifyDataSetChanged()
//    }

    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}