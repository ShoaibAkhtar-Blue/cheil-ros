package com.example.cheilros.fragments

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import kotlinx.android.synthetic.main.activity_new_dashboard.*

open class BaseFragment : Fragment() {


    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        val fragmentLabel = findNavController().currentDestination!!.label
        println(fragmentLabel)
        if(fragmentLabel == "dashboard"){
            configureToolbar("Dashboard")
        }
    }

    @SuppressLint("RestrictedApi")
    fun configureToolbar(title:String, isBackHandle: Boolean = false, isSearchHandle: Boolean = false){
        println("configureToolbar")
        var activity = requireActivity()
        activity.toolbar_title.text = title


        if(isBackHandle){
            activity.btnLeftMenu.visibility = View.VISIBLE
            activity.btnLeftMenu.setImageResource(R.drawable.back)
            activity.btnLeftMenu.setOnClickListener {
                findNavController().popBackStack()
            }
        }else{
            activity.btnLeftMenu.visibility = View.INVISIBLE
            activity.btnRightMenu.visibility = View.INVISIBLE
            activity.toolbar_search.visibility = View.GONE
        }

        if(isSearchHandle){
            activity.toolbar_search.visibility = View.VISIBLE

            activity.toolbar_search.setOnSearchClickListener {
                println("search")
            }
        }else{
            activity.btnRightMenu.visibility = View.INVISIBLE
            activity.toolbar_search.visibility = View.GONE
        }

    }
}