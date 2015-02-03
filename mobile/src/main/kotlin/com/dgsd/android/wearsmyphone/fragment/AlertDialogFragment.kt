package com.dgsd.android.wearsmyphone.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes

import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

public open class AlertDialogFragment : BaseDialogFragment(), DialogInterface.OnClickListener {

    private var title: String? = null
    private var message: String? = null
    private var positive: String? = null
    private var negative: String? = null

    class object {

        protected val EXTRA_TITLE: String = "_title"
        protected val EXTRA_MESSAGE: String = "_message"
        protected val EXTRA_POSITIVE: String = "_positive"
        protected val EXTRA_NEGATIVE: String = "_negative"

        public fun newInstance(context: Context,
                               StringRes title: Int,
                               StringRes message: Int,
                               StringRes positive: Int,
                               StringRes negative: Int): AlertDialogFragment {
            return newInstance(context.getString(title),
                    context.getString(message),
                    if (positive > 0) context.getString(positive) else null,
                    if (negative > 0) context.getString(negative) else null)
        }

        public fun newInstance(title: String, message: String,
                               positive: String?,
                               negative: String?): AlertDialogFragment {
            val fragment = AlertDialogFragment()

            val args = Bundle()
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, message)
            args.putString(EXTRA_POSITIVE, positive)
            args.putString(EXTRA_NEGATIVE, negative)

            fragment.setArguments(args)
            return fragment
        }
    }

    private val onButtonClickObservable: PublishSubject<Int>

    {
        onButtonClickObservable = PublishSubject.create<Int>()
        onButtonClickObservable.subscribeOn(Schedulers.immediate())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<BaseDialogFragment>.onCreate(savedInstanceState)

        val args = getArguments()
        if (args != null) {
            title = args.getString(EXTRA_TITLE)
            message = args.getString(EXTRA_MESSAGE)
            positive = args.getString(EXTRA_POSITIVE)
            negative = args.getString(EXTRA_NEGATIVE)
        }
    }

    override final fun createDialog(savedInstanceState: Bundle?): Dialog {
        return getDialogBuilder().create()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        onButtonClickObservable.onNext(which)
    }

    protected open fun getDialogBuilder(): AlertDialog.Builder {
        val builder = AlertDialog.Builder(getActivity())

        builder.setTitle(title).setMessage(message)

        if (positive != null) {
            builder.setPositiveButton(positive, this)
        }

        if (negative != null) {
            builder.setNegativeButton(negative, this)
        }

        return builder
    }

    public fun observeButtonClicked(): Observable<Int> {
        return onButtonClickObservable
    }
}
