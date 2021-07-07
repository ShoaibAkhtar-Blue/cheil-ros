package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.StoreActiveAssetsFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.AssetListData


class AssetListAdapter(
    val context: Context,
    val itemList: List<AssetListData>,
    val fragment: StoreActiveAssetsFragment
) : RecyclerView.Adapter<AssetListAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLassets: LinearLayout = view.findViewById(R.id.LLassets)
        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtDesc: TextView = view.findViewById(R.id.txtDesc)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
        var txtUpdatedBy: TextView = view.findViewById(R.id.txtUpdatedBy)
        var imgAsset: ImageView = view.findViewById(R.id.imgAsset)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_assets, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtSerialNo.text = (position + 1).toString()
        holder.txtTitle.text = itemList[position].AssetTypeName
        holder.txtDesc.text = itemList[position].AssetDescription
        holder.txtDate.text = "Installation Date: ${itemList[position].CreationDateTime}"
        holder.txtUpdatedBy.text = "Updated By: ${itemList[position].TeamMemberName}"
        Glide.with(context)
            .load("${CSP.getData("base_url")}/Assets/${itemList[position].AssetID}.jpg")
            .into(holder.imgAsset!!)

        holder.LLassets.setOnClickListener {
            fragment.addAsset(itemList[position])
        }
    }



    override fun getItemCount(): Int {
        return itemList.size
    }
}