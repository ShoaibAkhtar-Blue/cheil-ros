package com.example.cheilros.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.adapters.JPAdapter
import com.example.cheilros.adapters.MyCoverageAdapter
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import kotlinx.android.synthetic.main.fragment_my_coverage.*
import okhttp3.*
import java.io.IOException


class MyCoverageFragment : Fragment() {

    private val client = OkHttpClient()

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
    lateinit var recylcerAdapter: MyCoverageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_coverage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView= rvCoverage
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        layoutManager= LinearLayoutManager(requireContext())
        recyclerView.layoutManager=layoutManager




        fetchData("http://rosturkey.cheildata.com/Storelist.asmx/TeamMemberStoreList?TeamMemberID=1&ChannelID=1&SearchKeyWord=")
    }

    companion object {

    }

    fun fetchData(url: String) {
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
                val apiData = gson.fromJson(body, MyCoverageModel::class.java)
                println(apiData.status)
                if(apiData.status == 200){
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        recylcerAdapter= MyCoverageAdapter(requireContext(), apiData.data)
                        recyclerView.adapter=recylcerAdapter
                    })
                }else{
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
    }
}

//Models
class MyCoverageModel(val status: Int, val data : List<MyCoverageData>)
class MyCoverageData(val StoreID: Int,
                     val StoreCode: String,
                     val StoreName: String,
                     val ChannelID: Int,
                     val ChannelName: String,
                     val RegionName: String,
                     val DistrcitName: String,
                     val MallName: String,
                     val Address: String)