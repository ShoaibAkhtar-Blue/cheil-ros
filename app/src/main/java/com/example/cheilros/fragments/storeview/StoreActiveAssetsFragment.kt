package com.example.cheilros.fragments.storeview

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.AssetListAdapter
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.dialog_assets.*
import kotlinx.android.synthetic.main.dialog_assets.btnDate
import kotlinx.android.synthetic.main.dialog_assets.checkBox
import kotlinx.android.synthetic.main.dialog_assignedtask.*
import kotlinx.android.synthetic.main.dialog_assignedtask.rvTaskPictures
import kotlinx.android.synthetic.main.dialog_checklist.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_store_active_assets.*
import kotlinx.android.synthetic.main.fragment_store_status.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.*

class StoreActiveAssetsFragment(val StoreID: Int?, val StoreName: String?) : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: AssetListAdapter

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()

    var defaultBrand = "0"
    var defaultAsset = "0"

    lateinit var brandData: List<AssetBrandsData>
    lateinit var assetsData: List<AssetBrandsListData>

    lateinit var activity: NewDashboardActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_active_assets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity = requireActivity() as NewDashboardActivity
        fetchAssetList("${CSP.getData("base_url")}/Asset.asmx/AssetList?StoreID=$StoreID")

        btnAddAsset.setOnClickListener {
            addAsset(null)
        }

        fetchBrands("${CSP.getData("base_url")}/Asset.asmx/AssetTypeBrand?BrandID=${defaultBrand}")
    }

    override fun onResume() {
        super.onResume()
        println("onResume StoreActive")
        if (!CSP.getData("StoreAsset_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            try {
                recylcerAdapterPA.addNewItem(CSP.getData("StoreAsset_SESSION_IMAGE").toString())
                CSP.delData("StoreAsset_SESSION_IMAGE")

                if (CSP.getData("StoreAsset_SESSION_IMAGE").equals("")) {
                    recylcerAdapterPA.addNewItem(CSP.getData("StoreAsset_SESSION_IMAGE").toString())
                    CSP.saveData(
                        "StoreAsset_SESSION_IMAGE_SET",
                        CSP.getData("StoreAsset_SESSION_IMAGE")
                    )
                    CSP.delData("StoreAsset_SESSION_IMAGE")
                } else {
                    recylcerAdapterPA.addNewItem(CSP.getData("StoreAsset_SESSION_IMAGE").toString())
                    CSP.saveData(
                        "StoreAsset_SESSION_IMAGE_SET",
                        "${CSP.getData("StoreAsset_SESSION_IMAGE_SET")},${CSP.getData("StoreAsset_SESSION_IMAGE")}"
                    )
                    CSP.delData("StoreAsset_SESSION_IMAGE")
                }
            } catch (ex: Exception) {

            }
        } else if (!CSP.getData("sess_gallery_img").equals("")) {
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                recylcerAdapterPA.addNewItem(CSP.getData("sess_gallery_img").toString())
                CSP.delData("sess_gallery_img")
            } catch (ex: Exception) {

            }
        }
    }

    fun fetchAssetList(url: String) {
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

                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, AssetListModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvAssetsList.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvAssetsList.layoutManager = layoutManager
                        recylcerAdapter =
                            AssetListAdapter(
                                requireContext(),
                                apiData.data,
                                this@StoreActiveAssetsFragment
                            )
                        rvAssetsList.adapter = recylcerAdapter
                        val emptyView: View = todo_list_empty_view3
                        rvAssetsList.setEmptyView(emptyView)
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

    fun fetchBrands(url: String) {
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
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, AssetBrandsModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    brandData = apiData.data
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

    fun addAsset(updatedList: AssetListData?) {

        val li = LayoutInflater.from(requireContext())
        val promptsView: View = li.inflate(R.layout.dialog_assets, null)

        var assetMethod = "AddNewAsset"

        val dialog = Dialog(requireContext())
        dialog.setContentView(promptsView)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)



        dialog.rvTaskPictures.setHasFixedSize(true)
        layoutManagerPA = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        dialog.rvTaskPictures.layoutManager = layoutManagerPA
        capturedPicturesList.clear()
        recylcerAdapterPA = CapturedPictureAdapter(requireContext(), capturedPicturesList)
        dialog.rvTaskPictures.adapter = recylcerAdapterPA

        val btnBrands = dialog.btnBrands
        val btnAsset = dialog.btnAsset

        if (updatedList != null) {
            assetMethod = "UpdateAsset"
            defaultBrand = updatedList.BrandID.toString()
            btnBrands.text = updatedList.BrandName

            defaultAsset = updatedList.AssetID.toString()
            btnAsset.text = updatedList.AssetTypeName

            dialog.btnDate.text = updatedList.CreationDateTime
            dialog.etdescription.setText(updatedList.AssetDescription)
            //dialog.checkBox.isChecked = updatedList[0].
        }

        btnBrands.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose brand")

            // add a list
            var channels: Array<String> = arrayOf()
            for (c in brandData) {
                channels += c.BrandName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(brandData[which].BrandID)
                    defaultBrand = brandData[which].BrandID.toString()
                    btnBrands.text = "Selected Brand: ${brandData[which].BrandName}"
                })

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        btnAsset.setOnClickListener {
            if (defaultBrand != "0") {
                println("btnAsset")
                assetsData = brandData.filter { it.BrandID == defaultBrand.toInt() }[0].Assets
                println("btnAsset Size: " + assetsData.size)
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Choose asset")

                // add a list
                var channels: Array<String> = arrayOf()
                for (c in assetsData) {
                    channels += c.AssetTypeName
                }

                builder.setItems(channels,
                    DialogInterface.OnClickListener { dialog, which ->
                        println(assetsData[which].AssetTypeName)
                        defaultAsset = assetsData[which].AssetTypeID.toString()
                        btnAsset.text = "Selected Asset: ${assetsData[which].AssetTypeName}"
                    })
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }

        dialog.btnDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    requireContext(), DatePickerDialog.OnDateSetListener
                    { view, year, monthOfYear, dayOfMonth ->
                        val currentDate: String = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                        dialog.btnDate.text = currentDate
                    }, year, month, day
                )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        dialog.btnTakePicture.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

            builder.setTitle("Choose...")
            builder.setMessage("Please select one of the options")

            builder.setPositiveButton("Camera") { dialog, which ->
                CSP.saveData("fragName", "StoreAsset")
                findNavController().navigate(R.id.action_storeViewFragment_to_cameraActivity)
            }

            builder.setNegativeButton("Gallery") { dialog, which ->
                activity.pickFromGallery()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        dialog.btnAcceptAsset.setOnClickListener {
            val client = OkHttpClient()
            try {
                val builder: MultipartBody.Builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                for (paths in capturedPicturesList) {
                    println(paths)
                    val sourceFile = File(paths)
                    val mimeType =
                        CoreHelperMethods(context as Activity).getMimeType(sourceFile)
                    val fileName: String = sourceFile.name
                    builder.addFormDataPart(
                        "AssetsPicture",
                        fileName,
                        sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                    )
                }

                builder.addFormDataPart(
                    "test",
                    "test"
                )

                val requestBody = builder.build()

                val status = if (dialog.checkBox.isChecked) "Active" else "In-Active"

                val request: Request = Request.Builder()
                    .url(
                        "${CSP.getData("base_url")}/Asset.asmx/${assetMethod}?AssetTypeID=${defaultAsset}&BrandID=${defaultBrand}&StoreID=${
                            StoreID
                        }&AssetDescription=${dialog.etdescription.text}&ActiveStatus=${status}&TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }"
                    )
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        (requireActivity().runOnUiThread {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("not completed!")
                                    .sneakError()
                            }

                            dialog.dismiss()
                        })
                    }

                    override fun onResponse(call: Call, response: Response) {
                        (requireActivity().runOnUiThread {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("updated!")
                                    .sneakSuccess()
                            }

                            CSP.delData("fragName")
                            CSP.delData("StoreAsset_SESSION_IMAGE")
                            CSP.delData("StoreAsset_SESSION_IMAGE_SET")

                            fetchAssetList("${CSP.getData("base_url")}/Asset.asmx/AssetList?StoreID=1")

                            dialog.dismiss()
                        })
                    }

                })


            } catch (ex: Exception) {

            }
        }

        dialog.show()
    }
}