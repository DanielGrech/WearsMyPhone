package com.dgsd.android.wearsmyphone.util;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class CrashlyticsLogger extends Timber.HollowTree {

    private static final String TAG = CrashlyticsLogger.class.getSimpleName();

    public CrashlyticsLogger(Context context) {
        Crashlytics.start(context);
    }

    @Override
    public void v(String message, Object... args) {
        Crashlytics.log(Log.VERBOSE, TAG, args == null ? message : String.format(message, args));
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        v(message, args);
        Crashlytics.logException(t);
    }

    @Override
    public void d(String message, Object... args) {
        Crashlytics.log(Log.DEBUG, TAG, args == null ? message : String.format(message, args));
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        d(message, args);
        Crashlytics.logException(t);
    }

    @Override
    public void i(String message, Object... args) {
        Crashlytics.log(Log.INFO, TAG, args == null ? message : String.format(message, args));
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        i(message, args);
        Crashlytics.logException(t);
    }

    @Override
    public void w(String message, Object... args) {
        Crashlytics.log(Log.WARN, TAG, args == null ? message : String.format(message, args));
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        w(message, args);
        Crashlytics.logException(t);
    }

    @Override
    public void e(String message, Object... args) {
        Crashlytics.log(Log.ERROR, TAG, args == null ? message : String.format(message, args));
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        e(message, args);
        Crashlytics.logException(t);
    }
}
