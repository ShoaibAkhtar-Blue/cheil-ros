package com.example.cheilros.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import com.example.cheilros.datavm.AppSettingViewModel


class SplashFragment : Fragment() {

    private lateinit var mAppSettingViewModel: AppSettingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_splash, container, false)
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)
        mAppSettingViewModel.nukeTable()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_baseUrlFragment)
        }, 3000)
    }

    companion object {

    }
}