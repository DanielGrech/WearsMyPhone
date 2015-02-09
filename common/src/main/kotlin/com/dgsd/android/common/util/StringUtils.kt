package com.dgsd.android.common.util

fun String.sentenceCase(): String {
    when {
        this.length() <= 1 -> return this
        else -> return "${Character.toUpperCase(this[0])}${this.substring(1)}"
    }
}