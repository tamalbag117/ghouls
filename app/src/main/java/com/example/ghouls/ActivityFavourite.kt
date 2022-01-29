package com.example.ghouls

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ghouls.databinding.ActivityFavouriteBinding

class ActivityFavourite : AppCompatActivity() {


    private lateinit var binding: ActivityFavouriteBinding

    private lateinit var adapter : FavouriteAdapter

    companion object{
        var favouriteSongs : ArrayList<Music> = ArrayList()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val templist = ArrayList<String>()
//        templist.add("song 1")
//        templist.add("song 2")
//        templist.add("song 3")
//        templist.add("song 4")
//        templist.add("song 5")
//        templist.add("song 6")
//        templist.add("song 7")
//        templist.add("song 8")
//        templist.add("song 9")
//        templist.add("song 10")







        binding.backbuttonFavactivity.setOnClickListener { finish() }


        binding.favouriterecycleview.setHasFixedSize(true)
        binding.favouriterecycleview.setItemViewCacheSize(20)
        binding.favouriterecycleview.layoutManager = GridLayoutManager(this,4)
        adapter = FavouriteAdapter(this, favouriteSongs)
        binding.favouriterecycleview.adapter = adapter

        // shuffle button
        if(favouriteSongs.size<1){
            binding.shuffleButtonFavouritsA.visibility = View.INVISIBLE
        }
        binding.shuffleButtonFavouritsA.setOnClickListener {
            val intent = Intent(this,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","ActivityFavourite")
            startActivity(intent)
        }

    }
}