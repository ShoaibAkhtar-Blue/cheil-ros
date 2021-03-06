package com.example.cheilros.fragments.storeview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.ActivityAdapter
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.ActivityTypeModel
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.RecentActivityModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_activity.rvRecentActivities
import kotlinx.android.synthetic.main.fragment_activity.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class ActivityFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: ActivityAdapter

    lateinit var layoutManagerRecent: RecyclerView.LayoutManager
    lateinit var recylcerAdapterRecent: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity, container, false)
        arguments?.getString("StoreName")?.let { configureToolbar(it, true, true) }
        toolbarVisibility(false)
        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Reset Session
        CSP.delData("ActivityDetail_BARCODE_SET")
        //endregion

        //region Set Labels
        view.txtStoreName.text =
            settingData.filter { it.fixedLabelName == "StoreMenu_Activity" }.get(0).labelName
        view.StoreView_SubTitle.text =
            settingData.filter { it.fixedLabelName == "Activity_SubTitle" }.get(0).labelName
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //txtStoreName.text = arguments?.getString("StoreName")

        if (team_type.toInt() <= 4)
            cvActivityData.visibility = View.GONE

        fetchActivity("${CSP.getData("base_url")}/Activity.asmx/ActivityTypeList?DivisionID=1")
        fetchRecentActivities(
            "${CSP.getData("base_url")}/OperMarketActivities.asmx/ViewMarketActivityList?StoreID=${
                arguments?.getInt(
                    "StoreID"
                ).toString()
            }&ActivityCategoryID=0&ActivityTypeID=0&BrandID=0&TeamMemberID=${CSP.getData("user_id")}"
        )

        requireActivity().toolbar_search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                if (team_type.toInt() <= 4)
                    recylcerAdapterRecent?.filter?.filter(qString)
                else
                    recylcerAdapter?.filter?.filter(qString)
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {

                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!CSP.getData("Activity_Followup_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapterRecent.addNewItem(
                    CSP.getData("Activity_Followup_SESSION_IMAGE").toString()
                )
                CSP.delData("Activity_Followup_SESSION_IMAGE")

                if (CSP.getData("Activity_Followup_SESSION_IMAGE").equals("")) {
                    recylcerAdapterRecent.addNewItem(
                        CSP.getData("Activity_Followup_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "Activity_Followup_SESSION_IMAGE_SET",
                        CSP.getData("Activity_Followup_SESSION_IMAGE")
                    )
                    CSP.delData("Activity_Followup_SESSION_IMAGE")
                } else {
                    recylcerAdapterRecent.addNewItem(
                        CSP.getData("Activity_Followup_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "Activity_Followup_SESSION_IMAGE_SET",
                        "${CSP.getData("Activity_Followup_SESSION_IMAGE_SET")},${CSP.getData("Activity_Followup_SESSION_IMAGE")}"
                    )
                    CSP.delData("Activity_Followup_SESSION_IMAGE")
                }
            } catch (ex: Exception) {

            }
        }
    }

    fun fetchActivity(url: String) {
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
                    val apiData = gson.fromJson(body, ActivityTypeModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvActivity.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvActivity.layoutManager = layoutManager
                            recylcerAdapter =
                                ActivityAdapter(requireContext(), apiData.data, arguments)
                            rvActivity.adapter = recylcerAdapter
                            mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                            toolbarVisibility(true)
                            (activity as NewDashboardActivity).shouldGoBack = true
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

    fun fetchRecentActivities(url: String) {
        println(url)
        val request = Request.Builder()
            .url(url)
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
                    val apiData = gson.fromJson(body, RecentActivityModel::class.java)

                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvRecentActivities.setHasFixedSize(true)
                            layoutManagerRecent = LinearLayoutManager(requireContext())
                            rvRecentActivities.layoutManager = layoutManagerRecent
                            recylcerAdapterRecent = RecentActivityAdapter(
                                requireContext(),
                                "activity",
                                apiData.data as MutableList<RecentActivityData>,
                                arguments,
                                requireActivity() as NewDashboardActivity,
                                userData
                            )
                            rvRecentActivities.adapter = recylcerAdapterRecent

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