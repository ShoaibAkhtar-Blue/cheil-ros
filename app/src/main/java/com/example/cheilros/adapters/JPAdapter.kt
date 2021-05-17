package com.example.cheilros.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.fragments.JPStatusModel
import com.example.cheilros.fragments.JourneyPlanData
import com.example.cheilros.models.CheckInOutModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import okhttp3.*
import java.io.IOException


class JPAdapter(val context: Context, val itemList: List<JourneyPlanData>): RecyclerView.Adapter<JPAdapter.ViewHolder>(), OnMapReadyCallback {

    private val client = OkHttpClient()
    var ctx: Context? = null
    var curPos: Int = 0

    lateinit var locationManager: LocationManager

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgStatus : ImageView = view.findViewById(R.id.imgStatus)
        var txtCode : TextView = view.findViewById(R.id.txtCode)
        var mapView : MapView = view.findViewById(R.id.mapView)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
        var txtTitleHeader : TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleContent : TextView = view.findViewById(R.id.txtTitleContent)
        var txtRemarks : TextView = view.findViewById(R.id.txtRemarks)
        var txtRemarksContent : TextView = view.findViewById(R.id.txtRemarksContent)
        var txtTime : TextView = view.findViewById(R.id.txtTime)

        var RLStatus : RelativeLayout = view.findViewById(R.id.RLStatus)

        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)

        var btnAccept  : Button = view.findViewById(R.id.btnAccept)
        var btnCancel  : Button = view.findViewById(R.id.btnCancel)


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
        holder.txtRemarksContent.text = itemList[position].VisitRemarks

        Glide.with(context).load(itemList[position].ImageLocation).into(holder.imgStatus!!)

        if(itemList[position].VisitRemarks == null)
            holder.txtRemarks.visibility =  View.GONE

        if(itemList[position].VisitStatusID === 1){
            holder.RLStatus.setBackgroundColor(Color.GRAY)
            holder.txtTime.visibility =  View.GONE
            holder.btnAccept.text = "Check in"
        }
        else if (itemList[position].VisitStatusID === 2){
            holder.RLStatus.setBackgroundColor(Color.BLUE)
            holder.btnAccept.text = "Check out"
        }

        else if (itemList[position].VisitStatusID === 3){
            holder.RLStatus.setBackgroundColor(Color.GREEN)
            holder.btnAccept.isEnabled = false
            holder.btnAccept.isClickable = false
        }

        else if (itemList[position].VisitStatusID === 4){
            holder.RLStatus.setBackgroundColor(Color.RED)

            holder.btnAccept.isEnabled = false
            holder.btnAccept.isClickable = false

            holder.btnCancel.isEnabled = false
            holder.btnCancel.isClickable = false
        }


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
            curPos = position
        }

        holder.btnClose.setOnClickListener {
            holder.fc.toggle(false)
        }

        holder.btnAccept.setOnClickListener{

            var lat: String = "0"
            var lng: String = "0"


            locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setOnClickListener
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0F,object : LocationListener{
                override fun onLocationChanged(location: Location) {
                    lat = location.latitude.toString()
                    lng = location.longitude.toString()
                    println("loc: ${location.latitude}")
                }

            })

            if(itemList[position].VisitStatusID === 1)
                sendCheckInOutRequest("http://rosturkey.cheildata.com/JourneyPlan.asmx/CheckIn?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
            if(itemList[position].VisitStatusID === 2)
                sendCheckInOutRequest("http://rosturkey.cheildata.com/JourneyPlan.asmx/CheckOut?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        //val location: Location = locations.get(getAdapterPosition())
        println("onMapReady-${itemList[curPos].Latitude}-${itemList[curPos].Longitude}")
        val sydney = LatLng(itemList[curPos].Latitude.toDouble(), itemList[curPos].Longitude.toDouble())
        googleMap!!.addMarker(MarkerOptions().position(sydney).title(itemList[curPos].StoreName))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,13.5f));
    }

    fun sendCheckInOutRequest(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (context as Activity).runOnUiThread {
                    context?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Error!!")
                            .setMessage(e.message.toString())
                            .sneakWarning()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, CheckInOutModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success!!")
                                .setMessage("Data Updated")
                                .sneakSuccess()
                        }
                    }
                } else {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not Updated.")
                                .sneakWarning()
                        }
                    }
                }
            }
        })
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
