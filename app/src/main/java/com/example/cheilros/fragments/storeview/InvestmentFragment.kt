package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.InvestmentAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.InvestmentModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.txtStoreName
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_investment.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class InvestmentFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: InvestmentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try {
            view.txtStoreName.text =
                settingData.filter { it.fixedLabelName == "StoreMenu_Investment" }.get(0).labelName
        } catch (ex: Exception) {

        }
        //endregion


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //txtStoreName.text = arguments?.getString("StoreName")
        println(
            "${CSP.getData("base_url")}/Audit.asmx/InvestmentElement_AuditView?StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }"
        )
        fetchInvestment(
            "${CSP.getData("base_url")}/Audit.asmx/InvestmentElement_AuditView?StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }"
        )
    }

    companion object {

    }

    fun fetchInvestment(url: String) {
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
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, InvestmentModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvInvestment.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvInvestment.layoutManager = layoutManager
                            recylcerAdapter = InvestmentAdapter(
                                requireContext(),
                                apiData.data,
                                arguments?.getInt("StoreID")
                            )
                            rvInvestment.adapter = recylcerAdapter
                            mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                        })
                    } else {
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
                } catch (ex: Exception) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage(ex.message.toString())
                                .sneakError()
                        }
                        findNavController().popBackStack()
                    })
                }
            }
        })
    }
}