package com.example.cheilros.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.models.*
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
    lateinit var channelTypeData:List<ChannelTypeData>

    var defaultChannel = "0"
    var defaultChannelType = "0"

    lateinit var recylcerAdapter: MyCoverageAdapter

    lateinit var locationManager: LocationManager

    var latitude: String = "0.0"
    var longitude: String = "0.0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_coverage, container, false)

        //region Reset Sessions
        CSP.delData("sess_last_update_element_id")
        //endregion

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
        fetchChannelType("${CSP.getData("base_url")}/Webservice.asmx/ChannelTypeList")
        fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=${defaultChannel}&SearchKeyWord=&ChannelTypeID=${defaultChannelType}")

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
                    defaultChannel = channelData[which].ChannelID.toString()
                    btnChannel.text = "Selected Channel: ${channelData[which].ChannelName}"
                    fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=${defaultChannel}&SearchKeyWord=&ChannelTypeID=${defaultChannelType}")
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()

            /*val colorList: ArrayList<ColorVO> = ArrayList<ColorVO>()
            // String array for alert dialog multi choice items
            // String array for alert dialog multi choice items
            val colors = arrayOf(
                "Red",
                "Green",
                "Blue",
                "Purple",
                "Olive"
            )
            // Boolean array for initial selected items
            // Boolean array for initial selected items
            val checkedColors = booleanArrayOf(
                false,  // Red
                false,  // Green
                false,  // Blue
                false,  // Purple
                false // Olive
            )

            val builder = AlertDialog.Builder(requireContext())

            // make a list to hold state of every color

            // make a list to hold state of every color
            for (i in colors.indices) {
                val colorVO = ColorVO()
                colorVO.setName(colors.get(i))
                colorVO.setSelected(checkedColors.get(i))
                colorList.add(colorVO)
            }

            // Do something here to pass only arraylist on this both arrays ('colors' & 'checkedColors')

            // Do something here to pass only arraylist on this both arrays ('colors' & 'checkedColors')
            builder.setMultiChoiceItems(colors, checkedColors,
                OnMultiChoiceClickListener { dialog, which, isChecked -> // set state to vo in list
                    colorList.get(which).setSelected(isChecked)
                    Toast.makeText(
                        ApplicationProvider.getApplicationContext(),
                        colorList.get(which).getName().toString() + " " + isChecked,
                        Toast.LENGTH_SHORT
                    ).show()
                })

            builder.setCancelable(false)

            builder.setTitle("Your preferred colors?")

            builder.setPositiveButton(
                "OK"
            ) { dialog, which ->
                txtSelected.setText("Your preferred colors..... \n")

                // save state of selected vos
                val selectedList: ArrayList<ColorVO> = ArrayList()
                for (i in 0 until colorList.size()) {
                    val colorVO: ColorVO = colorList.get(i)
                    colors.get(i) = colorVO.getName()
                    checkedColors.get(i) = colorVO.isSelected()
                    if (colorVO.isSelected()) {
                        selectedList.add(colorVO)
                    }
                }
                for (i in 0 until selectedList.size()) {
                    // if element is last then not attach comma or attach it
                    if (i != selectedList.size() - 1) txtSelected.setText(
                        txtSelected.getText() + selectedList[i].getName().toString() + " ,"
                    ) else txtSelected.setText(txtSelected.getText() + selectedList[i].getName())
                }
                colorList.clear()
            }

            builder.setNegativeButton(
                "No"
            ) { dialog, which -> // make sure to clear list that duplication dont formed here
                colorList.clear()
            }

            builder.setNeutralButton(
                "Cancel"
            ) { dialog, which -> // make sure to clear list that duplication dont formed here
                colorList.clear()
            }

            val dialog = builder.create()
            dialog.show()*/
        }

        btnChannelType.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose an channel type")

            // add a list

            // add a list
            var channels : Array<String> = arrayOf()
            for (c in channelTypeData){
                channels += c.ChannelTypeName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(channelTypeData[which].ChannelTypeID)
                    defaultChannelType = channelTypeData[which].ChannelTypeID.toString()
                    btnChannelType.text = "Selected Channel: ${channelTypeData[which].ChannelTypeName}"
                    fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=${defaultChannel}&SearchKeyWord=&ChannelTypeID=${defaultChannelType}")
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

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, ChannelTypeModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    channelTypeData = apiData.data
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
        println(url)
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
                        recylcerAdapter = MyCoverageAdapter(requireContext(), apiData.data, settingData, latitude, longitude)
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

