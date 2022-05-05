package com.example.cheilros.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.AppLanguagesData
import com.example.cheilros.models.AppLanguagesModel
import com.example.cheilros.models.AppSettingModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_base_url.*
import kotlinx.android.synthetic.main.fragment_base_url.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class BaseUrlFragment : Fragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var CSP: CustomSharedPref

    var isLanguageVisible = false

    lateinit var languagesData: List<AppLanguagesData>

    var defaultLanguageID = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_base_url, container, false)

        //Init DB VM
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)

        CSP = CustomSharedPref(requireContext())

        //Save Setting to DB
        mAppSettingViewModel.nukeTable()

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
                        view.CLbaseurl.background = resource
                    }
                })

            /*Glide.with(this).load("${CSP.getData("base_url")}/AppImages/ROS_Logo.png").into(object :
                CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.imgLogo.background=resource
                }
            })*/
        } catch (ex: Exception) {
            Log.e("Error_", "CLlogin: ${ex.message.toString()}")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etBaseUrl.setText(CSP.getData("base_url").toString())

        btnLanguage.visibility = View.GONE

        btnLanguage.setOnClickListener {
            // setup the alert builder
            // setup the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setTitle("")

            // add a list

            // add a list
            var channels: Array<String> = arrayOf()
            for (c in languagesData) {
                channels += c.Language
            }

            builder.setItems(channels,
                DialogInterface.OnClickListener { dialog, which ->
                    println(languagesData[which].LanguageID)
                    defaultLanguageID = languagesData[which].LanguageID.toString()
                    btnLanguage.text = "${languagesData[which].Language}"
                })

            // create and show the alert dialog

            // create and show the alert dialog
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        /*val items = listOf("English", "Korean", "Turkish", "Chinese")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        //var etLang = view.findViewById<AutoCompleteTextView>(R.id.etLanguage)
        (etLanguage as? AutoCompleteTextView)?.setAdapter(adapter)

        etLanguage.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            var lang_id : Int = items.indexOf(etLanguage.text.toString()) + 1
            Log.i("etLanguage", "${etLanguage.text}-${lang_id.toString()}")
            CSP.saveData("lang_id", lang_id.toString())
        }*/


        btnSave.setOnClickListener {
            Log.i(Companion.TAG, "btnSave — clicked ${etLanguage.text}")

            if (etBaseUrl.text!!.isEmpty()) {
                requireActivity().runOnUiThread(java.lang.Runnable {
                    activity?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Warning!!")
                            .setMessage("Please Fill all fields before proceed!")
                            .sneakWarning()
                    }
                })
            } else {
                try {
                    if (isLanguageVisible)
                        fetchData(
                            "${etBaseUrl.text.toString()}/Webservice.asmx/AppSettings?LanguageID=${defaultLanguageID}"
                        )
                    else
                        fetchLanguage("${etBaseUrl.text.toString()}/Webservice.asmx/AppLanguages")
                } catch (ex: Exception) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Invalid URL!")
                                .sneakError()
                        }
                    })
                }

            }
            /*if(etLanguage.text.isEmpty() || etBaseUrl.text!!.isEmpty()){
                requireActivity().runOnUiThread(java.lang.Runnable {
                    activity?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Warning!!")
                            .setMessage("Please Fill all fields before proceed!")
                            .sneakWarning()
                    }
                })
            }else{
                try{
                    fetchData("${etBaseUrl.text.toString()}/Webservice.asmx/AppSettings?LanguageID=${CSP.getData("lang_id")}")
                }catch (ex: Exception){
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Invalid URL!")
                                .sneakError()
                        }
                    })
                }
            }*/
        }
    }

    companion object {
        private const val TAG = "BaseUrlFragment"
    }


    fun fetchData(url: String) {
        if(defaultLanguageID != "0"){
            CSP.delAll()
            mAppSettingViewModel.nukeTable()
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

                    try {
                        val gson = GsonBuilder().create()
                        val apiData = gson.fromJson(body, AppSettingModel::class.java)

                        if (apiData.status == 200) {
                            CSP.saveData("base_url", etBaseUrl.text.toString())
                            for (data in apiData.data) {
                                println(data.ROS_Screen)


                                val setting = AppSetting(
                                    0,
                                    data.ROS_LabelID,
                                    data.ROS_Screen,
                                    data.ROS_LabelName,
                                    data.LanguageID,
                                    data.ImageLocation,
                                    data.FixedLabelName
                                )
                                mAppSettingViewModel.addSettings(setting)
                            }
                            requireActivity().runOnUiThread(java.lang.Runnable {
                                activity?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Success!!")
                                        .setMessage("Setting Added Successfully")
                                        .sneakSuccess()
                                }
                                findNavController().navigate(R.id.action_baseUrlFragment_to_loginFragment)
                            })
                        } else {

                        }
                    } catch (ex: Exception) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Invalid URL")
                                    .sneakError()
                            }
                        })
                    }
                }
            })
        }else{
            requireActivity().runOnUiThread(java.lang.Runnable {
                activity?.let { it1 ->
                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                        .setTitle("Warning!!")
                        .setMessage("Please Select Language")
                        .sneakWarning()
                }
            })
        }

    }

    fun fetchLanguage(url: String) {
        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()

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

                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, AppLanguagesModel::class.java)

                    if (apiData.status == 200) {
                        languagesData = apiData.data

                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Languages Fetched Successfully")
                                    .sneakSuccess()
                            }
                            if (!isLanguageVisible) {
                                btnLanguage.visibility = View.VISIBLE
                                isLanguageVisible = true
                            }
                            //findNavController().navigate(R.id.action_baseUrlFragment_to_loginFragment)
                        })
                    } else {

                    }
                } catch (ex: Exception) {
                    Log.e("Error_", ex.message.toString())
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Invalid URL")
                                .sneakError()
                        }
                    })
                }
            }
        })
    }


}



