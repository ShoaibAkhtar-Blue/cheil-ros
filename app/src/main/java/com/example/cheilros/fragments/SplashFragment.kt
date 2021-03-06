package com.example.cheilros.fragments

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.helpers.CustomSharedPref
import kotlinx.android.synthetic.main.fragment_base_url.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_login.view.imgLogoLogin
import kotlinx.android.synthetic.main.fragment_splash.view.*
import kotlinx.android.synthetic.main.fragment_splash.view.imgLogo
import java.net.URL


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

        try {
            Glide.with(this).load("${CSP.getData("base_url")}/AppImages/Background.jpg").into(object :
                CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.CLsplash.background=resource
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
        }catch (ex: Exception){
            Log.e("Error_", "CLlogin: ${ex.message.toString()}")
        }

        //region reset session
        CSP.delData("sess_gallery_img")
        //endregion

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
                    //findNavController().navigate(R.id.action_splashFragment_to_dashboardActivity2)
                    findNavController().navigate(R.id.action_splashFragment_to_newDashboardActivity)
                    (activity as MainActivity).finish()
                }
            }

        }, 3000)
    }

    companion object {

    }
}