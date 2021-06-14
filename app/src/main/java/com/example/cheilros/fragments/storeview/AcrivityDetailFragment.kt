package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.CapturedPictureAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_acrivity_detail.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.txtStoreSubName
import kotlinx.android.synthetic.main.fragment_acrivity_detail.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class AcrivityDetailFragment : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: CapturedPictureAdapter

    var capturedPicturesList: MutableList<String> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acrivity_detail, container, false)

        //region Set Labels
        view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Activity" }.get(0).labelName
        view.txtBrandDescription.hint = settingData.filter { it.fixedLabelName == "ActivityDescription" }.get(0).labelName
        view.txtBrandQuantity.hint = settingData.filter { it.fixedLabelName == "Activity_Qty" }.get(0).labelName
        view.ScanCode.text = settingData.filter { it.fixedLabelName == "ScanCode" }.get(0).labelName
        view.btnSubmit.text = settingData.filter { it.fixedLabelName == "LoginForgetSubmitButton" }.get(0).labelName
        //endregion



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtStoreSubName.text = arguments?.getString("ActivityTypeName")
        txtTitleHeader.text = arguments?.getString("ActivityCategoryName")

        try{
            rvActivityPictures.setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL, false)
            rvActivityPictures.layoutManager = layoutManager
            recylcerAdapter = CapturedPictureAdapter(requireContext(), capturedPicturesList)
            rvActivityPictures.adapter = recylcerAdapter
        }catch (ex: Exception){

        }

        try {
            txtBarcodeCount.text = if (CSP.getData("activity_barcodes").equals("")) "0" else {
                CSP.getData("activity_barcodes")?.split(",")?.size.toString()
            }
        }catch (ex: Exception){
            txtBarcodeCount.text = "0"
        }

        btnScanBarcode.setOnClickListener {
            findNavController().navigate(R.id.action_acrivityDetailFragment_to_barcodeFragment)
        }

        btnTakePicture.setOnClickListener {
            CSP.saveData("fragName", "ActivityDetail")
            val bundle = bundleOf("fragName" to "ActivityDetailFragment")
            findNavController().navigate(R.id.action_acrivityDetailFragment_to_cameraActivity, bundle)
        }

        btnSubmit.setOnClickListener {
            val client = OkHttpClient()

            try {

                val builder: MultipartBody.Builder  =  MultipartBody.Builder().setType(MultipartBody.FORM)

                builder.addFormDataPart("SerialNumbers", CSP.getData("activity_barcodes").toString())

                if(!CSP.getData("ActivityDetail_SESSION_IMAGE_SET").equals("")){
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
                }

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
                        }&ActivityTypeID=${arguments?.getInt("ActivityTypeID")}&ActivityCategoryID=${arguments?.getInt("ActivityCategoryID")}&StoreID=${arguments?.getInt("StoreID")}&BrandID=1&ActivityDescription=${txtBrandDescription.text}&StatusID=1&Quantity=${txtBrandQuantity.text}"
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
        println(CSP.getData("ActivityDetail_SESSION_IMAGE_SET"))
        if(!CSP.getData("ActivityDetail_SESSION_IMAGE").equals("")){
            Sneaker.with(requireActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Success!!")
                .setMessage("Image Added to this session!")
                .sneakSuccess()

            if (CSP.getData("ActivityDetail_SESSION_IMAGE_SET").equals("")) {
                recylcerAdapter.addNewItem(CSP.getData("ActivityDetail_SESSION_IMAGE"))
                CSP.saveData("ActivityDetail_SESSION_IMAGE_SET", CSP.getData("ActivityDetail_SESSION_IMAGE"))
                CSP.delData("ActivityDetail_SESSION_IMAGE")
            } else {
                recylcerAdapter.addNewItem(CSP.getData("ActivityDetail_SESSION_IMAGE"))
                CSP.saveData(
                    "ActivityDetail_SESSION_IMAGE_SET",
                    "${CSP.getData("ActivityDetail_SESSION_IMAGE_SET")},${CSP.getData("ActivityDetail_SESSION_IMAGE")}"
                )
                CSP.delData("ActivityDetail_SESSION_IMAGE")
            }
        }
    }

}