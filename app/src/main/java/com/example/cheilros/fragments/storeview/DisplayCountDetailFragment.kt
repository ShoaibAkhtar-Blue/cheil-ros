package com.example.cheilros.fragments.storeview

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.DisplayCountDetailAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.SwipeHelper
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.project.mhmd.voj.swipelsit.library.SwipeListHelper
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_display_count_detail.*
import kotlinx.android.synthetic.main.fragment_display_count_detail.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_display_count_detail.view.*
import okhttp3.*
import java.io.IOException


class DisplayCountDetailFragment : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: DisplayCountDetailAdapter

    var defaultChannel = "0"

    lateinit var productCategoryData: List<DisplayProductCategoryData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_display_count_detail, container, false)

        //view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try {
            view.txtStoreName.text =
                settingData.filter { it.fixedLabelName == "StoreMenu_ModelCount" }[0].labelName + " / " + arguments?.getString(
                    "BrandName"
                )
            view.BrandHeading.text =
                settingData.filter { it.fixedLabelName == "DisplayCount_Model" }[0].labelName
            view.CountHeading.text =
                settingData.filter { it.fixedLabelName == "DisplayCount_Display" }[0].labelName
            view.btnProductCategory.text =
                "${arguments?.getString("ProductCategoryName")}"
        } catch (ex: Exception) {
            Log.e("Error_", ex.message.toString())
        }
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchCategory("${CSP.getData("base_url")}/DisplayCount.asmx/ProductCategoryList")
        fetchDisplayCountDetail(
            "${CSP.getData("base_url")}/DisplayCount.asmx/DisplayCountView?BrandID=${
                arguments?.getInt(
                    "BrandID"
                )
            }&ProductCategoryID=${arguments?.getInt("ProductCategoryID")}&StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }"
        )

        btnProductCategory.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

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
                    fetchDisplayCountDetail(
                        "${CSP.getData("base_url")}/DisplayCount.asmx/DisplayCountView?BrandID=${
                            arguments?.getInt(
                                "BrandID"
                            )
                        }&ProductCategoryID=${defaultChannel}&StoreID=${arguments?.getInt("StoreID")}"
                    )
                })
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }



        requireActivity().toolbar_search.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                recylcerAdapter?.filter?.filter(qString)
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                println("onQueryTextSubmit: $qString")
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!CSP.getData("activity_barcodes").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Barcode Added to this session!")
                .sneakSuccess()

            println("activity_barcodes: ${CSP.getData("activity_barcodes")}")
            println("ActivityDetail_BARCODE_SET: ${CSP.getData("ActivityDetail_BARCODE_SET")}")
            println("dispProdID: ${CSP.getData("dispProdID")}")
            println("dispPosition: ${CSP.getData("dispPosition")}")

            if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                CSP.saveData(
                    "ActivityDetail_BARCODE_SET",
                    "${CSP.getData("activity_barcodes")}_${CSP.getData("dispProdID")}"
                )

                //Add in Json Object
                CSP.getData("dispPosition")?.let {
                    recylcerAdapter.addDatainJsonObject(
                        it.toInt(),
                        CSP.getData("activity_barcodes")!!, true
                    )
                }

                CSP.delData("activity_barcodes")

                CSP.getData("dispProdID")?.let { recylcerAdapter.updateItem(it.toInt()) }
            } else {
                CSP.saveData(
                    "ActivityDetail_BARCODE_SET",
                    "${CSP.getData("ActivityDetail_BARCODE_SET")},${CSP.getData("activity_barcodes")}_${
                        CSP.getData(
                            "dispProdID"
                        )
                    }"
                )

                //Add in Json Object
                CSP.getData("dispPosition")?.let {
                    recylcerAdapter.addDatainJsonObject(
                        it.toInt(),
                        CSP.getData("activity_barcodes")!!,true
                    )
                }

                CSP.delData("activity_barcodes")
                CSP.getData("dispProdID")?.let { recylcerAdapter.updateItem(it.toInt()) }
            }

            CSP.delData("dispPosition")
        }
    }

    fun fetchDisplayCountDetail(url: String) {
        println(url)
        val ref = this
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
                val apiData = gson.fromJson(body, DisplayCountViewModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvDisplayCountDetail.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvDisplayCountDetail.layoutManager = layoutManager
                        recylcerAdapter =
                            DisplayCountDetailAdapter(
                                requireContext(),
                                apiData.data as MutableList<DisplayCountViewData>, ref, arguments
                            )
                        rvDisplayCountDetail.adapter = recylcerAdapter

                        object : SwipeHelper(requireContext(), rvDisplayCountDetail, false) {
                            override fun instantiateUnderlayButton(
                                viewHolder: RecyclerView.ViewHolder?,
                                underlayButtons: MutableList<UnderlayButton>?
                            ) {
                                // Archive Button
                                underlayButtons?.add(SwipeHelper.UnderlayButton(
                                    "View",
                                    AppCompatResources.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_baseline_view
                                    ),
                                    Color.parseColor("#000000"), Color.parseColor("#ffffff"),
                                    UnderlayButtonClickListener { pos: Int ->
                                        recylcerAdapter.allBarcodes(pos)
                                    }
                                ))

                                // Flag Button
                                underlayButtons?.add(SwipeHelper.UnderlayButton(
                                    "Scan",
                                    AppCompatResources.getDrawable(
                                        requireContext(),
                                        R.drawable.barcode2
                                    ),
                                    Color.parseColor("#FF0000"), Color.parseColor("#ffffff"),
                                    UnderlayButtonClickListener { pos: Int ->
                                        recylcerAdapter.barCodeScan(pos)
                                    }
                                ))

                                // More Button
                                underlayButtons?.add(SwipeHelper.UnderlayButton(
                                    "Input",
                                    AppCompatResources.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_baseline_edit_24
                                    ),
                                    Color.parseColor("#00FF00"), Color.parseColor("#ffffff"),
                                    UnderlayButtonClickListener { pos: Int ->
                                        recylcerAdapter.inputBarcode(pos)
                                    }
                                ))
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

    fun fetchCategory(url: String) {
        mainLoadingLayoutCC.setState(LoadingLayout.LOADING)
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
                        //mainLoadingLayoutCoverage.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

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
            }

        })
    }


}