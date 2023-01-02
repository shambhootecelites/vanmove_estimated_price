package com.vanmove.passesger.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    fun changeDateFormat2(time: String?): String? {
        val inputPattern = "yyyy/MM/dd HH:mm"
        val outputPattern = "yyyy/MM/dd HH:mm"
        @SuppressLint("SimpleDateFormat") val inputFormat =
            SimpleDateFormat(inputPattern)
        @SuppressLint("SimpleDateFormat") val outputFormat =
            SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }
}