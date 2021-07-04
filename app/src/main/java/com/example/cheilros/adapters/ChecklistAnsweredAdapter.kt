package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListAnswerData
import com.example.cheilros.models.CheckListData


class ChecklistAnsweredAdapter(val context: Context, val itemList: List<CheckListAnswerData>, val arguments: Bundle?): RecyclerView.Adapter<ChecklistAnsweredAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtQuestion : TextView = view.findViewById(R.id.txtQuestion)
        var txtAnswer : TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistAnsweredAdapter.ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(context).inflate(R.layout.list_checklistanswer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistAnsweredAdapter.ViewHolder, position: Int) {
        holder.txtQuestion.text = itemList[position].Question
        holder.txtAnswer.text = itemList[position].CheckListStatus

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}