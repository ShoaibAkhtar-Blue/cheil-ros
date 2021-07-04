package com.example.cheilros.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
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
import com.example.cheilros.adapters.TaskAssignedAdapter
import com.example.cheilros.data.AppSetting
import com.example.cheilros.models.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_dashboard.view.gridview
import kotlinx.android.synthetic.main.activity_dashboard.view.imgUser
import kotlinx.android.synthetic.main.activity_dashboard.view.txtUsername
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import okhttp3.*
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : BaseFragment() {

    var gridView: GridView? = null
    var menuData: java.util.ArrayList<MenuNavigationModel>? = null
    var adapter: MenuNavigationAdapter? = null

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TaskAssignedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val simpleDateFormat = SimpleDateFormat("E dd, MMMM")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        //region Set Labels
        //view.Dashboard_TodayVisit.text = settingData.filter { it.fixedLabelName == "Dashboard_TodayVisit" }.get(0).labelName
        view.Dashboard_StorePlan.text = settingData.filter { it.fixedLabelName == "Dashboard_StorePlan" }.get(0).labelName
        view.Dashboard_GraphTitle.text = settingData.filter { it.fixedLabelName == "Dashboard_GraphTitle" }.get(0).labelName
        view.Dashboard_TaskTitle.text = settingData.filter { it.fixedLabelName == "Dashboard_TaskTitle" }.get(0).labelName
        view.Dashboard_CoverageButton.text = settingData.filter { it.fixedLabelName == "Dashboard_CoverageButton" }.get(0).labelName
        view.Dashboard_PendingButton.text = settingData.filter { it.fixedLabelName == "Dashboard_PendingButton" }.get(0).labelName
        view.txtCurrentDate.text = currentDateAndTime
        //endregion

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        //region Clear Sessions
        CSP.delData("fragName")
        //endregion

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        try{
            fetchDashboardData("${CSP.getData("base_url")}/Dashboard.asmx/DashboardLabels?TeamMemberID=${userData[0].memberID}")
        }catch (ex: Exception){

        }


        Glide.with(this)
            .load("${CSP.getData("base_url")}/TeamMemberPicture/${userData[0].memberID}.png")
            .into(view.imgUser)

        view.txtUsername.text = userData[0].memberName
        view.txtUserteam.text = userData[0].teamType
        view.txtUserregion.text = userData[0].regionName

        //region Menu Grid

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



        /*gridView!!.onItemClickListener = OnItemClickListener { parent, v, i, id ->
            //Toast.makeText(this, "menu " + menuDataList.get(i).fixedLabelName + " clicked! $i", Toast.LENGTH_SHORT).show()
            val navController = findNavController(R.id.main_nav_fragment)
            try {
                if (menuDataList.get(i).fixedLabelName == "MenuTitle3") {
                    CSP.delData("sess_visit_id")
                    CSP.delData("sess_visit_status_id")

                    navController.navigateUp()
                    findNavController(R.id.main_nav_fragment).navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
                    //toolbar.visibility = View.GONE
                }

                if (menuDataList.get(i).fixedLabelName == "MenuTitle2") {
                    navController.navigateUp()
                    findNavController(R.id.main_nav_fragment).navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
                }

            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                Log.e("Error_Nav", e.message.toString())
            }
        }*/

        //endregion


        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(settingData.filter { it.fixedLabelName == "Logout_Sure" }.get(0).labelName)
            builder.setMessage(settingData.filter { it.fixedLabelName == "Logout_Want" }.get(0).labelName)

            // add the buttons

            // add the buttons
            builder.setPositiveButton(settingData.filter { it.fixedLabelName == "Logout_Exit" }.get(0).labelName) { dialogInterface, which ->
                requireActivity().finish()
            }
            builder.setNeutralButton(settingData.filter { it.fixedLabelName == "Logout_Logout" }.get(0).labelName) { dialogInterface, which ->
                CSP.delData("user_id")
                mUserDataViewModel.nukeTable()
//            val navController = findNavController(R.id.main_nav_fragment)
//            navController.navigate(R.id.auth_nav)

                val intent = Intent(requireContext(), MainActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                requireContext().startActivity(intent)

                requireActivity().finish()
            }
            builder.setNegativeButton(settingData.filter { it.fixedLabelName == "Logout_Cancel" }.get(0).labelName, null)

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        view.mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)

        cvCoverage.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
        }

        cvJourneyPlan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
        }

        cvDeployement.setOnClickListener {
            /*val url = "http://rosturkey.cheildata.com/Audit.asmx/CheckList_AuditAdd"

            val JSON: MediaType? = "application/text; charset=utf-8".toMediaTypeOrNull()

            val JSONObjectString = "{\n" +
                    "    \"data\":\n" +
                    "    [\n" +
                    "        {\"CheckListID\":\"1\",\"StoreID\":\"1\",\"CheckListStatus\":\"0\",\"TeamMemberID\":\"1\"},\n" +
                    "        {\"CheckListID\":\"2\",\"StoreID\":\"1\",\"CheckListStatus\":\"0\",\"TeamMemberID\":\"1\"},\n" +
                    "        {\"CheckListID\":\"3\",\"StoreID\":\"1\",\"CheckListStatus\":\"0\",\"TeamMemberID\":\"1\"}\n" +
                    "    ]\n" +
                    "}" //The data I want to send

            var body: RequestBody = RequestBody.create(JSON, JSONObjectString)
            val request = Request.Builder().post(body).url(url).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val tm = response.body?.string()
                    println(tm)
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("Failed", "FAILED")
                    e.printStackTrace()
                }
            })*/
        }

        /*val client = OkHttpClient()
        val sourceFile = File("/storage/emulated/0/Pictures/1621837639994.jpg")
        val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
        val fileName: String = sourceFile.name

        try {
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("CheckInImage", fileName,sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull()))
                    .build()

            val request: Request = Request.Builder()
                .url("http://rosturkey.cheildata.com/Checkin.asmx/CheckInImg?VisitID=9&Longitude=1234&Latitude=4567&Remarks=Test")
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()

            println(response.body!!.string())

        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("File upload", "failed")
        }*/


        /*btnCamera.setOnClickListener {
            findNavController().navigate(R.id.cameraActivity)
        }

        btnJourneyPlan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
        }

        btnTest.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
        }*/
        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val currentDateAndTime: String = simpleDateFormat.format(Date())



        try{
            fetchTaskAssignedData("${CSP.getData("base_url")}/Dashboard.asmx/TaskAssigned?TeamMemberID=${CSP.getData("user_id")}&Status=1")
            fetchChartData("${CSP.getData("base_url")}/Dashboard.asmx/DailyTrend?TeamMemberID=${CSP.getData("user_id")}&TrendDate=${currentDateAndTime}")
        }catch (ex:Exception){

        }


    }

    override fun onResume() {
        super.onResume()
        println("onResume Dash Frag")
        if(!CSP.getData("Dashboard_SESSION_IMAGE").equals("")){
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapter.addNewItem(CSP.getData("Dashboard_SESSION_IMAGE").toString())
                CSP.delData("Dashboard_SESSION_IMAGE")

                if (CSP.getData("Dashboard_SESSION_IMAGE").equals("")) {
                    recylcerAdapter.addNewItem(CSP.getData("Dashboard_SESSION_IMAGE").toString())
                    CSP.saveData("Dashboard_SESSION_IMAGE_SET", CSP.getData("Dashboard_SESSION_IMAGE"))
                    CSP.delData("Dashboard_SESSION_IMAGE")
                } else {
                    recylcerAdapter.addNewItem(CSP.getData("Dashboard_SESSION_IMAGE").toString())
                    CSP.saveData(
                        "Dashboard_SESSION_IMAGE_SET",
                        "${CSP.getData("Dashboard_SESSION_IMAGE_SET")},${CSP.getData("Dashboard_SESSION_IMAGE")}"
                    )
                    CSP.delData("Dashboard_SESSION_IMAGE")
                }
            }catch (ex: Exception){

            }
        }else if(!CSP.getData("sess_gallery_img").equals("")){
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                recylcerAdapter.addNewItem(CSP.getData("sess_gallery_img").toString())
                CSP.delData("sess_gallery_img")
            }catch (ex: Exception){

            }
        }
    }

    fun fetchDashboardData(url: String){



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
                    mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, DashboardModel::class.java)

                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        val formatter: NumberFormat = DecimalFormat("00")
                        val formatter1: NumberFormat = DecimalFormat("00000")
                        txtTodayVisitCount.text = formatter.format(apiData.data[0].TodayVisit.toInt())
                        txtCoverageCount.text = formatter1.format(apiData.data[0].Coverage.toInt())
                        txtPendingCount.text = formatter1.format(apiData.data[0].PendingData.toInt())
                    })

                }else{
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

            }

        })
    }

    fun fetchChartData(url: String){
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
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, DashboardBarChartModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        val data = createChartData(apiData.data)
                        configureChartAppearance(apiData.data)
                        data?.let { prepareChartData(it) }
                    })
                }else{
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
            }
        })
    }

    fun fetchTaskAssignedData(url: String){
        val client = OkHttpClient()
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
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, DashboardTaskAssignedModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvAssignedTask.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvAssignedTask.layoutManager = layoutManager
                        recylcerAdapter = TaskAssignedAdapter(requireContext(),
                            apiData.data as MutableList<DashboardTaskAssignedData>, this@DashboardFragment,
                            requireActivity() as NewDashboardActivity
                        )
                        rvAssignedTask.adapter = recylcerAdapter
                    })
                }else{
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
            }
        })
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

    private fun prepareChartData(data: BarData) {
        data.setValueTextSize(0f)
        data.barWidth = 0.5f

        chartDailyStatus.setData(data)
        chartDailyStatus.invalidate()
    }

    private fun configureChartAppearance(data: List<DashboardBarChartData>) {
        chartDailyStatus.getDescription().setEnabled(false)
        chartDailyStatus.setDrawValueAboveBar(false)
        val xAxis: XAxis = chartDailyStatus.getXAxis()
        xAxis.position = XAxisPosition.BOTTOM
        /*xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DAYS.get(value.toInt())
            }
        }*/
        var xAxisLabels : MutableList<String> = ArrayList()
        var i = 0
        for(label in data){
            println(label.TrendDate)

            xAxisLabels.add(label.TrendDate)
            if(i == 0)
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

        chartDailyStatus.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)

        chartDailyStatus.legend.isEnabled = false
        chartDailyStatus.axisLeft.setDrawGridLines(false)
        chartDailyStatus.xAxis.setDrawGridLines(false)
//        chartDailyStatus.axisLeft.setDrawLabels(false);
//        chartDailyStatus.axisRight.setDrawLabels(false);
        val axisLeft: YAxis = chartDailyStatus.axisLeft
        axisLeft.granularity = 0f
        axisLeft.axisMinimum = 0f
        val axisRight: YAxis = chartDailyStatus.axisRight
        axisRight.granularity = 0f
        axisRight.axisMinimum = 0f
    }

    private fun createChartData(data: List<DashboardBarChartData>): BarData? {
        val r = Random()

        val values: ArrayList<BarEntry> = ArrayList()
        var i = 0
        for(chartVal in data){
            val random: Float = 5 + r.nextFloat()* (50 - 5)
            val x = i.toFloat() +1
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

    companion object {

    }
}