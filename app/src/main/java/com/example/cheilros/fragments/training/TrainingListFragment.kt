package com.example.cheilros.fragments.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.adapters.RecentActivityAdapter
import com.example.cheilros.adapters.TrainingTypeAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.RecentActivityData
import com.example.cheilros.models.RecentActivityModel
import com.example.cheilros.models.TrainingTypesData
import com.example.cheilros.models.TrainingTypesModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.valartech.loadinglayout.LoadingLayout
import kotlinx.android.synthetic.main.fragment_activity_sub_category.*
import kotlinx.android.synthetic.main.fragment_training_list.*
import kotlinx.android.synthetic.main.fragment_training_list.view.*
import okhttp3.*
import java.io.IOException

class TrainingListFragment : BaseFragment() {

    lateinit var layoutTrainingList: RecyclerView.LayoutManager
    lateinit var recylcerAdapterTrainingType: TrainingTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_list, container, false)
        toolbarVisibility(false)
        view.mainLoadingLayout.setState(LoadingLayout.LOADING)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            // Handle the back button event
            println("callback")
            findNavController().popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchTrainingType("${CSP.getData("base_url")}/Training.asmx/TrainingTypes")
    }

    fun fetchTrainingType(url: String){
        val client = OkHttpClient()
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
                    mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, TrainingTypesModel::class.java)

                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvTrainingType.setHasFixedSize(true)
                            layoutTrainingList = LinearLayoutManager(requireContext())
                            rvTrainingType.layoutManager = layoutTrainingList
                            recylcerAdapterTrainingType = TrainingTypeAdapter(
                                requireContext(),
                                apiData.data as MutableList<TrainingTypesData>,
                                arguments,
                                requireActivity() as NewDashboardActivity,
                                userData
                            )
                            rvTrainingType.adapter = recylcerAdapterTrainingType

                            mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                            toolbarVisibility(true)
                            (activity as NewDashboardActivity).shouldGoBack = true
                        })
                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                            }
                            mainLoadingLayout.setState(LoadingLayout.COMPLETE)
                        })
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage(ex.message.toString())
                                .sneakError()
                        }
                        findNavController().popBackStack()
                    })
                }
            }

        })

    }
}