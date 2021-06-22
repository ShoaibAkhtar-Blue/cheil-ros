package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.models.MenuNavigationModel
import java.util.*

class MenuNavigationAdapter(private val context: Context, data: ArrayList<MenuNavigationModel>) : BaseAdapter() {
    private val menuData: ArrayList<MenuNavigationModel>
    private val mInflater: LayoutInflater
    override fun getCount(): Int {
        return menuData.size
    }

    override fun getItem(position: Int): Any {
        return menuData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_menunavigation, null)
            holder = ViewHolder()
            holder.buttonContainer = convertView.findViewById<View>(R.id.buttonContainer) as CardView
            holder.buttonMenu = convertView.findViewById<View>(R.id.buttonMenu) as LinearLayout
            holder.icon = convertView.findViewById<View>(R.id.icon) as ImageView
            holder.name = convertView.findViewById<View>(R.id.name) as TextView
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        Glide.with(context).load(menuData[position].menuImage).into(holder.icon!!)
        //holder.icon!!.setImageResource(menuData[position].menuIcon)
        holder.name?.setText(menuData[position].menuName)
        if (menuData[position].isSelected) {
            holder.buttonContainer?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.purple_200))
            holder.name!!.setTypeface(null, Typeface.BOLD)
        } else {
            holder.buttonContainer?.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.name!!.setTypeface(null, Typeface.NORMAL)
        }

        holder.buttonMenu!!.setOnClickListener {
            if(menuData[position].fixedLabel == "MenuTitle5"){
                Navigation.findNavController(it)
                    .navigate(R.id.action_dashboardFragment_to_teamStatusFragment)
            }
        }

        return convertView
    }

    internal class ViewHolder {
        var buttonContainer: CardView? = null
        var buttonMenu: LinearLayout? = null
        var icon: ImageView? = null
        var name: TextView? = null
    }

    init {
        mInflater = LayoutInflater.from(context)
        menuData = data
    }
}