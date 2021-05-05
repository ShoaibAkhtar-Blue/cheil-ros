package com.example.cheilros.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.fragments.JourneyPlanData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ramotion.foldingcell.FoldingCell


class JPAdapter(val context: Context, val itemList: List<JourneyPlanData>): RecyclerView.Adapter<JPAdapter.ViewHolder>(), OnMapReadyCallback {

    var ctx: Context? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtCode : TextView = view.findViewById(R.id.txtCode)
        var mapView : MapView = view.findViewById(R.id.mapView)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
        var txtTitleHeader : TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleContent : TextView = view.findViewById(R.id.txtTitleContent)
        var txtRemarks : TextView = view.findViewById(R.id.txtRemarks)
        var txtTime : TextView = view.findViewById(R.id.txtTime)

        var RLStatus : RelativeLayout = view.findViewById(R.id.RLStatus)

        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ctx = parent.context;
        val view= LayoutInflater.from(ctx).inflate(R.layout.journy_plan_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtCode.text = itemList[position].StoreCode

        holder.txtTitle.text = itemList[position].StoreName
        holder.txtTitleHeader.text = itemList[position].StoreName
        holder.txtTitleContent.text = itemList[position].StoreName

        holder.txtRemarks.text = itemList[position].VisitRemarks

        if(itemList[position].VisitRemarks == null)
            holder.txtRemarks.visibility =  View.GONE

        if(itemList[position].VisitStatusID === 1){
            holder.RLStatus.setBackgroundColor(Color.GRAY)
            holder.txtTime.visibility =  View.GONE
        }
        else if (itemList[position].VisitStatusID === 2)
            holder.RLStatus.setBackgroundColor(Color.BLUE)
        else if (itemList[position].VisitStatusID === 3)
            holder.RLStatus.setBackgroundColor(Color.GREEN)
        else if (itemList[position].VisitStatusID === 4)
            holder.RLStatus.setBackgroundColor(Color.RED)

        val mapView: MapView = holder.mapView

        if (mapView != null) {
            // Initialise the MapView
            mapView.onCreate(null)
            mapView.onResume();  //Probably U r missing this
            // Set the map ready callback to receive the GoogleMap object
            mapView.getMapAsync(this)
        }


        holder.btnSee.setOnClickListener {
            holder.fc.toggle(false)
        }

        holder.btnClose.setOnClickListener {
            holder.fc.toggle(false)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        //val location: Location = locations.get(getAdapterPosition())
        val sydney = LatLng(-33.852, 151.211)
        googleMap!!.addMarker(MarkerOptions().position(sydney).title("Sydney"))
    }
}


/*
class JPAdapter(val context: Context, val itemList:ArrayList<String>): RecyclerView.Adapter<JPAdapter.ViewHolder>()   {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //var textView: TextView = view.findViewById(R.id.txtTitle)
        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)
        //var btnClose  : MaterialButton = view.findViewById(R.id.btnCancel)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.journy_plan_cell,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        */
/*val text=itemList[position]
        holder.textView.text=text*//*

        holder.btnSee.setOnClickListener {
            holder.fc.toggle(false)
        }

        holder.btnClose.setOnClickListener {
            holder.fc.toggle(false)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}*/
