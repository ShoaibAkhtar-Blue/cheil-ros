package com.example.cheilros.fragments.storeview

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.BarcodeAdapter
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.UtilClass
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.models.DeploymentReasonData
import com.example.cheilros.models.DeploymentReasonModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.dialog_barcode.*
import kotlinx.android.synthetic.main.dialog_barcode_input.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


class AcrivityDetailFragment : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: CapturedPictureAdapter
    lateinit var recylcerAdapterAfter: CapturedPictureAdapter

    lateinit var layoutManagerBC: RecyclerView.LayoutManager
    lateinit var recylcerAdapterBC: BarcodeAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()
    var capturedPicturesListAfter: MutableList<String> = arrayListOf()

    lateinit var reasonData: List<DeploymentReasonData>

    var defaultReason = "0"


    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acrivity_detail, container, false)

        //region Set Labels
        view.txtStoreName.text = arguments?.getString("ActivityTypeName")

        /*view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Activity" }
            .get(0).labelName + " / ${arguments?.getString("ActivityTypeName")}"*/
        view.txtBrandDescription.hint =
            settingData.filter { it.fixedLabelName == "ActivityDescription" }.get(0).labelName
        view.txtBrandQuantity.hint =
            settingData.filter { it.fixedLabelName == "Activity_Qty" }.get(0).labelName
        view.ScanCode.text = settingData.filter { it.fixedLabelName == "ScanCode" }.get(0).labelName
        view.btnSubmit.text =
            settingData.filter { it.fixedLabelName == "LoginForgetSubmitButton" }.get(0).labelName
        view.ActivityScreen_Before.text =
            settingData.filter { it.fixedLabelName == "ActivityScreen_Before" }.get(0).labelName
        view.ActivityScreen_After.text =
            settingData.filter { it.fixedLabelName == "ActivityScreen_After" }.get(0).labelName
        //endregion

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            try {
                // Handle the back button event
                println("callback")
                // setup the alert builder
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(settingData.filter { it.fixedLabelName == "General_CloseSession" }
                    .get(0).labelName)
                builder.setMessage(settingData.filter { it.fixedLabelName == "General_CloseSessionMessage" }
                    .get(0).labelName)

                // add the buttons

                // add the buttons
                builder.setPositiveButton("Ok") { dialogInterface, which ->
                    findNavController().popBackStack()
                }

                builder.setNegativeButton("Cancel", null)

                // create and show the alert dialog

                // create and show the alert dialog
                val dialog: AlertDialog = builder.create()
                dialog.show()
            } catch (ex: Exception) {
                //findNavController().backStack
            }
        }


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtStoreSubName.text = arguments?.getString("ActivityTypeName")
        txtTitleHeader.text = arguments?.getString("ActivityCategoryName")



        if (arguments?.getInt("ActivityTypeID")!! > 20) {
            LLScanBarcode.visibility = View.VISIBLE
            LLAfter.visibility = View.VISIBLE
            ActivityScreen_After.visibility = View.VISIBLE
            ActivityScreen_Before.visibility = View.VISIBLE

            if(CSP.getData("OnlyBefore") == "Y"){
                ActivityScreen_After.visibility = View.GONE
                ActivityScreen_Before.visibility = View.INVISIBLE
                LLAfter.visibility = View.GONE
            }

            fetchDeploymentReason("${CSP.getData("base_url")}/Webservice.asmx/DeploymentReason")

            btnDeploymentReason.setOnClickListener {
                // setup the alert builder
                val builder: android.app.AlertDialog.Builder =
                    android.app.AlertDialog.Builder(requireContext())
                builder.setTitle("")

                // add a list

                // add a list
                var channels: Array<String> = arrayOf()
                for (c in reasonData) {
                    channels += c.DeploymentReason
                }

                builder.setItems(channels,
                    DialogInterface.OnClickListener { dialog, which ->
                        println(reasonData[which].DeploymentReasonID)
                        defaultReason = reasonData[which].DeploymentReasonID.toString()
                        btnDeploymentReason.text = "${reasonData[which].DeploymentReason}"
                    })

                // create and show the alert dialog

                // create and show the alert dialog
                val dialog: android.app.AlertDialog = builder.create()
                dialog.show()
            }
        } else {
            ActivityScreen_Before.visibility = View.GONE
            LLScanBarcode.visibility = View.INVISIBLE
            btnDeploymentReason.visibility = View.GONE
        }

        try {
            rvActivityPictures.setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            rvActivityPictures.layoutManager = layoutManager
            recylcerAdapter = CapturedPictureAdapter(requireContext(), capturedPicturesList)
            rvActivityPictures.adapter = recylcerAdapter

            rvActivityPicturesAfter.setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            rvActivityPicturesAfter.layoutManager = layoutManager
            recylcerAdapterAfter =
                CapturedPictureAdapter(requireContext(), capturedPicturesListAfter)
            rvActivityPicturesAfter.adapter = recylcerAdapterAfter
        } catch (ex: Exception) {

        }

        try {
            txtBarcodeCount.text =
                if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) "0" else {
                    CSP.getData("ActivityDetail_BARCODE_SET")?.split(",")?.size.toString()
                }
        } catch (ex: Exception) {
            txtBarcodeCount.text = "0"
        }

        RLbarcode.setOnClickListener {

            if (!CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                var savedBarcodes =
                    CSP.getData("ActivityDetail_BARCODE_SET")?.split(",")?.toTypedArray()
                //var savedBarcodes = "abc, xyz"?.split(",").toTypedArray()

                val li = LayoutInflater.from(requireContext())
                val promptsView: View = li.inflate(R.layout.dialog_barcode, null)

                val dialog = Dialog(requireContext())
                dialog.setContentView(promptsView)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(true)

                dialog.rvBarcode.setHasFixedSize(true)
                layoutManagerBC = LinearLayoutManager(requireContext())
                dialog.rvBarcode.layoutManager = layoutManagerBC
                recylcerAdapterBC = savedBarcodes?.toMutableList()?.let { it1 ->
                    BarcodeAdapter(
                        requireContext(),
                        it1, dialog, true, null, null, arguments
                    )
                }!!
                dialog.rvBarcode.adapter = recylcerAdapterBC

                dialog.setOnDismissListener {
                    if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                        txtBarcodeCount.text = "0"
                    } else {
                        var barcodeCount =
                            CSP.getData("ActivityDetail_BARCODE_SET")?.split(",")?.size
                        txtBarcodeCount.text = barcodeCount.toString()

                    }
                }

                dialog.show()
            } else {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Info!!")
                    .setMessage("No Barcode Added to the session!")
                    .sneakWarning()
            }
        }

        btnScanBarcode.setOnClickListener {
            //findNavController().navigate(R.id.action_acrivityDetailFragment_to_barcodeFragment)
            findNavController().navigate(R.id.action_acrivityDetailFragment_to_barcodeActivity)
        }

        btnInputBarcode.setOnClickListener {
            val li = LayoutInflater.from(context)
            val promptsView: View = li.inflate(R.layout.dialog_barcode_input, null)

            val dialog = Dialog(requireContext())
            dialog.setContentView(promptsView)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)

            dialog.btnAccept.setOnClickListener {
                dialog.rgBarcodeType.visibility = View.GONE
                val selectedBarcodeType: Int = dialog.rgBarcodeType!!.checkedRadioButtonId
                val barcodeTypeVal = dialog.findViewById<RadioButton>(selectedBarcodeType).text

                var barcodeType = if (barcodeTypeVal.equals("LDU"))
                    "1"
                else
                    "2"


                val barInput = dialog.etInputBarcode.text.toString()

                if (!barInput.equals("")) {
                    Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                        .setTitle("Success!!")
                        .setMessage("Barcode Added to this session!")
                        .sneakSuccess()

                    println("ActivityDetail_BARCODE_SET: ${barInput}")

                    if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                        CSP.saveData("ActivityDetail_BARCODE_SET", barInput)
                        CSP.delData("activity_barcodes")
                        txtBarcodeCount.text = "1"
                    } else {
                        CSP.saveData(
                            "ActivityDetail_BARCODE_SET",
                            "${CSP.getData("ActivityDetail_BARCODE_SET")},${barInput}"
                        )
                        CSP.delData("activity_barcodes")

                        var barcodeCount =
                            CSP.getData("ActivityDetail_BARCODE_SET")?.split(",")?.size
                        txtBarcodeCount.text = barcodeCount.toString()

                    }
                }

                dialog.dismiss()
            }
            dialog.show()
        }

        btnTakePicture.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose...")
            builder.setMessage("Please select one of the options")

            builder.setPositiveButton("Camera") { dialog, which ->
                CSP.saveData("fragName", "ActivityDetail")
                CSP.saveData("imgType", "before")
                val bundle = bundleOf("fragName" to "ActivityDetailFragment")
                findNavController().navigate(
                    R.id.action_acrivityDetailFragment_to_cameraActivity,
                    bundle
                )
            }

            builder.setNegativeButton("Gallery") { dialog, which ->
                val activity = requireActivity() as NewDashboardActivity
                CSP.saveData("imgType", "before")
                activity.pickFromGallery()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        btnTakePictureAfter.setOnClickListener {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Choose...")
            builder.setMessage("Please select one of the options")

            builder.setPositiveButton("Camera") { dialog, which ->
                CSP.saveData("fragName", "ActivityDetail")
                CSP.saveData("imgType", "after")
                val bundle = bundleOf("fragName" to "ActivityDetailFragment")
                findNavController().navigate(
                    R.id.action_acrivityDetailFragment_to_cameraActivity,
                    bundle
                )
            }

            builder.setNegativeButton("Gallery") { dialog, which ->
                val activity = requireActivity() as NewDashboardActivity
                CSP.saveData("imgType", "after")
                activity.pickFromGallery()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        btnSubmit.setOnClickListener {
            mainLoadingLayout.setState(LoadingLayout.LOADING)
            /*btnSubmit.isClickable = false
            btnSubmit.isEnabled = false

            btnSubmit.text = "Processing..."*/

            //val client = OkHttpClient()
            //NIK: 2022-03-22
            val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                .build()
            try {
                println("SerialNumbers: ${CSP.getData("ActivityDetail_BARCODE_SET").toString()}")
                val builder: MultipartBody.Builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                builder.addFormDataPart(
                    "SerialNumbers",
                    CSP.getData("ActivityDetail_BARCODE_SET").toString()
                )

                for (paths in capturedPicturesList) {
                    println(paths)
                    //NIK: 2022-03-18
                    //val sourceFile = File(paths)
                    val ImageFile = File(paths)
                    val sourceFile = UtilClass.saveBitmapToFile(ImageFile)

                    if (sourceFile!= null) {
                        val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                        val fileName: String = sourceFile.name
                        builder.addFormDataPart(
                            "BeforeActivityPicture",
                            fileName,
                            sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                        )
                    }
                }

                for (paths in capturedPicturesListAfter) {
                    println(paths)
                    //NIK: 2022-03-18
                    //val sourceFile = File(paths)
                    val ImageFile = File(paths)
                    val sourceFile = UtilClass.saveBitmapToFile(ImageFile)
                    if (sourceFile!= null) {
                        val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                        val fileName: String = sourceFile.name
                        builder.addFormDataPart(
                            "AfterActivityPicture",
                            fileName,
                            sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                        )
                    }
                }

                if (capturedPicturesList.size == 0)
                    builder.addFormDataPart("BeforeActivityPicture", "")
                if (capturedPicturesListAfter.size == 0)
                    builder.addFormDataPart("AfterActivityPicture", "")
                if (CSP.getData("ActivityDetail_BARCODE_SET").toString() == "")
                    builder.addFormDataPart("SerialNumbers", "")


                if(CSP.getData("Task_Before") == "Y" && capturedPicturesList.size == 0){
                    requireActivity().runOnUiThread {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage("Please take atleast one Before Image")
                                .sneakWarning()
                        }
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                    }
                    return@setOnClickListener
                }


                if(CSP.getData("Task_After") == "Y" && capturedPicturesListAfter.size == 0 && arguments?.getInt("ActivityTypeID")!! > 20){
                    requireActivity().runOnUiThread {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage("Please take atleast one After Image")
                                .sneakWarning()
                        }
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                    }

                    return@setOnClickListener
                }


                /*if(!CSP.getData("ActivityDetail_SESSION_IMAGE_SET").equals("")){
                    val imgPaths = CSP.getData("ActivityDetail_SESSION_IMAGE_SET")?.split(",")
                    if (imgPaths != null) {
                        for (paths in imgPaths){
                            println(paths)
                            val sourceFile = File(paths)
                            val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                            val fileName: String = sourceFile.name
                            builder.addFormDataPart("ActivityPictures", fileName,sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull()))
                        }
                    }
                }*/

                /* val requestBody: RequestBody =
                     MultipartBody.Builder().setType(MultipartBody.FORM)
                         .addFormDataPart("SerialNumbers", CSP.getData("activity_barcodes").toString())
                         .build()*/
                val requestBody = builder.build()
                println(
                    "${CSP.getData("base_url")}/OperMarketActivities.asmx/MarketActivityDetails?TeamMemberID=${
                        CSP.getData(
                            "user_id"
                        )
                    }&ActivityTypeID=${arguments?.getInt("ActivityTypeID")}&ActivityCategoryID=${
                        arguments?.getInt(
                            "ActivityCategoryID"
                        )
                    }&StoreID=${arguments?.getInt("StoreID")}&BrandID=1&ActivityDescription=${txtBrandDescription.text}&StatusID=1&Quantity=${txtBrandQuantity.text}&DeploymentReasonID=${defaultReason}"
                )
                val request: Request = Request.Builder()
                    .url(
                        "${CSP.getData("base_url")}/OperMarketActivities.asmx/MarketActivityDetails?TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&ActivityTypeID=${arguments?.getInt("ActivityTypeID")}&ActivityCategoryID=${
                            arguments?.getInt(
                                "ActivityCategoryID"
                            )
                        }&StoreID=${arguments?.getInt("StoreID")}&BrandID=1&ActivityDescription=${txtBrandDescription.text}&StatusID=1&Quantity=${txtBrandQuantity.text}&DeploymentReasonID=${defaultReason}"
                    )
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        requireActivity().runOnUiThread {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage(e.message.toString())
                                    .sneakWarning()

                                /*btnSubmit.isClickable = true
                                btnSubmit.isEnabled = true

                                btnSubmit.text = "Save"*/

                                mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println("ActivityDetailResponse")
                        println(body)

                        requireActivity().runOnUiThread {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Activity Updated!")
                                    .sneakSuccess()
                            }
                            CSP.delData("activity_barcodes")
                            CSP.delData("ActivityDetail_BARCODE_SET")
                            txtBarcodeCount.text = "0"
                            CSP.delData("ActivityDetail_SESSION_IMAGE")
                            CSP.delData("ActivityDetail_SESSION_IMAGE_SET")

                            findNavController().navigateUp()

                            mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                        }
                    }
                })
            } catch (ex: Exception) {
                mainLoadingLayout.setState(LoadingLayout.COMPLETE)
            }

        }

    }

    override fun onResume() {
        super.onResume()

        if (!CSP.getData("activity_barcodes").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Barcode Added to this session!")
                .sneakSuccess()

            println("ActivityDetail_BARCODE_SET: ${CSP.getData("ActivityDetail_BARCODE_SET")}")

            if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                CSP.saveData("ActivityDetail_BARCODE_SET", CSP.getData("activity_barcodes"))
                CSP.delData("activity_barcodes")
                txtBarcodeCount.text = "1"
            } else {
                CSP.saveData(
                    "ActivityDetail_BARCODE_SET",
                    "${CSP.getData("ActivityDetail_BARCODE_SET")},${CSP.getData("activity_barcodes")}"
                )
                CSP.delData("activity_barcodes")

                var barcodeCount = CSP.getData("ActivityDetail_BARCODE_SET")?.split(",")?.size
                txtBarcodeCount.text = barcodeCount.toString()

            }
        }

        println(CSP.getData("ActivityDetail_SESSION_IMAGE_SET"))
        if (!CSP.getData("ActivityDetail_SESSION_IMAGE").equals("")) {
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            if (CSP.getData("ActivityDetail_SESSION_IMAGE_SET").equals("")) {
                if (CSP.getData("imgType") == "before")
                    recylcerAdapter.addNewItem(CSP.getData("ActivityDetail_SESSION_IMAGE"))
                else
                    recylcerAdapterAfter.addNewItem(CSP.getData("ActivityDetail_SESSION_IMAGE"))

                CSP.saveData(
                    "ActivityDetail_SESSION_IMAGE_SET",
                    CSP.getData("ActivityDetail_SESSION_IMAGE")
                )
                CSP.delData("ActivityDetail_SESSION_IMAGE")
            } else {
                if (CSP.getData("imgType") == "before")
                    recylcerAdapter.addNewItem(CSP.getData("ActivityDetail_SESSION_IMAGE"))
                else
                    recylcerAdapterAfter.addNewItem(CSP.getData("ActivityDetail_SESSION_IMAGE"))

                CSP.saveData(
                    "ActivityDetail_SESSION_IMAGE_SET",
                    "${CSP.getData("ActivityDetail_SESSION_IMAGE_SET")},${CSP.getData("ActivityDetail_SESSION_IMAGE")}"
                )
                CSP.delData("ActivityDetail_SESSION_IMAGE")
            }
        } else if (!CSP.getData("sess_gallery_img").equals("")) {
            try {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Success!!")
                    .setMessage("Image Added to this session!")
                    .sneakSuccess()

                println("imgType: ${CSP.getData("imgType")}")

                if (CSP.getData("imgType") == "before")
                    recylcerAdapter.addNewItem(CSP.getData("sess_gallery_img").toString())
                else
                    recylcerAdapterAfter.addNewItem(CSP.getData("sess_gallery_img").toString())

                CSP.delData("sess_gallery_img")
                CSP.delData("imgType")
            } catch (ex: Exception) {

            }
        }
    }

    fun fetchDeploymentReason(url: String) {
        mainLoadingLayout.setState(LoadingLayout.LOADING)

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
                        mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                    }
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, DeploymentReasonModel::class.java)
                    println(apiData.status)
                    if (apiData.status == 200) {
                        reasonData = apiData.data
                        try {
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                                    try {
                                        btnDeploymentReason.text = reasonData[0].DeploymentReason
                                    } catch (ex: Exception) {

                                    }
                                }
                            })
                        } catch (ex: Exception) {
//                        requireActivity().runOnUiThread(java.lang.Runnable {
//                            Toast.makeText(context, "Error ${ex.message}", Toast.LENGTH_SHORT).show()
//                        })
                        }

                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                                mainLoadingLayout.setState(LoadingLayout.COMPLETE)
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