package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.ActivityAdapter
import com.example.cheilros.adapters.ChecklistAdapter
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.ActivityTypeModel
import com.example.cheilros.models.CheckListModel
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.RecentActivityModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_activity.rvRecentActivities
import kotlinx.android.synthetic.main.fragment_activity.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.txtStoreName
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_store_view.*
import okhttp3.*
import java.io.IOException

class ActivityFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: ActivityAdapter

    lateinit var layoutManagerRecent: RecyclerView.LayoutManager
    lateinit var recylcerAdapterRecent: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_activity, container, false)
        arguments?.getString("StoreName")?.let { configureToolbar(it, true, true) }

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Activity" }.get(0).labelName
        view.StoreView_SubTitle.text = settingData.filter { it.fixedLabelName == "Activity_SubTitle" }.get(0).labelName
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //txtStoreName.text = arguments?.getString("StoreName")
        fetchActivity("${CSP.getData("base_url")}/Activity.asmx/ActivityTypeList?DivisionID=1")
        fetchRecentActivities("${CSP.getData("base_url")}/OperMarketActivities.asmx/ViewMarketActivityList?StoreID=${arguments?.getInt("StoreID").toString()}&ActivityCategoryID=0&ActivityTypeID=0&BrandID=0&TeamMemberID=${CSP.getData("user_id")}")

        requireActivity().toolbar_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                recylcerAdapter?.filter?.filter(qString)
                return true
            }
            override fun onQueryTextSubmit(qString: String): Boolean {

                return true
            }
        })
    }

    fun fetchActivity(url: String){
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
                val apiData = gson.fromJson(body, ActivityTypeModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvActivity.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvActivity.layoutManager = layoutManager
                        recylcerAdapter = ActivityAdapter(requireContext(), apiData.data, arguments)
                        rvActivity.adapter = recylcerAdapter
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
                        layoutManagerRecent = LinearLayoutManager(requireContext())
                        rvRecentActivities.layoutManager = layoutManagerRecent
                        recylcerAdapterRecent = RecentActivityAdapter(requireContext(),
                            apiData.data as MutableList<RecentActivityData>, arguments)
                        rvRecentActivities.adapter = recylcerAdapterRecent

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