package com.example.cheilros.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.data.AppSetting
import com.example.cheilros.fragments.MyCoverageFragment
import com.example.cheilros.fragments.training.TrainingStoresFragment
import com.example.cheilros.helpers.CustomSharedPref
import com.example.cheilros.models.CheckInOutModel
import com.example.cheilros.models.MyCoverageData
import com.example.cheilros.models.SelectedMyCoverageData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.irozon.sneaker.Sneaker
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.fragment_training_stores.*
import kotlinx.android.synthetic.main.mycoverage_content_cell.view.*
import kotlinx.android.synthetic.main.mycoverage_title_cell.view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TrainingStoresAdapter(
    val context: Context,
    val itemList: List<MyCoverageData>,
    val settingData: List<AppSetting>,
    val latitude: String,
    val longitude: String,
    fragment: TrainingStoresFragment,
    val activity: NewDashboardActivity
) : RecyclerView.Adapter<TrainingStoresAdapter.ViewHolder>(), Filterable {

    var onClick: AdapterView.OnItemClickListener? = null

    fun setOnItemClickLitener(mOnItemClickLitener: AdapterView.OnItemClickListener?) {
        this.onClick = mOnItemClickLitener
    }

    interface OnItemClickListener {
        fun onItemClick(bean: SelectedMyCoverageData)
    }

    lateinit var CSP: CustomSharedPref
    private val client = OkHttpClient()
    lateinit var locationManager: LocationManager
    //val selectStores= mutableListOf<SelectedMyCoverageData>()
    var curPos: Int = 0
    var lat: String = "0"
    var lng: String = "0"

    //var minDistance = 750.0
    private val frag: TrainingStoresFragment

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
        var LLjp: LinearLayout = view.findViewById(R.id.LLjp)
        var imgArrowDown: ImageView = view.findViewById(R.id.imgArrowDown)
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


        return ViewHolder(view, parent.context)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filterList[position]
        holder.item = item
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            val uLocation = activity.userLocation
            context?.let {
                holder.item?.let { itemData ->

                    holder.imgArrowDown.visibility = View.INVISIBLE

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

                    val isSelected = frag.selectStores.filter { it.StoreID == itemData.StoreID }
                    if(isSelected.size == 1)
                        holder.RLnum.setBackgroundColor(Color.YELLOW)

                    holder.LLjp.setOnClickListener {
                        val isSelected = frag.selectStores.filter { it.StoreID == itemData.StoreID }
                        if(isSelected.size == 0){
                            frag.selectStores.add(SelectedMyCoverageData(itemData.StoreID, itemData.StoreCode, itemData.StoreName, true))
                            holder.RLnum.setBackgroundColor(Color.YELLOW)
                        }else{
                            val index = frag.selectStores.indexOf(frag.selectStores.find { it.StoreID == itemData.StoreID })
                            frag.selectStores.removeAt(index)
                            holder.RLnum.setBackgroundColor(Color.parseColor("#534f47"))
                        }
                        println("isSelected: $isSelected")

                    }

                    frag.btnSubmit.setOnClickListener {
                        if(frag.selectStores.size > 0){
                            var selectedStores = mutableListOf<String>()
                            for(store in frag.selectStores){
                                println("store: ${store.StoreName}")
                                selectedStores.add(store.StoreID.toString())
                            }
                            println("selectedStores: ${selectedStores.joinToString().trim()}")
                            CSP.saveData("selectedStores", selectedStores.joinToString().trim())
                            val bundle = bundleOf(
                                "StoreID" to selectedStores.joinToString().trim(),
                            )
                            Navigation.findNavController(it)
                                .navigate(R.id.action_trainingStoresFragment_to_trainingNewFragment, bundle)

                        }
                    }

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


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filterList = itemList as ArrayList<MyCoverageData>
                } else {
                    if (filterList.size > 0) {
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
                    }
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