package com.vanmove.passesger.utils

import androidx.fragment.app.FragmentManager
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker
import java.util.*

object DateTimePicker {


    fun ShowDatePicker(fm: FragmentManager,listener: SlideDateTimeListener) {
        val calendar = Calendar.getInstance().apply {
            time = Calendar.getInstance().time
            add(Calendar.HOUR, 2)
        }
        SlideDateTimePicker.Builder(fm)
            .setListener(listener)
            .setInitialDate(calendar.time)
            .setMinDate(Date())
            .build()
            .show()
    }

}
