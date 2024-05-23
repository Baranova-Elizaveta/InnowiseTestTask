package com.testtask.pexels.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.testtask.pexels.R
import com.testtask.pexels.models.WallpaperModel

class WallpaperAdapter(
    private val context: Context,
    private val wallpaperModelList: List<WallpaperModel>
) : RecyclerView.Adapter<WallpaperViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.wallpaper_item, parent, false)
        return WallpaperViewHolder(view)
    }

    override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
        val model = wallpaperModelList[position]

        Glide.with(context).load(model.mediumUrl).into(holder.imageView)
        holder.imageView.setOnClickListener {
            Toast.makeText(context, "clicked!!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return wallpaperModelList.size
    }
}

class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.imageViewItem)
    val photographerName: TextView = itemView.findViewById(R.id.photographerName)
}

