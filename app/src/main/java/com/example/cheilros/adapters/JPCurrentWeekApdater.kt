package com.example.cheilros.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import com.example.cheilros.R
import com.example.cheilros.fragments.JourneyPlanFragment
import com.example.cheilros.models.JPCurrentWeekData
import kotlinx.android.synthetic.main.item_jpcurrentweek.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class JPCurrentWeekApdater(
    private val context: Context,
    private val fragment: JourneyPlanFragment,
    private val data: List<String>
) : BaseAdapter() {

    private val mInflater: LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = mInflater.inflate(R.layout.item_jpcurrentweek, null)

        val listDate = data[position]
        val formatter = DateTimeFormatter.ofPattern("MM/d/yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(listDate, formatter)

        var curDay = date.format(DateTimeFormatter.ofPattern("E"))
        var curDate = date.format(DateTimeFormatter.ofPattern("d"))
        println("listDate: ${curDay}")


        convertView.txtDay.text = curDay
        convertView.txtDate.text = curDate

        return convertView
    }

    init {
        mInflater = LayoutInflater.from(context)
    }
}