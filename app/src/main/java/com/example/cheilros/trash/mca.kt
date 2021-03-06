/*
package com.example.cheilros.adapters

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
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
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import okhttp3.*
import java.io.IOException


class JPAdaptaer(
    val context: Context,
    val itemList: List<JourneyPlanData>,
    fragment: JourneyPlanFragment,
    isCurrentDate: Boolean,
    val settingData: List<AppSetting>
) : RecyclerView.Adapter<JPAdapter.ViewHolder>(), OnMapReadyCallback {

    lateinit var CSP: CustomSharedPref

    private val client = OkHttpClient()
    var ctx: Context? = null
    var curPos: Int = 0

    lateinit var locationManager: LocationManager

    private val frag: JourneyPlanFragment
    private val isCurrDt: Boolean

    var lat: String = "0"
    var lng: String = "0"
    var minDistance = 750.0

    init {
        frag = fragment
        isCurrDt = isCurrentDate
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgStatus: ImageView = view.findViewById(R.id.imgStatus)
        var txtCode: TextView = view.findViewById(R.id.txtCode)
        var mapView: MapView = view.findViewById(R.id.mapView)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtTitleHeader: TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleContent: TextView = view.findViewById(R.id.txtTitleContent)
        var txtRemarks: TextView = view.findViewById(R.id.txtRemarks)
        var txtRemarksContent: TextView = view.findViewById(R.id.txtRemarksContent)
        var txtTime: TextView = view.findViewById(R.id.txtTime)

        var RLStatus: RelativeLayout = view.findViewById(R.id.RLStatus)
        var RLHeader: RelativeLayout = view.findViewById(R.id.RLHeader)

        var fc: FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee: LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose: RelativeLayout = view.findViewById(R.id.RLHeader)

        var btnAccept: Button = view.findViewById(R.id.btnAccept)
        var btnCancel: Button = view.findViewById(R.id.btnCancel)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ctx = parent.context;
        CSP = CustomSharedPref(parent.context)
        val view = LayoutInflater.from(ctx).inflate(R.layout.journy_plan_cell, parent, false)

        try {
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                LocationListener {
                override fun onLocationChanged(location: Location) {
                    lat = location.latitude.toString()
                    lng = location.longitude.toString()
                    println("loc: ${location.latitude}")

                }
            })
        } catch (ex: Exception) {
            Log.e("Error_", ex.message.toString())
        }


        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtCode.text = itemList[position].StoreCode
        holder.txtCode.visibility = View.GONE

        holder.txtTitle.text = itemList[position].StoreName
        holder.txtTitleHeader.text =
            "${itemList[position].StoreCode} - ${itemList[position].StoreName}"
        holder.txtTitleContent.text = itemList[position].Address

        holder.txtRemarks.text = itemList[position].VisitRemarks
        holder.txtRemarksContent.text = itemList[position].VisitRemarks

        holder.txtTime.text =
            "\uD83D\uDD65 In: ${itemList[position].CheckInTime} | \uD83D\uDD65 Out: ${itemList[position].CheckOutTime}"

        Glide.with(context).load(itemList[position].ImageLocation).into(holder.imgStatus!!)

        if (itemList[position].VisitRemarks == null)
            holder.txtRemarks.text = "No Remarks"

        holder.btnCancel.text =
            settingData.filter { it.fixedLabelName == "JuorneyPlan_CancelButton" }.get(0).labelName
        holder.btnAccept.text =
            settingData.filter { it.fixedLabelName == "JourneyPlan_CheckinButton" }.get(0).labelName

        if (itemList[position].VisitStatusID === 1) {
            holder.RLStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_none))
            holder.RLHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.status_none))
            holder.txtTime.visibility = View.GONE

        } else if (itemList[position].VisitStatusID === 2) {
            holder.RLStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_checkout
                )
            )
            holder.RLHeader.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_checkout
                )
            )
            holder.btnAccept.text =
                settingData.filter { it.fixedLabelName == "JourneyPlan_CheckoutButton" }
                    .get(0).labelName
            holder.btnCancel.text =
                settingData.filter { it.fixedLabelName == "JourneyPlan_ViewButton" }
                    .get(0).labelName
//            holder.btnCancel.isEnabled = false
//            holder.btnCancel.isClickable = false
        } else if (itemList[position].VisitStatusID === 3) {
            holder.RLStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_checkin
                )
            )
            holder.RLHeader.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_checkin
                )
            )
            holder.btnCancel.text =
                settingData.filter { it.fixedLabelName == "JourneyPlan_ViewButton" }
                    .get(0).labelName
            holder.btnAccept.setTextColor(Color.GRAY)

            holder.btnAccept.isEnabled = false
            holder.btnAccept.isClickable = false

            //holder.btnCancel.visibility = View.GONE

            //holder.btnCancel.isEnabled = false
            //holder.btnCancel.isClickable = false
        } else if (itemList[position].VisitStatusID === 4) {
            holder.RLStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_cancel
                )
            )
            holder.RLHeader.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.status_cancel
                )
            )
            holder.btnCancel.text =
                settingData.filter { it.fixedLabelName == "JourneyPlan_ViewButton" }
                    .get(0).labelName
            holder.btnAccept.setTextColor(Color.GRAY)

            holder.btnAccept.isEnabled = false
            holder.btnAccept.isClickable = false

            holder.btnAccept.visibility = View.GONE
            //holder.btnCancel.visibility = View.GONE

//            holder.btnCancel.isEnabled = false
//            holder.btnCancel.isClickable = false
        }

        if (!isCurrDt) {
            holder.btnAccept.setTextColor(Color.GRAY)
            holder.btnCancel.setTextColor(Color.GRAY)

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

        holder.btnAccept.setOnClickListener {


            */
/*locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
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

            })*//*

            //val bundle = bundleOf("visit_id" to itemList[position].VisitID)


//            val myactivity = Intent(context.applicationContext, CameraActivity::class.java)
//            myactivity.addFlags(FLAG_ACTIVITY_FORWARD_RESULT)
//            context.applicationContext.startActivity(myactivity)


            try {
                println("Location: $lat-$lng")
                val myLocation = Location("")

                myLocation.latitude = lat.toDouble()
                myLocation.longitude = lng.toDouble()

                val storeLocation = Location("")
                storeLocation.latitude = itemList[position].Latitude.toDouble()
                storeLocation.longitude = itemList[position].Longitude.toDouble()

                val distanceInMeters: Float = myLocation.distanceTo(storeLocation)
                println("distanceInMeters: ${distanceInMeters}")

                if (CSP.getData("LocationLimit").equals("Y")) {
                    if (distanceInMeters >= minDistance) {
                        println("distanceInMeters: Distance is greater")
                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                    .setTitle("Out of Range!!")
                                    .setMessage("Your Current Location is greater than $minDistance meters!")
                                    .sneakWarning()
                            }
                        }
                    }else{
                        if(itemList[position].VisitStatusID === 1){
                            if(CSP.getData("CheckIn_Camera").equals("Y")){
                                CSP.saveData("sess_visit_id", itemList[position].VisitID.toString())
                                CSP.saveData("sess_visit_status_id", itemList[position].VisitStatusID.toString())
                                CSP.saveData("fragName", "JP")

                                Navigation.findNavController(it).navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                            }else{
                                sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckIn?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                            }
                        }

                        if(itemList[position].VisitStatusID === 2){
                            if(CSP.getData("CheckOut_Camera").equals("Y")){
                                CSP.saveData("sess_visit_id", itemList[position].VisitID.toString())
                                CSP.saveData("sess_visit_status_id", itemList[position].VisitStatusID.toString())
                                CSP.saveData("fragName", "JP")
                                Navigation.findNavController(it).navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                            }else{
                                sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                            }
                        }
                    }
                }else{
                    if (itemList[position].VisitStatusID === 1) {
                        if (CSP.getData("CheckIn_Camera").equals("Y")) {
                            CSP.saveData("sess_visit_id", itemList[position].VisitID.toString())
                            CSP.saveData(
                                "sess_visit_status_id",
                                itemList[position].VisitStatusID.toString()
                            )
                            CSP.saveData("fragName", "JP")

                            Navigation.findNavController(it)
                                .navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                        } else {
                            sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckIn?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                        }
                    }

                    if (itemList[position].VisitStatusID === 2) {
                        if (CSP.getData("CheckOut_Camera").equals("Y")) {
                            CSP.saveData("sess_visit_id", itemList[position].VisitID.toString())
                            CSP.saveData(
                                "sess_visit_status_id",
                                itemList[position].VisitStatusID.toString()
                            )
                            CSP.saveData("fragName", "JP")
                            Navigation.findNavController(it)
                                .navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                        } else {
                            sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                        }
                    }
                }

            } catch (ex: Exception) {

            }




        }

        holder.btnCancel.setOnClickListener {
            if (itemList[position].VisitStatusID === 1) {
                if (CSP.getData("CancelVisit").equals("Y")) {
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
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        0F,
                        object : LocationListener {
                            override fun onLocationChanged(location: Location) {
                                lat = location.latitude.toString()
                                lng = location.longitude.toString()
                                println("loc: ${location.latitude}")
                            }

                        })

                    val li = LayoutInflater.from(context)
                    val promptsView: View = li.inflate(R.layout.dialog_add_visit, null)

                    val dialog = Dialog(context)
                    dialog.setContentView(promptsView)
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(true)


                    dialog.txtTitle.text =
                        settingData.filter { it.fixedLabelName == "JourneyPlan_Title" }
                            .get(0).labelName
                    dialog.txtQuestion.text =
                        settingData.filter { it.fixedLabelName == "JourneyPlan_CancelTitle" }
                            .get(0).labelName

                    dialog.btnCancel.text =
                        settingData.filter { it.fixedLabelName == "StoreList_PopupCancel" }
                            .get(0).labelName
                    dialog.btnCancel.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.btnAccept.text =
                        settingData.filter { it.fixedLabelName == "JourneyPlan_CancelSave" }
                            .get(0).labelName
                    dialog.btnAccept.setOnClickListener {
                        cancelJP("${CSP.getData("base_url")}/JourneyPlan.asmx/CancelVisit?VisitID=${itemList[position].VisitID}&Longitude=$lng&Latitude=$lat&Remarks=${dialog.etRemarks.text}")
                        dialog.dismiss()
                    }
                    dialog.show()

                } else {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Permission!!")
                                .setMessage("You Don't have permission rights for this action!")
                                .sneakWarning()
                        }
                    }
                }
            } else {
                // If type is view
                val bundle = bundleOf(
                    "StoreID" to itemList[position].StoreID,
                    "StoreName" to itemList[position].StoreName
                )
                Navigation.findNavController(it)
                    .navigate(R.id.action_journeyPlanFragment_to_storeViewFragment, bundle)
            }
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        //val location: Location = locations.get(getAdapterPosition())
        try {
            println("onMapReady-${itemList[curPos].Latitude}-${itemList[curPos].Longitude}")
            val sydney =
                LatLng(itemList[curPos].Latitude.toDouble(), itemList[curPos].Longitude.toDouble())
            googleMap!!.addMarker(
                MarkerOptions().position(sydney).title(itemList[curPos].StoreName)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20.5f))

            googleMap!!.setOnMapClickListener {
                try {
                    val uri =
                        "http://maps.google.com/maps?daddr=${itemList[curPos].Latitude},${itemList[curPos].Longitude}"
                    val intent = Intent(ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    context.startActivity(intent)
                } catch (ex: Exception) {

                }
            }

        } catch (ex: Exception) {
            val sydney = LatLng(0.0, 0.0)
            googleMap!!.addMarker(
                MarkerOptions().position(sydney).title(itemList[curPos].StoreName)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20.5f))
        }

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
*/
