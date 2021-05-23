package com.example.cheilros.adapters

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.JourneyPlanFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.JourneyPlanData
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


class JPAdapter(
    val context: Context,
    val itemList: List<JourneyPlanData>,
    fragment: JourneyPlanFragment,
    isCurrentDate: Boolean,
    val settingData: List<AppSetting>
): RecyclerView.Adapter<JPAdapter.ViewHolder>(), OnMapReadyCallback {

    lateinit var CSP: CustomSharedPref

    private val client = OkHttpClient()
    var ctx: Context? = null
    var curPos: Int = 0

    lateinit var locationManager: LocationManager

    private val frag: JourneyPlanFragment
    private val isCurrDt: Boolean

    init {
        frag = fragment
        isCurrDt = isCurrentDate
    }

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
        CSP = CustomSharedPref(parent.context)
        val view= LayoutInflater.from(ctx).inflate(R.layout.journy_plan_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtCode.text = itemList[position].StoreCode

        holder.txtTitle.text = itemList[position].StoreName
        holder.txtTitleHeader.text = "${itemList[position].StoreCode} - ${itemList[position].StoreName}"
        holder.txtTitleContent.text = itemList[position].Address

        holder.txtRemarks.text = itemList[position].VisitRemarks
        holder.txtRemarksContent.text = itemList[position].VisitRemarks

        holder.txtTime.text = "\uD83D\uDD65 In: ${itemList[position].CheckInTime} | \uD83D\uDD65 Out: ${itemList[position].CheckOutTime}"

        Glide.with(context).load(itemList[position].ImageLocation).into(holder.imgStatus!!)

        if(itemList[position].VisitRemarks == null)
            holder.txtRemarks.visibility =  View.GONE

        if(itemList[position].VisitStatusID === 1){
            holder.RLStatus.setBackgroundColor(Color.GRAY)
            holder.txtTime.visibility =  View.GONE
            holder.btnAccept.text = settingData.filter { it.fixedLabelName == "JourneyPlan_CheckinButton" }.get(0).labelName
        }
        else if (itemList[position].VisitStatusID === 2){
            holder.RLStatus.setBackgroundColor(Color.BLUE)
            holder.btnAccept.text = settingData.filter { it.fixedLabelName == "JourneyPlan_CheckoutButton" }.get(0).labelName
            holder.btnCancel.text = settingData.filter { it.fixedLabelName == "JourneyPlan_ViewButton" }.get(0).labelName
//            holder.btnCancel.isEnabled = false
//            holder.btnCancel.isClickable = false
        }

        else if (itemList[position].VisitStatusID === 3){
            holder.RLStatus.setBackgroundColor(Color.GREEN)
            holder.btnAccept.isEnabled = false
            holder.btnAccept.isClickable = false

//            holder.btnCancel.isEnabled = false
//            holder.btnCancel.isClickable = false
        }

        else if (itemList[position].VisitStatusID === 4){
            holder.RLStatus.setBackgroundColor(Color.RED)

            holder.btnAccept.isEnabled = false
            holder.btnAccept.isClickable = false

//            holder.btnCancel.isEnabled = false
//            holder.btnCancel.isClickable = false
        }

        if(!isCurrDt){
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
            //val bundle = bundleOf("visit_id" to itemList[position].VisitID)


//            val myactivity = Intent(context.applicationContext, CameraActivity::class.java)
//            myactivity.addFlags(FLAG_ACTIVITY_FORWARD_RESULT)
//            context.applicationContext.startActivity(myactivity)

            if(itemList[position].VisitStatusID === 1){
                if(CSP.getData("CheckIn_Camera").equals("Y")){
                    CSP.saveData("sess_visit_id", itemList[position].VisitID.toString())
                    CSP.saveData("sess_visit_status_id", itemList[position].VisitStatusID.toString())
                    Navigation.findNavController(it).navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                }else{
                    sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckIn?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                }
            }

            if(itemList[position].VisitStatusID === 2){
                if(CSP.getData("CheckIn_Camera").equals("Y")){
                    CSP.saveData("sess_visit_id", itemList[position].VisitID.toString())
                    CSP.saveData("sess_visit_status_id", itemList[position].VisitStatusID.toString())
                    Navigation.findNavController(it).navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                }else{
                    sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                }
            }

        }

        holder.btnCancel.setOnClickListener {
            if(itemList[position].VisitStatusID === 1){
                if(CSP.getData("CancelVisit").equals("Y")){
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

                    cancelJP("${CSP.getData("base_url")}/JourneyPlan.asmx/CancelVisit?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=Cancel")
                }else{
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Permission!!")
                                .setMessage("You Don't have permission rights for this action!")
                                .sneakWarning()
                        }
                    }
                }
            }else{

            }
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
                        frag.reloadJP()
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

    fun cancelJP(url: String) {
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
                        frag.reloadJP()
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
