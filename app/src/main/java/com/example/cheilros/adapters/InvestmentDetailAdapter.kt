package com.example.cheilros.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.InvestmentDetailFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.BrandsData
import com.example.cheilros.models.InvestmentJSON
import com.example.cheilros.models.InvestmentJSONData
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
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class InvestmentDetailAdapter(
    val context: Context,
    val itemList: MutableList<BrandsData>,
    val fragment: InvestmentDetailFragment,
    val arguments: Bundle?
) :
    RecyclerView.Adapter<InvestmentDetailAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref
    var investmentsCountData: MutableList<InvestmentJSONData> = mutableListOf()


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var onTextUpdated: (String) -> Unit

        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtBrand: TextView = view.findViewById(R.id.txtBrand)
        var txtAttend: EditText = view.findViewById(R.id.txtAttend)
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
            LayoutInflater.from(context).inflate(R.layout.list_investmentdetail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtNum.text = (position + 1).toString()
        holder.txtBrand.text = itemList[position].BrandName

        if (CSP.getData("team_type_id")!!.toInt() <= 4){
            holder.txtAttend.isEnabled = false
            fragment.btnSubmit.visibility = View.GONE
        }

        holder.onTextUpdated = { text ->
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            try {
                if (investmentsCountData.isNullOrEmpty()) {
                    println("investmentsCountData: null")
                    investmentsCountData.add(
                        InvestmentJSONData(
                            itemList[position].BrandID,
                            arguments?.getInt("StoreID"),
                            arguments?.getInt("ElementID"),
                            text.toString(),
                            "",
                            CSP.getData("user_id")?.toInt(),
                            currentDateAndTime
                        )
                    )
                } else {
                    val investmentSize =
                        investmentsCountData.filter { it.BrandID == itemList[position].BrandID }.size
                    println(investmentSize)
                    if (investmentSize == 0) {
                        investmentsCountData.add(
                            InvestmentJSONData(
                                itemList[position].BrandID,
                                arguments?.getInt("StoreID"),
                                arguments?.getInt("ElementID"),
                                text.toString(),
                                "",
                                CSP.getData("user_id")?.toInt(),
                                currentDateAndTime
                            )
                        )
                    } else {
                        val investmentIndex =
                            investmentsCountData.indexOf(investmentsCountData.find { it.BrandID == itemList[position].BrandID })
                        investmentsCountData[investmentIndex] =
                            InvestmentJSONData(
                                itemList[position].BrandID,
                                arguments?.getInt("StoreID"),
                                arguments?.getInt("ElementID"),
                                text.toString(),
                                "",
                                CSP.getData("user_id")?.toInt(),
                                currentDateAndTime
                            )
                        println("investmentIndex $investmentIndex")
                        println(investmentsCountData)
                    }
                }

                updateItem(
                    position,
                    BrandsData(
                        itemList[position].BrandID,
                        itemList[position].BrandName,
                        text.toString()
                    )
                )

            } catch (ex: Exception) {
                println(ex.message)
            }
        }

        if(itemList[position].ElementStatus != "")
            holder.txtAttend.setText(itemList[position].ElementStatus)

        /*holder.watcher = holder.txtAttend.doAfterTextChanged { text ->
            println(text.toString())
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            try {
                if (investmentsCountData.isNullOrEmpty()) {
                    println("investmentsCountData: null")
                    investmentsCountData.add(
                        InvestmentJSONData(
                            itemList[position].BrandID,
                            arguments?.getInt("StoreID"),
                            arguments?.getInt("ElementID"),
                            text.toString(),
                            "",
                            CSP.getData("user_id")?.toInt(),
                            currentDateAndTime
                        )
                    )
                } else {
                    val investmentSize =
                        investmentsCountData.filter { it.BrandID == itemList[position].BrandID }.size
                    println(investmentSize)
                    if (investmentSize == 0) {
                        investmentsCountData.add(
                            InvestmentJSONData(
                                itemList[position].BrandID,
                                arguments?.getInt("StoreID"),
                                arguments?.getInt("ElementID"),
                                text.toString(),
                                "",
                                CSP.getData("user_id")?.toInt(),
                                currentDateAndTime
                            )
                        )
                    } else {
                        val investmentIndex =
                            investmentsCountData.indexOf(investmentsCountData.find { it.BrandID == itemList[position].BrandID })
                        *//*investmentsCountData.add(
                            investmentIndex,
                            InvestmentJSONData(
                                itemList[position].BrandID,
                                arguments?.getInt("StoreID"),
                                arguments?.getInt("ElementID"),
                                text.toString(),
                                "",
                                CSP.getData("user_id")?.toInt(),
                                currentDateAndTime
                            )
                        )*//*
                        investmentsCountData[investmentIndex] = InvestmentJSONData(
                            itemList[position].BrandID,
                            arguments?.getInt("StoreID"),
                            arguments?.getInt("ElementID"),
                            text.toString(),
                            "",
                            CSP.getData("user_id")?.toInt(),
                            currentDateAndTime
                        )
                        println("investmentIndex $investmentIndex")
                        println("investmentIndex ${text.toString()}")
                    }
                }

                updateItem(
                    position,
                    BrandsData(
                        itemList[position].BrandID,
                        itemList[position].BrandName,
                        text.toString()
                    )
                )

            } catch (ex: Exception) {
                println(ex.message)
            }
        }*/

        /*holder.txtAttend.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("$position : $s")
                val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
                val currentDateAndTime: String = simpleDateFormat.format(Date())
                try {
                    if (investmentsCountData.isNullOrEmpty()) {
                        println("investmentsCountData: null")
                        investmentsCountData.add(
                            InvestmentJSONData(
                                itemList[position].BrandID,
                                arguments?.getInt("StoreID"),
                                arguments?.getInt("ElementID"),
                                s.toString(),
                                "",
                                CSP.getData("user_id")?.toInt(),
                                currentDateAndTime
                            )
                        )
                    } else {
                        val investmentSize =
                            investmentsCountData.filter { it.BrandID == itemList[position].BrandID }.size
                        println(investmentSize)
                        if (investmentSize == 0) {
                            investmentsCountData.add(
                                InvestmentJSONData(
                                    itemList[position].BrandID,
                                    arguments?.getInt("StoreID"),
                                    arguments?.getInt("ElementID"),
                                    s.toString(),
                                    "",
                                    CSP.getData("user_id")?.toInt(),
                                    currentDateAndTime
                                )
                            )
                        } else {
                            val investmentIndex =
                                investmentsCountData.indexOf(investmentsCountData.find { it.BrandID == itemList[position].BrandID })
                            investmentsCountData.add(
                                investmentIndex,
                                InvestmentJSONData(
                                    itemList[position].BrandID,
                                    arguments?.getInt("StoreID"),
                                    arguments?.getInt("ElementID"),
                                    s.toString(),
                                    "",
                                    CSP.getData("user_id")?.toInt(),
                                    currentDateAndTime
                                )
                            )
                            println("investmentIndex $investmentIndex")
                        }
                    }
                } catch (ex: Exception) {
                    println(ex.message)
                }
            }
        })*/

        fragment.btnSubmit.setOnClickListener {

            fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

            val gson = Gson()
            val jsonString: String = gson.toJson(InvestmentJSON(investmentsCountData))
            println(jsonString)

            val url = "${CSP.getData("base_url")}/Audit.asmx/InvestmentElement_AuditAdd"

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
                        CSP.saveData("sess_last_update_element_id", arguments?.getInt("ElementID").toString())
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
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int = position

    fun updateItem(position: Int, item: BrandsData) {
        itemList[position] = item
        //itemList[position] = item
        //notifyDataSetChanged()
    }
}