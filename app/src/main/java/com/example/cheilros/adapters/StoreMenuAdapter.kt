package com.example.cheilros.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
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
import com.example.cheilros.helpers.CustomSharedPref
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.item_jpstatus.view.imgStatus
import kotlinx.android.synthetic.main.item_jpstatus.view.txtLabel
import kotlinx.android.synthetic.main.item_storemenu.view.*

class StoreMenuAdapter(
    private val context: Context,
    private val ListData: List<AppSetting>,
    private val arguments: Bundle?,
    val settingData: List<AppSetting>
) : BaseAdapter() {
    lateinit var CSP: CustomSharedPref
    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
        CSP = CustomSharedPref(context)
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

        if (ListData[position].fixedLabelName == "StoreMenu_Checklist" || ListData[position].fixedLabelName == "StoreMenu_Investment" || ListData[position].fixedLabelName == "StoreMenu_Activity" || ListData[position].fixedLabelName == "StoreMenu_Training" || ListData[position].fixedLabelName == "StoreMenu_Ticket" || ListData[position].fixedLabelName == "StoreMenu_Installation" || ListData[position].fixedLabelName == "StoreMenu_ModelCount" || ListData[position].fixedLabelName == "StoreMenu_PricePromotions" || ListData[position].fixedLabelName == "StoreMenu_DailySales" || ListData[position].fixedLabelName == "StoreMenu_Campaign" || ListData[position].fixedLabelName == "StoreMenu_DailyStock") {

        } else {
            convertView.cvStoreMenu.setCardBackgroundColor(Color.GRAY)
        }

        convertView.cvStoreMenu.setOnClickListener {
            println(ListData[position].labelName)

            val bundle = bundleOf(
                "StoreID" to arguments?.getInt("StoreID"),
                "StoreName" to arguments?.getString("StoreName")
            )

            if (ListData[position].fixedLabelName == "StoreMenu_Checklist") {
                if(CSP.getData("StoreMenu_Checklist").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_checklistCategoryFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_Investment") {
                if(CSP.getData("StoreMenu_Investment").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_investmentFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_Activity") {
                if(CSP.getData("StoreMenu_Activity").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_activityFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_Training") {
                if(CSP.getData("StoreMenu_Training").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_trainingNewFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_Ticket") {
                if(CSP.getData("StoreMenu_Ticket").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_installationFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_Installation") {
                if(CSP.getData("StoreMenu_Installation").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_installationMainFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_ModelCount") {
                if(CSP.getData("StoreMenu_ModelCount").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_displayCountFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_PricePromotions") {
                if(CSP.getData("StoreMenu_PricePromotions").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_priceFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_DailySales") {
                if(CSP.getData("StoreMenu_DailySales").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_salesFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_Campaign") {
                if(CSP.getData("StoreMenu_Campaign").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_storePicturesFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }

            if (ListData[position].fixedLabelName == "StoreMenu_DailyStock") {
                if(CSP.getData("StoreMenu_DailyStock").equals("Y")){
                    Navigation.findNavController(it)
                        .navigate(R.id.action_storeViewFragment_to_stockFragment, bundle)
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Warning!!")
                                .setMessage(settingData.filter { it.fixedLabelName == "ScreenAccessPermission_Msg" }[0].labelName)
                                .sneakWarning()
                        }
                    }
                }
            }
        }

        return convertView
    }
}