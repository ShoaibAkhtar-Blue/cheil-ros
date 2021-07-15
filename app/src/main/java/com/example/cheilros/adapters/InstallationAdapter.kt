package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.ActivityCategoryData
import com.example.cheilros.models.ActivityCategoryModel
import com.example.cheilros.models.ActivityTypeData
import com.example.cheilros.models.CheckListData

class InstallationAdapter(val context: Context, val itemList: List<ActivityCategoryData>, val arguments: Bundle?): RecyclerView.Adapter<InstallationAdapter.ViewHolder>(), Filterable {

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<ActivityCategoryData>()

    init {
        filterList = itemList as ArrayList<ActivityCategoryData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLchecklist : LinearLayout = view.findViewById(R.id.LLchecklist)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(context).inflate(R.layout.list_checklist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = filterList[position].ActivityCategoryName
        holder.LLchecklist.setOnClickListener {

            val bundle = bundleOf(
                "ActivityCategoryID" to itemList[position].ActivityCategoryID,
                "ActivityCategoryName" to itemList[position].ActivityCategoryName,
                "ActivityTypeID" to itemList[position].ActivityTypeID,
                "ActivityTypeName" to itemList[position].ActivityTypeName,
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )

            try {
                Navigation.findNavController(it)
                    .navigate(R.id.action_installationFragment_to_acrivityDetailFragment, bundle)
            }catch (ex: Exception){
                Navigation.findNavController(it)
                    .navigate(R.id.action_taskDeploymentFragment_to_acrivityDetailFragment, bundle)
            }

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
                    filterList = itemList as ArrayList<ActivityCategoryData>
                } else {
                    val resultList = ArrayList<ActivityCategoryData>()
                    for (row in itemList) {
                        if (row.ActivityCategoryName.toLowerCase()
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
                filterList = results?.values as ArrayList<ActivityCategoryData>
                notifyDataSetChanged()
            }
        }
    }
}