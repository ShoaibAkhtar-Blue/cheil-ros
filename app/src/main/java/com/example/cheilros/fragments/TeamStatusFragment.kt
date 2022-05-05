package com.example.cheilros.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.JPCurrentWeekApdater
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.adapters.TeamStatusAdapter
import com.example.cheilros.adapters.TeamStatusCurrentWeekApdater
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import kotlinx.android.synthetic.main.fragment_journey_plan.view.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_team_status.*
import kotlinx.android.synthetic.main.fragment_team_status.btnDate
import kotlinx.android.synthetic.main.fragment_team_status.rvCurrentWeek
import okhttp3.*
import java.io.IOException
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TeamStatusFragment : BaseFragment() {

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TeamStatusAdapter

    lateinit var userList: List<AssignedTeamMemberData>
    lateinit var teamTypeList: List<ManagementTeamTypeData>

    lateinit var tsscurrentweekAdapter: TeamStatusCurrentWeekApdater

    var assignedUserID = 0

    val calendar = Calendar.getInstance()
    lateinit var currentDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_team_status, container, false)
        toolbarVisibility(false)


        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainLoadingLayoutTS.setState(LoadingLayout.LOADING)

        if (team_type.toInt() <= 4) {
            LLWeeklyCalendar.visibility = View.GONE
        } else {
            cvFieldUser.visibility = View.GONE
            LLCheckBtns.visibility = View.GONE
        }

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

                        fetchTeamStatus(
                            "${CSP.getData("base_url")}/Team.asmx/TeamStatus?TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                )
                            }&AssignedTeamMemberID=${assignedUserID}&PlanDate=${btnDate.tag}"
                        )

                    }, year, month, day
                )
            datePickerDialog.show()
        }

        getCurrentWeek(btnDate.tag as String)
        if (team_type.toInt() > 4)
            fetchUser(
                "${CSP.getData("base_url")}/Team.asmx/AssignedTeamMember?TeamMemberID=${
                    CSP.getData(
                        "user_id"
                    )
                }"
            )
        else
            fetchTeamType("${CSP.getData("base_url")}/Team.asmx/ManagementTeamType")


        var team_status_url =
            "${CSP.getData("base_url")}/Team.asmx/TeamStatus?TeamMemberID=${CSP.getData("user_id")}&AssignedTeamMemberID=0&PlanDate=${currentDateAndTime}"

        if (team_type.toInt() <= 4)
            team_status_url =
                "${CSP.getData("base_url")}/Dashboard.asmx/ManagmentTeamStatus?TeamMemberID=${
                    CSP.getData("user_id")
                }&TeamTypeID=$team_type&ChannelTypeID=1&ChannelID=1"

        fetchTeamStatus(team_status_url)

        btnUser.setOnClickListener {

            if (team_type.toInt() > 4) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Choose user")
                var channels: Array<String> = arrayOf()
                for (c in userList) {
                    channels += c.TeamMemberName
                }

                builder.setItems(channels,
                    DialogInterface.OnClickListener { dialog, which ->

                        btnUser.text = "Selected User: ${userList[which].TeamMemberName}"
                        assignedUserID = userList[which].AssignedTeamMemberID

                        fetchTeamStatus(
                            "${CSP.getData("base_url")}/Team.asmx/TeamStatus?TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                )
                            }&AssignedTeamMemberID=${userList[which].AssignedTeamMemberID}&PlanDate=${btnDate.tag}"
                        )
                    })

                // create and show the alert dialog

                // create and show the alert dialog
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } else {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Choose team type")
                var channels: Array<String> = arrayOf()
                for (c in teamTypeList) {
                    channels += c.TeamTypeName
                }

                builder.setItems(channels,
                    DialogInterface.OnClickListener { dialog, which ->

                        btnUser.text = "Selected Team Type: ${teamTypeList[which].TeamTypeName}"
                        assignedUserID = teamTypeList[which].TeamTypeID

                        fetchTeamStatus(
                            "${CSP.getData("base_url")}/Dashboard.asmx/ManagmentTeamStatus?TeamMemberID=${
                                CSP.getData(
                                    "user_id"
                                )
                            }&TeamTypeID=$assignedUserID&ChannelTypeID=1&ChannelID=1"
                        )
                    })

                // create and show the alert dialog

                // create and show the alert dialog
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }

        requireActivity().toolbar_search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                recylcerAdapter?.filter?.filter(qString)
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {

                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterTS(status: Int = 0, filterDate: String = "") {
        var fd = if (filterDate.equals("")) btnDate.tag else filterDate
        btnDate.tag = fd
        getCurrentWeek(btnDate.tag as String)
        fetchTeamStatus(
            "${CSP.getData("base_url")}/Team.asmx/TeamStatus?TeamMemberID=${
                CSP.getData(
                    "user_id"
                )
            }&AssignedTeamMemberID=${assignedUserID}&PlanDate=${btnDate.tag}"
        )
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

        tsscurrentweekAdapter = TeamStatusCurrentWeekApdater(
            requireContext(),
            this@TeamStatusFragment,
            dayList,
            mDate
        )
        rvCurrentWeek!!.adapter = tsscurrentweekAdapter
    }

    fun fetchUser(url: String) {

        mainLoadingLayoutTS.setState(LoadingLayout.LOADING)

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
                        mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, AssignedTeamMemberModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    userList = apiData.data
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            //mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                        }
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                            mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                        }
                    })
                }
            }
        })
    }

    fun fetchTeamType(url: String) {

        mainLoadingLayoutTS.setState(LoadingLayout.LOADING)

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
                        mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, ManagementTeamTypeModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    teamTypeList = apiData.data
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            //mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                        }
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                            mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                        }
                    })
                }
            }
        })
    }

    fun fetchTeamStatus(url: String) {

        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()

        mainLoadingLayoutTS.setState(LoadingLayout.LOADING)
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
                    //mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                })
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, TeamStatusModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        recyclerView = rvTeamStatus
                        recyclerView.setHasFixedSize(true);
                        recyclerView.addItemDecoration(
                            DividerItemDecoration(
                                context,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        layoutManager = LinearLayoutManager(requireContext())
                        recyclerView.layoutManager = layoutManager

//                        val emptyView: View = todo_list_empty_view
//                        recyclerView.setEmptyView(emptyView)
                        recylcerAdapter =
                            TeamStatusAdapter(requireContext(), apiData.data, arguments)
                        recyclerView.adapter = recylcerAdapter

                        getCurrentWeek(btnDate.tag as String)

                        val formatter1: NumberFormat = DecimalFormat("00000")
                        txtManagerDeploymentCount.text =
                            formatter1.format(apiData.data.size.toInt())

                        mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
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
                        mainLoadingLayoutTS.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }
}