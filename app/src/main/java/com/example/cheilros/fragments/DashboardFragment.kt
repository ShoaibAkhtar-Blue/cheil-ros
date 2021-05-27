package com.example.cheilros.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment() {

    lateinit var CSP: CustomSharedPref

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mUserDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        CSP = CustomSharedPref(requireContext())

        requireActivity().title = "Home"

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Are You Sure?")
            builder.setMessage("You want to exit app or logout?")

            // add the buttons

            // add the buttons
            builder.setPositiveButton("Exit App") { dialogInterface, which ->
                requireActivity().finish()
            }
            builder.setNeutralButton("Logout") { dialogInterface, which ->
                CSP.delData("user_id")
                mUserDataViewModel.nukeTable()
//            val navController = findNavController(R.id.main_nav_fragment)
//            navController.navigate(R.id.auth_nav)

                val intent = Intent(requireContext(), MainActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                requireContext().startActivity(intent)

                requireActivity().finish()
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



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

        val data = createChartData()
        configureChartAppearance()
        data?.let { prepareChartData(it) }
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

    private fun configureChartAppearance() {
        chartDailyStatus.getDescription().setEnabled(false)
        chartDailyStatus.setDrawValueAboveBar(false)
        val xAxis: XAxis = chartDailyStatus.getXAxis()
        xAxis.position = XAxisPosition.BOTTOM
        /*xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DAYS.get(value.toInt())
            }
        }*/
        chartDailyStatus.legend.isEnabled = false
        chartDailyStatus.axisLeft.setDrawGridLines(false);
        chartDailyStatus.xAxis.setDrawGridLines(false);
        chartDailyStatus.axisLeft.setDrawLabels(false);
        chartDailyStatus.axisRight.setDrawLabels(false);
        /*val axisLeft: YAxis = chartDailyStatus.getAxisLeft()
        axisLeft.granularity = 10f
        axisLeft.axisMinimum = 0f
        val axisRight: YAxis = chartDailyStatus.getAxisRight()
        axisRight.granularity = 10f
        axisRight.axisMinimum = 0f*/
    }

    private fun createChartData(): BarData? {

        val r = Random()

        val values: ArrayList<BarEntry> = ArrayList()
        for (i in 0 until 7) {
            val random: Float = 5 + r.nextFloat()* (50 - 5)
            val x = i.toFloat() +1
            val y: Float = random
            values.add(BarEntry(x, y))
        }
        val set1 = BarDataSet(values, "Daily Visit Status")
        set1.setColors(Color.GRAY)
        val dataSets: ArrayList<IBarDataSet> = ArrayList()

        dataSets.add(set1)
        return BarData(dataSets)
    }

    companion object {

    }
}