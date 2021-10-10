package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.RecentTrainingModelData

class RecentTrainingAdapter(
    val context: Context,
    val itemList: List<RecentTrainingModelData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<RecentTrainingAdapter.ViewHolder>(){

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<RecentTrainingModelData>()

    init {
        filterList = itemList as ArrayList<RecentTrainingModelData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLrecenttraining: LinearLayout = view.findViewById(R.id.LLrecenttraining)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtNofAttendee: TextView = view.findViewById(R.id.txtNofAttendee)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_recent_training, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = filterList[position].TrainingModelTitle
        holder.txtNofAttendee.text = "Number of Attendee: ${filterList[position].Attendees}"
        holder.txtDate.text = filterList[position].TrainingDateTime
        holder.LLrecenttraining.setOnClickListener {
            /*val bundle = bundleOf(
                "DivisionID" to arguments?.getInt("DivisionID"),
                "TrainingModelID" to itemList[position].TrainingModelID,
                "TrainingModelTitle" to itemList[position].TrainingModelTitle,
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )
            Navigation.findNavController(it)
                .navigate(R.id.action_trainingFragment_to_trainingDetailFragment, bundle)*/
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun getItemViewType(position: Int): Int = position
}