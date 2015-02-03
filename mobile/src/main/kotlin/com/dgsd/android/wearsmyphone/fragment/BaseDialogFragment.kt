package com.dgsd.android.wearsmyphone.fragment

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.dgsd.android.wearsmyphone.R

/**
 * Base class for all dialog fragments in the app
 */
public abstract class BaseDialogFragment : DialogFragment() {

    protected abstract fun createDialog(savedInstanceState: Bundle?): Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = createDialog(savedInstanceState)
        dialog.getWindow().setWindowAnimations(R.style.DialogWindowStyleUpDown)
        return dialog
    }
}
