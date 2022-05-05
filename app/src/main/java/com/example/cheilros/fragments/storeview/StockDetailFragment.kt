package com.example.cheilros.fragments.storeview

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.StockDetailAdapter
import com.example.cheilros.adapters.StockDetailCurrentWeekAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_display_count_detail.*
import kotlinx.android.synthetic.main.fragment_display_count_detail.btnProductCategory
import kotlinx.android.synthetic.main.fragment_display_count_detail.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_display_count_detail.view.*
import kotlinx.android.synthetic.main.fragment_display_count_detail.view.BrandHeading
import kotlinx.android.synthetic.main.fragment_display_count_detail.view.btnProductCategory
import kotlinx.android.synthetic.main.fragment_stock_detail.*
import kotlinx.android.synthetic.main.fragment_stock_detail.view.*
import okhttp3.*
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StockDetailFragment : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: StockDetailAdapter

    var defaultChannel = "0"

    lateinit var productCategoryData: List<DisplayProductCategoryData>

    lateinit var tsscurrentweekAdapter: StockDetailCurrentWeekAdapter

    val calendar = Calendar.getInstance()
    lateinit var currentDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_stock_detail, container, false)

        //region Set Labels
        try {

            view.SalesQty.text =
                settingData.filter { it.fixedLabelName == "Stock_Column1" }
                    .get(0).labelName

            view.SalesValue.text =
                settingData.filter { it.fixedLabelName == "Stock_Column2" }
                    .get(0).labelName

            view.txtStoreName.text =
                settingData.filter { it.fixedLabelName == "StoreMenu_DailyStock" }
                    .get(0).labelName + " / " + arguments?.getString("BrandName")
            view.BrandHeading.text =
                settingData.filter { it.fixedLabelName == "StoreMenu_DailyStock" }
                    .get(0).labelName
            view.btnProductCategory.text =
                "${arguments?.getString("ProductCategoryName")}"

            if (settingData.filter { it.fixedLabelName == "Stock_Column2" }[0].labelName == "")
                view.SalesValue.visibility = View.GONE
        } catch (ex: Exception) {
            Log.e("Error_", ex.message.toString())
        }
        //endregion

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        btnDate.tag = CSP.getData("salesData")

        getCurrentWeek(btnDate.tag as String)

        fetchCategory("${CSP.getData("base_url")}/DisplayCount.asmx/ProductCategoryList")
        fetchStockDetail(
            "${CSP.getData("base_url")}/Stock.asmx/StockStatus?BrandID=${
                arguments?.getInt(
                    "BrandID"
                )
            }&ProductCategoryID=${arguments?.getInt("ProductCategoryID")}&StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }&StockDate=${CSP.getData("salesData")}"
        )

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
                        fetchStockDetail(
                            "${CSP.getData("base_url")}/Stock.asmx/StockStatus?BrandID=${
                                arguments?.getInt(
                                    "BrandID"
                                )
                            }&ProductCategoryID=${arguments?.getInt("ProductCategoryID")}&StoreID=${
                                arguments?.getInt(
                                    "StoreID"
                                )
                            }&StockDate=$currentDate"
                        )
                    }, year, month, day
                )
            //datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        btnProductCategory.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list

            // add a list
            var channels: Array<String> = arrayOf()
            for (c in productCategoryData) {
                channels += c.ProductCategoryName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(productCategoryData[which].ProductCategoryName)
                    defaultChannel = productCategoryData[which].ProductCategoryID.toString()
                    btnProductCategory.text =
                        "${productCategoryData[which].ProductCategoryName}"
                    fetchStockDetail(
                        "${CSP.getData("base_url")}/Stock.asmx/StockStatus?BrandID=${
                            arguments?.getInt(
                                "BrandID"
                            )
                        }&ProductCategoryID=${defaultChannel}&StoreID=${arguments?.getInt("StoreID")}&StockDate=${btnDate.tag}"
                    )
                })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        requireActivity().toolbar_search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                try {
                    recylcerAdapter?.filter?.filter(qString)
                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                }

                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {

                return true
            }
        })
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

        tsscurrentweekAdapter = StockDetailCurrentWeekAdapter(
            requireContext(),
            this@StockDetailFragment,
            dayList,
            mDate
        )
        rvCurrentWeek!!.adapter = tsscurrentweekAdapter
    }

    fun fetchStockDetail(url: String) {
        println(url)
        mainLoadingLayoutCC.setState(LoadingLayout.LOADING)
        val ref = this
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
                    mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, StockDetailModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvStockDetail.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvStockDetail.layoutManager = layoutManager
                            recylcerAdapter =
                                StockDetailAdapter(
                                    requireContext(),
                                    apiData.data as MutableList<StockDetailData>,
                                    ref,
                                    arguments,
                                    btnDate.tag.toString(),
                                    settingData
                                )
                            rvStockDetail.adapter = recylcerAdapter
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
                            mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
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

    fun fetchCategory(url: String) {
        mainLoadingLayoutCC.setState(LoadingLayout.LOADING)
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
                        //mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, DisplayProductCategoryModel::class.java)
                    if (apiData.status == 200) {
                        productCategoryData = apiData.data
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                //mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                            }
                        })
                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                                //mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterTS(status: Int = 0, filterDate: String = "") {
        var fd = if (filterDate.equals("")) btnDate.tag else filterDate
        btnDate.tag = fd
        CSP.saveData("salesData", fd.toString())
        getCurrentWeek(btnDate.tag as String)
        fetchStockDetail(
            "${CSP.getData("base_url")}/Stock.asmx/StockStatus?BrandID=${
                arguments?.getInt(
                    "BrandID"
                )
            }&ProductCategoryID=${arguments?.getInt("ProductCategoryID")}&StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }&StockDate=${btnDate.tag}"
        )
    }
}