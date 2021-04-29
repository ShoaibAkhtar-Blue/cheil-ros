package com.example.cheilros.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cheilros.R
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnJourneyPlan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_journeyPlanFragment)
        }

        btnTest.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_myCoverageFragment)
        }
    }

    companion object {

    }
}