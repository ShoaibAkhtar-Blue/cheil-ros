package com.example.cheilros.fragments.storeview

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.ChecklistDetailAdapter
import com.example.cheilros.adapters.DisplayCountDetailAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_checklist_category_detail.*
import kotlinx.android.synthetic.main.fragment_display_count_detail.*
import kotlinx.android.synthetic.main.fragment_display_count_detail.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_display_count_detail.view.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
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
                settingData.filter { it.fixedLabelName == "StoreMenu_ModelCount" }
                    .get(0).labelName + " / " + arguments?.getString("ProductCategoryName")
            view.BrandHeading.text =
                settingData.filter { it.fixedLabelName == "DisplayCount_Model" }
                    .get(0).labelName
            view.CountHeading.text =
                settingData.filter { it.fixedLabelName == "DisplayCount_Display" }
                    .get(0).labelName
        } catch (ex: Exception) {

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
            builder.setTitle("Choose Category")

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
                        "Selected Category: ${productCategoryData[which].ProductCategoryName}"
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
    }

    fun fetchDisplayCountDetail(url: String) {
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
                        //mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
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