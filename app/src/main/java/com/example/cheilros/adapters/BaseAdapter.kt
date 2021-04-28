package com.example.cheilros.adapters

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.SpinnerAdapter

abstract class BaseAdapter : ListAdapter, SpinnerAdapter {
    private val mDataSetObservable = DataSetObservable()
    override fun hasStableIds(): Boolean {
        return false
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver) {
        mDataSetObservable.unregisterObserver(observer)
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    open fun notifyDataSetChanged() {
        mDataSetObservable.notifyChanged()
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    open fun notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated()
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return count == 0
    }
}