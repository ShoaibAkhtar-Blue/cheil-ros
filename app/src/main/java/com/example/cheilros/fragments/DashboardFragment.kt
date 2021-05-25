package com.example.cheilros.fragments

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CoreHelperMethods
import com.example.cheilros.helpers.CustomSharedPref
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class DashboardFragment : Fragment() {

    lateinit var CSP: CustomSharedPref

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mUserDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        CSP = CustomSharedPref(requireContext())

        requireActivity().title = "Home"

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Are You Sure?")
            builder.setMessage("You want to exit app or logout?")

            // add the buttons

            // add the buttons
            builder.setPositiveButton("Exit App") { dialogInterface, which ->
                requireActivity().finish()
            }
            builder.setNeutralButton("Logout") { dialogInterface, which ->
                CSP.delData("user_id")
                mUserDataViewModel.nukeTable()
//            val navController = findNavController(R.id.main_nav_fragment)
//            navController.navigate(R.id.auth_nav)

                val intent = Intent(requireContext(), MainActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                requireContext().startActivity(intent)

                requireActivity().finish()
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

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
                    .addFormDataPart("CheckInImage", fileName,sourceFile.asRequestBody(mimeType?.toMediaTypeOrNull()))
                    .build()

            val request: Request = Request.Builder()
                .url("http://rosturkey.cheildata.com/Checkin.asmx/CheckInImg?VisitID=9&Longitude=1234&Latitude=4567&Remarks=Test")
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