package com.example.cheilros.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cheilros.BuildConfig
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission
import com.example.cheilros.models.LoginUserPermission
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.activity_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_base_url.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException
import java.net.URL

class LoginFragment : BaseFragment() {

    private val client = OkHttpClient()

    var userIMEI : String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)



        //Remove User Data & Permissions
        mUserDataViewModel.nukeTable()
        mUserPermissionViewModel.nukeTable()



        //Set Labels
        try {
            view.txtAppVersion.text = BuildConfig.VERSION_NAME
            view.OTFUsername.hint =
                settingData.filter { it.fixedLabelName == "Login_UserName" }?.get(0).labelName
            view.OTFPassword.hint =
                settingData.filter { it.fixedLabelName == "Login_Password" }?.get(0).labelName
            view.txtCopyright.text =
                settingData.filter { it.fixedLabelName == "CopyRight" }.get(0).labelName
            view.txtAppName.text =
                settingData.filter { it.fixedLabelName == "AppTitle" }?.get(0).labelName
            view.btnLogin.text =
                settingData.filter { it.fixedLabelName == "Login_Title" }?.get(0).labelName
            view.btnForgot.text =
                settingData.filter { it.fixedLabelName == "Login_ForgetPassword" }?.get(0).labelName
        } catch (ex: Exception) {
            Log.e("Error_", ex.message.toString())
        }

        try {
            val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            println("IMEI: ${telephonyManager.imei}")
            userIMEI = telephonyManager.imei
        }catch (ex: Exception){
            Log.e("Error_", ex.message.toString())
        }


        try {
            Glide.with(this).load("${CSP.getData("base_url")}/AppImages/Background.jpg").into(object :
                CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.CLlogin.background=resource
                }
            })

            Glide.with(this).load("${CSP.getData("base_url")}/AppImages/ROS_Logo.png").into(object :
                CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.imgLogoLogin.background=resource
                }
            })
        }catch (ex: Exception){
            Log.e("Error_", "CLlogin: ${ex.message.toString()}")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {





        btnSetting.setOnClickListener {
            findNavController().navigate(R.id.action_global_baseUrlFragment)
        }

        btnLogin.setOnClickListener {
            Log.i(TAG, "btnLogin — clicked")
            //run("http://rosturkey.cheildata.com/Webservice.asmx/ROSAppUserLogin?Username=test.ros1&Password=ros&DeviceIMEIID=test.ros1")
            fetchData(
                "${CSP.getData("base_url")}/Webservice.asmx/ROSAppUserLogin?Username=${
                    etUsername.text.toString().trim()
                }&Password=${etPassword.text.toString().trim()}&DeviceIMEIID=$userIMEI"
            )

            /*if(!etUsername.text.toString().trim().isEmpty() && !etUsername.text.toString().trim().isEmpty()){
                fetchData("http://rosturkey.cheildata.com/Webservice.asmx/ROSAppUserLogin?Username=test.ros1&Password=ros&DeviceIMEIID=test.ros1")
            }else{
                activity?.let { it1 ->
                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Warning!!")
                            .setMessage("Please Fill all Field!")
                            .sneakWarning()
                }
            }*/


        }

        btnForgot.setOnClickListener {
            Log.i(TAG, "btnLogin — clicked")
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

    fun fetchData(url: String) {
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
                val apiData = gson.fromJson(body, LoginModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    println("TeamMemberID: ${apiData.data[0].TeamMemberID}")
                    CSP.saveData("user_id", apiData.data[0].TeamMemberID.toString())
                    CSP.saveData("team_type_id", apiData.data[0].TeamTypeID.toString())
                    for (data in apiData.data) {
                        var userdata = UserData(
                            0,
                            data.TeamMemberID,
                            data.TeamTypeID,
                            data.TeamMemberName,
                            data.mySingleID,
                            data.Email,
                            data.DivisionID,
                            data.DivisionName,
                            "",
                            data.TeamTypeName,
                            data.RegionName
                        )
                        mUserDataViewModel.addUser(userdata)
                    }

                    fetchUserPermission("${CSP.getData("base_url")}/Webservice.asmx/TeamMemberPermissions?TeamMemberID=${apiData.data[0].TeamMemberID}")

                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Invalid!!")
                                .setMessage("Your Credentials are wrong.")
                                .sneakWarning()
                        }
                    })
                }
            }
        })
    }

    fun fetchUserPermission(url: String) {
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
                val apiData = gson.fromJson(body, LoginUserPermission::class.java)
                println(apiData.status)
                if (apiData.status == 200) {

                    for (data in apiData.data) {
                        //Saving Permission in Shared Preference
                        CSP.saveData(data.PermissionName, data.Permission)

                        var userpermission = UserPermission(
                            0,
                            data.PermissionID,
                            data.PermissionName,
                            data.Permission
                        )
                        mUserPermissionViewModel.addPermission(userpermission)
                    }

                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("Welcome User!")
                                .sneakSuccess()
                        }
                        //findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity2)
                        findNavController().navigate(R.id.action_loginFragment_to_newDashboardActivity2)
                        (activity as MainActivity).finish()
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Invalid!!")
                                .setMessage("Your Credentials are wrong.")
                                .sneakWarning()
                        }
                    })
                }
            }
        })
    }
}


//Model
class LoginModel(val status: Int, val data: List<LoginData>)
class LoginData(
    val TeamMemberID: Int,
    val TeamTypeID: Int,
    val TeamMemberName: String,
    val mySingleID: String,
    val Email: String,
    val DivisionID: Int,
    val DivisionName: String,
    val TeamTypeName: String,
    val RegionName: String
)