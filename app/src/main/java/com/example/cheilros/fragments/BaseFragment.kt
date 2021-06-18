package com.example.cheilros.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.data.UserData
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserDataViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import kotlinx.android.synthetic.main.activity_new_dashboard.*


open class BaseFragment : Fragment() {

    lateinit var mAppSettingViewModel: AppSettingViewModel
    lateinit var mUserDataViewModel: UserDataViewModel
    lateinit var mUserPermissionViewModel: UserPermissionViewModel

    lateinit var userData: List<UserData>
    lateinit var settingData: List<AppSetting>

    lateinit var CSP: CustomSharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAppSettingViewModel = ViewModelProvider(this).get(AppSettingViewModel::class.java)
        mUserDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        mUserPermissionViewModel = ViewModelProvider(this).get(UserPermissionViewModel::class.java)

        settingData = mAppSettingViewModel.getAllSetting
        userData = mUserDataViewModel.getAllUser

        CSP = CustomSharedPref(requireContext())

    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentLabel = findNavController().currentDestination!!.label
        val fragmentID = findNavController().currentDestination!!.id
        println("label: ${fragmentID}")
        if (fragmentLabel == "dashboard") {
            configureToolbar(settingData.filter { it.fixedLabelName == "Dashbord_Title" }
                .get(0).labelName)
        } else if (fragmentLabel == "journeyplan") {
            configureToolbar(settingData.filter { it.fixedLabelName == "JourneyPlan_Title" }
                .get(0).labelName, true)
        } else if (fragmentLabel == "mycoverage") {
            configureToolbar(settingData.filter { it.fixedLabelName == "StoreList_Title" }
                .get(0).labelName, true, true)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()
        val fragmentLabel = findNavController().currentDestination!!.label

        println(fragmentLabel)
        if (fragmentLabel == "dashboard") {
            configureToolbar(settingData.filter { it.fixedLabelName == "Dashbord_Title" }
                .get(0).labelName)
        } else if (fragmentLabel == "journeyplan") {
            configureToolbar(settingData.filter { it.fixedLabelName == "JourneyPlan_Title" }
                .get(0).labelName, true)
        } else if (fragmentLabel == "mycoverage") {
            configureToolbar(settingData.filter { it.fixedLabelName == "StoreList_Title" }
                .get(0).labelName, true, true)
        } else if (fragmentLabel == "activity_detail") {
            arguments?.getString("StoreName")?.let { configureToolbar(it, true) }
        } else if (fragmentLabel == "training" || fragmentLabel == "activity_category" || fragmentLabel == "store_view" || fragmentLabel == "activity") {
            val callback =
                requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
                    findNavController().popBackStack()
                }
        } else if (fragmentLabel == "training_detail") {
            arguments?.getString("StoreName")?.let { configureToolbar(it, true) }

            try {
                val activity: Activity? = requireActivity()
                if (activity != null) {
                    val callback =
                        requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
                            // Handle the back button event
                            println("callback")
                            // setup the alert builder
                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(requireActivity())
                            builder.setTitle("Close Session")
                            builder.setMessage("Are You Sure you want to close current session?")

                            // add the buttons

                            // add the buttons
                            builder.setPositiveButton("Ok") { dialogInterface, which ->
                                findNavController().popBackStack()
                            }

                            builder.setNegativeButton("Cancel", null)

                            // create and show the alert dialog

                            // create and show the alert dialog
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                }
            } catch (ex: Exception) {

            }

        }
    }

    @SuppressLint("RestrictedApi")
    fun configureToolbar(
        title: String,
        isBackHandle: Boolean = false,
        isSearchHandle: Boolean = false
    ) {
        println("configureToolbar")
        var activity = requireActivity()
        activity.toolbar_title.text = title


        if (isBackHandle) {
            activity.btnLeftMenu.visibility = View.VISIBLE
            activity.btnLeftMenu.setImageResource(R.drawable.back)
            activity.btnLeftMenu.setOnClickListener {
                findNavController().popBackStack()
            }
        } else {
            activity.btnLeftMenu.visibility = View.INVISIBLE
            activity.btnRightMenu.visibility = View.INVISIBLE
            activity.toolbar_search.visibility = View.GONE
        }

        if (isSearchHandle) {
            activity.toolbar_search.visibility = View.VISIBLE

            activity.toolbar_search.setOnSearchClickListener {
                println("search")
            }
        } else {
            activity.btnRightMenu.visibility = View.INVISIBLE
            activity.toolbar_search.visibility = View.GONE
        }

    }
}