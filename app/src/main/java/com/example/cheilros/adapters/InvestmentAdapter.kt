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
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.BrandsData
import com.example.cheilros.models.InvestmentData


class InvestmentAdapter(val context: Context, val itemList: List<InvestmentData>, val arguments: Bundle?): RecyclerView.Adapter<InvestmentAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLInvestment : LinearLayout = view.findViewById(R.id.LLInvestment)
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitleHeader.text = itemList[position].ElementTitle

        holder.LLInvestment.setOnClickListener {

            val brands: ArrayList<BrandsData> = itemList[position].Brands as ArrayList<BrandsData>

            val bundle = bundleOf(
                "StoreID" to arguments?.getInt("StoreID"),
                "ElementID" to itemList[position].ElementID,
                "ElementTitle" to itemList[position].ElementTitle,
                "BrandsList" to brands
            )

            Navigation.findNavController(it)
                .navigate(R.id.action_investmentFragment_to_investmentDetailFragment, bundle)
        }

        if(holder.LLtable!!.childCount == 0){

            try {
                var table = TableLayout(context)

                val numRows: Int = (itemList[position].Brands.size / 2).toInt()
                var brandIndex = 0

                for (i in 0 until numRows) { //Rows
                    val row = TableRow(context)
                    for (j in 0 until 4) { //Column
                        println("j: $j")
                        val value: Int = (100..200).random()
                        val tv = TextView(context)
                        tv.setTextColor(Color.BLACK)

                        if (j % 2 == 0){
                            //tv!!.typeface = ResourcesCompat.getFont(context!!, R.font.samsungsharpsans_bold)
                            tv.setTextColor(Color.parseColor("#4c4c4c"))
                            tv.text = itemList[position].Brands[brandIndex].BrandName
                        }
                        else{
                            tv.text = if(itemList[position].Brands[brandIndex].ElementStatus == "") "0" else itemList[position].Brands[brandIndex].ElementStatus
                            brandIndex++
                        }


                        val tableRowParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                        tableRowParams.setMargins(5, 5, 5, 5)
                        row.addView(tv, tableRowParams)
                    }
                    table.addView(row)
                }

                holder.LLtable.addView(table)
            }catch (ex:Exception){

            }
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}