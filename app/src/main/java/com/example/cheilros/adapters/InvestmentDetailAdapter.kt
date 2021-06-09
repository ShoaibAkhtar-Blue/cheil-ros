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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.InvestmentDetailFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.BrandsData
import com.example.cheilros.models.InvestmentJSON
import com.example.cheilros.models.InvestmentJSONData
import com.google.gson.Gson
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class InvestmentDetailAdapter(val context: Context, val itemList: ArrayList<BrandsData>, val fragment: InvestmentDetailFragment, val arguments: Bundle?) :
    RecyclerView.Adapter<InvestmentDetailAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref
    var investmentsCountData: MutableList<InvestmentJSONData> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtNum: TextView = view.findViewById(R.id.txtNum)
        var txtBrand: TextView = view.findViewById(R.id.txtBrand)
        var txtAttend: EditText = view.findViewById(R.id.txtAttend)
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
        holder.txtAttend.setText(itemList[position].ElementStatus)

        holder.txtAttend.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("$position : $s")
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
                                "2021-06-09"
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
                                    "2021-06-09"
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
                                    "2021-06-09"
                                )
                            )
                            println("investmentIndex $investmentIndex")
                        }
                    }
                } catch (ex: Exception) {
                    println(ex.message)
                }

            }
        })

        fragment.btnSubmit.setOnClickListener {

            fragment.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

            val gson = Gson()
            val jsonString: String = gson.toJson(InvestmentJSON(investmentsCountData))
            println(jsonString)

            val url = "${CSP.getData("base_url")}/Audit.asmx/InvestmentElement_AuditAdd"

            val request_header: MediaType? = "application/text; charset=utf-8".toMediaTypeOrNull()

            var body: RequestBody = jsonString.toRequestBody(request_header)
            val request = Request.Builder().post(body).url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val tm = response.body?.string()
                    println(tm)
                    (context as Activity).runOnUiThread {
                        Navigation.findNavController(it).navigateUp()
                        fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Failed", "FAILED")
                    e.printStackTrace()
                    (context as Activity).runOnUiThread {
                        fragment.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}