package com.example.cheilros.fragments.training

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cheilros.R
import okhttp3.OkHttpClient
import okhttp3.Request

class TrainingListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_list, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    fun fetchTrainingType(url: String){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()


    }
}