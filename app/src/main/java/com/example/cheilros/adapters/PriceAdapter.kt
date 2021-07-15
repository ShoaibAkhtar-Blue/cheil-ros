package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.PriceData
import java.security.AccessController.getContext


class PriceAdapter(
    val context: Context,
    val itemList: List<PriceData>,
    val StoreID: Int?
) : RecyclerView.Adapter<PriceAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var RLHeader: RelativeLayout = view.findViewById(R.id.RLHeader)
        var LLInvestment: LinearLayout = view.findViewById(R.id.LLInvestment)
        var txtTitleHeader: TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleDate: TextView = view.findViewById(R.id.txtTitleDate)
        var LLtable: LinearLayout = view.findViewById(R.id.LLtable)
        var imgArrowRight: ImageView = view.findViewById(R.id.imgArrowRight)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_investment, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor", "WrongConstant", "ResourceType")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitleHeader.text = itemList[position].BrandName
        holder.txtTitleDate.text = ""

        holder.imgArrowRight.visibility = View.GONE

        if (holder.LLtable!!.childCount == 0) {

            try {
                var table = TableLayout(context)

                val numRows: Int = itemList[position].Products.size
                var brandIndex = 0

                for (i in 0 until numRows) { //Rows
                    val row = TableRow(context)
                    for (j in 0 until 1) { //Column
                        println("j: $j")
                        val value: Int = (100..200).random()
                        val tv = TextView(context)
                        tv.setTextColor(Color.BLACK)
                        tv.textSize = 20.0f

                        val productName = itemList[position].Products[brandIndex].ProductCategoryName
                        val productID = itemList[position].Products[brandIndex].ProductCategoryID
                        tv!!.typeface =
                            ResourcesCompat.getFont(context!!, R.font.samsungsharpsans_bold)
                        tv.setTextColor(Color.parseColor("#4c4c4c"))
                        tv.text = productName.toString()

                        val tableRowParams = TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        tableRowParams.setMargins(5, 5, 5, 5)
                        row.setBackgroundResource(R.drawable.row_border)
                        row.addView(tv, tableRowParams)

                        /*val outValue = TypedValue()
                        context.theme.resolveAttribute(
                            android.R.attr.selectableItemBackground,
                            outValue,
                            true
                        )
                        row.setBackgroundResource(outValue.resourceId)*/

                        //row.setForeground(android.R.attr.selectableItemBackground)
                        //row.setBackgroundResource(android.R.attr.selectableItemBackground)

                        row.setOnClickListener {
                            println("${itemList[position].BrandID}-${productName}-${productID}-${StoreID}")
                            val bundle = bundleOf(
                                "StoreID" to StoreID,
                                "BrandName" to itemList[position].BrandName,
                                "BrandID" to itemList[position].BrandID,
                                "ProductCategoryID" to productID,
                                "ProductCategory" to productName.toString()
                            )
                            Navigation.findNavController(it)
                                .navigate(R.id.action_priceFragment_to_priceDetailFragment, bundle)
                        }
                    }
                    table.addView(row)
                }

                holder.LLtable.addView(table)
            } catch (ex: Exception) {

            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int = position
}