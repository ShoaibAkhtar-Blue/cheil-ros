package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_acrivity_detail.*
import kotlinx.android.synthetic.main.fragment_acrivity_detail.txtStoreSubName
import kotlinx.android.synthetic.main.fragment_activity_sub_category.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException

class AcrivityDetailFragment : Fragment() {

    lateinit var CSP: CustomSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acrivity_detail, container, false)

        CSP = CustomSharedPref(requireContext())

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtStoreSubName.text = arguments?.getString("ActivityCategoryName")

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
            //val bundle = bundleOf("fragName" to "ActivityDetailFragment")
            //findNavController().navigate(R.id.action_acrivityDetailFragment_to_cameraFragment, bundle)
        }

        btnSubmit.setOnClickListener {
            val client = OkHttpClient()

            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("SerialNumbers", "1,2,3,4")
                        .build()

                val request: Request = Request.Builder()
                    .url(
                        "${CSP.getData("base_url")}/OperMarketActivities.asmx/MarketActivityDetails?TeamMemberID=${
                            CSP.getData(
                                "user_id"
                            )
                        }&ActivityTypeID=1&ActivityCategoryID=1&StoreID=1&BrandID=1&ActivityDescription=${txtBrandDescription.text}&StatusID=1&Quantity=1"
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

                            findNavController().navigateUp()
                        }


                    }

                })

            } catch (ex: Exception) {

            }

        }

    }

}