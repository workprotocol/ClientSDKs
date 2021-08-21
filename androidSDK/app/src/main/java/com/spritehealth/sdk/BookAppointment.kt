
package com.spritehealth.sdk


import Util
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.*
import kotlinx.android.synthetic.main.activity_book_appointment.*
import kotlinx.android.synthetic.main.activity_book_appointment.tvDuration
import kotlinx.android.synthetic.main.activity_book_appointment.tvPrice
import kotlinx.android.synthetic.main.activity_book_appointment.tvServiceName
import kotlinx.android.synthetic.main.activity_specialist_detail.progressBar
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


internal class BookAppointment : AppCompatActivity() {

    private val whereAbout: String?=null
    private var currentNavigatedCalendarDate: GregorianCalendar? =null
    private var coverage: NetworkCoverage?=null
    private var costBreakUp: CostBreakUp =CostBreakUp();
    private var selectedReason: Reason? = null
    private var specialistAvailability: SpecialistAvailability? = null
    private var reasonList: MutableList<Reason> = ArrayList()
    var specialist:Specialist?=null;
    var service: Service?=null
    var serviceId:Long=0;

    val sdkClientInstance = SpriteHealthClient.getInstance(this)
    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson = builder.create()


    var specialization = ""
    var qualification = ""
    var specialistId: Long = 0
    var reasonId:kotlin.Long = 0
    var year = 0
    var month:Int = 0
    var day:Int = 0
    var calendar: Calendar? = null
    var hour = 0
    var minute:Int = 0
    var startDate: String? = null
    var endDate: String? = null
    var requestedTime: String? = null
    var timeNotation: String? = null
    var serviceName: String? = null
    var overview: String? = null
    var date: String? = null
    var hour24Format = 0
    var notation: String? = null
    var startDateForDisplay: String? = null
    var availabilities: MutableList<SpecialistAvailability> = ArrayList()
    var availabilitiesGroupedByDate:MutableList<AvailabilityGroupedByDate> = ArrayList()
    private val duration = 30
    private val reasonName: String? = null
    private val selectedMember: User? = null
    private val isInitiallyLoaded = false
    private val pickableList: List<Service>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)
        //setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
        getSupportActionBar()?.setCustomView(R.layout.custom_toolbar);

        val tvPageHeading = supportActionBar!!.customView.findViewById<TextView>(R.id.tvPageHeading)
        tvPageHeading.text = "Select Date & Time"

        imgvBack.setOnClickListener(){
            this.finish();
        }

        var progressBar: ProgressBar = findViewById(R.id.progressBar);

        if(intent.hasExtra("serviceId")){
            serviceId=intent.getLongExtra("serviceId", 0)
        }

        if(intent.hasExtra("specialistJSON")) {
            val specialistJSON = intent.getStringExtra("specialistJSON")
            val specialistType = object : TypeToken<Specialist>() {}.type
            specialist = gson.fromJson(specialistJSON.toString(), specialistType);

            if (specialist != null) {
              fetchReasons();
                if(serviceId>0) {
                    fetchServiceDetails()
                }

            }
        }
    }


    private fun fetchReasons(){
        progressBar.visibility = View.VISIBLE
        var specialities: String? = specialist?.specialization?.joinToString(",")

        var params:MutableMap<String,String> = HashMap()

        if (specialities != null) {
            params.put("specialities",specialities)
        }
             sdkClientInstance.getReasons(
                this,
                 params,
                object : SpriteHealthClient.Callback<MutableList<Reason>> {
                    override fun onSuccess(reasons: MutableList<Reason>) {

                        readReasons(reasons)
                        progressBar.visibility = View.GONE
                    }

                    override fun onError(error: String?) {
                        var errorMsg = error
                        progressBar.visibility = View.GONE
                    }
                })

    }

    private fun readReasons(reasons: MutableList<Reason>) {

        reasonList = reasons

        if (reasonList?.isNotEmpty() == true) {


            //sort reasonNames in Ascending order
            reasonList!!.sortWith(Comparator { reason1, reason2 ->
                var returnValue = 0
                try {

                    if(reason1.name!=null && reason2.name!=null)
                    returnValue=reason1.name.toUpperCase().compareTo(reason2.name.toUpperCase())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                returnValue
            })


            reasonList!!.add(0,Reason(0L, "Select reason"))

            val reasonNames: MutableList<String> = ArrayList()
            for (reason in reasonList!!) {
                reasonNames.add(reason.name)
            }
            val reasonDataAdapter = ArrayAdapter(this, R.layout.spinner_item, reasonNames)
            reasonDataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            reasonSpinner.adapter = reasonDataAdapter

            reasonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        selectedReason = reasonList!!.get(position)
                    } else {
                        selectedReason = null
                    }
                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //selectedReason=null;
                }
            }
        } 

    }

    private fun fetchServiceDetails() {
        progressBar.visibility = View.VISIBLE

        sdkClientInstance.getServiceDetails(serviceId, this, object : SpriteHealthClient.Callback<Service> {
            override fun onSuccess(serviceInfo: Service) {
                service= serviceInfo
                readService()
                progressBar.visibility = View.GONE
            }

            override fun onError(error: String?) {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun readService() {
      
        if(service!=null) {
            lloServiceWrapper.visibility=View.VISIBLE
            tvServiceName.text = service?.wpName ?: "";
            if (service!!.wpDuration > 0) {
                tvDuration.text = service!!.wpDuration.toString() + " mins"
                tvDuration.visibility = VISIBLE
            }
            costBreakUp.insured = service!!.wpFixedCost
            costBreakUp.negotiated = costBreakUp.insured;
            costBreakUp.sponsor = 0.0;

            if (service!!.wpIsDefault) {
                renderCoverage()
                fetchAvailableSlots(null);
                return;
            }
            tvPrice.text = "...";
            tvPrice.visibility = VISIBLE;
            fetchServiceNetworkCoverage()
        }

    }

    private fun fetchServiceNetworkCoverage() {
        progressBar.visibility = View.GONE
        progressBarPricing.visibility = View.VISIBLE
        val formPost: MutableMap<String, String> = HashMap()
        formPost.put("memberId", SpriteHealthClient.member.id.toString());
        service?.let { formPost.put("providerId", it.wpVendorId.toString()) };
        if(service!!.wpCode!=null) {
            formPost.put("serviceCode", service?.wpCode.toString());
        }
        if(specialist!=null) {
            formPost.put("specialistId", specialist!!.id.toString());
        }
        formPost.put("organizationId", SpriteHealthClient.member.organizationId.toString());
        formPost.put("termType", "Purchase");
        formPost.put("operation", "COMPUTE");


        sdkClientInstance.getServiceNetworkCoverage(
            formPost,
            this,
            object : SpriteHealthClient.Callback<NetworkCoverage> {
                override fun onSuccess(coverageInfo: NetworkCoverage) {
                    coverage= coverageInfo
                    readCoverage()
                    progressBarPricing.visibility = View.GONE
                }

                override fun onError(error: String?) {
                    fetchAvailableSlots(null);
                    var errorMsg = error
                    progressBarPricing.visibility = View.GONE
                }
            })
    }

    private fun readCoverage() {
        try {
            if (coverage!=null){
                if(coverage!!.errorDescription == null || coverage!!.errorDescription!!.isEmpty() ) {
                    costBreakUp.coverageId = coverage!!.id;
                    costBreakUp.insured =
                        coverage!!.patientAmountBreakupStructure?.Insured;
                    costBreakUp.negotiated = coverage!!.totalProviderAmount;
                    costBreakUp.sponsor =
                        coverage!!.patientAmountBreakupStructure?.Sponsor;
                }
            }
        } catch (e: Exception) {

        }

        fetchAvailableSlots(null);
        renderCoverage()
    }

    private fun renderCoverage() {
        tvPrice.visibility = VISIBLE
        val memberPayment: Double? = costBreakUp.insured
        tvPrice.text="$" + memberPayment?.let { Util().roundTo2DecimalStringValue(it) }
    }

    private fun fetchAvailableSlots(startDate: Date?) {
        progressBar.visibility = View.GONE
        progressBarSlots.visibility=VISIBLE
        val params: MutableMap<String, String> = HashMap()

        params["vendorUserId"] = specialist?.id.toString()
        params["weeks"] = "3"
        params["serviceId"] = serviceId.toString()

        var currentHour= LocalDateTime.now().hour;
        val  currentTime:String?=null
        var refDate : LocalDateTime?=null

        if(currentHour>=22){
            refDate =LocalDateTime.now().plusHours(3)
        }else{
            refDate = LocalDateTime.now().plusHours(2)
        }

        params["startDateTime"] = refDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"))
        params["currentTime"] = refDate.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        sdkClientInstance.getSpecialistAvailability(
            params,
            this,
            object : SpriteHealthClient.Callback<SpecialistAvailability> {
                override fun onSuccess(response: SpecialistAvailability) {
                    readAvailability(response)
                    progressBar.visibility = View.GONE
                    progressBarSlots.visibility=GONE
                }

                override fun onError(error: String?) {
                    var errorMsg = error
                    progressBar.visibility = View.GONE
                    progressBarSlots.visibility=GONE
                }
            })
    }

    private fun readAvailability(specialistAvailabilityInfo: SpecialistAvailability) {
        specialistAvailability = specialistAvailabilityInfo

        if(specialistAvailability?.freeTimePeriods?.isNotEmpty() == true) {
            //organize/group by slots by days
            getDayWiseSlots(specialistAvailability!!.freeTimePeriods);
        }else{
            tvNoSlotMessage.visibility = VISIBLE;
            //btnShowMoreSlots.visibility = VISIBLE;
        }

    }

    private fun getDayWiseSlots(freeTimePeriods: List<TimePeriodConverter>) {
        val dateWiseSlots: HashMap<String, List<TimePeriodConverter?>?> = HashMap()
        for (timeSlot in freeTimePeriods) {
            var timeSlots: MutableList<TimePeriodConverter?>? = null
            val dateParts: Array<String> = timeSlot.startTime?.split(" ")?.toTypedArray() ?: emptyArray()
            val dateKey = dateParts[0].trim { it <= ' ' }
            //if already added
            timeSlots = if (dateWiseSlots.containsKey(dateKey)) {
                dateWiseSlots[dateKey] as MutableList<TimePeriodConverter?>?
            } else {
                ArrayList<TimePeriodConverter?>()
            }
            timeSlots!!.add(timeSlot)
            dateWiseSlots[dateKey] = timeSlots
        }
        for (dateKey in dateWiseSlots.keys) {
            val dayWiseSlots = AvailabilityGroupedByDate()
            dayWiseSlots.availableDate = dateKey
            dayWiseSlots.slots = dateWiseSlots.get(dateKey) as List<TimePeriodConverter>
            availabilitiesGroupedByDate.add(dayWiseSlots)
        }

        //sort availabiity by available date in Ascending order
        availabilitiesGroupedByDate.sortWith(Comparator { appt1, appt2 ->
            var returnValue = 0
            try {
                val sdf = SimpleDateFormat("MM-dd-yyyy")
                val start1 = sdf.parse(appt1.availableDate)
                val start2 = sdf.parse(appt2.availableDate)
                returnValue = start1.compareTo(start2)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            returnValue
        })

      renderAvailableSlots(availabilitiesGroupedByDate);
    }



    private fun renderAvailableSlots(availabilitiesGroupedByDate:List<AvailabilityGroupedByDate>) {
        var i = 0
        for (availability in availabilitiesGroupedByDate) {
            val dayWiseSlotsView: DayWiseSlotsView = DayWiseSlotsView(this)
            dayWiseSlotsView.slotPickedCallback=object : TimeSlotCallback {
                override fun onSuccess(timeSlot: TimePeriodConverter) {
                    goToConfirmationPage(timeSlot)
                }
            }
            dayWiseSlotsView.bind(availability)
            i++
            dayWiseSlotsView.setOnClickListener(View.OnClickListener { v ->
                val dayWiseSlotsView: DayWiseSlotsView =
                        v as DayWiseSlotsView

            })
            lloSlotGroupedByDateWrapper.addView(dayWiseSlotsView)
        }
    }


    fun loadNextAvailability(view: View) {

    }

    fun showMoreSlots() {
        btnShowMoreSlots.visibility = GONE
        if (currentNavigatedCalendarDate == null) {
            currentNavigatedCalendarDate = GregorianCalendar() //Current Date
            currentNavigatedCalendarDate!!.add(
                Calendar.DAY_OF_MONTH,
                7 - currentNavigatedCalendarDate!!.get(Calendar.DAY_OF_WEEK) + 1
            )
        } else {
            currentNavigatedCalendarDate!!.add(Calendar.DAY_OF_MONTH, 7)
        }
        fetchAvailableSlots(currentNavigatedCalendarDate!!.time)
    }


/*

    fun goToConfirmation(view: View){
        var builder:GsonBuilder = GsonBuilder()
        var gson:Gson =builder.create()

        val intent = Intent(this, PreviewAppointment::class.java).apply {
           /*
             putExtra("specialistWithAvailabilityJSON", gson.toJson(specialistWithAvailability))
            if(specialistUser!=null) {
                putExtra("specialistJSON", gson.toJson(specialistUser))
            }
            if(specialistWithAvailability!=null && specialistWithAvailability!!.serviceId!=null) {
                putExtra("serviceId", specialistWithAvailability!!.serviceId)
            }
        */
        }
        this.startActivity(intent)
    }
*/

    fun goToConfirmationPage(timeSlot: TimePeriodConverter?) {
        val intent =
            Intent(this, PreviewAppointment::class.java)
        if (selectedReason != null) {
            intent.putExtra("selectedReasonJSON", gson.toJson(selectedReason))
        }
        val specialistJSON: String = gson.toJson(specialist)
        intent.putExtra("specialistJSON", specialistJSON)
        val serviceJSON: String = gson.toJson(service)
        intent.putExtra("serviceJSON", serviceJSON)

        val costBreakUpJSON: String = gson.toJson(costBreakUp)
        intent.putExtra("costBreakUpJSON", costBreakUpJSON)

        if (specialist != null) {
            intent.putExtra("vendorUserId", specialist!!.id)
            intent.putExtra("name", specialist!!.name)
            intent.putExtra("specialistMobilePhone", specialist!!.mobilePhone)
            intent.putExtra("vendorName", specialist!!.vendorName)
        } else {
            intent.putExtra("vendorUserId", specialist?.id)
        }
        if (this.service != null) {
            intent.putExtra("serviceId", this.service!!.id)
            intent.putExtra("serviceName", this.service!!.wpName)
        }

        val selectedMemberJSON: String = gson.toJson(SpriteHealthClient.member)
        intent.putExtra("selectedMemberJSON", selectedMemberJSON)

        val timeSlotJSON: String = gson.toJson(timeSlot)
        intent.putExtra("timeSlotJSON", timeSlotJSON)

        /*


        var startTimeFormatted: String? = timeSlot?.startTime
        //Format date time as 08/13 - Fri, 05:15 pm
        var inFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
        var appointmentDate=inFormatter.parse(startTimeFormatted)//08-13-2021 17:15:00
        var outFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd at hh:mm a - ")
        startTimeFormatted= outFormatter.format(appointmentDate)

        val eventStartTime: String = startDate.toString() + " " + timeNotation //requestedTime;
        var eventEndTime = "" // startDate + " " + getEndTime();
        var dateToDisplay = eventStartTime
        try {
            val outFormatter: DateFormat = SimpleDateFormat("MMM dd, yyyy")
            val sdf: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
            val eventStartDateTime: Date = sdf.parse(eventStartTime)
            var duration:Int=30;
            if (service != null && service!!.wpDuration > 0) {
                duration = service!!.wpDuration
            }
            val eventEndDateTime: Date? =DateUtils().getNextDate(eventStartDateTime, duration)
            eventEndTime = sdf.format(eventEndDateTime)

            calendar?.time = eventStartDateTime
            dateToDisplay = outFormatter.format(calendar?.time)
            dateToDisplay += " at $timeNotation"
        } catch (e: Exception) {

        }



        intent.putExtra("eventStartTime", eventStartTime)
        intent.putExtra("eventEndTime", eventEndTime)
        intent.putExtra("timeNotation", timeNotation)
        intent.putExtra("startDate", startDate)
        intent.putExtra("startDateForDisplay", dateToDisplay)
        intent.putExtra("whereAbout", whereAbout)

        */

        startActivity(intent)
    }


}

interface TimeSlotCallback{
    fun onSuccess(timeSlot: TimePeriodConverter)
}