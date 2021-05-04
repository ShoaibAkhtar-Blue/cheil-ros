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
import com.example.cheilros.fragments.JourneyPlanData
import com.ramotion.foldingcell.FoldingCell

class JPAdapter(val context: Context, val itemList: List<JourneyPlanData>): RecyclerView.Adapter<JPAdapter.ViewHolder>()   {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtCode : TextView = view.findViewById(R.id.txtCode)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
        var txtRemarks : TextView = view.findViewById(R.id.txtRemarks)

        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.journy_plan_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtCode.text = itemList[position].StoreCode
        holder.txtTitle.text = itemList[position].StoreName
        holder.txtRemarks.text = itemList[position].VisitRemarks


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


/*
class JPAdapter(val context: Context, val itemList:ArrayList<String>): RecyclerView.Adapter<JPAdapter.ViewHolder>()   {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //var textView: TextView = view.findViewById(R.id.txtTitle)
        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)
        //var btnClose  : MaterialButton = view.findViewById(R.id.btnCancel)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.journy_plan_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        */
/*val text=itemList[position]
        holder.textView.text=text*//*

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
}*/
