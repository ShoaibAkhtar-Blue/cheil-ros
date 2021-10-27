package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.cheilros.R
import com.example.cheilros.fragments.storeview.SalesDetailFragment
import com.example.cheilros.fragments.storeview.StockDetailFragment
import kotlinx.android.synthetic.main.item_jpcurrentweek.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class StockDetailCurrentWeekAdapter(
    private val context: Context,
    private val fragment: StockDetailFragment,
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

        val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val listDate = data[position]

        /*val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(listDate, formatter)

        val filterFormatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.ENGLISH)
        val filterdate = LocalDate.parse(filterDate, filterFormatter)

        val jpdate = date.format(DateTimeFormatter.ofPattern("yyyy-M-d"))

        println("listDate: ${listDate}-${filterdate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}")

        if (listDate.equals(filterdate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))) {
            convertView.cvJPweek.setCardBackgroundColor(Color.BLACK)
            //convertView.cvJPweek.layoutParams = LinearLayout.LayoutParams(160, LinearLayout.LayoutParams.WRAP_CONTENT);
            convertView.txtDate.setTextColor(Color.WHITE)
        }

        var curDay = date.format(DateTimeFormatter.ofPattern("E"))
        var curDate = date.format(DateTimeFormatter.ofPattern("d"))
        //println("listDate: ${curDay}")

        convertView.txtDay.text = curDay
        convertView.txtDate.text = curDate

        convertView.cvJPweek.setOnClickListener {
            fragment.filterTS(0, jpdate)
        }*/

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH)
            val date = LocalDate.parse(listDate, formatter)

            val filterFormatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.ENGLISH)
            val filterdate = LocalDate.parse(filterDate, filterFormatter)

            val jpdate = date.format(DateTimeFormatter.ofPattern("yyyy-M-d"))

            println("listDate: ${listDate}-${filterdate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))}")

            if (listDate.equals(filterdate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))) {
                convertView.cvJPweek.setCardBackgroundColor(Color.BLACK)
                //convertView.cvJPweek.layoutParams = LinearLayout.LayoutParams(160, LinearLayout.LayoutParams.WRAP_CONTENT);
                convertView.txtDate.setTextColor(Color.WHITE)
            }

            var curDay = date.format(DateTimeFormatter.ofPattern("E"))
            var curDate = date.format(DateTimeFormatter.ofPattern("d"))
            //println("listDate: ${curDay}")

            convertView.txtDay.text = curDay
            convertView.txtDate.text = curDate

            convertView.cvJPweek.setOnClickListener {
                fragment.filterTS(0, jpdate)
            }
        }else{

            val parser =  SimpleDateFormat("MM/dd/yyyy")
            val formatter = SimpleDateFormat("yyyy-M-d")
            val formatter1 = SimpleDateFormat("E")
            val formatter2 = SimpleDateFormat("d")
            //val formattedDate = formatter.format(parser.parse("2018-12-14T09:55:00"))

            val date = parser.parse(listDate)
            val filterdate = formatter.parse(filterDate)

            val jpdate = formatter.format(date)

            if (listDate == parser.format(formatter.parse(filterDate))) {
                convertView.cvJPweek.setCardBackgroundColor(Color.BLACK)
                //convertView.cvJPweek.layoutParams = LinearLayout.LayoutParams(160, LinearLayout.LayoutParams.WRAP_CONTENT);
                convertView.txtDate.setTextColor(Color.WHITE)
            }

            var curDay = formatter1.format(parser.parse(listDate))
            var curDate = formatter2.format(parser.parse(listDate))

            convertView.txtDay.text = curDay.toString()
            convertView.txtDate.text = curDate.toString()

            convertView.cvJPweek.setOnClickListener {
                fragment.filterTS(0, jpdate)
            }
        }

        return convertView
    }

    init {
        mInflater = LayoutInflater.from(context)
    }
}