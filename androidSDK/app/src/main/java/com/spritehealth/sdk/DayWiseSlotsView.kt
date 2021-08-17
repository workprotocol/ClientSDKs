package com.spritehealth.sdk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.spritehealth.sdk.model.AvailabilityGroupedByDate
import com.spritehealth.sdk.model.TimePeriodConverter
import kotlinx.android.synthetic.main.view_daywise_slots.view.*
import kotlinx.android.synthetic.main.view_time_slot.view.*
import java.time.format.DateTimeFormatter


internal class DayWiseSlotsView: LinearLayout{

    lateinit var slotPickedCallback: TimeSlotCallback

    constructor(context: Context) : super(context) {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_daywise_slots, this, true)
    }


    fun bind(availability: AvailabilityGroupedByDate) {
        var startTimeFormatted: String? =availability.availableDate
        //Format date time as 08/13 - Fri, 05:15 pm
        var inFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        var firstDate=inFormatter.parse(startTimeFormatted)//08-13-2021 17:15:00
        var outFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd")
        startTimeFormatted= outFormatter.format(firstDate)
        tvDate.text=startTimeFormatted


        var i = 0
        for (timeSlot in availability.slots) {
            val timeSlotView: TimeSlotView = TimeSlotView(context)
            timeSlotView.bind(timeSlot)
            i++
            timeSlotView.setOnClickListener(View.OnClickListener { v ->
                val timeSlotView: TimeSlotView =
                    v as TimeSlotView
                timeSlotView.bind(timeSlot)
                onSlotSelected(timeSlot)
            })
            lloTimeSlotsWrapper.addView(timeSlotView)
        }

        renderUI()
    }

    private fun onSlotSelected(timeSlot: TimePeriodConverter) {
        slotPickedCallback.onSuccess(timeSlot)
    }

    fun renderUI() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}