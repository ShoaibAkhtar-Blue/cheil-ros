package com.example.cheilros.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref

class BarcodeAdapter(val context: Context, val barcodeList: MutableList<String>, val dialog: Dialog, val isRemoveActive: Boolean): RecyclerView.Adapter<BarcodeAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(context).inflate(R.layout.list_barcode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(barcodeList[position].contains("_")){
            var splitText = barcodeList[position].split("_")
            holder.txtTitle.text = splitText[0]
        }else{
            holder.txtTitle.text = barcodeList[position]
        }

        if(!isRemoveActive)
            holder.btnRemove.visibility = View.GONE

        holder.btnRemove.setOnClickListener {
            barcodeList.removeAt(position)
            notifyDataSetChanged()

            CSP.saveData("ActivityDetail_BARCODE_SET", barcodeList.joinToString(","))

            if(barcodeList.size == 0)
                dialog.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return barcodeList.size
    }
}