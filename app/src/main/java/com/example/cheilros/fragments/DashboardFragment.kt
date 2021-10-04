package com.example.cheilros.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.MenuNavigationAdapter
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.adapters.TaskAssignedAdapter
import com.example.cheilros.data.AppSetting
import com.example.cheilros.models.*
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_dashboard.view.gridview
import kotlinx.android.synthetic.main.activity_dashboard.view.imgUser
import kotlinx.android.synthetic.main.activity_dashboard.view.txtUsername
import kotlinx.android.synthetic.main.fragment_activity.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.StoreView_SubTitle
import kotlinx.android.synthetic.main.fragment_dashboard.view.mainLoadingLayoutCC
import okhttp3.*
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.data.LineData

import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator


class DashboardFragment : BaseFragment() {

    var gridView: GridView? = null
    var menuData: java.util.ArrayList<MenuNavigationModel>? = null
    var adapter: MenuNavigationAdapter? = null

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TaskAssignedAdapter

    lateinit var layoutManagerRecent: RecyclerView.LayoutManager
    lateinit var recylcerAdapterRecent: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //team_type = "7"
        println("team_type: ${team_type}")
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        (activity as NewDashboardActivity).shouldGoBack = true

        val simpleDateFormat = SimpleDateFormat("E dd, MMMM")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        //region Set Labels
        try {
            //view.Dashboard_TodayVisit.text = settingData.filter { it.fixedLabelName == "Dashboard_TodayVisit" }.get(0).labelName
            view.Dashboard_StorePlan.text =
                settingData.filter { it.fixedLabelName == "Dashboard_StorePlan" }.get(0).labelName
            view.Dashboard_GraphTitle.text =
                settingData.filter { it.fixedLabelName == "Dashboard_GraphTitle" }.get(0).labelName
            view.Dashboard_TaskTitle.text =
                settingData.filter { it.fixedLabelName == "Dashboard_TaskTitle" }.get(0).labelName
            view.Dashboard_CoverageButton.text =
                settingData.filter { it.fixedLabelName == "Dashboard_CoverageButton" }
                    .get(0).labelName
            view.Dashboard_PendingButton.text =
                settingData.filter { it.fixedLabelName == "Dashboard_PendingButton" }
                    .get(0).labelName
            view.txtCurrentDate.text = currentDateAndTime
            view.StoreView_SubTitle.text =
                settingData.filter { it.fixedLabelName == "Dashboard_TaskTitle" }.get(0).labelName

            view.LLGraphTwo.visibility = View.GONE
            view.LLGraphOne.visibility = View.GONE

            view.LLTask.visibility = View.GONE

            //If user is Manager
            if(team_type.toInt() <= 4){
                view.cvJourneyPlan.visibility = View.GONE
                view.LLNormalCard.visibility = View.GONE
                view.gridview.visibility = View.GONE
                view.LLGraphThree.visibility = View.GONE
                view.LLGraphOne.visibility = View.GONE
                view.LLGraphTwo.visibility = View.GONE
                view.LLTask.visibility = View.GONE
                view.LLRecentActivity.visibility = View.GONE
            }else{
                view.LLNormalCardOne.visibility = View.GONE
                view.LLNormalCardTwo.visibility = View.GONE
                view.LLManagerGraphSales.visibility = View.GONE
                view.LLManagerGraphShares.visibility = View.GONE
                view.LLManagerActivities.visibility = View.GONE
            }

            if (team_type == "7") {
                view.Dashboard_GraphTitle.text =
                    settingData.filter { it.fixedLabelName == "DashboadPromoter_Visit" }
                        .get(0).labelName
                view.DashboadPromoter_Performance.text =
                    settingData.filter { it.fixedLabelName == "DashboadPromoter_Performance" }
                        .get(0).labelName
                view.LLGraphTwo.visibility = View.VISIBLE
                view.txtTodayVisitCount.visibility = View.GONE
                view.Dashboard_StorePlan.visibility = View.GONE
                view.LLTask.visibility = View.GONE
                view.LLRecentActivity.visibility = View.GONE
                view.gridview.visibility = View.GONE
            }
        } catch (ex: Exception) {
            Log.e("Error_", ex.message.toString())
        }
        //endregion

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //region Clear Sessions
        CSP.delData("fragName")
        //endregion

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)




        Glide.with(this)
            .load("${CSP.getData("base_url")}/TeamMemberPicture/${userData[0].memberID}.png")
            .into(view.imgUser)

        view.txtUsername.text = userData[0].memberName
        view.txtUserteam.text = userData[0].teamType
        view.txtUserregion.text = userData[0].regionName

        //region Menu Grid
        if (team_type != "7") {
            menuData = java.util.ArrayList<MenuNavigationModel>()
            var menuDataList: List<AppSetting> = emptyList()
            try {
                menuDataList = settingData.filter { it.screenName == "Menu" }
                menuDataList.sortedBy { it.labelID }
                println(menuDataList)
                menuData!!.clear()
                for (m in menuDataList) {
                    println(m.labelName)
                    val menu = MenuNavigationModel()
                    menu.labelid = m.labelID
                    menu.menuName = m.labelName
                    menu.menuImage = m.imagePath
                    menu.fixedLabel = m.fixedLabelName
                    menuData!!.add(menu)
                }
            } catch (ex: Exception) {

            }
            menuData!!.sortedBy { it.labelid }

            gridView = view.gridview
            adapter = MenuNavigationAdapter(requireContext(), menuData!!)
            gridView!!.adapter = adapter
        }
        //endregion


        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(settingData.filter { it.fixedLabelName == "Logout_Sure" }
                .get(0).labelName)
            builder.setMessage(settingData.filter { it.fixedLabelName == "Logout_Want" }
                .get(0).labelName)

            // add the buttons

            // add the buttons
            builder.setPositiveButton(settingData.filter { it.fixedLabelName == "Logout_Exit" }
                .get(0).labelName) { dialogInterface, which ->
                requireActivity().finish()
            }
            builder.setNeutralButton(settingData.filter { it.fixedLabelName == "Logout_Logout" }
                .get(0).labelName) { dialogInterface, which ->
                CSP.delData("user_id")
                mUserDataViewModel.nukeTable()
//            val navController = findNavController(R.id.main_nav_fragment)
//            navController.navigate(R.id.auth_nav)

                val intent = Intent(requireContext(), MainActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                requireContext().startActivity(intent)

                requireActivity().finish()
            }
            builder.setNegativeButton(settingData.filter { it.fixedLabelName == "Logout_Cancel" }
                .get(0).labelName, null)

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        //view.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            //TODO: Fetch Commented
            val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
            val currentDateAndTimeFormated: String = simpleDateFormat.format(Date())
            fetchAllDashboardData(
                "${CSP.getData("base_url")}/Dashboard.asmx/Dashboard_Cumulative?StoreID=0&ActivityCategoryID=0&ActivityTypeID=20&BrandID=0&TeamMemberID=${userData[0].memberID}&PerformanceMonth=0&PerformanceYear=0&TrendDate=${currentDateAndTimeFormated}&Status=1&ActivityDate=2021-01-01&TeamTypeID=${
                    CSP.getData(
                        "team_type_id"
                    )
                }"
            )
        } catch (ex: Exception) {

        }
        //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)

        //CSP.saveData("TicketFollowup", "N")
        //CSP.saveData("CheckIn_Camera", "Y")
        //CSP.saveData("Display_BarCode", "N")
        //CSP.saveData("Asset_Parameters", "N")
        //CSP.saveData("CheckIn_Camera", "N")
        //CSP.saveData("CheckOut_Camera", "N")
        //CSP.saveData("LocationLimit", "20000000000000000000000000000000000000000000000000000000000")

        /*val myLocation = Location("")

        myLocation.latitude = 41.037917
        myLocation.longitude = 28.932340

        val storeLocation = Location("")
        storeLocation.latitude = 41.03787872940302
        storeLocation.longitude = 28.93203486688435


        val distanceInMeters: Float = myLocation.distanceTo(storeLocation)
        println("distanceInMeters: ${distanceInMeters}")*/


        cvManagerCoverage.setOnClickListener {
            try {
                (activity as NewDashboardActivity).userLocation
                findNavController().navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
            } catch (ex: java.lang.Exception) {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Warning!!")
                    .setMessage("Please Allow Location Permission!")
                    .sneakWarning()
            }
        }

        cvManagerDeployment.setOnClickListener {
                try {
                    (activity as NewDashboardActivity).userLocation
                    findNavController().navigate(R.id.action_dashboardFragment_to_teamStatusFragment)
                } catch (ex: java.lang.Exception) {
                    Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                        .setTitle("Warning!!")
                        .setMessage("Please Allow Location Permission!")
                        .sneakWarning()
                }
            }



        cvCoverage.setOnClickListener {
            try {
                (activity as NewDashboardActivity).userLocation
                findNavController().navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
            } catch (ex: java.lang.Exception) {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Warning!!")
                    .setMessage("Please Allow Location Permission!")
                    .sneakWarning()
            }
        }

        cvJourneyPlan.setOnClickListener {
            try {
                (activity as NewDashboardActivity).userLocation
                if (team_type != "7")
                    findNavController().navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
            } catch (ex: java.lang.Exception) {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Warning!!")
                    .setMessage("Please Allow Location Permission!")
                    .sneakWarning()
            }
        }

        cvDeployement.setOnClickListener {
            try {
                (activity as NewDashboardActivity).userLocation
                findNavController().navigate(R.id.action_dashboardFragment_to_pendingDeploymentFragment)
            } catch (ex: java.lang.Exception) {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Warning!!")
                    .setMessage("Please Allow Location Permission!")
                    .sneakWarning()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        println("onResume Dash Frag")
        if (!CSP.getData("Dashboard_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapter.addNewItem(CSP.getData("Dashboard_SESSION_IMAGE").toString())
                CSP.delData("Dashboard_SESSION_IMAGE")

                if (CSP.getData("Dashboard_SESSION_IMAGE").equals("")) {
                    recylcerAdapter.addNewItem(CSP.getData("Dashboard_SESSION_IMAGE").toString())
                    CSP.saveData(
                        "Dashboard_SESSION_IMAGE_SET",
                        CSP.getData("Dashboard_SESSION_IMAGE")
                    )
                    CSP.delData("Dashboard_SESSION_IMAGE")
                } else {
                    recylcerAdapter.addNewItem(CSP.getData("Dashboard_SESSION_IMAGE").toString())
                    CSP.saveData(
                        "Dashboard_SESSION_IMAGE_SET",
                        "${CSP.getData("Dashboard_SESSION_IMAGE_SET")},${CSP.getData("Dashboard_SESSION_IMAGE")}"
                    )
                    CSP.delData("Dashboard_SESSION_IMAGE")
                }
            } catch (ex: Exception) {

            }
        }

        if (!CSP.getData("Dashboard_Followup_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapterRecent.addNewItem(
                    CSP.getData("Dashboard_Followup_SESSION_IMAGE").toString()
                )
                CSP.delData("Dashboard_Followup_SESSION_IMAGE")

                if (CSP.getData("Dashboard_Followup_SESSION_IMAGE").equals("")) {
                    recylcerAdapterRecent.addNewItem(
                        CSP.getData("Dashboard_Followup_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "Dashboard_Followup_SESSION_IMAGE_SET",
                        CSP.getData("Dashboard_Followup_SESSION_IMAGE")
                    )
                    CSP.delData("Dashboard_Followup_SESSION_IMAGE")
                } else {
                    recylcerAdapterRecent.addNewItem(
                        CSP.getData("Dashboard_Followup_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "Dashboard_Followup_SESSION_IMAGE_SET",
                        "${CSP.getData("Dashboard_Followup_SESSION_IMAGE_SET")},${CSP.getData("Dashboard_Followup_SESSION_IMAGE")}"
                    )
                    CSP.delData("Dashboard_Followup_SESSION_IMAGE")
                }
            } catch (ex: Exception) {

            }
        }

        if (!CSP.getData("sess_gallery_img").equals("")) {
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                recylcerAdapter.addNewItem(CSP.getData("sess_gallery_img").toString())
                CSP.delData("sess_gallery_img")
            } catch (ex: Exception) {

            }
        }
    }

    private fun setBarChart() {
        chartDailyStatus.getDescription().setEnabled(false)

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chartDailyStatus.setMaxVisibleValueCount(4)
        chartDailyStatus.getXAxis().setDrawGridLines(false)
        // scaling can now only be done on x- and y-axis separately
        // scaling can now only be done on x- and y-axis separately
        chartDailyStatus.setPinchZoom(false)

        chartDailyStatus.setDrawBarShadow(false)
        chartDailyStatus.setDrawGridBackground(false)

        val xAxis: XAxis = chartDailyStatus.getXAxis()
        xAxis.setDrawGridLines(false)

        chartDailyStatus.getAxisLeft().setDrawGridLines(false)
        chartDailyStatus.getAxisRight().setDrawGridLines(false)
        chartDailyStatus.getAxisRight().setEnabled(false)
        chartDailyStatus.getAxisLeft().setEnabled(true)
        chartDailyStatus.getXAxis().setDrawGridLines(false)
        // add a nice and smooth animation
        // add a nice and smooth animation
        chartDailyStatus.animateY(1500)


        chartDailyStatus.getLegend().setEnabled(false)

        chartDailyStatus.getAxisRight().setDrawLabels(false)
        chartDailyStatus.getAxisLeft().setDrawLabels(true)
        chartDailyStatus.setTouchEnabled(false)
        chartDailyStatus.setDoubleTapToZoomEnabled(false)
        chartDailyStatus.getXAxis().setEnabled(true)
        chartDailyStatus.getXAxis().setPosition(XAxisPosition.BOTTOM)
        chartDailyStatus.invalidate()
    }

    private fun prepareChartData(data: BarData, chart: BarChart) {
        data.setValueTextSize(0f)
        data.barWidth = 0.5f

        chart.data = data
        chart.invalidate()
    }

    private fun configureChartAppearance(data: List<DashboardBarChartData>, chart: BarChart) {
        chart.getDescription().setEnabled(false)
        chart.setDrawValueAboveBar(false)
        val xAxis: XAxis = chart.getXAxis()
        xAxis.position = XAxisPosition.BOTTOM
        /*xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DAYS.get(value.toInt())
            }
        }*/
        var xAxisLabels: MutableList<String> = ArrayList()
        var i = 0
        for (label in data) {
            println(label.TrendDate)

            xAxisLabels.add(label.TrendDate)
            if (i == 0)
                xAxisLabels.add(label.TrendDate)
            i++
        }


        xAxis.setCenterAxisLabels(false)

        xAxis.setDrawGridLines(false)
        //xAxis.granularity = 1f // only intervals of 1 day
        xAxis.textColor = Color.BLACK;
        xAxis.textSize = 8f
        //xAxis.axisLineColor = Color.WHITE;
        //xAxis.axisMinimum = 1f

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        chart.legend.isEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.xAxis.setDrawGridLines(false)
//        chart.axisLeft.setDrawLabels(false);
//        chart.axisRight.setDrawLabels(false);
        val axisLeft: YAxis = chart.axisLeft
        axisLeft.granularity = 0f
        axisLeft.axisMinimum = 0f
        val axisRight: YAxis = chart.axisRight
        axisRight.granularity = 0f
        axisRight.axisMinimum = 0f
    }

    private fun createChartData(data: List<DashboardBarChartData>): BarData? {
        val r = Random()

        val values: ArrayList<BarEntry> = ArrayList()
        var i = 0
        for (chartVal in data) {
            val random: Float = 5 + r.nextFloat() * (50 - 5)
            val x = i.toFloat() + 1
            values.add(BarEntry(x, chartVal.Value1.toFloat(), chartVal.Value2.toFloat()))
            i++
        }

        /*for (i in 0 until 7) {
            val random: Float = 5 + r.nextFloat()* (50 - 5)
            val x = i.toFloat() +1
            val y: Float = random
            values.add(BarEntry(x, y))
        }*/
        val set1 = BarDataSet(values, "Daily Visit Status")
        set1.setColors(Color.GRAY)
        val dataSets: ArrayList<IBarDataSet> = ArrayList()

        dataSets.add(set1)
        return BarData(dataSets)
    }

    private fun setupCombinedCart(
        combined_chart: CombinedChart,
        data: List<DashboardBarChartData>
    ) {
        combined_chart.description.isEnabled = false
        combined_chart.setBackgroundColor(Color.WHITE)
        combined_chart.setDrawGridBackground(false)
        combined_chart.setDrawBarShadow(false)
        combined_chart.isHighlightFullBarEnabled = false
        combined_chart.setTouchEnabled(false)
        combined_chart.isDragEnabled = false
        combined_chart.setScaleEnabled(false)
        combined_chart.setPinchZoom(false)


        // draw bars behind lines
        /*chartDailyStatusThree.setDrawOrder(
            arrayOf(
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
            )
        )*/

        val l: Legend = combined_chart.getLegend()
        l.isWordWrapEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        val rightAxis: YAxis = combined_chart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        val leftAxis: YAxis = combined_chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        val xAxis: XAxis = combined_chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.axisMinimum = 0f
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK;
        xAxis.textSize = 8f

        var xAxisLabels: MutableList<String> = ArrayList()
        var i = 0
        for (label in data) {
            println(label.TrendDate)

            xAxisLabels.add(label.TrendDate)
            /*if(i == 0)
                xAxisLabels.add(label.TrendDate)*/
            i++
        }
        combined_chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        //xAxis.setValueFormatter(IAxisValueFormatter { value, axis -> months.get(value.toInt() % months.length) } as ValueFormatter?)

        val chartData: CombinedData = CombinedData()

        chartData.setData(generateLineData(data))
        chartData.setData(generateBarData(data))
        //chartData.setValueTypeface(tfLight)

        //xAxis.axisMaximum = chartData.xMax + 0.25f

        combined_chart.data = chartData
        combined_chart.invalidate()
    }

    private fun showPieChart(
        pieChart: PieChart,
        data: List<ManagerDisplayShareData>
    ) {
        pieChart.setUsePercentValues(true)
        pieChart.description.text = ""
        //hollow pie chart
        pieChart.isDrawHoleEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.setDrawEntryLabels(false)
        //adding padding
        pieChart.setExtraOffsets(20f, 0f, 20f, 20f)
        pieChart.setUsePercentValues(true)
        pieChart.isRotationEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        pieChart.legend.isWordWrapEnabled = true

        pieChart.setUsePercentValues(true)
        val dataEntries = ArrayList<PieEntry>()
        for (chartVal in data) {
            dataEntries.add(PieEntry(chartVal.TTLDisplay.toFloat(), chartVal.BrandName))
        }


        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#4DD0E1"))
        colors.add(Color.parseColor("#FFF176"))
        colors.add(Color.parseColor("#FF8A65"))

        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)

        // In Percentage
        data.setValueFormatter(PercentFormatter())
        dataSet.sliceSpace = 3f
        dataSet.colors = colors
        pieChart.data = data
        data.setValueTextSize(15f)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        //create hole in center
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)


        //add text in center
        pieChart.setDrawCenterText(true);
        pieChart.centerText = "Display Share"

        pieChart.invalidate()
    }

    private fun showLineChart(lineChart: LineChart){

        lineChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        lineChart.axisRight.isEnabled = false

        //remove legend
        lineChart.legend.isEnabled = false


        //remove description label
        lineChart.description.isEnabled = false


        //add animation
        lineChart.animateX(1000, Easing.EaseInSine)

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        //.valueFormatter = MyAxisFormatter()
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = +90f


        var lineData: LineData
        var entries: MutableList<Entry> = arrayListOf()

        entries.add(Entry(10.toFloat(), 20.toFloat()))
        entries.add(Entry(7.toFloat(), 58.toFloat()))
        entries.add(Entry(5.toFloat(), 45.toFloat()))
        entries.add(Entry(9.toFloat(), 96.toFloat()))
        Collections.sort(entries, EntryXComparator())

        val lineDataSet = LineDataSet(entries, "-")

        val data = LineData(lineDataSet)
        lineChart.data = data

        lineChart.invalidate()
    }

    private fun generateLineData(data: List<DashboardBarChartData>): LineData? {
        val d = LineData()
        val r = Random()
        val random: Float = 5 + r.nextFloat() * (50 - 5)
        val entries = java.util.ArrayList<Entry>()
        var index = 0
        for (chartVal in data) {
            entries.add(Entry(index + 0.5f, chartVal.Value1.toFloat()))
            index++
        }
        //for (index in 0 until 12) entries.add(Entry(index + 0.5f, 5 + r.nextFloat() * (50 - 5)))
        val set = LineDataSet(
            entries,
            settingData.filter { it.fixedLabelName == "Dashboard_GrapheValueTitle1" }
                .get(0).labelName
        )
        set.color = Color.rgb(255, 0, 0)
        set.lineWidth = 2.5f
        set.setCircleColor(Color.rgb(255, 0, 0))
        set.circleRadius = 5f
        set.fillColor = Color.rgb(255, 0, 0)
        set.mode = LineDataSet.Mode.CUBIC_BEZIER
        set.setDrawValues(true)
        set.valueTextSize = 10f
        set.valueTextColor = Color.rgb(255, 0, 0)
        set.axisDependency = YAxis.AxisDependency.LEFT
        d.addDataSet(set)
        return d
    }

    private fun generateBarData(data: List<DashboardBarChartData>): BarData? {
        val r = Random()
        val random: Float = 5 + r.nextFloat() * (50 - 5)
        val entries1: ArrayList<BarEntry> = ArrayList()
        val entries2: ArrayList<BarEntry> = ArrayList()
        var index = 0
        for (chartVal in data) {
            entries1.add(BarEntry(index.toFloat(), chartVal.Value2.toFloat()))
            index++
        }
        /*for (index in 0 until 12) {
            entries1.add(BarEntry(index.toFloat(), 5 + r.nextFloat() * (50 - 5)))

            // stacked
            //entries2.add(BarEntry(index.toFloat(), floatArrayOf(random, random)))
        }*/
        val set1 = BarDataSet(
            entries1,
            settingData.filter { it.fixedLabelName == "Dashboard_GrapheValueTitle2" }
                .get(0).labelName
        )
        set1.color = Color.rgb(0, 100, 0)
        set1.valueTextColor = Color.rgb(0, 100, 0)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT
        /*val set2 = BarDataSet(entries2, "")
        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
        set2.setColors(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255))
        set2.valueTextColor = Color.rgb(61, 165, 255)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT*/
        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        val d = BarData(set1)
        d.barWidth = barWidth

        // make this BarData object grouped
        //d.groupBars(0f, groupSpace, barSpace) // start at x = 0
        return d
    }

    fun fetchAllDashboardData(url: String) {
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
                    //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, DashboardCumlativeModel::class.java)

                if (apiData.status == 200) {
                    println("DashboardCumlativeModel")
                    requireActivity().runOnUiThread(java.lang.Runnable {

                        if (apiData.data.dashboard_labels != null) {
                            val formatter: NumberFormat = DecimalFormat("00")
                            val formatter1: NumberFormat = DecimalFormat("00000")
                            txtTodayVisitCount.text =
                                formatter.format(apiData.data.dashboard_labels[0].TodayVisit.toInt())
                            txtCoverageCount.text =
                                formatter1.format(apiData.data.dashboard_labels[0].Coverage.toInt())
                            txtPendingCount.text =
                                formatter1.format(apiData.data.dashboard_labels[0].PendingData.toInt())
                        }

                        if (team_type != "7") {
                            if (apiData.data.market_activity != null) {
                                rvRecentSubActivities.setHasFixedSize(true)
                                layoutManagerRecent = LinearLayoutManager(requireContext())
                                rvRecentSubActivities.layoutManager = layoutManagerRecent
                                recylcerAdapterRecent = RecentActivityAdapter(
                                    requireContext(),
                                    "dashboard",
                                    apiData.data.market_activity as MutableList<RecentActivityData>,
                                    arguments,
                                    requireActivity() as NewDashboardActivity,
                                    userData
                                )
                                rvRecentSubActivities.adapter = recylcerAdapterRecent
                            }

                            if (apiData.data.task_assigned != null) {
                                println("task_assigned")
                                rvAssignedTask.setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(requireContext())
                                rvAssignedTask.layoutManager = layoutManager
                                recylcerAdapter = TaskAssignedAdapter(
                                    requireContext(),
                                    apiData.data.task_assigned as MutableList<DashboardTaskAssignedData>,
                                    this@DashboardFragment,
                                    requireActivity() as NewDashboardActivity
                                )
                                rvAssignedTask.adapter = recylcerAdapter
                            }

                            if (apiData.data.daily_trend != null) {
                                setupCombinedCart(chartDailyStatusThree, apiData.data.daily_trend)
                            }

                            if (apiData.data.market_activity != null) {
                                rvRecentSubActivities.setHasFixedSize(true)
                                layoutManagerRecent = LinearLayoutManager(requireContext())
                                rvRecentSubActivities.layoutManager = layoutManagerRecent
                                recylcerAdapterRecent = RecentActivityAdapter(
                                    requireContext(),
                                    "dashboard",
                                    apiData.data.market_activity as MutableList<RecentActivityData>,
                                    arguments,
                                    requireActivity() as NewDashboardActivity,
                                    userData
                                )
                                rvRecentSubActivities.adapter = recylcerAdapterRecent
                            }
                        } else {
                            if (team_type == "7") {
                                if (apiData.data.daily_trend != null) {
                                    setupCombinedCart(chartDailyStatusTwo, apiData.data.daily_trend)
                                }


                            }
                        }


                        //If Manager
                        if(team_type.toInt() <= 4){

                            if(apiData.data.managment_display_share != null){
                                showPieChart(ManagerDisplayChart, apiData.data.managment_display_share)
                                showLineChart(ManagerShareChart)
                            }



                            if(apiData.data.managment_dashboard_labels != null){
                                val management_label = apiData.data.managment_dashboard_labels

                                try{
                                    txtManagerCoverageCount.text = management_label.filter { it.ManagementLabelName == "Store_Coverage_L1" }[0].ManagementLabelValue
                                    Manager_Dashboard_Online.text = management_label.filter { it.ManagementLabelName == "Store_Coverage_L2" }[0].ManagementLabelValue
                                    Manager_Dashboard_CoverageButton.text = management_label.filter { it.ManagementLabelName == "Store_Coverage_L3" }[0].ManagementLabelValue

                                    txtManagerDeploymentCount.text = management_label.filter { it.ManagementLabelName == "Pending_Deployment_L1" }[0].ManagementLabelValue
                                    Manager_Deployment_Online.text = management_label.filter { it.ManagementLabelName == "Pending_Deployment_L2" }[0].ManagementLabelValue
                                    Manager_Dashboard_DeploymentButton.text = management_label.filter { it.ManagementLabelName == "Pending_Deployment_L3" }[0].ManagementLabelValue

                                    txtManagerTicketCount.text = management_label.filter { it.ManagementLabelName == "Open_Tickets_L1" }[0].ManagementLabelValue
                                    Manager_Ticket_Online.text = management_label.filter { it.ManagementLabelName == "Open_Tickets_L2" }[0].ManagementLabelValue
                                    Manager_Dashboard_TicketButton.text = management_label.filter { it.ManagementLabelName == "Open_Tickets_L3" }[0].ManagementLabelValue

                                    txtManagerTaskCount.text = management_label.filter { it.ManagementLabelName == "Pending_Tasks_L1" }[0].ManagementLabelValue
                                    Manager_Task_Online.text = management_label.filter { it.ManagementLabelName == "Pending_Tasks_L2" }[0].ManagementLabelValue
                                    Manager_Dashboard_TaskButton.text = management_label.filter { it.ManagementLabelName == "Pending_Tasks_L3" }[0].ManagementLabelValue
                                }catch (ex: java.lang.Exception){

                                }

                            }

                            if(apiData.data.managment_daily_activity != null){
                                rvManagerActiviites.setHasFixedSize(true)
                                layoutManagerRecent = LinearLayoutManager(requireContext())
                                rvManagerActiviites.layoutManager = layoutManagerRecent
                                recylcerAdapterRecent = RecentActivityAdapter(
                                    requireContext(),
                                    "dashboard",
                                    apiData.data.managment_daily_activity as MutableList<RecentActivityData>,
                                    arguments,
                                    requireActivity() as NewDashboardActivity,
                                    userData
                                )
                                rvManagerActiviites.adapter = recylcerAdapterRecent
                            }
                        }


                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                        //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }
}