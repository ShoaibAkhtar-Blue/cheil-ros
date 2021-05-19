package com.example.cheilros.adapters

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.datavm.AppSettingViewModel
import com.example.cheilros.datavm.UserPermissionViewModel
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.MyCoverageData
import com.example.cheilros.models.MyCoverageModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.dialog_add_visit.*
import okhttp3.*
import java.io.IOException


class MyCoverageAdapter(val context: Context, val itemList: List<MyCoverageData>): RecyclerView.Adapter<MyCoverageAdapter.ViewHolder>(),
    OnMapReadyCallback {

    lateinit var CSP: CustomSharedPref
    private val client = OkHttpClient()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtSerialNo: TextView = view.findViewById(R.id.txtSerialNo)
        var mapView : MapView = view.findViewById(R.id.mapView)
        var txtTitle : TextView = view.findViewById(R.id.txtTitle)
        var txtTitleHeader : TextView = view.findViewById(R.id.txtTitleHeader)
        var txtTitleContent : TextView = view.findViewById(R.id.txtTitleContent)
        var txtRegion: TextView = view.findViewById(R.id.txtRegion)
        var txtAddress: TextView = view.findViewById(R.id.txtAddress)
        var fc : FoldingCell = view.findViewById(R.id.folding_cell)
        var btnSee  : LinearLayout = view.findViewById(R.id.LLjp)
        var btnClose  : RelativeLayout = view.findViewById(R.id.RLHeader)
        var btnAccept  : Button = view.findViewById(R.id.btnAccept)
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

        holder.txtTitle.text = itemList[position].StoreName
        holder.txtTitleHeader.text = itemList[position].StoreName
        holder.txtTitleContent.text = itemList[position].StoreName

        holder.txtRegion.text = itemList[position].RegionName
        holder.txtAddress.text = itemList[position].Address

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

        holder.btnAccept.setOnClickListener {

            if(CSP.getData("AddVisit").equals("Y")){
                val li = LayoutInflater.from(context)
                val promptsView: View = li.inflate(R.layout.dialog_add_visit, null)

                val dialog = Dialog(context)
                dialog.setContentView(promptsView)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(true)

                dialog.txtTitle.text = "Add Visit Plan"
                dialog.txtQuestion.text = "Do you want to add Visit Plan for ${itemList[position].StoreName} in your journey plan?"
                dialog.btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.btnAccept.setOnClickListener {
                    sendVisitRequest("${CSP.getData("base_url")}/JourneyPlan.asmx/TeamMemberAddPlan?StoreID=${itemList[position].StoreID}&TeamMemberID=${CSP.getData("user_id")}&PlanRemarks=${dialog.etRemarks.text}")
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
            }
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
}