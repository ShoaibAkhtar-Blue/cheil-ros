package com.example.cheilros.fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.MyActivitiesAdapter
import com.example.cheilros.adapters.MyActivityCurrentWeekApdater
import com.example.cheilros.models.MyActivitiesModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_journey_plan.view.*
import kotlinx.android.synthetic.main.fragment_journey_plan.view.txtNoRecord
import kotlinx.android.synthetic.main.fragment_my_activities.*
import kotlinx.android.synthetic.main.fragment_store_view.view.*
import kotlinx.android.synthetic.main.fragment_team_status.*
import kotlinx.android.synthetic.main.fragment_team_status.btnDate
import kotlinx.android.synthetic.main.fragment_team_status.mainLoadingLayoutTS
import kotlinx.android.synthetic.main.fragment_team_status.rvCurrentWeek
import okhttp3.*
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MyActivitiesFragment : BaseFragment() {

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: MyActivitiesAdapter

    lateinit var tsscurrentweekAdapter: MyActivityCurrentWeekApdater

    var assignedUserID = 0

    val calendar = Calendar.getInstance()
    lateinit var currentDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =   inflater.inflate(R.layout.fragment_my_activities, container, false)

        //region Set Label
        try {
            view.txtNoRecord.text = settingData.filter { it.fixedLabelName == "General_NoRecordFound" }[0].labelName
        }catch (ex: Exception){

        }
        //endregion

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
                        fetchMyActivities("${CSP.getData("base_url")}/WebService.asmx/MyDailyActivity?TeamMemberID=${CSP.getData("user_id")}&ActivityDate=${btnDate.tag}")
                    }, year, month, day
                )
            datePickerDialog.show()
        }

        getCurrentWeek(btnDate.tag as String)
        fetchMyActivities("${CSP.getData("base_url")}/WebService.asmx/MyDailyActivity?TeamMemberID=${CSP.getData("user_id")}&ActivityDate=${currentDateAndTime}")
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

        tsscurrentweekAdapter = MyActivityCurrentWeekApdater(
            requireContext(),
            this@MyActivitiesFragment,
            dayList,
            mDate
        )
        rvCurrentWeek!!.adapter = tsscurrentweekAdapter
    }

    fun fetchMyActivities(url: String){
        println(url)
        val client = OkHttpClient()

        mainLoadingLayout.setState(LoadingLayout.LOADING)
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
                    //mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                })
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, MyActivitiesModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        recyclerView = rvActivity
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(
                            DividerItemDecoration(
                                context,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        layoutManager= LinearLayoutManager(requireContext())
                        recyclerView.layoutManager=layoutManager

                        val emptyView: View = todo_list_empty_view1
                        recyclerView.setEmptyView(emptyView)
                        recylcerAdapter = MyActivitiesAdapter(requireContext(), apiData.data, arguments)
                        recyclerView.adapter = recylcerAdapter

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
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterTS(status: Int = 0, filterDate: String = ""){
        var fd = if (filterDate.equals("")) btnDate.tag else filterDate
        btnDate.tag = fd
        getCurrentWeek(btnDate.tag as String)
        fetchMyActivities("${CSP.getData("base_url")}/WebService.asmx/MyDailyActivity?TeamMemberID=${CSP.getData("user_id")}&ActivityDate=${btnDate.tag}")
    }


}