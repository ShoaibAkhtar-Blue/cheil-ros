package com.example.cheilros.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.example.cheilros.R
import com.example.cheilros.adapters.JourneyPlanAdapter
import com.example.cheilros.models.JourneyPlanModel
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import java.util.*


class JourneyPlanFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journey_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // prepare elements to display
        val items: ArrayList<JourneyPlanModel> = JourneyPlanModel.testingList

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        val adapter = JourneyPlanAdapter(requireContext(), items)

        // set elements to adapter
        rvJourneyPlan!!.adapter = adapter


        // set on click event listener to list view
        rvJourneyPlan.onItemClickListener = OnItemClickListener { adapterView, view, pos, l -> // toggle clicked cell state
            Log.i("FoldingCell", "clicked")
            (view as FoldingCell).toggle(false)
            // register in adapter that state for selected cell is toggled
            adapter.registerToggle(pos)
        }
    }

    companion object {

    }
}