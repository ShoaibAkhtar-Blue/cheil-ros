package com.example.cheilros.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.JPAdapter
import com.example.cheilros.adapters.JPStatusAdapter
import com.example.cheilros.data.UserData
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.JPStatusModel
import com.example.cheilros.models.JourneyPlanModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import kotlinx.android.synthetic.main.fragment_journey_plan.view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class JourneyPlanFragment : Fragment() {

    private val client = OkHttpClient()

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var CSP: CustomSharedPref

    lateinit var userData: List<UserData>

    //lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val calendar = Calendar.getInstance()

    lateinit var recylcerAdapter: JPAdapter
    lateinit var jpstatusAdapter: JPStatusAdapter

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
                        val customDate: String = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                        btnDate.text = customDate

                        fetchJPStatus("${CSP.getData("base_url")}/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=$customDate&TeamMemberID=${userData[0].memberID}")
                        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=$customDate&TeamMemberID=${userData[0].memberID}&VisitStatus=0")

                    }, year, month, day
                )
            datePickerDialog.show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        btnDate.text = currentDateAndTime

        fetchJPStatus("${CSP.getData("base_url")}/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=$currentDateAndTime&TeamMemberID=${userData[0].memberID}")
        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=$currentDateAndTime&TeamMemberID=${userData[0].memberID}&VisitStatus=0")

    }

    companion object {

    }

    fun filerJP(status: Int) {
        println("filerJP")
        println("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=${btnDate.text}&TeamMemberID=${userData[0].memberID}&VisitStatus=${status.toString()}")
        fetchJourneyPlan("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=${btnDate.text}&TeamMemberID=${userData[0].memberID}&VisitStatus=${status.toString()}")
    }

    fun fetchJPStatus(url: String) {

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

    fun fetchJourneyPlan(url: String) {
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
                val apiData = gson.fromJson(body, JourneyPlanModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvJourneyPlan.setHasFixedSize(true);
                        rvJourneyPlan.addItemDecoration(
                            DividerItemDecoration(
                                context,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        layoutManager = LinearLayoutManager(requireContext())
                        rvJourneyPlan.layoutManager = layoutManager
                        recylcerAdapter = JPAdapter(requireContext(), apiData.data)
                        rvJourneyPlan.adapter = recylcerAdapter
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

