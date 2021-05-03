package com.example.cheilros.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.MyCoverageData
import com.ramotion.foldingcell.FoldingCell

class MyCoverageAdapter(val context: Context, val itemList:List<MyCoverageData>): RecyclerView.Adapter<MyCoverageAdapter.ViewHolder>()   {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtRegion: TextView = view.findViewById(R.id.txtRegion)
        var txtAddress: TextView = view.findViewById(R.id.txtAddress)
        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)
        //var btnClose  : MaterialButton = view.findViewById(R.id.btnCancel)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.mycoverage_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtSerialNo.text = (position+1).toString()
        holder.txtTitle.text = itemList[position].StoreName
        holder.txtRegion.text = itemList[position].RegionName
        holder.txtAddress.text = itemList[position].Address

        holder.btnSee.setOnClickListener {
            holder.fc.toggle(false)
        }

        holder.btnClose.setOnClickListener {
            holder.fc.toggle(false)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}