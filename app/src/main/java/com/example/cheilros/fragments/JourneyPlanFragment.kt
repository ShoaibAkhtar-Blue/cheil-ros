package com.example.cheilros.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.JPAdapter
import kotlinx.android.synthetic.main.fragment_journey_plan.*


class JourneyPlanFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    val booklist=arrayListOf(
            "P.S I Love You",
            "The Great Gatsby",
            "Anna Karenina",
            "Madame Bovary",
            "War and Peace",
            "Loyalty",
            "Middle March",
            "The Adventures",
            "Mona Dick",
            "The Lord Of Rings"
    )
    lateinit var recylcerAdapter: JPAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journey_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView= rvJourneyPlan
        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))


        layoutManager= LinearLayoutManager(requireContext())

        recylcerAdapter= JPAdapter(requireContext(), booklist)

        recyclerView.adapter=recylcerAdapter
        recyclerView.layoutManager=layoutManager
    }

    companion object {

    }
}