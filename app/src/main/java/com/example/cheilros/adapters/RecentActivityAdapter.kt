package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.RecentActivityData

class RecentActivityAdapter(
    val context: Context,
    val itemList: List<RecentActivityData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<RecentActivityAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<RecentActivityData>()

    init {
        filterList = itemList as ArrayList<RecentActivityData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLrecentactivity: LinearLayout = view.findViewById(R.id.LLrecentactivity)
        var RLrecentactivity: RelativeLayout = view.findViewById(R.id.RLrecentactivity)
        var ActivityTypeName: TextView = view.findViewById(R.id.ActivityTypeName)
        var ActivityDescription: TextView = view.findViewById(R.id.ActivityDescription)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_recent_activities, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ActivityTypeName.text = filterList[position].ActivityTypeName
        holder.ActivityDescription.text = filterList[position].ActivityDescription
        holder.txtDate.text = filterList[position].ActivityDateTime

        if(filterList[position].ActivityTypeID == 20)
            holder.RLrecentactivity.setBackgroundColor(Color.RED)
        else if(filterList[position].ActivityTypeID > 20)
            holder.RLrecentactivity.setBackgroundColor(Color.parseColor("#ffa500"))

        holder.LLrecentactivity.setOnClickListener {
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
}
