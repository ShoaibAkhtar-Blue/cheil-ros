package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.StorePicturesFragment
import com.example.cheilros.globals.gConstants
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.GeneralPicturesData
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class StorePicturesAdapter(
    val context: Context?,
    var titles: List<GeneralPicturesData>,
    val fragment: StorePicturesFragment
) :
    RecyclerView.Adapter<StorePicturesAdapter.ViewHolder>() {
    var inflater: LayoutInflater
    lateinit var CSP: CustomSharedPref
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = inflater.inflate(R.layout.list_store_pictures, parent, false)
        CSP = context?.let { CustomSharedPref(it) }!!
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.title.text = titles[position].StorePictureElementName
        holder.desc.text = titles[position].Remarks

        //"${CSP.getData("base_url")}/StoreGeneralPictures/${titles[position].PictureID}.png"
        //"https://images.samsung.com/is/image/samsung/assets/pk/galaxy-a52/pcd/a-category/img_bnn_galaxy_device.png"
        if (context != null) {
            Glide.with(context)
                .load("${CSP.getData("base_url")}/StoreGeneralPictures/${titles[position].PictureName}")
                .into(holder.imgStore)
        }

        val imagePopup = ImagePopup(context)
        imagePopup.windowHeight = 800 // Optional
        imagePopup.windowWidth = 800 // Optional
        imagePopup.backgroundColor = Color.BLACK // Optional
        imagePopup.isFullScreen = true // Optional
        imagePopup.isHideCloseIcon = false // Optional
        imagePopup.isImageOnClickClose = false // Optional
        imagePopup.initiatePopupWithGlide("${CSP.getData("base_url")}/StoreGeneralPictures/${titles[position].PictureName}") // Load Image from Drawable

        holder.imgStore.setOnClickListener {
            imagePopup.viewPopup()
        }

        holder.imgStore.setOnLongClickListener {
            println("setOnLongClickListener")
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Remove Picture...")
            builder.setMessage("Are you Sure?")

            builder.setPositiveButton("Yes") { dialog1, which ->

                val request = Request.Builder()
                    .url("${CSP.getData("base_url")}/Webservice.asmx/RemoveGeneralPicture?StoreID=${titles[position].StoreID}&PictureID=${titles[position].PictureID}")
                    .build()
                //val client = OkHttpClient()
                //NIK: 2022-03-22
                val client: OkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .writeTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .readTimeout(gConstants.gCONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Error!!")
                                    .setMessage(e.message.toString())
                                    .sneakWarning()
                            }
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()
                        val apiData = gson.fromJson(body, CheckInOutModel::class.java)
                        println(apiData.status)
                        if (apiData.status == 200) {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Success!!")
                                        .setMessage("Data Updated")
                                        .sneakSuccess()

                                    fragment.fetchStorePictures(
                                        "${CSP.getData("base_url")}/Webservice.asmx/GeneralPictureVie?StoreID=${
                                            titles[position].StoreID
                                        }&BrandID=0&ElementID=0"
                                    )
                                }
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                context?.let { it1 ->
                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                        .setTitle("Error!!")
                                        .setMessage("Data not Updated.")
                                        .sneakWarning()
                                }
                            }
                        }
                    }
                })
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()

            true
        }

    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.txtTitle)
        var desc: TextView = itemView.findViewById(R.id.txtDesc)
        var imgStore: ImageView = itemView.findViewById(R.id.imgStore)

        init {
            itemView.setOnClickListener { v ->
                /*Toast.makeText(
                    v.context,
                    "Clicked -> $adapterPosition",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        }
    }

    init {
        inflater = LayoutInflater.from(context)
    }
}