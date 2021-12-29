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
import com.example.cheilros.data.AppSetting
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.DailyMultipleSaleData
import com.example.cheilros.models.SalesJSONData
import com.kyleduo.switchbutton.SwitchButton
import java.text.SimpleDateFormat
import java.util.*

class DailyMultipleSaleAdapter(
    val context: Context,
    val itemList: MutableList<DailyMultipleSaleData>,
    val arguments: Bundle?,
    val salesDetailAdapter: SalesDetailAdapter,
    val selectedDate: String,
    val settingData: List<AppSetting>
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
        var cbSaleType: SwitchButton = view.findViewById(R.id.cbSaleType)
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
        var SaleType = CSP.getData("SaleType")

        if(SaleType.equals("N")){
            holder.cbSaleType.visibility = View.INVISIBLE
        }

        if(itemList[position].SaleType == 1){
            holder.cbSaleType.isChecked = true
            holder.cbSaleType.text = "1"
        }

        holder.txtNum.text = (position + 1).toString()
        holder.txtBrand.visibility = View.INVISIBLE

        holder.onTextUpdated = { text ->
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            println("productid ${itemList[position].ProductID}")

            if(SaleType.equals("Y")){
                if(holder.cbSaleType.text.equals(""))
                    holder.cbSaleType.text = "2"
            }

            /*if(SaleType.equals("Y")){
                if (holder.cbSaleType.isChecked){
                    SaleType = settingData.filter { it.fixedLabelName == "SaleScreen_Cash" }[0].labelName
                }else{
                    SaleType = settingData.filter { it.fixedLabelName == "SaleScreen_Installment" }[0].labelName
                }
            }*/

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
                            (position + 1),
                            holder.cbSaleType.text.toString().toInt()
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
                                (position + 1),
                                holder.cbSaleType.text.toString().toInt()
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
                                (position + 1),
                                holder.cbSaleType.text.toString().toInt()
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

            if(SaleType.equals("Y")){
                if(holder.cbSaleType.text.equals(""))
                    holder.cbSaleType.text = "2"
            }

            /*if(SaleType.equals("Y")){
                if (holder.cbSaleType.isChecked){
                    SaleType = settingData.filter { it.fixedLabelName == "SaleScreen_Cash" }[0].labelName
                }else{
                    SaleType = settingData.filter { it.fixedLabelName == "SaleScreen_Installment" }[0].labelName
                }
            }*/

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
                            (position + 1),
                            holder.cbSaleType.text.toString().toInt()
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
                                (position + 1),
                                holder.cbSaleType.text.toString().toInt()
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
                                (position + 1),
                                holder.cbSaleType.text.toString().toInt()
                            )
                    }
                }
            } catch (ex: Exception) {
                println(ex.message)
            }
        }

        holder.cbSaleType.setOnClickListener {
            println("cbSaleType")
            println("SaleType: $SaleType")
            println(holder.cbSaleType.isChecked)
            println(holder.cbSaleType.text)
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            println("productid ${itemList[position].ProductID}")

            println("cbSaleType: $SaleType")
            var SaleVal = "Cash"
            if(SaleType.equals("Y")){
                if (holder.cbSaleType.isChecked){
                    SaleVal = "1"
                }else{
                    SaleVal = "2"
                }
            }

            println("cbSaleType: $SaleVal")
            holder.cbSaleType.text = SaleVal
            println("cbSaleType: $SaleType")

            try {
                if (salesDetailAdapter.salesData.isNullOrEmpty()) {
                    println("salesCountData: null")
                    salesDetailAdapter.salesData.add(
                        SalesJSONData(
                            itemList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            holder.txtSaleQuantity.text.toString(),
                            holder.txtSalesValue.text.toString(),
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString(),
                            (position + 1),
                            SaleVal.toInt()
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
                                holder.txtSaleQuantity.text.toString(),
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                (position + 1),
                                SaleVal.toInt()
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
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                (position + 1),
                                SaleVal.toInt()
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