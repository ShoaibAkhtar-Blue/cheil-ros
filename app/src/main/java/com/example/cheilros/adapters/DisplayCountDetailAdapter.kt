package com.example.cheilros.adapters

import android.app.Activity
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
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.DisplayCountDetailFragment
import com.example.cheilros.fragments.storeview.InvestmentDetailFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.*
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

    init {
        filterList = itemList as ArrayList<DisplayCountViewData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var onTextUpdated: (String) -> Unit

        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtBrand: TextView = view.findViewById(R.id.txtBrand)
        var txtAttend: EditText = view.findViewById(R.id.txtAttend)
        var btnBarCode: ImageButton = view.findViewById(R.id.btnBarCode)
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
        holder.txtBrand.text = itemList[position].ShortName

        holder.btnBarCode.setOnClickListener{
//            Navigation.findNavController(it)
//                .navigate(R.id.action_displayCountDetailFragment_to_barcodeFragment)
            Navigation.findNavController(it)
                .navigate(R.id.action_displayCountDetailFragment_to_barcodeActivity)
        }

        holder.onTextUpdated = { text ->
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            try {
                if (displayCountData.isNullOrEmpty()) {
                    println("investmentsCountData: null")
                    displayCountData.add(
                        DisplayCountJSONData(
                            itemList[position].ProductID,
                            arguments?.getInt("StoreID"),
                            text.toInt(),
                            CSP.getData("user_id")?.toInt()
                        )
                    )
                } else {
                    val investmentSize =
                        displayCountData.filter { it.ProductID == itemList[position].ProductID }.size
                    println(investmentSize)
                    if (investmentSize == 0) {
                        displayCountData.add(
                            DisplayCountJSONData(
                                itemList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text.toInt(),
                                CSP.getData("user_id")?.toInt()
                            )
                        )
                    } else {
                        val investmentIndex =
                            displayCountData.indexOf(displayCountData.find { it.ProductID == itemList[position].ProductID })
                        displayCountData[investmentIndex] =
                            DisplayCountJSONData(
                                itemList[position].ProductID,
                                arguments?.getInt("StoreID"),
                                text.toInt(),
                                CSP.getData("user_id")?.toInt()
                            )

                    }
                }

                /*updateItem(
                    position,
                    DisplayCountProductsData(
                        itemList[position].ProductID,
                        itemList[position].ShortName,
                        text.toInt()
                    )
                )*/

            } catch (ex: Exception) {
                println(ex.message)
            }
        }

        holder.txtAttend.setText(itemList[position].DisplayCount.toString())

        fragment.btnSubmit.setOnClickListener {

            fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

            val gson = Gson()
            val jsonString: String = gson.toJson(DisplayCountJSON(displayCountData))
            println(jsonString)

            val url = "${CSP.getData("base_url")}/DisplayCount.asmx/DisplayCountAdd"

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
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<DisplayCountViewData>
                notifyDataSetChanged()
            }
        }
    }

    /*fun updateItem(position: Int, item: DisplayCountProductsData) {
        itemList[position] = item
        //itemList[position] = item
        //notifyDataSetChanged()
    }*/
}