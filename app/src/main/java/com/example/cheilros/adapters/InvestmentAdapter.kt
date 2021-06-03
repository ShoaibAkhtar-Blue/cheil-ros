package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.InvestmentData


class InvestmentAdapter(val context: Context, val itemList: List<InvestmentData>, arguments: Bundle?): RecyclerView.Adapter<InvestmentAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLchecklist : LinearLayout = view.findViewById(R.id.LLchecklist)
        var txtTitleHeader : TextView = view.findViewById(R.id.txtTitleHeader)
        var LLtable : LinearLayout = view.findViewById(R.id.LLtable)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(context).inflate(R.layout.list_investment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvestmentAdapter.ViewHolder, position: Int) {
        holder.txtTitleHeader.text = itemList[position].ElementTitle

        if(holder.LLtable!!.childCount == 0){
            var table = TableLayout(context)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            //table.layoutParams = lp

            for (i in 0 until 3) {
                val row = TableRow(context)
                for (j in 0 until 4) {
                    val value: Int = (100..200).random()
                    val tv = TextView(context)
                    tv.setTextColor(Color.BLACK)

                    if (j % 2 == 0){
                        tv!!.typeface = ResourcesCompat.getFont(context!!, R.font.samsungsharpsans_bold)
                        tv.text = "LABEL"
                    }
                   else
                    tv.text = "${value.toString()}($j)"

                    val tableRowParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                    tableRowParams.setMargins(5, 5, 5, 5)
                    row.addView(tv, tableRowParams)
                }
                table.addView(row)
            }

            holder.LLtable.addView(table)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}