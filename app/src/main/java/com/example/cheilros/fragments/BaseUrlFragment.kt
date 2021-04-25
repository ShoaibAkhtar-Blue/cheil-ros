package com.example.cheilros.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import at.markushi.ui.CircleButton
import com.example.cheilros.R

class BaseUrlFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_url, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<CircleButton>(R.id.btnSave).setOnClickListener {
            Log.i(Companion.TAG, "btnSave — clicked")
            findNavController().navigate(R.id.action_baseUrlFragment_to_loginFragment)
        }
    }

    companion object {
        private const val TAG = "BaseUrlFragment"
    }
}