package com.example.cheilros.fragments.storeview

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.PriceDetailAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_price_detail.*
import kotlinx.android.synthetic.main.fragment_price_detail.btnProductCategory
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class PriceDetailFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: PriceDetailAdapter

    var defaultChannel = "0"

    lateinit var productCategoryData: List<DisplayProductCategoryData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_price_detail, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try {
            view.txtStoreName.text =
                settingData.filter { it.fixedLabelName == "StoreMenu_PricePromotions" }
                    .get(0).labelName + " / " + arguments?.getString("BrandName")
        } catch (ex: Exception) {

        }
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchCategory("${CSP.getData("base_url")}/DisplayCount.asmx/ProductCategoryList")
        fetchPrices(
            "${CSP.getData("base_url")}/Prices.asmx/PricePromotionView?BrandID=${
                arguments?.getInt(
                    "BrandID"
                )
            }&ProductCategoryID=${arguments?.getInt("ProductCategoryID")}&StoreID=${
                arguments?.getInt(
                    "StoreID"
                )
            }"
        )

        btnProductCategory.text =
            "${arguments?.getString("ProductCategory")}"

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
                        "${productCategoryData[which].ProductCategoryName}"
                    fetchPrices(
                        "${CSP.getData("base_url")}/Prices.asmx/PricePromotionView?BrandID=${
                            arguments?.getInt(
                                "BrandID"
                            )
                        }&ProductCategoryID=${defaultChannel}&StoreID=${
                            arguments?.getInt(
                                "StoreID"
                            )
                        }"
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

    override fun onResume() {
        super.onResume()
        println("onResume Dash Frag")
        if (!CSP.getData("PriceDetail_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapter.addNewItem(
                    CSP.getData("productid").toString(),
                    CSP.getData("PriceDetail_SESSION_IMAGE").toString()
                )
                //CSP.delData("PriceDetail_SESSION_IMAGE")

                if (CSP.getData("PriceDetail_SESSION_IMAGE").equals("")) {
                    recylcerAdapter.addNewItem(
                        CSP.getData("productid").toString(),
                        CSP.getData("PriceDetail_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "PriceDetail_SESSION_IMAGE_SET",
                        CSP.getData("PriceDetail_SESSION_IMAGE")
                    )
                    //CSP.delData("PriceDetail_SESSION_IMAGE")
                } else {
                    recylcerAdapter.addNewItem(
                        CSP.getData("productid").toString(),
                        CSP.getData("PriceDetail_SESSION_IMAGE").toString()
                    )
                    CSP.saveData(
                        "PriceDetail_SESSION_IMAGE_SET",
                        "${CSP.getData("PriceDetail_SESSION_IMAGE")}"
                    )
                    //CSP.delData("PriceDetail_SESSION_IMAGE")
                }
            } catch (ex: Exception) {

            }
        } else if (!CSP.getData("sess_gallery_img").equals("")) {
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                recylcerAdapter.addNewItem(
                    CSP.getData("productid").toString(),
                    CSP.getData("sess_gallery_img").toString()
                )
                //CSP.delData("sess_gallery_img")
            } catch (ex: Exception) {

            }
        }
    }


    fun fetchPrices(url: String) {
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
                    mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                println(userData.toString())
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, PriceDetailModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvPriceDetail.setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext())
                            rvPriceDetail.layoutManager = layoutManager
                            recylcerAdapter = PriceDetailAdapter(
                                requireContext(),
                                apiData.data as MutableList<PriceDetailData>,
                                arguments?.getInt("StoreID"),
                                requireActivity() as NewDashboardActivity,
                                settingData,
                                userData
                            )
                            rvPriceDetail.adapter = recylcerAdapter
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
}