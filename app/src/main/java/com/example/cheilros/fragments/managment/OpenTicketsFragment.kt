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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.PendingDeploymentAdapter
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_open_tickets.*
import kotlinx.android.synthetic.main.fragment_open_tickets.btnChannelType
import kotlinx.android.synthetic.main.fragment_open_tickets.mainLoadingLayout
import kotlinx.android.synthetic.main.fragment_open_tickets.txtManagerDeploymentCount
import kotlinx.android.synthetic.main.fragment_pending_deployment.*
import kotlinx.android.synthetic.main.fragment_pending_task.*
import okhttp3.*
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class OpenTicketsFragment : BaseFragment() {

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: RecentActivityAdapter

    private val client = OkHttpClient()

    lateinit var channelData: List<ChannelData>
    lateinit var channelTypeData: List<ChannelTypeData>

    var defaultChannel = "0"
    var defaultChannelType = "0"
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_open_tickets, container, false)


        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }
        
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchChannel("${CSP.getData("base_url")}/Webservice.asmx/ChannelList")
        fetchChannelType("${CSP.getData("base_url")}/Webservice.asmx/ChannelTypeList")
        fetchOpenTickets("${CSP.getData("base_url")}/Dashboard.asmx/ManagmentOpenIssues?RegionID=2&ChannelTypeID=${defaultChannelType}&ChannelID=$defaultChannel")

        btnChannel.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list

            // add a list
            var channels: Array<String> = arrayOf()
            for (c in channelData) {
                channels += c.ChannelName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(channelData[which].ChannelID)
                    defaultChannel = channelData[which].ChannelID.toString()
                    btnChannel.text = "${channelData[which].ChannelName}"
                    fetchOpenTickets("${CSP.getData("base_url")}/Dashboard.asmx/ManagmentOpenIssues?RegionID=2&ChannelTypeID=${defaultChannelType}&ChannelID=$defaultChannel")
                })

            // create and show the alert dialog

            // create and show the alert dialog
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
                    fetchOpenTickets("${CSP.getData("base_url")}/Dashboard.asmx/ManagmentOpenIssues?RegionID=2&ChannelTypeID=${defaultChannelType}&ChannelID=$defaultChannel")
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

    fun fetchChannel(url: String) {

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
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, ChannelModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        channelData = apiData.data
                        try {
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    //mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                                    try {
                                        btnChannelType.text = channelData[0].ChannelName
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
                                mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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

    fun fetchChannelType(url: String) {

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
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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
                                    //mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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
                                mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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

    fun fetchOpenTickets(url: String) {
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
                    val apiData = gson.fromJson(body, RecentActivityModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvOpenTickets.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvOpenTickets.layoutManager = layoutManager
                            recylcerAdapter = RecentActivityAdapter(
                                requireContext(),
                                "dashboard",
                                apiData.data as MutableList<RecentActivityData>,
                                arguments,
                                requireActivity() as NewDashboardActivity,
                                userData
                            )
                            rvOpenTickets.adapter = recylcerAdapter

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
                } catch (ex: java.lang.Exception) {
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