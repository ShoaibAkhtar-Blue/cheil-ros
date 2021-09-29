package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.TrainingFeaturesData
import com.example.cheilros.models.TrainingSimpleData
import net.cachapa.expandablelayout.ExpandableLayout
import net.cachapa.expandablelayout.ExpandableLayout.OnExpansionUpdateListener


class TrainingSimpleAdapter(
    val context: Context,
    val itemList: List<TrainingSimpleData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<TrainingSimpleAdapter.ViewHolder>(),
    Filterable {

    lateinit var CSP: CustomSharedPref

    var selectedFeatures: MutableList<String> = arrayListOf()

    var filterList = ArrayList<TrainingSimpleData>()

    init {
        filterList = itemList as ArrayList<TrainingSimpleData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLsimpletraining: LinearLayout = view.findViewById(R.id.LLsimpletraining)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtNofAttendee: TextView = view.findViewById(R.id.txtNofAttendee)
        var txtDate: TextView = view.findViewById(R.id.txtDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_training_simple, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = filterList[position].StoreName
        holder.txtNofAttendee.text = "Number of Attendee: ${filterList[position].Attendese}"
        holder.txtDate.text = filterList[position].TrainingDateTime

        holder.LLsimpletraining.setOnClickListener {
            /* val bundle = bundleOf(
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
                    filterList = itemList as ArrayList<TrainingSimpleData>
                } else {
                    val resultList = ArrayList<TrainingSimpleData>()
                    for (row in itemList) {
                        if (row.StoreName.toLowerCase()
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
                filterList = results?.values as ArrayList<TrainingSimpleData>
                notifyDataSetChanged()
            }
        }
    }
}