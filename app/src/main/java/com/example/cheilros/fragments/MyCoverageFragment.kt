package com.example.cheilros.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.data.UserData
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.MyCoverageModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import okhttp3.*
import java.io.IOException


class MyCoverageFragment : Fragment() {

    private val client = OkHttpClient()

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var mAppSettingViewModel: AppSettingViewModel
    private lateinit var mUserDataViewModel: UserDataViewModel
    private lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var CSP: CustomSharedPref

    lateinit var userData:List<UserData>

    lateinit var recylcerAdapter: MyCoverageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_coverage, container, false)

        //Init DB VM
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)
        mUserDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        mUserPermissionViewModel = ViewModelProvider(this).get(UserPermissionViewModel::class.java)

        CSP = CustomSharedPref(requireContext())

        userData = mUserDataViewModel.getAllUser

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        recyclerView= rvCoverage
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        layoutManager= LinearLayoutManager(requireContext())
        recyclerView.layoutManager=layoutManager

        fetchData("${CSP.getData("base_url")}/Storelist.asmx/TeamMemberStoreList?TeamMemberID=${userData[0].memberID}&ChannelID=1&SearchKeyWord=")
    }

    companion object {

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
                val apiData = gson.fromJson(body, MyCoverageModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        recylcerAdapter = MyCoverageAdapter(requireContext(), apiData.data)
                        recyclerView.adapter = recylcerAdapter
                    })
                } else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                    })
                }
            }
        })
    }
}

