package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.MyCoverageFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.MyCoverageData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.mycoverage_content_cell.view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MyCoverageAdapter(
    val context: Context,
    val itemList: List<MyCoverageData>,
    val settingData: List<AppSetting>,
    val latitude: String,
    val longitude: String,
    fragment: MyCoverageFragment,
    val activity: NewDashboardActivity,
    val fusedLocationClient: FusedLocationProviderClient
) : RecyclerView.Adapter<MyCoverageAdapter.ViewHolder>(), Filterable {


    lateinit var CSP: CustomSharedPref
    private val client = OkHttpClient()
    lateinit var locationManager: LocationManager

    var curPos: Int = 0
    var lat: String = "0"
    var lng: String = "0"

    //var minDistance = 750.0
    private val frag: MyCoverageFragment

    var filterList = ArrayList<MyCoverageData>()

    init {
        frag = fragment
        filterList = itemList as ArrayList<MyCoverageData>
    }

    class ViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {

        var item: MyCoverageData? = null
        val mapView = view.mapView

        private lateinit var supportMapFragment: SupportMapFragment

        fun removeMapFragment(mContext: Context) {
            mContext.let {
                val fragmentManager: FragmentManager =
                    (it as AppCompatActivity).supportFragmentManager
                fragmentManager.beginTransaction().remove(supportMapFragment)
                    .commitAllowingStateLoss()
            }
        }

        fun bind(coverageData: MyCoverageData, mContext: Context, callback: OnMapReadyCallback) {


            try {
                mContext.let {
                    mapView.onCreate(null)
                    mapView.onResume()
                    mapView.getMapAsync(callback)


                }
            } catch (ex: Exception) {
                Log.e("Error_", ex.message.toString())
            }

        }

        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)

        //var mapView: MapView = view.findViewById(R.id.mapView)
        var txtCode: TextView = view.findViewById(R.id.txtCode)
        var txtTitle: TextView = view.findViewById(R.id.txtTitle)
        var txtTitleHeader: TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleContent: TextView = view.findViewById(R.id.txtTitleContent)
        var txtRemarksContent: TextView = view.findViewById(R.id.txtRemarksContent)
        var txtRegion: TextView = view.findViewById(R.id.txtRegion)
        var txtAddress: TextView = view.findViewById(R.id.txtAddress)
        var fc: FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee: LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose: RelativeLayout = view.findViewById(R.id.RLHeader)
        var btnAccept: Button = view.findViewById(R.id.btnAccept)
        var btnCancel: Button = view.findViewById(R.id.btnCancel)
        var btnLocUpdate: Button = view.findViewById(R.id.btnLocUpdate)
        var RLnum: RelativeLayout = view.findViewById(R.id.RLnum)
        //var imgMap: ImageView = view.findViewById(R.id.imgMap)


        //var btnClose  : MaterialButton = view.findViewById(R.id.btnCancel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.mycoverage_cell,
            parent,
            false
        )
        CSP = CustomSharedPref(parent.context)


        /* try {
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
         }*/

        return ViewHolder(view, parent.context)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filterList[position]
        holder.item = item
    }

    @SuppressLint("MissingPermission")
    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            val uLocation = activity.userLocation
            context?.let {
                holder.item?.let { itemData ->

                    if (CSP.getData("team_type_id")!!.toInt() <= 4) {
                        holder.btnAccept.visibility = View.GONE
                        holder.btnLocUpdate.visibility = View.GONE
                    }

                    if (CSP.getData("team_type_id")!!.toInt() >= 9) {
                        //holder.btnAccept.visibility = View.GONE
                        //holder.btnLocUpdate.visibility = View.GONE
                    }


                    holder.txtSerialNo.text = (holder.layoutPosition + 1).toString()

                    holder.txtCode.text = itemData.StoreCode
                    holder.txtTitle.text = itemData.StoreName
                    holder.txtTitleHeader.text =
                        "${itemData.StoreCode} - ${itemData.StoreName}"
                    holder.txtTitleContent.text = itemData.Address
                    holder.txtRemarksContent.text =
                        "Last Visit: ${itemData.LastVisitedDate} (${itemData.VistedBy})"

                    holder.txtRegion.text = itemData.RegionName
                    holder.txtAddress.text = itemData.Address

                    try {
                        if (settingData.filter { it.fixedLabelName == "StoreList_LocationUpdate" }[0].labelName == "") {
                            holder.btnLocUpdate.visibility = View.GONE
                        } else {
                            holder.btnLocUpdate.text =
                                settingData.filter { it.fixedLabelName == "StoreList_LocationUpdate" }[0].labelName
                        }
                    } catch (ex: Exception) {
                        holder.btnLocUpdate.visibility = View.GONE
                    }

                    holder.btnLocUpdate.setOnClickListener {

                        val builder = android.app.AlertDialog.Builder(context)
                        builder.setTitle("Update Location...")
                        builder.setMessage("Are you Sure?")

                        builder.setPositiveButton("Yes") { dialog, which ->
                            var locURL =
                                "${CSP.getData("base_url")}/Activity.asmx/UpdateLocation?StoreID=${itemData.StoreID}&TeamMemberID=${
                                    CSP.getData("user_id")
                                }&Longitude=${uLocation.longitude.toString()}&Latitude=${uLocation.latitude.toString()}"
                            println(locURL)
                            try {
                                val request = Request.Builder()
                                    .url(locURL)
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
                                        val apiData =
                                            gson.fromJson(body, CheckInOutModel::class.java)
                                        println(apiData.status)
                                        if (apiData.status == 200) {
                                            (context as Activity).runOnUiThread {
                                                context?.let { it1 ->
                                                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                                        .setTitle("Success!!")
                                                        .setMessage("Location Updated")
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
                            } catch (ex: Exception) {

                            }
                        }

                        builder.setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }

                        builder.show()


                    }


                    //Update Labels
                    try {
                        if (itemData.VisitStatusID != 0) {
                            holder.RLnum.setBackgroundColor(Color.parseColor("#d4eb0e"))
                            holder.btnAccept.text =
                                settingData.filter { it.fixedLabelName == "JourneyPlan_CheckoutButton" }
                                    .get(0).labelName
                        } else
                            holder.btnAccept.text =
                                settingData.filter { it.fixedLabelName == "JourneyPlan_CheckinButton" }
                                    .get(0).labelName

                        holder.btnCancel.text =
                            settingData.filter { it.fixedLabelName == "StoreList_ViewButton" }
                                .get(0).labelName

                        if (CSP.getData("team_type_id")!!.toInt() >= 9) {
                            holder.btnCancel.text = "Training"
                        }
                    } catch (ex: Exception) {

                    }

                    holder.btnAccept.setOnClickListener {

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    val lat = location.latitude
                                    val lng = location.longitude
                                    Log.d("getLastKnownLocation", "$lat-$lng")

                                    try {
                                        println("Location: ${location.latitude.toDouble()}-${location.longitude.toDouble()}")
                                        val myLocation = Location("")

                                        myLocation.latitude = 31.513813030475678
                                        myLocation.longitude = 74.34433225759757


                                        myLocation.latitude = location.latitude.toDouble()
                                        myLocation.longitude = location.longitude.toDouble()

//                                        lat = uLocation.latitude.toString()
//                                        lng = uLocation.longitude.toString()

                                        val storeLocation = Location("")

                                        try {
                                            storeLocation.latitude = itemData.Longitude.toDouble()
                                            storeLocation.longitude = itemData.Latitude.toDouble()
                                        } catch (ex: Exception) {
                                            storeLocation.latitude = 0.0
                                            storeLocation.longitude = 0.0
                                        }


                                        val distanceInMeters: Float =
                                            myLocation.distanceTo(storeLocation)
                                        println("distanceInMeters: ${distanceInMeters} Location: $lat - $lng")
                                        //activity.getLocation()


                                        /*val builder = AlertDialog.Builder(context)
                                        builder.setMessage("ULocation:${location.latitude.toDouble()} - ${location.longitude.toDouble()} \n Your Location: $lat - $lng \n Store Location: ${itemData.Longitude.toDouble()} - ${itemData.Latitude.toDouble()} \n Distance: $distanceInMeters")

                                        builder.setPositiveButton("Ok") { dialog, which ->

                                        }

                                        builder.show()*/

                                        var minDistance = CSP.getData("LocationLimit")?.toDouble()

                                        if (minDistance!! >= 0) {
                                            println("LocationLimit: $lat-$lng")
                                            println(
                                                "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                    itemData.StoreID
                                                }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=&Longitude=$lng&Latitude=$lat&Remarks=-"
                                            )
                                            if (distanceInMeters >= minDistance) {
                                                println("distanceInMeters: Distance is greater")
                                                (context as Activity).runOnUiThread {
                                                    context?.let { it1 ->
                                                        try {
                                                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                                                .setTitle(settingData.filter { it -> it.fixedLabelName == "General_OutOfRangeTitle" }[0].labelName)
                                                                .setMessage(settingData.filter { it -> it.fixedLabelName == "General_OutOfRangeMessage" }[0].labelName)
                                                                .sneakWarning()
                                                        } catch (ex: Exception) {
                                                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                                                .setTitle("Out of Range!!")
                                                                .setMessage("Your Current Location is greater than $minDistance meters!")
                                                                .sneakWarning()
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (itemData.VisitStatusID != 0) { // Checkout

                                                    if (CSP.getData("CheckOut_Camera")
                                                            .equals("Y")
                                                    ) {
                                                        CSP.saveData("fragName", "MyCoverage")
                                                        CSP.saveData(
                                                            "sess_store_id",
                                                            itemData.StoreID.toString()
                                                        )
                                                        CSP.saveData(
                                                            "sess_visit_status_id",
                                                            itemData.VisitStatusID.toString()
                                                        )
                                                        CSP.saveData(
                                                            "sess_visit_id",
                                                            itemData.VisitStatusID.toString()
                                                        )
                                                        Navigation.findNavController(it)
                                                            .navigate(R.id.action_myCoverageFragment_to_cameraActivity)
                                                    } else {
                                                        CSP.saveData(
                                                            "sess_visit_status_id",
                                                            itemData.VisitStatusID.toString()
                                                        )
                                                        CSP.saveData(
                                                            "sess_visit_id",
                                                            itemData.VisitStatusID.toString()
                                                        )

                                                        val simpleDateFormat =
                                                            SimpleDateFormat("yyyy-M-d")
                                                        val currentDateAndTime: String =
                                                            simpleDateFormat.format(Date())

                                                        println(
                                                            "${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemData.VisitStatusID}&Longitude=$lng&Latitude=$lat&Remarks=-"
                                                        )

                                                        sendVisitRequest(
                                                            "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                                itemData.StoreID
                                                            }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=${currentDateAndTime}&Longitude=${location.longitude.toString()}&Latitude=${location.latitude.toString()}&Remarks=-"
                                                        )

                                                        sendVisitRequest(
                                                            "${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemData.VisitStatusID}&Longitude=$lng&Latitude=$lat&Remarks=-"
                                                        )
                                                    }
                                                } else { // Checkin
                                                    if (CSP.getData("CheckIn_Camera").equals("Y")) {
                                                        CSP.saveData("fragName", "MyCoverage")
                                                        CSP.saveData(
                                                            "sess_store_id",
                                                            itemData.StoreID.toString()
                                                        )
                                                        CSP.saveData(
                                                            "sess_visit_status_id",
                                                            itemData.VisitStatusID.toString()
                                                        )
                                                        Navigation.findNavController(it)
                                                            .navigate(R.id.action_myCoverageFragment_to_cameraActivity)
                                                    } else {
                                                        CSP.saveData(
                                                            "sess_visit_status_id",
                                                            itemData.VisitStatusID.toString()
                                                        )
                                                        CSP.saveData(
                                                            "sess_visit_id",
                                                            itemData.VisitStatusID.toString()
                                                        )

                                                        val simpleDateFormat =
                                                            SimpleDateFormat("yyyy-M-d")
                                                        val currentDateAndTime: String =
                                                            simpleDateFormat.format(Date())

                                                        println(
                                                            "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                                itemData.StoreID
                                                            }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=${currentDateAndTime}&Longitude=${location.longitude.toString()}&Latitude=${location.latitude.toString()}&Remarks=-"
                                                        )

                                                        sendVisitRequest(
                                                            "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                                itemData.StoreID
                                                            }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=${currentDateAndTime}&Longitude=${location.longitude.toString()}&Latitude=${location.latitude.toString()}&Remarks=-"
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            println("Else")
                                            if (itemData.VisitStatusID != 0) { // Checkout

                                                if (CSP.getData("CheckOut_Camera").equals("Y")) {
                                                    CSP.saveData("fragName", "MyCoverage")
                                                    CSP.saveData(
                                                        "sess_store_id",
                                                        itemData.StoreID.toString()
                                                    )
                                                    CSP.saveData(
                                                        "sess_visit_status_id",
                                                        itemData.VisitStatusID.toString()
                                                    )
                                                    CSP.saveData(
                                                        "sess_visit_id",
                                                        itemData.VisitStatusID.toString()
                                                    )
                                                    Navigation.findNavController(it)
                                                        .navigate(R.id.action_myCoverageFragment_to_cameraActivity)
                                                } else {
                                                    CSP.saveData(
                                                        "sess_visit_status_id",
                                                        itemData.VisitStatusID.toString()
                                                    )
                                                    CSP.saveData(
                                                        "sess_visit_id",
                                                        itemData.VisitStatusID.toString()
                                                    )

                                                    val simpleDateFormat =
                                                        SimpleDateFormat("yyyy-M-d")
                                                    val currentDateAndTime: String =
                                                        simpleDateFormat.format(Date())
                                                    sendVisitRequest(
                                                        "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                            itemData.StoreID
                                                        }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=${currentDateAndTime}&Longitude=$lng&Latitude=$lat&Remarks=-"
                                                    )

                                                    println(
                                                        "${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemData.VisitStatusID}&Longitude=$lng&Latitude=$lat&Remarks=-"
                                                    )

                                                    sendVisitRequest(
                                                        "${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemData.VisitStatusID}&Longitude=$lng&Latitude=$lat&Remarks=-"
                                                    )
                                                }
                                            } else { // Checkin
                                                println("Checkin")
                                                if (CSP.getData("CheckIn_Camera").equals("Y")) {
                                                    CSP.saveData("fragName", "MyCoverage")
                                                    CSP.saveData(
                                                        "sess_store_id",
                                                        itemData.StoreID.toString()
                                                    )
                                                    CSP.saveData(
                                                        "sess_visit_status_id",
                                                        itemData.VisitStatusID.toString()
                                                    )
                                                    Navigation.findNavController(it)
                                                        .navigate(R.id.action_myCoverageFragment_to_cameraActivity)
                                                } else {
                                                    CSP.saveData(
                                                        "sess_visit_status_id",
                                                        itemData.VisitStatusID.toString()
                                                    )
                                                    CSP.saveData(
                                                        "sess_visit_id",
                                                        itemData.VisitStatusID.toString()
                                                    )

                                                    val simpleDateFormat =
                                                        SimpleDateFormat("yyyy-M-d")
                                                    val currentDateAndTime: String =
                                                        simpleDateFormat.format(Date())

                                                    println(
                                                        "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                            itemData.StoreID
                                                        }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=${currentDateAndTime}&Longitude=${location.longitude.toString()}&Latitude=${location.latitude.toString()}&Remarks=-"
                                                    )

                                                    sendVisitRequest(
                                                        "${CSP.getData("base_url")}/StoreVisit.asmx/TeamMemberCheckInDirect?StoreID=${
                                                            itemData.StoreID
                                                        }&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=-&PlanDate=${currentDateAndTime}&Longitude=${location.longitude.toString()}&Latitude=${location.latitude.toString()}&Remarks=-"
                                                    )
                                                }
                                            }
                                        }
                                    } catch (ex: Exception) {
                                        Log.e("Error_", ex.message.toString())
                                    }

                                }
                            }

                    }

                    holder.btnClose.setOnClickListener {
                        holder.fc.toggle(false)
                    }

                    holder.btnCancel.setOnClickListener {

                        val bundle = bundleOf(
                            "StoreID" to itemData.StoreID,
                            "StoreName" to itemData.StoreName
                        )

                        if (CSP.getData("team_type_id")!!.toInt() >= 9) {
                            println("Location: ${uLocation.latitude.toDouble()}-${uLocation.longitude.toDouble()}")
                            val myLocation = Location("")

                            myLocation.latitude = uLocation.latitude.toDouble()
                            myLocation.longitude = uLocation.longitude.toDouble()

                            lat = uLocation.latitude.toString()
                            lng = uLocation.longitude.toString()

                            val storeLocation = Location("")

                            try {
                                storeLocation.latitude = itemData.Longitude.toDouble()
                                storeLocation.longitude = itemData.Latitude.toDouble()
                            } catch (ex: Exception) {
                                storeLocation.latitude = 0.0
                                storeLocation.longitude = 0.0
                            }

                            val distanceInMeters: Float = myLocation.distanceTo(storeLocation)
                            println("distanceInMeters: ${distanceInMeters} Location: $lat - $lng")

                            var minDistance = CSP.getData("LocationLimit")?.toDouble()
                            println("minDistance: ${minDistance}")
                            if (minDistance!! >= 0) {
                                if (distanceInMeters >= minDistance) {
                                    println("distanceInMeters: Distance is greater")
                                    (context as Activity).runOnUiThread {
                                        context?.let { it1 ->
                                            try {
                                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                                    .setTitle(settingData.filter { it -> it.fixedLabelName == "General_OutOfRangeTitle" }[0].labelName)
                                                    .setMessage(settingData.filter { it -> it.fixedLabelName == "General_OutOfRangeMessage" }[0].labelName)
                                                    .sneakWarning()
                                            } catch (ex: Exception) {
                                                Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                                    .setTitle("Out of Range!!")
                                                    .setMessage("Your Current Location is greater than $minDistance meters!")
                                                    .sneakWarning()
                                            }
                                        }
                                    }
                                } else {
                                    Navigation.findNavController(it)
                                        .navigate(
                                            R.id.action_myCoverageFragment_to_trainingFragment,
                                            bundle
                                        )
                                }
                            }
                        } else {
                            Navigation.findNavController(it)
                                .navigate(
                                    R.id.action_myCoverageFragment_to_storeViewFragment,
                                    bundle
                                )
                        }
                    }
                }
            }

            holder.btnSee.setOnClickListener {
                holder.fc.toggle(false)

                context?.let {
                    holder.item?.let { itemData ->
                        holder.bind(itemData, it,
                            OnMapReadyCallback {
                                //holder.mapView
                                println("OnMapReadyCallback")
                                try {
                                    if (itemData.Latitude != "" && itemData.Longitude != "") {
                                        val latLng: LatLng = LatLng(
                                            itemData.Longitude.toDouble(),
                                            itemData.Latitude.toDouble()
                                        )
                                        it.addMarker(
                                            MarkerOptions().position(latLng)
                                                .title(itemData.StoreName)
                                        )
                                        it.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                latLng,
                                                17.0f
                                            )
                                        )
                                        it!!.setOnMapClickListener {
                                            try {
                                                val uri =
                                                    "https://maps.google.com/maps?daddr=${itemData.Longitude},${itemData.Latitude}"
                                                val intent =
                                                    Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                                intent.setPackage("com.google.android.apps.maps")
                                                context.startActivity(intent)
                                            } catch (ex: Exception) {

                                            }
                                        }
                                    }
                                } catch (ex: Exception) {
                                    Log.e("Error_", ex.message.toString())
                                }
                            })
                    }
                }
            }
        } catch (ex: Exception) {
            (context as Activity).runOnUiThread {
                context?.let { it1 ->
                    Sneaker.with(it1) // Activity, Fragment or ViewGroup
                        .setTitle("Warning")
                        .setMessage(settingData.filter { it.fixedLabelName == "Dashboard_GPSLocatingMessage" }[0].labelName)
                        .sneakWarning()

                }
            }
        }
    }


    private fun sendVisitRequest(url: String) {

        var checkType =
            if (CSP.getData("sess_visit_status_id")
                    .equals("0")
            ) "CheckInImg" else "CheckOutImage"


        println(checkType)

        val client = OkHttpClient()

        try {
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "test",
                        "test"
                    )
                    .build()

            val request: Request = Request.Builder()
                .url(url)
                .post(requestBody)
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
                    println(apiData)
                    if (apiData.status == 200) {
                        (context as Activity).runOnUiThread {
                            context?.let { it1 ->
                                Sneaker.with(it1) //Activity, Fragment or ViewGroup
                                    .setTitle("Success!!")
                                    .setMessage("Data Updated")
                                    .sneakSuccess()
                                frag.reloadCoverage()
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
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.e("File upload", "failed")
        }

        /*val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (context as Activity).runOnUiThread {
                    context?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Error!!")
                            .setMessage(e.message.toString())
                            .sneakError()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()
                val apiData = gson.fromJson(body, MyCoverageModel::class.java)
                println(apiData.status)
                if (apiData.status == 200) {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Success")
                                .setMessage("Visit added in your Journey.")
                                .sneakSuccess()
                        }
                    }
                } else {
                    (context as Activity).runOnUiThread {
                        context?.let { it1 ->
                            Sneaker.with(it1) // Activity, Fragment or ViewGroup
                                .setTitle("Error!!")
                                .setMessage("Data not fetched.")
                                .sneakWarning()
                        }
                    }
                }
            }
        })*/
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<MyCoverageData>
                } else {
                    //if (filterList.size > 0) {
                    val resultList = ArrayList<MyCoverageData>()
                    for (row in itemList) {
                        if (row.StoreName.toLowerCase().contains(
                                constraint.toString().toLowerCase()
                            ) || row.StoreCode.toLowerCase()
                                .contains(constraint.toString().toLowerCase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
                    //}
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterList = results?.values as ArrayList<MyCoverageData>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun getItemViewType(position: Int): Int = position
}