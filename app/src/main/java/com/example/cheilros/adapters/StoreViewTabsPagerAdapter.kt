package com.example.cheilros.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE
import androidx.viewpager2.adapter.FragmentStateAdapter


class StoreViewTabsPagerAdapter(
    val fm: FragmentManager,
    lifecycle: Lifecycle,
    private var numberOfTabs: Int,
    val arguments: Bundle?,
    val fr_list: MutableList<Fragment>,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT: Int
) : FragmentStateAdapter(fm, lifecycle) {


    override fun createFragment(position: Int): Fragment {
        /*when (position) {
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
            else -> return StoreStatusFragment(
                arguments?.getInt("StoreID")?.let { bundle.putInt("StoreID", it) },
                arguments?.getString("StoreName")?.let { bundle.putString("StoreName", it) })
        }*/

        return fr_list[position]
    }

    fun getItemPosition(`object`: Any?): Int {
        return POSITION_NONE
    }

    override fun getItemCount(): Int {
        return fr_list.size
    }

    fun refresh() {
        /*val ft: FragmentTransaction = fm.beginTransaction()
        ft.detach(StoreStatusFragment()).attach(StoreStatusFragment()).commit()*/
    }
}