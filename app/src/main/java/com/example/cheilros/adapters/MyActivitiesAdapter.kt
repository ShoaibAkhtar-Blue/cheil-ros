package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.MyActivitiesData

class MyActivitiesAdapter(
    val context: Context,
    val itemList: List<MyActivitiesData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<MyActivitiesAdapter.ViewHolder>() {
    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<MyActivitiesData>()

    init {
        filterList = itemList as ArrayList<MyActivitiesData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgActivity: ImageView = view.findViewById(R.id.imgActivity)
        var ActivityTypeName: TextView = view.findViewById(R.id.ActivityTypeName)
        var ActivityDescription: TextView = view.findViewById(R.id.ActivityDescription)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_my_activities, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load("${CSP.getData("base_url")}/${filterList[position].ImageActivity}")
            .into(holder.imgActivity)
        holder.ActivityTypeName.text = filterList[position].ActivityTypeName
        holder.ActivityTypeName.text = filterList[position].ActivityTypeName
        holder.txtDate.text = filterList[position].ActivityDateTime
    }

    override fun getItemCount(): Int {
        return filterList.size
    }
}