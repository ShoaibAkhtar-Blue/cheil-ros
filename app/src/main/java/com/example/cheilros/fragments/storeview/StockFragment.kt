package com.example.cheilros.fragments.storeview

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.StockAdapter
import com.example.cheilros.adapters.StockCurrentWeekAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.StockModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_sales.*
import kotlinx.android.synthetic.main.fragment_sales.btnDate
import kotlinx.android.synthetic.main.fragment_sales.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_sales.rvCurrentWeek
import kotlinx.android.synthetic.main.fragment_stock.*
import okhttp3.*
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class StockFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: StockAdapter

    lateinit var tsscurrentweekAdapter: StockCurrentWeekAdapter

    val calendar = Calendar.getInstance()
    lateinit var currentDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stock, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try{
            view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_DailyStock" }.get(0).labelName
        }catch (ex: Exception){

        }
        //endregion

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        btnDate.tag = currentDateAndTime

        CSP.saveData("salesData", currentDateAndTime)

        btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    requireContext(), DatePickerDialog.OnDateSetListener
                    { view, year, monthOfYear, dayOfMonth ->
                        val currentDate: String = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                        btnDate.tag = currentDate
                        CSP.saveData("salesData", currentDate)
                        fetchStock("${CSP.getData("base_url")}/Stock.asmx/StockSummary?StoreID=${arguments?.getInt("StoreID")}&StockDate=$currentDate")
                    }, year, month, day
                )
            //datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
        getCurrentWeek(btnDate.tag as String)
        fetchStock("${CSP.getData("base_url")}/Stock.asmx/StockSummary?StoreID=${arguments?.getInt("StoreID")}&StockDate=$currentDateAndTime")
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

        tsscurrentweekAdapter = StockCurrentWeekAdapter(
            requireContext(),
            this@StockFragment,
            dayList,
            mDate
        )
        rvCurrentWeek!!.adapter = tsscurrentweekAdapter
    }

    fun fetchStock(url: String){
        mainLoadingLayoutCC.setState(LoadingLayout.LOADING)
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
                    mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, StockModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvStock.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvStock.layoutManager = layoutManager
                        recylcerAdapter = StockAdapter(requireContext(), apiData.data, arguments?.getInt("StoreID"), arguments?.getString("StoreName"), settingData)
                        rvStock.adapter = recylcerAdapter
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }else {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterTS(status: Int = 0, filterDate: String = ""){
        var fd = if (filterDate.equals("")) btnDate.tag else filterDate
        btnDate.tag = fd
        getCurrentWeek(btnDate.tag as String)
        fetchStock("${CSP.getData("base_url")}/Stock.asmx/StockSummary?StoreID=${arguments?.getInt("StoreID")}&StockDate=${btnDate.tag}")
    }

}