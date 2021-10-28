package com.example.cheilros.adapters

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.TrainingSummaryData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class TrainingSummaryAdapter(
    val context: Context,
    val itemList: List<TrainingSummaryData>,
    val arguments: Bundle?
) : RecyclerView.Adapter<TrainingSummaryAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<TrainingSummaryData>()

    init {
        filterList = itemList as ArrayList<TrainingSummaryData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
        var TrainingTypeName: TextView = view.findViewById(R.id.TrainingTypeName)
        var Description: TextView = view.findViewById(R.id.Description)
        var TrainingDate: TextView = view.findViewById(R.id.TrainingDate)
        var Attendese: TextView = view.findViewById(R.id.Attendese)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_training_summary_new, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtSerialNo.text = (position + 1).toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
            val date = LocalDate.parse(filterList[position].TrainingDate, formatter)

            val jpdate = date.format(DateTimeFormatter.ofPattern("yyyy-M-d"))


            holder.TrainingTypeName.text = filterList[position].TrainingTypeName
            holder.Description.text = filterList[position].Description
            holder.TrainingDate.text =
                "$jpdate - ${filterList[position].Start} - ${filterList[position].EndTime}"
            holder.Attendese.text = "# Of Attendees: ${filterList[position].Attendese.toString()}"

        } else {

            val parser = SimpleDateFormat("MM/dd/yyyy")
            val formatter = SimpleDateFormat("yyyy-M-d")

            val date = parser.parse(filterList[position].TrainingDate)
            val jpdate = formatter.format(date)

            holder.TrainingTypeName.text = filterList[position].TrainingTypeName
            holder.Description.text = filterList[position].Description
            holder.TrainingDate.text =
                "$jpdate - ${filterList[position].Start} - ${filterList[position].EndTime}"
            holder.Attendese.text = "# Of Attendese: ${filterList[position].Attendese.toString()}"
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }


}