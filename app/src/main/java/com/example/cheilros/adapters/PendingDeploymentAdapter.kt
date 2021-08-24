package com.example.cheilros.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.PendingDeploymentFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.MyCoverageData
import com.example.cheilros.models.PendingDeploymentData

class PendingDeploymentAdapter(
    val context: Context,
    val itemList: List<PendingDeploymentData>,
    val settingData: List<AppSetting>,
    val fragment: PendingDeploymentFragment,
    val activity: NewDashboardActivity
) : RecyclerView.Adapter<PendingDeploymentAdapter.ViewHolder>() {
    lateinit var CSP: CustomSharedPref

    var filterList = ArrayList<PendingDeploymentData>()

    init {
        filterList = itemList as ArrayList<PendingDeploymentData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
        var txtTask: TextView = view.findViewById(R.id.txtTask)
        var txtCode: TextView = view.findViewById(R.id.txtCode)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view =
            LayoutInflater.from(context).inflate(R.layout.list_pending_deployment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtSerialNo.text = (position + 1).toString()
        holder.txtTask.text = filterList[position].TaskDeploymentCategoryName + " / " + filterList[position].ActivityCategoryName
        holder.txtCode.text = filterList[position].StoreCode
        holder.txtTitle.text = filterList[position].StoreName
    }
    override fun getItemCount(): Int {
        return filterList.size
    }
}