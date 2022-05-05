package com.example.cheilros.fragments.managment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.PendingDeploymentAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_pending_deployment.mainLoadingLayout
import kotlinx.android.synthetic.main.fragment_pending_deployment.rvPendingDeployment
import kotlinx.android.synthetic.main.fragment_pending_deployment.todo_list_empty_view
import kotlinx.android.synthetic.main.fragment_pending_task.*
import kotlinx.android.synthetic.main.fragment_pending_task.btnChannelType
import kotlinx.android.synthetic.main.fragment_pending_task.txtManagerDeploymentCount
import kotlinx.android.synthetic.main.fragment_team_status.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit


class PendingTaskFragment : BaseFragment() {

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var activityData: List<ActivityCategoryData>
    lateinit var channelTypeData: List<ChannelTypeData>
    var defaultActivity = "0"
    var defaultChannelType = "0"

    lateinit var recylcerAdapter: PendingDeploymentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pending_task, container, false)
        toolbarVisibility(false)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = rvPendingTask
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        val emptyView: View = todo_list_empty_view
        recyclerView.setEmptyView(emptyView)
        fetchChannelType("${CSP.getData("base_url")}/Webservice.asmx/ChannelTypeList")
        fetchActivityCategory("${CSP.getData("base_url")}/Activity.asmx/ActivityCategoryList?DivisionID=1&ActivityTypeID=21")

        fetchPendingDeployment(
            "${CSP.getData("base_url")}/Dashboard.asmx/ManagmentPendingDeployment?TeamMemberID=${
                CSP.getData(
                    "user_id"
                )
            }&RegionID=1&ChannelTypeID=$defaultChannelType&ChannelID=1&ActivityCategory=${defaultActivity}"
        )
        btnTaskType.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list
            var channels: Array<String> = arrayOf()
            for (c in activityData) {
                channels += c.ActivityCategoryName
            }
            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(activityData[which].ActivityCategoryID)
                    defaultActivity = activityData[which].ActivityCategoryID.toString()
                    btnTaskType.text = "${activityData[which].ActivityCategoryName}"
                    fetchPendingDeployment(
                        "${CSP.getData("base_url")}/Dashboard.asmx/ManagmentPendingDeployment?TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&RegionID=1&ChannelTypeID=$defaultChannelType&ChannelID=1&ActivityCategory=${defaultActivity}"
                    )
                })

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        btnChannelType.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list

            // add a list
            var channels: Array<String> = arrayOf()
            for (c in channelTypeData) {
                channels += c.ChannelTypeName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(channelTypeData[which].ChannelTypeID)
                    defaultChannelType = channelTypeData[which].ChannelTypeID.toString()
                    btnChannelType.text = "${channelTypeData[which].ChannelTypeName}"
                    fetchPendingDeployment(
                        "${CSP.getData("base_url")}/Dashboard.asmx/ManagmentPendingDeployment?TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&RegionID=1&ChannelTypeID=$defaultChannelType&ChannelID=1&ActivityCategory=${defaultActivity}"
                    )
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        requireActivity().toolbar_search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                try {
                    recylcerAdapter?.filter?.filter(qString)
                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                }

                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {

                return true
            }
        })
    }

    fun fetchChannelType(url: String) {
        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()
        mainLoadingLayout.setState(LoadingLayout.LOADING)

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
                        mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, ChannelTypeModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        channelTypeData = apiData.data
                        try {
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    //mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                                    try {
                                        btnChannel.text = channelTypeData[0].ChannelTypeName
                                    } catch (ex: Exception) {

                                    }
                                }
                            })
                        } catch (ex: Exception) {
//                        requireActivity().runOnUiThread(java.lang.Runnable {
//                            Toast.makeText(context, "Error ${ex.message}", Toast.LENGTH_SHORT).show()
//                        })
                        }

                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                                mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                            }
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

    fun fetchActivityCategory(url: String) {
        println(url)
        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()
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
                    mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, ActivityCategoryModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activityData = apiData.data
                        try {
                            btnTaskType.text = activityData[0].ActivityCategoryName
                        } catch (ex: Exception) {

                        }
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }

    fun fetchPendingDeployment(url: String) {
        println(url)
        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()
        mainLoadingLayout.setState(LoadingLayout.LOADING)
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
                    mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, PendingDeploymentModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            recylcerAdapter = PendingDeploymentAdapter(
                                requireContext(),
                                apiData.data,
                                settingData,
                                requireActivity() as NewDashboardActivity
                            )
                            recyclerView.adapter = recylcerAdapter

                            val formatter1: NumberFormat = DecimalFormat("00000")
                            txtManagerDeploymentCount.text = formatter1.format(apiData.data.size.toInt())

                            mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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
                            mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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