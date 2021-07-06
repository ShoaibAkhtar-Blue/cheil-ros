package com.example.cheilros.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cheilros.fragments.storeview.StoreActiveAssetsFragment
import com.example.cheilros.fragments.storeview.StoreStatusFragment


class StoreViewTabsPagerAdapter(
    val fm: FragmentManager,
    lifecycle: Lifecycle,
    private var numberOfTabs: Int,
    val arguments: Bundle?
) : FragmentStateAdapter(fm, lifecycle) {



    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                val bundle = Bundle()
                arguments?.getInt("StoreID")?.let { bundle.putInt("StoreID", it) }
                arguments?.getString("StoreName")?.let { bundle.putString("StoreName", it) }
                val storeStatusFragment = StoreStatusFragment()
                storeStatusFragment.arguments = bundle
                return storeStatusFragment
            }
            1 -> {
                val bundle = Bundle()
                arguments?.getInt("StoreID")?.let { bundle.putInt("StoreID", it) }
                arguments?.getString("StoreName")?.let { bundle.putString("StoreName", it) }
                val storeActiveAssetsFragment = StoreActiveAssetsFragment()
                storeActiveAssetsFragment.arguments = bundle
                return storeActiveAssetsFragment
            }
            else -> return StoreStatusFragment()
        }
    }

    fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }

    fun refresh(){
        /*val ft: FragmentTransaction = fm.beginTransaction()
        ft.detach(StoreStatusFragment()).attach(StoreStatusFragment()).commit()*/
    }
}