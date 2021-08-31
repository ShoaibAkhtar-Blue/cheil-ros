package com.example.cheilros.fragments.storeview

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.adapters.StorePicturesAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_store_pictures.*
import okhttp3.*
import java.io.IOException
import java.util.ArrayList

class StorePicturesFragment : BaseFragment() {


    lateinit var elementData: List<GeneralPicturesData>
    lateinit var brandData: List<BrandData>

    var defaultElement = "0"
    var defaultBrand = "0"

    var adapter: StorePicturesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_pictures, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var titles: ArrayList<String> = arrayListOf()
        titles.add("First Item")
        titles.add("Second Item")
        titles.add("Third Item")
        titles.add("Fourth Item")
        titles.add("First Item")
        titles.add("Second Item")
        titles.add("Third Item")
        titles.add("Fourth Item")

        fetchElement("${CSP.getData("base_url")}/Webservice.asmx/StorePicture_GeneralElement")
        fetchBrand("${CSP.getData("base_url")}/Webservice.asmx/BrandList_General")

        fetchStorePictures(
            "${CSP.getData("base_url")}/Webservice.asmx/GeneralPictureVie?StoreID=${
                arguments?.getInt(
                    "StoreID"
                ).toString()
            }&BrandID=${defaultBrand}&ElementID=${defaultElement}"
        )


        btnElement.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list

            // add a list
            var channels : Array<String> = arrayOf()
            for (c in elementData){
                channels += c.StorePictureElementName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(elementData[which].PictureElementID)
                    defaultElement = elementData[which].PictureElementID.toString()
                    btnElement.text = "${elementData[which].StorePictureElementName}"
                    fetchStorePictures(
                        "${CSP.getData("base_url")}/Webservice.asmx/GeneralPictureVie?StoreID=${
                            arguments?.getInt(
                                "StoreID"
                            ).toString()
                        }&BrandID=${defaultBrand}&ElementID=${defaultElement}"
                    )
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        btnBrand.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list

            // add a list
            var channels : Array<String> = arrayOf()
            for (c in brandData){
                channels += c.BrandName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(brandData[which].BrandID)
                    defaultElement = brandData[which].BrandID.toString()
                    btnBrand.text = "${brandData[which].BrandName}"
                    fetchStorePictures(
                        "${CSP.getData("base_url")}/Webservice.asmx/GeneralPictureVie?StoreID=${
                            arguments?.getInt(
                                "StoreID"
                            ).toString()
                        }&BrandID=${defaultBrand}&ElementID=${defaultElement}"
                    )
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun fetchElement(url: String) {
        val client = OkHttpClient()
        mainLoading.setState(LoadingLayout.LOADING)

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
                        mainLoading.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, GeneralPicturesModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    elementData = apiData.data
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            //mainLoading.setState(LoadingLayout.COMPLETE)
                            try {
                                btnElement.text = elementData[0].StorePictureElementName
                            }catch (ex: Exception){

                            }
                        }
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                            mainLoading.setState(LoadingLayout.COMPLETE)
                        }
                    })
                }
            }
        })
    }

    fun fetchBrand(url: String) {
        val client = OkHttpClient()
        mainLoading.setState(LoadingLayout.LOADING)

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
                        mainLoading.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, BrandModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    brandData = apiData.data
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            //mainLoading.setState(LoadingLayout.COMPLETE)
                            try {
                                btnBrand.text = brandData[0].BrandName
                            }catch (ex: Exception){

                            }
                        }
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                            mainLoading.setState(LoadingLayout.COMPLETE)
                        }
                    })
                }
            }
        })
    }

    fun fetchStorePictures(url: String) {
        println(url)
        val client = OkHttpClient()
        mainLoading.setState(LoadingLayout.LOADING)
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
                    mainLoading.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, GeneralPicturesModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {

                        adapter = StorePicturesAdapter(requireContext(), apiData.data)

                        val gridLayoutManager = GridLayoutManager(
                            requireContext(),
                            2,
                            GridLayoutManager.VERTICAL,
                            false
                        )
                        rvStorePictures.layoutManager = gridLayoutManager
                        rvStorePictures.adapter = adapter

                        mainLoading.setState(LoadingLayout.COMPLETE)
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                        mainLoading.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }

}