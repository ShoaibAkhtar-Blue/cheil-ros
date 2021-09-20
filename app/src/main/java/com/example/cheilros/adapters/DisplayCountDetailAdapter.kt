package com.example.cheilros.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.DisplayCountDetailFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_barcode.*
import kotlinx.android.synthetic.main.dialog_barcode_input.*
import kotlinx.android.synthetic.main.fragment_investment_detail.btnSubmit
import kotlinx.android.synthetic.main.fragment_investment_detail.mainLoadingLayoutCC
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DisplayCountDetailAdapter(
    val context: Context,
    val itemList: MutableList<DisplayCountViewData>,
    val fragment: DisplayCountDetailFragment,
    val arguments: Bundle?
) :
    RecyclerView.Adapter<DisplayCountDetailAdapter.ViewHolder>(),
    Filterable {

    lateinit var CSP: CustomSharedPref
    var displayCountData: MutableList<DisplayCountJSONData> = mutableListOf()

    var filterList = ArrayList<DisplayCountViewData>()

    lateinit var layoutManagerBC: RecyclerView.LayoutManager
    lateinit var recylcerAdapterBC: BarcodeAdapter

    private val SHOW_MENU = 1
    private val HIDE_MENU = 2


    init {
        filterList = itemList as ArrayList<DisplayCountViewData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var onTextUpdated: (String) -> Unit

        var LLDisplayCount: LinearLayout = view.findViewById(R.id.LLDisplayCount)
        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtBrand: TextView = view.findViewById(R.id.txtBrand)
        var txtAttend: EditText = view.findViewById(R.id.txtAttend)
        var btnBarCode: ImageButton = view.findViewById(R.id.btnBarCode)
        var btnAllBarCode: ImageButton = view.findViewById(R.id.btnAllBarCode)

        // var qpAttend: HorizontalQuantitizer = view.findViewById(R.id.qpAttend)
        var watcher: TextWatcher? = null

        init { // TextChanged listener added only once.
            txtAttend.doAfterTextChanged { editable ->
                val text = editable.toString()
                println(text)
                onTextUpdated(text)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_displaycountdetail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtNum.text = (position + 1).toString()
        holder.txtBrand.text = filterList[position].ShortName

        holder.btnBarCode.visibility = View.GONE
        holder.btnAllBarCode.visibility = View.GONE

        if (filterList[position].isBarCodeEnabled == "Y") {
            holder.txtAttend.isEnabled = false
            holder.txtAttend.isFocusable = false
        }

        holder.onTextUpdated = { text ->
            if (filterList[position].isBarCodeEnabled == "N")
                addDatainJsonObject(position, text)
        }

        holder.txtAttend.setText(filterList[position].DisplayCount.toString())

        fragment.btnSubmit.setOnClickListener {

            fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)


            val gson = Gson()
            val jsonString: String = gson.toJson(DisplayCountJSON(displayCountData))
            println(jsonString)

            val url = "${CSP.getData("base_url")}/DisplayCount.asmx/DisplayCountDetailAdd"

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

                        CSP.delData("activity_barcodes")
                        CSP.delData("ActivityDetail_BARCODE_SET")

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
                println("charSearch: $charSearch")
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<DisplayCountViewData>
                } else {
                    val resultList = ArrayList<DisplayCountViewData>()
                    for (row in itemList) {
                        if (row.ShortName.toLowerCase()
                                .contains(constraint.toString().toLowerCase())
                        ) {

                            resultList.add(row)
                        }
                    }

                    filterList = resultList
                }
                //return FilterResults().apply { values = filterList }

                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
                /*val filteredList = ArrayList<DisplayCountViewData>()
                if (charSearch.isEmpty()) filterList =
                    itemList as ArrayList<DisplayCountViewData> else {

                    itemList
                        .filter {
                            (it.ShortName.uppercase(Locale.ROOT).contains(charSearch!!, true))
                        }
                        .forEach {
                            if (it.ShortName.uppercase(Locale.ROOT)
                                    .contains(constraint.toString().uppercase(Locale.ROOT))
                            ) {
                                filteredList.add(it)
                            }
                        }
                    filterList = filteredList

                }
                return FilterResults().apply { values = filteredList }*/
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                println("filterList ${filterList.size}")
                filterList = results?.values as ArrayList<DisplayCountViewData>
                notifyDataSetChanged()
            }
        }
    }

    fun updateItem(pid: Int, isRemove: Boolean = false) {
        val numbersOnSameIndexAsValue = filterList.indexOf(filterList.find { it.ProductID == pid })
        if (isRemove) {
            if (filterList[numbersOnSameIndexAsValue].DisplayCount > 0)
                filterList[numbersOnSameIndexAsValue].DisplayCount =
                    (filterList[numbersOnSameIndexAsValue].DisplayCount - 1)
        } else {
            filterList[numbersOnSameIndexAsValue].DisplayCount =
                (filterList[numbersOnSameIndexAsValue].DisplayCount + 1)
        }

        println("updateItem $numbersOnSameIndexAsValue")
        //itemList[position] = item
        notifyDataSetChanged()
    }

    fun barCodeScan(position: Int) {
        CSP.saveData("dispProdID", filterList[position].ProductID.toString())
        CSP.saveData("dispPosition", position.toString())
        fragment.view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.action_displayCountDetailFragment_to_barcodeActivity)
        }
    }

    fun allBarcodes(position: Int) {
        if (!CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
            println("ActivityDetail_BARCODE_SET: ${CSP.getData("ActivityDetail_BARCODE_SET")}")
            var savedBarcodes =
                CSP.getData("ActivityDetail_BARCODE_SET")?.split(",")?.toTypedArray()
            var savedBarcodes1 =
                savedBarcodes?.filter { it.contains("_${filterList[position].ProductID}") }
            //var savedBarcodes = "abc, xyz"?.split(",").toTypedArray()

            if (savedBarcodes1 != null) {
                if (savedBarcodes1.isNotEmpty()) {
                    val li = LayoutInflater.from(context)
                    val promptsView: View = li.inflate(R.layout.dialog_barcode, null)

                    val dialog = Dialog(context)
                    dialog.setContentView(promptsView)
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(true)

                    dialog.rvBarcode.setHasFixedSize(true)
                    layoutManagerBC = LinearLayoutManager(context)
                    dialog.rvBarcode.layoutManager = layoutManagerBC
                    recylcerAdapterBC = savedBarcodes1?.toMutableList()?.let { it1 ->
                        BarcodeAdapter(
                            context,
                            it1,
                            dialog,
                            true,
                            fragment.recylcerAdapter,
                            filterList[position].ProductID,
                            arguments
                        )
                    }!!
                    dialog.rvBarcode.adapter = recylcerAdapterBC

                    dialog.show()
                } else {
                    fetchSerialNum(position)
                }
            } else {
                //fetchSerialNum(position)
            }

        } else {
            fetchSerialNum(position)
        }
    }

    fun inputBarcode(position: Int) {
        if (position != -1) {
            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_barcode_input, null)

            val dialog = Dialog(context)
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.btnAccept.setOnClickListener {
                val barInput = dialog.etInputBarcode.text.toString()

                try {
                    if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                        CSP.saveData(
                            "ActivityDetail_BARCODE_SET",
                            "${barInput}_${filterList[position].ProductID}"
                        )
                        CSP.delData("activity_barcodes")
                        updateItem(filterList[position].ProductID)


                    } else {

                        println("ActivityDetail_BARCODE_SET: ${CSP.getData("ActivityDetail_BARCODE_SET")}")
                        println("barInput: $barInput")
                        println("filterList: ${filterList[position].ProductID}")

                        CSP.saveData(
                            "ActivityDetail_BARCODE_SET",
                            "${CSP.getData("ActivityDetail_BARCODE_SET")},${barInput}_${filterList[position].ProductID}"
                        )
                        CSP.delData("activity_barcodes")
                        updateItem(filterList[position].ProductID)
                    }

                    addDatainJsonObject(position, barInput, true)
                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    fun addDatainJsonObject(position: Int, text: String, isMuliProdSerialAllow: Boolean = false) {
        println("addDatainJsonObject: $text")
        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        try {
            if (displayCountData.isNullOrEmpty()) {
                println("investmentsCountData: null")
                displayCountData.add(
                    DisplayCountJSONData(
                        filterList[position].ProductID,
                        arguments?.getInt("StoreID"),
                        text,
                        CSP.getData("user_id")?.toInt(),
                        filterList[position].isBarCodeEnabled
                    )
                )
            } else {
                val displaySize =
                    displayCountData.filter { it.ProductID == filterList[position].ProductID }.size
                println(displaySize)
                if (displaySize == 0) {
                    displayCountData.add(
                        DisplayCountJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text,
                            CSP.getData("user_id")?.toInt(),
                            filterList[position].isBarCodeEnabled
                        )
                    )
                } else if (isMuliProdSerialAllow) {
                    displayCountData.add(
                        DisplayCountJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text,
                            CSP.getData("user_id")?.toInt(),
                            filterList[position].isBarCodeEnabled
                        )
                    )
                } else {
                    val displayIndex =
                        displayCountData.indexOf(displayCountData.find { it.ProductID == filterList[position].ProductID })
                    displayCountData[displayIndex] =
                        DisplayCountJSONData(
                            filterList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text,
                            CSP.getData("user_id")?.toInt(),
                            filterList[position].isBarCodeEnabled
                        )

                }
            }

        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    fun fetchSerialNum(position: Int) {
        var url =
            "${CSP.getData("base_url")}/DisplayCount.asmx/DisplayCountDetailView?ProductID=${filterList[position].ProductID}&StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData =
                    gson.fromJson(body, DisplayCountDetailViewModel::class.java)
                if (apiData.status == 200) {

                    if (apiData.data.isNotEmpty()) {
                        (context as Activity).runOnUiThread {
                            try {
                                var barcodeList: MutableList<String> = arrayListOf()

                                for (bl in apiData.data) {
                                    barcodeList.add(bl.SerialNumber)
                                }

                                val li = LayoutInflater.from(context)
                                val promptsView: View =
                                    li.inflate(R.layout.dialog_barcode, null)

                                val dialog = Dialog(context)
                                dialog.setContentView(promptsView)
                                dialog.setCancelable(false)
                                dialog.setCanceledOnTouchOutside(true)

                                dialog.rvBarcode.setHasFixedSize(true)
                                layoutManagerBC = LinearLayoutManager(context)
                                dialog.rvBarcode.layoutManager = layoutManagerBC
                                recylcerAdapterBC =
                                    barcodeList?.toMutableList()?.let { it1 ->
                                        BarcodeAdapter(
                                            context,
                                            it1,
                                            dialog,
                                            true,
                                            fragment.recylcerAdapter,
                                            filterList[position].ProductID,
                                            arguments,
                                            true
                                        )
                                    }!!
                                dialog.rvBarcode.adapter = recylcerAdapterBC

                                dialog.show()
                            } catch (ex: Exception) {

                            }
                        }
                    }
                } else {

                }
            }
        })
    }
}