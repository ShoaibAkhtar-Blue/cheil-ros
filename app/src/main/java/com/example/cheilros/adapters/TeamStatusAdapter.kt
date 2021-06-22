package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.MyCoverageData
import com.example.cheilros.models.TeamStatusData
import kotlinx.android.synthetic.main.activity_dashboard.view.*

class TeamStatusAdapter(
    val context: Context,
    val itemList: List<TeamStatusData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<TeamStatusAdapter.ViewHolder>(), Filterable {

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<TeamStatusData>()

    init {
        filterList = itemList as ArrayList<TeamStatusData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLteamstatus: LinearLayout = view.findViewById(R.id.LLteamstatus)
        var imgUser: ImageView = view.findViewById(R.id.imgUser)
        var txtUserName: TextView = view.findViewById(R.id.txtUserName)
        var txtTeamType: TextView = view.findViewById(R.id.txtTeamType)
        var txtTime: TextView = view.findViewById(R.id.txtTime)
        var txtStoreName: TextView = view.findViewById(R.id.txtStoreName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_team_status, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load("${CSP.getData("base_url")}/TeamMemberPicture/${CSP.getData("user_id")}.png")
            .into(holder.imgUser)

        holder.txtUserName.text = filterList[position].TeamMemberName
        holder.txtTeamType.text = filterList[position].TeamTypeName
        holder.txtTime.text = "${filterList[position].CheckInTime}-${filterList[position].CheckOutTime}"
        holder.txtStoreName.text = filterList[position].StoreName


        holder.LLteamstatus.setOnClickListener {
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<TeamStatusData>
                } else {
                    val resultList = ArrayList<TeamStatusData>()
                    for (row in itemList) {
                        if (row.TeamMemberName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<TeamStatusData>
                notifyDataSetChanged()
            }
        }
    }
}