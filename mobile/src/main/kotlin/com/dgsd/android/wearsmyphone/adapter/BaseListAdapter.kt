package com.dgsd.android.wearsmyphone.adapter

import android.widget.BaseAdapter

/**
 * Base class for showing a list of items in an {@link android.widget.AdapterView}
 *
 * @param <DataType> The model type to display
 */
public abstract class BaseListAdapter<DataType> : BaseAdapter() {

    private var items: List<DataType>? = null

    override fun getCount(): Int {
        return if (items == null) 0 else items!!.size()
    }

    override fun getItem(position: Int): DataType {
        return items!!.get(position)
    }

    /**
     * Reload the adapter with the given items
     *
     * @param items The items to show in the adapter
     */
    public fun populate(items: List<DataType>?) {
        this.items = items
        notifyDataSetChanged()
    }
}