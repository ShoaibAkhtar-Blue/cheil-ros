package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.InvestmentDetailAdapter
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.BrandsData
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_investment_detail.*
import kotlinx.android.synthetic.main.fragment_investment_detail.mainLoadingLayoutCC
import okhttp3.OkHttpClient

class InvestmentDetailFragment : Fragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: InvestmentDetailAdapter

    lateinit var CSP: CustomSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_investment_detail, container, false)

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        CSP = CustomSharedPref(requireContext())

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