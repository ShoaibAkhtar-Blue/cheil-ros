package com.example.cheilros.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.JPAdapter
import com.example.cheilros.adapters.JPCurrentWeekApdater
import com.example.cheilros.adapters.JPStatusAdapter
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.JPStatusData
import com.example.cheilros.models.JPStatusModel
import com.example.cheilros.models.JourneyPlanModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import kotlinx.android.synthetic.main.fragment_journey_plan.todo_list_empty_view
import kotlinx.android.synthetic.main.fragment_journey_plan.view.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.item_jpcurrentweek.*
import okhttp3.*
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class JourneyPlanFragment : Fragment() {

    private val client = OkHttpClient()

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var CSP: CustomSharedPref

    lateinit var userData: List<UserData>
    lateinit var settingData: List<AppSetting>

    //lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val calendar = Calendar.getInstance()
    lateinit var currentDate: String

    lateinit var recylcerAdapter: JPAdapter
    lateinit var jpstatusAdapter: JPStatusAdapter
    lateinit var jpscurrentweekAdapter: JPCurrentWeekApdater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_journey_plan, container, false)

        //Init DB VM
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)
        mUserDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        mUserPermissionViewModel = ViewModelProvider(this).get(UserPermissionViewModel::class.java)

        CSP = CustomSharedPref(requireContext())

        userData = mUserDataViewModel.getAllUser
        settingData = mAppSettingViewModel.getAllSetting

        view.mainLoadingLayout.setState(LoadingLayout.LOADING)

        view.btnDate.setOnClickListener {
            //getting current day,month and year.
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    requireContext(), DatePickerDialog.OnDateSetListener
                    { view, year, monthOfYear, dayOfMonth ->
                        val currentDate: String = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                        btnDate.tag = currentDate

                        fetchJPStatus("${CSP.getData("base_url")}/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=$currentDate&TeamMemberID=${userData[0].memberID}")
                        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=$currentDate&TeamMemberID=${userData[0].memberID}&VisitStatus=0")

                    }, year, month, day
                )
            datePickerDialog.show()
        }

        requireActivity().title = "Journey Plan"
        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }



        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        btnDate.tag = currentDateAndTime
        currentDate = currentDateAndTime



        fetchJPStatus("${CSP.getData("base_url")}/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=$currentDateAndTime&TeamMemberID=${userData[0].memberID}")
        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=$currentDateAndTime&TeamMemberID=${userData[0].memberID}&VisitStatus=0")
    }

    override fun onResume() {
        super.onResume()
        if(CSP.getData("sess_visit_id").isNullOrEmpty()){

        }else{
            println("onResume JP")
            CSP.delData("sess_visit_id")
            CSP.delData("sess_visit_status_id")
            reloadJP()
        }
    }

    companion object {

    }

    fun setupChart(jpdata: List<JPStatusData>){
        val NoOfEmp = ArrayList<PieEntry>()

        var total: Int = 0
        jpdata.forEach { d ->
            total += d.StatusCount.toInt()
            NoOfEmp.add(PieEntry(d.StatusCount.toFloat(), d.VisitStatus))
        }

        val dataSet = PieDataSet(NoOfEmp, "Number Of Employees")

        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 0f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        val colorFirst = context?.let { ContextCompat.getColor(it, R.color.status_checkin) }
        val colorSecond = context?.let { ContextCompat.getColor(it, R.color.status_checkout) }
        val colorThird = context?.let { ContextCompat.getColor(it, R.color.status_cancel) }
        val colorForth = context?.let { ContextCompat.getColor(it, R.color.status_none) }

        dataSet.colors = mutableListOf(colorFirst, colorSecond, colorThird, colorForth)
        //dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val data = PieData(dataSet)
        data.setValueTextSize(0f)
        data.setValueTextColor(Color.TRANSPARENT)

        val builder = SpannableStringBuilder()

        val str1 = SpannableString("$total \n")
        str1.setSpan(ForegroundColorSpan(Color.BLACK), 0, str1.length, 0)
        builder.append(str1)

        val str2 = SpannableString("Plans")
        str2.setSpan(ForegroundColorSpan(Color.GRAY), 0, str2.length, 0)
        builder.append(str2)

        totalJPChart.centerText = builder
        totalJPChart.setCenterTextSize(14f)

        val x = 80f
        totalJPChart.isDrawHoleEnabled = true
        totalJPChart.holeRadius = x

        totalJPChart.description.isEnabled = false // hide the description
        totalJPChart.legend.isEnabled = false // hide the legend
        totalJPChart.setDrawEntryLabels(false)

        totalJPChart.setDrawMarkers(false)
        //totalJPChart.setDrawSliceText(false)
        totalJPChart.setDrawRoundedSlices(true)

        //totalJPChart.xAxis.setDrawLabels(false) // hide bottom label
        //totalJPChart.axisLeft.setDrawLabels(false) // hide left label
        //totalJPChart.axisRight.setDrawLabels(false) // hide right label


        totalJPChart.data = data
        totalJPChart.highlightValues(null)
        totalJPChart.invalidate()
        totalJPChart.animateXY(3000, 3000)

        totalJPChart.isClickable = false

        LLJPChart.setOnClickListener {
            println("LLJPChart")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentWeek(mDate: String = "") {

        val date = SimpleDateFormat("yyyy-M-d").parse(mDate)

        val format: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar[Calendar.DAY_OF_WEEK] = Calendar.MONDAY

        val days = arrayOfNulls<String>(7)
        var dayList: MutableList<String> = mutableListOf()
        for (i in 0..6) {
            days[i] = format.format(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            dayList.add(days[i].toString())
            println(days[i])
        }
        println(dayList.size)

        jpscurrentweekAdapter = JPCurrentWeekApdater(requireContext(), this@JourneyPlanFragment, dayList, btnDate.tag.toString())
        rvCurrentWeek!!.adapter = jpscurrentweekAdapter
    }

    fun filerJP(status: Int = 0, filterDate: String = "") {
        println("filerJP")
        var fd = if(filterDate.equals("")) btnDate.tag else filterDate
        btnDate.tag = fd

        println("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=${fd}&TeamMemberID=${CSP.getData("user_id")}&VisitStatus=${status}")
        fetchJPStatus("${CSP.getData("base_url")}/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=${fd}&TeamMemberID=${CSP.getData("user_id")}")
        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=${fd}&TeamMemberID=${CSP.getData("user_id")}&VisitStatus=${status}", false)
    }

    fun reloadJP(){
        fetchJPStatus("${CSP.getData("base_url")}/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=${btnDate.tag}&TeamMemberID=${CSP.getData("user_id")}")
        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=${btnDate.tag}&TeamMemberID=${CSP.getData("user_id")}&VisitStatus=0", false)
    }

    fun fetchJPStatus(url: String) {
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
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, JPStatusModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        setupChart(apiData.data)
                        jpstatusAdapter = JPStatusAdapter(
                            requireContext(),
                            apiData.data,
                            this@JourneyPlanFragment
                        )
                        rvJPStatus!!.adapter = jpstatusAdapter
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                    })
                }
            }
        })
    }

    fun fetchJourneyPlan(url: String, isDecoratorEnabled: Boolean = true) {
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

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, JourneyPlanModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvJourneyPlan.setHasFixedSize(true)

                        if(isDecoratorEnabled){
                            /*var itemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
                            itemDecoration.setDrawable(getDrawable(R.drawable.border_grey)!!)*/
                            rvJourneyPlan.addItemDecoration(
                                DividerItemDecoration(
                                    context,
                                    DividerItemDecoration.VERTICAL
                                )
                            )
                        }


                        val isCurrentDate :Boolean = currentDate.equals(btnDate.tag.toString())

                        layoutManager = LinearLayoutManager(requireContext())
                        rvJourneyPlan.layoutManager = layoutManager
                        recylcerAdapter = JPAdapter(requireContext(), apiData.data, this@JourneyPlanFragment, isCurrentDate, settingData)
                        rvJourneyPlan.adapter = recylcerAdapter
                        val emptyView: View = todo_list_empty_view
                        rvJourneyPlan.setEmptyView(emptyView)

                        getCurrentWeek(btnDate.tag as String)

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
                    })
                }
            }
        })
    }
}

