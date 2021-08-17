
package com.spritehealth.sdk

import Util
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.*
import kotlinx.android.synthetic.main.activity_preview_appointment.*
import kotlinx.android.synthetic.main.activity_specialist_detail.tvSpeciality
import org.json.JSONObject
import java.time.format.DateTimeFormatter


internal class PreviewAppointment : AppCompatActivity() {

    private var eventStartTime: String? = null
    private var eventEndTime: String?=null
    private lateinit var timeSlot: TimePeriodConverter


    var patientEmail: String? = null
    var patientName:kotlin.String? = null
    var patientMobile:kotlin.String? = null


    var displayableAppointmentDateTimeFormatted: String? = null

    var specialist:User?=null;
    private var service: Service? = null
    private var costBreakUp = CostBreakUp()

    private var selectedReason: Reason? = null
    private var selectedMember: User? = null
    private var whereAbout = ""

    var builder: GsonBuilder = GsonBuilder();
    var gson: Gson = builder.create()

    val clientSdkInstance = SpriteHealthClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_appointment)
        setSupportActionBar(findViewById(R.id.toolbar))

        val bundle = intent.extras
        if (bundle != null) {

            if (bundle.containsKey("selectedReasonJSON")) {
                val selectedReasonJSON = bundle.getString("selectedReasonJSON")
                val type = object : TypeToken<Reason>() {}.type
                selectedReason =gson.fromJson(selectedReasonJSON, type)
            }

            if (bundle.containsKey("specialistJSON")) {
                val specialistJSON = bundle.getString("specialistJSON")
                val type = object : TypeToken<User>() {}.type
                specialist =gson.fromJson(specialistJSON, type)
            }

            if (bundle.containsKey("timeSlotJSON")) {
                val timeSlotJSON = bundle.getString("timeSlotJSON")
                val type = object : TypeToken<TimePeriodConverter>() {}.type
                timeSlot =gson.fromJson(timeSlotJSON, type)
            }
            
            if (bundle.containsKey("serviceJSON")) {
                val serviceJSON = bundle.getString("serviceJSON")
                val type = object : TypeToken<Service>() {}.type
                service =gson.fromJson(serviceJSON, type)

                costBreakUp.insured = service?.wpFixedCost
                costBreakUp.negotiated = service?.wpFixedCost
                costBreakUp.sponsor = 0.0
            }

            if (bundle.containsKey("selectedMemberJSON")) {
                val selectedMemberJSON = bundle.getString("selectedMemberJSON")
                val type = object : TypeToken<User>() {}.type
                selectedMember =gson.fromJson(selectedMemberJSON, type)
            }

            if (bundle.containsKey("costBreakUpJSON")) {
                val costBreakUpJSON = bundle.getString("costBreakUpJSON")
                val type = object : TypeToken<CostBreakUp>() {}.type
                costBreakUp =gson.fromJson(costBreakUpJSON.toString(), type)

            }

            if(timeSlot!=null){

                //Format date time as 08/13 - Fri, 05:15 pm
                var inFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
                var appointmentStartDateTime=inFormatter.parse(timeSlot?.startTime)//08-13-2021 17:15:00
                var appointmentEndDateTime=inFormatter.parse(timeSlot?.endTime)//08-13-2021 17:45:00

                var outFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd")
                displayableAppointmentDateTimeFormatted= outFormatter.format(appointmentStartDateTime)
                outFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                displayableAppointmentDateTimeFormatted+=" at "+outFormatter.format(appointmentStartDateTime)
                displayableAppointmentDateTimeFormatted+=" - "+outFormatter.format(appointmentEndDateTime)

                outFormatter = DateTimeFormatter.ofPattern("MM/dd/YYYY hh:mm a")
                eventStartTime= outFormatter.format(appointmentStartDateTime)
                eventEndTime =outFormatter.format(appointmentEndDateTime)

            }

        }

        if (selectedMember != null) {
            patientEmail = selectedMember!!.email
            patientName = selectedMember!!.name
            patientMobile = selectedMember!!.mobilePhone
        }


        if(specialist!=null) {
            tvSpecialistName.text = specialist!!.name
            tvSpecialistName.visibility = VISIBLE

            if (specialist!!.specializationNames != null && !specialist!!.specializationNames!!.isEmpty()) {
                tvSpeciality.text = specialist!!.specializationNames
                tvSpeciality.visibility = VISIBLE
            }
        }



        if (service != null) {
            tvServiceName.text = service!!.wpName

            if (service!!.wpDuration > 0) {
                tvDuration.text = service!!.wpDuration.toString() + " mins"
                tvDuration.visibility = VISIBLE
            }
            if (service!!.vendorName!=null){
                tvVendorName.text = service!!.vendorName
                tvVendorName.visibility = VISIBLE
            }
        }

        if (selectedReason != null) {
            loReasonWrapper.visibility = VISIBLE
            tvReason.text = selectedReason!!.name
        }

        setPrice()

        if (patientName != null && patientName!!.isNotEmpty()) {
            patientsNameEditText.setText(patientName)
        }

        if (patientEmail != null && !patientEmail!!.isEmpty()) {
            emailEditText.setText(patientEmail)
        }


        tvDisplayAppointmentTime.text = "$displayableAppointmentDateTimeFormatted"
        //tvMember.text="$patientName"
        mobileEditText.isCursorVisible = true
        mobileEditText.isFocusable = true
        mobileEditText.isFocusableInTouchMode = true
        mobileEditText.setText(patientMobile)



        tvNoticeSecondLine.text="At the time of your visit, you and ${specialist?.name} will join the video call. You can join via the Sprite app or website."


    }

    private fun setPrice() {
        val memberPayment = costBreakUp.insured!!
        tvPrice.visibility = VISIBLE
        tvPrice.text = "$"+ Util().roundTo2DecimalStringValue(memberPayment)
    }


    fun bookVisit(view: View){

        progressBar.visibility = VISIBLE
        bookButton.visibility = View.GONE

        patientName = patientsNameEditText.text.toString()
        patientEmail = emailEditText.text.toString()
        patientMobile = mobileEditText.text.toString()


        if(patientName!!.isEmpty()){
            patientsNameEditText.error = "Please enter the name"
            progressBar.visibility = View.GONE
            bookButton.visibility = VISIBLE
            return;
        }
        if(patientEmail!!.isEmpty()){
            emailEditText.error = "Please enter the email"
            progressBar.visibility = View.GONE
            bookButton.visibility = VISIBLE
            return;
        }

        if (patientMobile != null && !patientMobile!!.isEmpty()) {
            postAppointmentRequest()
        } else {
            mobileEditText.error = "Please enter the mobile"
            progressBar.visibility = View.GONE
            bookButton.visibility = VISIBLE
            return
        }
    }


    @Synchronized
    fun postAppointmentRequest() {
        progressBar.visibility = VISIBLE
        bookButton.visibility = View.GONE

        clientSdkInstance.appointmentBooking(getFormPost(), this, object : SpriteHealthClient.Callback {
            override fun onSuccess(response: String?) {
                // do stuff here
                readAppointmentResponse(response)
                progressBar.visibility = View.GONE
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )

    }

    private fun readAppointmentResponse(responseStr: String?) {
        val calendarEvent: CalendarEvent =
            gson.fromJson(responseStr, object : TypeToken<CalendarEvent?>() {}.type)

        var msg="";
        if (calendarEvent != null && calendarEvent.errors?.size ?:0 >0) {
            msg="Request failed due to " + calendarEvent.errors!!.get(0).message
            Toast.makeText(this,  msg, Toast.LENGTH_LONG).show();

            progressBar.visibility = View.GONE
            bookButton.visibility = VISIBLE
        } else if (calendarEvent != null && calendarEvent.appointment != null) {
            msg ="Appointment booked successfully."
            Toast.makeText(this, msg,Toast.LENGTH_LONG).show();
        }
    }

    fun getFormPost(): MutableMap<String, String> {
            val params: MutableMap<String, String> = HashMap()

        if(specialist!=null) {
            params["vendorUserId"] = specialist?.id.toString()
            params["specialistName"] = specialist?.name.toString()
        }
            params["eventStartTime"] = eventStartTime!!
            params["eventEndTime"] = eventEndTime!!

            //READ FROM EDITABLE FIELDS
            params["customerPhone"] = patientMobile!!
            params["customerEmail"] = patientEmail!!
            params["customerName"] = patientName!!

        if(selectedMember!=null) {
            params["patientId"] = selectedMember?.id.toString()
            params["walletId"] = selectedMember?.walletId.toString()
        }

            params["status"] = "booked"

            if (selectedReason != null) {
                params["reasonId"] = selectedReason!!.id.toString()
                params["reasonName"] = selectedReason!!.name
            }

            if (service != null) {
                params["serviceId"] = service?.id.toString()
                params["serviceName"] = service!!.wpName!!
                params["providerName"] = service!!.vendorName!!
                params["serviceSetting"] =service!!.wpSetting!!

                if(service!!.wpOverview!=null) {
                    params["where"] = service!!.wpOverview!!
                }
            }
            return params
    }

    private fun onFailedAppointment() {
        progressBar.visibility = View.GONE
        bookButton.visibility = VISIBLE
        bookButton.isEnabled = true
        bookButton.isClickable = true
        Toast.makeText(this, "There is some error while booking appointment.", Toast.LENGTH_LONG).show();
    }

     fun readResponse(responseStr: String?) {
         /*
        val calendarEvent: CalendarEvent =
            Gson().fromJson(responseStr, object : TypeToken<CalendarEvent?>() {}.type)
        launched = false
        if (calendarEvent != null && !CollectionUtils.isEmpty(calendarEvent.errors)) {
            WPUtil.showToast(
                applicationContext,
                "Request failed due to " + calendarEvent.errors.get(0).message
            )
            progressBar.visibility = View.GONE
            bookButton.visibility = VISIBLE
        } else if (calendarEvent != null && calendarEvent.appointment != null) {
            val msg =
                if (isBooking) "Appointment booked successfully." else "Your request has been sent successfully."
            WPUtil.showToast(applicationContext, msg)
        }
         */
    }



}
