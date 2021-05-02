package com.example.cheilros. fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import at.markushi.ui.CircleButton
import com.example.cheilros.R
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.fragment_base_url.*
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.*
import org.json.JSONObject
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
        btnLogin.setOnClickListener {
            Log.i(TAG, "btnLogin — clicked")
            //run("http://rosturkey.cheildata.com/Webservice.asmx/ROSAppUserLogin?Username=test.ros1&Password=ros&DeviceIMEIID=test.ros1")
            fetchData("http://rosturkey.cheildata.com/Webservice.asmx/ROSAppUserLogin?Username=${etUsername.text.toString().trim()}&Password=${etPassword.text.toString().trim()}&DeviceIMEIID=test.ros1")

            /*if(!etUsername.text.toString().trim().isEmpty() && !etUsername.text.toString().trim().isEmpty()){
                fetchData("http://rosturkey.cheildata.com/Webservice.asmx/ROSAppUserLogin?Username=test.ros1&Password=ros&DeviceIMEIID=test.ros1")
            }else{
                activity?.let { it1 ->
                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Warning!!")
                            .setMessage("Please Fill all Field!")
                            .sneakWarning()
                }
            }*/


        }

        btnForgot.setOnClickListener {
            Log.i(TAG, "btnLogin — clicked")
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
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
                val userData = gson.fromJson(body, LoginModel::class.java)
                println(userData.status)
                if(userData.status == 200){
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Welcome User!")
                                    .sneakSuccess()
                        }
                        findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity2)
                    })
                }else{
                    requireActivity().runOnUiThread(java.lang.Runnable {
                        activity?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Invalid!!")
                                    .setMessage("Your Credentials are wrong.")
                                    .sneakWarning()
                        }
                    })
                }
            }
        })
    }

    /*fun run(url: String) {
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
                println(response.body?.string())
//                val gson = GsonBuilder().create()
//                val userData = gson.fromJson(body, LoginModel::class.java)

                //println(body)
                //println(response.isSuccessful)
               *//* requireActivity().runOnUiThread(java.lang.Runnable {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        val gson = GsonBuilder().create()
                        val userData = gson.fromJson(body, Array<LoginModel>::class.java).toList()

                        //println(userData.get(0).status)


                        //findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity2)
                        //val jsonObject = JSONObject(response.body?.string())
                        //val jsonArray =jsonObject.getJSONArray("data");
                        //println(jsonArray.length().toString())
                    }
                })*//*


            }
        })
    }*/
}


class LoginFeed(val logindata: List<LoginModel>)
class LoginModel(val status: Int, val data : List<LoginData>)
class LoginData(val TeamMemberID: Int,val TeamTypeID: Int,val mySingleID: String,val Email: String,val DivisionID: Int,val DivisionName: String)