package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.DailyMultipleSaleData
import com.example.cheilros.models.SalesJSONData
import java.text.SimpleDateFormat
import java.util.*

class DailyMultipleSaleAdapter(
    val context: Context,
    val itemList: MutableList<DailyMultipleSaleData>,
    val arguments: Bundle?,
    val salesDetailAdapter: SalesDetailAdapter,
    val selectedDate: String
) : RecyclerView.Adapter<DailyMultipleSaleAdapter.ViewHolder>()  {

    lateinit var CSP: CustomSharedPref

    //var salesData: MutableList<SalesJSONData> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var onTextUpdated: (String) -> Unit
        lateinit var onTextUpdated1: (String) -> Unit

        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtBrand: TextView = view.findViewById(R.id.txtBrand)
        var txtSaleQuantity: EditText = view.findViewById(R.id.txtSaleQuantity)
        var txtSalesValue: EditText = view.findViewById(R.id.txtSalesValue)
        var LLSaleDetail: LinearLayout = view.findViewById(R.id.LLSaleDetail)
        var watcher: TextWatcher? = null

        init { // TextChanged listener added only once.
            txtSaleQuantity.doAfterTextChanged { editable ->
                val text = editable.toString()
                println(text)
                onTextUpdated(text)
            }

            txtSalesValue.doAfterTextChanged { editable ->
                val text = editable.toString()
                println(text)
                onTextUpdated1(text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_daily_sale_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtNum.text = (position + 1).toString()
        holder.txtBrand.visibility = View.INVISIBLE

        holder.onTextUpdated = { text ->
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            println("productid ${itemList[position].ProductID}")
            try {
                if (salesDetailAdapter.salesData.isNullOrEmpty()) {
                    println("salesCountData: null")
                    salesDetailAdapter.salesData.add(
                        SalesJSONData(
                            itemList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text,
                            holder.txtSalesValue.text.toString(),
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString(),
                            (position + 1)
                        )
                    )
                } else {
                    val salesSize =
                        salesDetailAdapter.salesData.filter { it.intSerialNo == (position + 1)}.size
                    println(salesSize)
                    if (salesSize == 0) {
                        salesDetailAdapter.salesData.add(
                            SalesJSONData(
                                itemList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text,
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                (position + 1)
                            )
                        )
                    } else {
                        val salesIndex =
                            salesDetailAdapter.salesData.indexOf(salesDetailAdapter.salesData.find { it.intSerialNo == (position + 1) })
                        salesDetailAdapter.salesData[salesIndex] =
                            SalesJSONData(
                                itemList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text,
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                (position + 1)
                            )
                    }
                }
            } catch (ex: Exception) {
                println(ex.message)
            }
        }

        holder.onTextUpdated1 = { text ->
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            println("productid2 ${itemList[position].ProductID}")
            try {
                if (salesDetailAdapter.salesData.isNullOrEmpty()) {
                    println("salesCountData: null")
                    salesDetailAdapter.salesData.add(
                        SalesJSONData(
                            itemList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            holder.txtSaleQuantity.text.toString(),
                            text,
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString(),
                            (position + 1)
                        )
                    )
                } else {
                    val salesSize =
                        salesDetailAdapter.salesData.filter { it.intSerialNo == (position + 1) }.size
                    println(salesSize)
                    if (salesSize == 0) {
                        salesDetailAdapter.salesData.add(
                            SalesJSONData(
                                itemList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                holder.txtSaleQuantity.text.toString(),
                                text,
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                (position + 1)
                            )
                        )
                    } else {
                        val salesIndex =
                            salesDetailAdapter.salesData.indexOf(salesDetailAdapter.salesData.find { it.intSerialNo == (position + 1) })
                        salesDetailAdapter.salesData[salesIndex] =
                            SalesJSONData(
                                itemList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                holder.txtSaleQuantity.text.toString(),
                                text,
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                (position + 1)
                            )
                    }
                }
            } catch (ex: Exception) {
                println(ex.message)
            }
        }

        holder.txtSaleQuantity.setText(itemList[position].SaleCount.toString())
        holder.txtSalesValue.setText(itemList[position].SalePrice.toString())


    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int = position

}