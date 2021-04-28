package com.example.cheilros.fragments

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cheilros.MainActivity
import com.example.cheilros.R
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_base_url.*
import okhttp3.*
import java.io.IOException
import java.util.*


class BaseUrlFragment : Fragment() {

    private val client = OkHttpClient()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_base_url, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val items = listOf("Turkish", "English")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        //var etLang = view.findViewById<AutoCompleteTextView>(R.id.etLanguage)
        (etLanguage as? AutoCompleteTextView)?.setAdapter(adapter)

        etLanguage.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->

        }

        btnSave.setOnClickListener {
           Log.i(Companion.TAG, "btnSave — clicked")
           fetchData("${etBaseUrl.text.toString()}/Webservice.asmx/AppSettings?LanguageID=2")
        }


    }

    companion object {
        private const val TAG = "BaseUrlFragment"
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
                println(response.body?.string())
                requireActivity().runOnUiThread(java.lang.Runnable {
                    activity?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("Setting Added Successfully")
                                .sneakSuccess()
                    }
                    findNavController().navigate(R.id.action_baseUrlFragment_to_loginFragment)
                })
            }
        })
    }
}
