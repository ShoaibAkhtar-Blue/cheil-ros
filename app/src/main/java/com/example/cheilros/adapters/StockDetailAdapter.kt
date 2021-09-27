package com.example.cheilros.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.storeview.StockDetailFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.StockJSON
import com.example.cheilros.models.StockJSONData
import com.example.cheilros.models.StockDetailData
import com.google.gson.Gson
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class StockDetailAdapter(
    val context: Context,
    val itemList: MutableList<StockDetailData>,
    val fragment: StockDetailFragment,
    val arguments: Bundle?,
    val selectedDate: CharSequence,
    val settingData: List<AppSetting>
) :
    RecyclerView.Adapter<StockDetailAdapter.ViewHolder>(),
    Filterable {

    lateinit var CSP: CustomSharedPref
    var salesData: MutableList<StockJSONData> = mutableListOf()

    var filterList = ArrayList<StockDetailData>()

    init {
        filterList = itemList as ArrayList<StockDetailData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var onTextUpdated: (String) -> Unit
        lateinit var onTextUpdated1: (String) -> Unit

        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtBrand: TextView = view.findViewById(R.id.txtBrand)
        var txtSaleQuantity: EditText = view.findViewById(R.id.txtSaleQuantity)
        var txtSalesValue: EditText = view.findViewById(R.id.txtSalesValue)
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_sales_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (CSP.getData("team_type_id")!!.toInt() <= 4) {
            holder.txtSaleQuantity.isEnabled = false
            holder.txtSalesValue.isEnabled = false
            fragment.btnSubmit.visibility = View.GONE
        }

        holder.txtNum.text = (position + 1).toString()
        holder.txtBrand.text = filterList[position].ShortName

        holder.onTextUpdated = { text ->
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            try {
                if (salesData.isNullOrEmpty()) {
                    println("salesCountData: null")
                    salesData.add(
                        StockJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text,
                            holder.txtSalesValue.text.toString(),
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString()
                        )
                    )
                } else {
                    val salesSize =
                        salesData.filter { it.ProductID == filterList[position].ProductID }.size
                    println(salesSize)
                    if (salesSize == 0) {
                        salesData.add(
                            StockJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text,
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString()
                            )
                        )
                    } else {
                        val salesIndex =
                            salesData.indexOf(salesData.find { it.ProductID == filterList[position].ProductID })
                        salesData[salesIndex] =
                            StockJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text,
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString()
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
            try {
                if (salesData.isNullOrEmpty()) {
                    println("salesCountData: null")
                    salesData.add(
                        StockJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            holder.txtSaleQuantity.text.toString(),
                            text,
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString()
                        )
                    )
                } else {
                    val salesSize =
                        salesData.filter { it.ProductID == filterList[position].ProductID }.size
                    println(salesSize)
                    if (salesSize == 0) {
                        salesData.add(
                            StockJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                holder.txtSaleQuantity.text.toString(),
                                text,
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString()
                            )
                        )
                    } else {
                        val salesIndex =
                            salesData.indexOf(salesData.find { it.ProductID == filterList[position].ProductID })
                        salesData[salesIndex] =
                            StockJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                holder.txtSaleQuantity.text.toString(),
                                text,
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString()
                            )

                    }
                }

                /*updateItem(
                    position,
                    DisplayCountProductsData(
                        filterList[position].ProductID,
                        filterList[position].ShortName,
                        text.toInt()
                    )
                )*/

            } catch (ex: Exception) {
                println(ex.message)
            }
        }

        val isAlreadyEdit = salesData.filter { it.ProductID == filterList[position].ProductID}
        if(isAlreadyEdit.isNotEmpty()){
            holder.txtSaleQuantity.setText(isAlreadyEdit[0].StockCount)
            holder.txtSalesValue.setText(isAlreadyEdit[0].Field2)
        }else{
            holder.txtSaleQuantity.setText(filterList[position].StockStatus.toString())
            holder.txtSalesValue.setText(filterList[position].Field.toString())
        }



        if(settingData.filter { it.fixedLabelName == "Stock_Column2" }[0].labelName == "")
            holder.txtSalesValue.visibility = View.GONE

        fragment.btnSubmit.setOnClickListener {

            fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

            val gson = Gson()
            val jsonString: String = gson.toJson(StockJSON(salesData))
            println(jsonString)

            val url = "${CSP.getData("base_url")}/Stock.asmx/StockStatusAdd"

            val request_header: MediaType? = "application/text; charset=utf-8".toMediaTypeOrNull()

            var body: RequestBody = jsonString.toRequestBody(request_header)
            val request = Request.Builder().post(body).url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val tm = response.body?.string()
                    println(tm)
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("Data saved!")
                                .sneakSuccess()
                        }

                        Navigation.findNavController(it).navigateUp()
                        fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Failed", "FAILED")
                    //e.printStackTrace()
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not saved!")
                                .sneakError()
                        }
                        fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<StockDetailData>
                } else {
                    val resultList = ArrayList<StockDetailData>()
                    for (row in itemList) {
                        if (row.ShortName.toLowerCase()
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
                filterList = results?.values as ArrayList<StockDetailData>
                notifyDataSetChanged()
            }
        }
    }
}