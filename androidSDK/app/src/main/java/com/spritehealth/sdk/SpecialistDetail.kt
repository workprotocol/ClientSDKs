
package com.spritehealth.sdk
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.spritehealth.sdk.model.InitializationStatus
import com.spritehealth.sdk.model.Specialist
import com.spritehealth.sdk.model.VendorDescriptionTypeEnum
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_specialist_detail.*
import kotlinx.android.synthetic.main.custom_toolbar.*


internal class SpecialistDetail : AppCompatActivity() {

    private var specialistWithAvailability:Specialist?=null;
    private lateinit var specialistUser:Specialist;

    val clientSdkInstance = SpriteHealthClient.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specialist_detail)
        //setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM;
        getSupportActionBar()?.setCustomView(R.layout.custom_toolbar);

        val tvPageHeading = supportActionBar!!.customView.findViewById<TextView>(R.id.tvPageHeading)
        tvPageHeading.text = "Specialist Details"

        imgvBack.setOnClickListener(){
            this.finish();
        }

        var id:Long = intent.getLongExtra("id",0)

        if(intent.hasExtra("specialistWithAvailabilityJSON")) {
            val specialistWithAvailabilityJSON = intent.getStringExtra("specialistWithAvailabilityJSON")

            var builder: GsonBuilder = GsonBuilder();
            var gson: Gson = builder.create()
            val specialistType = object : TypeToken<Specialist>() {}.type
            specialistWithAvailability = gson.fromJson(specialistWithAvailabilityJSON.toString(), specialistType);

            if (specialistWithAvailability != null) {
                tvName.text = specialistWithAvailability!!.name
                tvPageHeading.text =specialistWithAvailability!!.name

                    if (specialistWithAvailability!!.specializationNames != null) {
                        tvSpeciality.text = specialistWithAvailability!!.specializationNames
                    }

                tvGender.text = specialistWithAvailability!!.gender

                setImage(specialistWithAvailability!!.imageIds)
            }
        }

        var response="";
        var progressBar: ProgressBar = findViewById(R.id.progressBar);
        progressBar.visibility = View.VISIBLE

        clientSdkInstance.getSpecialistDetails(id, this, object : SpriteHealthClient.Callback<Specialist> {
            override fun onSuccess(specialist: Specialist) {
                specialistUser= specialist
                displaySpecailistDetail()
                progressBar.visibility = View.GONE
            }
            override fun onError(error: String?)
            {
                var errorMsg = error
                progressBar.visibility = View.GONE
            }
        } )
    }

    fun displaySpecailistDetail() {

        if(specialistUser!=null) {

            val textView = findViewById<TextView>(R.id.tvName)
            textView.text = specialistUser.name

           setImage(specialistUser.imageIds)

            var specialityNames: String? = ""
            if (specialistUser.specialization!!.isNotEmpty()) {

                var speciality = SpriteHealthClient.specialities?.find {
                    if (it.value != null && specialistUser.specialization != null) {
                        it.value!! == specialistUser.specialization!![0];
                    } else {
                        false
                    }
                }

                if (speciality != null) {
                    specialityNames = speciality.name
                    //save it for later use
                    specialistUser.specializationNames = specialityNames;
                    tvSpeciality.text = specialityNames;
                };
            }


            val genderView = findViewById<TextView>(R.id.tvGender)
            genderView.text = specialistUser.gender

            if (specialistUser.prefferedLanguage != null) {
                lloLanguage.visibility = View.VISIBLE
                tvLanguage.text = specialistUser.prefferedLanguage
            }


            for (vendorDescription in specialistUser.descriptions!!) {
                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.PROFESSIONAL_EXPERIENCE && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloProfExp.visibility = View.VISIBLE
                    tvProfExp.text = Html.fromHtml(vendorDescription.description)
                }
                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.EDUCATION && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloEducation.visibility = View.VISIBLE
                    val allEducation = vendorDescription.description
                    val educationBuffer = StringBuffer()

                    if (allEducation!!.contains("%%")) {
                        for (education: String in allEducation!!.split("%%".toRegex())
                            .toTypedArray()) {
                            educationBuffer.append(education.replace("$$", " ").toString())
                            educationBuffer.append(", ")
                        }
                    } else if (allEducation!!.contains("$$")) {
                        educationBuffer.append(allEducation!!.replace("$$", " "))
                    } else {
                        educationBuffer.append(allEducation)
                    }
                    if (educationBuffer.isNotEmpty()) {
                        var qualification =
                            educationBuffer.toString().trim { it <= ' ' }
                        if (qualification.contains(",")) {
                            qualification =
                                qualification.substring(0, qualification.lastIndexOf(","))
                        }
                        lloEducation.visibility = View.VISIBLE
                        tvEducation.text = qualification
                    }
                }
                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.SHORT_DESCRIPTION && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloProfStatement.visibility = View.VISIBLE
                    tvProfStatement.text = Html.fromHtml(vendorDescription.description)
                }

                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.LANGUAGES && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloLanguage.visibility = View.VISIBLE
                    tvLanguage.text = vendorDescription.description
                }
                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.REGISTRATION && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloRegistration.visibility = View.VISIBLE
                    tvRegistration.text = Html.fromHtml(vendorDescription.description)
                }

                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.AWARDS_AND_RECOGNITIONS && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloAwards.visibility = View.VISIBLE
                    tvAwards.text = Html.fromHtml(vendorDescription.description)
                }
                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.MEMBERSHIPS && vendorDescription.description != null && !vendorDescription.description!!.isEmpty()) {
                    lloMemberships.visibility = View.VISIBLE
                    tvMemberships.text = Html.fromHtml(vendorDescription.description)
                }
                if (vendorDescription.vendorDescriptionType == VendorDescriptionTypeEnum.QUALIFICATION) {
                    val qualification = vendorDescription.description
                    tvQualification.text = qualification
                    tvQualification.visibility = View.VISIBLE
                }
            }

        }

        //if NO AVAILABILITY, then hide BOOK Visit Button
        if(specialistWithAvailability!!.availableSlots?.size ?: 0>0) {
            btnBookVisit.visibility = View.VISIBLE
        }
    }

    private fun setImage(imageIds: Set<Long>?) {
        if (imageIds != null && imageIds!!.isNotEmpty()) {
            var imageId = imageIds!!.iterator().next()

            var imageUrl =
                SpriteHealthClient.apiRoot + "/images/" + imageId.toString()
            Picasso.get().load(imageUrl).into(imgvSpecialist)
        }

    }


    fun bookVisit(view: View){
        var builder:GsonBuilder = GsonBuilder()
        var gson:Gson =builder.create()

        val intent = android.content.Intent(this, BookAppointment::class.java).apply {

            if(specialistUser!=null) {
                putExtra("specialistJSON", gson.toJson(specialistUser))
            }
            if(specialistWithAvailability!=null && specialistWithAvailability!!.serviceId!=null) {
                putExtra("specialistWithAvailabilityJSON", gson.toJson(specialistWithAvailability))
                putExtra("serviceId", specialistWithAvailability!!.serviceId)
            }

        }
        this.startActivity(intent)
    }


}
