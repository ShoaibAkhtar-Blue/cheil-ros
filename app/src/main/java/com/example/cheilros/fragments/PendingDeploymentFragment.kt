package com.example.cheilros.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.PendingDeploymentAdapter
import com.example.cheilros.models.ActivityCategoryData
import com.example.cheilros.models.ActivityCategoryModel
import com.example.cheilros.models.PendingDeploymentModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_activity_sub_category.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_pending_deployment.*
import kotlinx.android.synthetic.main.fragment_pending_deployment.todo_list_empty_view
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class PendingDeploymentFragment : BaseFragment() {

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var activityData: List<ActivityCategoryData>
    var defaultActivity = "0"

    lateinit var recylcerAdapter: PendingDeploymentAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pending_deployment, container, false)

        //requireActivity().toolbar_title.text = "Deployment Pending"
        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = rvPendingDeployment
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

        fetchActivityCategory("${CSP.getData("base_url")}/Activity.asmx/ActivityCategoryList?DivisionID=1&ActivityTypeID=21")
        fetchPendingDeployment(
            "${CSP.getData("base_url")}/Webservice.asmx/PendingDeployment?TeamMemberID=${
                CSP.getData(
                    "user_id"
                )
            }&ActivityCategory=${defaultActivity}"
        )
        btnActivity.setOnClickListener {
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
                    btnActivity.text = "${activityData[which].ActivityCategoryName}"
                    fetchPendingDeployment(
                        "${CSP.getData("base_url")}/Webservice.asmx/PendingDeployment?TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&ActivityCategory=${defaultActivity}"
                    )
                })

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun fetchActivityCategory(url: String) {
        println(url)
        val client = OkHttpClient()
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
                            btnActivity.text = activityData[0].ActivityCategoryName
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
        val client = OkHttpClient()
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
                                this@PendingDeploymentFragment,
                                requireActivity() as NewDashboardActivity
                            )
                            recyclerView.adapter = recylcerAdapter
                            mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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