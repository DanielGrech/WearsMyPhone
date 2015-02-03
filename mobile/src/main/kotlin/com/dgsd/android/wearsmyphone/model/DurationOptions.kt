package com.dgsd.android.wearsmyphone.model

import com.dgsd.android.wearsmyphone.R
import android.support.annotation.StringRes
import java.util.concurrent.TimeUnit

public enum class DurationOptions(StringRes val displayStringRes: Int,
                                  val timeUnit: TimeUnit, val duration: Int) {

    class object {
        public fun fromDurationInSeconds(duration: Long) : DurationOptions? {
            return DurationOptions.values().firstOrNull {
                opt -> opt.durationInSeconds() == duration
            }
        }
    }

    TEN_SECONDS : DurationOptions(
            R.string.duration_labal_ten_seconds, TimeUnit.SECONDS, 10)
    THIRTY_SECONDS : DurationOptions(
            R.string.duration_labal_thirty_seconds, TimeUnit.SECONDS, 30)
    ONE_MINUTE : DurationOptions(
            R.string.duration_labal_1_minute, TimeUnit.MINUTES, 1)
    THREE_MINUTES : DurationOptions(
            R.string.duration_labal_three_minutes, TimeUnit.MINUTES, 3)
    FIVE_MINUTES : DurationOptions(
            R.string.duration_labal_five_minutes, TimeUnit.MINUTES, 5)
    TEN_MINUTES : DurationOptions(
            R.string.duration_labal_ten_minutes, TimeUnit.MINUTES, 10)
    THIRTY_MINUTES : DurationOptions(
            R.string.duration_labal_thirty_minutes, TimeUnit.MINUTES, 30)
    ONE_HOUR : DurationOptions(
            R.string.duration_labal_one_hour, TimeUnit.HOURS, 1)
    INFINITE : DurationOptions(
            R.string.duration_labal_infinite, TimeUnit.DAYS, Integer.MAX_VALUE)

    public fun durationInSeconds(): Long {
        return this.timeUnit.toSeconds(this.duration.toLong())
    }
}