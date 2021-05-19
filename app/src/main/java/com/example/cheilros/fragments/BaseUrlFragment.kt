package com.example.cheilros.fragments

import android.content.SharedPreferences
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
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.AppSettingModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_base_url.*
import okhttp3.*
import java.io.IOException
import java.util.*


class BaseUrlFragment : Fragment() {

    private val client = OkHttpClient()
    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var CSP: CustomSharedPref

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_base_url, container, false)

        //Init DB VM
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)

        CSP = CustomSharedPref(requireContext())


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val items = listOf("Turkish", "English")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        //var etLang = view.findViewById<AutoCompleteTextView>(R.id.etLanguage)
        (etLanguage as? AutoCompleteTextView)?.setAdapter(adapter)

        etLanguage.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            Log.i("etLanguage", "${etLanguage.text}-${position.toString()}")
        }


        btnSave.setOnClickListener {
           Log.i(Companion.TAG, "btnSave — clicked")
            CSP.saveData("base_url", etBaseUrl.text.toString())
            fetchData("${etBaseUrl.text.toString()}/Webservice.asmx/AppSettings?LanguageID=2")
        }


    }

    companion object {
        private const val TAG = "BaseUrlFragment"
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
                val apiData = gson.fromJson(body, AppSettingModel::class.java)

                try {
                    if (apiData.status == 200) {
                        for (data in apiData.data) {
                            println(data.ROS_Screen)
                            //Save Setting to DB
                            //mAppSettingViewModel.nukeTable()

                            val setting = AppSetting(0, data.ROS_LabelID, data.ROS_Screen, data.ROS_LabelName, data.LanguageID, data.ImageLocation, data.FixedLabelName)
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
                    }else{

                    }
                }catch (ex: Exception){

                }




            }
        })
    }


}



