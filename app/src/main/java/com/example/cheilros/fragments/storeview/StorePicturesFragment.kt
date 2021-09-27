package com.example.cheilros.fragments.storeview

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.adapters.StorePicturesAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.models.*
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_add_store_picture.*
import kotlinx.android.synthetic.main.dialog_add_store_picture.btnAccept
import kotlinx.android.synthetic.main.dialog_add_store_picture.btnCancel
import kotlinx.android.synthetic.main.dialog_add_store_picture.etRemarks
import kotlinx.android.synthetic.main.dialog_add_store_picture.rvTaskPictures
import kotlinx.android.synthetic.main.dialog_add_store_picture.txtTitle
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import kotlinx.android.synthetic.main.fragment_store_pictures.*
import kotlinx.android.synthetic.main.fragment_store_pictures.btnBrand
import kotlinx.android.synthetic.main.fragment_store_pictures.btnElement
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.ArrayList

class StorePicturesFragment : BaseFragment() {

    lateinit var layoutManagerPA: RecyclerView.LayoutManager
    lateinit var recylcerAdapterPA: CapturedPictureAdapter

    lateinit var elementData: List<GeneralPicturesData>
    lateinit var brandData: List<BrandData>

    var capturedPicturesList: MutableList<String> = arrayListOf()

    var defaultElement = "0"
    var defaultBrand = "0"

    var adapter: StorePicturesAdapter? = null

    lateinit var activity: NewDashboardActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity() as NewDashboardActivity
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

        view.txtStoreName.text =
            settingData.filter { it.fixedLabelName == "StoreMenu_Campaign" }.get(0).labelName

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
            var channels: Array<String> = arrayOf()
            for (c in elementData) {
                channels += c.StorePictureElementName
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(elementData[which].StorePictureElementID)
                    defaultElement = elementData[which].StorePictureElementID.toString()
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
            var channels: Array<String> = arrayOf()
            for (c in brandData) {
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

        btnAddStorePicture.setOnClickListener {
            val li = LayoutInflater.from(requireContext())
            val promptsView: View = li.inflate(R.layout.dialog_add_store_picture, null)

            var selEle = "0"
            var selBrnd = "0"

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


            dialog.btnTakePicture.setOnClickListener {
                if (capturedPicturesList.size == 0) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

                    builder.setTitle("Choose...")
                    builder.setMessage("Please select one of the options")

                    builder.setPositiveButton("Camera") { dialog, which ->
                        CSP.saveData("fragName", "StorePicture")
                        Navigation.findNavController(view)
                            .navigate(R.id.action_storePicturesFragment_to_cameraActivity)
                    }

                    builder.setNegativeButton("Gallery") { dialog, which ->
                        activity?.pickFromGallery()
                    }

                    builder.setNeutralButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    builder.show()
                }
            }

            dialog.txtTitle.text = "Add New Store Picture"

            dialog.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            val btnEle = dialog.btnElement
            val btnBrnd = dialog.btnBrand

            btnEle.setOnClickListener {
                // setup the alert builder
                // setup the alert builder
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("")

                // add a list

                // add a list
                var channels: Array<String> = arrayOf()
                for (c in elementData) {
                    channels += c.StorePictureElementName
                }

                builder.setItems(channels,
                    DialogInterface.OnClickListener { dialog, which ->
                        println("StorePictureElementID: ${elementData[which].StorePictureElementID}")
                        selEle = elementData[which].StorePictureElementID.toString()
                        btnEle.text = "${elementData[which].StorePictureElementName}"

                    })

                // create and show the alert dialog

                // create and show the alert dialog
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            btnBrnd.setOnClickListener {
                // setup the alert builder
                // setup the alert builder
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("")

                // add a list

                // add a list
                var channels: Array<String> = arrayOf()
                for (c in brandData) {
                    channels += c.BrandName
                }

                builder.setItems(channels,
                    DialogInterface.OnClickListener { dialog, which ->
                        println(brandData[which].BrandID)
                        selBrnd = brandData[which].BrandID.toString()
                        btnBrnd.text = "${brandData[which].BrandName}"
                    })

                // create and show the alert dialog

                // create and show the alert dialog
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            dialog.btnAccept.setOnClickListener {

                val client = OkHttpClient()

                val url: String =
                    "${CSP.getData("base_url")}/Webservice.asmx/GeneralPictureAdd?StoreID=${
                        arguments?.getInt(
                            "StoreID"
                        ).toString()
                    }&BrandID=$selBrnd&TeamMemberID=${CSP.getData("user_id")}&PictureElementID=$selEle&Remarks=${dialog.etRemarks.text}"

                println(url)

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
                            "ElementImg",
                            fileName,
                            sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                        )
                    }

                    builder.addFormDataPart(
                        "test",
                        "test"
                    )

                    val requestBody = builder.build()

                    val request: Request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(activity) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Task not completed!")
                                        .sneakError()
                                }
                                dialog.dismiss()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(activity) // Activity, Fragment or ViewGroup
                                        .setTitle("Success!!")
                                        .setMessage("Task updated!")
                                        .sneakSuccess()
                                }
                                CSP.delData("fragName")
                                CSP.delData("StorePicture_SESSION_IMAGE")
                                CSP.delData("StorePicture_SESSION_IMAGE_SET")
                                dialog.dismiss()

                                fetchStorePictures(
                                    "${CSP.getData("base_url")}/Webservice.asmx/GeneralPictureVie?StoreID=${
                                        arguments?.getInt(
                                            "StoreID"
                                        ).toString()
                                    }&BrandID=${defaultBrand}&ElementID=${defaultElement}"
                                )
                            }
                        }

                    })
                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                }
            }

            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        super.onResume()
        println(CSP.getData("StorePicture_SESSION_IMAGE_SET"))
        if (!CSP.getData("StorePicture_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            if (CSP.getData("StorePicture_SESSION_IMAGE_SET").equals("")) {
                recylcerAdapterPA.addNewItem(CSP.getData("StorePicture_SESSION_IMAGE"))
                CSP.saveData(
                    "StorePicture_SESSION_IMAGE_SET",
                    CSP.getData("StorePicture_SESSION_IMAGE")
                )
                CSP.delData("StorePicture_SESSION_IMAGE")
            } else {
                recylcerAdapterPA.addNewItem(CSP.getData("StorePicture_SESSION_IMAGE"))
                CSP.saveData(
                    "StorePicture_SESSION_IMAGE_SET",
                    "${CSP.getData("StorePicture_SESSION_IMAGE_SET")},${CSP.getData("StorePicture_SESSION_IMAGE")}"
                )
                CSP.delData("StorePicture_SESSION_IMAGE")
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
                try {
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
                                } catch (ex: Exception) {

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
                try {
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
                                } catch (ex: Exception) {

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

    fun fetchStorePictures(url: String) {
        println(url)
        btnAddStorePicture.visibility = View.INVISIBLE
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
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, GeneralPicturesModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {

                            adapter = StorePicturesAdapter(
                                requireContext(),
                                apiData.data,
                                this@StorePicturesFragment
                            )

                            val gridLayoutManager = GridLayoutManager(
                                requireContext(),
                                2,
                                GridLayoutManager.VERTICAL,
                                false
                            )
                            rvStorePictures.layoutManager = gridLayoutManager
                            rvStorePictures.adapter = adapter

                            if (CSP.getData("team_type_id")!!.toInt() <= 4) {

                            } else
                                btnAddStorePicture.visibility = View.VISIBLE

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