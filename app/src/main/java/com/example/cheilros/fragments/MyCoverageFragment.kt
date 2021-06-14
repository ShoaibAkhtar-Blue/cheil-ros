package com.example.cheilros.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.ChannelData
import com.example.cheilros.models.ChannelModel
import com.example.cheilros.models.MyCoverageModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_my_coverage.view.*
import okhttp3.*
import java.io.IOException


class MyCoverageFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var channelData:List<ChannelData>

    lateinit var recylcerAdapter: MyCoverageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_coverage, container, false)

        //region Set Labels
        view.btnChannel.text = settingData.filter { it.fixedLabelName == "StoreList_SearchBox" }.get(0).labelName
        //endregion

        requireActivity().title = "My Coverage"
        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        recyclerView= rvCoverage
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        layoutManager= LinearLayoutManager(requireContext())
        recyclerView.layoutManager=layoutManager

        val emptyView: View = todo_list_empty_view
        recyclerView.setEmptyView(emptyView)

        fetchChannel("${CSP.getData("base_url")}/Webservice.asmx/ChannelList")
        fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=0&SearchKeyWord=")

        btnChannel.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an channel")

            // add a list

            // add a list
            var channels : Array<String> = arrayOf()
            for (c in channelData){
                channels += c.ChannelName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(channelData[which].ChannelID)
                    btnChannel.text = "Selected Channel: ${channelData[which].ChannelName}"
                    fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=${channelData[which].ChannelID}&SearchKeyWord=")
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

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

        /*etSearch.doOnTextChanged { text, start, before, count ->
            recylcerAdapter?.filter?.filter(text)
        }*/
    }

    companion object {

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

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, ChannelModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    channelData = apiData.data
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                        }
                    })
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
            }
        })
    }
    fun fetchData(url: String) {

        mainLoadingLayoutCoverage.setState(LoadingLayout.LOADING)
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
                    mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, MyCoverageModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        recylcerAdapter = MyCoverageAdapter(requireContext(), apiData.data, settingData)
                        recyclerView.adapter = recylcerAdapter
                        mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                    })
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
            }
        })
    }
}

