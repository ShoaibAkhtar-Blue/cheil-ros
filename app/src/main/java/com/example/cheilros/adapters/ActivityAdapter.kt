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
import com.example.cheilros.models.ActivityTypeData
import com.example.cheilros.models.CheckListData
import com.example.cheilros.models.MyCoverageData

class ActivityAdapter(
    val context: Context,
    val itemList: List<ActivityTypeData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>(),
    Filterable {

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<ActivityTypeData>()

    init {
        filterList = itemList as ArrayList<ActivityTypeData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLchecklist: LinearLayout = view.findViewById(R.id.LLchecklist)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityAdapter.ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_checklist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityAdapter.ViewHolder, position: Int) {
        holder.txtTitle.text = filterList[position].ActivityTypeName
        holder.LLchecklist.setOnClickListener {

//            if(CSP.getData("user_id")!!.toInt() <= 4){
//                Toast.makeText(context, "You don't have permission", Toast.LENGTH_SHORT).show()
//            }else{
                val bundle = bundleOf(
                    "DivisionID" to arguments?.getInt("DivisionID"),
                    "ActivityTypeID" to itemList[position].ActivityTypeID,
                    "ActivityTypeName" to itemList[position].ActivityTypeName,
                    "StoreID" to arguments?.getInt("StoreID"),
                    "StoreName" to arguments?.getString("StoreName")
                )
                Navigation.findNavController(it)
                    .navigate(R.id.action_activityFragment_to_activityDetailFragment, bundle)
            //}
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
                    filterList = itemList as ArrayList<ActivityTypeData>
                } else {
                    val resultList = ArrayList<ActivityTypeData>()
                    for (row in itemList) {
                        if (row.ActivityTypeName.toLowerCase()
                                .contains(constraint.toString().toLowerCase())
                        ) {
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
                filterList = results?.values as ArrayList<ActivityTypeData>
                notifyDataSetChanged()
            }
        }
    }
}