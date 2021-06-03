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
import com.example.cheilros.models.CheckListData

class ActivitySubCategoryAdapter(val context: Context, val itemList: List<CheckListData>, val arguments: Bundle?): RecyclerView.Adapter<ActivitySubCategoryAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

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
        holder.txtTitle.text = itemList[position].Checklist
        holder.LLchecklist.setOnClickListener {
            /*val bundle = bundleOf(
                "ChecklistID" to itemList[position].ChecklistCategoryID,
                "ChecklistName" to itemList[position].Checklist,
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )
            Navigation.findNavController(it)
                .navigate(R.id.action_checklistCategoryFragment_to_checklistCategoryDetailFragment, bundle)
                */

            Navigation.findNavController(it)
                .navigate(R.id.action_activityDetailFragment_to_acrivityDetailFragment)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}