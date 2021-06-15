package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.ChecklistDetailAdapter
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckListDetailData
import com.example.cheilros.models.CheckListDetailModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.txtStoreName
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category_detail.*
import okhttp3.*
import java.io.IOException

class ChecklistCategoryDetailFragment : Fragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: ChecklistDetailAdapter

    lateinit var CSP: CustomSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_checklist_category_detail, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        CSP = CustomSharedPref(requireContext())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtStoreName.text = arguments?.getString("ChecklistName")
        fetchChecklistDetail("${CSP.getData("base_url")}/Audit.asmx/CheckList_Audit?CheckListCategoryID=${arguments?.getInt("ChecklistID").toString()}&StoreID=${arguments?.getInt("StoreID").toString()}")
    }

    companion object {

    }

    fun fetchChecklistDetail(url: String) {

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
                val apiData = gson.fromJson(body, CheckListDetailModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvChecklistDetail.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvChecklistDetail.layoutManager = layoutManager
                        recylcerAdapter =
                            ChecklistDetailAdapter(requireContext(),
                                apiData.data as MutableList<CheckListDetailData>, arguments, this@ChecklistCategoryDetailFragment)
                        rvChecklistDetail.adapter = recylcerAdapter
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                } else {
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