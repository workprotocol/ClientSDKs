package com.spritehealth.sdk

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.spritehealth.sdk.model.TimePeriodConverter
import kotlinx.android.synthetic.main.view_time_slot.view.*
import java.time.format.DateTimeFormatter


class TimeSlotView: LinearLayout{

    constructor(context: Context) : super(context) {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_time_slot, this, true)
    }

    fun bind(timeSlot: TimePeriodConverter) {
        var startTimeFormatted: String? = timeSlot.startTime
        //Format date time as 08/13 - Fri, 05:15 pm
        var inFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
        var firstDate=inFormatter.parse(startTimeFormatted)//08-13-2021 17:15:00
        var outFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        startTimeFormatted= outFormatter.format(firstDate)
        this.tvSlot.text=startTimeFormatted
    }

}