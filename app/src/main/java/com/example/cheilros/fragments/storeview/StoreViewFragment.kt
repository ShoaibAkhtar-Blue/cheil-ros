package com.example.cheilros.fragments.storeview

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.ChecklistAnsweredAdapter
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.adapters.StoreMenuAdapter
import com.example.cheilros.adapters.StoreViewTabsPagerAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.CheckListAnswerModel
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.RecentActivityModel
import com.example.cheilros.models.StoreInfoModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_store_view.*
import kotlinx.android.synthetic.main.fragment_store_view.view.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class StoreViewFragment : BaseFragment() {

    lateinit var storemenuAdapter: StoreMenuAdapter

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: RecentActivityAdapter

    lateinit var layoutManagerCL: RecyclerView.LayoutManager
    lateinit var recylcerAdapterCL: ChecklistAnsweredAdapter

    var shouldAllowBack = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_store_view, container, false)

        toolbarVisibility(false)
        configureToolbar("${arguments?.getString("StoreName")}", true)

        //region Reset Sessions
        CSP.delData("sess_last_update_element_id")
        CSP.delData("activity_barcodes")
        CSP.delData("ActivityDetail_BARCODE_SET")
        CSP.delData("ActivityDetail_SESSION_IMAGE")
        CSP.delData("ActivityDetail_SESSION_IMAGE_SET")
        CSP.delData("salesData")
        CSP.delData("selectedStores")
        //endregion

        //StoreView_SubTitle
        //region Set Labels
        try {
            view.StoreMenu_Investment.text =
                settingData.filter { it.fixedLabelName == "StoreView_InvesmentSubTitle" }[0].labelName
            view.StoreView_SubTitle.text =
                settingData.filter { it.fixedLabelName == "StoreView_SubTitle" }.get(0).labelName
            view.StoreView_SubTitle.text =
                settingData.filter { it.fixedLabelName == "StoreView_ChecklistSubTitle" }
                    .get(0).labelName

            view.txtNoRecord.text =
                settingData.filter { it.fixedLabelName == "General_NoRecordFound" }[0].labelName
            view.StoreSummary_Region.text =
                settingData.filter { it.fixedLabelName == "StoreSummary_Region" }[0].labelName
            view.StoreSummary_StoreType.text =
                settingData.filter { it.fixedLabelName == "StoreSummary_StoreType" }[0].labelName
            view.StoreSummary_CityDistrict.text =
                settingData.filter { it.fixedLabelName == "StoreSummary_CityDistrict" }[0].labelName
        } catch (ex: Exception) {

        }

        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated")
        //var menuDataList: List<AppSetting> = settingData.filter { it.screenName == "StoreView" }
        storemenuAdapter = StoreMenuAdapter(
            requireContext(),
            settingData.filter { it.screenName == "StoreView" }.sortedBy { it.labelID },
            arguments,
            settingData
        )
        rvStoreMenu!!.adapter = storemenuAdapter

        fetchChecklistanswer(
            "${CSP.getData("base_url")}/Checklist.asmx/ChecklistAnswered?StoreID=${
                arguments?.getInt(
                    "StoreID"
                ).toString()
            }&TeamMemberID=${CSP.getData("user_id")}"
        )
        //fetchRecentActivities("${CSP.getData("base_url")}/OperMarketActivities.asmx/ViewMarketActivityList?StoreID=${arguments?.getInt("StoreID").toString()}&ActivityCategoryID=0&ActivityTypeID=0&BrandID=0&TeamMemberID=${CSP.getData("user_id")}")
        fetchStoreInfo(
            "${CSP.getData("base_url")}/Storelist.asmx/StoreInfo?StoreID=${
                arguments?.getInt(
                    "StoreID"
                ).toString()
            }&TeamMemberID=${CSP.getData("user_id")}"
        )

        if (CSP.getData("team_type_id")!!.toInt() <= 4)
            btnEditChecklist.visibility = View.INVISIBLE

        btnEditChecklist.setOnClickListener {
            val bundle = bundleOf(
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )
            findNavController().navigate(
                R.id.action_storeViewFragment_to_checklistCategoryFragment,
                bundle
            )
        }

        genTabs()


    }

    override fun onResume() {
        super.onResume()
        //(activity as NewDashboardActivity).shouldGoBack = true
    }

    private fun genTabs() {
        //region Store Tabs

        tab_layout.setSelectedTabIndicatorColor(Color.RED)
        tab_layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black))
        tab_layout.tabTextColors =
            ContextCompat.getColorStateList(requireContext(), android.R.color.white)


        // Number Of Tabs
        val numberOfTabs = 2

        // Show all Tabs in screen
        tab_layout.tabMode = TabLayout.MODE_FIXED

        // Scroll to see all Tabs
        //tab_layout.tabMode = TabLayout.MODE_SCROLLABLE

        // Set Tab icons next to the text, instead above the text
        tab_layout.isInlineLabel = true

        var fr_list: MutableList<Fragment> = emptyArray<Fragment>().toMutableList()
        fr_list.clear()
        fr_list.add(
            StoreStatusFragment(
                arguments?.getInt("StoreID"),
                arguments?.getString("StoreName")
            )
        )
        fr_list.add(
            StoreActiveAssetsFragment(
                arguments?.getInt("StoreID"),
                arguments?.getString("StoreName")
            )
        )

        // Set the ViewPager Adapter
        tabs_viewpager.adapter = null
        val adapter = StoreViewTabsPagerAdapter(
            requireActivity().supportFragmentManager,
            lifecycle,
            numberOfTabs,
            arguments,
            fr_list,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        tabs_viewpager.adapter = adapter


        //tabs_viewpager.currentItem = 1

        // Enable Swipe
        tabs_viewpager.isUserInputEnabled = true

        // Enable Swipe
        tabs_viewpager.isUserInputEnabled = true

        // Link the TabLayout and the ViewPager2 together and Set Text & Icons
        TabLayoutMediator(tab_layout, tabs_viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text =
                        settingData.filter { it.fixedLabelName == "StoreView_SubTitleTAB1" }[0].labelName
                }
                1 -> {
                    tab.text =
                        settingData.filter { it.fixedLabelName == "StoreView_SubTitleTAB2" }[0].labelName
                }
            }
            // Change color of the icons
            tab.icon?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    Color.WHITE,
                    BlendModeCompat.SRC_ATOP
                )
        }.attach()

        setCustomTabTitles()

        //endregion
    }

    private fun setCustomTabTitles() {
        val vg = tab_layout.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount

        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup

            val tabChildCount = vgTab.childCount

            for (i in 0 until tabChildCount) {
                val tabViewChild = vgTab.getChildAt(i)
                if (tabViewChild is TextView) {

                    // Change Font and Size
                    tabViewChild.typeface = Typeface.DEFAULT_BOLD
//                    val font = ResourcesCompat.getFont(this, R.font.myFont)
//                    tabViewChild.typeface = font
//                    tabViewChild.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
                }
            }
        }
    }

    fun fetchStoreInfo(url: String) {
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
                    mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, StoreInfoModel::class.java)

                    if (apiData.status == 200) {
                        try {
                            if (requireActivity() == null) {
                                return
                            }
                        } catch (ex: Exception) {
                            return
                        }

                        requireActivity().runOnUiThread(java.lang.Runnable {
                            txtStoreRegion.text = apiData.data[0].RegionName
                            txtStoreType.text = apiData.data[0].StoreTypeName
                            txtStoreGrade.text = apiData.data[0].GradeName
                            txtStoreCity.text = apiData.data[0].DistrcitName
                            txtStoreDistributor.text = apiData.data[0].DistributorName

                            CSP.saveData("SaleType", apiData.data[0].SaleType)

                            toolbarVisibility(true)
                            (activity as NewDashboardActivity).shouldGoBack = true
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
                    mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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
                            layoutManager = LinearLayoutManager(requireContext())
                            rvRecentActivities.layoutManager = layoutManager
                            recylcerAdapter =
                                RecentActivityAdapter(
                                    requireContext(),
                                    "storeview",
                                    apiData.data as MutableList<RecentActivityData>,
                                    arguments,
                                    requireActivity() as NewDashboardActivity,
                                    userData
                                )
                            rvRecentActivities.adapter = recylcerAdapter

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

    fun fetchChecklistanswer(url: String) {
        mainLoadingLayout.setState(LoadingLayout.LOADING)
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
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, CheckListAnswerModel::class.java)
                    if (apiData.status == 200) {
                        try {
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                rvChecklistAnswer.setHasFixedSize(true)
                                layoutManagerCL = LinearLayoutManager(requireContext())
                                rvChecklistAnswer.layoutManager = layoutManagerCL
                                recylcerAdapterCL =
                                    ChecklistAnsweredAdapter(requireContext(), apiData.data)
                                rvChecklistAnswer.adapter = recylcerAdapterCL
                                val emptyView: View = todo_list_empty_view1
                                rvChecklistAnswer.setEmptyView(emptyView)
                            })
                        } catch (ex: Exception) {
                            /*requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Issue in loading data.")
                                        .sneakWarning()
                                }
                                mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                            })*/
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