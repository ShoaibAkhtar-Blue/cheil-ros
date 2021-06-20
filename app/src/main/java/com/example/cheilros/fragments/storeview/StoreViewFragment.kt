package com.example.cheilros.fragments.storeview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.adapters.RecentTrainingAdapter
import com.example.cheilros.adapters.StoreMenuAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.RecentActivityModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_store_view.*
import kotlinx.android.synthetic.main.fragment_store_view.view.*
import kotlinx.android.synthetic.main.fragment_training.*
import okhttp3.*
import java.io.IOException


class StoreViewFragment : BaseFragment() {

    lateinit var storemenuAdapter: StoreMenuAdapter

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_store_view, container, false)

        configureToolbar("${arguments?.getString("StoreName")}", true)

        StoreView_SubTitle
        //region Set Labels
        view.StoreView_SubTitle.text = settingData.filter { it.fixedLabelName == "StoreView_SubTitle" }.get(0).labelName
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //var menuDataList: List<AppSetting> = settingData.filter { it.screenName == "StoreView" }
        storemenuAdapter = StoreMenuAdapter(requireContext(), settingData.filter { it.screenName == "StoreView" }, arguments)
        rvStoreMenu!!.adapter = storemenuAdapter

        fetchRecentActivities("${CSP.getData("base_url")}/OperMarketActivities.asmx/ViewMarketActivityList?StoreID=${arguments?.getInt("StoreID").toString()}&ActivityCategoryID=0&ActivityTypeID=0&BrandID=0&TeamMemberID=${CSP.getData("user_id")}")
    }

    fun fetchRecentActivities(url: String){
        println(url)
        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()

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
                val apiData = gson.fromJson(body, RecentActivityModel::class.java)

                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvRecentActivities.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvRecentActivities.layoutManager = layoutManager
                        recylcerAdapter = RecentActivityAdapter(requireContext(), apiData.data, arguments)
                        rvRecentActivities.adapter = recylcerAdapter

                    })
                }else{
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