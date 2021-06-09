package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListDetailData
import com.example.cheilros.models.CheckListJSONData


class ChecklistDetailAdapter(
    val context: Context,
    val itemList: List<CheckListDetailData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<ChecklistDetailAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    var checklistAnswer: MutableList<CheckListJSONData> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLchecklist: LinearLayout = view.findViewById(R.id.LLchecklist)
        var LLAnswer: LinearLayout = view.findViewById(R.id.LLAnswer)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_checklistdetail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = itemList[position].Question

        if(holder.LLAnswer!!.childCount == 0){
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            if(itemList[position].InputTypeID == 1){
                val rowView: View = inflater.inflate(R.layout.checkbox, null)
                holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
            }
            if(itemList[position].InputTypeID == 2){
                val rowView: View = inflater.inflate(R.layout.field, null)
                holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
            }

            if(itemList[position].InputTypeID == 3){
                val rowView: View = inflater.inflate(R.layout.field, null)
                holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
            }

            if(itemList[position].InputTypeID == 4){
                val rowView: View = inflater.inflate(R.layout.datebutton, null)
                holder.LLAnswer!!.addView(rowView, holder.LLAnswer!!.childCount - 1)
            }


            if(itemList[position].InputTypeID == 2 || itemList[position].InputTypeID == 3){
                var v = holder.LLAnswer.getChildAt(1)
                if (v is EditText) {
                    println(v.text)
                }
            }

        }

        holder.LLchecklist.setOnClickListener {
            /*println("LLchecklist")
            println(holder.LLAnswer.childCount)
            var ed: EditText = holder.LLAnswer.getChildAt(0).findViewById(R.id.number_edit_text)
            println(ed.text)*/
            /*var v = holder.LLAnswer.getChildAt(1)
            if (v is EditText) {
                println(v.text)
            }*/

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun generateLayout(type: Int){

    }
}