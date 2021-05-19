package com.example.cheilros.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import at.markushi.ui.CircleButton
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.LoginUserPermission
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_base_url.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private val client = OkHttpClient()
    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var CSP: CustomSharedPref

    var userIMEI : String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //Init DB VM
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)
        mUserDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        mUserPermissionViewModel = ViewModelProvider(this).get(UserPermissionViewModel::class.java)

        CSP = CustomSharedPref(requireContext())

        //Remove User Data & Permissions
        mUserDataViewModel.nukeTable()
        mUserPermissionViewModel.nukeTable()

        val settingData: List<AppSetting> = mAppSettingViewModel.getAllSetting
        println(settingData)

        //Set Labels
        try {
            view.txtCopyright.text =
                settingData.filter { it.fixedLabelName == "CopyRight" }.get(0).labelName
            view.txtAppName.text =
                settingData.filter { it.fixedLabelName == "Login_Title" }?.get(0).labelName
            view.OTFUsername.hint =
                settingData.filter { it.fixedLabelName == "Login_UserName" }?.get(0).labelName
            view.OTFPassword.hint =
                settingData.filter { it.fixedLabelName == "Login_Password" }?.get(0).labelName
            view.btnForgot.hint =
                settingData.filter { it.fixedLabelName == "Login_ForgetPassword" }?.get(0).labelName
        } catch (ex: Exception) {

        }

        try {
            val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            println("IMEI: ${telephonyManager.imei}")
            userIMEI = telephonyManager.imei
        }catch (ex: Exception){
            Log.e("Error_", ex.message.toString())
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                            ""
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
                        findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity2)
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
    val DivisionName: String
)