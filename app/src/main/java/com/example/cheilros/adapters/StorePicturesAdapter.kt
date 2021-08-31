package com.example.cheilros.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.GeneralPicturesData

class StorePicturesAdapter(val ctx: Context?, var titles: List<GeneralPicturesData>) :
    RecyclerView.Adapter<StorePicturesAdapter.ViewHolder>() {
    var inflater: LayoutInflater
    lateinit var CSP: CustomSharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.list_store_pictures, parent, false)
        CSP = ctx?.let { CustomSharedPref(it) }!!
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = titles[position].StorePictureElementName

        if (ctx != null) {
            Glide.with(ctx)
                .load("${CSP.getData("base_url")}/StoreGeneralPictures/${titles[position].PictureID}.png")
                .into(holder.imgStore)
        }
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.txtTitle)
        var imgStore: ImageView = itemView.findViewById(R.id.imgStore)

        init {
            itemView.setOnClickListener { v ->
                Toast.makeText(
                    v.context,
                    "Clicked -> $adapterPosition",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    init {
        inflater = LayoutInflater.from(ctx)
    }
}