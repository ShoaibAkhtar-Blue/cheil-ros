package com.example.cheilros.fragments.storeview

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.InvestmentDetailAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.BrandsData
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.txtStoreName
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import kotlinx.android.synthetic.main.fragment_investment_detail.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_investment_detail.view.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class InvestmentDetailFragment : BaseFragment() {

    //private val client = OkHttpClient()
    //NIK: 2022-03-22
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
        .build()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: InvestmentDetailAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment_detail, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        try{
            view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreView_InvesmentSubTitle" }.get(0).labelName + " / ${arguments?.getString("ElementTitle")}"
            view.BrandHeading.text = settingData.filter { it.fixedLabelName == "BrandHeading" }.get(0).labelName
            view.CountHeading.text = settingData.filter { it.fixedLabelName == "CountHeading" }.get(0).labelName
            view.btnSubmit.text = settingData.filter { it.fixedLabelName == "LoginForgetSubmitButton" }.get(0).labelName
        }catch (ex: Exception){
            Log.e("Error_", ex.message.toString())
        }
        //endregion

        println("onCreateView")
        println("BrandsList: ${arguments?.getParcelableArrayList<BrandsData>("BrandsList")?.size}")
        println("BrandsList: ${arguments?.getParcelableArrayList<BrandsData>("BrandsList")?.get(0)?.BrandName}")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        txtElementTotal.text = "TOTAL: ${arguments?.getParcelableArrayList<BrandsData>("BrandsList")?.size}"
        txtElementName.text = "${arguments?.getString("ElementTitle")}"
        arguments?.getParcelableArrayList<BrandsData>("BrandsList")?.let { fetchInvestmentDetail(it) }
    }

    fun fetchInvestmentDetail(brands: ArrayList<BrandsData>){
        rvInvestmentDetail.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        rvInvestmentDetail.layoutManager = layoutManager

        recylcerAdapter = InvestmentDetailAdapter(requireContext(), brands, this, arguments)
        rvInvestmentDetail.adapter = recylcerAdapter
        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
    }

}