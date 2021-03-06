package com.example.cheilros.fragments.training

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.adapters.TrainingStoresAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
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
import java.util.concurrent.TimeUnit


class TrainingStoresFragment : BaseFragment() {

    lateinit var activity: NewDashboardActivity
    lateinit var uLocation: Location
    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var channelData: List<ChannelData>
    lateinit var channelTypeData: List<ChannelTypeData>

    val selectStores= mutableListOf<SelectedMyCoverageData>()

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

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }

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
                    fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=${defaultChannel}&SearchKeyWord=&ChannelTypeID=${defaultChannelType}")
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

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

    fun fetchData(url: String) {
        println(url)
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



                                mainLoadingLayout.setState(LoadingLayout.COMPLETE)

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