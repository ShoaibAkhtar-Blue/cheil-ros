package com.example.cheilros.fragments.training

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.adapters.TrainingStoresAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_my_coverage.btnChannel
import kotlinx.android.synthetic.main.fragment_my_coverage.btnChannelType
import kotlinx.android.synthetic.main.fragment_my_coverage.todo_list_empty_view
import kotlinx.android.synthetic.main.fragment_my_coverage.txtManagerCoverageCount
import kotlinx.android.synthetic.main.fragment_training_stores.*
import okhttp3.*
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat


class TrainingStoresFragment : BaseFragment() {

    lateinit var activity: NewDashboardActivity
    lateinit var uLocation: Location
    private val client = OkHttpClient()

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var channelData: List<ChannelData>
    lateinit var channelTypeData: List<ChannelTypeData>

    var defaultChannel = "0"
    var defaultChannelType = "0"

    lateinit var recylcerAdapter: TrainingStoresAdapter

    lateinit var locationManager: LocationManager

    var latitude: String = "0.0"
    var longitude: String = "0.0"

    var isLoc: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as NewDashboardActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_stores, container, false)

        toolbarVisibility(false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = rvTrainingStores
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

        fetchChannel("${CSP.getData("base_url")}/Webservice.asmx/ChannelList")
        fetchChannelType("${CSP.getData("base_url")}/Webservice.asmx/ChannelTypeList")
        fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=${defaultChannel}&SearchKeyWord=&ChannelTypeID=${defaultChannelType}")

    }

    fun fetchChannel(url: String) {

        mainLoadingLayoutCoverage.setState(LoadingLayout.LOADING)

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
                    val apiData = gson.fromJson(body, ChannelModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        channelData = apiData.data
                        try {
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    //mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
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

    fun fetchChannelType(url: String) {

        mainLoadingLayoutCoverage.setState(LoadingLayout.LOADING)

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

    fun fetchData(url: String) {
        println(url)
        mainLoadingLayoutCoverage.setState(LoadingLayout.LOADING)
        btnLocation.visibility = View.INVISIBLE
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
                    mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, MyCoverageModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        try {
                            val formatter1: NumberFormat = DecimalFormat("00000")
                            txtManagerCoverageCount.text =
                                formatter1.format(apiData.data.size.toInt())

                            requireActivity().runOnUiThread(java.lang.Runnable {
                                recylcerAdapter = TrainingStoresAdapter(
                                    requireContext(),
                                    apiData.data,
                                    settingData,
                                    latitude,
                                    longitude,
                                    this@TrainingStoresFragment,
                                    requireActivity() as NewDashboardActivity
                                )
                                recyclerView.adapter = recylcerAdapter

                                btnLocation.visibility = View.VISIBLE

                                mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)

                                toolbarVisibility(true)
                                (activity as NewDashboardActivity).shouldGoBack = true
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
                            }
                            mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
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