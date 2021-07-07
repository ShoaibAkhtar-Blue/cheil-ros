package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.TrainingFeaturesData
import com.example.cheilros.models.TrainingModelData


class TrainingAdapter(
    val context: Context,
    val itemList: List<TrainingModelData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<TrainingAdapter.ViewHolder>(),
    Filterable {

    lateinit var CSP: CustomSharedPref

    var selectedFeatures: MutableList<String> = arrayListOf()

    var filterList = ArrayList<TrainingModelData>()

    init {
        filterList = itemList as ArrayList<TrainingModelData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLchecklist: LinearLayout = view.findViewById(R.id.LLchecklist)
        var LLfeatures: LinearLayout = view.findViewById(R.id.LLfeatures)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_new_training, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = filterList[position].TrainingModelTitle
        holder.LLchecklist.setOnClickListener {
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

        val lparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        for (features in filterList[position].Features){
            //println(features.TrainingModelFeatureTitle)

            val checkbox = CheckBox(context)
            checkbox.text = features.TrainingModelFeatureTitle
            checkbox.tag = filterList[position].TrainingModelTitle
            checkbox.setTextColor(Color.BLACK)
            checkbox.layoutParams = lparams
            checkbox.layoutDirection = View.LAYOUT_DIRECTION_RTL

            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    selectedFeatures.add("${filterList[position].TrainingModelID}:${features.TrainingModelFeatureID}")

                }else{

                }
                CSP.saveData("sess_selected_training_features", selectedFeatures.joinToString(","))
            }

            holder.LLfeatures.addView(checkbox)
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
                    filterList = itemList as ArrayList<TrainingModelData>
                } else {
                    val resultList = ArrayList<TrainingModelData>()
                    for (row in itemList) {
                        if (row.TrainingModelTitle.toLowerCase()
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
                filterList = results?.values as ArrayList<TrainingModelData>
                notifyDataSetChanged()
            }
        }
    }
}