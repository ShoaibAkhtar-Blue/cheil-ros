package com.example.cheilros.fragments.storeview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.ChecklistAnsweredAdapter
import com.example.cheilros.adapters.InvestmentAdapter
import com.example.cheilros.adapters.InvestmentAnswerAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.models.CheckListAnswerModel
import com.example.cheilros.models.InvestmentAnswerModel
import com.example.cheilros.models.InvestmentModel
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_store_status.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class StoreStatusFragment(val StoreID: Int?, val StoreName: String?) : BaseFragment() {

    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recylcerAdapter: ChecklistAnsweredAdapter

    lateinit var layoutManagerInvest: RecyclerView.LayoutManager
    lateinit var recylcerAdapterInvest: InvestmentAnswerAdapter

    lateinit var layoutManagerInvest1: RecyclerView.LayoutManager
    lateinit var recylcerAdapterInvest1: InvestmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated StoreStatusFragment")


        //fetchChecklistanswer("${CSP.getData("base_url")}/Checklist.asmx/ChecklistAnswered?StoreID=$StoreID")
        //fetchInvestmentanswer("${CSP.getData("base_url")}/Storelist.asmx/StoreInvestmentElements?StoreID=${arguments?.getInt("StoreID")}")
        fetchInvestment("${CSP.getData("base_url")}/Audit.asmx/InvestmentElement_AuditView?StoreID=$StoreID&TeamMemberID=${CSP.getData("user_id")}")


        /*btnEditChecklist.setOnClickListener {
            val bundle = bundleOf(
                "StoreID" to StoreID,
                "StoreName" to StoreName
            )
            findNavController().navigate(R.id.action_storeViewFragment_to_checklistCategoryFragment,bundle)
        }*/
        /*btnEditInvestment.setOnClickListener {
            val bundle = bundleOf(
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )
            findNavController().navigate(R.id.action_storeViewFragment_to_investmentFragment,bundle)
        }*/
    }

    public fun refresh() {

    }


    override fun onResume() {
        super.onResume()
        /*println("onResume StoreStatusFragment")
        fetchChecklistanswer("${CSP.getData("base_url")}/Checklist.asmx/ChecklistAnswered?StoreID=$StoreID")
        //fetchInvestmentanswer("${CSP.getData("base_url")}/Storelist.asmx/StoreInvestmentElements?StoreID=${arguments?.getInt("StoreID")}")
        fetchInvestment("${CSP.getData("base_url")}/Audit.asmx/InvestmentElement_AuditView?StoreID=$StoreID")*/
    }

    /*fun fetchChecklistanswer(url: String){

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

                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, CheckListAnswerModel::class.java)
                if (apiData.status == 200) {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        rvChecklistAnswer.setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        rvChecklistAnswer.layoutManager = layoutManager
                        recylcerAdapter = ChecklistAnsweredAdapter(requireContext(), apiData.data)
                        rvChecklistAnswer.adapter = recylcerAdapter
                        val emptyView: View = todo_list_empty_view1
                        rvChecklistAnswer.setEmptyView(emptyView)
                    })
                }else {
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }

                    })
                }
            }
        })
    }*/

    fun fetchInvestmentanswer(url: String) {

        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()

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

                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, InvestmentAnswerModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvInvestmentAnswer.setHasFixedSize(true)
                            layoutManagerInvest = LinearLayoutManager(requireContext())
                            rvInvestmentAnswer.layoutManager = layoutManagerInvest
                            recylcerAdapterInvest =
                                InvestmentAnswerAdapter(requireContext(), apiData.data)
                            rvInvestmentAnswer.adapter = recylcerAdapterInvest
                            val emptyView: View = todo_list_empty_view2
                            rvInvestmentAnswer.setEmptyView(emptyView)
                        })
                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                            }

                        })
                    }
                } catch (ex: Exception) {
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

    fun fetchInvestment(url: String) {
        println(url)
        //val client = OkHttpClient()
        //NIK: 2022-03-22
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()

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
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)
                try {
                    val gson = GsonBuilder().create()
                    val apiData = gson.fromJson(body, InvestmentModel::class.java)
                    if (apiData.status == 200) {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            rvInvestmentAnswer.setHasFixedSize(true)
                            layoutManagerInvest1 = LinearLayoutManager(requireContext())
                            rvInvestmentAnswer.layoutManager = layoutManagerInvest1
                            recylcerAdapterInvest1 =
                                InvestmentAdapter(requireContext(), apiData.data, StoreID)
                            rvInvestmentAnswer.adapter = recylcerAdapterInvest1
                            val emptyView: View = todo_list_empty_view2
                            rvInvestmentAnswer.setEmptyView(emptyView)
                        })
                    } else {
                        requireActivity().runOnUiThread(java.lang.Runnable {
                            activity?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage("Data not fetched.")
                                    .sneakWarning()
                            }
                        })
                    }
                } catch (ex: Exception) {
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