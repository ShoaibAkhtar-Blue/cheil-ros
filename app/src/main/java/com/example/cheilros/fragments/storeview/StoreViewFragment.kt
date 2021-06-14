package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.cheilros.R
import com.example.cheilros.adapters.StoreMenuAdapter
import com.example.cheilros.data.AppSetting
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_store_view.*


class StoreViewFragment : BaseFragment() {

    lateinit var storemenuAdapter: StoreMenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_store_view, container, false)

        configureToolbar("${arguments?.getString("StoreName")}", true)

        //Init DB VM
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)

        settingData = mAppSettingViewModel.getAllSetting

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //var menuDataList: List<AppSetting> = settingData.filter { it.screenName == "StoreView" }
        storemenuAdapter = StoreMenuAdapter(requireContext(), settingData.filter { it.screenName == "StoreView" }, arguments)

        rvStoreMenu!!.adapter = storemenuAdapter
    }

    companion object {

    }
}