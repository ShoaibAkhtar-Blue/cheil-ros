package com.example.cheilros.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

/*
class RecyclerViewLiteModeAdapter(addressModelList: List<AddressModel>?, var mContext: Context) :
    RecyclerView.Adapter<RecyclerViewLiteModeAdapter.ViewHolder?>() {
    var layoutinflater: LayoutInflater
    private var addressModels: List<AddressModel>? = ArrayList<AddressModel>()
    private val mActivity: Activity
    private val lastPosition = -1
    private val mAddressModel: AddressModel? = null
    var previousPosition = 0
    fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemLayoutView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lite_mode_layout, parent, false)
        return ViewHolder(itemLayoutView)
    }

    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initializeMapView(position, addressModels!![position])
        holder.tvTitle.setText(addressModels[position].getLine1())
        holder.tvCategories.setText(addressModels[position].getLine2())
        holder.tvOffers.setText(addressModels[position].getCity())
        holder.tvDistanceInKm.setText(addressModels[position].getDistance())
        holder.tvRating.setText(addressModels[position].getRating())
        if (position > previousPosition) {
            MyAnimationUtils().animate(holder, true)
        } else {
            MyAnimationUtils().animate(holder, false)
        }
        previousPosition = position
    }

    val itemCount: Int
        get() = addressModels?.size ?: 0

    class ViewHolder(itemLayoutView: View) : RecyclerView.ViewHolder(itemLayoutView),
        OnMapReadyCallback {
        var tvTitle: TextView
        var tvRating: TextView
        var tvDistanceInKm: TextView
        var tvOffers: TextView
        var tvLocation: TextView
        var tvCategories: TextView
        var ivMain: ImageView
        var mBook: Button
        var mPay: Button
        private val mItemLayout: LinearLayout? = null
        var map: GoogleMap? = null
        var mapView: MapView?
        var isMapReady = false
        var mapInterface: MapInterface? = null
        var position = 0
        var addressModel: AddressModel? = null
        override fun onMapReady(googleMap: GoogleMap) {
            MapsInitializer.initialize(mContext)
            map = googleMap
            isMapReady = true
            map!!.uiSettings.isMapToolbarEnabled = false
            if (mapView!!.viewTreeObserver.isAlive) {
                mapView!!.viewTreeObserver.addOnGlobalLayoutListener(object :
                    OnGlobalLayoutListener {
                    // We use the new method when supported
                    @SuppressLint("NewApi") // We check which build version we are using.
                    override fun onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView!!.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        } else {
                            mapView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                        //  showBoth(null);
                    }
                })
            }
            try {
                setMapData(
                    LatLng(
                        addressModel.getLatitude().toDouble(),
                        addressModel.getLongitude().toDouble()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }


            //  mapInterface.mapIsReadyToSet(this,position);
        }

        fun setMapData(newLatLng: LatLng?) {
            if (map != null) {
                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 13f))
                map!!.addMarker(MarkerOptions().position(newLatLng!!))
                map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            } else {
                Log.e("Map", "Map null")
            }
        }

        fun initializeMapView(pos: Int, address: AddressModel?) {
            try {
                position = pos
                addressModel = address
                if (mapView != null) {
                    // Initialise the MapView
                    mapView!!.onCreate(null)
                    // Set the map ready callback to receive the GoogleMap object
                    mapView!!.getMapAsync(this)
                } else {
                    Log.e("Map", "Mapview null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        interface MapInterface {
            fun mapIsReadyToSet(holder: ViewHolder?, pos: Int)
        }

        init {
            mapView = itemLayoutView.findViewById<View>(TextView) as MapView
            tvTitle = itemLayoutView.findViewById<View>(R.id.tv_title) as TextView
            tvLocation = itemLayoutView.findViewById<View>(R.id.tv_location) as TextView
            tvCategories = itemLayoutView.findViewById<View>(R.id.tv_categories) as TextView
            tvRating = itemLayoutView.findViewById<View>(R.id.tv_rating) as TextView
            tvDistanceInKm = itemLayoutView.findViewById<View>(R.id.tv_distanceinkm) as TextView
            tvOffers = itemLayoutView.findViewById<View>(R.id.tv_offers) as TextView
            ivMain = itemLayoutView.findViewById<View>(R.id.iv_main) as ImageView
            mBook = itemLayoutView.findViewById<View>(R.id.btn_book) as Button
            mPay = itemLayoutView.findViewById<View>(R.id.btn_pay) as Button
        }
    }

    companion object {
        private val lastSelectedViewHolder: ViewHolder? = null
    }

    init {
        addressModels = addressModelList
        layoutinflater = LayoutInflater.from(mContext)
        mActivity = mContext as Activity
    }
}*/
