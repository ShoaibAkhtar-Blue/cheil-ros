package com.example.cheilros.fragments.storeview

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.cheilros.helpers.CoreHelperMethods
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.dialog_barcode.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.txtStoreSubName
import kotlinx.android.synthetic.main.fragment_acrivity_detail.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class AcrivityDetailFragment : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: CapturedPictureAdapter
    lateinit var recylcerAdapterAfter: CapturedPictureAdapter

    lateinit var layoutManagerBC: RecyclerView.LayoutManager
    lateinit var recylcerAdapterBC: BarcodeAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()
    var capturedPicturesListAfter: MutableList<String> = arrayListOf()


    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acrivity_detail, container, false)

        //region Set Labels
        view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Activity" }
            .get(0).labelName + " / ${arguments?.getString("ActivityTypeName")}"
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
                builder.setTitle("Close Session")
                builder.setMessage("Are You Sure you want to close current session?")

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
        } else {
            LLScanBarcode.visibility = View.INVISIBLE
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
            txtBarcodeCount.text = if (CSP.getData("activity_barcodes").equals("")) "0" else {
                CSP.getData("activity_barcodes")?.split(",")?.size.toString()
            }
        } catch (ex: Exception) {
            txtBarcodeCount.text = "0"
        }

        RLbarcode.setOnClickListener {

            if(!CSP.getData("activity_barcodes").equals("")){
                var savedBarcodes = CSP.getData("activity_barcodes")?.split(",")?.toTypedArray()
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
                    BarcodeAdapter(requireContext(),
                        it1, dialog)
                }!!
                dialog.rvBarcode.adapter = recylcerAdapterBC

                dialog.show()
            } else {
                Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                    .setTitle("Info!!")
                    .setMessage("No Barcode Added to the session!")
                    .sneakWarning()
            }
        }

        btnScanBarcode.setOnClickListener {
            findNavController().navigate(R.id.action_acrivityDetailFragment_to_barcodeFragment)
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
                activity.pickFromGallery()
            }

            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        btnSubmit.setOnClickListener {
            val client = OkHttpClient()
            try {
                val builder: MultipartBody.Builder =
                    MultipartBody.Builder().setType(MultipartBody.FORM)

                builder.addFormDataPart(
                    "SerialNumbers",
                    CSP.getData("activity_barcodes").toString()
                )

                for (paths in capturedPicturesList) {
                    println(paths)
                    val sourceFile = File(paths)
                    val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                    val fileName: String = sourceFile.name
                    builder.addFormDataPart(
                        "BeforeActivityPicture",
                        fileName,
                        sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                    )
                }

                for (paths in capturedPicturesListAfter) {
                    println(paths)
                    val sourceFile = File(paths)
                    val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
                    val fileName: String = sourceFile.name
                    builder.addFormDataPart(
                        "AfterActivityPicture",
                        fileName,
                        sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull())
                    )
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
                        }&StoreID=${arguments?.getInt("StoreID")}&BrandID=1&ActivityDescription=${txtBrandDescription.text}&StatusID=1&Quantity=${txtBrandQuantity.text}"
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
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println(body)

                        requireActivity().runOnUiThread {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Activity Updated!")
                                    .sneakSuccess()
                            }
                            CSP.delData("activity_barcodes")
                            CSP.delData("ActivityDetail_SESSION_IMAGE")
                            CSP.delData("ActivityDetail_SESSION_IMAGE_SET")

                            findNavController().navigateUp()
                        }


                    }

                })

            } catch (ex: Exception) {

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

            if (CSP.getData("ActivityDetail_BARCODE_SET").equals("")) {
                CSP.saveData("ActivityDetail_BARCODE_SET", CSP.getData("activity_barcodes"))
                CSP.delData("activity_barcodes")
            } else {
                CSP.saveData(
                    "ActivityDetail_BARCODE_SET",
                    "${CSP.getData("ActivityDetail_BARCODE_SET")},${CSP.getData("activity_barcodes")}"
                )
                CSP.delData("activity_barcodes")
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

                if (CSP.getData("imgType") == "before")
                    recylcerAdapter.addNewItem(CSP.getData("sess_gallery_img").toString())
                else
                    recylcerAdapterAfter.addNewItem(CSP.getData("sess_gallery_img").toString())

                CSP.delData("sess_gallery_img")
            } catch (ex: Exception) {

            }
        }
    }

}