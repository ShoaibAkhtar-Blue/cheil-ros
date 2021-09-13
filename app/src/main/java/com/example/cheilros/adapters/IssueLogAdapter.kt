package com.example.cheilros.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.data.UserData
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.ActivityCategoryData
import com.example.cheilros.models.IssueLogData

class IssueLogAdapter(
    val context: Context,
    val itemList: List<IssueLogData>,
    val arguments: Bundle?,
    val userData: List<UserData>
): RecyclerView.Adapter<IssueLogAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var LLchecklist : LinearLayout = view.findViewById(R.id.LLchecklist)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
        var txtDesc : TextView = view.findViewById(R.id.txtDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(context).inflate(R.layout.list_issue_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtTitle.text = "${itemList[position].FollowupDateTime} - ${userData[0].memberName}"
        holder.txtDesc.text = "${itemList[position].FollowupDescription}"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}