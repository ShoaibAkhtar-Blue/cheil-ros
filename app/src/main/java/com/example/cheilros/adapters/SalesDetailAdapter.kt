package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.storeview.SalesDetailFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_sales_detail_product.*
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import kotlinx.android.synthetic.main.fragment_investment_detail.mainLoadingLayoutCC
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SalesDetailAdapter(
    val context: Context,
    val itemList: MutableList<SalesDetailData>,
    val fragment: SalesDetailFragment,
    val arguments: Bundle?,
    val selectedDate: CharSequence,
    val settingData: List<AppSetting>
) :
    RecyclerView.Adapter<SalesDetailAdapter.ViewHolder>(),
    Filterable {

    lateinit var CSP: CustomSharedPref
    var salesData: MutableList<SalesJSONData> = mutableListOf()

    var filterList = ArrayList<SalesDetailData>()

    lateinit var recylcerDailyMultipleSaleAdapter: DailyMultipleSaleAdapter

    init {
        filterList = itemList as ArrayList<SalesDetailData>
    }

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
                //if(text != "")
                onTextUpdated(text)
//                else {
//                    txtSaleQuantity.setText("0")
//                    onTextUpdated("0")
//                }
            }

            txtSalesValue.doAfterTextChanged { editable ->
                val text = editable.toString()
                println(text)
                //if(text != "")
                onTextUpdated1(text)
//                else {
//                    txtSalesValue.setText("0")
//                    onTextUpdated1("0")
//                }
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

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val isMultiSaleAllow = CSP.getData("AllowMultipleSale").equals("Y")

        if (CSP.getData("team_type_id")!!.toInt() <= 4) {
            holder.txtSaleQuantity.isEnabled = false
            holder.txtSalesValue.isEnabled = false
            fragment.btnSubmit.visibility = View.GONE
        }

        if (isMultiSaleAllow) {
            holder.txtSaleQuantity.isEnabled = false
            holder.txtSalesValue.isEnabled = false

            holder.LLSaleDetail.setOnClickListener {

                salesData.clear()

                val li = LayoutInflater.from(context)
                val promptsView: View = li.inflate(R.layout.dialog_sales_detail_product, null)
                var ctx = it
                val dialog = Dialog(context)
                dialog.setContentView(promptsView)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(true)

                dialog.SalesQty.text = settingData.filter { it.fixedLabelName == "Sale_Quantity" }[0].labelName
                dialog.SalesValue.text = settingData.filter { it.fixedLabelName == "Sale_Value" }[0].labelName
                dialog.SaleScreen_Cash.text = settingData.filter { it.fixedLabelName == "SaleScreen_Cash" }[0].labelName

                var SaleType = CSP.getData("SaleType")

                if(SaleType.equals("N")){
                    dialog.SaleScreen_Cash.visibility = View.INVISIBLE
                }

                //region Load Daily Sale Data
                println("${CSP.getData("base_url")}/Sales.asmx/DailyMultipleSaleView?ProductID=${filterList[position].ProductID}&StoreID=${arguments?.getInt("StoreID").toString()}&SaleDate=${selectedDate}")
                val request = Request.Builder()
                    .url("${CSP.getData("base_url")}/Sales.asmx/DailyMultipleSaleView?ProductID=${filterList[position].ProductID}&StoreID=${arguments?.getInt("StoreID").toString()}&SaleDate=${selectedDate}")
                    .build()

                //val client = OkHttpClient()
                //NIK: 2022-03-22
                val client: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage(e.message.toString())
                                    .sneakError()
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println(body)
                        try {
                            val gson = GsonBuilder().create()
                            val apiData = gson.fromJson(body, DailyMultipleSaleModel::class.java)

                            if (apiData.status == 200) {
                                (context as Activity).runOnUiThread {
                                     dialog.rvSalesProductDetail.setHasFixedSize(true)
                                    var layoutManager: RecyclerView.LayoutManager =  LinearLayoutManager(context)
                                    dialog.rvSalesProductDetail.layoutManager = layoutManager
                                    recylcerDailyMultipleSaleAdapter = DailyMultipleSaleAdapter(
                                         context,
                                         apiData.data as MutableList<DailyMultipleSaleData>,
                                         arguments,
                                        this@SalesDetailAdapter,
                                        selectedDate as String,
                                        settingData
                                     )
                                    dialog.rvSalesProductDetail.adapter = recylcerDailyMultipleSaleAdapter

                                    if(apiData.data.size < 5){
                                        val total_new_row = 5 - apiData.data.size
                                        println("total_new_row $total_new_row")

                                        repeat(total_new_row){
                                            recylcerDailyMultipleSaleAdapter.itemList.add(DailyMultipleSaleData(0, arguments?.getInt("StoreID")!!
                                                .toInt(), filterList[position].ProductID, 0,0, selectedDate as String
                                            ,0))

                                        }
                                        recylcerDailyMultipleSaleAdapter.notifyDataSetChanged()
                                    }

                                }
                            } else {
                                (context as Activity).runOnUiThread {
                                    context?.let { it1 ->
                                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                            .setTitle("Error!!")
                                            .setMessage("Data not fetched.")
                                            .sneakWarning()
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage(ex.message.toString())
                                        .sneakError()
                                }
                                //findNavController().popBackStack()
                            }
                        }
                    }

                })

                //endregion

                dialog.txtTitle.text = filterList[position].ShortName

                dialog.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.btnAccept.setOnClickListener {

                    val gson = Gson()
                    val jsonString: String = gson.toJson(SalesJSON(salesData))
                    println(jsonString)

                    for(s in salesData){
                        if(s.SaleCount.toInt() > 0 && s.SalePrice.toInt() == 0){
                            dialog.txtError.visibility = View.VISIBLE
                            return@setOnClickListener
                        }else{
                            dialog.txtError.visibility = View.GONE
                        }
                    }

                    val del_sale_url = "${CSP.getData("base_url")}/Sales.asmx/SaleCountRemove?ProductID=${filterList[position].ProductID}&StoreID=${arguments?.getInt("StoreID")}&SaleDate=${selectedDate as String}"
                    println(del_sale_url)
                    //val client = OkHttpClient()
                    //NIK: 2022-03-22
                    val client: OkHttpClient = OkHttpClient.Builder()
                        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                        .build()

                    val request = Request.Builder()
                        .url(del_sale_url)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val body = response.body?.string()
                            println(body)
                            val gson = GsonBuilder().create()
                            val apiData =
                                gson.fromJson(body, CheckInOutModel::class.java)
                            if (apiData.status == 200) {
                                (context as Activity).runOnUiThread {
                                    saveSale(ctx, salesData)
                                }
                            } else {

                            }
                        }
                    })

                    dialog.dismiss()
                }

                dialog.btnAddNew.visibility = View.GONE
                dialog.btnAddNew.setOnClickListener {
                    recylcerDailyMultipleSaleAdapter.itemList.add(DailyMultipleSaleData(0, arguments?.getInt("StoreID")!!
                        .toInt(), filterList[position].ProductID, 0,0, selectedDate as String
                    ,0))
                    recylcerDailyMultipleSaleAdapter.notifyDataSetChanged()
                }

                dialog.show()

                salesData.clear()
            }
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
                        SalesJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text,
                            holder.txtSalesValue.text.toString(),
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString(),
                            0,
                            0
                        )
                    )
                } else {
                    val salesSize =
                        salesData.filter { it.ProductID == filterList[position].ProductID }.size
                    println(salesSize)
                    if (salesSize == 0) {
                        salesData.add(
                            SalesJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text,
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                0,
                                0
                            )
                        )
                    } else {
                        val salesIndex =
                            salesData.indexOf(salesData.find { it.ProductID == filterList[position].ProductID })
                        salesData[salesIndex] =
                            SalesJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text,
                                holder.txtSalesValue.text.toString(),
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                0,
                                0
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
                        SalesJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            holder.txtSaleQuantity.text.toString(),
                            text,
                            CSP.getData("user_id")?.toInt(),
                            selectedDate.toString(),
                            0,
                            0
                        )
                    )
                } else {
                    val salesSize =
                        salesData.filter { it.ProductID == filterList[position].ProductID }.size
                    println(salesSize)
                    if (salesSize == 0) {
                        salesData.add(
                            SalesJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                holder.txtSaleQuantity.text.toString(),
                                text,
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                0,
                                0
                            )
                        )
                    } else {
                        val salesIndex =
                            salesData.indexOf(salesData.find { it.ProductID == filterList[position].ProductID })
                        salesData[salesIndex] =
                            SalesJSONData(
                                filterList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                holder.txtSaleQuantity.text.toString(),
                                text,
                                CSP.getData("user_id")?.toInt(),
                                selectedDate.toString(),
                                0,
                                0
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

        val isAlreadyEdit = salesData.filter { it.ProductID == filterList[position].ProductID }
        if (isAlreadyEdit.isNotEmpty()) {
            holder.txtSaleQuantity.setText(isAlreadyEdit[0].SaleCount)
            holder.txtSalesValue.setText(isAlreadyEdit[0].SalePrice)
        } else {
            holder.txtSaleQuantity.setText(filterList[position].SaleQuantity.toString())
            holder.txtSalesValue.setText(filterList[position].SaleValue.toString())
        }

        fragment.btnSubmit.setOnClickListener {
            saveSale(it, salesData)
        }
    }

    fun saveSale(view: View, salesData: MutableList<SalesJSONData>) {
        fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        val gson = Gson()
        val jsonString: String = gson.toJson(SalesJSON(this.salesData))
        println(jsonString)

        val url = "${CSP.getData("base_url")}/Sales.asmx/SaleCountAdd"

        val request_header: MediaType? = "application/text; charset=utf-8".toMediaTypeOrNull()

        var body: RequestBody = jsonString.toRequestBody(request_header)
        val request = Request.Builder().post(body).url(url).build()
        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()
        client.newCall(request).enqueue(object : Callback {
            @RequiresApi(Build.VERSION_CODES.O)
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

                    //Navigation.findNavController(view).navigateUp()
                    fragment.filterTS(0)
                    //fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
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

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun getItemViewType(position: Int): Int = position
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<SalesDetailData>
                } else {
                    val resultList = ArrayList<SalesDetailData>()
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
                filterList = results?.values as ArrayList<SalesDetailData>
                notifyDataSetChanged()
            }
        }
    }

    /*fun updateItem(position: Int, item: DisplayCountProductsData) {
        filterList[position] = item
        //filterList[position] = item
        //notifyDataSetChanged()
    }*/
}