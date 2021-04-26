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
import okhttp3.*
import java.io.IOException

class LoginFragment : Fragment() {

    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btnLogin).setOnClickListener {
            Log.i(TAG, "btnLogin — clicked")
            run("http://rosturkey.cheildata.com/Webservice.asmx/ROSAppUserLogin?Username=test.ros1&Password=ros&DeviceIMEIID=test.ros1")
        }

        view.findViewById<Button>(R.id.btnForgot).setOnClickListener {
            Log.i(TAG, "btnLogin — clicked")
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) = println(response.body?.string())
        })
    }
}