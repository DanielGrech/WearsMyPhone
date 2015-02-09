package com.dgsd.android.common.util

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataEvent
import com.dgsd.android.common.WearableConstants
import com.google.android.gms.wearable.DataMapItem
import android.text.TextUtils
import java.util.HashSet

private fun DataEvent.isChangeType(): Boolean {
    return DataEvent.TYPE_CHANGED.equals(getType())
}

public fun DataEventBuffer.getDeviceNames(fromWatch: Boolean): Set<String> {
    val names = HashSet<String>()
    filter { event ->
        event.isChangeType()
    }.map { event ->
        event.getDataItem()
    }.filter { dataItem ->
        val path: String
        if (fromWatch) {
            path = WearableConstants.Path.WATCH_NAME
        } else {
            path = WearableConstants.Path.DEVICE_NAME
        }

        path.equals(dataItem.getUri().getPath())
    }.forEach { dataItem ->
        val deviceName = DataMapItem.fromDataItem(dataItem)
                .getDataMap()?.getString(WearableConstants.Data.DEVICE_NAME)
        if (!TextUtils.isEmpty(deviceName)) {
            names.add(deviceName!!)
        }
    }
    return names
}

public fun DataEventBuffer.getCurrentAlertStatus(): String {
    filter { event ->
        event.isChangeType()
    }.map { event ->
        event.getDataItem()
    }.filter { data ->
        WearableConstants.Path.ALERT_STATUS.equals(data.getUri().getPath())
    }.map { data ->
        DataMapItem.fromDataItem(data).getDataMap()
    }.forEach { dataMap ->
        val status = dataMap.getString(WearableConstants.Data.STATUS)
        if (TextUtils.equals(WearableConstants.AlertStatus.RUNNING, status)
                .or(TextUtils.equals(WearableConstants.AlertStatus.NOT_RUNNING, status))) {
            return status
        }
    }

    return WearableConstants.AlertStatus.UNKNOWN
}