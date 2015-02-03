package com.dgsd.android.wearsmyphone.fragment

import android.content.Context
import android.os.Bundle
import com.dgsd.android.wearsmyphone.R
import android.app.AlertDialog
import com.dgsd.android.wearsmyphone.adapter.BaseViewConvertingAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import com.dgsd.android.wearsmyphone.util.AppPreferences
import com.dgsd.android.wearsmyphone.model.DurationOptions
import android.graphics.Typeface

public class DurationChoiceDialogFragment : AlertDialogFragment() {

    private var prefs : AppPreferences? = null

    private var currentOption: DurationOptions? = null

    class object {

        public fun newInstance(context: Context): DurationChoiceDialogFragment {
            val fragment = DurationChoiceDialogFragment()

            val args = Bundle()
            args.putString(AlertDialogFragment.EXTRA_TITLE,
                    context.getString(R.string.setting_title_duration))
            fragment.setArguments(args)

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPreferences.getInstance(getActivity())

        currentOption = DurationOptions.fromDurationInSeconds(prefs!!.getDurationForAlert())
                ?: DurationOptions.INFINITE
    }

    override fun getDialogBuilder(): AlertDialog.Builder {
        val adapter = DurationAdapter()
        adapter.populate(DurationOptions.values().toArrayList())

        val builder = super.getDialogBuilder()
        builder.setAdapter(adapter, { (dialog, which) ->
            prefs?.setDurationForAlert(adapter.getItem(which).durationInSeconds())
            dialog.dismiss()
        })
        return builder
    }

    private inner class DurationAdapter : BaseViewConvertingAdapter<DurationOptions, TextView>() {

        override fun createView(parent: ViewGroup): TextView {
            return LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.li_dialog_item, parent, false) as TextView
        }

        override fun populateView(data: DurationOptions, view: TextView) {
            view.setText(data.displayStringRes)

            view.setTypeface(
                    if (data.equals(currentOption)) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            )
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).ordinal().toLong()
        }

    }
}
