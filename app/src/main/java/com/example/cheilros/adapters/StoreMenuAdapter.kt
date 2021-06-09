package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import kotlinx.android.synthetic.main.item_jpstatus.view.imgStatus
import kotlinx.android.synthetic.main.item_jpstatus.view.txtLabel
import kotlinx.android.synthetic.main.item_storemenu.view.*

class StoreMenuAdapter(
    private val context: Context,
    private val ListData: List<AppSetting>,
    private val arguments: Bundle?
) : BaseAdapter() {

    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return ListData.size
    }

    override fun getItem(position: Int): Any {
        return ListData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = mInflater.inflate(R.layout.item_storemenu, null)
        Glide.with(context).load(ListData[position].imagePath).into(convertView.imgStatus!!)
        convertView.txtLabel.text = ListData[position].labelName

        convertView.cvStoreMenu.setOnClickListener {
            println(ListData[position].labelName)

            val bundle = bundleOf(
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )

            if (ListData[position].fixedLabelName.equals("StoreMenu_Checklist")) {
                Navigation.findNavController(it)
                    .navigate(R.id.action_storeViewFragment_to_checklistCategoryFragment, bundle)
            }

            if (ListData[position].fixedLabelName.equals("StoreMenu_Investment")) {
                Navigation.findNavController(it)
                    .navigate(R.id.action_storeViewFragment_to_investmentFragment, bundle)
            }

            if (ListData[position].fixedLabelName.equals("StoreMenu_Activity")) {
                Navigation.findNavController(it)
                    .navigate(R.id.action_storeViewFragment_to_activityFragment)
            }

            if (ListData[position].fixedLabelName.equals("StoreMenu_Training")) {
                Navigation.findNavController(it)
                    .navigate(R.id.action_storeViewFragment_to_trainingFragment)
            }
        }

        return convertView
    }
}