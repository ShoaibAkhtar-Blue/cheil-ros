package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.ActivitySubCategoryAdapter
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_activity_sub_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import okhttp3.*
import java.io.IOException


class ActivitySubCategoryFragment : Fragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: ActivitySubCategoryAdapter

    lateinit var CSP: CustomSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_activity_sub_category, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        CSP = CustomSharedPref(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchActivtyDetail("${CSP.getData("base_url")}/Audit.asmx/CheckList?StoreID=1")
    }

    fun fetchActivtyDetail(url: String){
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
                    mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, CheckListModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvActivityDetail.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvActivityDetail.layoutManager = layoutManager
                        recylcerAdapter = ActivitySubCategoryAdapter(requireContext(), apiData.data, arguments)
                        rvActivityDetail.adapter = recylcerAdapter
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }
            }
        })
    }
}