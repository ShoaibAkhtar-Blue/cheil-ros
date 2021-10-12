package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.TrainingSummaryData

class TrainingSummaryAdapter(val context: Context, val itemList: List<TrainingSummaryData>, val arguments: Bundle?): RecyclerView.Adapter<TrainingSummaryAdapter.ViewHolder>(){

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<TrainingSummaryData>()

    init {
        filterList = itemList as ArrayList<TrainingSummaryData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtType : TextView = view.findViewById(R.id.txtType)
        var txtDate : TextView = view.findViewById(R.id.txtDate)
        var txtStart : TextView = view.findViewById(R.id.txtStart)
        var txtEnd : TextView = view.findViewById(R.id.txtEnd)
        var txtAttendees : TextView = view.findViewById(R.id.txtAttendees)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(context).inflate(R.layout.list_training_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtType.text = filterList[position].TrainingTypeName.take(6)
        holder.txtDate.text = filterList[position].TrainingDate
        holder.txtStart.text = filterList[position].Start
        holder.txtEnd.text = filterList[position].EndTime
        holder.txtAttendees.text = filterList[position].Attendese.toString()
    }

    override fun getItemCount(): Int {
        return filterList.size
    }


}