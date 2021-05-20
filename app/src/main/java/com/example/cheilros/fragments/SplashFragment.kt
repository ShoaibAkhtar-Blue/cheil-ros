package com.example.cheilros.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.helpers.CustomSharedPref


class SplashFragment : Fragment() {

    lateinit var CSP: CustomSharedPref

    private lateinit var mAppSettingViewModel: AppSettingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_splash, container, false)
        CSP = CustomSharedPref(requireContext())
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)

        if(CSP.getData("base_url").isNullOrEmpty())
            mAppSettingViewModel.nukeTable()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Handler(Looper.getMainLooper()).postDelayed({
            Log.i("base_url", "bu-${CSP.getData("base_url").toString()}")
            if(CSP.getData("base_url").isNullOrEmpty()){
                findNavController().navigate(R.id.action_splashFragment_to_baseUrlFragment)
            }else{
                if(CSP.getData("user_id").isNullOrEmpty())
                    findNavController().navigate(R.id.action_splashFragment_to_login_graph)
                else{
                    findNavController().navigate(R.id.action_splashFragment_to_dashboardActivity2)
                    (activity as MainActivity).finish()
                }
            }

        }, 3000)
    }

    companion object {

    }
}