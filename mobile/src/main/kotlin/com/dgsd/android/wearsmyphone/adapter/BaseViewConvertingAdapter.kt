package com.dgsd.android.wearsmyphone.adapter

import android.view.View
import android.view.ViewGroup

/**
 * Adapter which shows a list of items and adds view recycling
 *
 * @param <DataType> The model type to display in the adapter
 * @param <ViewType> The type of view to display in each item
 */
public abstract class BaseViewConvertingAdapter<DataType, ViewType : View> : BaseListAdapter<DataType>() {

    /**
     * Instantiate a new view for display
     *
     * @param parent The parent {@link android.view.ViewGroup} where the view will be placed
     * @return A view to show in the adapter
     */
    protected abstract fun createView(parent: ViewGroup): ViewType

    /**
     * Populates the view with a specific {@link DataType}
     *
     * @param data The item to display
     * @param view The view to display the item in
     */
    protected abstract fun populateView(data: DataType, view: ViewType)

    SuppressWarnings("unchecked")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: ViewType
        if (convertView == null) {
            view = createView(parent)
        } else {
            view = convertView as ViewType
        }

        populateView(getItem(position), view)

        return view
    }

}