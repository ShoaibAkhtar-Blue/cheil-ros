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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.JourneyPlanFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.JourneyPlanData
import com.example.cheilros.models.MyCoverageData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.dialog_add_visit.*
import kotlinx.android.synthetic.main.fragment_journey_plan.*
import kotlinx.android.synthetic.main.mycoverage_content_cell.view.*
import okhttp3.*
import java.io.IOException


class JPAdapter(
    val context: Context,
    val itemList: List<JourneyPlanData>,
    fragment: JourneyPlanFragment,
    isCurrentDate: Boolean,
    val settingData: List<AppSetting>,
    val activity: NewDashboardActivity
) : RecyclerView.Adapter<JPAdapter.ViewHolder>() {

    lateinit var CSP: CustomSharedPref


    private val client = OkHttpClient()
    var ctx: Context? = null

    lateinit var locationManager: LocationManager

    private val frag: JourneyPlanFragment
    private val isCurrDt: Boolean

    /*var lat: String = "0"
    var lng: String = "0"*/
    //var minDistance = 750.0

    init {
        frag = fragment
        isCurrDt = isCurrentDate
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var item: JourneyPlanData? = null
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

        fun bind(journeyData: JourneyPlanData, mContext: Context, callback: OnMapReadyCallback){
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



        var imgStatus: ImageView = view.findViewById(R.id.imgStatus)
        var txtCode: TextView = view.findViewById(R.id.txtCode)
        //var mapView: MapView = view.findViewById(R.id.mapView)
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

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.item = item
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)

        context?.let {
            holder.item?.let { itemData ->

                holder.txtCode.text = itemData.StoreCode
                holder.txtCode.visibility = View.GONE

                holder.txtTitle.text = itemData.StoreName
                holder.txtTitleHeader.text =
                    "${itemData.StoreCode} - ${itemData.StoreName}"
                holder.txtTitleContent.text = itemData.Address

                holder.txtRemarks.text = itemData.VisitRemarks
                holder.txtRemarksContent.text = itemData.VisitRemarks

                holder.txtTime.text =
                    "\uD83D\uDD65 In: ${itemData.CheckInTime} | \uD83D\uDD65 Out: ${itemData.CheckOutTime}"

                Glide.with(context).load(itemData.ImageLocation).into(holder.imgStatus!!)

                if (itemData.VisitRemarks == null)
                    holder.txtRemarks.text = "No Remarks"

                holder.btnCancel.text =
                    settingData.filter { it.fixedLabelName == "JuorneyPlan_CancelButton" }.get(0).labelName
                holder.btnAccept.text =
                    settingData.filter { it.fixedLabelName == "JourneyPlan_CheckinButton" }.get(0).labelName

                if (itemData.VisitStatusID === 1) {
                    holder.RLStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.status_none))
                    holder.RLHeader.setBackgroundColor(ContextCompat.getColor(context, R.color.status_none))
                    holder.txtTime.visibility = View.GONE

                } else if (itemData.VisitStatusID === 2) {
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
                } else if (itemData.VisitStatusID === 3) {
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

                } else if (itemData.VisitStatusID === 4) {
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
                }

                if (!isCurrDt) {
                    holder.btnAccept.setTextColor(Color.GRAY)
                    holder.btnCancel.setTextColor(Color.GRAY)

                    holder.btnAccept.isEnabled = false
                    holder.btnAccept.isClickable = false

                    holder.btnCancel.isEnabled = false
                    holder.btnCancel.isClickable = false
                }

                /*holder.btnSee.setOnClickListener {
                    holder.fc.toggle(false)
                }*/

                holder.btnClose.setOnClickListener {
                    holder.fc.toggle(false)
                }

                holder.btnAccept.setOnClickListener {
                    try {
                        val uLocation =  activity.userLocation
                        println("Location: ${uLocation.latitude.toDouble()}-${uLocation.longitude.toDouble()}")

                        var lat: String = uLocation.latitude.toString()
                        var lng: String = uLocation.longitude.toString()

                        val myLocation = Location("")

                        myLocation.latitude = uLocation.latitude.toDouble()
                        myLocation.longitude = uLocation.longitude.toDouble()

                        val storeLocation = Location("")
                        try {
                            storeLocation.latitude = itemData.Longitude.toDouble()
                            storeLocation.longitude = itemData.Latitude.toDouble()
                        } catch (ex: Exception) {
                            storeLocation.latitude = 0.0
                            storeLocation.longitude = 0.0
                        }

                        val distanceInMeters: Float = myLocation.distanceTo(storeLocation)
                        println("distanceInMeters: ${distanceInMeters}")


                        var minDistance = CSP.getData("LocationLimit")?.toDouble()

                        if (minDistance!! >= 0) {
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
                                if(itemData.VisitStatusID === 1){
                                    if(CSP.getData("CheckIn_Camera").equals("Y")){
                                        CSP.saveData("sess_visit_id", itemData.VisitID.toString())
                                        CSP.saveData("sess_visit_status_id", itemData.VisitStatusID.toString())
                                        CSP.saveData("fragName", "JP")

                                        Navigation.findNavController(it).navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                                    }else{
                                        sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckIn?VisitID=${itemData.VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                                    }
                                }

                                if(itemData.VisitStatusID === 2){
                                    if(CSP.getData("CheckOut_Camera").equals("Y")){
                                        CSP.saveData("sess_visit_id", itemData.VisitID.toString())
                                        CSP.saveData("sess_visit_status_id", itemData.VisitStatusID.toString())
                                        CSP.saveData("fragName", "JP")
                                        Navigation.findNavController(it).navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                                    }else{
                                        sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemData.VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                                    }
                                }
                            }
                        }else{
                            if (itemData.VisitStatusID === 1) {
                                if (CSP.getData("CheckIn_Camera").equals("Y")) {
                                    CSP.saveData("sess_visit_id", itemData.VisitID.toString())
                                    CSP.saveData(
                                        "sess_visit_status_id",
                                        itemData.VisitStatusID.toString()
                                    )
                                    CSP.saveData("fragName", "JP")

                                    Navigation.findNavController(it)
                                        .navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                                } else {
                                    sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckIn?VisitID=${itemData.VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                                }
                            }

                            if (itemData.VisitStatusID === 2) {
                                if (CSP.getData("CheckOut_Camera").equals("Y")) {
                                    CSP.saveData("sess_visit_id", itemData.VisitID.toString())
                                    CSP.saveData(
                                        "sess_visit_status_id",
                                        itemData.VisitStatusID.toString()
                                    )
                                    CSP.saveData("fragName", "JP")
                                    Navigation.findNavController(it)
                                        .navigate(R.id.action_journeyPlanFragment_to_cameraActivity)
                                } else {
                                    sendCheckInOutRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/CheckOut?VisitID=${itemData.VisitID}&Longitude=$lng&Latitude=$lat&Remarks=-")
                                }
                            }
                        }

                    } catch (ex: Exception) {

                    }
                }

                holder.btnCancel.setOnClickListener {
                    if (itemData.VisitStatusID === 1) {

                        val uLocation =  activity.userLocation
                        println("Location: ${uLocation.latitude.toDouble()}-${uLocation.longitude.toDouble()}")

                        if (CSP.getData("CancelVisit").equals("Y")) {
                            var lat: String = uLocation.latitude.toString()
                            var lng: String = uLocation.longitude.toString()

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
                                cancelJP("${CSP.getData("base_url")}/JourneyPlan.asmx/CancelVisit?VisitID=${itemData.VisitID}&Longitude=$lng&Latitude=$lat&Remarks=${dialog.etRemarks.text}")
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
                            "StoreID" to itemData.StoreID,
                            "StoreName" to itemData.StoreName
                        )
                        Navigation.findNavController(it)
                            .navigate(R.id.action_journeyPlanFragment_to_storeViewFragment, bundle)
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
                                        MarkerOptions().position(latLng).title(itemData.StoreName)
                                    )
                                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f))
                                    it!!.setOnMapClickListener {
                                        try {
                                            val uri =
                                                "https://maps.google.com/maps?daddr=${itemData.Longitude},${itemData.Latitude}"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
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

    }

    override fun getItemCount(): Int {
        return itemList.size
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
