package com.example.cheilros.fragments.storeview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.ActivityAdapter
import com.example.cheilros.adapters.TrainingAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.ActivityTypeModel
import com.example.cheilros.models.TrainingModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.activity_new_dashboard.*
import kotlinx.android.synthetic.main.fragment_activity.*
import kotlinx.android.synthetic.main.fragment_checklist_category.*
import kotlinx.android.synthetic.main.fragment_checklist_category.mainLoadingLayoutCC
import kotlinx.android.synthetic.main.fragment_checklist_category.view.*
import kotlinx.android.synthetic.main.fragment_training.*
import kotlinx.android.synthetic.main.list_checklist.*
import okhttp3.*
import java.io.IOException

class TrainingFragment : BaseFragment() {

    private val client = OkHttpClient()

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: TrainingAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =   inflater.inflate(R.layout.fragment_training, container, false)

        arguments?.getString("StoreName")?.let { configureToolbar(it, true, true) }

        view.mainLoadingLayoutCC.setState(LoadingLayout.LOADING)

        //region Set Labels
        view.txtStoreName.text = settingData.filter { it.fixedLabelName == "StoreMenu_Training" }.get(0).labelName
        //endregion

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*LLchecklist.setOnClickListener {
            findNavController().navigate(R.id.action_trainingFragment_to_trainingDetailFragment)
        }*/
        fetchTraining("${CSP.getData("base_url")}/Training.asmx/TrainingModelsList")

        requireActivity().toolbar_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(qString: String): Boolean {
                recylcerAdapter?.filter?.filter(qString)
                return true
            }
            override fun onQueryTextSubmit(qString: String): Boolean {

                return true
            }
        })
    }

    fun fetchTraining(url: String){
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
                val apiData = gson.fromJson(body, TrainingModel::class.java)

                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvTraining.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvTraining.layoutManager = layoutManager
                        recylcerAdapter = TrainingAdapter(requireContext(), apiData.data, arguments)
                        rvTraining.adapter = recylcerAdapter
                        mainLoadingLayoutCC.setState(LoadingLayout.COMPLETE)
                    })
                }else{
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