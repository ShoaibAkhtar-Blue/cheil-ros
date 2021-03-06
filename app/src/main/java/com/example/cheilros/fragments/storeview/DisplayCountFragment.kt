package com.example.cheilros.fragments.storeview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.DisplayCountAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.DisplayCountModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_display_count.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class DisplayCountFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: DisplayCountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_display_count, container, false)
        toolbarVisibility(false)
        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try {
            view.txtStoreName.text =
                settingData.filter { it.fixedLabelName == "StoreMenu_ModelCount" }.get(0).labelName
        } catch (ex: Exception) {

        }
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchDisplayCount(
            "${CSP.getData("base_url")}/DisplayCount.asmx/DisplayCountSummary?StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }"
        )
    }

    fun fetchDisplayCount(url: String) {
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
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, DisplayCountModel::class.java)
                    if (apiData.status == 200) {
                        try {
                            if (requireActivity() == null) {
                                return;
                            }
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                rvDisplayCount.setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(requireContext())
                                rvDisplayCount.layoutManager = layoutManager
                                recylcerAdapter = DisplayCountAdapter(
                                    requireContext(),
                                    apiData.data,
                                    arguments?.getInt("StoreID"),
                                    arguments?.getString("StoreName"),
                                    settingData
                                )
                                rvDisplayCount.adapter = recylcerAdapter
                                mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                                toolbarVisibility(true)
                                (activity as NewDashboardActivity).shouldGoBack = true
                            })
                        } catch (ex: java.lang.Exception) {

                        }
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