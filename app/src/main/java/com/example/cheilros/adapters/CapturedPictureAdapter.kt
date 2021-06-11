package com.example.cheilros.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R

class CapturedPictureAdapter(val context: Context, val itemList: MutableList<String>): RecyclerView.Adapter<CapturedPictureAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCaptured: ImageView = view.findViewById(R.id.imgCaptured)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CapturedPictureAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_capture_picture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CapturedPictureAdapter.ViewHolder, position: Int) {
        Glide.with(context).load(itemList[position]).into(holder.imgCaptured!!)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun addNewItem(itemsNew: String?){
        itemList.add(itemsNew.toString())
        notifyDataSetChanged()
    }
}