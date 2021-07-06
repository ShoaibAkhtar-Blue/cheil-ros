package com.example.cheilros.adapters

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.data.AppSetting
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.MyCoverageData
import com.example.cheilros.models.MyCoverageModel
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
import kotlinx.android.synthetic.main.dialog_add_visit.btnAccept
import kotlinx.android.synthetic.main.dialog_add_visit.btnCancel
import kotlinx.android.synthetic.main.dialog_add_visit.btnDate
import kotlinx.android.synthetic.main.dialog_add_visit.txtQuestion
import kotlinx.android.synthetic.main.dialog_add_visit.txtTitle
import kotlinx.android.synthetic.main.dialog_checklist.*
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
    val longitude: String
): RecyclerView.Adapter<MyCoverageAdapter.ViewHolder>(),
    OnMapReadyCallback, Filterable {


    lateinit var CSP: CustomSharedPref
    private val client = OkHttpClient()
    lateinit var locationManager: LocationManager

    var curPos: Int = 0

    var filterList = ArrayList<MyCoverageData>()

    init {
        filterList = itemList as ArrayList<MyCoverageData>
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
        var mapView : MapView = view.findViewById(R.id.mapView)
        var txtCode : TextView = view.findViewById(R.id.txtCode)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
        var txtTitleHeader : TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleContent : TextView = view.findViewById(R.id.txtTitleContent)
        var txtRemarksContent : TextView = view.findViewById(R.id.txtRemarksContent)
        var txtRegion: TextView = view.findViewById(R.id.txtRegion)
        var txtAddress: TextView = view.findViewById(R.id.txtAddress)
        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)
        var btnAccept  : Button = view.findViewById(R.id.btnAccept)
        var btnCancel  : Button = view.findViewById(R.id.btnCancel)
        //var btnClose  : MaterialButton = view.findViewById(R.id.btnCancel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(
            R.layout.mycoverage_cell,
            parent,
            false
        )
        CSP = CustomSharedPref(parent.context)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtSerialNo.text = (position+1).toString()

        holder.txtCode.text = filterList[position].StoreCode
        holder.txtTitle.text = filterList[position].StoreName
        holder.txtTitleHeader.text = "${filterList[position].StoreCode} - ${filterList[position].StoreName}"
        holder.txtTitleContent.text = filterList[position].Address
        holder.txtRemarksContent.text = "Last Visit: ${filterList[position].LastVisitedDate} (${filterList[position].VistedBy})"

        holder.txtRegion.text = filterList[position].RegionName
        holder.txtAddress.text = filterList[position].Address

        val mapView: MapView = holder.mapView

        mapView.setOnClickListener {
            println("mapview")
        }

        //Update Labels
        try {
            if(filterList[position].VisitStatusID == 2)
                holder.btnAccept.text = settingData.filter { it.fixedLabelName == "JourneyPlan_CheckoutButton" }.get(0).labelName
            else
                holder.btnAccept.text = settingData.filter { it.fixedLabelName == "JourneyPlan_CheckinButton" }.get(0).labelName

            holder.btnCancel.text = settingData.filter { it.fixedLabelName == "StoreList_ViewButton" }.get(0).labelName
        }catch (ex: Exception){

        }

        if (mapView != null) {
            // Initialise the MapView
            mapView.onCreate(null)
            mapView.onResume();  //Probably U r missing this
            // Set the map ready callback to receive the GoogleMap object
            mapView.getMapAsync(this)
        }

        holder.btnSee.setOnClickListener {
            curPos = position
            holder.fc.toggle(false)
        }

        holder.btnClose.setOnClickListener {
            holder.fc.toggle(false)
        }

        holder.btnCancel.setOnClickListener {
            val bundle = bundleOf("StoreID" to filterList[position].StoreID, "StoreName" to filterList[position].StoreName)
            Navigation.findNavController(it).navigate(R.id.action_myCoverageFragment_to_storeViewFragment, bundle)
        }

        holder.btnAccept.setOnClickListener {

            /*if(CSP.getData("AddVisit").equals("Y")){

                val simpleDateFormat = SimpleDateFormat("yyyy-M-d")
                val currentDateAndTime: String = simpleDateFormat.format(Date())

                val li = LayoutInflater.from(context)
                val promptsView: View = li.inflate(R.layout.dialog_add_visit, null)

                val dialog = Dialog(context)
                dialog.setContentView(promptsView)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(true)

                dialog.txtTitle.text = settingData.filter { it.fixedLabelName == "StoreList_AddJPButton" }.get(0).labelName
                dialog.txtQuestion.text = settingData.filter { it.fixedLabelName == "JounreyPlan_AddMsg" }.get(0).labelName

                dialog.btnDate.visibility = View.VISIBLE
                dialog.btnDate.text = currentDateAndTime

                dialog.btnDate.setOnClickListener {
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    val datePickerDialog =
                        DatePickerDialog(
                            context, DatePickerDialog.OnDateSetListener
                            { view, year, monthOfYear, dayOfMonth ->
                                val currentDate: String = "$year-${(monthOfYear + 1)}-$dayOfMonth"
                                dialog.btnDate.text = currentDate
                            }, year, month, day
                        )
                    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                    datePickerDialog.show()
                }

                dialog.btnCancel.text = settingData.filter { it.fixedLabelName == "StoreList_PopupCancel" }.get(0).labelName
                dialog.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.btnAccept.text = settingData.filter { it.fixedLabelName == "StoreList_PopupAdd" }.get(0).labelName
                dialog.btnAccept.setOnClickListener {
                    sendVisitRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamMemberAddPlan?StoreID=${filterList[position].StoreID}&TeamMemberID=${CSP.getData("user_id")}&PlanDate=${dialog.btnDate.text}&PlanRemarks=${dialog.etRemarks.text}")
                    dialog.dismiss()
                    holder.fc.toggle(false)
                }

                dialog.show()
            }else{
                (context as Activity).runOnUiThread {
                    context?.let { it1 ->
                        Sneaker.with(it1) // Activity, Fragment or ViewGroup
                            .setTitle("Permission!!")
                            .setMessage("You Don't have permission rights for this action!")
                            .sneakWarning()
                    }
                }
            }*/


            //region Checkin
            var lat: String = "0"
            var lng: String = "0"

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
                return@setOnClickListener
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000, 0F,object :
                LocationListener {
                override fun onLocationChanged(location: Location) {
                    lat = location.latitude.toString()
                    lng = location.longitude.toString()
                    println("loc: ${location.latitude}")
                }

            })

            if(filterList[position].VisitStatusID == 2){ // Checkout

                if(CSP.getData("CheckOut_Camera").equals("Y")){
                    CSP.saveData("fragName", "MyCoverage")
                    CSP.saveData("sess_store_id", filterList[position].StoreID.toString())
                    Navigation.findNavController(it).navigate(R.id.action_myCoverageFragment_to_cameraActivity)
                }
            }else{ // Checkin
                if(CSP.getData("CheckIn_Camera").equals("Y")){
                    CSP.saveData("fragName", "MyCoverage")
                    CSP.saveData("sess_store_id", filterList[position].StoreID.toString())
                    Navigation.findNavController(it).navigate(R.id.action_myCoverageFragment_to_cameraActivity)
                }
            }




            //endregion

        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        try {
            //val myLocation: Location = googleMap!!.myLocation

            println("onMapReady-${itemList[curPos].Latitude}-${itemList[curPos].Longitude}")
            val sydney = LatLng(itemList[curPos].Latitude.toDouble(), itemList[curPos].Longitude.toDouble())
            googleMap!!.addMarker(MarkerOptions().position(sydney).title(itemList[curPos].StoreName))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20.5f))

            googleMap!!.setOnMapClickListener {
                try {
                    val uri =
                        "http://maps.google.com/maps?daddr=${itemList[curPos].Latitude},${itemList[curPos].Longitude}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps")
                    context.startActivity(intent)
                }catch (ex: Exception){

                }
            }

        }catch (ex: Exception){
            val sydney = LatLng(0.0, 0.0)
            googleMap!!.addMarker(MarkerOptions().position(sydney).title(itemList[curPos].StoreName))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20.5f))
        }


    }



    private fun sendVisitRequest(url: String) {
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
        })
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<MyCoverageData>
                } else {
                    val resultList = ArrayList<MyCoverageData>()
                    for (row in itemList) {
                        if (row.StoreName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    filterList = resultList
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
}