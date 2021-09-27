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
import com.example.cheilros.adapters.InstallationAdapter
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.ActivityCategoryModel
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.RecentActivityModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_activity.view.*
import kotlinx.android.synthetic.main.fragment_activity_sub_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import okhttp3.*
import java.io.IOException


class TaskDeploymentFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: InstallationAdapter

    lateinit var layoutManagerRecent: RecyclerView.LayoutManager
    lateinit var recylcerAdapterRecent: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_deployment, container, false)
        toolbarVisibility(false)
        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        view.txtStoreName.text =
            settingData.filter { it.fixedLabelName == "StoreMenu_Installation" }.get(0).labelName
        view.StoreView_SubTitle.text =
            settingData.filter { it.fixedLabelName == "StoreView_SubTitle" }.get(0).labelName
        //endregion

        CSP.delData("activity_barcodes")
        CSP.delData("ActivityDetail_SESSION_IMAGE")
        CSP.delData("ActivityDetail_SESSION_IMAGE_SET")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtStoreSubName.text = arguments?.getString("ActivityTypeName")

        println(
            "TaskDeploymentCategoryID: ${
                arguments?.getInt("TaskDeploymentCategoryID").toString()
            }"
        )


        fetchRecentActivities(
            "${CSP.getData("base_url")}/OperMarketActivities.asmx/ViewMarketActivityList?StoreID=${
                arguments?.getInt(
                    "StoreID"
                ).toString()
            }&ActivityCategoryID=0&ActivityTypeID=21&BrandID=0&TeamMemberID=${CSP.getData("user_id")}"
        )
        fetchActivityDetail(
            "${CSP.getData("base_url")}/Activity.asmx/ActivityCategoryList?DivisionID=1&ActivityTypeID=21"
        )

        requireActivity().toolbar_search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
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

    override fun onResume() {
        super.onResume()
        if (!CSP.getData("Task_Followup_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapterRecent.addNewItem(
                    CSP.getData("Task_Followup_SESSION_IMAGE").toString()
                )
                CSP.delData("Task_Followup_SESSION_IMAGE")

                if (CSP.getData("Task_Followup_SESSION_IMAGE").equals("")) {
                    recylcerAdapterRecent.addNewItem(
                        CSP.getData("Task_Followup_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "Task_Followup_SESSION_IMAGE_SET",
                        CSP.getData("Task_Followup_SESSION_IMAGE")
                    )
                    CSP.delData("Task_Followup_SESSION_IMAGE")
                } else {
                    recylcerAdapterRecent.addNewItem(
                        CSP.getData("Task_Followup_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "Task_Followup_SESSION_IMAGE_SET",
                        "${CSP.getData("Task_Followup_SESSION_IMAGE_SET")},${CSP.getData("Task_Followup_SESSION_IMAGE")}"
                    )
                    CSP.delData("Task_Followup_SESSION_IMAGE")
                }
            } catch (ex: Exception) {

            }
        }
    }

    fun fetchActivityDetail(url: String) {
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
                    val apiData = gson.fromJson(body, ActivityCategoryModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvActivityDetail.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvActivityDetail.layoutManager = layoutManager
                            recylcerAdapter =
                                InstallationAdapter(
                                    requireContext(),
                                    apiData.data.filter {
                                        it.TaskDeploymentCategoryID == arguments?.getInt("TaskDeploymentCategoryID")
                                    },
                                    arguments
                                )
                            rvActivityDetail.adapter = recylcerAdapter
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
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, RecentActivityModel::class.java)

                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvRecentSubActivities.setHasFixedSize(true)
                            layoutManagerRecent = LinearLayoutManager(requireContext())
                            rvRecentSubActivities.layoutManager = layoutManagerRecent
                            recylcerAdapterRecent = RecentActivityAdapter(
                                requireContext(),
                                "task",
                                apiData.data as MutableList<RecentActivityData>,
                                arguments,
                                requireActivity() as NewDashboardActivity,
                                userData
                            )
                            rvRecentSubActivities.adapter = recylcerAdapterRecent

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