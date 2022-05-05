package com.example.cheilros.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cheilros.BuildConfig
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.data.UserData
import com.example.cheilros.data.UserPermission
import com.example.cheilros.models.CheckInOutModel
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
import java.util.concurrent.Executor
import android.content.pm.PackageManager
import android.provider.Settings
import com.example.cheilros.globals.gConstants
import java.util.concurrent.TimeUnit


class LoginFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    var userIMEI: String = ""

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        if (BuildConfig.VERSION_NAME != CSP.getData("Version") && CSP.getData("Version") != "") {
            view.txtWarning.text = "Please install new version v${CSP.getData("Version")}"
            view.txtWarning.visibility = View.VISIBLE
        } else {
            view.txtWarning.visibility = View.GONE
        }


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

            /* val telephonyManager =
                 requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
             println("IMEI: ${telephonyManager.imei}")*/

            userIMEI = getIMEIDeviceId(requireContext()).toString()
            println("IMEI: ${userIMEI}")
        } catch (ex: Exception) {
            Log.e("Error_", ex.message.toString())
        }


        try {
            Glide.with(this).load("${CSP.getData("base_url")}/AppImages/Background.jpg")
                .into(object :
                    CustomTarget<Drawable>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        view.CLlogin.background = resource
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
                    view.imgLogoLogin.background = resource
                }
            })
        } catch (ex: Exception) {
            Log.e("Error_", "CLlogin: ${ex.message.toString()}")
        }

        return view
    }

    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //region Biometric Auth
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Toast.makeText(
                    requireContext(),
                    "App can authenticate using biometrics.",
                    Toast.LENGTH_SHORT
                ).show()
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(
                    requireContext(),
                    "No biometric features available on this device.",
                    Toast.LENGTH_SHORT
                ).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(
                    requireContext(),
                    "Biometric features are currently unavailable.",
                    Toast.LENGTH_SHORT
                ).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(
                    requireContext(),
                    "The device does not have any biometric credentials",
                    Toast.LENGTH_SHORT
                ).show()
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                Toast.makeText(
                    requireContext(),
                    "A security vulnerability has been discovered and the sensor is unavailable until a security update has addressed this issue.",
                    Toast.LENGTH_SHORT
                ).show()
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                Toast.makeText(
                    requireContext(),
                    "A given authenticator combination is not supported by the device.",
                    Toast.LENGTH_SHORT
                ).show()
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                Toast.makeText(
                    requireContext(),
                    "Unable to determine whether the user can authenticate.",
                    Toast.LENGTH_SHORT
                ).show()
        }

        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), errString.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val request = Request.Builder()
                        .url("${CSP.getData("base_url")}/Webservice.asmx/LoginWithDeviceIMEIID?DeviceIMEIID=$userIMEI")
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage(e.message.toString())
                                        .sneakWarning()
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
                                        data.RegionName,
                                        data.MarketType
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

                    Toast.makeText(requireContext(), "Authentication Success", Toast.LENGTH_SHORT)
                        .show()

                    //startActivity(Intent(this@MainActivity,SecondActivity::class.java))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        btnFingerAuth.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
        //endregion

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
        println(url)
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
                            data.RegionName,
                            data.MarketType
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

    fun getIMEIDeviceId(context: Context): String? {
        try {
            val deviceId: String
            deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            } else {
                val mTelephony =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return ""
                    }
                }
                assert(mTelephony != null)
                if (mTelephony.deviceId != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mTelephony.imei
                    } else {
                        mTelephony.deviceId
                    }
                } else {
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                }
            }
            Log.d("deviceId", deviceId)
            return deviceId
        } catch (ex: Exception) {
            return ""
        }
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
    val RegionName: String,
    val MarketType: Int
)