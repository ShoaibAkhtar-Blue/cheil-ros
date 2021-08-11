package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.DisplayCountAdapter
import com.example.cheilros.adapters.PriceAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.DisplayCountModel
import com.example.cheilros.models.PriceModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_display_count.*
import kotlinx.android.synthetic.main.fragment_display_count.rvDisplayCount
import kotlinx.android.synthetic.main.fragment_price.*
import okhttp3.*
import java.io.IOException

class PriceFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: PriceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_price, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try{
            view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_PricePromotions" }.get(0).labelName
        }catch (ex: Exception){

        }
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchPrices("${CSP.getData("base_url")}/Prices.asmx/PricePromotionSummary")
    }


    fun fetchPrices(url: String){
        println(url)
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread(java.lang.Runnable {
                    activity?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Error!!")
                            .setMessage(e.message.toString())
                            .sneakError()
                    }
                    mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, PriceModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvPrice.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvPrice.layoutManager = layoutManager
                        recylcerAdapter = PriceAdapter(requireContext(), apiData.data, arguments?.getInt("StoreID"))
                        rvPrice.adapter = recylcerAdapter
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }

}