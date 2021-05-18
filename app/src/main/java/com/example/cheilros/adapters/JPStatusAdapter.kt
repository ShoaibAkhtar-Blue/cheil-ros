package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.BaseAdapter
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.fragments.JourneyPlanFragment
import com.example.cheilros.models.JPStatusData
import com.example.cheilros.models.MenuNavigationModel
import kotlinx.android.synthetic.main.item_jpstatus.view.*
import java.util.ArrayList


class JPStatusAdapter (private val context: Context, data: List<JPStatusData>, fragment: JourneyPlanFragment) : BaseAdapter() {

    private val ListData: List<JPStatusData>
    private val mInflater: LayoutInflater
    private val frag: JourneyPlanFragment

    override fun getCount(): Int {
        return ListData.size
    }

    override fun getItem(position: Int): Any {
        return ListData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        /*var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_jpstatus, null)
            holder = ViewHolder()
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        //holder.txtCount!!.text = ListData[position].StatusCount
        //holder.txtLabel!!.text = ListData[position].VisitStatus*/
        Log.i("data", ListData[position].VisitStatus)

        var convertView = mInflater.inflate(R.layout.item_jpstatus, null)
        Glide.with(context).load(ListData[position].IconImage).into(convertView.imgStatus!!)
        convertView.txtCount.text = ListData[position].StatusCount
        convertView.txtLabel.text = ListData[position].VisitStatus

        convertView.cvJPstatus.setOnClickListener{
            frag.filerJP(position)
        }

        return convertView
    }

    internal class ViewHolder {
        var txtCount: TextView? = null
        var txtLabel: TextView? = null
    }

    init {
        mInflater = LayoutInflater.from(context)
        ListData = data
        frag = fragment
    }
}