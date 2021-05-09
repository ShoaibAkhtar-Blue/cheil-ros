package com.example.cheilros.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.JPAdapter
import com.example.cheilros.adapters.JPStatusAdapter
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class JourneyPlanFragment : Fragment() {

    private val client = OkHttpClient()

    //lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    val booklist=arrayListOf(
            "P.S I Love You",
            "The Great Gatsby",
            "Anna Karenina",
            "Madame Bovary",
            "War and Peace",
            "Loyalty",
            "Middle March",
            "The Adventures",
            "Mona Dick",
            "The Lord Of Rings"
    )
    lateinit var recylcerAdapter: JPAdapter
    lateinit var jpstatusAdapter: JPStatusAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journey_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        btnDate.text = currentDateAndTime

        fetchJPStatus("http://rosturkey.cheildata.com/JourneyPlan.asmx/JourneyPlanSummary?PlanDate=$currentDateAndTime&TeamMemberID=1")
        fetchJourneyPlan("http://rosturkey.cheildata.com/JourneyPlan.asmx/TeamJourneyPlan?PlanDate=2021-04-25&TeamMemberID=1&VisitStatus=0")

//        rvJourneyPlan.setHasFixedSize(true);
//        rvJourneyPlan.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
//        layoutManager= LinearLayoutManager(requireContext())
//        recylcerAdapter= JPAdapter(requireContext(), booklist)
//        rvJourneyPlan.adapter=recylcerAdapter
//        rvJourneyPlan.layoutManager=layoutManager
    }

    companion object {

    }

    fun fetchJPStatus(url: String) {
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
                if(apiData.status == 200){
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        jpstatusAdapter = JPStatusAdapter(requireContext(), apiData.data)
                        rvJPStatus!!.adapter = jpstatusAdapter
                    })
                }else{
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
                if(apiData.status == 200){
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvJourneyPlan.setHasFixedSize(true);
                        rvJourneyPlan.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                        layoutManager= LinearLayoutManager(requireContext())
                        rvJourneyPlan.layoutManager=layoutManager
                        recylcerAdapter= JPAdapter(requireContext(), apiData.data)
                        rvJourneyPlan.adapter=recylcerAdapter
                    })
                }else{
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

//Models
class JPStatusModel(val status: Int, val data : List<JPStatusData>)
class JPStatusData(val VisitStatusID: Int, val VisitStatus : String, val StatusCount : String)

class JourneyPlanModel(val status: Int, val data : List<JourneyPlanData>)
class JourneyPlanData(val VisitID: Int,
                      val TeamMemberID : Int,
                       val PlanDate: String,
                       val Month: Int,
                       val Year: Int,
                       val WeekNo: Int,
                       val CheckInTime: String,
                       val CheckOutTime: String,
                       val VisitRemarks: String,
                       val CheckInRemarks: String,
                       val CheckOutRemarks: String,
                       val VisitStatusID: Int,
                       val VisitStatus: String,
                       val StoreCode: String,
                       val StoreName: String,
                       val Longitude: String,
                       val Latitude: String,
                       val ImageLocation:String)