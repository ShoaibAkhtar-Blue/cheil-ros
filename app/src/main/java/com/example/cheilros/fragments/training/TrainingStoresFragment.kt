package com.example.cheilros.fragments.training

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cheilros.R
import com.example.cheilros.activities.NewDashboardActivity
import com.example.cheilros.activities.customobj.EmptyRecyclerView
import com.example.cheilros.adapters.MyCoverageAdapter
import com.example.cheilros.fragments.BaseFragment
import com.example.cheilros.models.ChannelData
import com.example.cheilros.models.ChannelTypeData
import okhttp3.OkHttpClient


class TrainingStoresFragment : BaseFragment() {

    lateinit var activity: NewDashboardActivity
    lateinit var uLocation: Location
    private val client = OkHttpClient()

    lateinit var recyclerView: EmptyRecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var channelData: List<ChannelData>
    lateinit var channelTypeData: List<ChannelTypeData>

    var defaultChannel = "0"
    var defaultChannelType = "0"

    lateinit var recylcerAdapter: MyCoverageAdapter

    lateinit var locationManager: LocationManager

    var latitude: String = "0.0"
    var longitude: String = "0.0"

    var isLoc: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity() as NewDashboardActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_training_stores, container, false)

        toolbarVisibility(false)

        return view
    }


}