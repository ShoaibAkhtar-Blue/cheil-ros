package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.DisplayCountDetailViewModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.dialog_barcode.*
import okhttp3.*
import java.io.IOException

class BarcodeAdapter(
    val context: Context,
    val barcodeList: MutableList<String>,
    val mainDialog: Dialog,
    val isRemoveActive: Boolean,
    val recylcerAdapter: DisplayCountDetailAdapter?,
    val productID: Int?,
    val arguments: Bundle?,
    val isRemoveOnline: Boolean = false
) : RecyclerView.Adapter<BarcodeAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtType: TextView = view.findViewById(R.id.txtType)
        var btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_barcode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        var serialNum = ""

        if (barcodeList[position].contains("_")) {
            var splitText = barcodeList[position].split("_")
            println("barcodeList: ${barcodeList[position]}")
            serialNum = splitText[0]
            println("serialNum: $serialNum ${splitText[2]}")

            if(splitText[2] != "")
                holder.txtType.text = "${splitText[2]}"
            else
                holder.txtType.visibility = View.GONE

                holder.txtTitle.text = serialNum
        } else {
            serialNum = barcodeList[position]
            holder.txtTitle.text = serialNum

            holder.txtType.visibility = View.GONE
        }

        if (!isRemoveActive)
            holder.btnRemove.visibility = View.GONE

        holder.btnRemove.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Remove Barcode...")
            builder.setMessage("Are you Sure?")
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton("Yes") { dialog, which ->

                if (!isRemoveOnline) {
                    barcodeList.removeAt(position)
                    notifyDataSetChanged()

                    println(productID)

                    productID?.let { it1 -> recylcerAdapter?.updateItem(it1, true) }

                    CSP.saveData("ActivityDetail_BARCODE_SET", barcodeList.joinToString(","))

                    if (barcodeList.size == 0)
                        mainDialog.dismiss()
                } else {
                    var url =
                        "${CSP.getData("base_url")}/DisplayCount.asmx/RemoveDisplayModel?ProductID=${productID}&StoreID=${
                            arguments?.getInt(
                                "StoreID"
                            )
                        }&SerialNumber=${serialNum}"
                    println(url)

                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url(url)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val body = response.body?.string()
                            println(body)
                            val gson = GsonBuilder().create()
                            val apiData =
                                gson.fromJson(body, CheckInOutModel::class.java)
                            if (apiData.status == 200) {
                                (context as Activity).runOnUiThread {
                                    barcodeList.removeAt(position)
                                    notifyDataSetChanged()

                                    println(productID)
                                    println("barcodeList:"+barcodeList.size)

                                    if (barcodeList.size == 0)
                                        mainDialog.dismiss()

                                    productID?.let { it1 -> recylcerAdapter?.updateItem(it1, true) }


                                }
                            } else {

                            }
                        }
                    })
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return barcodeList.size
    }
}