package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.data.UserData
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.TrainingTypesData

class TrainingTypeAdapter(
    val context: Context,
    val itemList: MutableList<TrainingTypesData>,
    val arguments: Bundle?,
    val activity: NewDashboardActivity,
    val userData: List<UserData>
) : RecyclerView.Adapter<TrainingTypeAdapter.ViewHolder>() {
    lateinit var CSP: CustomSharedPref


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLrecentactivity: LinearLayout = view.findViewById(R.id.LLrecentactivity)

        var ActivityTypeName: TextView = view.findViewById(R.id.ActivityTypeName)
        var ActivityDescription: TextView = view.findViewById(R.id.ActivityDescription)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
        var RLimgActivity: RelativeLayout = view.findViewById(R.id.RLimgActivity)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingTypeAdapter.ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_recent_activities, parent, false)
        return TrainingTypeAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ActivityDescription.visibility = View.GONE
        holder.txtDate.visibility = View.GONE
        holder.RLimgActivity.visibility = View.GONE

        holder.ActivityTypeName.text = itemList[position].TrainingTypeName

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}