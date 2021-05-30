package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RequiresApi
import com.example.cheilros.R
import com.example.cheilros.fragments.JourneyPlanFragment
import kotlinx.android.synthetic.main.item_jpcurrentweek.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class JPCurrentWeekApdater(
    private val context: Context,
    private val fragment: JourneyPlanFragment,
    private val data: List<String>,
    private val filterDate: String
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

        val simpleDateFormat = SimpleDateFormat("MM/d/yyyy")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val listDate = data[position]

        val formatter = DateTimeFormatter.ofPattern("MM/d/yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(listDate, formatter)

        val filterFormatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.ENGLISH)
        val filterdate = LocalDate.parse(filterDate, filterFormatter)

        val jpdate = date.format(DateTimeFormatter.ofPattern("yyyy-M-d"))

        if(listDate.equals(filterdate.format(DateTimeFormatter.ofPattern("MM/d/yyyy")))){
            convertView.cvJPweek.setCardBackgroundColor(Color.BLACK)
            convertView.txtDate.setTextColor(Color.WHITE)
        }

        var curDay = date.format(DateTimeFormatter.ofPattern("E"))
        var curDate = date.format(DateTimeFormatter.ofPattern("d"))
        println("listDate: ${curDay}")

        convertView.txtDay.text = curDay
        convertView.txtDate.text = curDate

        convertView.cvJPweek.setOnClickListener {
            fragment.filerJP(0, jpdate)
        }

        return convertView
    }

    init {
        mInflater = LayoutInflater.from(context)
    }
}