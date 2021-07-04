package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListAnswerData
import com.example.cheilros.models.InvestmentAnswerData


class InvestmentAnswerAdapter(val context: Context, val itemList: List<InvestmentAnswerData>, val arguments: Bundle?): RecyclerView.Adapter<InvestmentAnswerAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtTitleHeader : TextView = view.findViewById(R.id.txtTitleHeader)
        var txtStatus : TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentAnswerAdapter.ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(context).inflate(R.layout.list_investmentanswer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvestmentAnswerAdapter.ViewHolder, position: Int) {
        holder.txtTitleHeader.text = itemList[position].ElementTitle
        holder.txtStatus.text = itemList[position].ElementStatus

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}