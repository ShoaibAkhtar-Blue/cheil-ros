package com.example.cheilros.fragments

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import com.example.cheilros.helpers.CoreHelperMethods
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class DashboardFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /*val client = OkHttpClient()
        val sourceFile = File("/storage/emulated/0/Pictures/1621837639994.jpg")
        val mimeType = CoreHelperMethods(requireActivity()).getMimeType(sourceFile)
        val fileName: String = sourceFile.name

        try {
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("testfile", fileName,sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull()))
                    .build()

            val request: Request = Request.Builder()
                .url("http://rosturkey.cheildata.com/Test.asmx/BESURETest")
                .post(requestBody)
                .build()

            val response: Response = client.newCall(request).execute()

            println(response.body!!.string())

        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("File upload", "failed")
        }*/


        btnCamera.setOnClickListener {
            findNavController().navigate(R.id.cameraActivity)
        }

        btnJourneyPlan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
        }

        btnTest.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
        }
    }

    companion object {

    }
}